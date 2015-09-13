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
package com.almasb.fxgl.ui;

import java.io.Serializable;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.asset.SaveLoadManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.TextInputDialog;

/**
 * This is the default FXGL game menu
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public final class FXGLGameMenu extends FXGLAbstractMenu {

    public FXGLGameMenu(GameApplication app) {
        super(app);
    }

    @Override
    protected MenuBox createMenuBody() {
        MenuItem itemResume = new MenuItem("RESUME");
        itemResume.setAction(app.getSceneManager()::closeGameMenu);

        MenuItem itemSave = new MenuItem("SAVE");
        itemSave.setAction(() -> {
            Serializable data = app.saveState();

            if (data == null)
                return;

            TextInputDialog dialog = new TextInputDialog();
            dialog.setContentText("Enter name for save file");
            dialog.showAndWait().ifPresent(fileName -> {
                try {
                    SaveLoadManager.INSTANCE.save(data, fileName);
                }
                catch (Exception e) {
                    // TODO: use custom stages, as alerts will kick users from the fullscreen
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setContentText("Failed to save file: " + fileName + ". Error: " + e.getMessage());
                    alert.showAndWait();
                }
            });
        });

        MenuItem itemLoad = new MenuItem("LOAD");
        itemLoad.setMenuContent(createContentLoad());

        MenuItem itemOptions = new MenuItem("OPTIONS");
        itemOptions.setChild(createOptionsMenu());

        MenuItem itemExtra = new MenuItem("EXTRA");
        itemExtra.setChild(createExtraMenu());

        MenuItem itemExit = new MenuItem("MAIN MENU");
        itemExit.setAction(() -> {
            Text text = new Text("Are you sure?\nAll unsaved progress will be lost!");
            text.setFill(Color.WHITE);
            text.setFont(app.getSceneManager().getDefaultFont(18));

            Rectangle rect = new Rectangle(200 + 200 + 100, 200);
            rect.setStroke(Color.AZURE);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(root.getScene().getWindow());

            MenuItem btnOK = new MenuItem("OK");
            btnOK.setAction(() -> {
                app.getSceneManager().exitToMainMenu();
                stage.close();
            });
            MenuItem btnCancel = new MenuItem("CANCEL");
            btnCancel.setAction(stage::close);

            HBox hbox = new HBox(btnOK, btnCancel);
            hbox.setAlignment(Pos.CENTER);

            VBox root = new VBox(50, text, hbox);
            root.setAlignment(Pos.CENTER);
            Scene scene = new Scene(new StackPane(rect, root));

            stage.setScene(scene);
            stage.show();
        });

        MenuBox menu = new MenuBox(200, itemResume, itemSave, itemLoad, itemOptions, itemExtra, itemExit);
        menu.setTranslateX(50);
        menu.setTranslateY(app.getHeight() / 2 - menu.getLayoutHeight() / 2);
        return menu;
    }
}
