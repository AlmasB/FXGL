/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.spaceinvaders.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spaceinvaders.ExplosionEmitter;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import com.almasb.fxglgames.spaceinvaders.component.HPComponent;
import com.almasb.fxglgames.spaceinvaders.component.OwnerComponent;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class BulletEnemyHandler extends CollisionHandler {

    public BulletEnemyHandler() {
        super(SpaceInvadersType.BULLET, SpaceInvadersType.ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity enemy) {
        Object owner = bullet.getComponentUnsafe(OwnerComponent.class).getValue();

        // some enemy shot the bullet, skip collision handling
        if (owner == SpaceInvadersType.ENEMY) {
            return;
        }

        GameWorld world = (GameWorld) bullet.getWorld();

        Point2D hitPosition = bullet.getComponentUnsafe(PositionComponent.class).getValue();
        bullet.removeFromWorld();

        HPComponent hp = enemy.getComponentUnsafe(HPComponent.class);
        hp.setValue(hp.getValue() - 1);

        if (hp.getValue() <= 0) {

            FXGL.getMasterTimer().runOnceAfter(() -> {
                Entity entity = new Entity();
                entity.addComponent(new PositionComponent(Entities.getBBox(enemy).getCenterWorld()));
                entity.addControl(new ParticleControl(new ExplosionEmitter()));
                entity.addControl(new ExpireCleanControl(Duration.seconds(1)));
                world.addEntity(entity);

                world.spawn("Explosion", Entities.getBBox(enemy).getCenterWorld());

                enemy.removeFromWorld();
            }, Duration.seconds(0.1));

            FXGL.getAudioPlayer().playSound("explosion.wav");
            FXGL.getEventBus().fireEvent(new GameEvent(GameEvent.ENEMY_KILLED));
        } else {
            world.spawn("LaserHit", hitPosition);

            // make enemy look red
            enemy.getComponentUnsafe(ViewComponent.class).getView().setBlendMode(BlendMode.RED);

            // return enemy look to normal
            FXGL.getMasterTimer()
                    .runOnceAfter(() -> {
                        if (enemy.isActive())
                            enemy.getComponentUnsafe(ViewComponent.class).getView().setBlendMode(null);
                    }, Duration.seconds(0.33));
        }
    }
}
