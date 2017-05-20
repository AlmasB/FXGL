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

package s09advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.settings.GameSettings;

/**
 * This is an example of using async tasks.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AsyncSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AsyncSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {

        // arbitrary example

        Async<Integer> async = getExecutor().async(() -> {
            System.out.println("AI thread: " + Thread.currentThread().getName());
            System.out.println("AI tick");
            Thread.sleep(2000);
            System.out.println("AI Done");
            return 999;
        });

        Async<Double> async2 = getExecutor().async(() -> {
            System.out.println("Render thread: " + Thread.currentThread().getName());
            System.out.println("Render tick");
            Thread.sleep(300);
            System.out.println("Render Done");
            return 399.0;
        });

        Async<Void> async3 = getExecutor().async(() -> {
            System.out.println("Running some code");
        });

        System.out.println("Physics thread: " + Thread.currentThread().getName());
        System.out.println("Physics tick Done. Waiting for AI & Render");

        int value = async.await();
        double value2 = async2.await();

        System.out.println("Values: " + value + " " + value2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
