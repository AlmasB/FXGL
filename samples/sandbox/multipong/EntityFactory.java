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

package sandbox.multipong;

import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityFactory {

    public static Entity newBall(double x, double y) {
        GameEntity ball = new GameEntity();
        ball.getTypeComponent().setValue(EntityType.BALL);
        ball.getPositionComponent().setValue(x, y);
        ball.getBoundingBoxComponent().addHitBox(new HitBox("BODY", BoundingShape.circle(5)));
        ball.getMainViewComponent().setView(new Circle(5, Color.LIGHTGRAY));

        PhysicsComponent ballPhysics = new PhysicsComponent();
        ballPhysics.setBodyType(BodyType.DYNAMIC);

        FixtureDef def = new FixtureDef();
        def.setDensity(0.3f);
        def.setRestitution(1.0f);

        ballPhysics.setFixtureDef(def);
        ballPhysics.setOnPhysicsInitialized(() -> ballPhysics.setLinearVelocity(5 * 60, -5 * 60));

        ball.addComponent(ballPhysics);
        ball.addComponent(new CollidableComponent(true));
        ball.addControl(new BallControl());

        return ball;
    }

    public static Entity newBat(double x, double y, boolean isPlayer) {
        GameEntity bat = new GameEntity();
        bat.getTypeComponent().setValue(isPlayer ? EntityType.PLAYER_BAT : EntityType.ENEMY_BAT);
        bat.getPositionComponent().setValue(x, y);
        bat.getMainViewComponent().setView(new EntityView(new Rectangle(20, 60, Color.LIGHTGRAY)), true);

        PhysicsComponent batPhysics = new PhysicsComponent();
        batPhysics.setBodyType(BodyType.KINEMATIC);
        bat.addComponent(batPhysics);

        bat.addControl(isPlayer ? new BatControl() : new EnemyBatControl());

        return bat;
    }
}
