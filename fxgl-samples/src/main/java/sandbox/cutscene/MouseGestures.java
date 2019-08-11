/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.cutscene;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Adapted from Roland C. (https://www.youtube.com/watch?v=1Nx5Be9BDYg).
 */
public class MouseGestures {

    private DragContext dragContext = new DragContext();
    //private FXRenderer renderer;

    private EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
        dragContext.x = event.getSceneX();
        dragContext.y = event.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
        Node node = (Node) event.getSource();

        double offsetX = event.getSceneX() - dragContext.x;
        double offsetY = event.getSceneY() - dragContext.y;

//        node.setTranslateX(node.getTranslateX() + offsetX * 1 / renderer.getScaleX());
//        node.setTranslateY(node.getTranslateY() + offsetY * 1 / renderer.getScaleY());

        node.setLayoutX(node.getLayoutX() + offsetX);
        node.setLayoutY(node.getLayoutY() + offsetY);

        dragContext.x = event.getSceneX();
        dragContext.y = event.getSceneY();
    };

//    public MouseGestures(FXRenderer renderer) {
//        this.renderer = renderer;
//    }

    public void makeDraggable(Node node) {
        node.setOnMousePressed(onMousePressedEventHandler);
        node.setOnMouseDragged(onMouseDraggedEventHandler);
    }

    private static class DragContext {
        double x;
        double y;
    }
}
