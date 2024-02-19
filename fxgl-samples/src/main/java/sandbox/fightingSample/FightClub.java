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

package sandbox.fightingSample;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FightClub implements EntityFactory {

    @Spawns("bat")
    public Entity newBat(SpawnData data) {
        boolean isPlayer = data.get("isPlayer");

        PhysicsComponent physics = new PhysicsComponent();
            physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder(data)
                .type(isPlayer ? EntityType.PLAYER_1 : EntityType.PLAYER_2)
                .view(new Rectangle(64, 160, Color.RED))
                .view(new Rectangle(64, 32 , Color.GREEN))
                .bbox(new HitBox("head",new Point2D(0, 0),BoundingShape.box(64, 32)))
                .bbox(new HitBox("body",new Point2D(0, 32),BoundingShape.box(64, 128)))
                .bbox(new HitBox("top",new Point2D(32, 0),BoundingShape.box(1, 1)))
                .bbox(new HitBox("bot",new Point2D(32, 160),BoundingShape.box(1, 1)))
                .with(new CollidableComponent(true))
                .with(physics)
                .with(new PlayerComponent())
                .build();
    }


}
