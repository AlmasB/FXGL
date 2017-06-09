/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

import com.almasb.fxgl.ecs.component.BooleanComponent;
import com.almasb.fxgl.ecs.component.DoubleComponent;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.ecs.serialization.SerializableComponent;
import com.almasb.fxgl.ecs.serialization.SerializableControl;
import com.almasb.fxgl.io.serialization.Bundle;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
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

        boolean result = entity.removeControl(TestControl.class);
        assertFalse(entity.getControl(TestControl.class).isPresent());
        assertTrue(result);

        entity.addControl(control);
        maybe = entity.getControl(TestControl.class);
        assertTrue(maybe.isPresent());
        assertEquals(control, maybe.get());

        entity.removeAllControls();
        assertFalse(entity.getControl(TestControl.class).isPresent());

        result = entity.removeControl(TestControl.class);
        assertFalse(result);
    }

    @Test
    public void testGetControl() {
        TestControl control = new TestControl();
        entity.addControl(control);

        assertEquals(control, entity.getControl(TestControl.class).get());
        assertEquals(control, entity.getControlUnsafe(TestControl.class));
    }

    @Test
    public void testGetControls() {
        TestControl control = new TestControl();
        ControlAddingControl control2 = new ControlAddingControl();
        ControlRemovingControl control3 = new ControlRemovingControl();

        entity.addControl(control);
        entity.addControl(control2);
        entity.addControl(control3);

        assertThat(entity.getControls(), hasItems(control, control2, control3));
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

        boolean result = entity.removeComponent(HPComponent.class);
        assertTrue(result);
        assertFalse(entity.getComponent(HPComponent.class).isPresent());

        entity.addComponent(hp);
        maybe = entity.getComponent(HPComponent.class);
        assertTrue(maybe.isPresent());
        assertEquals(hp, maybe.get());

        entity.removeAllComponents();
        assertFalse(entity.getComponent(HPComponent.class).isPresent());

        result = entity.removeComponent(HPComponent.class);
        assertFalse(result);
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
    public void testComponentListener() {
        HPComponent hp = new HPComponent(20);

        ModuleListener listener = new ModuleListener() {
            @Override
            public void onAdded(Component component) {
                assertEquals(HPComponent.class, component.getClass());

                ((HPComponent)component).setValue(10);
            }

            @Override
            public void onRemoved(Component component) {
                assertEquals(HPComponent.class, component.getClass());

                ((HPComponent)component).setValue(0);
            }
        };

        entity.addModuleListener(listener);

        entity.addComponent(hp);
        assertThat(hp.getValue(), is(10.0));

        entity.removeComponent(HPComponent.class);
        assertThat(hp.getValue(), is(0.0));

        entity.removeModuleListener(listener);

        entity.addModuleListener(listener);
        assertThat(hp.getValue(), is(0.0));
    }

    @Test
    public void testControlListener() {
        HPControl control = new HPControl();

        ModuleListener listener = new ModuleListener() {
            @Override
            public void onAdded(Control control) {
                assertEquals(HPControl.class, control.getClass());

                ((HPControl)control).value = 10;
            }

            @Override
            public void onRemoved(Control control) {
                assertEquals(HPControl.class, control.getClass());

                ((HPControl)control).value = 20;
            }
        };

        entity.addModuleListener(listener);
        entity.addComponent(new HPComponent(33));

        entity.addControl(control);
        assertEquals(10, control.value, 0);

        entity.removeControl(HPControl.class);
        assertEquals(20, control.value, 0);

        entity.removeModuleListener(listener);

        entity.addControl(control);
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

        assertFalse(entity.activeProperty().get());

        entity.setOnActive(() -> hp.value = 30.0);
        assertThat(hp.value, is(0.0));

        GameWorld world = new GameWorld();
        world.addEntity(entity);
        assertThat(hp.value, is(30.0));
        assertTrue(entity.activeProperty().get());

        entity.setOnNotActive(() -> hp.value = -50.0);
        assertThat(hp.value, is(30.0));

        world.removeEntity(entity);
        assertThat(hp.value, is(-50.0));
        assertFalse(entity.activeProperty().get());

        entity.setOnNotActive(() -> hp.value = -33.0);
        assertThat(hp.value, is(-33.0));

        world.addEntity(entity);
        entity.setOnActive(() -> hp.value = 99.0);
        assertThat(hp.value, is(99.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPropertyKey() {
        entity.getProperty("no_key");
    }

    @Test
    public void testIntegrity() {
        int count = 0;

        GameWorld world = new GameWorld();

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

    @Test
    public void testRemoveFromWorld() {
        GameWorld world = new GameWorld();

        world.addEntity(entity);
        assertThat(world.getEntities(), hasItems(entity));

        entity.removeFromWorld();
        assertThat(world.getEntities(), not(hasItems(entity)));

        Entity ee = new Entity();
        ee.addControl(new EntityRemovingControl());

        world.addEntity(ee);
        world.onUpdate(0);

        assertThat(world.getEntities(), not(hasItems(ee)));
    }

    @Test
    public void testCopy() {
        entity.addComponent(new HPComponent(33));

        Entity e2 = entity.copy();

        assertThat(e2.hasComponent(HPComponent.class), is(true));
        assertThat(e2.getComponentUnsafe(HPComponent.class).getValue(), is(33.0));
    }

    @Test
    public void testToString() {
        HPComponent component = new HPComponent(33);
        HPControl control = new HPControl();

        entity.addComponent(component);
        entity.addControl(control);

        String toString = entity.toString();

        assertThat(toString, containsString(component.toString()));
        assertThat(toString, containsString(control.toString()));
    }

    private class TestControl extends Control {
        @Override
        public void onUpdate(Entity entity, double tpf) {}
    }

    private class ControlAddingControl extends Control {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            entity.addControl(new TestControl());
        }
    }

    private class ControlRemovingControl extends Control {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            entity.removeAllControls();
        }
    }

    private class EntityRemovingControl extends Control {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            entity.removeFromWorld();
        }
    }

    @Required(HPComponent.class)
    private class HPControl extends Control {
        private double value = 0;

        @Override
        public void onUpdate(Entity entity, double tpf) {}
    }

    private class TestComponent extends Component {
    }

    private class HPComponent extends DoubleComponent implements CopyableComponent<HPComponent> {
        HPComponent(double value) {
            super(value);
        }

        @Override
        public HPComponent copy() {
            return new HPComponent(getValue());
        }
    }

    @Required(TestComponent.class)
    @Required(HPComponent.class)
    private class ArmorComponent extends Component {
    }

    private class GravityComponent extends BooleanComponent {
        GravityComponent(boolean value) {
            super(value);
        }
    }

    public static class CustomDataComponent extends Component implements SerializableComponent {

        public String data;

        CustomDataComponent(String data) {
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

    public static class CustomDataControl extends Control implements SerializableControl {

        public String data;

        CustomDataControl(String data) {
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
