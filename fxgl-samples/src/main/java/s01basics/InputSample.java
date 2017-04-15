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

package s01basics;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.state.PlayState;
import com.almasb.fxgl.app.state.SubState;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.service.impl.input.FXGLInput;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.UpdateEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

/**
 * Shows how to use input service and bind actions to triggers.
 */
public class InputSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InputSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        // 1. get input service
        Input input = getInput();

        // 2. add key/mouse bound actions
        // when app is running press F to see output to console
        input.addAction(new UserAction("Print Line") {
            @Override
            protected void onActionBegin() {
                System.out.println("Action Begin");

                pushState(new SubState() {

                    Input i = new FXGLInput();
                    Rectangle r = new Rectangle(400 ,400);

                    {
                        r.setOnMouseClicked(e -> {
                            popState();
                        });

                        i.addAction(new UserAction("Key") {
                            @Override
                            protected void onActionBegin() {
                                System.out.println("from substate");
                            }

                            @Override
                            protected void onAction() {
                                System.out.println("from sub");
                            }
                        }, KeyCode.Y);
                    }

                    @NotNull
                    @Override
                    public String getName() {
                        return "MySubState";
                    }

                    @NotNull
                    @Override
                    public Node view() {
                        return r;
                    }

                    @NotNull
                    @Override
                    public Input input() {
                        return i;
                    }

                    @Override
                    public void onEnter() {

                    }

                    @Override
                    public void onExit() {

                    }

                    @Override
                    public void onUpdate(double tpf) {

                    }
                });
            }

            @Override
            protected void onAction() {
                System.out.println("On Action");
            }

            @Override
            protected void onActionEnd() {
                System.out.println("Action End");
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

//    @Override
//    protected void initGame() {
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    public void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
