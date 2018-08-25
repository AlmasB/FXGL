/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyControl extends Component {

    private PhysicsComponent physics;

    private LocalTimer jumpTimer;

    @Override
    public void onAdded() {
        jumpTimer = FXGL.newLocalTimer();
        jumpTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (jumpTimer.elapsed(Duration.seconds(3))) {
            jump();
            jumpTimer.capture();
        }
    }

    public void jump() {
        Animation<?> animation = Entities.animationBuilder()
                .duration(Duration.seconds(0.1))
                .interpolator(Interpolators.LINEAR.EASE_IN_OUT())
                .scale(getEntity())
                .from(new Point2D(1, 1))
                .to(new Point2D(2, 0.5))
                .buildAndPlay();



        animation.setOnFinished(() -> {
            physics.setVelocityY(-300);

            Animation<?> animation2 = Entities.animationBuilder()
                    .duration(Duration.seconds(0.2))
                    //.repeat(2)
                    //.autoReverse(true)
                    .interpolator(Interpolators.CIRCULAR.EASE_IN_OUT())
                    .scale(getEntity())
                    .from(new Point2D(2, 0.5))
                    .to(new Point2D(1, 1.0))
                    .buildAndPlay();
        });
    }
}
