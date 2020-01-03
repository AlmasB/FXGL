/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This is needed since, apparently, Java classes have slightly different behavior
 * compared to Kotlin classes in terms of reflection.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JavaComponentsTest {

    private Entity entity;

    @BeforeEach
    public void setUp() {
        entity = new Entity();
    }

    @Test
    public void testAnonymousComponent() {
        assertThrows(IllegalArgumentException.class, () -> entity.addComponent(new Component() {}));
    }

    @Test
    public void testAnonymousControl() {
        assertThrows(IllegalArgumentException.class, () -> entity.addComponent(new Component() {
                @Override
                public void onUpdate(double tpf) { }
        }));
    }

    @Test
    public void testRequiredPartial() {
        assertThrows(IllegalStateException.class, () -> entity.addComponent(new RComponent()));

        entity.addComponent(new AComponent());

        assertThrows(IllegalStateException.class, () -> entity.addComponent(new RComponent()));

        entity.addComponent(new BComponent());

        assertDoesNotThrow(() -> entity.addComponent(new RComponent()));
    }

    private static class AComponent extends Component { }
    private static class BComponent extends Component { }

    @Required(AComponent.class)
    @Required(BComponent.class)
    private static class RComponent extends Component { }
}
