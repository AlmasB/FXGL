/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.platformer;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotComponent extends Component {

    private StateComponent state;
    private PhysicsComponent physics;
    private ViewComponent view;

    private AnimatedTexture animatedTexture;

    private AnimationChannel animStand,
            animWalk,
            animRun,
            animJump,
            animFall,
            animCrouch,
            animRoll,
            animDeath;

    public RobotComponent() {
        animWalk = new AnimationChannel(image("robot_walk.png"), 7, 275, 275, Duration.seconds(1), 0, 15);
        animRun = new AnimationChannel(image("robot_run.png"), 7, 275, 275, Duration.seconds(1), 0, 15);
        animStand = new AnimationChannel(image("robot_stand.png"), 7, 275, 275, Duration.seconds(1.5), 0, 29);
        animJump = new AnimationChannel(image("robot_jump.png"), 7, 275, 275, Duration.seconds(0.95), 0, 25);
        animFall = new AnimationChannel(image("robot_jump.png"), 7, 275, 275, Duration.seconds(0.95), 25, 25);
        animCrouch = new AnimationChannel(image("robot_crouch.png"), 7, 275, 275, Duration.seconds(1), 0, 20);
        animRoll = new AnimationChannel(image("robot_roll.png"), 7, 275, 275, Duration.seconds(1), 0, 23);

        animatedTexture = new AnimatedTexture(animStand);
        animatedTexture.loop();

        animatedTexture.setOnCycleFinished(() -> {
            if (animatedTexture.getAnimationChannel() == animJump) {
                state.changeState(FALL);
            } else if (animatedTexture.getAnimationChannel() == animRoll) {
                state.changeState(STAND);
            }
        });
    }

    private final EntityState STAND = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState prevState) {
            animatedTexture.loopAnimationChannel(animStand);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (Math.abs(physics.getVelocityX()) > 200) {
                state.changeState(RUN);
            } else if (Math.abs(physics.getVelocityX()) > 150) {
                state.changeState(WALK);
            } else if (Math.abs(physics.getVelocityX()) > 60) {
                state.changeState(CROUCH);
            }
        }

        @Override
        public String toString() {
            return "STAND";
        }
    };

    private final EntityState WALK = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState prevState) {
            animatedTexture.loopAnimationChannel(animWalk);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (Math.abs(physics.getVelocityX()) == 0) {
                state.changeState(STAND);
            } else if (Math.abs(physics.getVelocityX()) > 200) {
                state.changeState(RUN);
            } else if (Math.abs(physics.getVelocityX()) > 60 && Math.abs(physics.getVelocityX()) < 150) {
                state.changeState(CROUCH);
            }
        }

        @Override
        public String toString() {
            return "WALK";
        }
    };

    private final EntityState RUN = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState prevState) {
            animatedTexture.loopAnimationChannel(animRun);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (Math.abs(physics.getVelocityX()) == 0) {
                state.changeState(STAND);
            } else if (Math.abs(physics.getVelocityX()) > 150 && Math.abs(physics.getVelocityX()) < 200) {
                state.changeState(WALK);
            } else if (Math.abs(physics.getVelocityX()) > 60 && Math.abs(physics.getVelocityX()) < 150) {
                state.changeState(CROUCH);
            }
        }

        @Override
        public String toString() {
            return "RUN";
        }
    };

    private final EntityState JUMP = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState prevState) {
            animatedTexture.playAnimationChannel(animJump);
        }

        @Override
        protected void onUpdate(double tpf) {

        }

        @Override
        public String toString() {
            return "JUMP";
        }
    };

    private final EntityState FALL = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState prevState) {
            animatedTexture.loopAnimationChannel(animFall);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (physics.isOnGround()) {
                physics.setVelocityX(0);
                state.changeState(STAND);
            }
        }

        @Override
        public String toString() {
            return "FALL";
        }
    };

    private final EntityState CROUCH = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState prevState) {
            animatedTexture.loopAnimationChannel(animCrouch);
        }

        @Override
        protected void onUpdate(double tpf) {
            if (Math.abs(physics.getVelocityX()) == 0) {
                state.changeState(STAND);
            } else if (Math.abs(physics.getVelocityX()) > 150 && Math.abs(physics.getVelocityX()) < 200) {
                state.changeState(WALK);
            } else if (Math.abs(physics.getVelocityX()) > 200) {
                state.changeState(RUN);
            }
        }

        @Override
        public String toString() {
            return "CROUCH";
        }
    };

    private final EntityState ROLL = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState prevState) {
            animatedTexture.playAnimationChannel(animRoll);
        }

        @Override
        protected void onUpdate(double tpf) {

        }

        @Override
        public void onExitingTo(EntityState entityState) {
            physics.setVelocityX(0);
        }

        @Override
        public String toString() {
            return "ROLL";
        }
    };

    private final EntityState DEATH = new EntityState() {

        @Override
        protected void onUpdate(double tpf) {

        }

        @Override
        public String toString() {
            return "DEATH";
        }
    };

    @Override
    public void onAdded() {
        view.addChild(animatedTexture);

        state.changeState(STAND);

        state.currentStateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("new state: " + newValue);
        });

        // TODO: why does this return 228 if all hitboxes add up to 260?
        System.out.println(entity.getHeight());
    }

    public void walkLeft() {
        if (state.isIn(ROLL, JUMP))
            return;

        getEntity().setScaleX(-FXGLMath.abs(getEntity().getScaleX()));
        physics.setVelocityX(-170);
    }

    public void walkRight() {
        if (state.isIn(ROLL, JUMP))
            return;

        getEntity().setScaleX(FXGLMath.abs(getEntity().getScaleX()));
        physics.setVelocityX(170);
    }

    public void runLeft() {
        if (state.isIn(ROLL, JUMP))
            return;

        getEntity().setScaleX(-FXGLMath.abs(getEntity().getScaleX()));
        physics.setVelocityX(-340);
    }

    public void runRight() {
        if (state.isIn(ROLL, JUMP))
            return;

        getEntity().setScaleX(FXGLMath.abs(getEntity().getScaleX()));
        physics.setVelocityX(340);
    }

    public void crouchLeft() {
        if (state.isIn(ROLL, JUMP))
            return;

        getEntity().setScaleX(-FXGLMath.abs(getEntity().getScaleX()));
        physics.setVelocityX(-70);
    }

    public void crouchRight() {
        if (state.isIn(ROLL, JUMP))
            return;

        getEntity().setScaleX(FXGLMath.abs(getEntity().getScaleX()));
        physics.setVelocityX(70);
    }

    public void stop() {
        if (state.isIn(WALK, RUN, CROUCH)) {
            physics.setVelocityX(0);
        }
    }

    public void jump() {
        if (state.isIn(ROLL, JUMP))
            return;

        physics.setVelocityY(-300);

        state.changeState(JUMP);
    }

    public void roll() {
        if (state.isIn(ROLL, JUMP))
            return;

        physics.setVelocityX(getEntity().getScaleX() * 400);
        state.changeState(ROLL);
    }
}
