/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.benchmark;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import kotlin.Unit;
import kotlin.system.TimingKt;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is a sample for testing the performance of tiled map loading
 *
 * @author Adam Bocco (adam.bocco) (adam.bocco@gmail.com)
 */
public class TiledBenchmarkSample2 extends GameApplication {

    private static String currentMapSelection = "";
    private static Text status;

    @Override
    protected void initSettings(GameSettings settings) {

        settings.setWidth(600);
        settings.setHeight(500);
        settings.setTitle("TiledBenchmarkSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initUI() {
        List<String> mapURLsList = List.of("tmx/benchmarking/csv_1layer_noflip_500x500.tmx",
                                    "tmx/benchmarking/gzip_5layers_noflip_500x500.tmx",
                                    "tmx/benchmarking/gzip_10layers_noflip_500x500.tmx",
                                    "tmx/benchmarking/csv_1layer_verticalflip_500x500.tmx",
                                    "tmx/benchmarking/csv_1layer_horizontalflip_500x500.tmx",
                                    "tmx/benchmarking/csv_1objectlayer_noflip_100x100.tmx",
                                    "tmx/benchmarking/csv_1objectlayer_verticalflip_100x100.tmx",
                                    "tmx/benchmarking/csv_1objectlayer_horizontalflip_100x100.tmx");

        ObservableList<String> mapURLs = FXCollections.observableList(mapURLsList);

        Text mapChoicesLabel = getUIFactoryService().newText("Map Choices: ", Color.BLACK, 14);

        ChoiceBox<String> mapChoices = getUIFactoryService().newChoiceBox(mapURLs);

        mapChoices.setOnAction(actionEvent -> currentMapSelection = mapChoices.getValue());

        Text iterationsLabel = getUIFactoryService().newText("Iterations: ", Color.BLACK, 14);
        TextField iterationsInput = new TextField();


        Button startTestButton = getUIFactoryService().newButton("Start");

        startTestButton.setOnAction(actionEvent -> {
            int iterations;
            try {
                iterations = Integer.parseInt(iterationsInput.getText());
            }
            catch (NumberFormatException e) {
                System.out.println("Enter a number of iterations");
                status.setText("Enter a number of iterations");
                return;
            }

            if (currentMapSelection.equals("")) {
                System.out.println("Choose a map to test");
                status.setText("Choose a map to test");
            }
            else {
                status.setText("Running test...");
                runMap(currentMapSelection, iterations);
            }
        });

        Text statusLabel = getUIFactoryService().newText("Status: ", Color.BLACK, 14);
        status = getUIFactoryService().newText("Choose a test map", Color.BLACK, 14);

        addUINode(mapChoicesLabel,10, 20);
        addUINode(mapChoices, 10, 25);
        addUINode(iterationsLabel, 10, 100);
        addUINode(iterationsInput, 10, 105);
        addUINode(startTestButton, 250, 20);
        addUINode(statusLabel, 10, 180);
        addUINode(status, 10, 200);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new TiledObjectFactory());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void runMap(String mapURL, int iterations) {

        getExecutor().startAsync(() -> {
            long totalTime = 0L;

            for (int i = 0; i < iterations; i++) {
                var time = TimingKt.measureNanoTime(() -> {
                    var level = getAssetLoader().loadLevel(mapURL, new TMXLevelLoader());

                    return Unit.INSTANCE;
                });
                totalTime += time;
                System.out.printf("Iteration " + (i+1) + " : %.2f seconds\n", time / 1_000_000_000.0);
            }
            String results = "Map: " + currentMapSelection +
                            "\nIterations: " + iterations +
                            "\nAverage: " + (totalTime/iterations) / 1_000_000_000.0;
            status.setText(results);
            System.out.println(results);
        });
    }

    public static class TiledObjectFactory implements EntityFactory {
        @Spawns("")
        public Entity spawnTiledObject(SpawnData data) {
            return entityBuilder(data).build();
        }
    }
}
