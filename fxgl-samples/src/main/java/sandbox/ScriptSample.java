/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.devtools.DeveloperWASDControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.control.JSControl;
import com.almasb.fxgl.gameplay.cutscene.CutsceneState;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.JavaScriptParser;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

/**
 * Shows how to use scripted controls.
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
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Trigger Cutscene") {
            @Override
            protected void onActionBegin() {
                getStateMachine().pushState(new CutsceneState("cutscene.js"));
            }
        }, KeyCode.ENTER);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("crystals", 4);
        vars.put("alive", true);
        vars.put("name", "MainCharacter");
        vars.put("armor", 10.5);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(300, 300)
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .with(new DeveloperWASDControl())
                .buildAndAttach(getGameWorld());

//        JavaScriptParser parser = new JavaScriptParser("cutscene.js");
//        System.out.println("Parsing");
//
//        parser.callFunction("doStuff");


    }

    public static void main(String[] args) {
        launch(args);
    }
}
