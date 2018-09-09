/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.SelectableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to make entities selectable with mouse clicks.
 * Press F to make player selectable.
 * Press G to make player unselectable.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SelectedEntitySample extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    private Entity player, enemy;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SelectedEntitySample");
        settings.setVersion("0.1");





    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Make unselectable") {
            @Override
            protected void onActionBegin() {
                player.getComponent(SelectableComponent.class)
                        .setValue(false);
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Make selectable") {
            @Override
            protected void onActionBegin() {
                player.getComponent(SelectableComponent.class)
                        .setValue(true);
            }
        }, KeyCode.G);
    }

    @Override
    protected void initGame() {
        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                // 1. attach selectable component
                .with(new SelectableComponent(true))
                .buildAndAttach(getGameWorld());

        enemy = Entities.builder()
                .type(Type.ENEMY)
                .at(200, 100)
                .viewFromNode(new Rectangle(40, 40, Color.RED))
                .with(new SelectableComponent(true))
                .buildAndAttach(getGameWorld());

        // 2. click on entity and see it being selected
        getGameWorld().selectedEntityProperty().addListener((o, oldEntity, newEntity) -> {
            System.out.println(oldEntity);
            System.out.println(newEntity);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
