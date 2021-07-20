/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.CopyableComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomComponent extends Component implements CopyableComponent<CustomComponent> {

    @Override
    public void onUpdate(double tpf) {
        entity.translate(75 * tpf, 50 * tpf);
    }

    @Override
    public CustomComponent copy() {
        return new CustomComponent();
    }

    public void exampleMethod() {
        FXGL.entityBuilder()
                .at(200, 200)
                .view(new Rectangle(60, 40, Color.RED))
                .with(new ProjectileComponent(new Point2D(1, 0), 150))
                .with(new OffscreenCleanComponent())
                .buildAndAttach();
    }
}
