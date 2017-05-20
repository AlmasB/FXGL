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

package s10miscellaneous;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FlyingKeysSample extends GameApplication {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("FlyingKeysSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Box") {
            @Override
            protected void onActionBegin() {
                GameEntity box = createPhysicsEntity();

                // 3. set hit box (-es) to specify bounding shape
                box.getBoundingBoxComponent()
                        .addHitBox(new HitBox("Left", BoundingShape.box(20, 30)));

                Button button = new Button(ALPHABET.charAt(FXGLMath.random(ALPHABET.length() - 1)) + "");
                button.setPrefWidth(20);
                button.setPrefHeight(30);
                button.setOnAction(e -> System.out.println(button.getText()));

                box.getViewComponent().setView(button);

                getGameWorld().addEntity(box);
            }
        }, MouseButton.SECONDARY);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        getGameWorld().addEntity(Entities.makeScreenBounds(50));
    }

    @Override
    protected void initPhysics() {

        getMasterTimer().runAtInterval(() -> {
            getPhysicsWorld().setGravity(FXGLMath.random(-10, 10), FXGLMath.random(-10, 10));
        }, Duration.seconds(2.5));
    }

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    private GameEntity createPhysicsEntity() {
        // 1. create and configure physics component
        PhysicsComponent physics = new PhysicsComponent();

        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(0.7f);
        fd.setRestitution(0.3f);
        physics.setFixtureDef(fd);

        return Entities.builder()
                .at(getWidth() / 2, getHeight() / 2)
                // 2. add physics component
                .with(physics)
                .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
