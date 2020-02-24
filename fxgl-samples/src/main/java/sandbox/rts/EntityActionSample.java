/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.input.UserAction;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to make entities perform actions.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityActionSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    private Entity entity;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Now") {
            @Override
            protected void onActionBegin() {

                entity.getComponent(ActionComponent.class).cancelActions();

                entity.getComponent(ActionComponent.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.PRIMARY);

        getInput().addAction(new UserAction("Queue Move Action") {
            @Override
            protected void onActionBegin() {
                entity.getComponent(ActionComponent.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.SECONDARY);

        getInput().addAction(new UserAction("Remove Current Action") {
            @Override
            protected void onActionBegin() {
                Action a = actionsView.getSelectionModel().getSelectedItem();
                if (a != null) {
                    entity.getComponent(ActionComponent.class).removeAction(a);
                }
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGame() {
        entity = entityBuilder()
                .at(400, 300)
                .view(new Rectangle(40, 40))
                .with(new ActionComponent())
                .buildAndAttach();
    }

    private ListView<Action> actionsView;

    @Override
    protected void initUI() {
        actionsView = getUIFactoryService().newListView(entity.getComponent(ActionComponent.class)
                .actionsProperty());

//        actionsView.getSelectionModel().selectedItemProperty().addListener((o, old, newValue) -> {
//            if (newValue != null) {
//                entity.getComponent(ActionComponent.class).removeAction(newValue);
//            }
//        });

        getGameScene().addUINode(actionsView);
    }

    private class MoveAction extends Action {

        private double x, y;
        private double speed;

        public MoveAction(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void onUpdate(double tpf) {
            speed = 150 * tpf;

            entity.translateTowards(new Point2D(x, y), speed);

            if (getEntity().getPosition().distance(x, y) < speed)
                setComplete();
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
