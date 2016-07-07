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

package com.almasb.fxgl.entity.component;

import com.almasb.easyio.serialization.Bundle;
import com.almasb.ents.AbstractComponent;
import com.almasb.ents.serialization.SerializableComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Adds ID to an entity, so it can be uniquely identified.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class IDComponent extends AbstractComponent implements SerializableComponent {

    private String name;
    private int id;

    /**
     * Constructs ID component with given entity name and id.
     * The combination of name and id must be unique.
     *
     * @param name string representation of entity name
     * @param id numeric id that uniquely identifies the entity with given name
     */
    public IDComponent(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * @return entity name / string representation
     */
    public final String getName() {
        return name;
    }

    /**
     * @return numeric id
     */
    public final int getID() {
        return id;
    }

    /**
     * @return full id, this must be unique
     */
    public final String getFullID() {
        return name + ":" + id;
    }

    @Override
    public int hashCode() {
        return getFullID().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // just assume it's IDComponent
        return ((IDComponent)obj).getFullID().equals(getFullID());
    }

    @Override
    public String toString() {
        return getFullID();
    }

    @Override
    public void write(@NotNull Bundle bundle) {
        bundle.put("name", name);
        bundle.put("id", id);
    }

    @Override
    public void read(@NotNull Bundle bundle) {
        name = bundle.get("name");
        id = bundle.get("id");
    }
}
