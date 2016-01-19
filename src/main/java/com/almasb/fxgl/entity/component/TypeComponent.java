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

import com.almasb.ents.AbstractComponent;
import com.almasb.ents.component.ObjectComponent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TypeComponent extends ObjectComponent<Object> {

    //private ReadOnlyObjectWrapper<T> type;

    public TypeComponent() {
        this(new Object());
    }

    public TypeComponent(Object type) {
        super(type);
    }

//    public TypeComponent(T type) {
//        if (type == null)
//            throw new IllegalArgumentException("Type cannot be null");
//
//        this.type = new ReadOnlyObjectWrapper<>(type);
//    }
//
//    public T getType() {
//        return type.get();
//    }
//
//    public ReadOnlyObjectProperty<T> typeProperty() {
//        return type.getReadOnlyProperty();
//    }
//
//    public void setType(T type) {
//        if (type == null)
//            throw new IllegalArgumentException("Type cannot be null");
//
//        this.type.set(type);
//    }
}
