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

package sandbox.towerfall;

import com.almasb.ents.Entity;
import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.parser.EntityFactory;
import com.almasb.fxgl.parser.EntityProducer;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.gameutils.math.GameMath;
import com.almasb.gameutils.math.Vec2;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerfallFactory extends EntityFactory {

    public TowerfallFactory() {
        super('0');
    }

    @EntityProducer('1')
    public Entity newPlatform(int x, int y) {
        return Entities.builder()
                .type(EntityType.PLATFORM)
                .at(x * 40, y * 40)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("brick.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PhysicsComponent())
                .build();
    }

    @EntityProducer('P')
    public Entity newPlayer(int x, int y) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(EntityType.PLAYER)
                .at(x * 40, y * 40)
                .viewFromNode(new Rectangle(36, 36, Color.BLUE))
                .bbox(new HitBox("Main", BoundingShape.circle(18)))
                .with(physics)
                .with(new CharacterControl())
                .build();
    }

    @EntityProducer('E')
    public Entity newEnemy(int x, int y) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(EntityType.ENEMY)
                .at(x * 40, y * 40)
                .viewFromNode(new Rectangle(36, 36, Color.RED))
                .bbox(new HitBox("Main", BoundingShape.circle(18)))
                .with(physics)
                //.with(new CharacterControl(), new AIControl("towerfall_enemy_easy.tree"))
                .build();
    }

    public Entity newArrow(int x, int y, Point2D velocity) {
        return Entities.builder()
                .type(EntityType.ARROW)
                .at(x, y)
                .viewFromNodeWithBBox(new Rectangle(15, 2, Color.RED))
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanControl(), new ExpireCleanControl(Duration.seconds(3)),
                        new ArrowControl(velocity.normalize()))
                .build();
    }
}
