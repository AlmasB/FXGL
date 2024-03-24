/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * @author Alex Moore (alexstephenmoore@gmail.com)
 * @author Leo Waters (li0nleo117@gmail.com)
 * @author Poppy Eyres (eyres.poppy@gmail.com)
 */
public final class SoftwareCursor {

    private static final Double[] DEFAULT_POINTS = { 0.0, 0.0, 0.0, 20.0, 20.0, 5.0 };
    private Polygon cursor;

    // creates software cursor with colour
    public SoftwareCursor(Color color){
        this(DEFAULT_POINTS, color);
    }

    // creates software cursor with colour and points to draw
    public SoftwareCursor(Double[] points, Color color){
        cursor = new Polygon();
        cursor.getPoints().addAll(points);
        cursor.setFill(color);
    }

    //gets the node to be added to the scene
    public Node getCursorNode(){
        return cursor;
    }

    //sets X position
    public void setPositionX(double x){
        cursor.setTranslateX(x);
    }

    //sets Y position
    public void setPositionY(double y){
        cursor.setTranslateY(y);
    }

    //sets X and Y position
    public void setPosition(double x, double y){
        cursor.setTranslateX(x);
        cursor.setTranslateY(y);
    }

    //moves X position by x
    public void translatePositionX(double x){
        cursor.setTranslateX(cursor.translateXProperty().doubleValue()+x);
    }

    //moves Y position by y
    public void translatePositionY(double y){
        cursor.setTranslateY(cursor.translateYProperty().doubleValue()+y);
    }

    //moves X & Y positions by x & y
    public void translatePosition(double x,double y){
        cursor.setTranslateX(cursor.translateXProperty().doubleValue()+x);
        cursor.setTranslateY(cursor.translateYProperty().doubleValue()+y);
    }
}
