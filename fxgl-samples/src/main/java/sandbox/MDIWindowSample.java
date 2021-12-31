/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.ui.MDIWindow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * TODO: 1 window per combination (resizable, closable, etc.)
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MDIWindowSample extends GameApplication {

    private MDIWindow window;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            addUINode(window);
        });
    }

    @Override
    protected void initGame() {
        window = new MDIWindow();
        window.setPrefSize(200, 720);
        window.relocate(50, 50);

        window.setTitle("Hello");

        //window.setMovable(false);
        //window.setManuallyResizable(false);

        window.setContentPane(new StackPane(new Rectangle(300, 50, Color.BLUE)));

        addUINode(window);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
