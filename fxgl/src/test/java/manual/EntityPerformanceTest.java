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

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.control.CircularMovementControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.logging.SystemLogger;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityPerformanceTest extends GameApplication {

    private static final int NUM_ENTITIES = 10000;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("EntityPerformanceTest");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Test Add") {
            @Override
            protected void onActionBegin() {
                testAdd();
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Test Remove") {
            @Override
            protected void onActionBegin() {
                testRemove();
            }
        }, KeyCode.G);
    }

//    @Override
//    protected void initGame() {
//
//    }
//
//    @Override
//    protected void onUpdate(double tpf) {
//
//    }

    private void testAdd() {
        long start = System.nanoTime();

        for (int i = 0; i < NUM_ENTITIES; i++) {
            getGameWorld().addEntity(newEntity());
        }

        SystemLogger.INSTANCE.infof("Adding %d entities took:  %.3f sec", NUM_ENTITIES, (System.nanoTime() - start) / 1000000000.0);
    }

    private void testRemove() {
        long start = System.nanoTime();

        List<Entity> entities = getGameWorld().getEntitiesCopy();
        for (Entity e : entities) {
            getGameWorld().removeEntity(e);
        }

        SystemLogger.INSTANCE.infof("Remove took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);
    }

    private Entity newGameEntity() {
        return Entities.builder()
                .at(FXGLMath.random() * getWidth(), FXGLMath.random() * getHeight())
                .viewFromNode(new Rectangle(30, 30))
                .with(new CircularMovementControl(2, 10))
                .build();
    }

    private Entity newEntity() {
        Entity e = new Entity();
        e.addControl(new TestControl());
        return e;
    }

    private static class TestControl extends AbstractControl {

        private int i = 0;

        @Override
        public void onUpdate(Entity entity, double tpf) {
            i++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
