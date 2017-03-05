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

package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxglgames.tictactoe.control.TileControl;

/**
 * Instead of using generic GameEntity we add a few convenience methods.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileEntity extends GameEntity {

    public TileEntity(double x, double y) {
        setX(x);
        setY(y);
        addComponent(new TileValueComponent());

        getViewComponent().setView(new TileView(this), true);
        addControl(new TileControl());
    }

    public TileValue getValue() {
        return getComponentUnsafe(TileValueComponent.class).getValue();
    }

    public TileControl getControl() {
        return getControlUnsafe(TileControl.class);
    }
}
