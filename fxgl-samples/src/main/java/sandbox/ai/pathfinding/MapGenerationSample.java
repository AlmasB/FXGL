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
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.pathfinding.Grid;
import com.almasb.fxgl.procedural.BiomeMapGenerator;
import com.almasb.fxgl.procedural.HeightMapGenerator;
import com.almasb.fxgl.texture.ColoredTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

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

        // these use various settings, comment out one and uncomment the other
        // to play around with values and see which values work

        coloredMap();

        //plain();

        //variety();
    }

    private void plain() {
        // tile size
        int size = 5;

        int W = getAppWidth() / size;
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

    private static class CustomHeightMapGenerator extends HeightMapGenerator {

        public CustomHeightMapGenerator(int width, int height) {
            super(width, height);
        }

        @Override
        public HeightData apply(int x, int y) {
            var nx = x * 1.0 / getWidth() - 0.5;
            var ny = y * 1.0 / getHeight() - 0.5;

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

            return new HeightData(x, y, noiseValue);
        }
    }

    private void variety() {
        // tile size
        int size = 5;

        int W = getAppWidth() / size;
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

    private void coloredMap() {
        // tile size
        int size = 1;

        int W = getAppWidth() / size;
        int H = getAppHeight() / size;

        Grid<HeightMapGenerator.HeightData> map = new Grid<>(HeightMapGenerator.HeightData.class, W, H, new CustomHeightMapGenerator(W, H));

        AnimatedColor water = new AnimatedColor(Color.DARKBLUE, Color.AQUA);
        AnimatedColor land = new AnimatedColor(Color.DARKGREEN, Color.LIGHTGREEN);
        AnimatedColor desert = new AnimatedColor(Color.YELLOW, Color.LIGHTYELLOW);
        AnimatedColor snow = new AnimatedColor(Color.BROWN, Color.WHITESMOKE);

        WritableImage image = new WritableImage(W, H);

        var interpolator = Interpolators.LINEAR.EASE_OUT();

        map.forEach(cell -> {
            AnimatedColor anim;

            if (cell.getHeight() < 0.2) {
                anim = water;
            } else if (cell.getHeight() < 0.4) {
                anim = land;
            } else if (cell.getHeight() < 0.9) {
                anim = desert;
            } else {
                anim = snow;
            }

            image.getPixelWriter().setColor(cell.getX(), cell.getY(), anim.getValue(cell.getHeight(), interpolator));
        });

        getGameScene().addUINode(new Texture(image));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
