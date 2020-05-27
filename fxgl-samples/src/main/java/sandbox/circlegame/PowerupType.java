/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame;

import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.effects.SlowTimeEffect;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;
import sandbox.circlegame.effects.RageEffect;
import sandbox.circlegame.effects.ShieldEffect;

import java.util.function.Consumer;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static javafx.util.Duration.seconds;
import static sandbox.circlegame.CircleNNType.CIRCLE;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum PowerupType implements Consumer<Entity> {
    // hourglass - https://opengameart.org/content/hourglass-icon
    // https://kenney.nl/assets/game-icons

    SLOW_TIME("hourglass.png", seconds(3)) {
        @Override
        public void accept(Entity entity) {
            byType(CIRCLE).forEach(circle -> {
                if (circle != entity) {
                    circle.getComponent(EffectComponent.class).startEffect(new SlowTimeEffect(0.05, duration));
                }
            });
        }
    },

    SHIELD("hourglass.png", seconds(3)) {
        @Override
        public void accept(Entity entity) {
            entity.getComponent(EffectComponent.class).startEffect(new ShieldEffect(duration));
        }
    },

    SPPED("hourglass.png", seconds(3)) {
        @Override
        public void accept(Entity entity) {
            //entity.getComponent(EffectComponent.class).startEffect(new ShieldEffect(duration));
        }
    },

    RAGE("warning.png", seconds(2)) {
        @Override
        public void accept(Entity entity) {
            entity.getComponent(EffectComponent.class).startEffect(new RageEffect(duration));
        }
    };

    final String textureName;
    final Duration duration;

    PowerupType(String textureName, Duration duration) {
        this.textureName = textureName;
        this.duration = duration;
    }
}
