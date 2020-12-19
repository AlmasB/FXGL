/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d.firingrange;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FiringRangeFactory implements EntityFactory {

    @Spawns("levelBox")
    public Entity newLevelBox(SpawnData data) {
        var ground = new Box(50, 0.5, 50);
        ground.setMaterial(new PhongMaterial(Color.BROWN));

        var WALL_HEIGHT = 20;

        var wallL = new Box(0.5, WALL_HEIGHT, 50);
        wallL.setMaterial(new PhongMaterial(Color.GRAY));
        wallL.setTranslateY(-WALL_HEIGHT / 2.0);
        wallL.setTranslateX(-25);

        var wallR = new Box(0.5, WALL_HEIGHT, 50);
        wallR.setMaterial(new PhongMaterial(Color.GRAY));
        wallR.setTranslateY(-WALL_HEIGHT / 2.0);
        wallR.setTranslateX(+25);

        var wallT = new Box(50, WALL_HEIGHT, 0.5);
        wallT.setMaterial(new PhongMaterial(Color.GRAY));
        wallT.setTranslateY(-WALL_HEIGHT / 2.0);
        wallT.setTranslateZ(+25);

        var wallB = new Box(50, WALL_HEIGHT, 0.5);
        wallB.setMaterial(new PhongMaterial(Color.GRAY));
        wallB.setTranslateY(-WALL_HEIGHT / 2.0);
        wallB.setTranslateZ(-25);

        return entityBuilder(data)
                .view(new Group(ground, wallL, wallR, wallT, wallB))
                .build();
    }

    @Spawns("target")
    public Entity newTarget(SpawnData data) {
        var box = new Box(5, 5, 0.2);
        box.setMaterial(new PhongMaterial(Color.DARKKHAKI));

        var e = entityBuilder(data)
                .view(box)
                .build();

        double delay = data.get("delay");

        animationBuilder()
                .delay(Duration.seconds(delay))
                .interpolator(Interpolators.SMOOTH.EASE_IN())
                .repeatInfinitely()
                .autoReverse(true)
                .translate(e)
                .from(new Point3D(e.getX(), e.getY(), 15))
                .to(new Point3D(e.getX(), e.getY() - 6, 15))
                .buildAndPlay();

        return e;
    }

    @Spawns("light")
    public Entity newLight(SpawnData data) {
        var light = new PointLight();
        light.setTranslateY(-15);
        light.setTranslateZ(-15);

        return entityBuilder(data)
                .view(light)
                .view(new PointLight())
                .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        var sphere = new Sphere(0.5);
        sphere.setMaterial(new PhongMaterial(Color.YELLOW));

        Point3D dir = data.get("dir");

        return entityBuilder(data)
                .view(sphere)
                .with(new Projectile3DComponent(dir, 50))
                .with(new ExpireCleanComponent(Duration.seconds(5)))
                .build();
    }
}
