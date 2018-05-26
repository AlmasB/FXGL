/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.devtools.Console;
import com.almasb.fxgl.devtools.controller.ColorAdjustController;
import com.almasb.fxgl.devtools.controller.DialogAddEntityController;
import com.almasb.fxgl.devtools.controller.DialogEditEntityController;
import com.almasb.fxgl.ui.InGameWindow;
import com.almasb.fxgl.ui.MDIWindow;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxgl.ui.UIController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import static com.almasb.fxgl.app.SystemPropertyKey.*;
import static com.almasb.fxgl.util.BackportKt.forEach;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DeveloperMenuBarController implements UIController {

    private static final Logger log = Logger.get(DeveloperMenuBarController.class);

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
        //app.pause();
    }

    public void onResume() {
        //app.resume();
    }

    public void onExit() {
        app.getDisplay().showConfirmationBox("Exit?", yes -> {
            if (yes)
                app.exit();
        });
    }

    public void openAddDialog() {
        UI ui = app.getAssetLoader().loadUI("dialog_add_entity.fxml", new DialogAddEntityController());

        MDIWindow window = new InGameWindow("Add Entity", InGameWindow.WindowDecor.ALL);
        window.setPrefSize(350, 300);
        window.setContentPane(new Pane(ui.getRoot()));

        app.getGameScene().addUINode(window);
    }

    public void openEditDialog() {
        UI ui = app.getAssetLoader().loadUI("dialog_edit_entity.fxml", new DialogEditEntityController());

        MDIWindow window = new InGameWindow("Edit Entity", InGameWindow.WindowDecor.ALL);
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

        MDIWindow window = new InGameWindow("Color Adjust", InGameWindow.WindowDecor.ALL);
        window.setPrefSize(380, 450);
        window.setContentPane(new Pane(uiColorAdjust.getRoot()));

        app.getGameScene().addUINode(window);
    }

    public void onShowBBox(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        FXGL.getProperties().setValue(DEV_SHOWBBOX, item.isSelected());
    }

    public void onShowPosition(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        FXGL.getProperties().setValue(DEV_SHOWPOSITION, item.isSelected());
    }

    private EventHandler<MouseEvent> clickTracker = e -> {
        log.info("World XY: " + app.getInput().getMousePositionWorld() + ", UI XY: " + app.getInput().getMousePositionUI());
        log.info("Entities clicked: ");

        forEach(
                app.getGameWorld().getEntitiesInRange(new Rectangle2D(app.getInput().getMouseXWorld(), app.getInput().getMouseYWorld(), 1, 1)),
                entity -> log.info(entity.toString())
        );
    };

    public void onTrackClicks(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getSource();

        if (item.isSelected()) {
            //app.getGameScene().addEventHandler(MouseEvent.MOUSE_PRESSED, clickTracker);
        } else {
            //app.getGameScene().removeEventHandler(MouseEvent.MOUSE_PRESSED, clickTracker);
        }
    }

    private Console console = null;

    public void openConsole() {
        if (console == null) {
            console = new Console();
        }

        if (console.isOpen()) {
            console.close();
        } else {
            console.open();
        }
    }
}
