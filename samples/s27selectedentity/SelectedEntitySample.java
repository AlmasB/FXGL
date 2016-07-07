/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package s27selectedentity;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.SelectableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.BoundingBox;
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

    private GameEntity player, enemy;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SelectedEntitySample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Make unselectable") {
            @Override
            protected void onActionBegin() {
                player.getComponentUnsafe(SelectableComponent.class)
                        .setValue(false);
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Make selectable") {
            @Override
            protected void onActionBegin() {
                player.getComponentUnsafe(SelectableComponent.class)
                        .setValue(true);
            }
        }, KeyCode.G);
    }

    @Override
    protected void initAssets() {}

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

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
