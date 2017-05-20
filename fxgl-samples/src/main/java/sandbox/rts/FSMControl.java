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

package sandbox.rts;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ai.fsm.DefaultStateMachine;
import com.almasb.fxgl.ai.fsm.State;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FSMControl<T extends Entity> extends AbstractControl {

    private DefaultStateMachine<T, State<T>> fsm;

    public <S extends State<T>> FSMControl(Class<S> stateType, S initialState) {
        fsm = new DefaultStateMachine<>(null, initialState);
    }

    @Override
    public void onAdded(Entity entity) {
        fsm.setOwner((T) entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        fsm.update();
    }

    public void changeState(State<T> newState) {
        fsm.changeState(newState);
    }
}
