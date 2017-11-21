/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.collections.FXCollections
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.StringConverter

/**
 * API INCOMPLETE
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DeveloperPane : VBox(25.0), EntityWorldListener {

    private val cbEntity = ChoiceBox<Entity>()

    companion object numberStringConverter : StringConverter<Number>() {
        override fun toString(number: Number): String {
            return number.toString()
        }

        override fun fromString(string: String): Number {
            return try { string.toDouble() } catch(e: Exception) { 0.0 }
        }
    }

    init {
        prefWidth = 300.0
        prefHeight = FXGL.getSettings().height.toDouble() - 200

        background = Background(BackgroundFill(Color.GRAY.deriveColor(1.0, 1.0, 1.0, 0.5), null, null))

        cbEntity.selectionModel.selectedItemProperty()
                .addListener { obs, old, entity -> onEntitySelect(entity) }

        cbEntity.items = FXCollections.observableArrayList(FXGL.getApp().gameWorld.entities)

        children.add(cbEntity)
    }

    private fun onEntitySelect(entity: Entity) {
        children.retainAll(cbEntity)

        for (component in entity.components) {

            // add component name
            val labelComponent = Label(component.toString())
            labelComponent.font = Font.font("", FontWeight.BOLD, 24.0)
            children.add(labelComponent)

            // add component values
            component.javaClass.declaredMethods
                    .filter { it.isAnnotationPresent(DeveloperEditable::class.java) }
                    .forEach { method ->
                        val ann = method.getDeclaredAnnotation(DeveloperEditable::class.java)
                        val label = Label(ann.value)
                        val field = TextField()

                        val property = method.invoke(component)

                        when(property) {
                            is IntegerProperty -> Bindings.bindBidirectional(field.textProperty(), property, numberStringConverter)
                            is DoubleProperty -> Bindings.bindBidirectional(field.textProperty(), property, numberStringConverter)
                            else -> throw RuntimeException("Unknown property type: $property")
                        }

                        children.addAll(HBox(5.0, label, field))
                    }
        }
    }

    override fun onEntityAdded(entity: Entity) {
        cbEntity.items.add(entity)
    }

    override fun onEntityRemoved(entity: Entity) {
        cbEntity.items.remove(entity)
    }
}