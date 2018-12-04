/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene


import com.almasb.fxgl.app.*
import com.almasb.sslogger.Logger
import com.almasb.fxgl.core.util.Language
import com.almasb.fxgl.core.util.Supplier
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.Trigger
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.TriggerView
import com.almasb.fxgl.saving.SaveFile
import com.almasb.fxgl.ui.FXGLScrollPane
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

import java.util.ArrayList

import com.almasb.fxgl.app.FXGL.Companion.getSettings
import com.almasb.fxgl.app.FXGL.Companion.getUIFactory
import com.almasb.fxgl.app.FXGL.Companion.getLocalizedString
import com.almasb.fxgl.app.FXGL.Companion.localizedStringProperty
import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.core.util.InputPredicates
import javafx.event.EventHandler

/**
 * This is a base class for main/game menus. It provides several
 * convenience methods for those who just want to extend an existing menu.
 * It also allows for implementors to build menus from scratch. Freshly
 * build menus can interact with FXGL by calling fire* methods.
 *
 * Both main and game menus **should** have the following items:
 *
 *  * Background
 *  * Title
 *  * Version
 *  * Profile name
 *  * Menu Body
 *  * Menu Content
 *
 *
 * However, in reality a menu can contain anything.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class FXGLMenu(protected val type: MenuType) : FXGLScene() {

    companion object {
        private val log = Logger.get("FXGL.Menu")
    }

    protected val controller: GameController = FXGL.getGameController()

    protected val menuRoot = Pane()
    protected val menuContentRoot = Pane()

    internal val EMPTY = MenuContent()

    private val pressAnyKeyState = PressAnyKeyState()

    init {
        contentRoot.children.addAll(
                createBackground(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble()),
                createTitleView(FXGL.getSettings().title),
                createVersionView(makeVersionString()),
                menuRoot, menuContentRoot)

        // we don't data-bind the name because menu subclasses
        // might use some fancy UI without Text / Label
        controller.profileNameProperty().addListener { o, oldName, newName ->
            if (!oldName.isEmpty()) {
                // remove last node which *should* be profile view
                contentRoot.children.removeAt(contentRoot.children.size - 1)
            }

            contentRoot.children.add(createProfileView(getLocalizedString("profile.profile") + ": " + newName))
        }
    }

    /**
     * Switches current active menu body to given.
     *
     * @param menuBox parent node containing menu body
     */
    protected open fun switchMenuTo(menuBox: Node) {
        // no default implementation
    }

    /**
     * Switches current active content to given.
     *
     * @param content menu content
     */
    protected open fun switchMenuContentTo(content: Node) {
        // no default implementation
    }

    protected abstract fun createActionButton(name: String, action: Runnable): Button
    protected abstract fun createActionButton(name: StringBinding, action: Runnable): Button

    protected fun createContentButton(name: String, contentSupplier: Supplier<MenuContent>): Button {
        return createActionButton(name, Runnable { switchMenuContentTo(contentSupplier.get()) })
    }

    protected fun createContentButton(name: StringBinding, contentSupplier: Supplier<MenuContent>): Button {
        return createActionButton(name, Runnable { switchMenuContentTo(contentSupplier.get()) })
    }

    /**
     * @return full version string
     */
    private fun makeVersionString(): String {
        return ("v" + FXGL.getSettings().version
                + if (FXGL.getSettings().applicationMode === ApplicationMode.RELEASE)
            ""
        else
            "-" + FXGL.getSettings().applicationMode)
    }

    /**
     * Create menu background.
     *
     * @param width width of the app
     * @param height height of the app
     * @return menu background UI object
     */
    protected abstract fun createBackground(width: Double, height: Double): Node

    /**
     * Create view for the app title.
     *
     * @param title app title
     * @return UI object
     */
    protected abstract fun createTitleView(title: String): Node

    /**
     * Create view for version string.
     *
     * @param version version string
     * @return UI object
     */
    protected abstract fun createVersionView(version: String): Node

    /**
     * Create view for profile name.
     *
     * @param profileName profile user name
     * @return UI object
     */
    protected abstract fun createProfileView(profileName: String): Node

    /**
     * @return menu content containing list of save files and loadTask/delete buttons
     */
    protected fun createContentLoad(): MenuContent {
        log.debug("createContentLoad()")
//
//        val list = getUIFactory().newListView<SaveFile>()
//
//        val FONT_SIZE = 16.0
//
//        list.setCellFactory { param ->
//            object : ListCell<SaveFile>() {
//                override fun updateItem(item: SaveFile?, empty: Boolean) {
//                    super.updateItem(item, empty)
//
//                    if (empty || item == null) {
//                        text = null
//                        graphic = null
//                    } else {
//
//                        val text = getUIFactory().newText(item.toString())
//                        text.font = getUIFactory().newFont(FontType.MONO, FONT_SIZE)
//
//                        graphic = text
//                    }
//                }
//            }
//        }
//
//        list.setItems(saveLoadManager.saveFiles())
//        list.prefHeightProperty().bind(Bindings.size(list.items).multiply(FONT_SIZE))
//
//        // this runs async
//        //listener.getSaveLoadManager().querySaveFiles()
//
//        val btnLoad = getUIFactory().newButton(localizedStringProperty("menu.load"))
//        btnLoad.disableProperty().bind(list.selectionModel.selectedItemProperty().isNull)
//
//        btnLoad.setOnAction { e ->
//            val saveFile = list.selectionModel.selectedItem
//
//            fireLoad(saveFile)
//        }
//
//        val btnDelete = getUIFactory().newButton(localizedStringProperty("menu.delete"))
//        btnDelete.disableProperty().bind(list.selectionModel.selectedItemProperty().isNull)
//
//        btnDelete.setOnAction { e ->
//            val saveFile = list.selectionModel.selectedItem
//
//            fireDelete(saveFile)
//        }
//
//        val hbox = HBox(50.0, btnLoad, btnDelete)
//        hbox.setAlignment(Pos.CENTER)
//
//        return MenuContent(list, hbox)
        return MenuContent()
    }

    /**
     * @return menu content with difficulty and playtime
     */
    protected fun createContentGameplay(): MenuContent {
        log.debug("createContentGameplay()")

        return MenuContent(
        )
    }

    /**
     * @return menu content containing input mappings (action -> key/mouse)
     */
    protected fun createContentControls(): MenuContent {
        log.debug("createContentControls()")

        val grid = GridPane()
        grid.alignment = Pos.CENTER
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
        grid.columnConstraints.add(ColumnConstraints(200.0, 200.0, 200.0, Priority.ALWAYS, HPos.LEFT, true))
        grid.rowConstraints.add(RowConstraints(40.0, 40.0, 40.0, Priority.ALWAYS, VPos.CENTER, true))

        // row 0
        grid.userData = 0

        FXGL.getInput().bindings.forEach { action, trigger -> addNewInputBinding(action, trigger, grid) }

        val scroll = FXGLScrollPane(grid)
        scroll.vbarPolicy = ScrollBarPolicy.ALWAYS
        scroll.maxHeight = FXGL.getAppHeight() / 2.5

        val hbox = HBox(scroll)
        hbox.alignment = Pos.CENTER

        return MenuContent(hbox)
    }

    private inner class PressAnyKeyState internal constructor() : SubScene() {

        internal var actionContext: UserAction? = null

        init {
            input.addEventHandler(KeyEvent.KEY_PRESSED, EventHandler { e ->
                if (Input.isIllegal(e.getCode()))
                    return@EventHandler

                val rebound = FXGL.getInput().rebind(actionContext!!, e.getCode(), InputModifier.from(e))

                if (rebound)
                    FXGL.getStateMachine().popState()
            })

            input.addEventHandler(MouseEvent.MOUSE_PRESSED, EventHandler { e ->
                val rebound = FXGL.getInput().rebind(actionContext!!, e.getButton(), InputModifier.from(e))

                if (rebound)
                    FXGL.getStateMachine().popState()
            })

            val rect = Rectangle(250.0, 100.0)
            rect.stroke = Color.color(0.85, 0.9, 0.9, 0.95)
            rect.strokeWidth = 10.0
            rect.arcWidth = 15.0
            rect.arcHeight = 15.0

            val text = getUIFactory().newText(getLocalizedString("menu.pressAnyKey"), 24.0)

            val pane = StackPane(rect, text)
            pane.translateX = (FXGL.getAppWidth() / 2 - 125).toDouble()
            pane.translateY = (FXGL.getAppHeight() / 2 - 50).toDouble()

            children.add(pane)
        }
    }

    private fun addNewInputBinding(action: UserAction, trigger: Trigger, grid: GridPane) {
        val actionName = getUIFactory().newText(action.name, Color.WHITE, 18.0)

        val triggerView = TriggerView(trigger)
        triggerView.triggerProperty().bind(FXGL.getInput().triggerProperty(action))

        triggerView.setOnMouseClicked { event ->
            pressAnyKeyState.actionContext = action
            FXGL.getStateMachine().pushState(pressAnyKeyState)
        }

        val hBox = HBox()
        hBox.prefWidth = 100.0
        hBox.alignment = Pos.CENTER
        hBox.children.add(triggerView)

        var controlsRow = grid.userData as Int
        grid.addRow(controlsRow++, actionName, hBox)
        grid.userData = controlsRow
    }

    /**
     * https://github.com/AlmasB/FXGL/issues/493
     *
     * @return menu content with video settings
     */
    protected fun createContentVideo(): MenuContent {
        log.debug("createContentVideo()")

        val languageBox = getUIFactory().newChoiceBox(FXCollections.observableArrayList(*Language.values()))
        languageBox.setValue(Language.ENGLISH)

        getSettings().language.bind(languageBox.valueProperty())

        val vbox = VBox()

        if (getSettings().isManualResizeEnabled) {
            val btnFixRatio = getUIFactory().newButton(localizedStringProperty("menu.fixRatio"))
            btnFixRatio.setOnAction { e -> controller.fixAspectRatio() }

            vbox.children.add(btnFixRatio)
        }

        if (getSettings().isFullScreenAllowed) {
            val cbFullScreen = getUIFactory().newCheckBox()
            cbFullScreen.isSelected = false
            cbFullScreen.selectedProperty().bindBidirectional(getSettings().fullScreen)

            vbox.children.add(HBox(25.0, getUIFactory().newText(getLocalizedString("menu.fullscreen") + ": "), cbFullScreen))
        }

        return MenuContent(
                HBox(25.0, getUIFactory().newText(localizedStringProperty("menu.language").concat(":")), languageBox),
                vbox
        )
    }

    /**
     * @return menu content containing music and sound volume sliders
     */
    protected fun createContentAudio(): MenuContent {
        log.debug("createContentAudio()")

        val sliderMusic = Slider(0.0, 1.0, 1.0)
        sliderMusic.valueProperty().bindBidirectional(FXGL.getAudioPlayer().globalMusicVolumeProperty())

        val textMusic = getUIFactory().newText(localizedStringProperty("menu.music.volume").concat(": "))
        val percentMusic = getUIFactory().newText("")
        percentMusic.textProperty().bind(sliderMusic.valueProperty().multiply(100).asString("%.0f"))

        val sliderSound = Slider(0.0, 1.0, 1.0)
        sliderSound.valueProperty().bindBidirectional(FXGL.getAudioPlayer().globalSoundVolumeProperty())

        val textSound = getUIFactory().newText(localizedStringProperty("menu.sound.volume").concat(": "))
        val percentSound = getUIFactory().newText("")
        percentSound.textProperty().bind(sliderSound.valueProperty().multiply(100).asString("%.0f"))

        val hboxMusic = HBox(15.0, textMusic, sliderMusic, percentMusic)
        val hboxSound = HBox(15.0, textSound, sliderSound, percentSound)

        hboxMusic.setAlignment(Pos.CENTER_RIGHT)
        hboxSound.setAlignment(Pos.CENTER_RIGHT)

        return MenuContent(hboxMusic, hboxSound)
    }

    /**
     * @return menu content containing a list of credits
     */
    protected fun createContentCredits(): MenuContent {
        log.debug("createContentCredits()")

        val pane = FXGLScrollPane()
        pane.prefWidth = (FXGL.getAppWidth() * 3 / 5).toDouble()
        pane.prefHeight = (FXGL.getAppHeight() / 2).toDouble()
        pane.style = "-fx-background:black;"

        val vbox = VBox()
        vbox.alignment = Pos.CENTER
        vbox.prefWidth = pane.prefWidth - 15

        val credits = ArrayList(getSettings().credits.list)
        credits.add("")
        credits.add("Powered by FXGL " + FXGL.getVersion())
        credits.add("Author: Almas Baimagambetov")
        credits.add("https://github.com/AlmasB/FXGL")
        credits.add("")

        for (credit in credits) {
            vbox.children.add(getUIFactory().newText(credit))
        }

        pane.content = vbox

        return MenuContent(pane)
    }

    /**
     * @return menu content containing feedback options
     */
    protected fun createContentFeedback(): MenuContent {
        log.debug("createContentFeedback()")

        return MenuContent(VBox())
    }

    /**
     * @return menu content containing a list of achievements
     */
    protected fun createContentAchievements(): MenuContent {
        log.debug("createContentAchievements()")

        return MenuContent()
    }

    /**
     * A generic vertical box container for menu content
     * where each element is followed by a separator.
     */
    class MenuContent(vararg items: Node) : VBox() {

        private var onOpen: Runnable? = null
        private var onClose: Runnable? = null

        init {

            if (items.size > 0) {
                var maxW = items[0].layoutBounds.width.toInt()

                for (n in items) {
                    val w = n.layoutBounds.width.toInt()
                    if (w > maxW)
                        maxW = w
                }

                for (item in items) {
                    children.addAll(item)
                }
            }

            sceneProperty().addListener { o, oldScene, newScene ->
                if (newScene != null) {
                    onOpen()
                } else {
                    onClose()
                }
            }
        }

        /**
         * Set on open handler.
         *
         * @param onOpenAction method to be called when content opens
         */
        fun setOnOpen(onOpenAction: Runnable) {
            this.onOpen = onOpenAction
        }

        /**
         * Set on close handler.
         *
         * @param onCloseAction method to be called when content closes
         */
        fun setOnClose(onCloseAction: Runnable) {
            this.onClose = onCloseAction
        }

        private fun onOpen() {
            if (onOpen != null)
                onOpen!!.run()
        }

        private fun onClose() {
            if (onClose != null)
                onClose!!.run()
        }
    }

    /**
     * Adds a UI node.
     *
     * @param node the node to add
     */
    protected fun addUINode(node: Node) {
        menuContentRoot.children.add(node)
    }

//
//    override fun getSaveLoadManager(): SaveLoadManager {
//        return saveLoadManager
//    }
//
//    fun isProfileSelected() = profileName.value.isNotEmpty()
//
//    private val hasSaves = ReadOnlyBooleanWrapper(false)
//
//    override fun hasSavesProperty(): ReadOnlyBooleanProperty {
//        return hasSaves.readOnlyProperty
//    }

//    override fun onDelete(saveFile: SaveFile) {
//        FXGL.getDisplay().showConfirmationBox(FXGL.getLocalizedString("menu.deleteSave")+"[${saveFile.name}]?", { yes ->
//
//            if (yes) {
//                saveLoadManager
//                        .deleteSaveFileTask(saveFile)
//                        .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.deleting")+": ${saveFile.name}"))
//            }
//        })
//    }

    /**
     * Can only be fired from main menu.
     * Starts new game.
     */
    protected fun fireNewGame() {
        log.debug("fireNewGame()")

        controller.startNewGame()
    }

    /**
     * Loads the game state from last modified save file.
     */
    protected fun fireContinue() {
        log.debug("fireContinue()")

        controller.loadGameFromLastSave()
    }

    /**
     * Loads the game state from previously saved file.
     */
    protected fun fireLoad(saveFile: SaveFile) {
        log.debug("fireLoad()")

        val text = FXGL.getLocalizedString("menu.loadSave") +
                " [${saveFile.name}]?\n" +
                FXGL.getLocalizedString("menu.unsavedProgress")

        FXGL.getDisplay().showConfirmationBox(text) { yes ->
            if (yes)
                controller.loadGame(saveFile)
        }
    }

    /**
     * Can only be fired from game menu.
     * Saves current state of the game with given file name.
     */
    protected fun fireSave() {
        log.debug("fireSave()")

        FXGL.getDisplay().showInputBoxWithCancel(FXGL.getLocalizedString("menu.enterSaveName"), InputPredicates.ALPHANUM, Consumer { saveFileName ->

            if (saveFileName.isEmpty())
                return@Consumer

            controller.saveGame(saveFileName)

//            if (saveLoadManager.saveFileExists(saveFileName)) {
//                FXGL.getDisplay().showConfirmationBox(getLocalizedString("menu.overwrite") +" [$saveFileName]?", { yes ->
//
//                    if (yes)
//                        doSave(saveFileName)
//                })
//            } else {
//                doSave(saveFileName)
//            }
        })
    }

    /**
     * @param fileName name of the save file
     */
    protected fun fireDelete(fileName: SaveFile) {
        log.debug("fireDelete()")

        //listener.onDelete(fileName)
    }

    /**
     * Can only be fired from game menu.
     * Will close the menu and unpause the game.
     */
    protected fun fireResume() {
        log.debug("fireResume()")

        controller.gotoPlay()
    }

    protected fun fireExit() {
        log.debug("fireExit()")

        val text = getLocalizedString("dialog.exitGame")

        FXGL.getDisplay().showConfirmationBox(text) { yes ->
            if (yes)
                controller.exit()
        }
    }

    protected fun fireExitToMainMenu() {
        log.debug("fireExitToMainMenu()")

        val text = FXGL.getLocalizedString("menu.exitMainMenu") + "\n" +
                FXGL.getLocalizedString("menu.unsavedProgress")

        FXGL.getDisplay().showConfirmationBox(text) { yes ->
            if (yes)
                controller.gotoMainMenu()
        }
    }
}
