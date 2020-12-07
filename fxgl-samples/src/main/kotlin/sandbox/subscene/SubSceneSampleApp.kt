/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.subscene

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.scene.SubScene
import javafx.event.EventHandler
import javafx.util.Duration

/**
 * @author Serge Merzliakov (smerzlia@gmail.com)
 */
class SubSceneSampleApp : GameApplication() {


	override fun initSettings(settings: GameSettings) {
		settings.width = 700
		settings.height = 400
		settings.title = "SubScene Navigation Demo"
		settings.isMainMenuEnabled = false
		settings.isGameMenuEnabled = false
		settings.isIntroEnabled = false
	}

	override fun initGame() {

		val handler: EventHandler<NavigateEvent> = EventHandler { e: NavigateEvent? ->
			handleNavigate(e!!)
		}

		FXGL.getEventBus().addEventHandler(NAVIGATION, handler)
	}

	override fun initUI() {
		FXGL.run(Runnable {
			FXGL.getSceneService().pushSubScene(MainSubScene())
		}, Duration.seconds(0.0))

	}

	private fun handleNavigate(e: NavigateEvent) {
		var subScene: SubScene? = null
		when (e.eventType) {
			MAIN_VIEW -> subScene = MainSubScene()
			ABOUT_VIEW -> subScene = AboutSubScene()
			OPTIONS_VIEW -> subScene = OptionsSubScene()
			PLAY_VIEW -> subScene = PlaySubScene()
		}

		FXGL.getSceneService().popSubScene()
		FXGL.getSceneService().pushSubScene(subScene!!)
	}
}

fun main(args: Array<String>) {
	GameApplication.launch(SubSceneSampleApp::class.java, args)
}
