/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev

import com.almasb.fxgl.animation.AnimatedValue
import javafx.animation.Interpolator

class AnimatedString(from: String, to: String)
    : AnimatedValue<String>(from, to) {

    override fun animate(val1: String, val2: String, progress: Double, interpolator: Interpolator): String {
        // case 1: val1 \in val2
        // TODO: just ignore val1?
        // TODO: extract common interpolator code?
        // TODO: limit interpolator progress to 1.0

        val index = val2.length * interpolator.interpolate(0.0, 1.0, progress)
        return val2.substring(0, index.toInt())


//        return Color.color(
//                interpolator.interpolate(val1.red, val2.red, progress),
//                interpolator.interpolate(val1.green, val2.green, progress),
//                interpolator.interpolate(val1.blue, val2.blue, progress),
//                interpolator.interpolate(val1.opacity, val2.opacity, progress)
//        )
    }
}