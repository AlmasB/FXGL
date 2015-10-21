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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package s10realphysics;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.UserAction;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.util.ApplicationMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class BasicGameApplication extends GameApplication {

    private enum Type implements EntityType {
        GROUND, BOX
    }

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
        InputManager input = getInputManager();

        input.addAction(new UserAction("Spawn Box") {
            @Override
            protected void onActionBegin() {
                // 1. create physics entity
                PhysicsEntity box = new PhysicsEntity(Type.GROUND);
                box.setPosition(input.getMouse().getGameX(), input.getMouse().getGameY());

                // 2. set body type to dynamic for moving entities
                // not controlled by user
                box.setBodyType(BodyType.DYNAMIC);

                // 3. set various physics properties
                FixtureDef fd = new FixtureDef();
                fd.density = 0.5f;
                fd.restitution = 0.3f;
                box.setFixtureDef(fd);

                Rectangle rect = new Rectangle(40, 40);
                rect.setFill(Color.BLUE);
                box.setSceneView(rect);

                getGameWorld().addEntity(box);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initAssets() throws Exception {}

    @Override
    protected void initGame() {
        // 4. by default a physics entity is statis
        PhysicsEntity ground = new PhysicsEntity(Type.GROUND);
        ground.setPosition(0, 500);
        ground.setSceneView(new Rectangle(800, 100));

        getGameWorld().addEntity(ground);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate() {}

    public static void main(String[] args) {
        launch(args);
    }
}
