/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.cutscene.dialogue.DialogueContext;
import com.almasb.fxgl.cutscene.dialogue.FunctionCallDelegate;
import com.almasb.fxgl.cutscene.dialogue.FunctionCallHandler;
import com.almasb.fxgl.dsl.components.view.TextViewComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use in-game dialogues.
 */
public class ComplexDialogueSample extends GameApplication {

    private Entity entity;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        // simple dialogue
        onKeyDown(KeyCode.Q, () -> {
            // the json file is loaded from /assets/dialogues/
            var dialogueGraph = getAssetLoader().loadDialogueGraph("simple.json");

            getCutsceneService().startDialogueScene(dialogueGraph);
        });

        onKeyDown(KeyCode.F, () -> {
            System.out.println("Global: " + getWorldProperties());
            System.out.println("Local: " + entity.getProperties());
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerName", "The One");
    }

    @Override
    protected void initGame() {
        entity = entityBuilder()
                .at(200, 200)
                .view(new Rectangle(50, 50))
                .with(new TextViewComponent(0, 63, "Placeholder NPC"))
                .with("hasMet", false)
                .onClick(e -> {
                    // the json file is loaded from /assets/dialogues/
                    var dialogueGraph = getAssetLoader().loadDialogueGraph("example_dialogue.json");

                    var exampleFunctionHandler = new ExampleFunctionHandler();
                    exampleFunctionHandler.addFunctionCallDelegate(new ExampleDelegate());

                    getCutsceneService().startDialogueScene(dialogueGraph, new ExampleContext(e), exampleFunctionHandler);
                })
                .buildAndAttach();
    }

    private static class ExampleContext implements DialogueContext {

        private Entity e;

        ExampleContext(Entity e) {
            this.e = e;
        }

        @Override
        public PropertyMap properties() {
            return e.getProperties();
        }
    }

    public static class ExampleFunctionHandler extends FunctionCallHandler {

        public void customFunction(int number) {
            System.out.println("Calling custom function with: " + number);
        }

        @Override
        protected Object handle(String functionName, String[] args) {
            System.out.println(functionName + " " + Arrays.toString(args));

            return false;
        }
    }

    public static class ExampleDelegate implements FunctionCallDelegate {

        public double anotherFunction(int i, double d, String s) {
            System.out.println("Calling anotherFunction from delegate: " + i + " " + d + " " + s);

            return d;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
