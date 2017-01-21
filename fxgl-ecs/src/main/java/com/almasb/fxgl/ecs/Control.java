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
 * Defines behavior of an entity. Unlike the "System" in the ECS model,
 * control is attached directly to an entity and is stateful (i.e. it
 * knows about entity to which it is attached).
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface Control {

    /**
     * Called when this control is added to entity.
     * This is called before any control related listeners are notified.
     * This allows the control to initiliaze properly.
     *
     * @param entity the entity to which this control was added
     */
    void onAdded(Entity entity);

    /**
     * Called on entity world update tick.
     *
     * @param entity the entity to which this control is attached
     * @param tpf time per frame
     */
    void onUpdate(Entity entity, double tpf);

    /**
     * Called when this control is removed from entity.
     * This is called after any control related listeners are notified.
     * This allows the control to clean up properly.
     *
     * @param entity the entity from which the control was removed
     */
    void onRemoved(Entity entity);

    /**
     * @return if execution of this control is paused
     */
    boolean isPaused();

    /**
     * Pauses execution of this control.
     */
    void pause();

    /**
     * Resumes execution of this control.
     */
    void resume();
}
