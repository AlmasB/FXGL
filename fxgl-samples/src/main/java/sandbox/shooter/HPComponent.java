/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.shooter;

import com.almasb.fxgl.entity.component.IntegerComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class HPComponent extends IntegerComponent {
    public HPComponent(int hp) {
        super(hp);
    }

    public void increment(int value) {
        setValue(getValue() + value);
    }

    public void decrement(int value) {
        increment(-value);
    }
}
