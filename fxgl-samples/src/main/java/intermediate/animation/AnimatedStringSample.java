/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.animation;

import com.almasb.fxgl.animation.AnimatedStringIncreasing;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use AnimatedString.
 * Press F to play the animation.
 */
public class AnimatedStringSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            animationBuilder()
                    .duration(Duration.seconds(3))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .animate(new AnimatedStringIncreasing("Hello World. This is an example of an AnimatedString(from, to)!"))
                    .onProgress(s -> text.setText(s))
                    .buildAndPlay();
        });
    }

    private Text text;

    @Override
    protected void initGame() {
        text = getUIFactoryService().newText("", Color.BLUE, 24.0);

        addUINode(text, 0, 150);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
