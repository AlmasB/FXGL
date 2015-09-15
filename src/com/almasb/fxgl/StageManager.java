/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl;

import java.util.logging.Logger;

import com.almasb.fxgl.util.FXGLLogger;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * Responsible for modifying stage state.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
/*package-private*/ class StageManager extends FXGLManager {

    /**
     * The logger
     */
    private static final Logger log = FXGLLogger.getLogger("FXGL.StageManager");

    /**
     * Game window
     */
    private Stage stage;

    /*package-private*/ void init(Stage stage) {
        this.stage = stage;
        stage.setTitle(app.getSettings().getTitle() + " " + app.getSettings().getVersion());
        stage.setResizable(false);

        // ensure the window frame is just right for the scene size
        stage.setScene(app.getSceneManager().getScene());
        stage.sizeToScene();
        stage.setOnCloseRequest(e -> app.exit());

        setAppIcon();

        if (app.getSettings().isFullScreen()) {
            stage.setFullScreenExitHint("");
            // we don't want the user to be able to exit full screen manually
            // but only through settings menu
            // so we set key combination to something obscure which isn't likely to be pressed
            stage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("Shortcut+>"));
            stage.setFullScreen(true);
        }
    }

    /**
     * Opens and shows the actual window.
     */
    /*package-private*/ void show() {
        stage.show();
    }

    private void setAppIcon() {
        try {
            String iconName = app.getSettings().getIconFileName();
            if (!iconName.isEmpty()) {
                Image icon = app.getAssetManager().loadAppIcon(iconName);
                stage.getIcons().add(icon);
            }
        }
        catch (Exception e) {
            log.warning("Failed to load app icon: " + e.getMessage());
        }
    }

    @Override
    protected void onUpdate(long now) {}
}
