/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputSequence;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

/**
 * Shows how to use input sequence.
 */
public class InputSequenceSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        Input input = FXGL.getInput();

        var sequence = new InputSequence(KeyCode.Q, KeyCode.W, KeyCode.E, KeyCode.R);

        // the action fires only when the sequence above (Q, W, E, R) is complete
        // useful for input combos
        input.addAction(new UserAction("Print Line") {
            @Override
            protected void onActionBegin() {
                System.out.println("Action Begin");
            }

            @Override
            protected void onAction() {
                System.out.println("On Action");
            }

            @Override
            protected void onActionEnd() {
                System.out.println("Action End");
            }
        }, sequence);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
