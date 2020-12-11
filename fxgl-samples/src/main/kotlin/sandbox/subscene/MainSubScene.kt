/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.subscene

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.scene.SubScene
import javafx.event.EventHandler
import javafx.scene.paint.Color

class MainSubScene : SubScene() {

    init {
        var top = 30.0

        val title = FXGL.getUIFactoryService().newText("Main Game Screen", Color.BLACK, 22.0)
        title.translateX = LEFT
        title.translateY = top
        contentRoot.children.add(title)

        top += VERTICAL_GAP
        val aboutButton = FXGL.getUIFactoryService().newButton("About")
        aboutButton.translateX = LEFT
        aboutButton.translateY = top

        aboutButton.onAction = EventHandler {
            FXGL.getEventBus().fireEvent(NavigateEvent(ABOUT_VIEW))
        }

        contentRoot.children.add(aboutButton)

        top += VERTICAL_GAP
        val optionsButton = FXGL.getUIFactoryService().newButton("Options")
        optionsButton.translateX = LEFT
        optionsButton.translateY = top

        optionsButton.onAction = EventHandler {
            FXGL.getEventBus().fireEvent(NavigateEvent(OPTIONS_VIEW))
        }
        contentRoot.children.add(optionsButton)

        top += VERTICAL_GAP
        val playButton = FXGL.getUIFactoryService().newButton("New Game")
        playButton.translateX = LEFT
        playButton.translateY = top

        playButton.onAction = EventHandler {
            FXGL.getEventBus().fireEvent(NavigateEvent(PLAY_VIEW))
        }
        contentRoot.children.add(playButton)
    }
}
