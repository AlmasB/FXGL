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

package com.almasb.fxglgames.towerfall;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.UserDataComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;

/**
 * Arrow sprite from https://www.spriters-resource.com/game_boy_advance/justiceleagueheroestheflash/sheet/18563/
 *
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class TowerfallFactory implements TextEntityFactory {

    @SpawnSymbol('B')
    public Entity newBackground(SpawnData data) {
        Rectangle rect = new Rectangle(1280, 720);
        rect.setFill(new LinearGradient(0, 0, 0, 720, false, CycleMethod.REFLECT,
                new Stop(0, Color.BLACK),
                new Stop(0.5, Color.GRAY)));

        return Entities.builder()
                .viewFromNode(new EntityView(rect, RenderLayer.BACKGROUND))
                .build();
    }

    @SpawnSymbol('1')
    public Entity newPlatform(SpawnData data) {
        return Entities.builder()
                .type(EntityType.PLATFORM)
                .at(data.getX(), data.getY())
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("towerfall/brick.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PhysicsComponent())
                .build();
    }

    @SpawnSymbol('P')
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(EntityType.PLAYER)
                .at(data.getX(), data.getY())
                .viewFromNode(new Rectangle(36, 36, Color.BLUE))
                .bbox(new HitBox("Main", BoundingShape.circle(18)))
                .with(physics)
                .with(new CharacterControl())
                .build();
    }

    @Spawns("Enemy")
    @SpawnSymbol('E')
    public Entity newEnemy(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(EntityType.ENEMY)
                .at(data.getX(), data.getY())
                .viewFromNode(new Rectangle(36, 36, Color.RED))
                .bbox(new HitBox("Main", BoundingShape.circle(18)))
                .with(physics, new CollidableComponent(true))
                .with(new CharacterControl(), new AIControl("towerfall/enemy_easy.tree"))
                .build();
    }

    @Spawns("Arrow")
    public Entity newArrow(SpawnData data) {
        return Entities.builder()
                .type(EntityType.ARROW)
                .at(data.getX(), data.getY())
                .viewFromNode(FXGL.getAssetLoader().loadTexture("towerfall/arrow.png", 35, 9))
                .bbox(new HitBox("MAIN", BoundingShape.box(28, 8)))
                .with(new CollidableComponent(true), new UserDataComponent(data.get("shooter")))
                .with(new OffscreenCleanControl(), new ExpireCleanControl(Duration.seconds(7)),
                        new ArrowControl(data.<Point2D>get("velocity").normalize()))
                .build();
    }

    @Override
    public char emptyChar() {
        return '0';
    }

    @Override
    public int blockWidth() {
        return 40;
    }

    @Override
    public int blockHeight() {
        return 40;
    }
}
