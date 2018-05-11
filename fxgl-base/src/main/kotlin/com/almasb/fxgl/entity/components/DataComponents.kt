package com.almasb.fxgl.entity.components

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.SerializableComponent
import com.almasb.fxgl.io.serialization.Bundle
import javafx.beans.property.*

/**
 * Represents a boolean value based component.
 *
 *
 * <pre>
 * Example:
 *
 * public class GravityComponent extends BooleanComponent {
 * public GravityComponent(boolean initialValue) {
 * super(initialValue);
 * }
 * }
 *
 * Entity player = ...
 * player.addComponent(new GravityComponent(true));
 *
 * boolean gravityEnabled = player.getComponent(GravityComponent.class).getValue();
 *
</pre> *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class BooleanComponent
/**
 * Constructs a boolean value component with given
 * initial value.
 *
 * @param initialValue initial value
 */
@JvmOverloads constructor(initialValue: Boolean = false) : Component(), SerializableComponent {
    private val property: BooleanProperty

    /**
     * @return value held by this component
     */
    /**
     * Set value of this component.
     *
     * @param value new value
     */
    var value: Boolean
        get() = property.get()
        set(value) = property.set(value)

    init {
        property = SimpleBooleanProperty(initialValue)
    }

    /**
     * @return value property
     */
    fun valueProperty(): BooleanProperty {
        return property
    }

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    override fun toString(): String {
        return javaClass.simpleName + "[value=" + value + "]"
    }
}

/**
 * Represents an int value based component.
 *
 *
 * <pre>
 * Example:
 *
 * public class MoneyComponent extends IntegerComponent {
 * public MoneyComponent(int initialValue) {
 * super(initialValue);
 * }
 * }
 *
 * Entity player = ...
 * player.addComponent(new MoneyComponent(5000));
 *
 * int money = player.getComponent(MoneyComponent.class).getValue();
 *
</pre> *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class IntegerComponent
/**
 * Constructs an int value component with given
 * initial value.
 *
 * @param initialValue the initial value
 */
@JvmOverloads constructor(initialValue: Int = 0) : Component(), SerializableComponent {
    private val property: IntegerProperty

    /**
     * @return value held by this component
     */
    /**
     * Set value to this component.
     *
     * @param value new value
     */
    var value: Int
        get() = property.get()
        set(value) = property.set(value)

    init {
        property = SimpleIntegerProperty(initialValue)
    }

    /**
     * @return value property
     */
    fun valueProperty(): IntegerProperty {
        return property
    }

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    override fun toString(): String {
        return javaClass.simpleName + "[value=" + value + "]"
    }
}

/**
 * Represents a double value based component.
 *
 *
 * <pre>
 * Example:
 *
 * public class AttackSpeedComponent extends DoubleComponent {
 * public AttackSpeedComponent(double initialValue) {
 * super(initialValue);
 * }
 * }
 *
 * Entity player = ...
 * player.addComponent(new AttackSpeedComponent(1.75));
 *
 * double attackSpeed = player.getComponent(AttackSpeedComponent.class).getValue();
 *
</pre> *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class DoubleComponent
/**
 * Constructs a double value component with given
 * initial value.
 *
 * @param initialValue initial value
 */
@JvmOverloads constructor(initialValue: Double = 0.0) : Component(), SerializableComponent {
    private val property: DoubleProperty

    /**
     * @return value held by this component
     */
    /**
     * Set value to this component.
     *
     * @param value new value
     */
    var value: Double
        get() = property.get()
        set(value) = property.set(value)

    init {
        property = SimpleDoubleProperty(initialValue)
    }

    /**
     * @return value property
     */
    fun valueProperty(): DoubleProperty {
        return property
    }

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    override fun toString(): String {
        return javaClass.simpleName + "[value=" + value + "]"
    }
}


// STRING

abstract class ObjectComponent<T>(initialValue: T) : Component() {
    private val property: ObjectProperty<T>

    var value: T
        get() = property.get()
        set(value) = property.set(value)

    init {
        property = SimpleObjectProperty(initialValue)
    }

    fun valueProperty(): ObjectProperty<T> {
        return property
    }

    override fun toString(): String {
        return javaClass.simpleName + "[value=" + value + "]"
    }
}
