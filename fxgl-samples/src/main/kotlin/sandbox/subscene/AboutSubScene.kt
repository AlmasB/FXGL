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

class AboutSubScene : SubScene() {

    init {
        var top = 30.0

        val title = FXGL.getUIFactoryService().newText("About Screen", Color.BLACK, 22.0)
        title.translateX = LEFT
        title.translateY = top
        contentRoot.children.add(title)

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
