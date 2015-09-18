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

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.util.FXGLLogger;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Default FXGL dialog box. Represented by a rectangle with a black
 * background and white stroke. The box opens as a separate stage
 * on top of the game application but
 * with the same window onwer as the FXGL game application. In terms
 * of input events the dialog box has its own scene and has no
 * connection to the game.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public final class FXGLDialogBox extends Stage {

    private static final Logger log = FXGLLogger.getLogger("FXGLDialogBox");

    private StackPane root = new StackPane();
    private Scene scene = new Scene(root);

    public FXGLDialogBox() {
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.WINDOW_MODAL);
        initOwner(GameApplication.getInstance().getSceneManager().getScene().getWindow());
        setScene(scene);

        try {
            root.getStylesheets().add(AssetManager.INSTANCE.loadCSS("fxgl_button.css"));
        }
        catch (Exception e) {
            log.warning("Failed to apply fxgl_button.css stylesheet: " + e.getMessage());
        }
    }

    public void showMessageBox(String message) {
        Text text = new Text(message);
        text.setFill(Color.WHITE);
        text.setFont(GameApplication.getInstance().getSceneManager().getDefaultFont(18));

        FXGLButton btnOK = new FXGLButton("OK");
        btnOK.setOnAction(e -> {
            close();
        });

        VBox vbox = new VBox(50, text, btnOK);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 200), text.getLayoutBounds().getHeight() * 2 + 50));

        setContent(vbox);
        show();
    }

    public void showConfirmationBox(String message, Consumer<Boolean> resultCallback) {
        Text text = new Text(message);
        text.setFill(Color.WHITE);
        text.setFont(GameApplication.getInstance().getSceneManager().getDefaultFont(18));

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

        setContent(vbox);
        show();
    }

    public void showInputBox(String message, Consumer<String> resultCallback) {
        Text text = new Text(message);
        text.setFill(Color.WHITE);
        text.setFont(GameApplication.getInstance().getSceneManager().getDefaultFont(18));

        TextField field = new TextField();
        field.setMaxWidth(Math.max(text.getLayoutBounds().getWidth(), 200));
        field.setFont(GameApplication.getInstance().getSceneManager().getDefaultFont(18));

        FXGLButton btnOK = new FXGLButton("OK");
        btnOK.setOnAction(e -> {
            close();
            resultCallback.accept(field.getText());
        });

        VBox vbox = new VBox(50, text, field, btnOK);
        vbox.setAlignment(Pos.CENTER);
        vbox.setUserData(new Point2D(Math.max(text.getLayoutBounds().getWidth(), 200), text.getLayoutBounds().getHeight() * 3 + 50 * 2));

        setContent(vbox);
        show();
    }

    /**
     * Replaces all content of the scene root by given node.
     * Creates an appropriate size rectangle box around the node
     * to serve as background.
     *
     * @param n
     */
    private void setContent(Node n) {
        Point2D size = (Point2D) n.getUserData();

        Rectangle box = new Rectangle(size.getX() + 200, size.getY() + 100);
        box.setStroke(Color.AZURE);

        root.getChildren().setAll(box, n);
    }
}
