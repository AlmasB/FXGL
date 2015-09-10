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

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public enum FXGLExceptionHandler implements ExceptionHandler {

    INSTANCE;

    private final Logger log = FXGLLogger.getLogger("FXGLExceptionHandler");

    private boolean exitPending = false;

    /**
     * Handles "Unhandled" exception by following these steps:
     * Logs the exception.
     * Displays exception dialog with info.
     * Exits the application on dialog close.
     */
    @Override
    public void handle(Throwable e) {
        GameApplication.getInstance().pause();

        log.severe("Unhandled Exception:");
        log.severe(FXGLLogger.errorTraceAsString(e));
        log.severe("Application will now exit");

        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(GameApplication.getInstance().getSceneManager().getScene().getWindow());

        Rectangle rect = new Rectangle(450, 200);
        rect.setStroke(Color.RED);

        Text text = new Text(e.getMessage() == null ? "Ooops :'(" : e.getMessage());
        text.setFill(Color.WHITE);
        text.setFont(Font.font(36));

        Scene scene = new Scene(new StackPane(rect, text));
        scene.setOnKeyPressed(event -> {
            stage.close();
        });
        scene.setOnMouseClicked(event -> {
            stage.close();
        });

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.showAndWait();

//        Dialog<ButtonType> dialog = new Dialog<>();
//        dialog.setTitle("Unhandled Exception");
//
//        final DialogPane dialogPane = dialog.getDialogPane();
//        dialogPane.setContentText("Exception details:");
//        dialogPane.getButtonTypes().addAll(ButtonType.OK);
//
//        String message = e.getMessage();
//
//        dialogPane.setContentText(message != null ? message : "Ooops :'(");
//        dialog.initModality(Modality.APPLICATION_MODAL);
//
//        ApplicationMode appMode = GameApplication.getInstance().getSettings().getApplicationMode();
//        if (appMode != ApplicationMode.RELEASE) {
//            Label label = new Label("Exception stacktrace:");
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            pw.close();
//
//            TextArea textArea = new TextArea(sw.toString());
//            textArea.setEditable(false);
//            textArea.setWrapText(true);
//            textArea.setPrefSize(600, 600);
//            textArea.setMaxWidth(Double.MAX_VALUE);
//            textArea.setMaxHeight(Double.MAX_VALUE);
//
//            GridPane.setVgrow(textArea, Priority.ALWAYS);
//            GridPane.setHgrow(textArea, Priority.ALWAYS);
//
//            GridPane root = new GridPane();
//            root.setVisible(false);
//            root.setMaxWidth(Double.MAX_VALUE);
//            root.add(label, 0, 0);
//            root.add(textArea, 0, 1);
//
//            dialogPane.setExpandableContent(root);
//        }
//
//        dialog.showAndWait();
        GameApplication.getInstance().exit();
    }
}
