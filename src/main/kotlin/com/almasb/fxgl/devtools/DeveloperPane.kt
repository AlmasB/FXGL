/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.devtools

import com.almasb.ents.Component
import com.almasb.ents.Entity
import com.almasb.fxgl.app.FXGL
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.collections.FXCollections
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.StringConverter

/**
 * API INCOMPLETE
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DeveloperPane : Pane() {

    private val cbEntity = ChoiceBox<Entity>()
    private val cbComponent = ChoiceBox<Component>()
    private val propertyPane = GridPane()

    companion object numberStringConverter : StringConverter<Number>() {
        override fun toString(number: Number): String {
            // TODO: format?
            return number.toString()
        }

        override fun fromString(string: String): Number {
            return try { string.toDouble() } catch(e: Exception) { 0.0 }
        }
    }

    init {
        prefWidth = 300.0
        prefHeight = FXGL.getSettings().height.toDouble()
        translateX = FXGL.getSettings().width - 300.0

        background = Background(BackgroundFill(Color.GRAY.deriveColor(1.0, 1.0, 1.0, 0.5), null, null))

        cbEntity.selectionModel.selectedItemProperty()
                .addListener { obs, old, entity -> cbComponent.items = FXCollections.observableArrayList(entity.components)}

        cbComponent.selectionModel.selectedItemProperty()
                .addListener { obs, old, component ->

                    component.javaClass.declaredMethods
                            .filter { it.isAnnotationPresent(DeveloperEditable::class.java) }
                            .forEachIndexed { i, method ->
                                val ann = method.getDeclaredAnnotation(DeveloperEditable::class.java)
                                val label = Label(ann.value)
                                val field = TextField()

                                val property = method.invoke(component)

                                when(property) {
                                    is IntegerProperty -> Bindings.bindBidirectional(field.textProperty(), property, numberStringConverter)
                                    is DoubleProperty -> Bindings.bindBidirectional(field.textProperty(), property, numberStringConverter)
                                }
                                // TODO: unresolved else

                                propertyPane.addRow(i, label, field)
                            }
                }

        children.addAll(VBox(50.0, cbEntity, cbComponent, propertyPane))
    }

    // TODO: we should have databinds instead
    fun update() {
        cbEntity.items = FXCollections.observableArrayList(FXGL.getGame().gameWorld.entities)
    }
}