/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.FollowComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.action.ContinuousAction;
import com.almasb.fxgl.entity.action.InstantAction;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.UserAction;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGL.texture;

/**
 * Shows how to make entities perform actions.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityActionSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidthFromRatio(16/9.0);
    }

    private Entity entity;
    private Entity mover, mover2;

    private Entity unit, unit2;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Now") {
            @Override
            protected void onActionBegin() {

                entity.getComponent(ActionComponent.class).cancelActions();

                entity.getComponent(ActionComponent.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.SECONDARY);

        getInput().addAction(new UserAction("Queue Move Action") {
            @Override
            protected void onActionBegin() {
                entity.getComponent(ActionComponent.class)
                        .addAction(new MoveAction(getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        }, MouseButton.PRIMARY);

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
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        mover = entityBuilder()
                .at(400, 20)
                .viewWithBBox(texture("player2rot.png").multiplyColor(Color.RED))
                .with(new ProjectileComponent(new Point2D(0, 1), 200))
                .onClick(e -> {
                    entity.getComponent(ActionComponent.class)
                            .addAction(new FollowAction(mover));
                })
                .buildAndAttach();

        mover2 = entityBuilder()
                .at(500, 200)
                .viewWithBBox(texture("player2rot.png").multiplyColor(Color.BLUE))
                .with(new ProjectileComponent(new Point2D(1, 0), 250))
                .onClick(e -> {
                    entity.getComponent(ActionComponent.class)
                            .addAction(new FollowAction(mover2));
                })
                .buildAndAttach();

        entity = entityBuilder()
                .at(400, 300)
                .viewWithBBox("player2rot.png")
                .with(new ActionComponent())
                .with(new FollowComponent(null, 150, 40, 100))
                .with(new FixRotateComponent())
                .buildAndAttach();

        unit = entityBuilder()
                .at(700, 400)
                .viewWithBBox(new Rectangle(40, 40, Color.GREEN))
                .with(new FollowComponent(null, 100, 30, 50))
                .onClick(e -> {
                    entity.getComponent(ActionComponent.class)
                            .addAction(new RecruitAction(unit));
                })
                .buildAndAttach();

//        unit2 = entityBuilder()
//                .at(700, 200)
//                .view(new Rectangle(40, 40, Color.GREEN))
//                .with(new FollowComponent(null, 50, 60, 130))
//                .onClick(() -> {
//                    entity.getComponent(ActionComponent.class)
//                            .addAction(new RecruitAction(unit2));
//                })
//                .buildAndAttach();
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

    @Override
    protected void onUpdate(double tpf) {
        if (mover.getBottomY() > getAppHeight()) {
            mover.getComponent(ProjectileComponent.class).setDirection(new Point2D(0, -1));
        } else if (mover.getY() < 0) {
            mover.getComponent(ProjectileComponent.class).setDirection(new Point2D(0, 1));
        }

        if (mover2.getRightX() > getAppWidth()) {
            mover2.getComponent(ProjectileComponent.class).setDirection(new Point2D(-1, 0));
        } else if (mover2.getX() < 450) {
            mover2.getComponent(ProjectileComponent.class).setDirection(new Point2D(1, 0));
        }
    }

    public static class FixRotateComponent extends Component {

        private Point2D prev;

        @Override
        public void onAdded() {
            prev = entity.getPosition();
        }

        @Override
        public void onUpdate(double tpf) {
            var vector = entity.getPosition().subtract(prev);

            var now = entity.getRotation();
            var next = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

            entity.setRotation(now * 0.9 + next * 0.1);

            prev = entity.getPosition();
        }

        @Override
        public boolean isComponentInjectionRequired() {
            return false;
        }
    }

    private static class MoveAction extends Action {

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

    private static class FollowAction extends ContinuousAction {

        private Entity target;

        public FollowAction(Entity target) {
            this.target = target;
        }

        @Override
        protected void onStarted() {
            entity.getComponent(FollowComponent.class).resume();
        }

        @Override
        protected void perform(double tpf) {
            entity.getComponent(FollowComponent.class).setTarget(target);
        }

        @Override
        protected void onCancelled() {
            entity.getComponent(FollowComponent.class).pause();
        }

        @Override
        public String toString() {
            return "Follow";
        }
    }


    private static class RecruitAction extends InstantAction {

        private Entity target;

        public RecruitAction(Entity target) {
            this.target = target;
        }

        @Override
        protected void performOnce(double tpf) {
            if (target.distance(entity) < 50) {
                target.getComponent(FollowComponent.class).setTarget(entity);
            }
        }

        @Override
        public String toString() {
            return "Recruit";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
