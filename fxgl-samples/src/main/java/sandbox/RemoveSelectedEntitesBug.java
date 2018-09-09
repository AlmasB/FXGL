/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.SelectableComponent;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.ColoredTexture;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RemoveSelectedEntitesBug extends GameApplication{

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RemoveSelectedEntitesBug");
    }

    @Override
    protected void initGame() {
        Entity e1 = Entities.builder()
                .at(100, 100)
                .viewFromNode(new ColoredTexture(40, 30, Color.BLUE))
                .with(new SelectableComponent(true))
                .buildAndAttach(getGameWorld());

        Entity e2 = Entities.builder()
                .at(200, 100)
                .viewFromNode(new Rectangle(40, 40, Color.RED))
                .with(new SelectableComponent(true))
                .buildAndAttach(getGameWorld());

        Cursor cursor = getGameScene().getRoot().getCursor();

        // this solves the issue, I think when mouse is clicked / released the game cursor is overridden
        //e1.getView().setCursor(cursor);
        //e2.getView().setCursor(cursor);

        getGameWorld().selectedEntityProperty().addListener((o, oldEntity, newEntity) -> {
            if (newEntity != null)
                getGameWorld().removeEntity(newEntity);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}