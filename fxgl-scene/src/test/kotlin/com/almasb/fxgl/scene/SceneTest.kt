/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SceneTest {

    @Test
    fun `Listeners fire on scene update`() {
        val scene = object : Scene() {}

        var count = 0.0

        val listener = object : SceneListener {
            override fun onUpdate(tpf: Double) {
                count = tpf
            }
        }

        scene.addListener(listener)

        scene.update(0.016)
        assertThat(count, `is`(0.016))

        scene.removeListener(listener)

        scene.update(1.0)
        assertThat(count, `is`(0.016))
    }
}