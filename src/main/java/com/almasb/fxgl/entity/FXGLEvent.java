/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.entity;

/**
 * A general FXGL event. Keeps track of its source and target.
 * Target is the entity on which the event was called, source (if exists)
 * is the entity that triggered the event. If the source doesn't exist,
 * it is equal to target.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLEvent {

    private FXGLEventType type;
    private Entity source, target;

    /**
     * Constructs a new FXGL event with given type.
     *
     * @param type event type
     */
    public FXGLEvent(FXGLEventType type) {
        this(type, null);
    }

    /**
     * Constructs a new FXGL event with given type and source.
     *
     * @param type event type
     * @param source event source
     */
    public FXGLEvent(FXGLEventType type, Entity source) {
        this.type = type;
        this.source = source;
    }

    /**
     * @return event type
     */
    public FXGLEventType getType() {
        return type;
    }

    /**
     * @return the entity which triggered the event
     */
    public Entity getSource() {
        return source;
    }

    /* package-private */ void setSource(Entity source) {
        this.source = source;
    }

    /**
     * @return the entity on which the event was fired by
     * calling {@link Entity#fireFXGLEvent(FXGLEvent)}
     */
    public Entity getTarget() {
        return target;
    }

    /* package-private */ void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FXGLEvent [type=");
        builder.append(type.getUniqueType());
        builder.append(", source=");
        builder.append(source);
        builder.append(", target=");
        builder.append(target);
        builder.append("]");
        return builder.toString();
    }
}
