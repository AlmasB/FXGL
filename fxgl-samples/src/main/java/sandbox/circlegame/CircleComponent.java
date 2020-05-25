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
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CircleComponent extends Component {

    private static final Duration SHOOT_INTERVAL = Duration.seconds(0.15);
    private LocalTimer shootTimer = newLocalTimer();

    private HealthIntComponent hp;

    @Override
    public void onUpdate(double tpf) {
        if (shootTimer.elapsed(SHOOT_INTERVAL) && shouldShoot()) {
            getGameWorld().getClosestEntity(entity, e -> e.isType(CircleNNType.CIRCLE))
                    .ifPresent(closestCircle -> {
                        var dir = closestCircle.getCenter().subtract(entity.getCenter());

                        shoot(dir);
                    });
        }
    }

    public void shoot(Point2D dir) {
        if (!shootTimer.elapsed(SHOOT_INTERVAL))
            return;

        spawn("bullet",
                new SpawnData(entity.getCenter().subtract(10, 0))
                        .put("owner", entity)
                        .put("dir", dir)
        );

        shootTimer.capture();
    }

    private boolean shouldShoot() {
        return FXGLMath.randomBoolean(0.1);
    }

    public void takeHit() {
        hp.damage(1);

        if (hp.isZero()) {
            entity.removeFromWorld();
        }
    }
}
