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

package com.almasb.fxgl.ecs;

/**
 * A single entity action.
 * Unlike a {@link Control} the action is finished after its execution,
 * a control is active during the entity lifetime (or until removed).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Action<T extends Entity> {

    private T entity;

    /**
     * @return entity to which the action is attached, or null if action is not attached
     */
    public final T getEntity() {
        return entity;
    }

    void setEntity(T entity) {
        if (entity == null && this.entity == null)
            throw new IllegalStateException("Attempt to clear entity but action is not attached to an entity");

        if (entity != null && this.entity != null)
            throw new IllegalStateException("Attempt to set entity but action is already attached to an entity");

        if (entity != null)
            onAdded(entity);
        else
            onRemoved(this.entity);

        this.entity = entity;
    }

    protected void onAdded(T entity) {
        // no-op
    }

    protected void onRemoved(T entity) {
        // no-op
    }

    /**
     * @return true if this action is complete and does not require further execution
     */
    public abstract boolean isComplete();

    /**
     * Called on entity world update tick.
     *
     * @param entity the entity to which this action is attached
     * @param tpf time per frame
     */
    protected abstract void onUpdate(T entity, double tpf);
}
