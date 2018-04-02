/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots.anim;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;
import sandbox.robots.MarioType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotComponent extends Component {

    private PositionComponent position;
    private ViewComponent view;
    private PhysicsComponent physics;

    private LocalTimer jumpTimer;
    private boolean jumped = false;

    private AnimatedTexture animatedTexture;

    private AnimationChannel animStand, animWalk, animRun, animJump, animFall, animDeath;

    public RobotComponent() {
        animWalk = new AnimationChannel("robot_walk.png", 7, 275, 275, Duration.seconds(1), 0, 15);
        animRun = new AnimationChannel("robot_run.png", 7, 275, 275, Duration.seconds(0.5), 0, 15);
        animStand = new AnimationChannel("robot_stand.png", 7, 275, 275, Duration.seconds(0.6), 0, 29);

        animJump = new AnimationChannel("robot_jump.png", 7, 275, 275, Duration.seconds(0.25), 0, 25);
        animFall = new AnimationChannel("robot_jump.png", 7, 275, 275, Duration.seconds(0.1), 25, 25);

        animDeath = new AnimationChannel("robot_death.png", 7, 275, 275, Duration.seconds(1.1), 0, 26);

        //animStand = animJump;

        animatedTexture = new AnimatedTexture(animStand);
        animatedTexture.start(FXGL.getApp().getStateMachine().getPlayState());

        jumpTimer = FXGL.newLocalTimer();
    }

    @Override
    public void onAdded() {
        view.getView().addNode(animatedTexture);
    }

    @Override
    public void onUpdate(double tpf) {
        boolean onGround = physics.isOnGround();

        if (onGround) {
            if (Math.abs(physics.getVelocityX()) == 0) {
                animatedTexture.setAnimationChannel(animStand);

                //System.out.println("stand");
            } else {

                if (Math.abs(physics.getVelocityX()) > 250) {
                    animatedTexture.setAnimationChannel(animRun);
                } else {
                    animatedTexture.setAnimationChannel(animWalk);
                }
            }
        } else {
            if ((physics.getVelocityY()) < 0) {
                if (jumped) {
                    animatedTexture.playAnimationChannel(animJump);
                    jumped = false;

                    //System.out.println("jump");
                }
            } else {
                // fall
                animatedTexture.setAnimationChannel(animFall);

                //System.out.println("fall");
            }
        }

        if (Math.abs(physics.getVelocityX()) < 140)
            physics.setVelocityX(0);
    }

    public void left() {
        view.getView().setScaleX(-1);
        physics.setVelocityX(-150);
    }

    public void right() {
        view.getView().setScaleX(1);
        physics.setVelocityX(150);
    }

    public void runLeft() {
        view.getView().setScaleX(-1);
        physics.setVelocityX(-350);
    }

    public void runRight() {
        view.getView().setScaleX(1);
        physics.setVelocityX(350);
    }

    public void jump() {
        if (jumpTimer.elapsed(Duration.seconds(0.25))) {
            if (physics.isOnGround()) {
                jumped = true;
                physics.setVelocityY(-750);
                jumpTimer.capture();
            }
        }
    }

    public void crouch() {
        animatedTexture.playAnimationChannel(animDeath);
    }

    public void stop() {
        physics.setVelocityX(physics.getVelocityX() * 0.7);
    }
}
