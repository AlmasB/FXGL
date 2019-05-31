/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.trade;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Map;


import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ArcadeShopSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {

    }

    private ArcadeShopState shopState;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Open Shop") {
            @Override
            protected void onActionBegin() {
                getGameController().pushSubScene(shopState);
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
