/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d.firingrange;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point3D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Projectile3DComponent extends Component {

    private Point3D direction;
    private double speed;

    private Point3D velocity;

    public Projectile3DComponent(Point3D direction, double speed) {
        this.direction = direction;
        this.speed = speed;

        velocity = direction.normalize().multiply(speed);
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translate3D(velocity.multiply(tpf));
    }
}
