/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ui.FXGLButton;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import jfxtras.scene.control.window.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * In-game dialog pane. The pane fills the whole scene area so that user
 * input does not pass through to the underlying nodes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DialogPane extends Pane {

    //private static final Logger log = FXGLLoggerOld.getLogger("FXGL.DialogPane");

    public static final Predicate<String> ALPHANUM = input -> input.matches("^[\\pL\\pN]+$");

    private Window window = new Window();
    private Display display;

    private Runnable onShown = null, onClosed = null;

    DialogPane(Display display) {
        this.display = display;

        display.currentSceneProperty().addListener((o, oldScene, newScene) -> {
            // if we somehow changed scene while the dialog is showing
            if (isShowing()) {
                oldScene.getRoot().getChildren().remove(this);
                newScene.getRoot().getChildren().add(this);
            }
        });

        double width = FXGL.getSettings().getWidth();
        double height = FXGL.getSettings().getHeight();

        setPrefSize(width, height);
        setBackground(new Background(new BackgroundFill(Color.rgb(127, 127, 123, 0.5), null, null)));

        window.setResizableWindow(false);
        window.setMovable(false);
        window.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        window.layoutXProperty().bind(window.widthProperty().divide(2).negate().add(width / 2));
        window.layoutYProperty().bind(window.heightProperty().divide(2).negate().add(height / 2));

        getChildren().add(window);

        // this is a workaround to avoid users traversing "through" the dialog to underlying nodes
        initTraversalPolicy();
    }

    private void initTraversalPolicy() {
        this.setImpl_traversalEngine(new ParentTraversalEngine(this, new Algorithm() {
            @Override
            public Node select(Node owner, Direction dir, TraversalContext context) {
                return window;
            }

            @Override
            public Node selectFirst(TraversalContext context) {
                return window;
            }

            @Override
            public Node selectLast(TraversalContext context) {
                return window;
            }
        }));
    }

    public boolean isShowing() {
        return getParent() != null;
    }

    /**
     * Shows a simple message box with OK button.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param message message to show
     */
    void showMessageBox(String message) {
        showMessageBox(message, () -> {});
    }

    /**
     * Shows a simple message box with OK button.
     * Calls back the given runnable on close.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param message message to show
     * @param callback function to call when closed
     */
    void showMessageBox(String message, Runnable callback) {
        Text text = createMessage(message);

        FXGLButton btnOK = new FXGLButton("OK");
        btnOK.setOnAction(e -> {
            close();
            callback.run();
        });

        VBox vbox = new VBox(50, text, btnOK);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 200), text.getLayoutBounds().getHeight() * 2 + 50));

        setContent("Message", vbox);
        show();
    }

    /**
     * Shows an error box with OK and LOG buttons.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param errorMessage error message to show
     */
    void showErrorBox(String errorMessage) {
        showErrorBox(new RuntimeException(errorMessage));
    }

    /**
     * Shows an error box with OK and LOG buttons.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param errorMessage error message to show
     * @param callback function to call back when closed
     */
    void showErrorBox(String errorMessage, Runnable callback) {
        showErrorBox(new RuntimeException(errorMessage), callback);
    }

    /**
     * Shows an error box with OK and LOG buttons.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param error error to show
     */
    void showErrorBox(Throwable error) {
        showErrorBox(error, () -> {});
    }

    /**
     * Shows an error box with OK and LOG buttons.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param error error to show
     * @param callback function to call when closed
     */
    void showErrorBox(Throwable error, Runnable callback) {
        Text text = createMessage(error.toString());

        FXGLButton btnOK = new FXGLButton("OK");
        btnOK.setOnAction(e -> {
            close();
            callback.run();
        });

        FXGLButton btnLog = new FXGLButton("LOG");
        btnLog.setOnAction(e -> {
            close();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            error.printStackTrace(pw);
            pw.close();

            try {
                Files.write(Paths.get("LastException.log"), Arrays.asList(sw.toString().split("\n")));
                showMessageBox("Log has been saved as LastException.log");
            } catch (Exception ex) {
                showMessageBox("Failed to save log file");
            }

            callback.run();
        });

        HBox hbox = new HBox(btnOK, btnLog);
        hbox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(50, text, hbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 400), text.getLayoutBounds().getHeight() * 2 + 50));

        setContent("Error", vbox);
        show();
    }

    /**
     * Shows confirmation message box with YES and NO buttons.
     * <p>
     * The callback function will be invoked with boolean answer
     * as parameter.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param message message to show
     * @param resultCallback result function to call back
     */
    void showConfirmationBox(String message, Consumer<Boolean> resultCallback) {
        Text text = createMessage(message);

        FXGLButton btnYes = new FXGLButton("YES");
        btnYes.setOnAction(e -> {
            close();
            resultCallback.accept(true);
        });

        FXGLButton btnNo = new FXGLButton("NO");
        btnNo.setOnAction(e -> {
            close();
            resultCallback.accept(false);
        });

        HBox hbox = new HBox(btnYes, btnNo);
        hbox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(50, text, hbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 400), text.getLayoutBounds().getHeight() * 2 + 50));

        setContent("Confirmation", vbox);
        show();
    }

    /**
     * Shows input box with input field and OK button.
     * The button will stay disabled until there is at least
     * 1 character in the input field.
     * <p>
     * The callback function will be invoked with input field text
     * as parameter.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param message message to show
     * @param resultCallback result function to call back
     */
    void showInputBox(String message, Consumer<String> resultCallback) {
        showInputBox(message, input -> true, resultCallback);
    }

    /**
     * Shows input box with input field and OK button.
     * The button will stay disabled until the input passes given filter.
     * <p>
     * The callback function will be invoked with input field text
     * as parameter.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param message message to show
     * @param filter the filter to validate input
     * @param resultCallback result function to call back
     */
    void showInputBox(String message, Predicate<String> filter, Consumer<String> resultCallback) {
        Text text = createMessage(message);

        TextField field = new TextField();
        field.setMaxWidth(Math.max(text.getLayoutBounds().getWidth(), 200));
        field.setFont(FXGL.getUIFactory().newFont(18));

        FXGLButton btnOK = new FXGLButton("OK");

        field.textProperty().addListener((observable, oldValue, newInput) -> {
            btnOK.setDisable(newInput.isEmpty() || !filter.test(newInput));
        });

        btnOK.setDisable(true);
        btnOK.setOnAction(e -> {
            close();
            resultCallback.accept(field.getText());
        });

        VBox vbox = new VBox(50, text, field, btnOK);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 200), text.getLayoutBounds().getHeight() * 3 + 50 * 2));

        setContent("Input", vbox);
        show();
    }

    /**
     * Shows input box with input field and OK button.
     * The button will stay disabled until the input passes given filter.
     * <p>
     * The callback function will be invoked with input field text
     * as parameter.
     * <p>
     * Opening more than 1 dialog box is not allowed.
     *
     * @param message message to show
     * @param filter the filter to validate input
     * @param resultCallback result function to call back or empty string if use cancelled the dialog
     */
    void showInputBoxWithCancel(String message, Predicate<String> filter, Consumer<String> resultCallback) {
        Text text = createMessage(message);

        TextField field = new TextField();
        field.setMaxWidth(Math.max(text.getLayoutBounds().getWidth(), 200));
        field.setFont(FXGL.getUIFactory().newFont(18));

        Button btnOK = FXGL.getUIFactory().newButton("OK");

        field.textProperty().addListener((observable, oldValue, newInput) -> {
            btnOK.setDisable(newInput.isEmpty() || !filter.test(newInput));
        });

        btnOK.setDisable(true);
        btnOK.setOnAction(e -> {
            close();
            resultCallback.accept(field.getText());
        });

        Button btnCancel = FXGL.getUIFactory().newButton("CANCEL");
        btnCancel.setOnAction(e -> {
            close();
            resultCallback.accept("");
        });

        HBox hBox = new HBox(btnOK, btnCancel);
        hBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(50, text, field, hBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 200), text.getLayoutBounds().getHeight() * 3 + 50 * 2));

        setContent("Input", vbox);
        show();
    }

    /**
     * Shows arbitrary box with message, content and given buttons.
     *
     * @param message the header message
     * @param content the box content
     * @param buttons buttons present in the box
     */
    void showBox(String message, Node content, Button... buttons) {
        for (Button btn : buttons) {
            EventHandler<ActionEvent> handler = btn.getOnAction();

            btn.setOnAction(e -> {
                close();

                if (handler != null)
                    handler.handle(e);
            });
        }

        Text text = createMessage(message);

        HBox hbox = new HBox(buttons);
        hbox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(50, text, content, hbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 200),
                text.getLayoutBounds().getHeight() * 3 + 50 * 2 + content.getLayoutBounds().getHeight()));

        setContent("Dialog", vbox);
        show();
    }

    private Text createMessage(String message) {
        return FXGL.getUIFactory().newText(message);
    }

    /**
     * Replaces all content of the scene root by given node.
     * Creates an appropriate size rectangle box around the node
     * to serve as background.
     *
     * @param title window title
     * @param n content node
     */
    private void setContent(String title, Node n) {
        if (isShowing()) {
            // dialog was requested while being shown so remember state
            states.push(new DialogData(window.getTitle(), window.getContentPane()));
        }

        Point2D size = (Point2D) n.getUserData();

        Rectangle box = new Rectangle(size.getX() + 200, size.getY() + 100);
        box.setStroke(Color.AZURE);

        StackPane root = new StackPane();
        root.getChildren().setAll(box, n);

        window.setTitle(title);
        window.setContentPane(root);
    }

    void setOnClosed(Runnable onClosed) {
        this.onClosed = onClosed;
    }

    void setOnShown(Runnable onShown) {
        this.onShown = onShown;
    }

    private Deque<DialogData> states = new ArrayDeque<>();

    void show() {
        if (!isShowing()) {
            display.getCurrentScene().getRoot().getChildren().add(this);

            this.requestFocus();

            if (onShown != null)
                onShown.run();
        }
    }

    void close() {
        if (states.isEmpty()) {
            display.getCurrentScene().getRoot().getChildren().remove(this);

            if (onClosed != null)
                onClosed.run();
        } else {
            DialogData data = states.pop();
            window.setTitle(data.title);
            window.setContentPane(data.contentPane);
        }
    }

    private static class DialogData {
        String title;
        Pane contentPane;

        DialogData(String title, Pane contentPane) {
            this.title = title;
            this.contentPane = contentPane;
        }
    }
}
