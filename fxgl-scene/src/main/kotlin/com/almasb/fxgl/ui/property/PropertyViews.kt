/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui.property

import com.almasb.fxgl.core.math.Vec2
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.DoubleExpression
import javafx.beans.binding.IntegerExpression
import javafx.beans.binding.StringExpression
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableStringValue
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.util.converter.DoubleStringConverter
import javafx.util.converter.IntegerStringConverter

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DoublePropertyView(property: ObservableDoubleValue) : TextField() {

    init {
        // TODO: any other way to check if read-only?
        if (!property.javaClass.canonicalName.contains("ReadOnlyDoubleWrapper")) {
            textProperty().bindBidirectional(property as Property<Double>, DoubleStringConverter())
        } else {
            textProperty().bind((property as DoubleExpression).asString())

            isDisable = true
        }
    }
}

class IntPropertyView(property: ObservableIntegerValue) : TextField() {

    init {
        if (!property.javaClass.canonicalName.contains("ReadOnlyIntegerWrapper")) {
            textProperty().bindBidirectional(property as Property<Int>, IntegerStringConverter())
        } else {
            textProperty().bind((property as IntegerExpression).asString())

            isDisable = true
        }
    }
}

class BooleanPropertyView(property: ObservableBooleanValue) : CheckBox() {

    init {
        if (!(property.javaClass.canonicalName?.contains("ReadOnlyBooleanWrapper") ?: true)) {
            selectedProperty().bindBidirectional(property as BooleanProperty)
        } else {
            selectedProperty().bind(property as BooleanExpression)

            isDisable = true
        }
    }
}

class StringPropertyView(property: ObservableStringValue) : TextField() {

    init {
        if (!property.javaClass.canonicalName.contains("ReadOnlyStringWrapper")) {
            textProperty().bindBidirectional(property as Property<String>)
        } else {
            textProperty().bind(property as StringExpression)

            isDisable = true
        }
    }
}

class Vec2PropertyView(property: ObjectProperty<Vec2>) : Parent() {

    init {
        children += Vec2PropertyViewChangeListener().makeViewInternal(property)

        // TODO: handle read-only version
    }
}
