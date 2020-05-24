/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.core.util.InputPredicates
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.Trigger
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.TriggerView
import com.almasb.fxgl.profile.SaveFile
import com.almasb.fxgl.profile.SaveLoadService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.ui.FXGLScrollPane
import com.almasb.fxgl.ui.FontType
import com.almasb.fxgl.logging.Logger
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections
import javafx.event.EventHandler
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
import javafx.util.Duration
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

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
abstract class FXGLMenu(protected val type: MenuType) : SubScene() {

    companion object {
        private val log = Logger.get("Menu")
    }

    protected val saveLoadService: SaveLoadService = FXGL.getSaveLoadService()

    protected val controller by lazy { FXGL.getGameController() }
    protected val appWidth by lazy { FXGL.getAppWidth() }
    protected val appHeight by lazy { FXGL.getAppHeight() }

    protected val menuRoot = Pane()
    protected val menuContentRoot = Pane()

    internal val EMPTY = MenuContent()

    private val pressAnyKeyState = PressAnyKeyState()

    init {
        contentRoot.children.addAll(
                createBackground(getAppWidth().toDouble(), getAppHeight().toDouble()),
                createTitleView(getSettings().title),
                createVersionView(makeVersionString()),
                menuRoot, menuContentRoot)

        // we don't data-bind the name because menu subclasses
        // might use some fancy UI without Text / Label
//        controller.profileNameProperty().addListener { _, oldName, newName ->
//            if (!oldName.isEmpty()) {
//                // remove last node which *should* be profile view
//                contentRoot.children.removeAt(contentRoot.children.size - 1)
//            }
//
//            contentRoot.children.add(createProfileView(FXGL.localize("profile.profile") + ": " + newName))
//        }
    }

    override fun onUpdate(tpf: Double) {
        // extract hardcoded string
        if (type == MenuType.MAIN_MENU && getSettings().isUserProfileEnabled && getSettings().profileName.value == "DEFAULT") {
            showProfileDialog()
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
        return ("v" + getSettings().version
                + if (getSettings().applicationMode === ApplicationMode.RELEASE)
            ""
        else
            "-" + getSettings().applicationMode)
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
     * @return menu content containing list of save files and load/delete buttons
     */
    protected fun createContentLoad(): MenuContent {
        log.debug("createContentLoad()")

        val list = getUIFactoryService().newListView<SaveFile>()

        val FONT_SIZE = 16.0

        list.setCellFactory { param ->
            object : ListCell<SaveFile>() {
                override fun updateItem(item: SaveFile?, empty: Boolean) {
                    super.updateItem(item, empty)

                    if (empty || item == null) {
                        text = null
                        graphic = null
                    } else {

                        val nameDate = "%-25.25s %s".format(item.name, item.dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm")))

                        val text = getUIFactoryService().newText(nameDate, Color.WHITE, FontType.MONO, FONT_SIZE)

                        graphic = text
                    }
                }
            }
        }

        // TODO: use profile name instead of dir name if present
        val task = saveLoadService.readSaveFilesTask("./", getSettings().saveFileExt)
                .onSuccess {
                    list.items.addAll(it)
                }

        FXGL.getTaskService().runAsyncFXWithDialog(task, localize("menu.load"))

        list.prefHeightProperty().bind(Bindings.size(list.items).multiply(FONT_SIZE).add(16))

        val btnLoad = getUIFactoryService().newButton(localizedStringProperty("menu.load"))
        btnLoad.disableProperty().bind(list.selectionModel.selectedItemProperty().isNull)

        btnLoad.setOnAction { e ->
            val saveFile = list.selectionModel.selectedItem

            fireLoad(saveFile)
        }

        val btnDelete = getUIFactoryService().newButton(localizedStringProperty("menu.delete"))
        btnDelete.disableProperty().bind(list.selectionModel.selectedItemProperty().isNull)

        btnDelete.setOnAction { e ->
            val saveFile = list.selectionModel.selectedItem

            fireDelete(saveFile)
        }

        val hbox = HBox(50.0, btnLoad, btnDelete)
        hbox.alignment = Pos.CENTER

        return MenuContent(list, hbox)
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

        getInput().allBindings.forEach { action, trigger -> addNewInputBinding(action, trigger, grid) }

        val scroll = FXGLScrollPane(grid)
        scroll.vbarPolicy = ScrollBarPolicy.ALWAYS
        scroll.maxHeight = getAppHeight() / 2.5

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

                val rebound = getInput().rebind(actionContext!!, e.getCode(), InputModifier.from(e))

                if (rebound)
                    FXGL.getSceneService().popSubScene()
            })

            input.addEventHandler(MouseEvent.MOUSE_PRESSED, EventHandler { e ->
                val rebound = getInput().rebind(actionContext!!, e.getButton(), InputModifier.from(e))

                if (rebound)
                    FXGL.getSceneService().popSubScene()
            })

            val rect = Rectangle(250.0, 100.0)
            rect.stroke = Color.color(0.85, 0.9, 0.9, 0.95)
            rect.strokeWidth = 10.0
            rect.arcWidth = 15.0
            rect.arcHeight = 15.0

            val text = getUIFactoryService().newText(localize("menu.pressAnyKey"), 24.0)

            val pane = StackPane(rect, text)
            pane.translateX = (getAppWidth() / 2 - 125).toDouble()
            pane.translateY = (getAppHeight() / 2 - 50).toDouble()

            contentRoot.children.add(pane)
        }
    }

    private fun addNewInputBinding(action: UserAction, trigger: Trigger, grid: GridPane) {
        val actionName = getUIFactoryService().newText(action.name, Color.WHITE, 18.0)

        val triggerView = TriggerView(trigger)
        triggerView.triggerProperty().bind(getInput().triggerProperty(action))

        triggerView.setOnMouseClicked {
            pressAnyKeyState.actionContext = action
            FXGL.getSceneService().pushSubScene(pressAnyKeyState)
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

        val languageBox = getUIFactoryService().newChoiceBox(FXCollections.observableArrayList(getSettings().supportedLanguages))
        languageBox.value = getSettings().language.value

        getSettings().language.bindBidirectional(languageBox.valueProperty())

        val vbox = VBox()

        if (getSettings().isFullScreenAllowed) {
            val cbFullScreen = getUIFactoryService().newCheckBox()
            cbFullScreen.selectedProperty().bindBidirectional(getSettings().fullScreen)

            vbox.children.add(HBox(25.0, getUIFactoryService().newText(localize("menu.fullscreen") + ": "), cbFullScreen))
        }

        return MenuContent(
                HBox(25.0, getUIFactoryService().newText(localizedStringProperty("menu.language").concat(":")), languageBox),
                vbox
        )
    }

    /**
     * @return menu content containing music and sound volume sliders
     */
    protected fun createContentAudio(): MenuContent {
        log.debug("createContentAudio()")

        val sliderMusic = Slider(0.0, 1.0, 1.0)
        sliderMusic.valueProperty().bindBidirectional(getSettings().globalMusicVolumeProperty)

        val textMusic = getUIFactoryService().newText(localizedStringProperty("menu.music.volume").concat(": "))
        val percentMusic = getUIFactoryService().newText("")
        percentMusic.textProperty().bind(sliderMusic.valueProperty().multiply(100).asString("%.0f"))

        val sliderSound = Slider(0.0, 1.0, 1.0)
        sliderSound.valueProperty().bindBidirectional(getSettings().globalSoundVolumeProperty)

        val textSound = getUIFactoryService().newText(localizedStringProperty("menu.sound.volume").concat(": "))
        val percentSound = getUIFactoryService().newText("")
        percentSound.textProperty().bind(sliderSound.valueProperty().multiply(100).asString("%.0f"))

        val hboxMusic = HBox(15.0, textMusic, sliderMusic, percentMusic)
        val hboxSound = HBox(15.0, textSound, sliderSound, percentSound)

        hboxMusic.alignment = Pos.CENTER_RIGHT
        hboxSound.alignment = Pos.CENTER_RIGHT

        return MenuContent(hboxMusic, hboxSound)
    }

    /**
     * @return menu content containing a list of credits
     */
    protected fun createContentCredits(): MenuContent {
        log.debug("createContentCredits()")

        val pane = FXGLScrollPane()
        pane.prefWidth = 500.0
        pane.prefHeight = (getAppHeight() / 2).toDouble()
        pane.style = "-fx-background:black;"

        val vbox = VBox()
        vbox.alignment = Pos.CENTER_LEFT
        vbox.prefWidth = pane.prefWidth - 15

        val credits = ArrayList(getSettings().credits)
        credits.add("")
        credits.add("Powered by FXGL " + FXGL.getVersion())
        credits.add("Author: Almas Baimagambetov")
        credits.add("https://github.com/AlmasB/FXGL")
        credits.add("")

        for (credit in credits) {
            if (credit.length > 45) {
                log.warning("Credit name length > 45: $credit")
            }

            vbox.children.add(getUIFactoryService().newText(credit))
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

        val content = MenuContent()
        
        getAchievementService().achievementsCopy.forEach { a ->
            val checkBox = CheckBox()
            checkBox.isDisable = true
            checkBox.selectedProperty().bind(a.achievedProperty())

            val text = getUIFactoryService().newText(a.name)
            val tooltip = Tooltip(a.description)
            tooltip.showDelay = Duration.seconds(0.1)

            Tooltip.install(text, tooltip)

            val box = HBox(25.0, text, checkBox)
            box.alignment = Pos.CENTER_RIGHT

            content.children.add(box)
        }

        return content
    }

    /**
     * A generic vertical box container for menu content
     * where each element is followed by a separator.
     */
    class MenuContent(vararg items: Node) : VBox() {

        private var onOpen: Runnable? = null
        private var onClose: Runnable? = null

        var maxW = 0

        init {
            if (items.isNotEmpty()) {
                maxW = items[0].layoutBounds.width.toInt()

                for (n in items) {
                    val w = n.layoutBounds.width.toInt()
                    if (w > maxW)
                        maxW = w
                }

                for (item in items) {
                    children.addAll(item)
                }
            }

            sceneProperty().addListener { _, _, newScene ->
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

//    override fun onDelete(saveFile: SaveFile) {
//        getDisplay().showConfirmationBox(Local.localize("menu.deleteSave")+"[${saveFile.name}]?", { yes ->
//
//            if (yes) {
//                saveLoadManager
//                        .deleteSaveFileTask(saveFile)
//                        .runAsyncFXWithDialog(ProgressDialog(Local.localize("menu.deleting")+": ${saveFile.name}"))
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

        //    override fun loadGameFromLastSave() {
//        saveLoadService
//                .loadLastModifiedSaveFileTask(getSettings().profileName.value, getSettings().saveFileExt)
//                .then { saveLoadService.readSaveFileTask(it) }
//                .onSuccess { startLoadedGame(it.data) }
//                .runAsyncFXWithDialog(ProgressDialog(local.getLocalizedString("menu.loading") + "..."))
//    }
    }

    /**
     * Loads the game state from previously saved file.
     */
    protected fun fireLoad(saveFile: SaveFile) {
        log.debug("fireLoad()")

        val text = localize("menu.loadSave") + " [${saveFile.name}]?\n" + localize("menu.unsavedProgress")

        getDisplay().showConfirmationBox(text) { yes ->

            if (yes) {
                controller.loadGame(saveFile.data)
            }
        }
    }

    /**
     * Can only be fired from game menu.
     * Saves current state of the game with given file name.
     */
    protected fun fireSave() {
        log.debug("fireSave()")

        getDisplay().showInputBoxWithCancel(localize("menu.enterSaveName"), InputPredicates.ALPHANUM, Consumer { saveFileName ->

            if (saveFileName.isEmpty())
                return@Consumer

            val saveFile = SaveFile(makeSaveFileName(saveFileName + "." + getSettings().saveFileExt))

            if (saveLoadService.saveFileExists(saveFile.name)) {
                getDisplay().showConfirmationBox(localize("menu.overwrite") +" [$saveFileName]?") { yes ->

                    if (yes)
                        doSave(saveFile)
                }
            } else {
                doSave(saveFile)
            }
        })
    }

    private fun doSave(saveFile: SaveFile) {
        controller.saveGame(saveFile.data)

        FXGL.getTaskService().runAsyncFXWithDialog(
                saveLoadService.writeTask(saveFile.name, saveFile.data),
                localize("menu.savingData") + ": ${saveFile.name}"
        )
    }

    /**
     * @param fileName name of the save file
     */
    protected fun fireDelete(saveFile: SaveFile) {
        log.debug("fireDelete()")

//        saveLoadService.deleteSaveFileTask(saveFile)
//                .run()
    }

    /**
     * Can only be fired from game menu.
     * Will close the menu and unpause the game.
     */
    protected fun fireResume() {
        log.debug("fireResume()")

        FXGL.getSceneService().popSubScene()
    }

    protected fun fireExit() {
        log.debug("fireExit()")

        val text = localize("dialog.exitGame")

        getDisplay().showConfirmationBox(text) { yes ->
            if (yes)
                controller.exit()
        }
    }

    protected fun fireExitToMainMenu() {
        log.debug("fireExitToMainMenu()")

        val text = localize("menu.exitMainMenu") + "\n" +
                localize("menu.unsavedProgress")

        getDisplay().showConfirmationBox(text) { yes ->
            if (yes)
                controller.gotoMainMenu()
        }
    }

    /**
     * Show profile dialog so that user selects existing or creates new profile.
     * The dialog is only dismissed when profile is chosen either way.
     */
    protected fun showProfileDialog() {
        val profilesBox = getUIFactoryService().newChoiceBox(FXCollections.observableArrayList<String>())

        val btnNew = getUIFactoryService().newButton(localizedStringProperty("multiplayer.new"))
        val btnSelect = getUIFactoryService().newButton(localizedStringProperty("multiplayer.select"))
        btnSelect.disableProperty().bind(profilesBox.valueProperty().isNull)
        val btnDelete = getUIFactoryService().newButton(localizedStringProperty("menu.delete"))
        btnDelete.disableProperty().bind(profilesBox.valueProperty().isNull)

        btnNew.setOnAction {
            getDisplay().showInputBox(localize("profile.new"), InputPredicates.ALPHANUM, Consumer { name ->

//                val task = saveLoadService.createProfileTask(name)
//                        .onSuccess { showProfileDialog() }
//                        .onFailure { error ->
//                            getDisplay().showErrorBox("$error") {
//                                showProfileDialog()
//                            }
//                        }
//
//                FXGL.getTaskService().runAsyncFXWithDialog(
//                        task,
//                        localize("profile.loadingProfile") + ": $name"
//                )
            })
        }

        btnSelect.setOnAction {
            val name = profilesBox.value

            getSettings().profileName.set(name)

//            saveLoadService.
//                    .onSuccess { profile ->
//                        val ok = loadFromProfile(profile)
//
//                        if (!ok) {
//                            getDisplay().showErrorBox(getLocalizedString("profile.corrupted")+": $name", { showProfileDialog() })
//                        } else {
//                            profileName.set(name)
//
//                            saveLoadManager.loadLastModifiedSaveFileTask()
//                                    .onSuccess { hasSaves.value = true }
//                                    .onFailure { hasSaves.value = false }
//                                    .runAsyncFXWithDialog(ProgressDialog(getLocalizedString("menu.loadingLast")))
//                        }
//                    }
//                    .onFailure { error ->
//                        getDisplay().showErrorBox(getLocalizedString("profile.corrupted")+(": $name\nError: $error"), { this.showProfileDialog() })
//                    }
//                    .runAsyncFXWithDialog(ProgressDialog(getLocalizedString("profile.loadingProfile")+": $name"))
        }

//        btnDelete.setOnAction {
//            val name = profilesBox.value
//
//            val task = saveLoadService.deleteProfileTask(name)
//                    .onSuccess { showProfileDialog() }
//                    .onFailure { error ->
//                        getDisplay().showErrorBox("$error") {
//                            showProfileDialog()
//                        }
//                    }
//
//            FXGL.getTaskService().runAsyncFXWithDialog(
//                    task,
//                    localize("profile.deletingProfile") + ": $name"
//            )
//        }
//
//        val task = saveLoadService.readProfileNamesTask()
//                .onSuccess { names ->
//                    profilesBox.items.addAll(names)
//
//                    if (!profilesBox.items.isEmpty()) {
//                        profilesBox.selectionModel.selectFirst()
//                    }
//
//                    getDisplay().showBox(localize("profile.selectOrCreate"), profilesBox, btnSelect, btnNew, btnDelete)
//                }
//                .onFailure { error ->
//                    log.warning("$error")
//
//                    getDisplay().showBox(localize("profile.selectOrCreate"), profilesBox, btnSelect, btnNew, btnDelete)
//                }
//
//        FXGL.getTaskService().runAsyncFXWithDialog(
//                task,
//                localize("profile.loadingProfiles")
//        )
    }

    private fun makeSaveFileName(rawName: String): String {
        // TODO: if profile enabled, use that
        return rawName
    }
}
