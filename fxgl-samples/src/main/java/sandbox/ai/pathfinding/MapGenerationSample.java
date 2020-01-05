/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.ai.pathfinding;

import com.almasb.fxgl.animation.AnimatedColor;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.GameView;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.Grid;
import com.almasb.fxgl.procedural.BiomeMapGenerator;
import com.almasb.fxgl.procedural.HeightMapGenerator;
import com.almasb.fxgl.texture.ColoredTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.core.math.FXGLMath.map;
import static com.almasb.fxgl.core.math.FXGLMath.noise2D;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use PCG maps.
 * Some concepts in this file are adapted from https://www.redblobgames.com/maps/terrain-from-noise/
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MapGenerationSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800 + 200);
        settings.setHeight(800);
        settings.setTitle("MapGenerationSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {

        // these use various settings, comment out one and uncomment the other
        // to play around with values and see which values work

        coloredMap();

        //plain();

        //variety();
    }

    private void plain() {
        // tile size
        int size = 5;

        int W = (getAppWidth() - 200) / size;
        int H = getAppHeight() / size;

        Grid<BiomeMapGenerator.BiomeData> map = new Grid<>(BiomeMapGenerator.BiomeData.class, W, H, new BiomeMapGenerator(W, H, 2.4));

        map.forEach(cell -> {
            Texture texture;

            if (cell.getElevation() < 0.2) {
                // water
                texture = new ColoredTexture(size, size, Color.BLUE);
            } else if (cell.getElevation() < 0.8) {
                // grass
                texture = texture("grass.png", size, size);
            } else {
                // in-land grass / mud?
                texture = texture("grass.png", size, size).desaturate().brighter().desaturate();
            }

            entityBuilder()
                    .at(cell.getX()*size, cell.getY()*size)
                    .view(texture)
                    .buildAndAttach();
        });
    }

    // TODO: interactive sample where the apply() func can be edited
    private static class CustomHeightMapGenerator extends HeightMapGenerator {

        public CustomHeightMapGenerator(int width, int height) {
            super(width, height);
        }

        @Override
        public HeightData apply(int x, int y) {
            var nx = x * 1.0 / getWidth() - 0.5;
            var ny = y * 1.0 / getHeight() - 0.5;

            double noiseValue = noise2D(nx, ny)
                    + 0.5 * noise2D(getAppWidth() / 400.0 * nx, getAppHeight() / 400.0 * ny)
                    + 0.2 * noise2D(7 * nx, 7 * ny);

            noiseValue *= noiseValue;

            if (noiseValue < 0) {
                noiseValue = 0;
            }

            if (noiseValue > 1) {
                noiseValue = 1;
            }

            return new HeightData(x, y, noiseValue);
        }
    }

    private void variety() {
        // tile size
        int size = 5;

        int W = (getAppWidth() - 200) / size;
        int H = getAppHeight() / size;

        Grid<HeightMapGenerator.HeightData> map = new Grid<>(HeightMapGenerator.HeightData.class, W, H, new CustomHeightMapGenerator(W, H));

        map.forEach(cell -> {
            Texture texture;

            if (cell.getHeight() < 0.2) {
                // water
                texture = new ColoredTexture(size, size, Color.BLUE);
            } else if (cell.getHeight() < 0.8) {
                // grass
                texture = texture("grass.png", size, size);
            } else {
                // in-land grass / mud?
                texture = texture("grass.png", size, size).desaturate().brighter().desaturate();
            }

            entityBuilder()
                    .at(cell.getX()*size, cell.getY()*size)
                    .view(texture)
                    .buildAndAttach();
        });
    }

    private Interpolators interpolator = Interpolators.LINEAR;

    private void coloredMap() {
        // tile size
        int size = 1;

        int W = (getAppWidth() + 200) / size;
        int H = (getAppHeight() + 200) / size;

        Grid<HeightMapGenerator.HeightData> map = new Grid<>(HeightMapGenerator.HeightData.class, W, H, new CustomHeightMapGenerator(W, H));

        AnimatedColor water = new AnimatedColor(Color.DARKBLUE, Color.AQUA);
        AnimatedColor land = new AnimatedColor(Color.DARKGREEN, Color.LIGHTGREEN);
        AnimatedColor desert = new AnimatedColor(Color.GREENYELLOW, Color.LIGHTYELLOW);
        AnimatedColor snow = new AnimatedColor(Color.GREEN, Color.WHITESMOKE);

        WritableImage image = new WritableImage(W, H);

        map.forEach(cell -> {

            AnimatedColor anim;

            double adjustedValue;

            if (cell.getHeight() < 0.2) {
                anim = water;
                adjustedValue = map(cell.getHeight(), 0.0, 0.2, 0.0, 1.0);
            } else if (cell.getHeight() < 0.4) {
                anim = land;
                adjustedValue = map(cell.getHeight(), 0.2, 0.4, 0.0, 1.0);
            } else if (cell.getHeight() < 0.9) {
                anim = desert;
                adjustedValue = map(cell.getHeight(), 0.4, 0.9, 0.0, 1.0);
            } else {
                anim = snow;
                adjustedValue = map(cell.getHeight(), 0.9, 1.0, 0.0, 1.0);
            }

            image.getPixelWriter().setColor(cell.getX(), cell.getY(), anim.getValue(adjustedValue, interpolator.EASE_OUT()));
        });

        getGameScene().getViewport().bindToEntity(new Entity(), 0, 0);

        var t = new Texture(image);
        t.setTranslateX(-100);
        t.setTranslateY(-100);

        getGameScene().addGameView(new GameView(t, 0));
    }

    @Override
    protected void initUI() {
        var choiceBox = new ChoiceBox<Interpolators>();
        choiceBox.setItems(FXCollections.observableArrayList(Interpolators.values()));
        choiceBox.setValue(Interpolators.LINEAR);

        var btn = new Button("Generate");
        btn.setOnAction(e -> {
            interpolator = choiceBox.getValue();
            getGameController().startNewGame();
        });

        addUINode(new VBox(btn, choiceBox), getAppWidth() - 200, 0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
