/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.serialization.SerializableType

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputCapture : TriggerListener(), SerializableType {

    private var timeSinceCaptureStarted = 0.0

    private val data = arrayListOf<CaptureData>()

    fun update(tpf: Double) {
        timeSinceCaptureStarted += tpf
    }

    override fun onActionBegin(trigger: Trigger) {
        data += CaptureData(timeSinceCaptureStarted, trigger, isPressed = true)
    }

    override fun onActionEnd(trigger: Trigger) {
        data += CaptureData(timeSinceCaptureStarted, trigger, isPressed = false)
    }

    override fun write(bundle: Bundle) {
        bundle.put("size", data.size)

        data.forEachIndexed { i, captureData ->
            val b = Bundle("Data$i")
            with(b) {
                put("time", captureData.time)
                put("isPressed", captureData.isPressed)
                put("isKeyTrigger", captureData.trigger.isKey)

                if (captureData.trigger.isKey) {
                    put("key", ((captureData.trigger) as KeyTrigger).key)
                } else {
                    put("btn", ((captureData.trigger) as MouseTrigger).button)
                }
            }

            bundle.put(b.name, b)
        }
    }

    override fun read(bundle: Bundle) {
        val size = bundle.get<Int>("size")

        for (i in 0 until size) {
            val b = bundle.get<Bundle>("Data$i")

            val time = b.get<Double>("time")
            val isPressed = b.get<Boolean>("isPressed")
            val isKeyTrigger = b.get<Boolean>("isKeyTrigger")

            val trigger: Trigger = if (isKeyTrigger) {
                KeyTrigger(b.get("key"))
            } else {
                MouseTrigger(b.get("btn"))
            }

            data += CaptureData(time, trigger, isPressed)
        }
    }

    internal class CaptureApplier(private val input: Input, private val capture: InputCapture) {
        private var time = 0.0
        private var index = 0

        fun update(tpf: Double) {
            if (index == capture.data.size)
                return

            time += tpf

            while (index < capture.data.size) {
                val data = capture.data[index]

                if (data.time <= time) {
                    if (data.isPressed) {
                        input.mockTriggerPress(data.trigger)
                    } else {
                        input.mockTriggerRelease(data.trigger)
                    }

                    index++
                } else {
                    break
                }
            }
        }
    }
}

private class CaptureData(
        val time: Double,
        val trigger: Trigger,
        val isPressed: Boolean
)