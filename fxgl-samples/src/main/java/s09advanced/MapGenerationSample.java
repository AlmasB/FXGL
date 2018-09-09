/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced;

import com.almasb.fxgl.animation.AnimatedColor;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.collection.Grid;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.extra.algorithm.procedural.BiomeMapGenerator;
import com.almasb.fxgl.extra.algorithm.procedural.HeightMapGenerator;
import com.almasb.fxgl.extra.algorithm.procedural.MapGenerator;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Shows how to use PCG maps.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MapGenerationSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("MapGenerationSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {

        // these use various settings, comment out one and uncommend the other
        // to play around with values and see which values work

        coloredMap();

        //plain();

        //variety();
    }

    private void plain() {
        // tile size
        int size = 5;

        int W = getWidth() / size;
        int H = getHeight() / size;

        MapGenerator<BiomeMapGenerator.BiomeData> gen = new BiomeMapGenerator(2.4);
        Grid<BiomeMapGenerator.BiomeData> map = gen.generate(W, H);

        map.forEach((data, x, y) -> {
            Texture texture;

            if (data.getElevation() < 0.2) {
                // water
                texture = new Texture(new WritableImage(size, size)).replaceColor(Color.TRANSPARENT, Color.BLUE);
            } else if (data.getElevation() < 0.8) {
                // grass
                texture = DSLKt.texture("grass.png", size, size);
            } else {
                // in-land grass / mud?
                texture = DSLKt.texture("grass.png", size, size).desaturate().brighter().desaturate();
            }

            Entities.builder()
                    .at(x*size, y*size)
                    .viewFromNode(texture)
                    .buildAndAttach();
        });
    }

    private void variety() {
        // tile size
        int size = 5;

        int W = getWidth() / size;
        int H = getHeight() / size;

        HeightMapGenerator gen = new HeightMapGenerator(2.4);
        gen.setGenFunction((nx, ny, freq) -> {
            double e = FXGLMath.noise2D(nx, ny)
                    + 0.5 * FXGLMath.noise2D(2 * nx, 2 * ny)
                    + 0.25 * FXGLMath.noise2D(9 * nx, 9 * ny);
            double noiseValue = Math.pow(e, 3.3);

            if (noiseValue < 0) {
                noiseValue = 0;
            }

            if (noiseValue > 1) {
                noiseValue = 1;
            }

            return noiseValue;
        });

        Grid<Double> map = gen.generate(W, H);

        map.forEach((data, x, y) -> {
            Texture texture;

            if (data < 0.2) {
                // water
                texture = new Texture(new WritableImage(size, size)).replaceColor(Color.TRANSPARENT, Color.BLUE);
            } else if (data < 0.8) {
                // grass
                texture = DSLKt.texture("grass.png", size, size);
            } else {
                // in-land grass / mud?
                texture = DSLKt.texture("grass.png", size, size).desaturate().brighter().desaturate();
            }

            Entities.builder()
                    .at(x*size, y*size)
                    .viewFromNode(texture)
                    .buildAndAttach();
        });
    }

    private void coloredMap() {
        // tile size
        int size = 1;

        int W = getWidth() / size;
        int H = getHeight() / size;

        HeightMapGenerator gen = new HeightMapGenerator(2.4);
        gen.setGenFunction((nx, ny, freq) -> {
            double e = FXGLMath.noise2D(nx, ny)
                    + 0.5 * FXGLMath.noise2D(2 * nx, 2 * ny)
                    + 0.25 * FXGLMath.noise2D(9 * nx, 9 * ny);
            double noiseValue = Math.pow(e, 3.3);

            if (noiseValue < 0) {
                noiseValue = 0;
            }

            if (noiseValue > 1) {
                noiseValue = 1;
            }

            return noiseValue;
        });

        Grid<Double> map = gen.generate(W, H);

        // use different interpolators if needed
        AnimatedColor water = new AnimatedColor(Color.DARKBLUE, Color.AQUA, Interpolators.LINEAR.EASE_OUT());
        AnimatedColor land = new AnimatedColor(Color.DARKGREEN, Color.LIGHTGREEN, Interpolators.LINEAR.EASE_OUT());
        AnimatedColor desert = new AnimatedColor(Color.YELLOW, Color.LIGHTYELLOW);
        AnimatedColor snow = new AnimatedColor(Color.BROWN, Color.WHITESMOKE);

        WritableImage image = new WritableImage(W, H);

        map.forEach((data, x, y) -> {
            AnimatedColor anim;

            if (data < 0.2) {
                anim = water;
            } else if (data < 0.4) {
                anim = land;
            } else if (data < 0.8) {
                anim = desert;
            } else {
                anim = snow;
            }

            image.getPixelWriter().setColor(x, y, anim.getValue(data));
        });

        getGameScene().addUINode(new Texture(image));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
