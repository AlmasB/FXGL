/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev

import com.almasb.fxgl.app.GameScene
import com.almasb.fxgl.app.ReadOnlyGameSettings
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getGameWorld
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.ui.FXGLCheckBox
import com.almasb.fxgl.ui.InGamePanel
import com.almasb.sslogger.Logger
import javafx.beans.binding.*
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import javafx.util.StringConverter

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DevPane(private val scene: GameScene, val settings: ReadOnlyGameSettings) {

    private val log = Logger.get(javaClass)

    private val panel = InGamePanel(350.0, scene.height)

    private val debugMessagesBox = VBox(5.0)

    private val entities = FXCollections.observableArrayList<Entity>()

    val isOpen: Boolean
        get() = panel.isOpen

    init {
        panel.styleClass.add("dev-pane")

        val acc =  Accordion(
                TitledPane("Dev vars", createContentDevVars()),
                TitledPane("Game vars", createContentGameVars()),
                TitledPane("Entities", createContentEntities())
        )
        acc.prefWidth = 340.0

        val scroll = ScrollPane(acc)
        scroll.setPrefSize(350.0, scene.height)

        panel.children += scroll

        debugMessagesBox.isMouseTransparent = true
        debugMessagesBox.background = Background(BackgroundFill(Color.color(0.7, 0.6, 0.7, 0.6), null, null))

        scene.addUINodes(panel, debugMessagesBox)
    }

    private val debugPoints = hashMapOf<Point2D, Node>()

    /**
     * Adds a point view at the given point.
     */
    fun addDebugPoint(p: Point2D) {
        val n = Circle(0.0, 0.0, 5.0, Color.color(0.0, 0.0, 1.0, 0.15))
        n.stroke = Color.RED

        val group = VBox(n, Text("${p.x}, ${p.y}"))
        group.alignment = Pos.TOP_LEFT
        group.translateX = p.x - n.radius
        group.translateY = p.y - n.radius

        scene.root.children += group

        debugPoints[p] = group
    }

    /**
     * Removes the view for the given point.
     */
    fun removeDebugPoint(p: Point2D) {
        debugPoints.remove(p)?.let { scene.root.children -= it }
    }

    fun pushMessage(message: String) {
        log.debug(message)

        val text = Text(message)
        text.font = Font.font(18.0)
        text.fill = Color.BLACK
        text.stroke = Color.BLACK

        debugMessagesBox.children += text

        FXGL.getEngineTimer().runOnceAfter({ debugMessagesBox.children.removeAt(0) }, Duration.seconds(5.0))
    }

    private fun createContentDevVars(): Pane {
        val vbox = VBox()
        vbox.padding = Insets(15.0)
        vbox.alignment = Pos.TOP_CENTER

        val pane = GridPane()
        pane.hgap = 25.0
        pane.vgap = 10.0

        settings.javaClass.declaredMethods
                .filter { it.name.startsWith("dev") }
                .sortedBy { it.name }
                .forEachIndexed { index, method ->

                    when (method.returnType) {
                        SimpleBooleanProperty::class.java -> {
                            val text = FXGL.getUIFactory().newText(method.name, Color.WHITE, 18.0)
                            val checkBox = FXGLCheckBox()

                            checkBox.selectedProperty().bindBidirectional(method.invoke(settings) as SimpleBooleanProperty)

                            pane.addRow(index, text, checkBox)
                        }

                        SimpleObjectProperty::class.java -> {
                            if (method.name.toLowerCase().contains("color")) {
                                val text = FXGL.getUIFactory().newText(method.name, Color.WHITE, 18.0)
                                val colorPicker = ColorPicker()

                                colorPicker.valueProperty().bindBidirectional(method.invoke(settings) as SimpleObjectProperty<Color>)

                                pane.addRow(index, text, colorPicker)
                            }

                        }
                        else -> {}
                    }
                }

        vbox.children.add(pane)

        return vbox
    }

    private fun createContentGameVars(): Parent {
        val vbox = VBox()
        vbox.padding = Insets(15.0)
        vbox.alignment = Pos.TOP_CENTER

        val pane = GridPane()
        pane.hgap = 25.0
        pane.vgap = 10.0

        FXGL.getGameState().properties.keys().forEachIndexed { index, key ->
            val textKey = FXGL.getUIFactory().newText(key, Color.WHITE, 18.0)

            val value = FXGL.getGameState().properties.getValueObservable(key)
            val textValue = FXGL.getUIFactory().newText("", Color.WHITE, 18.0)

            when (value.javaClass) {
                SimpleBooleanProperty::class.java -> {
                    textValue.textProperty().bind((value as SimpleBooleanProperty).asString())
                }

                SimpleIntegerProperty::class.java -> {
                    textValue.textProperty().bind((value as SimpleIntegerProperty).asString())
                }

                SimpleDoubleProperty::class.java -> {
                    textValue.textProperty().bind((value as SimpleDoubleProperty).asString())
                }

                SimpleStringProperty::class.java -> {
                    textValue.textProperty().bind((value as SimpleStringProperty))
                }

                SimpleObjectProperty::class.java -> {
                    textValue.textProperty().bind((value as SimpleObjectProperty<*>).asString())
                }

                else -> {
                    throw IllegalArgumentException("Unknown value type: ${value.javaClass}")
                }
            }

            pane.addRow(index, textKey, textValue)
        }

        vbox.children.add(pane)

        return vbox
    }

    private fun createContentEntities(): Parent {
        val vbox = VBox()
        vbox.padding = Insets(15.0)
        vbox.alignment = Pos.TOP_LEFT

        getGameWorld().addWorldListener(object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {
                entities += entity
            }

            override fun onEntityRemoved(entity: Entity) {
                entities -= entity
            }
        })

        entities.addAll(getGameWorld().entities)

        val innerBox = VBox(5.0)
        innerBox.padding = Insets(15.0)
        innerBox.alignment = Pos.TOP_LEFT

        val choiceBox = ChoiceBox(entities)
        choiceBox.converter = object : StringConverter<Entity>() {
            override fun fromString(string: String): Entity {
                throw AssertionError("Not supposed to be called")
            }

            override fun toString(entity: Entity): String {
                return entity.type.toString()
            }
        }
        choiceBox.prefWidth = 260.0

        val highlight = Rectangle(0.0, 0.0, null)
        highlight.strokeWidth = 3.0
        highlight.stroke = Color.LIGHTGOLDENRODYELLOW

        choiceBox.selectionModel.selectedItemProperty().addListener { _, old, entity ->

            old?.let {
                it.viewComponent.removeChild(highlight)
            }

            entity?.let {
                innerBox.children.clear()

                highlight.width = it.width
                highlight.height = it.height

                // highlight selected entity
                it.viewComponent.addChild(highlight)

                it.components.sortedBy { it.javaClass.simpleName }
                        .forEach { comp ->
                    val pane = GridPane()
                    pane.hgap = 25.0
                    pane.vgap = 10.0

                    var index = 0

                    val title = FXGL.getUIFactory().newText(comp.javaClass.simpleName.removeSuffix("Component"), Color.ANTIQUEWHITE, 22.0)

                    pane.addRow(index++, title)
                    pane.addRow(index++, Rectangle(165.0, 2.0, Color.ANTIQUEWHITE))

                    comp.javaClass.methods
                            .filter { it.name.endsWith("Property") }
                            .sortedBy { it.name }
                            .forEach { method ->

                                val textKey = FXGL.getUIFactory().newText(method.name.removeSuffix("Property"), Color.WHITE, 18.0)

                                val value = method.invoke(comp)
                                val textValue = FXGL.getUIFactory().newText("", Color.WHITE, 18.0)

                                when (value) {
                                    is BooleanExpression -> {
                                        textValue.textProperty().bind(value.asString())
                                    }

                                    is IntegerExpression -> {
                                        textValue.textProperty().bind(value.asString())
                                    }

                                    is DoubleExpression -> {
                                        textValue.textProperty().bind(value.asString("%.2f"))
                                    }

                                    is StringExpression -> {
                                        textValue.textProperty().bind(value)
                                    }

                                    is ObjectExpression<*> -> {
                                        textValue.textProperty().bind(value.asString())
                                    }

                                    is ObservableList<*> -> {
                                        // TODO:
                                    }

                                    else -> {
                                        throw IllegalArgumentException("Unknown value type: ${value.javaClass}")
                                    }
                                }

                                pane.addRow(index++, textKey, textValue)
                            }

                    pane.addRow(index++, Text(""))

                    innerBox.children += pane
                }
            }
        }

        if (entities.isNotEmpty()) {
            choiceBox.selectionModel.selectFirst()
        }

        vbox.children.addAll(choiceBox, innerBox)

        return vbox
    }

    fun open() {
        panel.open()
    }

    fun close() {
        panel.close()
    }
}