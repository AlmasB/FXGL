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
package sandbox;

import com.almasb.ents.Entity;
import com.almasb.ents.component.StringComponent;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.io.DataFile;
import com.almasb.fxgl.io.IOResult;
import com.almasb.fxgl.io.SaveFile;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class App2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic FXGL Application");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addInputMapping(new InputMapping("Open", KeyCode.O));
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {

    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    private CompletableFuture<String> future = new CompletableFuture<>();

    @OnUserAction(name = "Open", type = ActionType.ON_ACTION_BEGIN)
    public void retryDialog() {

        IOResult<DataFile> io = getSaveLoadManager().load(new SaveFile("Test", LocalDateTime.now()));

        DataFile dataFile = io.getData();
    }

    private CompletableFuture<String> openDialog() {
        getDisplay().showInputBox("Enter a number", input -> {
            System.out.println("Entered: " + input);
            future.complete(input);
        });

        return future;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
