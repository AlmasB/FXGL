/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ActivatorComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ActivatorSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(500);
        settings.setHeightFromRatio(16/9.0);
    }

    private Entity door;
    private Entity button;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "", () -> {
            getGameWorld()
                    .getEntities()
                    .get(0)
                    .call("press");
        });
    }

    @Override
    protected void initGame() {
        var rect = new Rectangle(40, 40);

        button = entityBuilder()
                .view(rect)
                .with(new ActivatorComponent())
                .buildAndAttach();

        door = entityBuilder()
                .at(getAppWidth() - 40, 0)
                .view(new Rectangle(40, getAppHeight()))
                .buildAndAttach();

        rect.fillProperty().bind(
                Bindings.when(button.getComponent(ActivatorComponent.class).valueProperty()).then(Color.GREEN).otherwise(Color.RED)
        );

        button.getComponent(ActivatorComponent.class).valueProperty().addListener((o, old, newValue) -> {
            if (newValue) {
                door.setY(-300);
            } else {
                door.setY(0);
            }
        });
    }

    @Override
    protected void initUI() {
        var text = getUIFactory().newText("", Color.BLACK, 24);
        text.textProperty().bind(new SimpleStringProperty("isActivated: ").concat(button.getComponent(ActivatorComponent.class).valueProperty().asString()));

        addUINode(text, 50, 150);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
