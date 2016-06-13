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

package manual;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.MainViewComponent;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.BoundingBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BBoxTest extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BBoxTest");
        //settings.setWidth(1920);
        //settings.setHeight(1080);
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
        MainViewComponent.turnOnDebugBBox(Color.RED);

        GameEntity e1 = new GameEntity();
        //e1.getBoundingBoxComponent().addHitBox(new HitBox("ARM", new BoundingBox(50, 50, 40, 60)));
        //e1.getMainViewComponent().setGraphics(new Rectangle(100, 100));

        GameEntity e2 = new GameEntity();
        e2.getPositionComponent().setValue(50, 50);
        //e2.getBoundingBoxComponent().addHitBox(new HitBox("ARM", new BoundingBox(50, 50, 40, 60)));
        e2.getMainViewComponent().setView(new Rectangle(40, 60, Color.YELLOWGREEN));

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
