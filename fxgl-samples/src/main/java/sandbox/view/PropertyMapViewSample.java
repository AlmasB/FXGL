/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.core.math.Vec2;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PropertyMapViewSample extends GameApplication {

    private enum TestEnum {
        ONE,TWO,THREE
    }
    
    private PropertyMap map = new PropertyMap();
    
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.G, "map", () -> {
            for (String key : map.keys()) {
                System.out.println(key + " - " + map.getValue(key).toString());
            }
        });
        onKeyDown(KeyCode.DIGIT1, () -> {
            map.setValue("Enum Test", TestEnum.ONE);
            map.setValue("Color Blind", true);
            map.setValue("Initial Health", 1.0);
            map.setValue("Player Name", "Jamie");
            map.setValue("Main color", Color.ORANGE);
            map.setValue("Initial Position Point2D", new Point2D(5, 1));
            map.setValue("Initial Position Vec2", new Vec2(5, 1));
        });
        onKeyDown(KeyCode.DIGIT2, () -> {
            map.setValue("Enum Test", TestEnum.TWO);
            map.setValue("Color Blind", false);
            map.setValue("Initial Health", 100.0);
            map.setValue("Player Name", "Dave");
            map.setValue("Main color", Color.RED);
            map.setValue("Initial Position Point2D", new Point2D(-6, 2));
            map.setValue("Initial Position Vec2", new Vec2(-6, 2));
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKGRAY);

        map.setValue("Music Volume", 10);
        map.setValue("Camera Distance", 120);
        map.setValue("Initial Health", 100.0);
        map.setValue("Initial Position Point2D", new Point2D(0, 0));
        map.setValue("Initial Position Vec2", new Vec2(0, 0));
        map.setValue("Enum Test", TestEnum.ONE);
        map.setValue("Color Blind", false);
        map.setValue("Player Name", "Jamie");
        map.setValue("Main color", Color.BLUE);
    
        Node view = getUIFactoryService().newPropertyMapView(map);

        addUINode(view, 100, 100);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
