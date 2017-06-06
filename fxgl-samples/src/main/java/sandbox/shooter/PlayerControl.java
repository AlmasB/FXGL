/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.shooter;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {
    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void shoot(Point2D direction) {
        GameEntity bullet = new GameEntity();
        bullet.getTypeComponent().setValue(FXShooterApp.EntityType.BULLET);
        bullet.getPositionComponent().setValue(getEntity().getComponentUnsafe(PositionComponent.class).getValue().add(20, 20));
        bullet.getViewComponent().setView(new EntityView(new Rectangle(10, 2, Color.BLACK)), true);

        bullet.addComponent(new CollidableComponent(true));
        bullet.addControl(new OffscreenCleanControl());
        bullet.addControl(new ProjectileControl(direction, 10 * 60));

        BulletComponent bulletData = new BulletComponent();
        bulletData.setDamage(1);
        bulletData.setHp(1);
        bulletData.setSpeed(10);

        bullet.addComponent(bulletData);

        getEntity().getWorld().addEntity(bullet);
    }
}
