/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.cutscene;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapted from Roland C. (https://www.youtube.com/watch?v=1Nx5Be9BDYg).
 */
public class MouseGestures {

    private boolean isDragging = false;

    private Map<Node, DragContext> draggableNodes = new HashMap<>();
    private Node context;

    public MouseGestures(Node context) {
        this.context = context;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void makeDraggable(Node node) {
        draggableNodes.put(node, new DragContext(node, context));
    }

    private class DragContext {
        private double x;
        private double y;

        private Node node;
        private Node context;

        private EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
            isDragging = true;

            x = event.getSceneX();
            y = event.getSceneY();
        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
            Node node = (Node) event.getSource();

            double offsetX = event.getSceneX() - x;
            double offsetY = event.getSceneY() - y;

//        node.setTranslateX(node.getTranslateX() + offsetX * 1 / renderer.getScaleX());
//        node.setTranslateY(node.getTranslateY() + offsetY * 1 / renderer.getScaleY());

            node.setLayoutX(node.getLayoutX() + offsetX * 1 / context.getScaleX());
            node.setLayoutY(node.getLayoutY() + offsetY * 1 / context.getScaleY());

            x = event.getSceneX();
            y = event.getSceneY();
        };

        private EventHandler<MouseEvent> onMouseReleased = event -> {
            isDragging = false;
        };

        DragContext(Node node, Node context) {
            this.node = node;
            this.context = context;

            node.setOnMousePressed(onMousePressedEventHandler);
            node.setOnMouseDragged(onMouseDraggedEventHandler);
            node.setOnMouseReleased(onMouseReleased);
        }
    }
}
