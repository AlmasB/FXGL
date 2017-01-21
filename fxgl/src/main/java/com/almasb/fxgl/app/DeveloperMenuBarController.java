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

package com.almasb.fxgl.app;

import com.almasb.fxgl.devtools.Console;
import com.almasb.fxgl.devtools.controller.ColorAdjustController;
import com.almasb.fxgl.devtools.controller.DialogAddEntityController;
import com.almasb.fxgl.devtools.controller.DialogEditEntityController;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.ui.InGameWindow;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxgl.ui.UIController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.layout.Pane;
import jfxtras.scene.control.window.Window;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DeveloperMenuBarController implements UIController {

    private GameApplication app;

    @FXML
    private Menu menuCustom;

    /**
     * @return custom menu that can be modified by clients to fit their games
     */
    public Menu getCustomMenu() {
        return menuCustom;
    }

    @Override
    public void init() {
        app = FXGL.getApp();
    }

    public void onPause() {
        app.pause();
    }

    public void onResume() {
        app.resume();
    }

    public void onExit() {
        app.getDisplay().showConfirmationBox("Exit?", yes -> {
            if (yes)
                app.exit();
        });
    }

    public void openAddDialog() {
        UI ui = app.getAssetLoader().loadUI("dialog_add_entity.fxml", new DialogAddEntityController());

        Window window = new InGameWindow("Add Entity", InGameWindow.WindowDecor.ALL);
        window.setPrefSize(350, 300);
        window.setContentPane(new Pane(ui.getRoot()));

        app.getGameScene().addUINode(window);
    }

    public void openEditDialog() {
        UI ui = app.getAssetLoader().loadUI("dialog_edit_entity.fxml", new DialogEditEntityController());

        Window window = new InGameWindow("Edit Entity", InGameWindow.WindowDecor.ALL);
        window.setPrefSize(380, 450);
        window.setContentPane(new Pane(ui.getRoot()));

        app.getGameScene().addUINode(window);
    }

    //private ColorAdjustController colorAdjustController = null;
    private UI uiColorAdjust = null;

    public void openColorAdjustDialog() {
        if (uiColorAdjust == null) {
            uiColorAdjust = app.getAssetLoader().loadUI("dialog_color_adjust.fxml", new ColorAdjustController());
        }

        Window window = new InGameWindow("Color Adjust", InGameWindow.WindowDecor.ALL);
        window.setPrefSize(380, 450);
        window.setContentPane(new Pane(uiColorAdjust.getRoot()));

        app.getGameScene().addUINode(window);
    }

    public void onShowBBox(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        FXGL.setProperty("dev.showbbox", item.isSelected());

        app.getGameWorld()
                .getEntitiesByComponent(ViewComponent.class)
                .forEach(e -> {
                    e.getComponentUnsafe(ViewComponent.class).turnOnDebugBBox(item.isSelected());
                });
    }

    private Console console = null;

    public void openConsole() {
        if (console == null) {
            console = new Console();
        }

        if (console.isOpen()) {
            app.getGameScene().removeUINode(console);
            app.getInput().setRegisterInput(true);
        } else {
            app.getInput().setRegisterInput(false);
            app.getGameScene().addUINode(console);
        }
    }
}
