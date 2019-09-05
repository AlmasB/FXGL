/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.input.view.MouseButtonView;
import com.almasb.fxgl.input.view.TriggerView;
import com.almasb.fxgl.input.virtual.*;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class VirtualControllerSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(850);
        settings.setHeight(700);
        settings.setMenuEnabled(false);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("test") {
            @Override
            protected void onActionBegin() {
                System.out.println("start f");
            }

            @Override
            protected void onAction() {
                System.out.println("f");
            }

            @Override
            protected void onActionEnd() {
                System.out.println("end f");
            }
        }, KeyCode.F, VirtualButton.Y);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(
                new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0.1, Color.WHITE),
                        new Stop(0.39, Color.GRAY),
                        new Stop(0.95, Color.BLACK)
                )
        );
    }

    @Override
    protected void initUI() {
        Node dpad = getInput().createVirtualDpadView(new CustomDpad());
        Node buttons = getInput().createPSVirtualControllerView();

        addUINode(dpad, 50, 20);
        addUINode(buttons, 200, 150);

        addUINode(getInput().createXboxVirtualControllerView(), 350, 20);

        addUINode(getInput().createVirtualDpadView(), 500, 150);

        addUINode(getInput().createVirtualMenuKeyView(getSettings().getMenuKey(), getSettings().isMenuEnabled()), 650, 50);

        var keyView = new KeyView(KeyCode.E);
        keyView.keyColorProperty().bind(
                Bindings.when(keyView.hoverProperty()).then(Color.GREEN).otherwise(Color.GRAY)
        );
        keyView.backgroundColorProperty().bind(
                Bindings.when(keyView.hoverProperty()).then(Color.WHITE).otherwise(Color.BLACK)
        );

        addUINode(keyView, 50, 400);

        var mouseView = new MouseButtonView(MouseButton.PRIMARY);
        mouseView.setBackgroundColor(Color.BLUEVIOLET);
        mouseView.colorProperty().bind(
                Bindings.when(mouseView.hoverProperty()).then(Color.GREEN).otherwise(Color.GRAY)
        );
//        mouseView.backgroundColorProperty().bind(
//                Bindings.when(mouseView.hoverProperty()).then(Color.WHITE).otherwise(Color.BLACK)
//        );

        addUINode(mouseView, 150, 400);

        var triggerView = new TriggerView(new KeyTrigger(KeyCode.K));
        triggerView.colorProperty().bind(
                Bindings.when(triggerView.hoverProperty()).then(Color.YELLOW).otherwise(Color.AZURE)
        );

        addUINode(triggerView, 350, 400);

        // special keys
        addUINode(new HBox(10,
                new KeyView(KeyCode.UP),
                new KeyView(KeyCode.RIGHT),
                new KeyView(KeyCode.DOWN),
                new KeyView(KeyCode.LEFT),
                new KeyView(KeyCode.SHIFT),
                new KeyView(KeyCode.CONTROL),
                new KeyView(KeyCode.ALT),
                new KeyView(KeyCode.DELETE),
                new KeyView(KeyCode.SPACE),
                new KeyView(KeyCode.ENTER, Color.BLUE)
                ), 50, 550);
    }

    public static class CustomDpad extends VirtualDpad {

        public CustomDpad() {
            super(getInput());
        }

        @Override
        public Node createView(VirtualButton dpadButton) {
            return new Rectangle(40, 40, Color.RED);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
