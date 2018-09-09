/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiledtest;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.spawnFadeIn;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrateComponent extends Component {

    // TODO: annotation
    @OnCollisionBegin("player")
    public void bump() {
        entity.getComponent(CollidableComponent.class).setValue(false);

        Entities.animationBuilder()
                .onFinished(entity::removeFromWorld)
                .duration(Duration.seconds(0.33))
                .interpolator(Interpolators.ELASTIC.EASE_IN())
                .scale(entity)
                .from(new Point2D(1, 1))
                .to(new Point2D(0, 0))
                .buildAndPlay();

        spawnFadeIn("coin", new SpawnData(entity.getX(), entity.getY()), Duration.seconds(1));
    }
}
