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

package s03entities;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.action.Action;
import com.almasb.fxgl.ecs.action.ActionControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to make entities perform actions.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityActionSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("EntityActionSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private GameEntity entity;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Now") {
            @Override
            protected void onActionBegin() {

                entity.getControlUnsafe(ActionControl.class).clearActions();

                entity.getControlUnsafe(ActionControl.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.PRIMARY);

        getInput().addAction(new UserAction("Queue Move Action") {
            @Override
            protected void onActionBegin() {
                entity.getControlUnsafe(ActionControl.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.SECONDARY);
    }

    @Override
    protected void initGame() {
        entity = Entities.builder()
                .at(400, 300)
                .viewFromNode(new Rectangle(40, 40))
                .with(new ActionControl<GameEntity>())
                .buildAndAttach(getGameWorld());
    }

    private class MoveAction extends Action<GameEntity> {

        private double x, y;
        private double speed;

        public MoveAction(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean isComplete() {
            return getEntity().getPosition().distance(x, y) < speed;
        }

        @Override
        public void onUpdate(GameEntity entity, double tpf) {
            speed = 150 * tpf;

            entity.translateTowards(new Point2D(x, y), speed);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
