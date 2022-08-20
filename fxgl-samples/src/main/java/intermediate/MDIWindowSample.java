/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.ui.MDIWindow;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use MDIWindow.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MDIWindowSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        var defaultWindow = new MDIWindow("Default");
        defaultWindow.setPrefSize(200, 150);
        defaultWindow.relocate(50, 50);

        addUINode(defaultWindow);

        var noDecorWindow = new MDIWindow("No Decor");
        noDecorWindow.setCloseable(false);
        noDecorWindow.setMinimizable(false);
        noDecorWindow.setPrefSize(200, 150);
        noDecorWindow.relocate(350, 50);

        addUINode(noDecorWindow);

        var notResizableWindow = new MDIWindow("No Resize");
        notResizableWindow.setManuallyResizable(false);
        notResizableWindow.setPrefSize(200, 150);
        notResizableWindow.relocate(650, 50);

        addUINode(notResizableWindow);

        var noMoveWindow = new MDIWindow("No Move");
        noMoveWindow.setMovable(false);
        noMoveWindow.setPrefSize(200, 150);
        noMoveWindow.relocate(950, 50);

        addUINode(noMoveWindow);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
