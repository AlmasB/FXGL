/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Component {

    private PositionComponent position;
    private ViewComponent view;
    private PhysicsComponent physics;

    private LocalTimer jumpTimer;

    private AnimatedTexture animatedTexture;

    private AnimationChannel animStand, animWalk, animJump;

    public PlayerControl() {
        Texture staticTexture = FXGL.getAssetLoader()
                .loadTexture("dude.png")
                .subTexture(new Rectangle2D(0, 0, 32, 42));

        animatedTexture = FXGL.getAssetLoader()
                .loadTexture("dude.png")
                .subTexture(new Rectangle2D(32, 0, 32*3, 42))
                .superTexture(staticTexture, HorizontalDirection.RIGHT)
                .toAnimatedTexture(4, Duration.seconds(0.5));

        animWalk = animatedTexture.getAnimationChannel();
        animStand = new AnimationChannel(animatedTexture.getImage(), 4, 32, 42, Duration.seconds(1), 1, 1);
        animJump = new AnimationChannel(animatedTexture.getImage(), 4, 32, 42, Duration.seconds(0.75), 1, 1);;

        animatedTexture.loopAnimationChannel(animStand);
        animatedTexture.start(FXGL.getApp().getStateMachine().getPlayState());

        jumpTimer = FXGL.newLocalTimer();
    }

    @Override
    public void onAdded() {
        view.getView().addNode(animatedTexture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (Math.abs(physics.getVelocityX()) == 0) {
            animatedTexture.loopAnimationChannel(animStand);
        } else {
            animatedTexture.loopAnimationChannel(animWalk);
        }

        if (Math.abs(physics.getVelocityX()) < 140)
            physics.setVelocityX(0);

        if (Math.abs(physics.getVelocityY()) != 0) {
            animatedTexture.loopAnimationChannel(animJump);
        }
    }

    boolean canJump = true;

    public void left() {
        view.getView().setScaleX(-1);
        physics.setVelocityX(-150);
    }

    public void right() {
        view.getView().setScaleX(1);
        physics.setVelocityX(150);
    }

    public void jump() {
        if (jumpTimer.elapsed(Duration.seconds(0.25))) {
            if (canJump) {
                physics.setVelocityY(-250);
                canJump = false;
                jumpTimer.capture();
            }
        }
    }

    public void stop() {
        physics.setVelocityX(physics.getVelocityX() * 0.7);
    }

    public void shoot(Point2D endPoint) {
        double x = position.getX();
        double y = position.getY();

        Point2D velocity = endPoint
                .subtract(x, y)
                .normalize()
                .multiply(500);

        getEntity().getWorld().spawn("Arrow",
                new SpawnData(x, y)
                        .put("velocity", velocity)
                        .put("shooter", getEntity()));
    }
}
