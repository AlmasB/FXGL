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

package sandbox.goap;

import com.almasb.fxgl.ecs.Entity;

/**
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class GoapAction {

    private State preconditions = new State();
    private State effects = new State();

    private boolean inRange = false;

    private final String name;

    /**
     * The cost of performing the action.
     * Figure out a weight that suits the action.
     * Changing it will affect what actions are chosen during planning.
     */
    public float cost = 1f;

    /**
     * An action often has to perform on an object.
     * This is that object.
     * Can be null.
     */
    public Entity target;

    public GoapAction(String name) {
        this.name = name;
    }

    public void doReset() {
        inRange = false;
        target = null;
        reset();
    }

    /**
     * Reset any variables that need to be reset before planning happens again.
     */
    public abstract void reset();

    /**
     * Is the action done?
     */
    public abstract boolean isDone();

    /**
     * Procedurally check if this action can run. Not all actions
     * will need this, but some might.
     */
    public abstract boolean checkProceduralPrecondition(Entity agent);

    /**
     * Run the action.
     * Returns True if the action performed successfully or false
     * if something happened and it can no longer perform. In this case
     * the action queue should clear out and the goal cannot be reached.
     */
    public abstract boolean perform(Entity agent);

    /**
     * Does this action need to be within range of a target game object?
     * If not then the moveTo state will not need to run for this action.
     */
    public abstract boolean requiresInRange();

    /**
     * Are we in range of the target?
     * The MoveTo state will set this and it gets reset each time this action is performed.
     */
    public boolean isInRange() {
        return inRange;
    }

    public void setInRange(boolean inRange) {
        this.inRange = inRange;
    }

    public void addPrecondition(String key, Object value) {
        preconditions.add(key, value);
    }

    public void removePrecondition(String key) {
        preconditions.remove(key);
    }

    public void addEffect(String key, Object value) {
        effects.add(key, value);
    }

    public void removeEffect(String key) {
        effects.remove(key);
    }

    public State getPreconditions() {
        return preconditions;
    }

    public State getEffects() {
        return effects;
    }

    @Override
    public String toString() {
        return name;
    }
}
