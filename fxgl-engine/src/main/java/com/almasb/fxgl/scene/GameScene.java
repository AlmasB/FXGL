/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.sslogger.Logger;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.EntityWorldListener;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.ui.FontType;
import com.almasb.fxgl.ui.UI;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.core.util.BackportKt.forEach;

/**
 * Represents the scene that shows entities on the screen during "play" mode.
 * Contains 2 layers. From bottom to top:
 * <ol>
 *     <li>Entities and their render layers (game view)</li>
 *     <li>UI Overlay</li>
 * </ol>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class GameScene extends FXGLScene implements EntityWorldListener {

    private static final Logger log = Logger.get(GameScene.class);

    /**
     * Root for entity views, it is affected by viewport movement.
     */
    private Group gameRoot = new Group();

    /**
     * The overlay root above {@link #gameRoot}. Contains UI elements, native JavaFX nodes.
     * uiRoot isn't affected by viewport movement.
     */
    private Group uiRoot = new Group();

    private Text profilerText = new Text();

    public Text getProfilerText() {
        return profilerText;
    }


    private List<Entity> entities = new ArrayList<>();


    private ObjectMap<Entity, EntityView> debugPositions = new ObjectMap<>();

    protected GameScene(int width, int height) {
        super(width, height);

        getContentRoot().getChildren().addAll(gameRoot, uiRoot);

        if (FXGL.getSettings().isProfilingEnabled()) {
            initProfilerText(0, height - 120);
        }

        initViewport(width, height);

        addDebugListener();

        log.debug("Game scene initialized: " + width + "x" + height);
    }

    private void initProfilerText(double x, double y) {
        profilerText.setFont(FXGL.getUIFactory().newFont(FontType.MONO, 20.0));
        profilerText.setFill(Color.RED);
        profilerText.setTranslateX(x);
        profilerText.setTranslateY(y);

        uiRoot.getChildren().add(profilerText);
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

        Rotate rotate = new Rotate(0, Rotate.Z_AXIS);
        rotate.pivotXProperty().bind(viewport.xProperty().add(w / 2));
        rotate.pivotYProperty().bind(viewport.yProperty().add(h / 2));
        rotate.angleProperty().bind(viewport.angleProperty().negate());
        gameRoot.getTransforms().add(rotate);
    }

    private void addDebugListener() {
        FXGL.getSettings().getDevShowPosition().addListener((o, prev, show) -> {
            if (show) {
                forEach(FXGL.getGameWorld().getEntities(), e -> {

                    addDebugView(e);

                });

            } else {
//                forEach(debugPositions, entry -> {
//                    EntityView view = entry.value;
//                    view.translateXProperty().unbind();
//                    view.translateYProperty().unbind();
//                    removeGameView(view, RenderLayer.TOP);
//                });
//
//                debugPositions.clear();
            }
        });
    }

    private void addDebugView(Entity e) {
//        Text textPos = new Text("");
//        textPos.textProperty().bind(e.xProperty().asString("(%.0f, ").concat(e.yProperty().asString("%.0f)")));
//
//        EntityView view = new EntityView(new Circle(2.5));
//        view.addNode(textPos);
//        view.translateXProperty().bind(e.xProperty());
//        view.translateYProperty().bind(e.yProperty());
//        addGameView(view, RenderLayer.TOP);
//
//        debugPositions.put(e, view);
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
//    public void addGameView(EntityView view, RenderLayer layer) {
//        getRenderGroup(layer).getChildren().add(view);
//    }
//
//    public void addGameView(EntityView view) {
//        getRenderGroup(RenderLayer.DEFAULT).getChildren().add(view);
//    }

    /**
     * Remove a view from the game root.
     *
     * @param view view to remove
     */
//    public void removeGameView(EntityView view, RenderLayer layer) {
//        getRenderGroup(layer).getChildren().remove(view);
//    }

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

    private void sortZ() {
        List<Entity> tmpE = new ArrayList<>(entities);
        tmpE.sort(Comparator.comparingInt(Entity::getZ));

        gameRoot.getChildren().setAll(
                tmpE.stream().map(e -> e.getViewComponent().getParent()).collect(Collectors.toList())
        );
    }

    public void onUpdate(double tpf) {
        getViewport().onUpdate(tpf);
    }

    /**
     * Unbinds viewport, clears game views and UI nodes.
     */
    public void clear() {
        log.debug("Clearing game scene");

        getViewport().unbind();
        gameRoot.getChildren().clear();
        uiRoot.getChildren().setAll(profilerText);
    }

    @Override
    public void onEntityAdded(Entity entity) {
        entities.add(entity);
        initView(entity.getViewComponent());

        if (FXGL.getSettings().getDevShowPosition().getValue()) {
            addDebugView(entity);
        }
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        entities.remove(entity);
        destroyView(entity.getViewComponent());

//        EntityView debugView = debugPositions.get(entity);
//        if (debugView != null) {
//            debugView.translateXProperty().unbind();
//            debugView.translateYProperty().unbind();
//            removeGameView(debugView, RenderLayer.TOP);
//
//            debugPositions.remove(entity);
//        }
    }

    private void initView(ViewComponent viewComponent) {
//        EntityView view = viewComponent.getView();
//        addGameView(view, viewComponent.getRenderLayer());
//
//        viewComponent.renderLayerProperty().addListener((o, oldLayer, newLayer) -> {
//            getRenderGroup(oldLayer).getChildren().remove(view);
//            getRenderGroup(newLayer).getChildren().add(view);
//        });
    }

    private void destroyView(ViewComponent viewComponent) {
//        EntityView view = viewComponent.getView();
//        removeGameView(view, viewComponent.getRenderLayer());
    }
}
