/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.devtools.DeveloperWASDControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityEvent;
import com.almasb.fxgl.entity.components.ScriptComponent;
import com.almasb.fxgl.extra.entity.components.ActivatorComponent;
import com.almasb.fxgl.extra.entity.components.AliveComponent;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.event.Handles;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScriptSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ScriptSample");
        settings.setVersion("0.1");
    }

    private Entity player, enemy;

    @Override
    protected void initInput() {
        DSLKt.onKeyDown(KeyCode.F, "Activate", () -> {

            getGameWorld().getCollidingEntities(player).forEach(e -> {
                e.getComponent(ActivatorComponent.class).activate(player);
            });
        });

        DSLKt.onKeyDown(KeyCode.G, "Kill", () -> {

            getGameWorld().getCollidingEntities(player).forEach(e -> {
                e.getComponent(AliveComponent.class).kill(player);

                e.getComponent(AliveComponent.class).revive();
            });
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("myColor", Color.GREEN);
        vars.put("enemyY", 50.0);
    }

    @Override
    protected void initGame() {
        player = Entities.builder()
                .at(100, 100)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.BLUE))
                .with(new DeveloperWASDControl())
                .buildAndAttach(getGameWorld());

        enemy = Entities.builder()
                .at(180, 100)
                .viewFromNodeWithBBox(new Rectangle(30, 30, Color.RED))
                .with(new AliveComponent(true))
                .with(new ActivatorComponent(), new IDComponent("wanderer", 0))
                .with(new ScriptComponent())
                .with("onActivate", "first_script.js")
                .with("onActivate.name", "wanderer")
                .with("onActivate.id", 0)
                .with("onDeath", "second_script.js")
                .with("onRevive", "first_script.js")
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(380, 100)
                .viewFromNodeWithBBox(new Rectangle(30, 30, Color.RED))
                .with(new AliveComponent(true))
                .with(new ActivatorComponent(), new IDComponent("wanderer", 1))
                .with(new ScriptComponent())
                .with("onActivate", "second_script.js")
                .with("onActivate.name", "wanderer")
                .with("onActivate.id", 1)
                .buildAndAttach(getGameWorld());
    }

//    @Handles(eventType = "ACTIVATE")
//    public void handle(EntityEvent event) {
//
//        System.out.println("Called in Java" + event.getData("name"));
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
