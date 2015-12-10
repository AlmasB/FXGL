/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.effect.ParticleEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.event.*;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the scene that shows game objects on the screen during "play" mode.
 * Contains 3 layers. From bottom to top:
 * <ol>
 *     <li>Entities and their render layers</li>
 *     <li>Particles</li>
 *     <li>UI Overlay</li>
 * </ol>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class GameScene extends FXGLScene {

    private static final Logger log = FXGLLogger.getLogger("FXGL.GameScene");

    /**
     * Root for entities, it is affected by viewport movement.
     */
    private Group gameRoot = new Group();

    /**
     * Canvas for particles to accelerate drawing.
     */
    private Canvas particlesCanvas = new Canvas();

    /**
     * Graphics context for drawing particles.
     */
    private GraphicsContext particlesGC = particlesCanvas.getGraphicsContext2D();

    private List<ParticleEntity> particles = new ArrayList<>();

    /**
     * The overlay root above {@link #gameRoot}. Contains UI elements, native JavaFX nodes.
     * May also contain entities as Entity is a subclass of Parent.
     * uiRoot isn't affected by viewport movement.
     */
    private Group uiRoot = new Group();

    private EventBus eventBus;

    @Inject
    private GameScene(ReadOnlyGameSettings settings) {
        getRoot().getChildren().addAll(gameRoot, particlesCanvas, uiRoot);

        initParticlesCanvas(settings.getWidth(), settings.getHeight());

        eventBus = GameApplication.getService(ServiceType.EVENT_BUS);
        eventBus.addEventHandler(WorldEvent.ENTITY_ADDED, event -> {
            Entity entity = event.getEntity();
            onEntityAdded(entity);
        });
        eventBus.addEventHandler(WorldEvent.ENTITY_REMOVED, event -> {
            Entity entity = event.getEntity();
            onEntityRemoved(entity);
        });
        eventBus.addEventHandler(UpdateEvent.ANY, event -> {
            onWorldUpdate();
        });
        eventBus.addEventHandler(FXGLEvent.RESET, event -> {
            onWorldReset();
        });

        addEventHandler(MouseEvent.ANY, event -> {
            FXGLInputEvent e = new FXGLInputEvent(event);
            e.setGameXY(screenToGame(new Point2D(event.getSceneX(), event.getSceneY())));
            eventBus.fireEvent(e);
        });
        addEventHandler(KeyEvent.ANY, event -> {
            eventBus.fireEvent(new FXGLInputEvent(event));
        });

        viewport = new Viewport(settings.getWidth(), settings.getHeight());
        gameRoot.layoutXProperty().bind(viewport.xProperty().negate());
        gameRoot.layoutYProperty().bind(viewport.yProperty().negate());
    }

    private void initParticlesCanvas(double w, double h) {
        particlesCanvas.setWidth(w);
        particlesCanvas.setHeight(h);
        particlesCanvas.setMouseTransparent(true);
    }

    /**
     * Add a node to the UI overlay.
     *
     * @param node UI node to add
     */
    public void addUINode(Node node) {
        uiRoot.getChildren().add(node);
    }

    /**
     * Add nodes to the UI overlay.
     *
     * @param nodes UI nodes to add
     */
    public void addUINodes(Node... nodes) {
        for (Node node : nodes)
            addUINode(node);
    }

    /**
     * Remove given node from the UI overlay.
     *
     * @param n node to remove
     */
    public void removeUINode(Node n) {
        uiRoot.getChildren().remove(n);
    }

    /**
     * Remove nodes from the UI overlay.
     *
     * @param nodes nodes to remove
     */
    public void removeUINodes(Node... nodes) {
        for (Node node : nodes)
            removeUINode(node);
    }

    /**
     * Add a view to the game root.
     *
     * @param view view to add
     */
    public void addGameView(EntityView view) {
        getRenderLayer(view.getRenderLayer()).getChildren().add(view);
    }

    /**
     * Remove a view from the game root.
     *
     * @param view view to remove
     */
    public void removeGameView(EntityView view) {
        view.removeFromScene();
    }

    /**
     * Returns render group for entity based on entity's
     * render layer. If no such group exists, a new group
     * will be created for that layer and placed
     * in the scene graph according to its layer index.
     *
     * @param layer render layer
     * @return render group
     */
    private Group getRenderLayer(RenderLayer layer) {
        Integer renderLayer = layer.index();
        Group group = gameRoot.getChildren()
                .stream()
                .filter(n -> (int) n.getUserData() == renderLayer)
                .findAny()
                .map(n -> (Group) n)
                .orElse(new Group());


        if (group.getUserData() == null) {
            log.finer("Creating render group for layer: " + layer.asString());
            group.setUserData(renderLayer);
            gameRoot.getChildren().add(group);
        }

        List<Node> tmpGroups = new ArrayList<>(gameRoot.getChildren());
        tmpGroups.sort((g1, g2) -> (int) g1.getUserData() - (int) g2.getUserData());

        gameRoot.getChildren().setAll(tmpGroups);

        return group;
    }

    private Viewport viewport;

    /**
     * @return viewport
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Converts a point on screen to a point within game scene.
     *
     * @param screenPoint point in UI coordinates
     * @return point in game coordinates
     */
    public Point2D screenToGame(Point2D screenPoint) {
        return screenPoint
                .multiply(1.0 / GameApplication.getService(ServiceType.DISPLAY).getScaleRatio())
                .add(viewport.getOrigin());
    }

    /**
     * Set true if UI elements should forward mouse events
     * to the game layer.
     *
     * @param b flag
     * @defaultValue false
     */
    public void setUIMouseTransparent(boolean b) {
        uiRoot.setMouseTransparent(b);
    }

    public void onEntityAdded(Entity entity) {
        entity.getSceneView().ifPresent(view -> {
            getRenderLayer(view.getRenderLayer()).getChildren().add(view);
        });

        if (entity instanceof ParticleEntity) {
            log.finer("Adding particle entity");
            particles.add((ParticleEntity) entity);
        }
    }

    public void onEntityRemoved(Entity entity) {
        particles.remove(entity);
    }

    public void onWorldUpdate() {
        particlesGC.setGlobalAlpha(1);
        particlesGC.setGlobalBlendMode(BlendMode.SRC_OVER);
        particlesGC.clearRect(0, 0, getWidth(), getHeight());

        particles.forEach(p -> p.renderParticles(particlesGC, getViewport().getOrigin()));
    }

    public void onWorldReset() {
        log.finer("Resetting game scene");

        getViewport().unbind();
        particles.clear();
        gameRoot.getChildren().clear();
        uiRoot.getChildren().clear();
    }
}
