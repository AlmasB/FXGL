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

package manual;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BBoxTest extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BBoxTest");
        settings.setMenuEnabled(false);
        settings.setIntroEnabled(false);
        settings.setFullScreen(false);
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initAssets() {

    }

    @Override
    protected void initGame() {
        GameEntity e1 = new GameEntity();
        e1.setPosition(new Point2D(100, 100));
        e1.getBoundingBoxComponent().addHitBox(new HitBox("HEAD", new Point2D(50, 0), BoundingShape.box(50, 80)));
        e1.getBoundingBoxComponent().addHitBox(new HitBox("ARM", BoundingShape.box(30, 30)));
        e1.setView(new Rectangle(100, 100));

        GameEntity e2 = new GameEntity();
        e2.getPositionComponent().setValue(50, 50);
        e2.getBoundingBoxComponent().addHitBox(new HitBox("ARM", BoundingShape.circle(20)));
        e2.getViewComponent().setView(new Rectangle(40, 60, Color.YELLOWGREEN));

        getGameWorld().addEntities(e1, e2);
    }

    @Override
    protected void initPhysics() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {

    }
}
