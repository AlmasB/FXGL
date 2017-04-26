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

package com.almasb.fxgl.service;

import com.almasb.fxgl.io.UIDialogHandler;
import com.almasb.fxgl.util.EmptyRunnable;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface DialogFactory {

    Pane messageDialog(String message, Runnable callback);

    default Pane messageDialog(String message) {
        return messageDialog(message, EmptyRunnable.INSTANCE);
    }

    Pane confirmationDialog(String message, Consumer<Boolean> callback);

    default Pane inputDialog(String message, Consumer<String> callback) {
        return inputDialog(message, s -> true, callback);
    }

    Pane inputDialog(String message, Predicate<String> filter, Consumer<String> callback);

    Pane inputDialogWithCancel(String message, Predicate<String> filter, Consumer<String> callback);

    Pane errorDialog(Throwable error, Runnable callback);

    default Pane errorDialog(Throwable error) {
        return errorDialog(error, EmptyRunnable.INSTANCE);
    }

    default Pane errorDialog(String errorMessage) {
        return errorDialog(errorMessage, EmptyRunnable.INSTANCE);
    }

    default Pane errorDialog(String errorMessage, Runnable callback) {
        return messageDialog("Error occurred: " + errorMessage, callback);
    }

    Pane progressDialog(DoubleProperty observable, Runnable callback);

    Pane progressDialogIndeterminate(String message, Runnable callback);

    @Deprecated
    Pane customDialog(String message, Node content, Runnable callback, Button... buttons);

    Pane customDialog(Node view, Runnable callback);
}
