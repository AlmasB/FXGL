/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.CopyableComponent;

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
}
