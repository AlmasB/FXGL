/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.MenuItem
import com.almasb.fxgl.core.math.FXGLMath.noise1D
import com.almasb.fxgl.core.util.InputPredicates
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.dsl.FXGL.Companion.animationBuilder
import com.almasb.fxgl.dsl.FXGL.Companion.random
import com.almasb.fxgl.dsl.FXGL.Companion.texture
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.Trigger
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.TriggerView
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.particle.ParticleEmitters
import com.almasb.fxgl.particle.ParticleSystem
import com.almasb.fxgl.profile.SaveFile
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.ui.FXGLScrollPane
import com.almasb.fxgl.ui.FontType
import javafx.animation.FadeTransition
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.*
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.effect.BlendMode
import javafx.scene.effect.GaussianBlur
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.*
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * This is the default FXGL menu used if the developers don't provide their own.
 * This class provides common structures used in FXGL default menu style.
 * This class is open in case developers want to use the FXGL default menu and only tweak.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
open class FXGLDefaultMenu(type: MenuType) : FXGLMenu(type) {

    companion object {
        private val log = Logger.get("FXGL.DefaultMenu")
    }

    private var particleSystem: ParticleSystem? = null

    private var titleColor: ObjectProperty<Color>? = null
    private var t = 0.0

    private val menuRoot = Pane()
    private val menuContentRoot = Pane()

    private val EMPTY = MenuContent()

    private val pressAnyKeyState = PressAnyKeyState()

    private val menu: Node

    init {
        if (appWidth < 800 || appHeight < 600)
            log.warning("FXGLDefaultMenu is not designed for resolutions < 800x600")

        contentRoot.children.addAll(
                createBackground(getAppWidth().toDouble(), getAppHeight().toDouble()),
                createTitleView(getSettings().title),
                createVersionView(makeVersionString()),
                menuRoot, menuContentRoot)
        
        menu = if (type === MenuType.MAIN_MENU)
            createMenuBodyMainMenu()
        else
            createMenuBodyGameMenu()

        val menuX = 50.0
        val menuY = appHeight / 2.0 - menu.layoutHeight / 2

        menuRoot.translateX = menuX
        menuRoot.translateY = menuY

        menuContentRoot.translateX = appWidth - 500.0
        menuContentRoot.translateY = menuY

        // particle smoke
        val t = FXGL.texture("particles/smoke.png", 128.0, 128.0).brighter().brighter()

        val emitter = ParticleEmitters.newFireEmitter()
        emitter.blendMode = BlendMode.SRC_OVER
        emitter.setSourceImage(t.getImage())
        emitter.setSize(150.0, 220.0)
        emitter.numParticles = 10
        emitter.emissionRate = 0.01
        emitter.setVelocityFunction { i -> Point2D(random() * 2.5, -random() * random(80, 120)) }
        emitter.setExpireFunction { i -> Duration.seconds(random(4, 7).toDouble()) }
        emitter.setScaleFunction { i -> Point2D(0.15, 0.10) }
        emitter.setSpawnPointFunction { i -> Point2D(random(0.0, appWidth - 200.0), 120.0) }

        particleSystem!!.addParticleEmitter(emitter, 0.0, FXGL.getAppHeight().toDouble())

        contentRoot.children.add(3, particleSystem!!.pane)

        menuRoot.children.addAll(menu)
        menuContentRoot.children.add(EMPTY)
    }

    private val animations = arrayListOf<Animation<*>>()

    override fun onCreate() {
        animations.clear()

        val menuBox = menuRoot.children[0] as MenuBox

        menuBox.children.forEachIndexed { index, node ->

            node.translateX = -250.0

            val animation = animationBuilder()
                    .delay(Duration.seconds(index * 0.07))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .duration(Duration.seconds(0.66))
                    .translate(node)
                    .from(Point2D(-250.0, 0.0))
                    .to(Point2D(0.0, 0.0))
                    .build()

            animations += animation

            animation.stop()
            animation.start()
        }
    }

    override fun onDestroy() {
        // the scene is no longer active so reset everything
        // so that next time scene is active everything is loaded properly
        switchMenuTo(menu)
        switchMenuContentTo(EMPTY)
    }

    override fun onUpdate(tpf: Double) {
        // extract hardcoded string
        if (type == MenuType.MAIN_MENU && getSettings().isUserProfileEnabled && getSettings().profileName.value == "DEFAULT") {
            showProfileDialog()
        }

        animations.forEach { it.onUpdate(tpf) }

        val frequency = 1.7

        t += tpf * frequency

        particleSystem!!.onUpdate(tpf)

        val color = Color.color(1.0, 1.0, 1.0, noise1D(t))
        titleColor!!.set(color)
    }

    private fun createBackground(width: Double, height: Double): Node {
        val bg = Rectangle(width, height)
        bg.fill = Color.rgb(10, 1, 1, if (type == MenuType.GAME_MENU) 0.5 else 1.0)
        return bg
    }

    private fun createTitleView(title: String): Node {
        titleColor = SimpleObjectProperty(Color.WHITE)

        val text = FXGL.getUIFactoryService().newText(title.substring(0, 1), 50.0)
        text.fill = null
        text.strokeProperty().bind(titleColor)
        text.strokeWidth = 1.5

        val text2 = FXGL.getUIFactoryService().newText(title.substring(1, title.length), 50.0)
        text2.fill = null
        text2.stroke = titleColor!!.value
        text2.strokeWidth = 1.5

        val textWidth = text.layoutBounds.width + text2.layoutBounds.width

        val border = Rectangle(textWidth + 30, 65.0, null)
        border.stroke = Color.WHITE
        border.strokeWidth = 4.0
        border.arcWidth = 25.0
        border.arcHeight = 25.0

        val emitter = ParticleEmitters.newExplosionEmitter(50)

        val t = texture("particles/trace_horizontal.png", 64.0, 64.0)

        emitter.blendMode = BlendMode.ADD
        emitter.setSourceImage(t.getImage())
        emitter.maxEmissions = Integer.MAX_VALUE
        emitter.setSize(18.0, 22.0)
        emitter.numParticles = 2
        emitter.emissionRate = 0.2
        emitter.setVelocityFunction { i ->
            if (i!! % 2 == 0)
                Point2D(random(-10, 0).toDouble(), random(0, 0).toDouble())
            else
                Point2D(random(0, 10).toDouble(), random(0, 0).toDouble())
        }
        emitter.setExpireFunction { Duration.seconds(random(4, 6).toDouble()) }
        emitter.setScaleFunction { Point2D(-0.03, -0.03) }
        emitter.setSpawnPointFunction { Point2D(random(0, 0).toDouble(), random(0, 0).toDouble()) }
        emitter.setAccelerationFunction { Point2D(random(-1, 1).toDouble(), random(0, 0).toDouble()) }

        val box = HBox(text, text2)
        box.alignment = Pos.CENTER

        val titleRoot = StackPane()
        titleRoot.children.addAll(border, box)

        titleRoot.translateX = appWidth / 2.0 - (textWidth + 30) / 2
        titleRoot.translateY = 50.0

        particleSystem = ParticleSystem()

        if (!FXGL.getSettings().isExperimentalNative)
            particleSystem!!.addParticleEmitter(emitter, appWidth / 2.0 - 30, titleRoot.translateY + border.height - 16)

        return titleRoot
    }

    private fun createVersionView(version: String): Node {
        val view = FXGL.getUIFactoryService().newText(version)
        view.translateY = (FXGL.getAppHeight() - 2).toDouble()
        return view
    }

    private fun createProfileView(profileName: String): Node {
        val view = FXGL.getUIFactoryService().newText(profileName)
        view.translateY = (FXGL.getAppHeight() - 2).toDouble()
        view.translateX = FXGL.getAppWidth() - view.layoutBounds.width
        return view
    }

    private fun createMenuBodyMainMenu(): MenuBox {
        log.debug("createMenuBodyMainMenu()")

        val box = MenuBox()

        val enabledItems = FXGL.getSettings().enabledMenuItems

        val itemNewGame = MenuButton("menu.newGame")
        itemNewGame.setOnAction(EventHandler{ fireNewGame() })
        box.add(itemNewGame)

        val itemOptions = MenuButton("menu.options")
        itemOptions.setChild(createOptionsMenu())
        box.add(itemOptions)

        if (enabledItems.contains(MenuItem.EXTRA)) {
            val itemExtra = MenuButton("menu.extra")
            itemExtra.setChild(createExtraMenu())
            box.add(itemExtra)
        }

        val itemExit = MenuButton("menu.exit")
        itemExit.setOnAction(EventHandler{ fireExit() })
        box.add(itemExit)

        return box
    }

    private fun createMenuBodyGameMenu(): MenuBox {
        log.debug("createMenuBodyGameMenu()")

        val box = MenuBox()

        val enabledItems = FXGL.getSettings().enabledMenuItems

        val itemResume = MenuButton("menu.resume")
        itemResume.setOnAction(EventHandler{ fireResume() })
        box.add(itemResume)

        if (enabledItems.contains(MenuItem.SAVE_LOAD)) {
            val itemSave = MenuButton("menu.save")
            itemSave.setOnAction(EventHandler{ fireSave() })

            val itemLoad = MenuButton("menu.load")
            itemLoad.setMenuContent(Supplier { createContentLoad() }, isCached = false)

            box.add(itemSave)
            box.add(itemLoad)
        }

        val itemOptions = MenuButton("menu.options")
        itemOptions.setChild(createOptionsMenu())
        box.add(itemOptions)

        if (enabledItems.contains(MenuItem.EXTRA)) {
            val itemExtra = MenuButton("menu.extra")
            itemExtra.setChild(createExtraMenu())
            box.add(itemExtra)
        }

        if (getSettings().isMainMenuEnabled) {
            val itemExit = MenuButton("menu.mainMenu")
            itemExit.setOnAction(EventHandler{ fireExitToMainMenu() })
            box.add(itemExit)
        } else {
            val itemExit = MenuButton("menu.exit")
            itemExit.setOnAction(EventHandler{ fireExit() })
            box.add(itemExit)
        }

        return box
    }

    private fun createOptionsMenu(): MenuBox {
        log.debug("createOptionsMenu()")

        val itemGameplay = MenuButton("menu.gameplay")
        itemGameplay.setMenuContent(Supplier { this.createContentGameplay() })

        val itemControls = MenuButton("menu.controls")
        itemControls.setMenuContent(Supplier { this.createContentControls() })

        val itemVideo = MenuButton("menu.video")
        itemVideo.setMenuContent(Supplier { this.createContentVideo() })
        val itemAudio = MenuButton("menu.audio")
        itemAudio.setMenuContent(Supplier { this.createContentAudio() })

        val btnRestore = MenuButton("menu.restore")
        btnRestore.setOnAction(EventHandler{ e ->
            FXGL.getDialogService().showConfirmationBox(FXGL.localize("menu.settingsRestore")) { yes ->
                if (yes!!) {
                    switchMenuContentTo(EMPTY)
                    //listener.restoreDefaultSettings()
                }
            }
        })

        return MenuBox(itemGameplay, itemControls, itemVideo, itemAudio, btnRestore)
    }

    private fun createExtraMenu(): MenuBox {
        log.debug("createExtraMenu()")

        val itemAchievements = MenuButton("menu.trophies")
        itemAchievements.setMenuContent(Supplier { this.createContentAchievements() })

        val itemCredits = MenuButton("menu.credits")
        itemCredits.setMenuContent(Supplier { this.createContentCredits() })

        val itemFeedback = MenuButton("menu.feedback")
        itemFeedback.setMenuContent(Supplier { this.createContentFeedback() })

        return MenuBox(itemAchievements, itemCredits, itemFeedback)
    }

    private fun switchMenuTo(menu: Node) {
        val oldMenu = menuRoot.children[0]

        val ft = FadeTransition(Duration.seconds(0.33), oldMenu)
        ft.toValue = 0.0
        ft.setOnFinished { e ->
            menu.opacity = 0.0
            menuRoot.children[0] = menu
            oldMenu.opacity = 1.0

            val ft2 = FadeTransition(Duration.seconds(0.33), menu)
            ft2.toValue = 1.0
            ft2.play()
        }
        ft.play()
    }

    private fun switchMenuContentTo(content: Node) {
        menuContentRoot.children[0] = content
    }

    private class MenuBox(vararg items: MenuButton) : VBox() {

        val layoutHeight: Double
            get() = (10 * children.size).toDouble()

        init {
            for (item in items) {
                add(item)
            }
        }

        fun add(item: MenuButton) {
            item.setParent(this)
            children.addAll(item)
        }
    }

    private inner class MenuButton internal constructor(stringKey: String) : Pane() {
        private var parent: MenuBox? = null
        private var cachedContent: MenuContent? = null

        private val p = Polygon(0.0, 0.0, 220.0, 0.0, 250.0, 35.0, 0.0, 35.0)
        val btn: Button

        private var isAnimating = false

        init {
            btn = getUIFactoryService().newButton(localizedStringProperty(stringKey))
            btn.alignment = Pos.CENTER_LEFT
            btn.style = "-fx-background-color: transparent"

            p.isMouseTransparent = true

            val g = LinearGradient(0.0, 1.0, 1.0, 0.2, true, CycleMethod.NO_CYCLE,
                    Stop(0.6, Color.color(1.0, 0.8, 0.0, 0.34)),
                    Stop(0.85, Color.color(1.0, 0.8, 0.0, 0.74)),
                    Stop(1.0, Color.WHITE))

            p.fillProperty().bind(
                    Bindings.`when`(btn.pressedProperty()).then(Color.color(1.0, 0.8, 0.0, 0.75) as Paint).otherwise(g)
            )

            p.stroke = Color.color(0.1, 0.1, 0.1, 0.15)
            p.effect = GaussianBlur()

            p.visibleProperty().bind(btn.hoverProperty())

            children.addAll(btn, p)

            btn.focusedProperty().addListener { _, _, isFocused ->
                if (isFocused) {
                    val isOK = animations.none { it.isAnimating } && !isAnimating
                    if (isOK) {
                        isAnimating = true

                        animationBuilder()
                                .onFinished(Runnable { isAnimating = false })
                                .bobbleDown(this)
                                .buildAndPlay(this@FXGLDefaultMenu)
                    }
                }
            }
        }

        fun setOnAction(e: EventHandler<ActionEvent>) {
            btn.onAction = e
        }

        fun setParent(menu: MenuBox) {
            parent = menu
        }

        fun setMenuContent(contentSupplier: Supplier<MenuContent>, isCached: Boolean = true) {

            btn.addEventHandler(ActionEvent.ACTION) { event ->
                if (cachedContent == null || !isCached)
                    cachedContent = contentSupplier.get()

                switchMenuContentTo(cachedContent!!)
            }
        }

        fun setChild(menu: MenuBox) {
            val back = MenuButton("menu.back")
            menu.children.add(0, back)

            back.addEventHandler(ActionEvent.ACTION) { event -> switchMenuTo(this@MenuButton.parent!!) }

            btn.addEventHandler(ActionEvent.ACTION) { event -> switchMenuTo(menu) }
        }
    }

    private fun createActionButton(name: String, action: Runnable): Button {
        val btn = MenuButton(name)
        btn.addEventHandler(ActionEvent.ACTION) { event -> action.run() }

        return btn.btn
    }

    private fun createActionButton(name: StringBinding, action: Runnable): Button {
        val btn = MenuButton(name.value)
        btn.addEventHandler(ActionEvent.ACTION) { event -> action.run() }

        return btn.btn
    }

// TODO:
//    /**
//     * Switches current active menu body to given.
//     *
//     * @param menuBox parent node containing menu body
//     */
//    protected open fun switchMenuTo(menuBox: Node) {
//        // no default implementation
//    }
//
//    /**
//     * Switches current active content to given.
//     *
//     * @param content menu content
//     */
//    protected open fun switchMenuContentTo(content: Node) {
//        // no default implementation
//    }
//
//    protected abstract fun createActionButton(name: String, action: Runnable): Button
//    protected abstract fun createActionButton(name: StringBinding, action: Runnable): Button
//
//    protected fun createContentButton(name: String, contentSupplier: Supplier<MenuContent>): Button {
//        return createActionButton(name, Runnable { switchMenuContentTo(contentSupplier.get()) })
//    }
//
//    protected fun createContentButton(name: StringBinding, contentSupplier: Supplier<MenuContent>): Button {
//        return createActionButton(name, Runnable { switchMenuContentTo(contentSupplier.get()) })
//    }

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

        getInput().allBindings.forEach { (action, trigger) -> addNewInputBinding(action, trigger, grid) }

        val scroll = FXGLScrollPane(grid)
        scroll.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scroll.maxHeight = getAppHeight() / 2.5

        val hbox = HBox(scroll)
        hbox.alignment = Pos.CENTER

        return MenuContent(hbox)
    }

    private inner class PressAnyKeyState internal constructor() : SubScene() {

        internal var actionContext: UserAction? = null

        var isActive = false

        init {
            input.addEventFilter(KeyEvent.KEY_PRESSED, EventHandler { e ->
                if (Input.isIllegal(e.getCode()))
                    return@EventHandler

                val rebound = getInput().rebind(actionContext!!, e.getCode(), InputModifier.from(e))

                if (rebound) {
                    FXGL.getSceneService().popSubScene()
                    isActive = false
                }
            })

            input.addEventFilter(MouseEvent.MOUSE_PRESSED, EventHandler { e ->
                val rebound = getInput().rebind(actionContext!!, e.getButton(), InputModifier.from(e))

                if (rebound) {
                    FXGL.getSceneService().popSubScene()
                    isActive = false
                }
            })

            val rect = Rectangle(250.0, 100.0)
            rect.stroke = Color.color(0.85, 0.9, 0.9, 0.95)
            rect.strokeWidth = 10.0
            rect.arcWidth = 15.0
            rect.arcHeight = 15.0

            val text = getUIFactoryService().newText("", 24.0)
            text.textProperty().bind(localizedStringProperty("menu.pressAnyKey"))

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
            if (pressAnyKeyState.isActive)
                return@setOnMouseClicked

            pressAnyKeyState.isActive = true
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
     * Show profile dialog so that user selects existing or creates new profile.
     * The dialog is only dismissed when profile is chosen either way.
     */
    private fun showProfileDialog() {
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
}
