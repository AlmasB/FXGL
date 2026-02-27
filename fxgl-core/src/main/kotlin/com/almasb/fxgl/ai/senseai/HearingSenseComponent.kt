/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.senseai

import com.almasb.fxgl.ai.senseai.SenseAIState.*
import com.almasb.fxgl.entity.component.Component
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import kotlin.math.max

/**
 * Adds the ability to "hear" "noises" of interest in the game environment.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class HearingSenseComponent(

        /**
         * Noise heard outside this radius will be ignored.
         */
        var hearingRadius: Double

): Component() {

    /**
     * Drives the change in [state].
     */
    private var alertness = 0.0

    /**
     * How quickly does the entity lose interest in being alert / aggressive.
     */
    var alertnessDecay = 0.1

    /**
     * Noise heard with volume less than or equal to this value will be ignored.
     */
    var noiseVolumeTolerance: Double = 0.0

    /**
     * When in [SenseAIState.CALM], only a portion of the noise volume will be heard.
     * By default the value is 0.5 (=50%).
     */
    var calmFactor: Double = 0.5

    /**
     * Alertness equal to or above this value will trigger state change to [SenseAIState.ALERT].
     */
    var alertStateThreshold: Double = 0.5

    /**
     * Alertness equal to or above this value will trigger state change to [SenseAIState.AGGRESSIVE].
     */
    var aggressiveStateThreshold: Double = 0.75

    /**
     * The position of the last heard noise.
     */
    var lastHeardPoint = Point2D.ZERO

    private val stateProp = SimpleObjectProperty(CALM)

    var state: SenseAIState
        get() = stateProp.value
        set(value) { stateProp.value = value }

    fun stateProperty() = stateProp

    override fun onUpdate(tpf: Double) {
        alertness = max(0.0, alertness - alertnessDecay * tpf)

        if (alertness >= aggressiveStateThreshold) {
            state = AGGRESSIVE
        } else if (alertness >= alertStateThreshold) {
            state = ALERT
        } else {
            state = CALM
        }
    }

    /**
     * Trigger this sense to hear a noise at [point] with given [volume].
     * The [volume] diminishes based on distance between [point] and the entity.
     * Based on [state], [hearingRadius] and [noiseVolumeTolerance] this noise may be ignored.
     */
    fun hearNoise(point: Point2D, volume: Double) {
        if (state == CANNOT_BE_DISTURBED)
            return

        if (volume <= noiseVolumeTolerance)
            return

        val distance = entity.position.distance(point)

        if (distance > hearingRadius)
            return

        lastHeardPoint = point

        val stateVolumeRatio = if (state == CALM) calmFactor else 1.0

        val adjustedVolume = volume * (1.0 - (distance / hearingRadius)) * stateVolumeRatio

        alertness += adjustedVolume
    }

    fun alertnessDecay(decayAmount: Double) = this.apply {
        this.alertnessDecay = decayAmount
    }

    fun noiseVolumeTolerance(noiseVolumeTolerance: Double) = this.apply {
        this.noiseVolumeTolerance = noiseVolumeTolerance
    }

    fun calmFactor(calmFactor: Double) = this.apply {
        this.calmFactor = calmFactor
    }

    fun alertStateThreshold(alertThreshold: Double) = this.apply {
        this.alertStateThreshold = alertThreshold
    }

    fun aggressiveStateThreshold(aggressiveStateThreshold: Double) = this.apply {
        this.aggressiveStateThreshold = aggressiveStateThreshold
    }

    fun lastHeardPoint(lastHeardPoint: Point2D) = this.apply {
        this.lastHeardPoint = lastHeardPoint
    }

}