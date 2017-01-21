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

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RangeTest extends GameApplication {

    private GameEntity player, markers;
    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);
    }

    @Override
    protected void initAssets() {

    }

    @Override
    protected void initGame() {
        spawnEntity(100, 100);
        spawnEntity(500, 300);
        spawnEntity(300, 15);
        spawnEntity(400, 500);
        spawnEntity(500, 500);

        player = new GameEntity();
        player.getPositionComponent().setValue(400, 300);
        player.getViewComponent().setView(new EntityView(new Rectangle(40, 40)), true);

        playerControl = new PlayerControl();
        player.addControl(playerControl);

        markers = new GameEntity();

        EntityView view = new EntityView();
        view.addNode(getMarker(-40, 0));
        view.addNode(getMarker(40, 0));
        view.addNode(getMarker(0, -40));
        view.addNode(getMarker(0, 40));

        view.addNode(getMarker(-40, -40));
        view.addNode(getMarker(40, -40));
        view.addNode(getMarker(-40, 40));
        view.addNode(getMarker(40, 40));

        markers.getViewComponent().setView(view);
        markers.getPositionComponent().xProperty().bind(player.getPositionComponent().xProperty());
        markers.getPositionComponent().yProperty().bind(player.getPositionComponent().yProperty());

        getGameWorld().addEntities(player, markers);
    }

    @Override
    protected void initPhysics() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {
        List<Entity> list = getGameWorld().getEntitiesInRange(player.getBoundingBoxComponent().range(40, 40));
        list.forEach(e -> {
            if (e == markers)
                return;

            EntityView view = e.getComponentUnsafe(ViewComponent.class).getView();
            view.getNodes()
                    .stream()
                    .map(n -> (Rectangle)n)
                    .forEach(r -> r.setFill(Color.YELLOW));
        });

        List<Entity> list2 = getGameWorld().getEntitiesCopy();
        list2.removeAll(list);

        list2.forEach(e -> {
            EntityView view = e.getComponentUnsafe(ViewComponent.class).getView();
            view.getNodes()
                    .stream()
                    .map(n -> (Rectangle)n)
                    .forEach(r -> r.setFill(Color.BLACK));
        });
    }

    private void spawnEntity(double x, double y) {
        GameEntity entity = new GameEntity();
        entity.getPositionComponent().setValue(x, y);
        entity.getViewComponent().setView(new EntityView(new Rectangle(40, 40)), true);

        getGameWorld().addEntity(entity);
    }

    private Rectangle getMarker(double x, double y) {
        Rectangle rect = new Rectangle(40, 40, null);
        rect.setTranslateX(x);
        rect.setTranslateY(y);
        rect.setStroke(Color.RED);
        return rect;
    }

    @Required(PositionComponent.class)
    public class PlayerControl extends AbstractControl {

        private PositionComponent position;

        @Override
        public void onAdded(Entity entity) {
            position = Entities.getPosition(entity);
        }

        private double speed = 0;

        @Override
        public void onUpdate(Entity entity, double tpf) {
            speed = tpf * 60;
        }

        public void up() {
            position.translateY(-5 * speed);
        }

        public void down() {
            position.translateY(5 * speed);
        }

        public void left() {
            position.translateX(-5 * speed);
        }

        public void right() {
            position.translateX(5 * speed);
        }
    }
}
