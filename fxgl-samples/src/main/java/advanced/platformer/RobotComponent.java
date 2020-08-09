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

import java.util.Map;

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

    private final EntityState STAND = new EntityState("STAND");
    private final EntityState WALK = new EntityState("WALK");
    private final EntityState RUN = new EntityState("RUN");
    private final EntityState CROUCH = new EntityState("CROUCH");

    private final EntityState JUMP = new EntityState("JUMP") {
        @Override
        protected void onUpdate(double tpf) {
            if (physics.getVelocityY() > 0) {
                state.changeState(FALL);
            }
        }
    };

    private final EntityState FALL = new EntityState("FALL") {

        @Override
        protected void onUpdate(double tpf) {
            if (physics.isOnGround()) {
                physics.setVelocityX(0);
                state.changeState(STAND);
            }
        }
    };

    private final EntityState ROLL = new EntityState("ROLL") {

        @Override
        public void onExitingTo(EntityState entityState) {
            physics.setVelocityX(0);
        }
    };

    private final EntityState DEATH = new EntityState("DEATH");

    private static class StateData {
        private AnimationChannel channel;
        private int moveSpeed;

        public StateData(AnimationChannel channel, int moveSpeed) {
            this.channel = channel;
            this.moveSpeed = moveSpeed;
        }
    }

    private Map<EntityState, StateData> stateData;

    public RobotComponent() {
        animWalk = new AnimationChannel(image("robot_walk.png"), 7, 275, 275, Duration.seconds(0.8), 0, 15);
        animRun = new AnimationChannel(image("robot_run.png"), 7, 275, 275, Duration.seconds(0.8), 0, 15);
        animStand = new AnimationChannel(image("robot_stand.png"), 7, 275, 275, Duration.seconds(1.5), 0, 29);
        animJump = new AnimationChannel(image("robot_jump.png"), 7, 275, 275, Duration.seconds(0.4), 0, 25);
        animFall = new AnimationChannel(image("robot_jump.png"), 7, 275, 275, Duration.seconds(0.95), 25, 25);
        animCrouch = new AnimationChannel(image("robot_crouch.png"), 7, 275, 275, Duration.seconds(0.8), 0, 20);
        animRoll = new AnimationChannel(image("robot_roll.png"), 7, 275, 275, Duration.seconds(0.9), 0, 23);
        animDeath = new AnimationChannel(image("robot_death.png"), 7, 275, 275, Duration.seconds(1.1), 0, 26);

        stateData = Map.of(
                STAND, new StateData(animStand, 0),
                WALK, new StateData(animWalk, 170),
                RUN, new StateData(animRun, 340),
                JUMP, new StateData(animJump, 580),
                FALL, new StateData(animFall, 0),
                CROUCH, new StateData(animCrouch, 70),
                ROLL, new StateData(animRoll, 400),
                DEATH, new StateData(animDeath, 0)
        );

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

    @Override
    public void onAdded() {

        state = entity.getComponent(StateComponent.class);
        physics = entity.getComponent(PhysicsComponent.class);
        view = entity.getComponent(ViewComponent.class);

        view.addChild(animatedTexture);

        state.changeState(STAND);

        state.currentStateProperty().addListener((o, oldState, newState) -> {
            System.out.println("new state: " + newState);

            var data = stateData.get(newState);

            animatedTexture.loopAnimationChannel(data.channel);
        });
    }

    public void walkLeft() {
        tryMovingState(WALK, -1);
    }

    public void walkRight() {
        tryMovingState(WALK, +1);
    }

    public void runLeft() {
        tryMovingState(RUN, -1);
    }

    public void runRight() {
        tryMovingState(RUN, +1);
    }

    public void crouchLeft() {
        tryMovingState(CROUCH, -1);
    }

    public void crouchRight() {
        tryMovingState(CROUCH, +1);
    }

    private void tryMovingState(EntityState newState, int scale) {
        if (state.isIn(STAND, WALK, RUN, CROUCH)) {
            getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));
            physics.setVelocityX(scale * stateData.get(newState).moveSpeed);

            if (state.getCurrentState() != newState) {
                state.changeState(newState);
            }
        }
    }

    public void stop() {
        if (state.isIn(WALK, RUN, CROUCH)) {
            physics.setVelocityX(0);
            state.changeState(STAND);
        }
    }

    public void jump() {
        if (state.isIn(ROLL, JUMP))
            return;

        physics.setVelocityY(-580);
        state.changeState(JUMP);
    }

    public void roll() {
        if (state.isIn(ROLL, JUMP))
            return;

        physics.setVelocityX(getEntity().getScaleX() * 400);
        state.changeState(ROLL);
    }

    public void die() {
        state.changeState(DEATH);
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
