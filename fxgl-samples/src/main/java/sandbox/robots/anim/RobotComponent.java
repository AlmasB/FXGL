/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots.anim;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.extra.entity.state.State;
import com.almasb.fxgl.extra.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotComponent extends StateComponent {

    private PositionComponent position;
    private ViewComponent view;
    private PhysicsComponent physics;

    private LocalTimer jumpTimer;

    private AnimatedTexture animatedTexture;

    private AnimationChannel animStand, animWalk, animRun, animJump, animFall, animDeath;

    private final State IDLE = new State() {

        @Override
        protected void onEnter(State prevState) {
            animatedTexture.loopAnimationChannel(animStand);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (Math.abs(physics.getVelocityX()) > 250) {
                setState(RUN);
            } else if (Math.abs(physics.getVelocityX()) > 0) {
                setState(WALK);
            }
        }
    };

    private final State WALK = new State() {

        @Override
        protected void onEnter(State prevState) {
            animatedTexture.loopAnimationChannel(animWalk);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (Math.abs(physics.getVelocityX()) == 0) {
                setState(IDLE);
            }

            if (Math.abs(physics.getVelocityX()) < 140)
                physics.setVelocityX(0);
        }
    };

    private final State RUN = new State() {

        @Override
        protected void onEnter(State prevState) {
            animatedTexture.loopAnimationChannel(animRun);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (Math.abs(physics.getVelocityX()) == 0) {
                setState(IDLE);
            }
        }
    };

    private final State JUMP = new State() {

        @Override
        protected void onEnter(State prevState) {
            physics.setVelocityY(-750);
            jumpTimer.capture();

            System.out.println("jumping");

            animatedTexture.playAnimationChannel(animJump);
        }

        @Override
        protected void onUpdate(double tpf) {

        }
    };

    private final State FALL = new State() {

        @Override
        protected void onEnter(State prevState) {
            System.out.println("fall");

            animatedTexture.loopAnimationChannel(animFall);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (physics.isOnGround()) {
                setState(IDLE);
            }
        }

        @Override
        protected void onExit() {
            // TODO: play land audio
        }
    };

//    public void d(double tpf) {
//        boolean onGround = physics.isOnGround();
//
//        if (onGround) {
//            if (Math.abs(physics.getVelocityX()) == 0) {
//                animatedTexture.loopAnimationChannel(animStand);
//            } else {
//
//                if (Math.abs(physics.getVelocityX()) > 250) {
//                    animatedTexture.loopAnimationChannel(animRun);
//                } else {
//                    animatedTexture.loopAnimationChannel(animWalk);
//                }
//            }
//        } else {
//            if ((physics.getVelocityY()) < 0) {
//                if (jumped) {
//                    animatedTexture.playAnimationChannel(animJump);
//                    jumped = false;
//                }
//            } else {
//                // fall
//                animatedTexture.loopAnimationChannel(animFall);
//            }
//        }
//
//        if (Math.abs(physics.getVelocityX()) < 140)
//            physics.setVelocityX(0);
//    }

    public RobotComponent() {
        animWalk = new AnimationChannel("robot_walk.png", 7, 275, 275, Duration.seconds(0.75), 0, 15);
        animRun = new AnimationChannel("robot_run.png", 7, 275, 275, Duration.seconds(0.5), 0, 15);

        animStand = new AnimationChannel("robot_stand.png", 7, 275, 275, Duration.seconds(1.1), 0, 29);

        animJump = new AnimationChannel("robot_jump.png", 7, 275, 275, Duration.seconds(0.35), 0, 25);
        animFall = new AnimationChannel("robot_jump.png", 7, 275, 275, Duration.seconds(0.1), 25, 25);

        animDeath = new AnimationChannel("robot_death.png", 7, 275, 275, Duration.seconds(1.1), 0, 26);

        animatedTexture = new AnimatedTexture(animStand);
        animatedTexture.start(FXGL.getApp().getStateMachine().getPlayState());

        animatedTexture.setOnCycleFinished(() -> {
            if (animatedTexture.getAnimationChannel() == animJump) {

                setState(FALL);
            }
        });

        jumpTimer = FXGL.newLocalTimer();
    }

    @Override
    public void onAdded() {
        if (entity.getProperties().exists("color")) {
            Color color = entity.getProperties().getValue("color");

            animStand = new AnimationChannel(texture("robot_stand.png").multiplyColor(color).getImage(), 7, 275, 275, Duration.seconds(0.6), 0, 29);
        }

        setState(IDLE);

        view.getView().addNode(animatedTexture);
    }

    @Override
    protected void preUpdate(double tpf) {
        if (physics.getVelocityY() > 0) {
            setState(FALL);
        }
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

            if (getState() == IDLE || getState() == WALK || getState() == RUN) {
                setState(JUMP);
            }
        }
    }

    public void crouch() {
        animatedTexture.playAnimationChannel(animDeath);
    }

    public void shoot() {
        spawn("rocket", entity.getCenter());
    }

    public void stop() {
        physics.setVelocityX(physics.getVelocityX() * 0.7);
    }
}
