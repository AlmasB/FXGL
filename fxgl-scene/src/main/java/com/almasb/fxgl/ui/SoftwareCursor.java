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
public class SoftwareCursor {
    Polygon Cursor;
    static Double[] defaultPoints = {0.0, 0.0,0.0, 20.0, 20.0, 5.0};

    // creates software cursor with colour
    public SoftwareCursor(Color color){
        this(defaultPoints, color);
    }
    // creates software cursor with colour and points to draw
    public SoftwareCursor(Double[] Points, Color color){
        Cursor = new Polygon();
        Cursor.getPoints().addAll(Points);
        Cursor.setFill(color);

    }
    //gets the node to be added to the scene
    public Node getCursorNode(){
        return  Cursor;
    }
    //sets X position
    public void setPositionX(double x){
        Cursor.setTranslateX(x);
    }
    //sets Y position
    public void setPositionY(double y){
        Cursor.setTranslateY(y);
    }
    //sets X and Y position
    public void setPosition(double x, double y){
        Cursor.setTranslateX(x);
        Cursor.setTranslateY(y);
    }
    //moves X position by x
    public void translatePositionX(double x){
        Cursor.setTranslateX(Cursor.translateXProperty().doubleValue()+x);
    }
    //moves Y position by y
    public void translatePositionY(double y){
        Cursor.setTranslateY(Cursor.translateYProperty().doubleValue()+y);
    }
    //moves X & Y positions by x & y
    public void translatePosition(double x,double y){
        Cursor.setTranslateX(Cursor.translateXProperty().doubleValue()+x);
        Cursor.setTranslateY(Cursor.translateYProperty().doubleValue()+y);
    }
}
