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

package s10realphysics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.RaycastResult;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use raycast.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RaycastSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("RaycastSample");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    private Line laser = new Line();

    @Override
    protected void initGame() {
        spawnWall(500, 100, 50, 50);
        spawnWall(550, 150, 50, 50);
        spawnWall(600, 200, 50, 50);
        spawnWall(600, 400, 50, 50);
        spawnWall(300, 450, 50, 50);
        spawnWall(500, 550, 50, 50);
        spawnWall(300, 300, 50, 50);

        laser.setStroke(Color.RED);
        laser.setStrokeWidth(2);
        laser.setStartY(300);

        getGameScene().addGameView(new EntityView(laser));
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    private double endY = -300;

    @Override
    protected void onUpdate(double tpf) {
        laser.setEndX(getWidth());
        laser.setEndY(endY);

        // 1. raycast is essentially a ray from start point to end point in physics world
        RaycastResult result = getPhysicsWorld().raycast(new Point2D(0, 300), new Point2D(getWidth(), endY));

        // 2. if the ray hits something in physics world the point will the closest hit
        result.getPoint().ifPresent(p -> {
            laser.setEndX(p.getX());
            laser.setEndY(p.getY());
        });

        endY++;
        if (endY >= getHeight() + 300)
            endY = -300;
    }

    private void spawnWall(double x, double y, double w, double h) {
        Entities.builder()
                .at(x, y)
                .viewFromNodeWithBBox(new Rectangle(w, h))
                .with(new PhysicsComponent())
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
