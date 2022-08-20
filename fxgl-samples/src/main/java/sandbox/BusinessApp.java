/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows a simplistic example of using FXGL for general-purpose applications.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BusinessApp extends GameApplication {

    private VBox loginRoot;
    private StackPane mainRoot;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);
        getGameScene().setCursor(Cursor.DEFAULT);

        var fieldUsername = new TextField();
        var fieldPassword = new PasswordField();

        var box1 = new HBox(10, new Label("Username: "), fieldUsername);
        var box2 = new HBox(10, new Label("Password: "), fieldPassword);
        box1.setAlignment(Pos.CENTER);
        box2.setAlignment(Pos.CENTER);

        var btn = new Button("Submit");
        btn.setOnAction(e -> {
            if (isValid(fieldUsername.getText(), fieldPassword.getText())) {
                showMainView();
            }
        });

        loginRoot = new VBox(
                10,
                box1,
                box2,
                btn
        );
        loginRoot.setAlignment(Pos.CENTER);
        loginRoot.setPrefSize(getAppWidth(), getAppHeight());

        var btnAddNew = new Button("Add New");
        btnAddNew.setOnAction(e -> {
            showAddNewDialog();
        });

        mainRoot = new StackPane(btnAddNew);
        mainRoot.setPrefSize(getAppWidth(), getAppHeight());

        addUINode(loginRoot);
    }

    private boolean isValid(String username, String password) {
        // code to check if account is valid
        return true;
    }

    private void showMainView() {
        removeUINode(loginRoot);
        addUINode(mainRoot);
    }

    private void showAddNewDialog() {
        getDialogService().showInputBox("Please enter username", username -> {
            getDialogService().showInputBox("Please enter password", password -> {
                addNew(username, password);
            });
        });
    }

    private void addNew(String username, String password) {
        // code to add new accounts
        System.out.println("Added: " + username + "," + password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
