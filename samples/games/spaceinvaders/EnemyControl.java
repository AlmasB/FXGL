/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

package games.spaceinvaders;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.control.AbstractControl;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {
    private LocalTimer hTimer;
    private LocalTimer vTimer;
    private LocalTimer attackTimer;

    private boolean movingRight = true;

    @Override
    protected void initEntity(Entity entity) {
        hTimer = GameApplication.getService(ServiceType.LOCAL_TIMER);
        vTimer = GameApplication.getService(ServiceType.LOCAL_TIMER);
        attackTimer = GameApplication.getService(ServiceType.LOCAL_TIMER);
    }

    @Override
    public void onUpdate(Entity entity) {
        if (hTimer.elapsed(Duration.seconds(2))) {
            movingRight = !movingRight;
            hTimer.capture();
        }

        if (vTimer.elapsed(Duration.seconds(4))) {
            entity.translate(0, 20);
            vTimer.capture();
        }

        if (attackTimer.elapsed(Duration.seconds(2))) {
            if (Math.random() < 0.3) {
                shoot();
            }
            attackTimer.capture();
        }

        entity.translate(movingRight ? 1 : -1, 0);
    }

    private void shoot() {
        Entity bullet = new Entity(SpaceInvadersApp.Type.ENEMY_BULLET);
        bullet.setPosition(entity.getCenter().add(0, entity.getHeight() / 2));
        bullet.setCollidable(true);
        bullet.setSceneView(GameApplication.getService(ServiceType.ASSET_LOADER).loadTexture("tank_bullet.png"));
        bullet.addControl(new BulletControl());

        entity.getWorld().addEntity(bullet);
    }
}
