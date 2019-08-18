/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Scale;

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
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            isDragging = true;

            x = event.getSceneX();
            y = event.getSceneY();
        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            Node node = (Node) event.getSource();

            double offsetX = event.getSceneX() - x;
            double offsetY = event.getSceneY() - y;

            var scale = (Scale) context.getTransforms().get(0);

            node.setLayoutX(node.getLayoutX() + offsetX * 1 / scale.getX());
            node.setLayoutY(node.getLayoutY() + offsetY * 1 / scale.getY());

            x = event.getSceneX();
            y = event.getSceneY();
        };

        private EventHandler<MouseEvent> onMouseReleased = event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

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
