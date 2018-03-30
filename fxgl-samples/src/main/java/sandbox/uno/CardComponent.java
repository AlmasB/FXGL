/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.entity.components.ObjectComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CardComponent extends ObjectComponent<Card> {

    /**
     * Constructs an object value component with given
     * initial value.
     *
     * @param initialValue the initial value
     */
    public CardComponent(Card initialValue) {
        super(initialValue);
    }
}
