/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.SelectableComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SelectedEntitySample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SelectedEntitySample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initInput() {
        DSLKt.onKey(KeyCode.F, "Move", () ->
                getGameWorld().getSelectedEntity()
                        .map(e -> (Entity)e)
                        .ifPresent(e -> e.translateTowards(getInput().getMousePositionWorld(), 5))
        );
    }

    @Override
    protected void initGame() {
        // 2. create entity and attach to world using fluent API
        Entity e1 = Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .with(new SelectableComponent(true))
                .buildAndAttach();

        Entity e2 = Entities.builder()
                .at(300, 100)
                .viewFromNode(new Rectangle(40, 40, Color.RED))
                .with(new SelectableComponent(true))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
