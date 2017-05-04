/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package sandbox.scifi;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.tiled.TiledMap;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScifiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(640);
        settings.setHeight(640);
        settings.setTitle("ScifiSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    private GameEntity player;
    private PlayerControl playerControl;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Use") {
            @Override
            protected void onActionBegin() {
                getGameWorld().getCollidingEntities(player)
                        .stream()
                        .filter(e -> e.hasControl(UsableControl.class))
                        .map(e -> e.getControlUnsafe(UsableControl.class))
                        .forEach(UsableControl::use);
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);
    }

    @Override
    protected void initGame() {
        TiledMap map = getAssetLoader().loadJSON("test_level.json", TiledMap.class);

        getGameWorld().setLevelFromMap(map);

        getGameWorld().spawn("button", 30, 340);

        player = (GameEntity) getGameWorld().spawn("player", 100, 100);
        playerControl = player.getControlUnsafe(PlayerControl.class);

        getGameWorld().addEntities(Entities.makeScreenBounds(40));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
