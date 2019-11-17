/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.math.FXGLMath.noise1D
import com.almasb.fxgl.core.util.Supplier
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.FXGL.Companion.animationBuilder
import com.almasb.fxgl.dsl.FXGL.Companion.random
import com.almasb.fxgl.dsl.FXGL.Companion.texture
import com.almasb.fxgl.particle.ParticleEmitters
import com.almasb.fxgl.particle.ParticleSystem
import com.almasb.fxgl.ui.FXGLButton
import com.almasb.sslogger.Logger
import javafx.animation.FadeTransition
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.effect.BlendMode
import javafx.scene.effect.GaussianBlur
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.*
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.util.Duration


/**
 * This is the default FXGL menu used if the users
 * don't provide their own. This class provides
 * common structures used in FXGL default menu style.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLDefaultMenu(type: MenuType) : FXGLMenu(type) {

    companion object {
        private val log = Logger.get("FXGL.DefaultMenu")
    }

    private var particleSystem: ParticleSystem? = null

    private var titleColor: ObjectProperty<Color>? = null
    private var t = 0.0

    init {
        if (FXGL.getAppWidth() < 600 || FXGL.getAppHeight() < 600)
            log.warning("FXGLDefaultMenu is not designed for resolutions < 800x600")

        val menu = if (type === MenuType.MAIN_MENU)
            createMenuBodyMainMenu()
        else
            createMenuBodyGameMenu()

        val menuX = 50.0
        val menuY = FXGL.getAppHeight() / 2 - menu.layoutHeight / 2

        menuRoot.translateX = menuX
        menuRoot.translateY = menuY

        menuContentRoot.translateX = (FXGL.getAppWidth() - 500).toDouble()
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
        emitter.setSpawnPointFunction { i -> Point2D(random(0, FXGL.getAppWidth() - 200).toDouble(), 120.0) }

        particleSystem!!.addParticleEmitter(emitter, 0.0, FXGL.getAppHeight().toDouble())

        contentRoot.children.add(3, particleSystem!!.pane)

        menuRoot.children.addAll(menu)
        menuContentRoot.children.add(EMPTY)

        activeProperty().addListener { observable, wasActive, isActive ->
            if (!isActive) {
                // the scene is no longer active so reset everything
                // so that next time scene is active everything is loaded properly
                switchMenuTo(menu)
                switchMenuContentTo(EMPTY)
            }
        }
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

    override fun onUpdate(tpf: Double) {
        animations.forEach { it.onUpdate(tpf) }

        val frequency = 1.7

        t += tpf * frequency

        particleSystem!!.onUpdate(tpf)

        val color = Color.color(1.0, 1.0, 1.0, noise1D(t))
        titleColor!!.set(color)
    }

    override fun createBackground(width: Double, height: Double): Node {
        val bg = Rectangle(width, height)
        bg.fill = Color.rgb(10, 1, 1)
        return bg
    }

    override fun createTitleView(title: String): Node {
        titleColor = SimpleObjectProperty(Color.WHITE)

        val text = FXGL.getUIFactory().newText(title.substring(0, 1), 50.0)
        text.fill = null
        text.strokeProperty().bind(titleColor)
        text.strokeWidth = 1.5

        val text2 = FXGL.getUIFactory().newText(title.substring(1, title.length), 50.0)
        text2.fill = null
        text2.stroke = titleColor!!.value
        text2.strokeWidth = 1.5

        val textWidth = text.layoutBounds.width + text2.layoutBounds.width

        val bg = Rectangle(textWidth + 30, 65.0, null)
        bg.stroke = Color.WHITE
        bg.strokeWidth = 4.0
        bg.arcWidth = 25.0
        bg.arcHeight = 25.0

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
        titleRoot.children.addAll(bg, box)

        titleRoot.translateX = FXGL.getAppWidth() / 2 - (textWidth + 30) / 2
        titleRoot.translateY = 50.0

        particleSystem = ParticleSystem()
        particleSystem!!.addParticleEmitter(emitter, (FXGL.getAppWidth() / 2 - 30).toDouble(), titleRoot.translateY + 34)

        return titleRoot
    }

    override fun createVersionView(version: String): Node {
        val view = FXGL.getUIFactory().newText(version)
        view.translateY = (FXGL.getAppHeight() - 2).toDouble()
        return view
    }

    override fun createProfileView(profileName: String): Node {
        val view = FXGL.getUIFactory().newText(profileName)
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
            itemLoad.setMenuContent(Supplier { this.createContentLoad() })

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

        val itemExit = MenuButton("menu.mainMenu")
        itemExit.setOnAction(EventHandler{ fireExitToMainMenu() })
        box.add(itemExit)

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
            FXGL.getDisplay().showConfirmationBox(FXGL.localize("menu.settingsRestore")) { yes ->
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

    override fun switchMenuTo(menu: Node) {
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

    override fun switchMenuContentTo(content: Node) {
        menuContentRoot.children[0] = content
    }

    internal class MenuBox internal constructor(vararg items: MenuButton) : VBox() {

        internal val layoutHeight: Double
            get() = (10 * children.size).toDouble()

        init {
            for (item in items) {
                add(item)
            }
        }

        internal fun add(item: MenuButton) {
            item.setParent(this)
            children.addAll(item)
        }
    }

    internal inner class MenuButton internal constructor(stringKey: String) : Pane() {
        private var parent: MenuBox? = null
        private var cachedContent: MenuContent? = null

        private val p = Polygon(0.0, 0.0, 220.0, 0.0, 250.0, 35.0, 0.0, 35.0)
        val btn: FXGLButton

        private var isAnimating = false

        init {
            btn = FXGLButton()
            btn.alignment = Pos.CENTER_LEFT
            btn.style = "-fx-background-color: transparent"
            btn.textProperty().bind(FXGL.localizedStringProperty(stringKey))

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

        fun setMenuContent(contentSupplier: Supplier<MenuContent>) {

            btn.addEventHandler(ActionEvent.ACTION) { event ->
                if (cachedContent == null)
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

    override fun createActionButton(name: String, action: Runnable): Button {
        val btn = MenuButton(name)
        btn.addEventHandler(ActionEvent.ACTION) { event -> action.run() }

        return btn.btn
    }

    override fun createActionButton(name: StringBinding, action: Runnable): Button {
        val btn = MenuButton(name.value)
        btn.addEventHandler(ActionEvent.ACTION) { event -> action.run() }

        return btn.btn
    }
}
