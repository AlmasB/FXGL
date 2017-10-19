/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.UnorderedArray;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.DrawableComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.physics.PhysicsParticleControl;
import com.almasb.fxgl.ui.UI;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the scene that shows game objects on the screen during "play" mode.
 * Contains 3 layers. From bottom to top:
 * <ol>
 *     <li>Entities and their render layers (game view)</li>
 *     <li>Particles</li>
 *     <li>UI Overlay</li>
 * </ol>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class GameScene extends FXGLScene
        implements EntityWorldListener, ModuleListener {

    private static final Logger log = Logger.get("FXGL.GameScene");

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

    private Array<ParticleControl> particles = new UnorderedArray<>(16);

    private Array<Entity> drawables = new UnorderedArray<>(128);

    /**
     * The overlay root above {@link #gameRoot}. Contains UI elements, native JavaFX nodes.
     * uiRoot isn't affected by viewport movement.
     */
    private Group uiRoot = new Group();

    protected GameScene(int width, int height) {
        getContentRoot().getChildren().addAll(gameRoot, particlesCanvas, uiRoot);

        initParticlesCanvas(width, height);
        initViewport(width, height);

        log.debug("Game scene initialized: " + width + "x" + height);
    }

    private void initParticlesCanvas(double w, double h) {
        particlesCanvas.setWidth(w);
        particlesCanvas.setHeight(h);
        particlesCanvas.setMouseTransparent(true);
    }

    private void initViewport(double w, double h) {
        Viewport viewport = getViewport();
        gameRoot.layoutXProperty().bind(viewport.xProperty().negate());
        gameRoot.layoutYProperty().bind(viewport.yProperty().negate());

        Scale scale = new Scale();
        scale.pivotXProperty().bind(viewport.xProperty());
        scale.pivotYProperty().bind(viewport.yProperty());
        scale.xProperty().bind(viewport.zoomProperty());
        scale.yProperty().bind(viewport.zoomProperty());
        gameRoot.getTransforms().add(scale);
    }

//    /**
//     * Converts a point on screen to a point within game scene.
//     *
//     * @param screenPoint point in UI coordinates
//     * @return point in game coordinates
//     */
//    public Point2D screenToGame(Point2D screenPoint) {
//        return screenPoint
//                .multiply(1.0 / FXGL.getDisplay().getScaleRatio())
//                .add(getViewport().getOrigin());
//    }
//
//    /**
//     * Converts a point in game world to a point within screen (viewport).
//     *
//     * @param gamePoint point in game world coordinates
//     * @return point in UI coordinates
//     */
//    public Point2D gameToScreen(Point2D gamePoint) {
//        return gamePoint
//                .subtract(getViewport().getOrigin())
//                .multiply(FXGL.getDisplay().getScaleRatio());
//    }

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

    public void addUI(UI ui) {
        addUINode(ui.getRoot());
    }

    public void removeUI(UI ui) {
        removeUINode(ui.getRoot());
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
    }

    /**
     * Removes all nodes from the game view layer.
     */
    public void clearGameViews() {
        gameRoot.getChildren().clear();
    }

    /**
     * Removes all nodes from the UI overlay.
     */
    public void clearUINodes() {
        uiRoot.getChildren().clear();
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
            log.debug("Creating render group for layer: " + layer.asString());
            group.setUserData(renderLayer);
            gameRoot.getChildren().add(group);
        }

        List<Node> tmpGroups = new ArrayList<>(gameRoot.getChildren());
        tmpGroups.sort(Comparator.comparingInt(g -> (int) g.getUserData()));

        gameRoot.getChildren().setAll(tmpGroups);

        return group;
    }

    public void onUpdate(double tpf) {
        getViewport().onUpdate(tpf);

        particlesGC.setGlobalAlpha(1);
        particlesGC.setGlobalBlendMode(BlendMode.SRC_OVER);
        particlesGC.clearRect(0, 0, getWidth(), getHeight());

        for (Entity e : drawables) {
            DrawableComponent drawable = e.getComponent(DrawableComponent.class);

            if (drawable != null) {
                drawable.draw(particlesGC);
            }
        }

        for (ParticleControl particle : particles) {
            particle.renderParticles(particlesGC, getViewport().getOrigin());
        }
    }

    public void clear() {
        log.debug("Clearing game scene");

        getViewport().unbind();
        drawables.clear();
        particles.clear();
        gameRoot.getChildren().clear();
        uiRoot.getChildren().clear();
    }

    @Override
    public void onEntityAdded(Entity entity) {
        initView(entity.getViewComponent());

        // TODO: how does this integrate with the new ECS model?
        entity.getComponentOptional(DrawableComponent.class)
                .ifPresent(c -> drawables.add(entity));

        entity.addModuleListener(this);

        entity.getControlOptional(ParticleControl.class)
                .ifPresent(particles::add);
        entity.getControlOptional(PhysicsParticleControl.class)
                .ifPresent(particles::add);
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        destroyView(entity.getViewComponent());

        entity.getComponentOptional(DrawableComponent.class)
                .ifPresent(c -> drawables.removeValueByIdentity(entity));

        entity.removeModuleListener(this);

        entity.getControlOptional(ParticleControl.class)
                .ifPresent(p -> particles.removeValueByIdentity(p));
        entity.getControlOptional(PhysicsParticleControl.class)
                .ifPresent(p -> particles.removeValueByIdentity(p));
    }

    private void initView(ViewComponent viewComponent) {
        EntityView view = viewComponent.getView();
        addGameView(view);

        viewComponent.renderLayerProperty().addListener((o, oldLayer, newLayer) -> {
            getRenderGroup(oldLayer).getChildren().remove(view);
            getRenderGroup(newLayer).getChildren().add(view);
        });
    }

    private void destroyView(ViewComponent viewComponent) {
        EntityView view = viewComponent.getView();
        removeGameView(view);
    }

    @Override
    public void onAdded(Control control) {
        if (control instanceof PhysicsParticleControl) {
            PhysicsParticleControl particleControl = (PhysicsParticleControl) control;
            particles.add(particleControl);
        }
    }

    @Override
    public void onRemoved(Control control) {
        if (control instanceof PhysicsParticleControl) {
            particles.removeValueByIdentity((PhysicsParticleControl) control);
        }
    }
}
