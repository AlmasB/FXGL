/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.GameWorld;
import com.almasb.fxglgames.geowars.component.GraphicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Grid {

    private static final double POINT_MASS_DAMPING = 0.8;
    private static final double SPRING_STIFFNESS = 0.28;
    private static final double SPRING_DAMPING = 0.06;

    private List<Spring> springs = new ArrayList<>();
    private PointMass[][] points;

    public Grid(Rectangle size, Point2D spacing, GameWorld world, GraphicsContext g) {
        int numColumns = (int) (size.getWidth() / spacing.getX()) + 2;
        int numRows = (int) (size.getHeight() / spacing.getY()) + 2;
        points = new PointMass[numColumns][numRows];

        PointMass[][] fixedPoints = new PointMass[numColumns][numRows];

        // create the point masses
        float xCoord = 0, yCoord = 0;
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                points[column][row] = new PointMass(new Vec2(xCoord, yCoord), POINT_MASS_DAMPING, 1);
                fixedPoints[column][row] = new PointMass(new Vec2(xCoord, yCoord), POINT_MASS_DAMPING, 0);
                xCoord += spacing.getX();
            }
            yCoord += spacing.getY();
            xCoord = 0;
        }

        Entity gridEntity = new Entity();
        gridEntity.addComponent(new GraphicsComponent(g));
        gridEntity.addControl(new GridControl());

        // link the point masses with springs
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                if (x == 0 || y == 0 || x == numColumns - 1 || y == numRows - 1) {
                    springs.add(new Spring(fixedPoints[x][y], points[x][y], 0.5, 0.1, false, null));
                } else if (x % 3 == 0 && y % 3 == 0) {
                    springs.add(new Spring(fixedPoints[x][y], points[x][y], 0.005, 0.02, false, null));
                }

                if (x > 0) {
                    springs.add(new Spring(points[x - 1][y], points[x][y], SPRING_STIFFNESS, SPRING_DAMPING, true, gridEntity));
                }

                if (y > 0) {
                    springs.add(new Spring(points[x][y - 1], points[x][y], SPRING_STIFFNESS, SPRING_DAMPING, true, gridEntity));
                }

                // add additional lines
                if (x > 0 && y > 0) {
                    gridEntity.getControl(GridControl.class).addControl(new AdditionalLineControl(
                            points[x - 1][y], points[x][y],
                            points[x - 1][y - 1], points[x][y - 1]));

                    gridEntity.getControl(GridControl.class).addControl(new AdditionalLineControl(
                            points[x][y - 1], points[x][y],
                            points[x - 1][y - 1], points[x - 1][y]));
                }
            }
        }

        world.addEntity(gridEntity);
    }

    public void update() {
        springs.forEach(Spring::update);

        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                points[x][y].update();
            }
        }
    }

//    public void applyDirectedForce(Point2D force, Point2D position, float radius) {
//        for (int x = 0; x < points.length; x++) {
//            for (int y = 0; y < points[0].length; y++) {
//                if (position.distance(points[x][y].getPosition()) * position.distance(points[x][y].getPosition())
//                        < radius * radius) {
//                    double forceFactor = 10 / (10 + position.distance(points[x][y].getPosition()));
//                    points[x][y].applyForce(force.multiply(forceFactor));
//                }
//            }
//        }
//    }
//
//    public void applyImplosiveForce(double force, Point2D position, float radius) {
//        for (int x = 0; x < points.length; x++) {
//            for (int y = 0; y < points[0].length; y++) {
//                double dist = position.distance(points[x][y].getPosition());
//                dist *= dist;
//                if (dist < radius * radius) {
//                    Point2D forceVec = position.subtract(points[x][y].getPosition());
//                    forceVec = forceVec.multiply(1f * force / (100 + dist));
//                    points[x][y].applyForce(forceVec);
//                    points[x][y].increaseDamping(0.6f);
//                }
//            }
//        }
//    }

    public void applyExplosiveForce(double force, Point2D position, double radius) {
        Vec2 tmpVec = Pools.obtain(Vec2.class);

        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                double dist = position.distance(points[x][y].getPosition().x, points[x][y].getPosition().y);
                dist *= dist;

                if (dist < radius * radius) {
                    tmpVec.set((float) position.getX(), (float) position.getY());
                    tmpVec.subLocal(points[x][y].getPosition()).mulLocal((float) (-10f * force / (10000 + dist)));

                    points[x][y].applyForce(tmpVec);
                    points[x][y].increaseDamping(0.6f);
                }
            }
        }

        Pools.free(tmpVec);
    }
}