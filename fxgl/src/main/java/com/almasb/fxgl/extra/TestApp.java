package com.almasb.fxgl.extra;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.State;
import com.almasb.fxgl.app.StateChangeListener;
import com.almasb.fxgl.app.SubState;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TestApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("TestApp 11");
    }

    @Override
    protected void initGame() {
        getStateMachine().addListener(new StateChangeListener() {
            @Override
            public void beforeEnter(State state) {
                if (state instanceof SubState) {

                    Node pausedView = new Button("Hello World");

                    Pane p = (Pane) ((SubState) state).getView();
                    p.getChildren().setAll(pausedView);
                }
            }

            @Override
            public void entered(State state) { }

            @Override
            public void beforeExit(State state) { }

            @Override
            public void exited(State state) { }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
