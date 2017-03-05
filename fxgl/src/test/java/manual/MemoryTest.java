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

package manual;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class MemoryTest extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Memory Test");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    private Text text;

    @Override
    protected void initUI() {
        text = new Text();
        text.setFont(Font.font("Lucida Console", 18));
        text.relocate(100, 100);

        getGameScene().addUINode(text);
    }

    private static final double MB = 1024 * 1024.0;

    @Override
    protected void onUpdate(double tpf) {
//        text.setText(String.format("Used:  %7.1f MB"
//                + "\nFree:  %7.1f MB"
//                + "\nTotal: %7.1f MB"
//                + "\nMax:   %7.1f MB",
//                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB,
//                Runtime.getRuntime().freeMemory() / MB,
//                Runtime.getRuntime().totalMemory() / MB,
//                Runtime.getRuntime().maxMemory() / MB));


//                "Occupied: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB
//                + "\nFree: " + Runtime.getRuntime().freeMemory() / MB
//                + "\nTotal: " + Runtime.getRuntime().totalMemory() / MB
//                + "\nMax:   " + Runtime.getRuntime().maxMemory() / MB);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
