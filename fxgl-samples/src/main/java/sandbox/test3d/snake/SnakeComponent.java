/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d.snake;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SnakeComponent extends Component {

    private Point3D next = new Point3D(0, 0, 0);
    private Point3D dir = new Point3D(1, 0, 0);

    @Override
    public void onAdded() {
        entity.getViewComponent().getParent().getChildrenUnmodifiable().get(0).translateXProperty().unbind();
        entity.getViewComponent().getParent().getChildrenUnmodifiable().get(0).translateYProperty().unbind();
        entity.getViewComponent().getParent().getChildrenUnmodifiable().get(0).translateZProperty().unbind();
    }

    @Override
    public void onUpdate(double tpf) {
        next = next.add(dir.multiply(tpf * 10));

        var box = entity.getViewComponent().getChildren().get(0);

        entity.getViewComponent().removeChild(box);

        box.setTranslateX(next.getX());
        box.setTranslateY(next.getY());
        box.setTranslateZ(next.getZ());

        entity.setPosition3D(next);

        entity.getViewComponent().addChild(box);
    }

    public void moveUp() {
        dir = new Point3D(0, -1, 0);
    }

    public void moveDown() {
        dir = new Point3D(0, 1, 0);
    }

    public void moveLeft() {
        dir = new Point3D(-1, 0, 0);
    }

    public void moveRight() {
        dir = new Point3D(1, 0, 0);
    }

    public void moveForward() {
        dir = new Point3D(0, 0, 1);
    }

    public void moveBack() {
        dir = new Point3D(0, 0, -1);
    }

    public void grow() {
        for (int i = 0; i < 5; i++) {
            Box box = new Box(1, 1, 1);
            box.setMaterial(new PhongMaterial(Color.BLUE));

            var pos = next.add(dir.multiply(FXGL.tpf() * 10));

            box.setTranslateX(pos.getX());
            box.setTranslateY(pos.getY());
            box.setTranslateZ(pos.getZ());

            entity.getViewComponent().addChild(box);
        }
    }
}
