/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotControl extends Component {

    private PositionComponent position;
    private ViewComponent view;
    private PhysicsComponent physics;

    private LocalTimer jumpTimer;

    private AnimatedTexture animatedTexture;

    private AnimationChannel animStand, animWalk, animJump, animPassive;

    public RobotControl() {
        animWalk = new AnimationChannel("robot.png", 7, 275, 275, Duration.seconds(1), 0, 15);
        animStand = new AnimationChannel("robot_stand.png", 7, 275, 275, Duration.seconds(0.6), 0, 29);

        animJump = animStand;
        animPassive = new AnimationChannel("robot_stand.png", 7, 275, 275, Duration.seconds(1.5), 0, 0);

        animatedTexture = new AnimatedTexture(animPassive);
        animatedTexture.start(FXGL.getApp().getStateMachine().getPlayState());

        jumpTimer = FXGL.newLocalTimer();
    }

    @Override
    public void onAdded() {
        view.getView().addNode(animatedTexture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity.distance(entity.getWorld().getSingleton(MarioType.PLAYER).get()) < 300) {
            animatedTexture.setAnimationChannel(animStand);
        } else {
            animatedTexture.setAnimationChannel(animPassive);
        }
    }
}
