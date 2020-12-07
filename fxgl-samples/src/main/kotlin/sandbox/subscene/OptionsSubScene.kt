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

class OptionsSubScene : SubScene() {

    init {
        var top = 30.0
        val text = FXGL.getUIFactoryService().newText("Options Screen", Color.BLACK, 22.0)
        text.translateX = LEFT
        text.translateY = top
        contentRoot.children.add(text)

        top += VERTICAL_GAP
        val exitButton = FXGL.getUIFactoryService().newButton("Main Menu")
        exitButton.translateX = LEFT
        exitButton.translateY = top

        exitButton.onAction = EventHandler {
            FXGL.getEventBus().fireEvent(NavigateEvent(MAIN_VIEW))
        }
        contentRoot.children.add(exitButton)
    }
}
