/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package shooter;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
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
        bullet.getMainViewComponent().setView(new EntityView(new Rectangle(10, 2, Color.BLACK)), true);

        bullet.addComponent(new CollidableComponent(true));
        bullet.addControl(new OffscreenCleanControl());
        bullet.addControl(new ProjectileControl(direction, 10));

        BulletComponent bulletData = new BulletComponent();
        bulletData.setDamage(1);
        bulletData.setHp(1);
        bulletData.setSpeed(10);

        bullet.addComponent(bulletData);

        getEntity().getWorld().addEntity(bullet);
    }
}
