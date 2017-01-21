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
package com.almasb.fxgl.app;

import com.almasb.fxgl.scene.menu.MenuStyle;
import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.util.Credits;
import javafx.scene.input.KeyCode;

import java.util.Arrays;

/**
 * A test game app used to mock the user app.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MockGameApplication extends GameApplication {

    public static MockGameApplication INSTANCE;

    public MockGameApplication() {
        if (INSTANCE != null)
            throw new IllegalStateException("INSTANCE != null");

        INSTANCE = this;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(500);
        settings.setHeight(500);
        settings.setTitle("Test");
        settings.setVersion("0.99");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setFullScreen(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setMenuKey(KeyCode.ENTER);
        settings.setMenuStyle(MenuStyle.CCTR);
        settings.setCredits(new Credits(Arrays.asList("TestCredit1", "TestCredit2")));
        settings.addServiceType(new ServiceType<MockService>() {
            @Override
            public Class<MockService> service() {
                return MockService.class;
            }

            @Override
            public Class<? extends MockService> serviceProvider() {
                return MockServiceProvider.class;
            }
        });
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
    }

    @Override
    protected void initAssets() {
    }

    @Override
    protected void initGame() {
    }

    @Override
    protected void initPhysics() {
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void onUpdate(double tpf) {
    }
}
