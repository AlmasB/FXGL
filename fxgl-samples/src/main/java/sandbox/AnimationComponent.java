/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.FrameData;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import kotlin.Pair;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AnimationComponent extends Component {

    private int speed = 0;

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk;

    public AnimationComponent() {
        animIdle = new AnimationChannel(FXGL.image("newdude.png"), 4, 32, 42, Duration.seconds(1), 1, 1);
        //animWalk = new AnimationChannel(FXGL.image("newdude.png"), 4, 32, 42, Duration.seconds(1), 0, 3);

//        animIdle = new AnimationChannel(FXGL.image("sprite_sheet1.png"), Duration.seconds(1.5), 7, List.of(
//                new AnimationChannelData(0, 5, 77, 37),
//                new AnimationChannelData(7, 12, 77, 37),
//                new AnimationChannelData(14, 19, 77, 37)
//        ));
//
//        animWalk = new AnimationChannel(FXGL.image("sprite_sheet1.png"), Duration.seconds(1.5), 7, List.of(
//                new AnimationChannelData(21, 26, 77, 37),
//                new AnimationChannelData(28, 33, 77, 37),
//                new AnimationChannelData(35, 40, 77, 37)
//        ));

        animWalk = new AnimationChannel(FXGL.image("newdude.png"), Duration.seconds(1), List.of(
                new Pair<>(0, new FrameData(0, 0, 32, 42)),
                new Pair<>(1, new FrameData(32, 0, 32, 42)),
                new Pair<>(2, new FrameData(32+32, 0, 32, 42)),
                new Pair<>(3, new FrameData(32+32+32, 0, 32, 42))
        ));

        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(speed * tpf);

        if (speed != 0) {

            if (texture.getAnimationChannel() == animIdle) {
                texture.loopAnimationChannel(animWalk);
            }

            speed = (int) (speed * 0.9);

            if (FXGLMath.abs(speed) < 1) {
                speed = 0;
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    public void moveRight() {
        speed = 150;

        getEntity().setScaleX(1);
    }

    public void moveLeft() {
        speed = -150;

        getEntity().setScaleX(-1);
    }
}
