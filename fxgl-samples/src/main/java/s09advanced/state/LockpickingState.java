/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced.state;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.SubState;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.input.UserAction;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LockpickingState extends SubState {

    private LockpickView view = new LockpickView();
    private double angle;
    private double lockpickAngle;

    public LockpickingState() {

        angle = FXGLMath.random(359);

        System.out.println("Angle is: " + angle);

        view.setTranslateX(250);
        view.setTranslateY(250);

        getInput().addAction(new UserAction("Pick Lock") {
            private double between;

            @Override
            protected void onActionBegin() {
                Point2D vector = getInput().getVectorToMouse(new Point2D(450, 450)).normalize();

                lockpickAngle = Math.abs(vector.angle(1, 0) + (vector.getY() > 0 ? -360 : 0));




                between = vector.angle(Vec2.fromAngle(angle).toPoint2D());

                System.out.println("Between: " + between);

                if (between < 5) {
                    FXGL.getDisplay().showMessageBox("Picked!");
                }
            }

            @Override
            protected void onAction() {
                view.getKeyHole().setRotate(180 - between);

                //view.getKeyHole().setRotate(FXGLMath.map(360 - Math.abs(lockpickAngle - angle), 0, 360, 0, 180));
                //view.getKeyHole().setRotate(view.getKeyHole().getRotate() + 1);
            }

            @Override
            protected void onActionEnd() {
                view.getKeyHole().setRotate(0);
            }
        }, KeyCode.A);

        getChildren().add(view);
    }

    @Override
    public void onUpdate(double tpf) {
        double length = 300;

        Point2D vector = getInput().getVectorToMouse(new Point2D(450, 450)).normalize().multiply(length);

        view.getKey().setEndX(view.getKey().getStartX() + vector.getX());
        view.getKey().setEndY(view.getKey().getStartY() + vector.getY());
    }

    private static class LockpickView extends Pane {

        private Line key;
        private Line keyHole;

        public LockpickView() {
            setPrefSize(400, 400);

            Circle outer = new Circle(200, 200, 200);
            Circle inner = new Circle(190, 190, 190);
            inner.setTranslateX(10);
            inner.setTranslateY(10);

            outer.setFill(null);
            outer.setStroke(Color.BLACK);

            Circle indicator = new Circle(10, 10, 10, Color.BLUE);
            indicator.setTranslateX(200 - 10);
            indicator.setTranslateY(100);

            Rotate r = new Rotate(0, 10, 100);
            indicator.getTransforms().add(r);

            keyHole = new Line(200, 100, 200, 300);
            keyHole.setStroke(Color.AQUA);
            keyHole.setStrokeWidth(8);

            r.angleProperty().bind(getKeyHole().rotateProperty());

            key = new Line(200, 200, 200, 400);
            key.setStroke(Color.RED);
            key.setStrokeWidth(4);

            getChildren().addAll(outer, inner, keyHole, indicator, key);
        }

        public Line getKey() {
            return key;
        }

        public Line getKeyHole() {
            return keyHole;
        }
    }

    private static class Lockpick {
        private double health;

        void applyPressure() {
            health -= 0.016;
        }

        boolean isBroken() {
            return health <= 0;
        }
    }
}
