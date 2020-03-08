/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.anim;

import com.almasb.fxgl.animation.AnimatedPoint2D;
import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.ImagesKt;
import dev.AnimatedString;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use input service and bind actions to triggers.
 */
public class AnimatedStringSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidthFromRatio(16/9.0);
        settings.setTitle("AnimSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "f", () -> {
            animationBuilder()
                    .duration(Duration.seconds(3))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .animate(new AnimatedString("H", "Hello World. This is an example of an AnimatedString(from, to)!"))
                    .onProgress(s -> text.setText(s))
                    .buildAndPlay();
        });
    }

    private Text text;

    @Override
    protected void initGame() {
        text = getUIFactoryService().newText("", Color.BLUE, 24.0);


        addUINode(text, 150, 150);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
