/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev.editor

import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CopyableComponent
import com.almasb.fxgl.ui.FXGLButton
import com.almasb.fxgl.ui.FXGLScrollPane
import javafx.beans.binding.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.FileChooser
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Paths


/**
 * TODO: how are going to modify each component data, e.g. ViewComponent add new view?
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityInspector : FXGLScrollPane() {

    private val innerBox = VBox(5.0)

    // TODO: experimental
    private val componentTypes = arrayListOf<Class<out Component>>(
            DevSpinComponent::class.java
    )

    private val addViewButton = FXGLButton("Add View")
    private val addComponentButton = FXGLButton("Add Component")
    private val addCustomComponentButton = FXGLButton("Add Custom Component")

    var entity: Entity? = null
        set(value) {
            field = value

            updateView()
        }

    init {
        background = Background(BackgroundFill(Color.BLACK, null, null))
        innerBox.background = Background(BackgroundFill(Color.BLACK, null, null))
        innerBox.padding = Insets(5.0)

        addViewButton.setOnAction {
            entity?.let {

                val viewComp = it.viewComponent

                val chooser = FileChooser()
                chooser.initialDirectory = File(System.getProperty("user.dir"))
                chooser.title = "Select image"
                chooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"))
                val file = chooser.showOpenDialog(null)

                file?.let {
                    viewComp.addChild(getAssetLoader().loadTexture(it.toURI().toURL()))
                }
            }
        }

        addComponentButton.setOnAction {
            entity?.let { selectedEntity ->
                val box = ComboBox(FXCollections.observableList(componentTypes))
                box.selectionModel.selectFirst()

                val dialog = Dialog<ButtonType>()
                dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
                dialog.dialogPane.content = box
                dialog.showAndWait().ifPresent {

                    if (it == ButtonType.OK) {
                        box.selectionModel.selectedItem?.let { item ->
                            val comp = ReflectionUtils.newInstance(item)

                            selectedEntity.addComponent(comp)
                        }
                    }
                }
            }
        }

        addCustomComponentButton.setOnAction {

//            val chooser = FileChooser()
//            chooser.initialDirectory = File(System.getProperty("user.dir"))
//            chooser.title = "Select java Component"
//            chooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Java Source", "*.java"))
//            val file = chooser.showOpenDialog(null)


            try {
                val file = Paths.get("fxgl-samples/target/classes/")

                file?.let {
                    val url: URL = it.toFile().toURI().toURL()

                    val urls: Array<URL> = arrayOf<URL>(url)

                    // Create a new class loader with the directory
                    val cl: ClassLoader = URLClassLoader(urls)

                    val cls = cl.loadClass("sandbox.CustomComponent")

                    val instance = cls.getDeclaredConstructor().newInstance() as Component

                    println(instance)

                    entity?.let {
                        it.addComponent(instance)

                        updateView()
                    }
                }

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }

        maxWidth = 460.0

        content = innerBox
    }

    private fun updateView() {
        innerBox.children.clear()

        if (entity == null)
            return

        innerBox.children += addViewButton
        innerBox.children += addComponentButton
        innerBox.children += addCustomComponentButton

        // TODO: this is just a placeholder and needs to be updated
        entity!!.components.sortedBy { it.javaClass.simpleName }
                .forEach { comp ->
                    val pane = GridPane()
                    pane.hgap = 25.0
                    pane.vgap = 10.0

                    var index = 0

                    val title = FXGL.getUIFactoryService().newText(comp.javaClass.simpleName.removeSuffix("Component"), Color.ANTIQUEWHITE, 22.0)

                    pane.addRow(index++, title)
                    pane.addRow(index++, Rectangle(165.0, 2.0, Color.ANTIQUEWHITE))

                    // add property based values
                    comp.javaClass.methods
                            .filter { it.name.endsWith("Property") }
                            .sortedBy { it.name }
                            .forEach { method ->

                                val textKey = FXGL.getUIFactoryService().newText(method.name.removeSuffix("Property"), Color.WHITE, 18.0)

                                val value = method.invoke(comp)
                                val textValue = FXGL.getUIFactoryService().newText("", Color.WHITE, 18.0)

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
                                        // ignore here
                                    }

                                    else -> {
                                        throw IllegalArgumentException("Unknown value type: ${value.javaClass}")
                                    }
                                }

                                if (value is ObservableList<*>) {
                                    pane.addRow(index++, textKey, ListView(value))
                                } else {
                                    pane.addRow(index++, textKey, textValue)
                                }
                            }

                    // add callable methods
                    // TODO: only allow void methods with 0 params for now
                    comp.javaClass.declaredMethods
                            .filter { !it.name.endsWith("Property") }
                            .sortedBy { it.name }
                            .forEach { method ->

                                val btnMethod = FXGL.getUIFactoryService().newButton(method.name + "()")
                                btnMethod.setOnAction {
                                    getDialogService().showInputBoxWithCancel("Input key", { true }) { input ->

                                        onKeyDown(KeyCode.valueOf(input)) {
                                            println("Invoking: $method")

                                            method.invoke(comp)
                                        }
                                    }
                                }

                                //val textKey = FXGL.getUIFactoryService().newText(method.name + "()", Color.WHITE, 18.0)

                                pane.addRow(index++, btnMethod)
                            }

                    pane.addRow(index++, Text(""))

                    innerBox.children += pane
                }


    }
}

internal class DevSpinComponent : Component(), CopyableComponent<DevSpinComponent> {
    override fun onUpdate(tpf: Double) {
        entity.rotateBy(90 * tpf)
    }

    override fun copy(): DevSpinComponent {
        return DevSpinComponent()
    }
}