/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.GameWorld;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.spaceinvaders.Config;
import com.almasb.fxglgames.spaceinvaders.component.InvincibleComponent;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
@Required(BoundingBoxComponent.class)
@Required(InvincibleComponent.class)
public class PlayerControl extends Control {

    private PositionComponent position;
    private BoundingBoxComponent bbox;
    private InvincibleComponent invicibility;

    private double dx = 0;
    private double attackSpeed = Config.PLAYER_ATTACK_SPEED;

    private boolean canShoot = true;
    private double lastTimeShot = 0;

    @Override
    public void onAdded(Entity entity) {
        position = entity.getComponent(PositionComponent.class);
        bbox = entity.getComponent(BoundingBoxComponent.class);
        invicibility = entity.getComponent(InvincibleComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        dx = Config.PLAYER_MOVE_SPEED * tpf;

        if (!canShoot) {
            if ((FXGL.getMasterTimer().getNow() - lastTimeShot) >= 1.0 / attackSpeed) {
                canShoot = true;
            }
        }
    }

    public void left() {
        if (position.getX() - dx >= 0)
            position.translateX(-dx);

        spawnParticles();
    }

    public void right() {
        if (position.getX() + bbox.getWidth() + dx <= Config.WIDTH)
            position.translateX(dx);

        spawnParticles();
    }

    public void shoot() {
        if (!canShoot)
            return;

        canShoot = false;
        lastTimeShot = FXGL.getMasterTimer().getNow();

        GameWorld world = (GameWorld) getEntity().getWorld();
        world.spawn("Laser", new SpawnData(0, 0).put("owner", getEntity()));

        FXGL.getAudioPlayer()
                .playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    public void enableInvincibility() {
        invicibility.setValue(true);
    }

    public void disableInvincibility() {
        invicibility.setValue(false);
    }

    public boolean isInvincible() {
        return invicibility.getValue();
    }

    public void increaseAttackSpeed(double value) {
        attackSpeed += value;
    }

    private Image particle;

    private void spawnParticles() {
        if (particle == null) {
            particle = FXGL.getAssetLoader()
                    .loadTexture("player2.png", 40, 30)
                    .getImage();
        }

        Texture t = new Texture(particle);

        Entities.builder()
                .at(bbox.getCenterWorld().subtract(particle.getWidth() / 2, particle.getHeight() / 2))
                .viewFromNode(new EntityView(t, new RenderLayer() {
                    @Override
                    public String name() {
                        return "PARTICLES";
                    }

                    @Override
                    public int index() {
                        return 5000;
                    }
                }))
                .with(new ExpireCleanControl(Duration.seconds(0.33)), new OpacityControl())
                .buildAndAttach(FXGL.getApp().getGameWorld());
    }

    private static class OpacityControl extends Control {

        private ViewComponent view;
        private double millis = 330;
        private double current = 330;

        @Override
        public void onAdded(Entity entity) {
            view = Entities.getView(entity);
        }

        @Override
        public void onUpdate(Entity entity, double tpf) {
            current -= 600 * tpf;

            if (current < 0)
                current = 0;

            view.getView().setOpacity(current / millis);
        }
    }
}
