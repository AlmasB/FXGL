/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.Animation;
import javafx.animation.Interpolator;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class StringAnimation extends Animation<String> {

    public StringAnimation(String s) {
        super(Duration.seconds(1), new AnimatedValue<String>("", s) {
            @Override
            public String animate(String val1, String val2, double progress, @NotNull Interpolator interpolator) {
                if (progress < 0.5)
                    return val1;
                else
                    return val2;
            }
        });
    }

    @Override
    public void onProgress(String value) {
        System.out.println(value);
    }

    private static class AnimatedString extends AnimatedValue<String> {

        public AnimatedString(String from, String to, @NotNull Interpolator interpolator) {
            super(from, to, interpolator);
        }

        @Override
        public String animate(String val1, String val2, double progress, @NotNull Interpolator interpolator) {
            double t = interpolator.interpolate(0.0, 1.0, progress);

            int endIndex = 0;

            return
        }
    }

    public static void main(String[] args) {
        StringAnimation a = new StringAnimation("Hello world");
        for (int i = 0; i < 10; i++) {
            a.onUpdate(i);
        }
    }
}
