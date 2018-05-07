/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiledtest;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioComponent extends Component {

    private PhysicsComponent physics;

    private AnimatedTexture texture;

    private AnimationChannel animIdle, animWalk;

    public MarioComponent() {

        animIdle = new AnimationChannel("newdude.png", 4, 32, 42, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel("newdude.png", 4, 32, 42, Duration.seconds(1), 0, 3);

        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        //texture.setScaleX(0.25);
        //texture.setScaleY(0.25);
        entity.setView(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (isMoving()) {

            if (texture.getAnimationChannel() == animIdle) {
                texture.loopAnimationChannel(animWalk);
            }

            physics.setVelocityX(physics.getVelocityX() * 0.9);

            if (!isMoving()) {
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    private boolean isMoving() {
        return FXGLMath.abs(physics.getVelocityX()) > 0;
    }

    public void left() {
        getEntity().setScaleX(-1);
        physics.setVelocityX(-350);
    }

    public void right() {
        getEntity().setScaleX(1);
        physics.setVelocityX(350);
    }

    public void jump() {
        physics.setVelocityY(-750);
    }
}
