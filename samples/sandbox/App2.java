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

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.io.IOTask;
import com.almasb.fxgl.io.SequentialIOTask;
import com.almasb.fxgl.net.Net;
import com.almasb.fxgl.scene.menu.MenuStyle;
import com.almasb.fxgl.settings.GameSettings;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setMenuStyle(MenuStyle.GTA5);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addInputMapping(new InputMapping("Open", KeyCode.O));
        input.addInputMapping(new InputMapping("Test", KeyCode.K));
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

    @OnUserAction(name = "Open", type = ActionType.ON_ACTION_BEGIN)
    public void test() {
//        Net net = FXGL.getService(ServiceType.NET);
//
//        net.downloadTask("https://raw.githubusercontent.com/AlmasB/FXGLGames/master/data/test.txt")
//                .onSuccess(file -> {
//                    try {
//                        Files.readAllLines(file)
//                                .forEach(System.out::println);
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                })
//                .onFailure(getDefaultCheckedExceptionHandler())
//                .executeAsyncWithProgressDialog("Downloading...");
//
//        System.out.println("Task submitted");




//        IOTask<String> task = new IOTask<String>() {
//            @Override
//            public String onExecute() throws Exception {
//                System.out.println("Executing");
//
//                Thread.sleep(3000);
//
//                return Files.readAllLines(Paths.get("test.txt")).get(0);
//            }
//        };
//
//        task.onSuccess(System.out::println)
//                .onFailure(getDefaultCheckedExceptionHandler())
//                .executeAsyncWithProgressDialog("Reading from File System");
//
//        System.out.println("Task submitted");





        new IOTask<String>() {
            @Override
            protected String onExecute() throws Exception {

                System.out.println("execute 1");

                //throw new IllegalArgumentException("Testerror");

                return "LongIntDouble";
            }
        }.then(s -> {

            System.out.println("Result1: " + s);

            return new IOTask<Integer>() {
                @Override
                protected Integer onExecute() throws Exception {
                    System.out.println("execute 2");


                    //throw new IllegalArgumentException("Testerror");
                    return s.length();
                }
            };
        }).then(i -> {

            System.out.println("Result2: " + i);

            return new IOTask<Double>() {
                @Override
                protected Double onExecute() throws Exception {
                    System.out.println("execute 3.1");

                    //Thread.sleep(1400);

                    return i + 1.5;
                }
            }.then(d -> new IOTask<Double>() {
                @Override
                protected Double onExecute() throws Exception {
                    System.out.println("execute 3.2");

                    return d * 2;
                }
            });


        }).then(i -> new IOTask<Integer>() {
            @Override
            protected Integer onExecute() throws Exception {
                System.out.println("execute 4");

                Thread.sleep(3000);

                return i.intValue();
            }
        }).onSuccess(value -> {
            System.out.println("Result: " + value);
        }).onFailure(error -> {
            System.out.println("error: " + error);
        }).executeAsyncWithProgressDialog("Computing");







        System.out.println("Task submitted");
    }

    @OnUserAction(name = "Test", type = ActionType.ON_ACTION_BEGIN)
    public void test2() {
        System.out.println("Printing");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
