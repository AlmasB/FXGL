/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Component {

    private PhysicsComponent physics;

    private AnimatedTexture texture;

    private AnimationChannel animIdle, animWalk;

    public PlayerControl() {

        animIdle = new AnimationChannel("newdude.png", 4, 32, 42, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel("newdude.png", 4, 32, 42, Duration.seconds(1), 0, 3);

        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        entity.setView(texture);
    }

    @Override
    public void onUpdate(double tpf) {

        texture.setAnimationChannel(isMoving() ? animWalk : animIdle);
    }

    private boolean isMoving() {
        return FXGLMath.abs(physics.getVelocityX()) > 0;
    }

    public void left() {
        getEntity().setScaleX(-1);
        physics.setVelocityX(-150);
    }

    public void right() {
        getEntity().setScaleX(1);
        physics.setVelocityX(150);
    }

    public void jump() {
        physics.setVelocityY(-400);
    }
}
