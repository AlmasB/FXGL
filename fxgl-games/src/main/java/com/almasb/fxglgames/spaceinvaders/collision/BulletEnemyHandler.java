/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.GameWorld;
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
        Object owner = bullet.getComponent(OwnerComponent.class).getValue();

        // some enemy shot the bullet, skip collision handling
        if (owner == SpaceInvadersType.ENEMY) {
            return;
        }

        GameWorld world = (GameWorld) bullet.getWorld();

        Point2D hitPosition = bullet.getComponent(PositionComponent.class).getValue();
        bullet.removeFromWorld();

        HPComponent hp = enemy.getComponent(HPComponent.class);
        hp.setValue(hp.getValue() - 1);

        if (hp.getValue() <= 0) {

            //FXGL.getMasterTimer().runOnceAfter(() -> {
                Entity entity = new Entity();
                entity.addComponent(new PositionComponent(Entities.getBBox(enemy).getCenterWorld()));
                entity.addControl(new ParticleControl(new ExplosionEmitter()));
                entity.addControl(new ExpireCleanControl(Duration.seconds(1)));
                world.addEntity(entity);

                world.spawn("Explosion", Entities.getBBox(enemy).getCenterWorld());

                enemy.removeFromWorld();
            //}, Duration.seconds(0.1));

            FXGL.getAudioPlayer().playSound("explosion.wav");
            FXGL.getEventBus().fireEvent(new GameEvent(GameEvent.ENEMY_KILLED));
        } else {
            world.spawn("LaserHit", hitPosition);

            // make enemy look red
            enemy.getComponent(ViewComponent.class).getView().setBlendMode(BlendMode.RED);

            // return enemy look to normal
            FXGL.getMasterTimer()
                    .runOnceAfter(() -> {
                        if (enemy.isActive())
                            enemy.getComponent(ViewComponent.class).getView().setBlendMode(null);
                    }, Duration.seconds(0.33));
        }
    }
}
