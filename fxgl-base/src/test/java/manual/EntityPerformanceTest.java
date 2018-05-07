/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package manual;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

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

        Logger.get("EPT").infof("Adding %d entities took:  %.3f sec", NUM_ENTITIES, (System.nanoTime() - start) / 1000000000.0);
    }

    private void testRemove() {
        long start = System.nanoTime();

        List<Entity> entities = getGameWorld().getEntitiesCopy();
        for (Entity e : entities) {
            getGameWorld().removeEntity(e);
        }

        Logger.get("EPT").infof("Remove took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);
    }

    private Entity newGameEntity() {
        return Entities.builder()
                .at(FXGLMath.random() * getWidth(), FXGLMath.random() * getHeight())
                .viewFromNode(new Rectangle(30, 30))
                //.with(new CircularMovementControl(2, 10))
                .build();
    }

    private Entity newEntity() {
        Entity e = new Entity();
        e.addComponent(new TestControl());
        return e;
    }

    private static class TestControl extends Component {

        private int i = 0;

        @Override
        public void onUpdate(double tpf) {
            i++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
