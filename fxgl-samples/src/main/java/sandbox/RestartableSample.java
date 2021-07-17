/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sandbox.test3d.Model3DSample;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RestartableSample extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    private Parent createContent() {
        List<Supplier<GameApplication>> games = List.of(
                SpriteSheetAnimationApp::new,
                ScrollingBackgroundSample::new,
                PlatformerSample::new,
                TiledMapSample::new,
                Model3DSample::new
        );

        var root = new VBox();
        root.setPrefSize(1280, 720);

        var hbox = new HBox(5);

        for (int i = 0; i < games.size(); i++) {
            var btn = new Button("game " + i);
            var gameConstructor = games.get(i);

            btn.setOnAction(e -> {
                GameApplication.embeddedShutdown();

                var pane = GameApplication.embeddedLaunch(gameConstructor.get());

                root.getChildren().set(1, pane);
            });

            hbox.getChildren().add(btn);
        }

        root.getChildren().addAll(hbox, new Pane());
        return root;
    }

    @Override
    public void stop() throws Exception {
        FXGL.getGameController().exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
