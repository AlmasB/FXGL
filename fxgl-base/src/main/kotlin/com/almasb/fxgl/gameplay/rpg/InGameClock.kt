/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.listener.StateListener
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.ReadOnlyIntegerWrapper
import javafx.scene.paint.Color
import javafx.scene.text.Text
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InGameClock(secondsIn24h: Int) : StateListener {

    private var seconds = 0.0

    private val gameHour = ReadOnlyIntegerWrapper()
    private val gameMinute = ReadOnlyIntegerWrapper()

    fun gameHourProperty(): ReadOnlyIntegerProperty {
        return gameHour.readOnlyProperty
    }

    fun gameMinuteProperty(): ReadOnlyIntegerProperty {
        return gameMinute.readOnlyProperty
    }

    /**
     * The range is [0, 23].
     */
    fun getGameHour() = gameHour.value

    /**
     * The range is [0, 59].
     */
    fun getGameMinute() = gameMinute.value

    /**
     * The range is [0, 59].
     */
    var gameSecond = 0
        private set

    /**
     * 24 * 3600 / real seconds in 24h
     */
    private val toGameSeconds = 86400.0 / secondsIn24h

    private var textView: Text? = null

    override fun onUpdate(tpf: Double) {
        seconds += tpf

        var totalGameSeconds = seconds * toGameSeconds

        if (totalGameSeconds > 86400) {
            seconds = 0.0
            totalGameSeconds = 0.0
        }

        gameHour.value = totalGameSeconds.toInt() / 3600
        gameMinute.value = (totalGameSeconds.toInt() / 60) % 60
        gameSecond = totalGameSeconds.toInt() % 60

        isDayTime.value = gameHour.value in dayTimeStart..nightTimeStart-1

        updateActions()
        updateView()
    }

    var dayTimeStart = 8
    var nightTimeStart = 20

    private val isDayTime = ReadOnlyBooleanWrapper(false)

    fun dayProperty(): ReadOnlyBooleanProperty {
        return isDayTime.readOnlyProperty
    }

    fun isDay() = isDayTime.value

    fun isNight() = !isDay()

    private var started = false

    fun start() {
        if (!started) {
            FXGL.getApp().addPlayStateListener(this)
            started = true
        }
    }

    fun stop() {
        if (started) {
            FXGL.getApp().removePlayStateListener(this)
            started = false
        }
    }

    /**
     * @return a Text view of clock in hh:mm format, the same instance of the node is returned
     */
    fun textView(): Text {
        if (textView == null) {
            textView = FXGL.getUIFactory().newText("", Color.BLACK, 16.0)
        }

        return textView!!
    }

    private val actions = CopyOnWriteArrayList<ClockAction>()

    private fun updateActions() {
        actions.filter { gameHour.value > it.hour || (gameHour.value == it.hour && gameMinute.value >= it.minute) }
                .onEach { it.done = true  }
                .forEach { it.action.run() }

        actions.removeIf { it.done }
    }

    private fun updateView() {
        textView?.run {
            val hour = "${gameHour.value}".padStart(2, '0')
            val min = "${gameMinute.value}".padStart(2, '0')

            text = "$hour:$min"
        }
    }

    /**
     * Runs [action] once when at the clock [hour] or past it.
     */
    fun runAtHour(action: Runnable, hour: Int) {
        runAt(action, hour, minute = 0)
    }

    /**
     * Runs [action] once when at the clock [hour]:[minute] or past it.
     */
    fun runAt(action: Runnable, hour: Int, minute: Int) {
        actions.add(ClockAction(hour, minute, action))
    }

    private data class ClockAction(var hour: Int, var minute: Int, var action: Runnable, var done: Boolean = false)
}