/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.ecs;

import com.almasb.fxgl.ecs.component.BooleanComponent;
import com.almasb.fxgl.ecs.component.DoubleComponent;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.ecs.serialization.SerializableComponent;
import com.almasb.fxgl.ecs.serialization.SerializableControl;
import com.almasb.fxgl.io.serialization.Bundle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityTest {

    private Entity entity;

    @Before
    public void setUp() {
        entity = new Entity();
    }

    @Test
    public void testControls() {
        TestControl control = new TestControl();
        entity.addControl(control);

        Optional<TestControl> maybe = entity.getControl(TestControl.class);
        assertTrue(maybe.isPresent());
        assertEquals(control, maybe.get());

        entity.removeControl(TestControl.class);
        assertFalse(entity.getControl(TestControl.class).isPresent());

        entity.addControl(control);
        maybe = entity.getControl(TestControl.class);
        assertTrue(maybe.isPresent());
        assertEquals(control, maybe.get());

        entity.removeAllControls();
        assertFalse(entity.getControl(TestControl.class).isPresent());
    }

    @Test
    public void testGetControl() {
        TestControl control = new TestControl();
        entity.addControl(control);

        assertEquals(control, entity.getControl(TestControl.class).get());
        assertEquals(control, entity.getControlUnsafe(TestControl.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testControlsAnonymous() {
        entity.addControl(new Control() {
            @Override
            public void onAdded(Entity entity) {}

            @Override
            public void onUpdate(Entity entity, double tpf) {}

            @Override
            public void onRemoved(Entity entity) {}

            @Override
            public boolean isPaused() {
                return false;
            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testControlsDuplicate() {
        entity.addControl(new TestControl());
        entity.addControl(new TestControl());
    }

    @Test
    public void testComponents() {
        HPComponent hp = new HPComponent(100);
        entity.addComponent(hp);

        Optional<HPComponent> maybe = entity.getComponent(HPComponent.class);
        assertTrue(maybe.isPresent());
        assertEquals(hp, maybe.get());

        entity.removeComponent(HPComponent.class);
        assertFalse(entity.getComponent(HPComponent.class).isPresent());

        entity.addComponent(hp);
        maybe = entity.getComponent(HPComponent.class);
        assertTrue(maybe.isPresent());
        assertEquals(hp, maybe.get());

        entity.removeAllComponents();
        assertFalse(entity.getComponent(HPComponent.class).isPresent());
    }

    @Test
    public void testGetComponent() {
        TestComponent component = new TestComponent();
        entity.addComponent(component);

        assertEquals(component, entity.getComponent(TestComponent.class).get());
        assertEquals(component, entity.getComponentUnsafe(TestComponent.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testRequiredAllMissing() {
        entity.addComponent(new ArmorComponent());
    }

    @Test(expected = IllegalStateException.class)
    public void testRequiredOneMissing() {
        entity.addComponent(new TestComponent());
        entity.addComponent(new ArmorComponent());
    }

    @Test
    public void testRequired() {
        entity.addComponent(new TestComponent());
        entity.addComponent(new HPComponent(33));
        entity.addComponent(new ArmorComponent());

        assertTrue(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiredRemovedComponent() {
        entity.addComponent(new TestComponent());
        entity.addComponent(new HPComponent(33));
        entity.addComponent(new ArmorComponent());

        entity.removeComponent(HPComponent.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiredRemovedControl() {
        entity.addComponent(new HPComponent(33));
        entity.addControl(new HPControl());

        entity.removeComponent(HPComponent.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComponentsAnonymous() {
        entity.addComponent(new Component() {
            @Override
            public void onAdded(Entity entity) {}

            @Override
            public void onRemoved(Entity entity) {}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComponentsDuplicate() {
        HPComponent hp1 = new HPComponent(100);
        HPComponent hp2 = new HPComponent(135);

        entity.addComponent(hp1);
        entity.addComponent(hp2);
    }

    @Test
    public void testRemoveAllComponents() {
        entity.addComponent(new TestComponent());
        entity.addComponent(new HPComponent(33));

        assertTrue(entity.hasComponent(TestComponent.class));
        assertTrue(entity.hasComponent(HPComponent.class));

        entity.removeAllComponents();

        assertFalse(entity.hasComponent(TestComponent.class));
        assertFalse(entity.hasComponent(HPComponent.class));
    }

    @Test
    public void testRemoveAllControls() {
        entity.addComponent(new HPComponent(33));

        entity.addControl(new TestControl());
        entity.addControl(new HPControl());

        assertTrue(entity.hasControl(TestControl.class));
        assertTrue(entity.hasControl(HPControl.class));

        entity.removeAllControls();

        assertFalse(entity.hasControl(TestControl.class));
        assertFalse(entity.hasControl(HPControl.class));
    }

    @Test
    public void testConcurrentModificationControl() {
        entity.addControl(new ControlAddingControl());
        entity.update(0.017);
    }

    @Test(expected = IllegalStateException.class)
    public void testConcurrentModificationControl2() {
        entity.addControl(new TestControl());
        entity.addControl(new ControlRemovingControl());
        entity.update(0.017);
    }

    @Test
    public void testSetControlsEnabled() {
        entity.addControl(new ControlAddingControl());
        entity.setControlsEnabled(false);
        entity.update(0.017);
    }

    @Test
    public void testControlPause() {
        Control c = new ControlAddingControl();
        entity.addControl(c);

        c.pause();
        entity.update(0.017);
    }

    @Test
    public void testControlResume() {
        Control c = new ControlAddingControl();
        entity.addControl(c);

        c.pause();
        c.resume();
        entity.update(0.017);
    }

    @Test
    public void testAddComponentListener() {
        HPComponent hp = new HPComponent(20);

        entity.addComponentListener(new ComponentListener() {
            @Override
            public void onComponentAdded(Component component) {
                assertEquals(HPComponent.class, component.getClass());

                ((HPComponent)component).setValue(10);
            }

            @Override
            public void onComponentRemoved(Component component) {
                assertEquals(HPComponent.class, component.getClass());

                ((HPComponent)component).setValue(0);
            }
        });

        entity.addComponent(hp);
        Assert.assertEquals(10, hp.getValue(), 0);

        entity.removeComponent(HPComponent.class);
        Assert.assertEquals(0, hp.getValue(), 0);
    }

    @Test
    public void testAddControlListener() {
        HPControl control = new HPControl();

        entity.addControlListener(new ControlListener() {
            @Override
            public void onControlAdded(Control control) {
                assertEquals(HPControl.class, control.getClass());

                ((HPControl)control).value = 10;
            }

            @Override
            public void onControlRemoved(Control control) {
                assertEquals(HPControl.class, control.getClass());

                ((HPControl)control).value = 20;
            }
        });

        entity.addComponent(new HPComponent(33));

        entity.addControl(control);
        assertEquals(10, control.value, 0);

        entity.removeControl(HPControl.class);
        assertEquals(20, control.value, 0);
    }

    @Test
    public void testSave() {
        entity.addComponent(new GravityComponent(true));
        entity.addComponent(new CustomDataComponent("SerializationData"));
        entity.addControl(new CustomDataControl("SerializableControl"));

        Bundle bundle = new Bundle("Entity01234");
        entity.save(bundle);

        Entity entity2 = new Entity();
        entity2.addComponent(new GravityComponent(false));
        entity2.addComponent(new CustomDataComponent(""));
        entity2.addControl(new CustomDataControl(""));

        entity2.load(bundle);

        assertThat(entity2.getControlUnsafe(CustomDataControl.class).data, is("SerializableControl"));
        assertThat(entity2.getComponentUnsafe(CustomDataComponent.class).data, is("SerializationData"));
        assertThat(entity2.getComponentUnsafe(GravityComponent.class).getValue(), is(true));
    }

    @Test
    public void testSaveNoSuchComponent() {
        Bundle bundle = new Bundle("Entity01234");
        entity.save(bundle);

        Entity entity2 = new Entity();
        entity2.addComponent(new CustomDataComponent(""));

        // even if bundle has no such component coz entity1 did not have it
        // we do not fail, but log a warning
        entity2.load(bundle);
    }

    @Test
    public void testProperties() {
        entity.setProperty("hp", 30);
        assertThat(entity.getProperty("hp"), is(30));

        entity.setProperty("hp", true);
        assertThat(entity.getProperty("hp"), is(true));
    }

    @Test
    public void testActiveCallbacks() {
        HPControl hp = new HPControl();

        entity.setOnActive(() -> hp.value = 30.0);
        assertThat(hp.value, is(0.0));

        EntityWorld world = new EntityWorld();
        world.addEntity(entity);
        assertThat(hp.value, is(30.0));

        entity.setOnNotActive(() -> hp.value = -50.0);
        assertThat(hp.value, is(30.0));

        world.removeEntity(entity);
        assertThat(hp.value, is(-50.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPropertyKey() {
        entity.getProperty("no_key");
    }

    @Test
    public void testIntegrity() {
        int count = 0;

        EntityWorld world = new EntityWorld();

        world.addEntity(entity);
        world.removeEntity(entity);

        try {
            entity.addComponent(new HPComponent(23));
        } catch (IllegalStateException e) {
            count++;
        }

        assertThat(count, is(1));

        try {
            entity.removeFromWorld();
        } catch (IllegalStateException e) {
            count++;
        }

        assertThat(count, is(2));
    }

    private class TestControl extends AbstractControl {
        @Override
        public void onUpdate(Entity entity, double tpf) {}
    }

    private class ControlAddingControl extends AbstractControl {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            entity.addControl(new TestControl());
        }
    }

    private class ControlRemovingControl extends AbstractControl {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            entity.removeAllControls();
        }
    }

    @Required(HPComponent.class)
    private class HPControl extends AbstractControl {
        private double value = 0;

        @Override
        public void onUpdate(Entity entity, double tpf) {}
    }

    private class TestComponent extends AbstractComponent {
    }

    private class HPComponent extends DoubleComponent {
        public HPComponent(double value) {
            super(value);
        }
    }

    @Required(TestComponent.class)
    @Required(HPComponent.class)
    private class ArmorComponent extends AbstractComponent {
    }

    private class GravityComponent extends BooleanComponent {
        public GravityComponent(boolean value) {
            super(value);
        }
    }

    private class CustomDataComponent extends AbstractComponent implements SerializableComponent {

        private String data;

        public CustomDataComponent(String data) {
            this.data = data;
        }

        @Override
        public void write(Bundle bundle) {
            bundle.put("data", data);
        }

        @Override
        public void read(Bundle bundle) {
            data = bundle.get("data");
        }
    }

    private class CustomDataControl extends AbstractControl implements SerializableControl {

        private String data;

        public CustomDataControl(String data) {
            this.data = data;
        }

        @Override
        public void onUpdate(Entity entity, double tpf) {

        }

        @Override
        public void write(Bundle bundle) {
            bundle.put("data", data);
        }

        @Override
        public void read(Bundle bundle) {
            data = bundle.get("data");
        }
    }
}
