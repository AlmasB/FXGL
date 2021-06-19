/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import dev.DeveloperWASDControl;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EmbeddedSample extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        var fxglRoot = GameApplication.embeddedLaunch(new MyGame());
        fxglRoot.setTranslateX(40);

        var area = new TextArea("Some text in this area");
        area.setPrefWidth(300);

        var btnResize = new Button("Example Resize");
        btnResize.setOnAction(e -> {
            // Note: whether the game is scaled up or actually resized depends on game settings (settings.setScaleAffectedOnResize();)
            fxglRoot.setRenderWidth(fxglRoot.getRenderWidth() + 15);
            fxglRoot.setRenderHeight(fxglRoot.getRenderHeight() + 15);
        });

        BorderPane root = new BorderPane();
        root.setPrefSize(800, 600);
        root.setTop(new HBox(btnResize, new Button("Button2"), new Button("Button3"), new Button("Button4")));
        root.setBottom(new HBox(new Text("The status area")));
        root.setLeft(area);
        root.setRight(new Text("RIGHT"));
        root.setCenter(fxglRoot);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        getGameController().exit();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public class MyGame extends GameApplication {

        private Text textPos;

        @Override
        protected void initSettings(GameSettings settings) {
            settings.setWidth(400);
            settings.setHeight(400);
        }

        @Override
        protected void initGameVars(Map<String, Object> vars) {
            vars.put("entities", 0);
        }

        @Override
        protected void initGame() {
            getGameWorld().addEntityFactory(new MyFactory());
            getGameScene().setBackgroundColor(Color.BLACK);

            var text = getUIFactoryService().newText("", Color.WHITE, 20.0);
            text.textProperty().bind(getip("entities").asString("Entities: %d"));

            addUINode(text, 25, 25);

            spawn("player");

            run(this::spawnCrystal, Duration.seconds(3));
        }

        @Override
        protected void initUI() {
            textPos = new Text();
            textPos.setFill(Color.WHITE);

            addUINode(textPos, 20, 80);
        }

        @Override
        protected void onUpdate(double tpf) {
            textPos.setText(getInput().getMousePositionWorld() + "");
        }

        private void spawnCrystal() {
            int numToSpawn = 50;

            for (int i = 0; i < numToSpawn; i++) {
                spawn("crystal");
            }

            inc("entities", +numToSpawn);
        }

        public class MyFactory implements EntityFactory {

            @Spawns("crystal")
            public Entity newEntity(SpawnData data) {
                return entityBuilder()
                        .at(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 55, getAppHeight() - 55)))
                        .viewWithBBox(texture("ball.png", 32, 32))
                        .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 250))
                        .with(new ExpireCleanComponent(Duration.seconds(5)).animateOpacity())
                        .onNotActive(e -> inc("entities", -1))
                        .build();
            }

            @Spawns("player")
            public Entity newPlayer(SpawnData data) {
                return entityBuilder()
                        .viewWithBBox(texture("brick.png", 32, 32))
                        .with(new DeveloperWASDControl())
                        .build();
            }
        }
    }
}
