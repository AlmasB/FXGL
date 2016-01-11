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

package com.almasb.fxgl.asset;

/**
 * Represents result of an IO based operation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class IOResult<T> {

    //private static final IOResult<?> SUCCESS = new IOResult<>(true, "");

    private final boolean ok;
    private final String errorMessage;
    private final T data;

    private IOResult(boolean ok, String errorMessage) {
        this.ok = ok;
        this.errorMessage = errorMessage;
        data = null;
    }

    private IOResult(T data) {
        this.ok = true;
        this.errorMessage = "";
        this.data = data;
    }

    /**
     * @return true iff the IO operation succeeded
     */
    public boolean isOK() {
        return ok;
    }

    /**
     * Returns error message associated with the operation.
     * Returns empty string if operation succeeded.
     *
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasData() {
        return data != null;
    }

    public T getData() {
        return data;
    }

    /**
     * @return successful IO result
     */
    public static <T> IOResult<T> success() {
        return new IOResult<>(true, "");
    }

    /**
     * @param data IO data
     * @return successful IO result
     */
    public static <T> IOResult<T> success(T data) {
        return new IOResult<>(data);
    }

    /**
     * @param message error message
     * @return failed IO result
     */
    public static <T> IOResult<T> failure(String message) {
        return new IOResult<>(false, message);
    }
}
