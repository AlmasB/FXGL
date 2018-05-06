/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package manual;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.input.Input;
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

    private Entity player, markers;
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
    protected void initGame() {
        spawnEntity(100, 100);
        spawnEntity(500, 300);
        spawnEntity(300, 15);
        spawnEntity(400, 500);
        spawnEntity(500, 500);

        player = new Entity();
        player.getPositionComponent().setValue(400, 300);
        player.getViewComponent().setView(new EntityView(new Rectangle(40, 40)), true);

        playerControl = new PlayerControl();
        player.addComponent(playerControl);

        markers = new Entity();

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

            EntityView view = e.getComponent(ViewComponent.class).getView();
            view.getNodes()
                    .stream()
                    .map(n -> (Rectangle)n)
                    .forEach(r -> r.setFill(Color.YELLOW));
        });

        List<Entity> list2 = getGameWorld().getEntitiesCopy();
        list2.removeAll(list);

        list2.forEach(e -> {
            EntityView view = e.getComponent(ViewComponent.class).getView();
            view.getNodes()
                    .stream()
                    .map(n -> (Rectangle)n)
                    .forEach(r -> r.setFill(Color.BLACK));
        });
    }

    private void spawnEntity(double x, double y) {
        Entity entity = new Entity();
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
    public class PlayerControl extends Component {

        private PositionComponent position;

        private double speed = 0;

        @Override
        public void onUpdate(double tpf) {
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
