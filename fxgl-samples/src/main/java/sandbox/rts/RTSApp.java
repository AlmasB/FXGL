/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.action.ContinuousAction;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RTSApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL RTS Game");
        settings.setClickFeedbackEnabled(true);
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {
            getGameWorld().getEntities().forEach(e -> e.getComponent(ActionComponent.class).addAction(new MoveAction(
                    getInput().getMouseXWorld(), getInput().getMouseYWorld()
            )));
        });
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new RTSFactory());

        spawn("unit", 300, 300);
    }

    public static class MoveAction extends ContinuousAction {

        private double x;
        private double y;

        public MoveAction(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        protected void perform(double tpf) {
            entity.translateTowards(new Point2D(x, y), tpf * 50);

            if (entity.getPosition().distance(x, y) < tpf * 50) {
                setComplete();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
