/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.ents.*;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.MainViewComponent;
import com.almasb.fxgl.gameplay.GameWorldListener;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;

import java.util.ArrayList;
import java.util.List;

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
public final class GameScene extends FXGLScene implements GameWorldListener, ComponentListener, ControlListener {

    private static final Logger log = FXGL.getLogger("FXGL.GameScene");

    /**
     * Root for entity views, it is affected by viewport movement.
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

    private List<ParticleControl> particles = new ArrayList<>();

    /**
     * The overlay root above {@link #gameRoot}. Contains UI elements, native JavaFX nodes.
     * uiRoot isn't affected by viewport movement.
     */
    private Group uiRoot = new Group();

    @Inject
    protected GameScene(@Named("appWidth") double width,
                        @Named("appHeight") double height) {
        getRoot().getChildren().addAll(gameRoot, particlesCanvas, uiRoot);

        initParticlesCanvas(width, height);
        initViewport(width, height);

        log.debug("Game scene initialized: " + width + "x" + height);
    }

    private void initParticlesCanvas(double w, double h) {
        particlesCanvas.setWidth(w);
        particlesCanvas.setHeight(h);
        particlesCanvas.setMouseTransparent(true);
    }

    private Viewport viewport;

    /**
     * @return viewport
     */
    public Viewport getViewport() {
        return viewport;
    }

    private void initViewport(double w, double h) {
        viewport = new Viewport(w, h);
        gameRoot.layoutXProperty().bind(viewport.xProperty().negate());
        gameRoot.layoutYProperty().bind(viewport.yProperty().negate());
    }

    /**
     * Converts a point on screen to a point within game scene.
     *
     * @param screenPoint point in UI coordinates
     * @return point in game coordinates
     */
    public Point2D screenToGame(Point2D screenPoint) {
        return screenPoint
                .multiply(1.0 / FXGL.getDisplay().getScaleRatio())
                .add(viewport.getOrigin());
    }

    /**
     * @return unmodifiable list of UI nodes
     */
    public ObservableList<Node> getUINodes() {
        return uiRoot.getChildrenUnmodifiable();
    }

    /**
     * Add a node to the UI overlay.
     *
     * @param node UI node to add
     */
    public void addUINode(Node node) {
        log.debug("Adding UI node: "+ node);
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
     * @return true iff the node has been removed
     */
    public boolean removeUINode(Node n) {
        log.debug("Removing UI node: "+ n);
        return uiRoot.getChildren().remove(n);
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
        getRenderGroup(view.getRenderLayer()).getChildren().add(view);
    }

    /**
     * Remove a view from the game root.
     *
     * @param view view to remove
     */
    public void removeGameView(EntityView view) {
        getRenderGroup(view.getRenderLayer()).getChildren().remove(view);
        view.removeFromScene();
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

    /**
     * Returns graphics context of the game scene.
     * The render layer is over all entities.
     * Use this only if performance is required.
     * The drawing on this context can be done in {@link GameApplication#onUpdate(double)}.
     *
     * @return graphics context
     */
    public GraphicsContext getGraphicsContext() {
        return particlesGC;
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
    private Group getRenderGroup(RenderLayer layer) {
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

    @Override
    public void onWorldUpdate(double tpf) {
        particlesGC.setGlobalAlpha(1);
        particlesGC.setGlobalBlendMode(BlendMode.SRC_OVER);
        particlesGC.clearRect(0, 0, getWidth(), getHeight());

        particles.forEach(p -> p.renderParticles(particlesGC, getViewport().getOrigin()));
    }

    @Override
    public void onWorldReset() {
        log.finer("Resetting game scene");

        getViewport().unbind();
        particles.clear();
        gameRoot.getChildren().clear();
        uiRoot.getChildren().clear();
    }

    @Override
    public void onEntityAdded(Entity entity) {
        log.finer("Entity added to scene");

        entity.getComponent(MainViewComponent.class)
                .ifPresent(viewComponent -> {
                    onComponentAdded(viewComponent);
                });

        entity.addComponentListener(this);
        entity.addControlListener(this);

        entity.getControl(ParticleControl.class)
                .ifPresent(particles::add);
        entity.getControl(PhysicsWorld.PhysicsParticleControl.class)
                .ifPresent(particles::add);
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        log.finer("Entity removed from scene");

        entity.getComponent(MainViewComponent.class)
                .ifPresent(viewComponent -> {
                    onComponentRemoved(viewComponent);
                });

        entity.removeComponentListener(this);
        entity.removeControlListener(this);

        entity.getControl(ParticleControl.class)
                .ifPresent(particles::remove);
        entity.getControl(PhysicsWorld.PhysicsParticleControl.class)
                .ifPresent(particles::remove);
    }

    private ChangeListener<EntityView> viewChangeListener = (o, oldView, newView) -> {
        Group renderGroup = getRenderGroup(oldView.getRenderLayer());
        int index = renderGroup.getChildren().indexOf(oldView);

        if (index != -1) {
            renderGroup.getChildren().set(index, newView);
        } else {
            log.warning("Old view was not in the scene graph. Adding new view");
            addGameView(newView);
        }
    };

//    private ChangeListener<RenderLayer> renderLayerChangeListener = (o, oldLayer, newLayer) -> {
//
//    };

    @Override
    public void onComponentAdded(Component component) {
        if (component instanceof MainViewComponent) {
            log.finer("Added MainViewComponent");

            MainViewComponent viewComponent = (MainViewComponent) component;

            EntityView view = viewComponent.getView();
            addGameView(view);

            viewComponent.viewProperty().addListener(viewChangeListener);
            viewComponent.renderLayerProperty().addListener((o, oldLayer, newLayer) -> {
                getRenderGroup(oldLayer).getChildren().remove(view);
                getRenderGroup(newLayer).getChildren().add(view);
            });
        }
    }

    @Override
    public void onComponentRemoved(Component component) {
        if (component instanceof MainViewComponent) {
            log.finer("Removed MainViewComponent");

            MainViewComponent viewComponent = (MainViewComponent) component;

            EntityView view = viewComponent.getView();
            removeGameView(view);

            viewComponent.viewProperty().removeListener(viewChangeListener);
        }
    }

    @Override
    public void onControlAdded(Control control) {
        if (control instanceof PhysicsWorld.PhysicsParticleControl) {
            PhysicsWorld.PhysicsParticleControl particleControl = (PhysicsWorld.PhysicsParticleControl) control;
            particles.add(particleControl);
        }
    }

    @Override
    public void onControlRemoved(Control control) {
        if (control instanceof PhysicsWorld.PhysicsParticleControl) {
            particles.remove(control);
        }
    }
}
