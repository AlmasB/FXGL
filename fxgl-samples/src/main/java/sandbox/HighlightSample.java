/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.HighlightableComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class HighlightSample extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER
    }

    // make the field instance level
    // but do NOT init here for properly functioning save-load system
    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("HighlightSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initGame() {
        // 2. create entity and attach to world using fluent API
        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new HighlightableComponent())
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(300, 100)
                .viewFromNode(new Rectangle(200, 40, Color.BLUE))
                .with(new HighlightableComponent())
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void initUI() {
        Text text = getUIFactory().newText("Level 1", Color.WHITESMOKE, 46.0);

        DropShadow ds = new DropShadow(25, 0, 0, Color.BLACK);

        text.setEffect(ds);

        DSLKt.centerText(text);

        getGameScene().addUINode(text);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
