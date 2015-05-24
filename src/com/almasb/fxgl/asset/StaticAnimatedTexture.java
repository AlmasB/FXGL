package com.almasb.fxgl.asset;

import com.almasb.fxgl.GameApplication;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * A texture which is statically animated, i.e.
 * loops through its frames constantly
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public class StaticAnimatedTexture extends Texture {

    private Timeline timeline;

    /**
     *
     * @param image     actual image
     * @param frames    number of frames in spritesheet
     * @param duration duration of the animation
     *      use GameApplication.SECOND * n for convenience
     */
    /*package-private*/ StaticAnimatedTexture(Image image, int frames, double duration) {
        super(image);

        final double frameW = image.getWidth() / frames;

        this.setFitWidth(frameW);
        this.setFitHeight(image.getHeight());

        this.setViewport(new Rectangle2D(0, 0, frameW, image.getHeight()));

        SimpleIntegerProperty frameProperty = new SimpleIntegerProperty();
        frameProperty.addListener((obs, old, newValue) -> {
            this.setViewport(new Rectangle2D(newValue.intValue() * frameW, 0, frameW, image.getHeight()));
        });

        timeline = new Timeline(new KeyFrame(Duration.seconds(duration / GameApplication.SECOND), new KeyValue(frameProperty, frames - 1)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
