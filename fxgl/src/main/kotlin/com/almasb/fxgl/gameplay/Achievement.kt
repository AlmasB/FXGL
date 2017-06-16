/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay


import com.almasb.fxgl.app.FXGL
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.value.ChangeListener

/**
 * A game achievement.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Achievement(

        /**
         * @return achievement name
         */
        val name: String,

        /**
         * Returns description.
         * This usually contains info on how
         * to unlock the achievement.
         *
         * @return achievement description
         */
        val description: String) {

    companion object {
        private val log = FXGL.getLogger("FXGL.Achievement")
    }

    private var onAchieved: Runnable? = null

    private val achieved = ReadOnlyBooleanWrapper(false)
    private val listener = ChangeListener<Boolean> { o, oldValue, newValue ->
        if (newValue) {
            setAchieved()
            if (onAchieved != null)
                onAchieved!!.run()
            else
                log.warning("onAchieved was not set. Unmanaged achievement!")
        }
    }

    init {
        achieved.addListener(listener)
    }

    internal fun setOnAchieved(onAchieved: Runnable) {
        this.onAchieved = onAchieved
    }

    internal fun setAchieved() {
        achieved.removeListener(listener)
        achieved.unbind()
        achieved.set(true)
    }

    /**
     * @return true iff the achievement has been unlocked
     */
    val isAchieved: Boolean
        get() = achievedProperty().get()

    /**
     * @return achieved boolean property (read-only)
     */
    fun achievedProperty(): ReadOnlyBooleanProperty {
        return achieved.readOnlyProperty
    }

    /**
     * Binds achievement to given binding (condition).
     * No-op if achievement is already unlocked.
     *
     * @param binding condition on which achievement is unlocked
     */
    fun bind(binding: BooleanBinding) {
        if (!isAchieved)
            achieved.bind(binding)
    }

    private var progressListener: ChangeListener<Boolean>? = null

    /**
     * Bind achievement condition to given property.
     *
     * @param property the property
     * @param value the value at which the achievement is unlocked
     */
    fun bind(property: IntegerProperty, value: Int) {
        if (isAchieved)
            return

        bind(property.greaterThanOrEqualTo(value))
        val bb = property.greaterThanOrEqualTo(value / 2)

        progressListener = ChangeListener<Boolean> { o, fired, reachedHalf ->
            if (reachedHalf && (!fired)) {
                FXGL.getEventBus().fireEvent(AchievementProgressEvent(this, property.get().toDouble(), value.toDouble()))
                bb.removeListener(progressListener)
            }
        }

        bb.addListener(progressListener)
    }

    override fun toString(): String {
        return "$name:achieved($isAchieved)"
    }
}
