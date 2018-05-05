/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestWindow
@JvmOverloads
constructor(title: String = "Quests", val questPane: QuestPane) : com.almasb.fxgl.ui.InGameWindow(title, WindowDecor.MINIMIZE) {

    init {
//        isResizableWindow = false
//        setPrefSize(questPane.prefWidth + 25, questPane.prefHeight + 32)
//        setBackgroundColor(Color.TRANSPARENT)
//
//        val scroll = ScrollPane(questPane)
//        scroll.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
//        scroll.maxHeight = prefHeight
//        scroll.style = "-fx-background: black;"
//
//        val pane = Pane(scroll)
//
//        contentPane = pane
//
//        val handler = rightIcons[0].onAction
//        rightIcons[0].setOnAction { e ->
//            val st = ScaleTransition(Duration.seconds(0.2), pane)
//            st.fromY = (if (isMinimized) 0 else 1).toDouble()
//            st.toY = (if (isMinimized) 1 else 0).toDouble()
//            st.play()
//
//            handler.handle(e)
//        }
    }
}