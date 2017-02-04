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

package com.almasb.fxglgames.towerfall.collision;

import com.almasb.fxgl.algorithm.AASubdivision;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import com.almasb.fxglgames.towerfall.ArrowControl;
import com.almasb.fxglgames.towerfall.EntityType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class ArrowPlatformHandler extends CollisionHandler {

    private Image blockImage;

    public ArrowPlatformHandler() {
        super(EntityType.ARROW, EntityType.PLATFORM);

        blockImage = FXGL.getAssetLoader().loadTexture("brick.png", 40, 40).getImage();
    }

    @Override
    protected void onCollisionBegin(Entity arrow, Entity platform) {
        // necessary since we can collide with two platforms in the same frame
        if (arrow.hasControl(ArrowControl.class)) {
            arrow.getComponentUnsafe(CollidableComponent.class).setValue(false);
            arrow.removeControl(ArrowControl.class);

            GameEntity block = (GameEntity) platform;

            Rectangle2D grid = new Rectangle2D(0, 0, 40, 40);

            Array<Rectangle2D> grids = AASubdivision.divide(grid, 30, 5);

            for (Rectangle2D rect : grids) {
                PhysicsComponent physics = new PhysicsComponent();
                physics.setBodyType(BodyType.DYNAMIC);

                FixtureDef fd = new FixtureDef();
                fd.setDensity(0.7f);
                fd.setRestitution(0.3f);
                physics.setFixtureDef(fd);

                physics.setOnPhysicsInitialized(() -> physics.setLinearVelocity(FXGLMath.random(-1, 1) * 50, FXGLMath.random(-3, -1) * 50));

                Image img = new WritableImage(blockImage.getPixelReader(),
                        (int) rect.getMinX(), (int) rect.getMinY(),
                        (int) rect.getWidth(), (int) rect.getHeight());


                Entities.builder()
                        .at(block.getX() + rect.getMinX(), block.getY() + rect.getMinY())
                        .viewFromNodeWithBBox(new ImageView(img))
                        //.viewFromNodeWithBBox(new Rectangle(rect.getWidth(), rect.getHeight(), Color.BLUE))
                        .with(physics)
                        .buildAndAttach(FXGL.getApp().getGameWorld());
            }

            platform.removeFromWorld();
        }
    }
}
