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

package sandbox.scifi;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimationTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {

    private PositionComponent position;
    private ViewComponent view;
    private PhysicsComponent physics;

    private double oldX = 0;
    private boolean isStatic = true;

    private Texture staticTexture;
    private AnimationTexture animatedTexture;

    private AnimationChannel animStand, animWalk;

    public PlayerControl(Texture staticTexture, AnimationTexture animatedTexture) {
        this.staticTexture = staticTexture;
        this.animatedTexture = animatedTexture;

        animStand = new AnimationChannel("dude.png", 4, 32, 42, 15, 1, 1);
        animWalk = new AnimationChannel("dude.png", 4, 32, 42, 15, 0, 3);

        //animStand = new AnimationChannel("dude.png", 6, 128, 133, 5, 0, 0);
        //animWalk = new AnimationChannel("dude.png", 6, 128, 133, 5, 0, 5);

        this.animatedTexture.setAnimationChannel(animStand);
    }

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        physics = Entities.getPhysics(entity);
        view = Entities.getView(entity);

        oldX = position.getX();
        view.getView().addNode(animatedTexture);
        //view.getView().addNode(staticTexture);
    }

    // not the most elegant solution for static checks
    // will replace when physics API allows us to check if body is ready
    // then simply query velocity magnitude
    @Override
    public void onUpdate(Entity entity, double tpf) {
        //if (oldX == position.getX()) {
        if (Math.abs(physics.getVelocityX()) == 0) {
            //if (!isStatic) {
                stopAnimate();
            //}
        } else {
            animate();
        }

        if (Math.abs(physics.getVelocityX()) < 140)
            physics.setVelocityX(0);


        animatedTexture.update();
    }

    public void left() {
        view.getView().setScaleX(-1);
        physics.setVelocityX(-150);

//        if (isStatic) {
//            animate();
//        }
    }

    public void right() {
        view.getView().setScaleX(1);
        physics.setVelocityX(150);

//        if (isStatic) {
//            animate();
//        }
    }

    private void animate() {
       // System.out.println("animate");


        //animWalk.reset();
        animatedTexture.setAnimationChannel(animWalk);
        isStatic = false;
    }

    private void stopAnimate() {
        //System.out.println("stop");

        //animWalk.reset();
        animatedTexture.setAnimationChannel(animStand);
        isStatic = true;
    }

    public void jump() {
        physics.setVelocityY(-350);
    }

    public void stop() {
        physics.setVelocityX(physics.getVelocityX() * 0.7);
    }

    public void shoot(Point2D endPoint) {
        double x = position.getX();
        double y = position.getY();

        Point2D velocity = endPoint
                .subtract(x, y)
                .normalize()
                .multiply(500);

        ((GameWorld) getEntity().getWorld()).spawn("Arrow",
                new SpawnData(x, y)
                        .put("velocity", velocity)
                        .put("shooter", getEntity()));
    }
}
