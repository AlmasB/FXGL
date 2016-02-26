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
package s8particles;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

/**
 * Example of using particles.
 * When left mouse button is clicked, an explosion will spawn at cursor position.
 */
public class ParticlesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ParticlesSample");
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

        input.addAction(new UserAction("Spawn Explosion") {
            @Override
            protected void onActionBegin() {
                // 1. create entity
                Entity explosion = new Entity();
                explosion.addComponent(new PositionComponent(input.getGameXY()));

                // 2. create and configure emitter + control
                ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter();
                ParticleControl control = new ParticleControl(emitter);

                // 3. add control to entity
                explosion.addControl(control);

                // we also want the entity to destroy itself after 3 seconds
                explosion.addControl(new ExpireCleanControl(Duration.seconds(3)));

                // 4. add entity to game world
                getGameWorld().addEntities(explosion);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {}

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
