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
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FiringRangeFactory implements EntityFactory {

    @Spawns("levelBox")
    public Entity newLevelBox(SpawnData data) {
        var ground = new Box(50, 0.5, 150);
        ground.setMaterial(new PhongMaterial(Color.BROWN));

        var WALL_HEIGHT = 20;

        var wallL = new Box(0.5, WALL_HEIGHT, 150);
        wallL.setMaterial(new PhongMaterial(Color.LIGHTCORAL));
        wallL.setTranslateY(-WALL_HEIGHT / 2.0);
        wallL.setTranslateX(-ground.getWidth() / 2.0);

        var wallR = new Box(0.5, WALL_HEIGHT, 150);
        wallR.setMaterial(new PhongMaterial(Color.LIGHTCORAL));
        wallR.setTranslateY(-WALL_HEIGHT / 2.0);
        wallR.setTranslateX(+ground.getWidth() / 2.0);

        var wallT = new Box(50, WALL_HEIGHT, 0.5);
        wallT.setMaterial(new PhongMaterial(Color.LIGHTCORAL));
        wallT.setTranslateY(-WALL_HEIGHT / 2.0);
        wallT.setTranslateZ(+wallL.getDepth() / 2.0);

        var wallB = new Box(50, WALL_HEIGHT, 0.5);
        wallB.setMaterial(new PhongMaterial(Color.LIGHTCORAL));
        wallB.setTranslateY(-WALL_HEIGHT / 2.0);
        wallB.setTranslateZ(-25);

        return entityBuilder(data)
                .view(new Group(
                        ground, wallL, wallR, wallT, wallB
                ))
                .build();
    }

    @Spawns("target")
    public Entity newTarget(SpawnData data) {
        var box = new Box(5, 5, 0.2);
        box.setMaterial(new PhongMaterial(Color.DARKKHAKI));

        var e = entityBuilder(data)
                .type(FiringRangeEntityType.TARGET)
                .bbox(new HitBox(BoundingShape.box3D(5, 5, 0.2)))
                .view(box)
                .collidable()
                .with(new Projectile3DComponent(new Point3D(1, 0, 0), 15))
                .with(new ExpireCleanComponent(Duration.seconds(6)))
                .build();

        animationBuilder()
                .duration(Duration.seconds(0.6))
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .scale(e)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(1, 1, 1))
                .buildAndPlay();

        return e;
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        var sphere = new Sphere(0.5);
        sphere.setMaterial(new PhongMaterial(Color.YELLOW));

        Point3D dir = data.get("dir");

        return entityBuilder(data)
                .type(FiringRangeEntityType.BULLET)
                .bbox(new HitBox(BoundingShape.box3D(1, 1, 1)))
                .view(sphere)
                .with(new Projectile3DComponent(dir, 50))
                .with(new ExpireCleanComponent(Duration.seconds(5)))
                .collidable()
                .build();
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
}
