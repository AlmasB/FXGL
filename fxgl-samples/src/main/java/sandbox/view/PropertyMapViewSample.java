/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.ui.PropertyMapView;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PropertyMapViewSample extends GameApplication {
    
    PropertyMap map = new PropertyMap();
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("PropertyMapViewSample");
        settings.setVersion("0.1");
    }
    
    @Override
    protected void initGame() {
        entityBuilder()
                .view("background.png")
                .buildAndAttach();
        map.setValue("Music Volume", 10);
        map.setValue("Camera Distance",120);
        map.setValue("Initial Health", 100.0);
        map.setValue("Initial Position", new Point2D(0, 0));
        map.setValue("Enum Test", TestEnum.ONE);
        map.setValue("Color Blind", false);
        map.setValue("Player Name", "Jamie");
    
        Node display = new PropertyMapView(map);
        display.setLayoutX(getAppWidth() / 2);
        getGameScene().addUINode(display);
    
        onKeyDown(KeyCode.G, "map", () -> {
            for (String key : map.keys()) {
                debug(key + " - " + map.getValue(key).toString());
            }
        });
        onKeyDown(KeyCode.DIGIT1, "1", () -> {
            map.setValue("Enum Test", TestEnum.ONE);
            map.setValue("Color Blind", true);
            map.setValue("Initial Health", 1.0);
            map.setValue("Player Name", "Jamie");
        });
        onKeyDown(KeyCode.DIGIT2, "2", () -> {
            map.setValue("Enum Test", TestEnum.TWO);
            map.setValue("Color Blind", false);
            map.setValue("Initial Health", 100.0);
            map.setValue("Player Name", "Dave");
        });
        onKeyDown(KeyCode.DIGIT3, "3", () -> {
            map.setValue("Enum Test", TestEnum.THREE);
            map.setValue("Color Blind", true);
            map.setValue("Initial Health", 123.45);
            map.setValue("Player Name", "Christ");
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private enum TestEnum {
        ONE,TWO,THREE
    }
}
