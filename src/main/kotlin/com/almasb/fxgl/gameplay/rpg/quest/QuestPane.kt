/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.app.FXGL
import javafx.collections.ListChangeListener
import javafx.scene.layout.VBox

/**
 * Convenient pane that contains quests in a vertical layout.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestPane(width: Double, height: Double) : VBox() {

    init {
        prefWidth = width
        prefHeight = height

        children.addAll(FXGL.getQuestManager().questsProperty().map { QuestView(it, prefWidth) })

        FXGL.getQuestManager().questsProperty().addListener(ListChangeListener { c ->
            while (c.next()) {

                if (c.wasAdded()) {
                    c.addedSubList.forEach { children.add(QuestView(it, prefWidth)) }
                } else if (c.wasRemoved()) {
                    c.removed.map { getView(it) }.forEach { children.remove(it) }
                }
            }
        })
    }

    private fun getView(quest: Quest) = children.map { it as QuestView }.filter { it.quest === quest }.firstOrNull()
}