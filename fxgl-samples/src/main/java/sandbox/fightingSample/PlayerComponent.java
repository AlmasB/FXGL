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

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    private static final double BAT_SPEED = 80;


    public boolean block1 = false;
    public boolean block2 = false;


    protected PhysicsComponent physics;


    public void right() {
        if (entity.getX() >= BAT_SPEED / 60)
            physics.setVelocityX(-BAT_SPEED);
      else
           stop();
    }

    public void left() {
        if (entity.getX() + entity.getWidth() <= FXGL.getAppWidth() - (BAT_SPEED / 60))
            physics.setVelocityX(BAT_SPEED);
      else
            stop();
    }

    public void block() {
        block1 = true;
    }

    public void blockKick() {
        block2 = true;
    }

    public void unblock() {
        block1 = false;
        block2 = false;
    }

    public void thrown(int playerThrown) {
        if(playerThrown == 1){
            physics.applyBodyForceToCenter(new Vec2(-40,200));
        }
        else {
            physics.applyBodyForceToCenter(new Vec2(40,200));
        }
    }

    public void death(int playerDead) {
        if(playerDead == 1) {
            physics.applyBodyForceToCenter(new Vec2(-40,200));
        }
        else {
            physics.applyBodyForceToCenter(new Vec2(40,200));
        }
    }

    public void reset (int player) {
        if(player == 1){

            physics.applyBodyForceToCenter(new Vec2(-100,200));
        }
        else {
            physics.applyBodyForceToCenter(new Vec2(100,200));
        }
    }

    public void stop() {
       double yVel = physics.getVelocityY();
        physics.setLinearVelocity(0, yVel);
    }
}
