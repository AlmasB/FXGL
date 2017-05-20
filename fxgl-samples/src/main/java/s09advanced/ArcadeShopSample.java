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

package s09advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.ArcadeShopItem;
import com.almasb.fxgl.gameplay.ArcadeShopState;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Map;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ArcadeShopSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ArcadeShopSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    private ArcadeShopState shopState;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Open Shop") {
            @Override
            protected void onActionBegin() {
                getStateMachine().pushState(shopState);
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("bonus.atk.level", 0);
        vars.put("bonus.def.level", 0);
        vars.put("bonus.hp.level", 0);
        vars.put("bonus.atk.value", 0);
        vars.put("bonus.def.value", 0);
        vars.put("bonus.hp.value", 0);
    }

    @Override
    protected void initGame() {
        getGameState().intProperty("bonus.atk.value").bind(getGameState().intProperty("bonus.atk.level").multiply(10));
        getGameState().intProperty("bonus.def.value").bind(getGameState().intProperty("bonus.def.level").multiply(25));
        getGameState().intProperty("bonus.hp.value").bind(getGameState().intProperty("bonus.hp.level").multiply(5));

        shopState = new ArcadeShopState();
        shopState.addItem(new ArcadeShopItem("ATK", "Increases ATK", 100, 10, 0, new Circle(10, 10, 10, Color.GOLD)));
    }

    @Override
    protected void initUI() {
        Text text = getUIFactory().newText("", Color.BLACK, 16);
        text.textProperty().bind(getGameState().intProperty("bonus.atk.value").asString("ATK: %d ")
                .concat(getGameState().intProperty("bonus.def.value").asString("DEF: %d "))
                .concat(getGameState().intProperty("bonus.hp.value").asString("HP: %d")));

        text.setTranslateY(50);

        getGameScene().addUINode(text);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
