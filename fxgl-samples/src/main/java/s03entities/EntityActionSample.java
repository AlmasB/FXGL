/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.extra.entity.action.Action;
import com.almasb.fxgl.extra.entity.action.ActionControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
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






    }

    private Entity entity;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Now") {
            @Override
            protected void onActionBegin() {

                entity.getComponent(ActionControl.class).clearActions();

                entity.getComponent(ActionControl.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.PRIMARY);

        getInput().addAction(new UserAction("Queue Move Action") {
            @Override
            protected void onActionBegin() {
                entity.getComponent(ActionControl.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.SECONDARY);

        getInput().addAction(new UserAction("Remove Current Action") {
            @Override
            protected void onActionBegin() {
                Action<?> a = actionsView.getSelectionModel().getSelectedItem();
                if (a != null) {
                    entity.getComponent(ActionControl.class).removeAction(a);
                }
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGame() {
        entity = Entities.builder()
                .at(400, 300)
                .viewFromNode(new Rectangle(40, 40))
                .with(new ActionControl<Entity>())
                .buildAndAttach(getGameWorld());
    }

    private ListView<Action<Entity>> actionsView;

    @Override
    protected void initUI() {
        actionsView = getUIFactory().newListView(entity.getComponent(ActionControl.class)
                .actionsProperty());

//        actionsView.getSelectionModel().selectedItemProperty().addListener((o, old, newValue) -> {
//            if (newValue != null) {
//                entity.getComponent(ActionControl.class).removeAction(newValue);
//            }
//        });

        getGameScene().addUINode(actionsView);
    }

    private class MoveAction extends Action<Entity> {

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
        public void onUpdate(Entity e, double tpf) {
            speed = 150 * tpf;

            entity.translateTowards(new Point2D(x, y), speed);
        }

        @Override
        public String toString() {
            return "Move(" + x + "," + y + ")";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
