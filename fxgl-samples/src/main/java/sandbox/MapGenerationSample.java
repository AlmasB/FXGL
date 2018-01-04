/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.algorithm.Grid;
import com.almasb.fxgl.algorithm.MapGenerator;
import com.almasb.fxgl.algorithm.TileType;
import com.almasb.fxgl.animation.AnimatedColor;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import kotlin.Pair;

import java.util.Arrays;

/**
 * This is an example of a minimalistic FXGL game application.
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

        int W = getWidth() / 10;
        int H = getHeight() / 10;

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {

                double nx = x * 1.0 / W - 0.5;
                double ny = y * 1.0 / H - 0.5;

                // wavelength = map_size / frequency
                //double noiseValue = FXGLMath.noise2D(10 * nx, 10 * ny);

                double e = FXGLMath.noise2D(nx, ny)
                        + 0.5 * FXGLMath.noise2D(2 * nx, 2 * ny)
                        + 0.25 * FXGLMath.noise2D(9 * nx, 9 * ny);
                double noiseValue = Math.pow(e, 3.3);

                //double refineValue = FXGLMath.noise2D(15 * nx, 15 * ny);

                if (noiseValue < 0) {
                    //System.out.println("< 0 : " + noiseValue);
                    noiseValue = 0;
                }

                if (noiseValue > 1) {
                    //System.out.println("> 1 : " + noiseValue);
                    noiseValue = 1;
                }

                Texture color;

                if (noiseValue < 0.2) {
                    color = new Texture(new WritableImage(10, 10)).replaceColor(Color.TRANSPARENT, Color.BLUE);
                } else if (noiseValue < 0.8) {
                    color = DSLKt.texture("grass.png", 10, 10);
                } else {
                    color = DSLKt.texture("brick.png", 10, 10);
                }

                Entities.builder()
                        .at(x*10, y*10)
                        .viewFromNode(color)
                        .buildAndAttach();
            }
        }
    }

    private void color() {
        int W = getWidth();
        int H = getHeight();

        // use different interpolators?
        AnimatedColor water = new AnimatedColor(Color.DARKBLUE, Color.AQUA, Interpolators.LINEAR.EASE_OUT());
        AnimatedColor land = new AnimatedColor(Color.DARKGREEN, Color.LIGHTGREEN, Interpolators.LINEAR.EASE_OUT());
        AnimatedColor desert = new AnimatedColor(Color.YELLOW, Color.LIGHTYELLOW);
        AnimatedColor snow = new AnimatedColor(Color.BROWN, Color.WHITESMOKE);

        WritableImage image = new WritableImage(W, H);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {

                double nx = x * 1.0 / W - 0.5;
                double ny = y * 1.0 / H - 0.5;

                // wavelength = map_size / frequency
                //double noiseValue = FXGLMath.noise2D(10 * nx, 10 * ny);

                double e = FXGLMath.noise2D(nx, ny)
                        + 0.5 * FXGLMath.noise2D(2 * nx, 2 * ny)
                        + 0.25 * FXGLMath.noise2D(9 * nx, 9 * ny);
                double noiseValue = Math.pow(e, 3.3);

                //double refineValue = FXGLMath.noise2D(15 * nx, 15 * ny);

                if (noiseValue < 0) {
                    //System.out.println("< 0 : " + noiseValue);
                    noiseValue = 0;
                }

                if (noiseValue > 1) {
                    //System.out.println("> 1 : " + noiseValue);
                    noiseValue = 1;
                }

                AnimatedColor anim;

                if (noiseValue < 0.2) {
                    anim = water;
                } else if (noiseValue < 0.4) {
                    anim = land;
                } else if (noiseValue < 0.8) {
                    anim = desert;
                } else {
                    anim = snow;
                }

                image.getPixelWriter().setColor(x, y, anim.getValue(noiseValue));

            }
        }

        getGameScene().addUINode(new Texture(image));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
