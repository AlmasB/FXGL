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
package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ControlsfxSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ControlsfxSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Notify") {
            @Override
            protected void onActionBegin() {
                callMasker();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {

        Text text = new Text("testest");
        text.setTranslateY(20);

        getGameScene().addUINode(text);
    }

    private MaskerPane maskerPane;

    private void callMasker() {
        if (maskerPane == null) {
            maskerPane = new MaskerPane();
        }
    }


    private NotificationPane notificationPane;

    private void callNotify() {
        if (notificationPane == null) {
            notificationPane = new NotificationPane();

            notificationPane.getStylesheets().add(getAssetLoader().loadCSS("test.css").getExternalForm());
            notificationPane.getStyleClass().add("fxgl");

            //notificationPane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));

            //notificationPane.setStyle("-fx-background-color: linear-gradient(#000000, #474747 55%, #FFFFFF);");

//            notificationPane.getActions().addAll(new Action("Sync", ae -> {
//                // do sync
//
//                // then hide...
//                notificationPane.hide();
//            }));

            Pane tmp = new Pane();
            tmp.setPrefSize(getWidth(), 100);

            notificationPane.setContent(tmp);

            getGameScene().addUINode(notificationPane);
        } else {
            notificationPane.show("Message of the Day! Tick: " + getTick());
        }

//        String imagePath = HelloNotificationPane.class.getResource("notification-pane-warning.png").toExternalForm();
//        ImageView image = new ImageView(imagePath);
//        notificationPane.setGraphic(image);

//        Button showBtn = new Button("Show / Hide");
//        showBtn.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent arg0) {
//                if (notificationPane.isShowing()) {
//                    notificationPane.hide();
//                } else {
//                    notificationPane.show();
//                }
//            }
//        });
//
//        CheckBox cbSlideFromTop = new CheckBox("Slide from top");
//        cbSlideFromTop.setSelected(true);
//        notificationPane.showFromTopProperty().bind(cbSlideFromTop.selectedProperty());
//
//        cbUseDarkTheme = new CheckBox("Use dark theme");
//        cbUseDarkTheme.setSelected(false);
//        cbUseDarkTheme.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent arg0) {
//                updateBar();
//            }
//        });
//
//        cbHideCloseBtn = new CheckBox("Hide close button");
//        cbHideCloseBtn.setSelected(false);
//        cbHideCloseBtn.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent arg0) {
//                notificationPane.setCloseButtonVisible(!cbHideCloseBtn.isSelected());
//            }
//        });
//
//        textField = new TextField();
//        textField.setPromptText("Type text to display and press Enter");
//        textField.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent arg0) {
//                notificationPane.show(textField.getText());
//            }
//        });

//        VBox root = new VBox(10);
//        root.setPadding(new Insets(50, 0, 0, 10));
//        root.getChildren().addAll(showBtn, cbSlideFromTop, cbUseDarkTheme, cbHideCloseBtn, textField);

        //notificationPane.setContent(root);
    }

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        System.setProperty("prism.verbose", "true");
        //System.setProperty("prism.forcerepaint", "true");
        System.setProperty("prism.showdirty", "true");
        System.setProperty("quantum.verbose", "true");
        launch(args);
    }
}
