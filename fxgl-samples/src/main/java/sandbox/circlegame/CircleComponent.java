/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.onKeyBuilder;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static sandbox.circlegame.CircleNNType.CIRCLE;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CircleComponent extends Component {

    // in seconds
    private double shootInterval = 0.15;
    private double time = 0.0;

    private HealthIntComponent hp;

    private Input input = new Input();

    @Override
    public void onAdded() {

        hp = entity.getComponent(HealthIntComponent.class);

        onKeyBuilder(input, KeyCode.W)
                .onAction(() -> entity.translateY(-5));

        onKeyBuilder(input, KeyCode.S)
                .onAction(() -> entity.translateY(5));

        onKeyBuilder(input, KeyCode.A)
                .onAction(() -> entity.translateX(-5));

        onKeyBuilder(input, KeyCode.D)
                .onAction(() -> entity.translateX(5));
    }

    public Input getInput() {
        return input;
    }

    @Override
    public void onUpdate(double tpf) {
        input.update(tpf);

        time += tpf;

        // if we can't shoot because of time interval or if we are player
        if (time < shootInterval || entity.hasComponent(PlayerComponent.class))
            return;

        if (shouldShoot()) {
            getGameWorld().getClosestEntity(entity, e -> e.isType(CIRCLE))
                    .ifPresent(closestCircle -> {
                        var dir = closestCircle.getCenter().subtract(entity.getCenter());

                        shoot(dir);
                    });
        }
    }

    public void shoot(Point2D dir) {
        if (time < shootInterval)
            return;

        spawn("bullet",
                new SpawnData(entity.getCenter().subtract(15, 0))
                        .put("owner", entity)
                        .put("dir", dir)
                        .put("damage", entity.getInt("rank"))
        );

        time = 0.0;
    }

    private boolean shouldShoot() {
        return FXGLMath.randomBoolean(0.1);
    }

    public void onKill() {
        entity.getProperties().increment("rank", +1);
    }

    public void takeHit(int damage) {
        if (isShielded())
            return;

        hp.damage(damage);

        if (hp.isZero()) {
            entity.removeFromWorld();
        }
    }

    public void applyPowerup(PowerupType type) {
        type.accept(entity);
    }

    public double getShootInterval() {
        return shootInterval;
    }

    public void setShootInterval(double shootInterval) {
        this.shootInterval = shootInterval;
    }

    public boolean isShielded() {
        return entity.getBoolean("isShielded");
    }

    public void setShielded(boolean isShielded) {
        entity.setProperty("isShielded", isShielded);
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
