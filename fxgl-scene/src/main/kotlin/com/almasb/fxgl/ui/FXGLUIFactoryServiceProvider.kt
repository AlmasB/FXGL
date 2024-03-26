/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.ui.FontType.UI
import com.almasb.fxgl.ui.property.*
import javafx.beans.binding.*
import javafx.beans.property.*
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.util.concurrent.Callable

/**
 * FXGL provider of UI factory service.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLUIFactoryServiceProvider : UIFactoryService() {

    private val log = Logger.get(javaClass)

    private val fontFactories = hashMapOf<FontType, ObjectProperty<FontFactory>>()
    private val propertyViewFactories = hashMapOf<Class<*>, PropertyViewFactory<*, *>>()

    init {
        FontType.values().forEach { fontType ->
            fontFactories[fontType] = SimpleObjectProperty(FontFactory(Font.font(18.0)))
        }

        propertyViewFactories[Vec2::class.java] = Vec2PropertyViewFactory()
        propertyViewFactories[Color::class.java] = ColorPropertyViewFactory()
        propertyViewFactories[Point2D::class.java] = Point2DPropertyViewFactory()
    }

    override fun registerFontFactory(type: FontType, fontFactory: FontFactory) {
        fontFactories[type]?.value = fontFactory
    }

    override fun newText(textBinding: StringExpression): Text {
        val text = newText(textBinding.get())
        text.textProperty().bind(textBinding)
        return text
    }

    override fun newText(message: String, fontSize: Double): Text {
        return newText(message, Color.WHITE, fontSize)
    }

    override fun newText(message: String): Text {
        return newText(message, Color.WHITE, 18.0)
    }

    override fun newText(message: String, textColor: Color, fontSize: Double): Text {
        return newText(message, textColor, UI, fontSize)
    }

    override fun newText(message: String, textColor: Color, type: FontType, fontSize: Double): Text {
        val text = Text(message)
        text.fill = textColor
        text.fontProperty().bind(fontProperty(type, fontSize))
        return text
    }

    override fun newTextFlow(): FXGLTextFlow {
        return FXGLTextFlow(this)
    }

    override fun newWindow(): MDIWindow {
        return MDIWindow()
    }

    override fun newFont(size: Double): Font {
        return newFont(UI, size)
    }

    override fun newFont(type: FontType, size: Double): Font {
        val font = fontFactories[type]?.value?.newFont(size)

        if (font != null) {
            return font
        }

        log.warning("No font factory found for $type. Using default")

        return Font.font(size)
    }
    
    override fun newButton(text: String): Button {
        return FXGLButton(text).also {
            it.fontProperty().bind(fontProperty(UI, 22.0))
        }
    }

    override fun newButton(text: StringBinding): Button {
        val btn = newButton(text.value)
        btn.textProperty().bind(text)
        return btn
    }

    override fun <T> newChoiceBox(items: ObservableList<T>): ChoiceBox<T> {
        return FXGLChoiceBox(items)
    }

    override fun <T> newChoiceBox(): ChoiceBox<T> {
        return FXGLChoiceBox()
    }

    override fun newCheckBox(): CheckBox {
        return FXGLCheckBox()
    }

    override fun newSlider(): Slider {
        return FXGLSlider()
    }

    override fun <T> newSpinner(items: ObservableList<T>): Spinner<T> {
        return FXGLSpinner(items).also {
            it.editor.fontProperty().bind(fontProperty(UI, 18.0))
        }
    }

    override fun <T : Any> newListView(items: ObservableList<T>): ListView<T> {
        return FXGLListView(items)
    }

    override fun <T : Any> newListView(): ListView<T> {
        return FXGLListView<T>()
    }

    override fun newPropertyView(propertyName: String, property: Any): Node {
        val text = Text(propertyName).also {
            it.fill = Color.WHITE
            it.wrappingWidth = 100.0
        }

        val view: Node = when (property) {
            is ReadOnlyDoubleProperty -> DoublePropertyView(property)
            is DoubleBinding -> DoublePropertyView(property)

            is ReadOnlyIntegerProperty -> IntPropertyView(property)
            is IntegerBinding -> IntPropertyView(property)

            is ReadOnlyBooleanProperty -> BooleanPropertyView(property)
            is BooleanBinding -> BooleanPropertyView(property)

            is ReadOnlyStringProperty -> StringPropertyView(property)
            is StringBinding -> StringPropertyView(property)

            is ObjectProperty<*> -> {
                if (property.get().javaClass in propertyViewFactories) {
                    val converter = propertyViewFactories[property.get().javaClass]!!

                    converter.makeViewInternal(property)
                } else {

                    if (property.get().javaClass.isEnum) {
                        EnumPropertyView(property as ObjectProperty<Enum<*>>)
                    } else {
                        Text("Not supported ObjectProperty<?>: ${property.get().javaClass}").also { it.fill = Color.WHITE }
                    }
                }
            }

            else -> Text("Not supported property type: $property").also { it.fill = Color.WHITE }
        }

        return HBox(10.0,
                StackPane(text).also {
                    it.prefWidth = 100.0
                    it.alignment = Pos.CENTER_LEFT
                },
                StackPane(view).also {
                    it.prefWidth = 80.0
                }
        )
    }

    override fun newPropertyMapView(map: PropertyMap): Node {
        val vbox = VBox(5.0)

        map.forEachObservable { key, property ->
            val node = newPropertyView(key, property)

            vbox.children += node
        }

        return vbox
    }

    private fun fontProperty(type: FontType, fontSize: Double) =
            Bindings.createObjectBinding(Callable {
                return@Callable fontFactories[type]!!.value.newFont(fontSize)
            }, fontFactories[type]!!)
}