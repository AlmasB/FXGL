/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.RaycastResult;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use raycast.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RaycastSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    private Line laser;
    private double endY;

    @Override
    protected void initGame() {
        spawnWall(500, 100, 50, 50);
        spawnWall(550, 150, 50, 50);
        spawnWall(600, 200, 50, 50);
        spawnWall(600, 400, 50, 50);
        spawnWall(300, 450, 50, 50);
        spawnWall(500, 550, 50, 50);
        spawnWall(300, 300, 50, 50);

        laser = new Line();
        laser.setStroke(Color.RED);
        laser.setStrokeWidth(2);
        laser.setStartY(300);

        getGameScene().addGameView(new GameView(laser, 0));

        endY = -300;
    }

    @Override
    protected void onUpdate(double tpf) {
        laser.setEndX(getAppWidth());
        laser.setEndY(endY);

        // 1. raycast is essentially a ray from start point to end point in physics world
        RaycastResult result = getPhysicsWorld().raycast(new Point2D(0, 300), new Point2D(getAppWidth(), endY));

        // 2. if the ray hits something in physics world the point will the closest hit
        result.getPoint().ifPresent(p -> {
            laser.setEndX(p.getX());
            laser.setEndY(p.getY());
        });

        endY += 2;
        if (endY >= getAppHeight() + 300)
            endY = -300;
    }

    private void spawnWall(double x, double y, double w, double h) {
        entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(w, h))
                .with(new PhysicsComponent())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
