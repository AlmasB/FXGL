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
package com.almasb.fxgl.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import com.almasb.fxgl.GameApplication;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

/**
 * Default FXGL exception handler for unhandled exceptions, most
 * of which will have runtime nature.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public enum FXGLExceptionHandler implements ExceptionHandler {

    INSTANCE;

    private final Logger log = FXGLLogger.getLogger("FXGLExceptionHandler");

    @Override
    public void handle(Throwable e) {
        log.severe("Unhandled Exception:");
        log.severe(FXGLLogger.errorTraceAsString(e));
        log.severe("Application will now exit");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Unhandled Exception");

        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContentText("Exception details:");
        dialogPane.getButtonTypes().addAll(ButtonType.OK);

        String message = e.getMessage();

        dialogPane.setContentText(message != null ? message : "Ooops :'(");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ApplicationMode appMode = GameApplication.getInstance().getSettings().getApplicationMode();
        if (appMode != ApplicationMode.RELEASE) {
            Label label = new Label("Exception stacktrace:");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.close();

            TextArea textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(600, 600);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);

            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane root = new GridPane();
            root.setVisible(false);
            root.setMaxWidth(Double.MAX_VALUE);
            root.add(label, 0, 0);
            root.add(textArea, 0, 1);

            dialogPane.setExpandableContent(root);
        }

        dialog.showAndWait();
    }
}
