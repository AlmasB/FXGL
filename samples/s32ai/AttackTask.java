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

package s32ai;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.ai.Action;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.GameEntity;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AttackTask extends Action {

    @Override
    public void action() {
        GameEntity player = ((BehaviorSample) FXGL.getApp()).player;

        if (player.distance(getObject()) < 100) {
            getObject().getControlUnsafe(AIControl.class).setBubbleMessage("Attack");
        } else {
            getObject().getControlUnsafe(AIControl.class).setBubbleMessage("Chase");
            double speed = 0.017 * 60 * 5;

            Point2D vector = player.getPosition()
                    .subtract(getObject().getPosition())
                    .normalize()
                    .multiply(speed);

            getObject().getPositionComponent().translate(vector);
        }
    }
}
