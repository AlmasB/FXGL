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
package com.almasb.fxgl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.almasb.fxgl.effect.ParticleEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.settings.SceneSettings;
import com.almasb.fxgl.ui.FXGLScene;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.WorldStateListener;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;

public final class GameScene extends FXGLScene implements WorldStateListener {

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

    public GameScene(SceneSettings settings) {
        super(settings);

        getRoot().getChildren().addAll(gameRoot, particlesCanvas, uiRoot);

        initParticlesCanvas();
    }

    private void initParticlesCanvas() {
        particlesCanvas.setWidth(getWidth());
        particlesCanvas.setHeight(getHeight());
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

    /**
     * Converts a point on screen to a point within game scene.
     *
     * @param screenPoint point in UI coordinates
     * @return point in game coordinates
     */
    public Point2D screenToGame(Point2D screenPoint) {
        return screenPoint.multiply(1.0 / getScaleRatio()).add(getViewportOrigin());
    }

    /**
     * Sets viewport origin. Use it for camera movement.
     * <p>
     * Do NOT use if the viewport was bound.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setViewportOrigin(int x, int y) {
        gameRoot.setLayoutX(-x);
        gameRoot.setLayoutY(-y);
    }

    /**
     * Note: viewport origin, like anything in a scene, has top-left origin point.
     *
     * @return viewport origin
     */
    public Point2D getViewportOrigin() {
        return new Point2D(-gameRoot.getLayoutX(), -gameRoot.getLayoutY());
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distX and distY represent bound distance between entity and viewport origin
     * <p>
     * <pre>
     * Example:
     *
     * bindViewportOrigin(player, (int) (getWidth() / 2), (int) (getHeight() / 2));
     *
     * the code above centers the camera on player
     * For most platformers / side scrollers use:
     *
     * bindViewportOriginX(player, (int) (getWidth() / 2));
     *
     * </pre>
     *
     * @param entity
     * @param distX
     * @param distY
     */
    public void bindViewportOrigin(Entity entity, int distX, int distY) {
        gameRoot.layoutXProperty().bind(entity.xProperty().negate().add(distX));
        gameRoot.layoutYProperty().bind(entity.yProperty().negate().add(distY));
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distX represent bound distance in X axis between entity and viewport origin.
     *
     * @param entity entity to follow
     * @param distX distance in X between origin and entity
     */
    public void bindViewportOriginX(Entity entity, int distX) {
        gameRoot.layoutXProperty().bind(entity.xProperty().negate().add(distX));
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distY represent bound distance in Y axis between entity and viewport origin.
     *
     * @param entity entity to follow
     * @param distY distance in Y between origin and entity
     */
    public void bindViewportOriginY(Entity entity, int distY) {
        gameRoot.layoutYProperty().bind(entity.yProperty().negate().add(distY));
    }

    public void unbindViewportOrigin() {
        gameRoot.layoutXProperty().unbind();
        gameRoot.layoutYProperty().unbind();
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

    public void addGameNode(Node node) {
        getRenderLayer(RenderLayer.TOP).getChildren().add(node);
    }

    @Override
    public void onEntityAdded(Entity entity) {
        //log.finer("Attaching " + entity + " to the scene");
        entity.getSceneView().ifPresent(view -> {
            getRenderLayer(view.getRenderLayer()).getChildren().add(view);
        });

        if (entity instanceof ParticleEntity) {
            particles.add((ParticleEntity) entity);
        }
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        entity.getSceneView().ifPresent(view ->
                getRenderLayer(view.getRenderLayer()).getChildren().remove(view));

        particles.remove(entity);
    }

    @Override
    public void onWorldUpdate() {
        particlesGC.setGlobalAlpha(1);
        particlesGC.setGlobalBlendMode(BlendMode.SRC_OVER);
        particlesGC.clearRect(0, 0, getWidth(), getHeight());

        particles.forEach(p -> p.renderParticles(particlesGC, getViewportOrigin()));
    }

    @Override
    public void onWorldReset() {
        log.finer("Resetting game scene");

        unbindViewportOrigin();
        particles.clear();
        gameRoot.getChildren().clear();
        uiRoot.getChildren().clear();
    }
}
