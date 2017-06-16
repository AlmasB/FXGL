/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.geowars.WeaponType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Control {

    private GameEntity player;
    private long spawnTime = System.currentTimeMillis();

    private LocalTimer weaponTimer = FXGL.newLocalTimer();

    @Override
    public void onAdded(Entity entity) {
        player = (GameEntity) entity;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
    }

    public void shoot(Point2D shootPoint) {
        WeaponType type = FXGL.getApp().getGameState().getObject("weaponType");

        if (weaponTimer.elapsed(type.delay)) {
            Point2D position = player.getCenter().subtract(14, 4.5);
            Point2D vectorToMouse = shootPoint.subtract(position);

            GameEntity bullet = spawnBullet(position, vectorToMouse);

            switch (type) {
                case RICOCHET:
                    bullet.removeControl(OffscreenCleanControl.class);
                    bullet.addControl(new RicochetControl());
                    break;

                case BEAM:
                    Point2D toMouse = vectorToMouse.normalize().multiply(10);
                    Point2D pos = position;

                    for (int i = 0; i < 7; i++) {
                        spawnBullet(pos.add(toMouse), toMouse);
                        pos = pos.add(toMouse);
                    }
                    break;

                case WAVE:
                    double baseAngle = new Vec2(vectorToMouse.getX(), vectorToMouse.getY()).angle() + 45;
                    for (int i = 0; i < 7; i++) {
                        Vec2 vec = Vec2.fromAngle(baseAngle);
                        spawnBullet(position, new Point2D(vec.x, vec.y));

                        baseAngle += 45;
                    }
                    break;

                case NORMAL:
                    default:
                    break;
            }

            weaponTimer.capture();
        }
    }

    private GameEntity spawnBullet(Point2D position, Point2D direction) {
        return (GameEntity) FXGL.getApp().getGameWorld().spawn("Bullet",
                new SpawnData(position.getX(), position.getY())
                        .put("direction", direction)
        );
    }

    public void left() {
        player.translateX(-5);
        makeExhaustFire();
    }

    public void right() {
        player.translateX(5);
        makeExhaustFire();
    }

    public void up() {
        player.translateY(-5);
        makeExhaustFire();
    }

    public void down() {
        player.translateY(5);
        makeExhaustFire();
    }

    private void makeExhaustFire() {
        Vec2 position = new Vec2(player.getCenter().getX(), player.getCenter().getY());
        double rotation = player.getRotation();

        Color midColor = Color.BLUE;
        Color sideColor = Color.MEDIUMVIOLETRED.brighter();

        Vec2 direction = Vec2.fromAngle(rotation);

        float t = (System.currentTimeMillis() - spawnTime) / 1000f;

        Vec2 baseVel = direction.mul(-45f);
        Vec2 perpVel = new Vec2(baseVel.y, -baseVel.x).mulLocal(2f * FXGLMath.sin(t * 10f));

        // subtract half extent x, y of Glow.png                                            mul half extent player
        Vec2 pos = position.sub(new Vec2(17.5, 10)).addLocal(direction.negate().normalizeLocal().mulLocal(20));

        // middle stream
        Vec2 randVec = Vec2.fromAngle(FXGLMath.radiansToDegrees * FXGLMath.random() * FXGLMath.PI2);
        Vec2 velMid = baseVel.add(randVec.mul(7.5f));

        Entities.builder()
                .at(pos.x, pos.y)
                .with(new ExhaustParticleControl(velMid, 800, midColor))
                .buildAndAttach(FXGL.getApp().getGameWorld());

        // side streams
        Vec2 randVec1 = Vec2.fromAngle(FXGLMath.radiansToDegrees * FXGLMath.random() * FXGLMath.PI2);
        Vec2 randVec2 = Vec2.fromAngle(FXGLMath.radiansToDegrees * FXGLMath.random() * FXGLMath.PI2);

        Vec2 velSide1 = baseVel.add(randVec1.mulLocal(2.4f)).addLocal(perpVel);
        Vec2 velSide2 = baseVel.add(randVec2.mulLocal(2.4f)).subLocal(perpVel);

        Entities.builder()
                .at(pos.x, pos.y)
                .with(new ExhaustParticleControl(velSide1, 800, sideColor))
                .buildAndAttach(FXGL.getApp().getGameWorld());

        Entities.builder()
                .at(pos.x, pos.y)
                .with(new ExhaustParticleControl(velSide2, 800, sideColor))
                .buildAndAttach(FXGL.getApp().getGameWorld());

//        Pools.free(direction);
//        Pools.free(position);
//        Pools.free(baseVel);
//        Pools.free(perpVel);
//        Pools.free(pos);
//        Pools.free(randVec);
//        Pools.free(velMid);
//        Pools.free(randVec1);
//        Pools.free(randVec2);
//        Pools.free(velSide1);
//        Pools.free(velSide2);
    }
}
