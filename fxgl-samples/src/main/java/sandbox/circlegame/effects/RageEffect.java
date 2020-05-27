/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame.effects;

import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;
import sandbox.circlegame.CircleComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RageEffect extends Effect {

    public RageEffect(Duration duration) {
        super(duration);
    }

    @Override
    public void onStart(Entity entity) {
        entity.getComponent(CircleComponent.class).setShootInterval(0.01);
    }

    @Override
    public void onEnd(Entity entity) {
        entity.getComponent(CircleComponent.class).setShootInterval(0.15);
    }
}
