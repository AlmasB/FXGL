/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.time.Timer
import javafx.util.Duration
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LiftComponentTest {

    private lateinit var lift: LiftComponent
    private lateinit var e: Entity

    @BeforeEach
    fun `setUp`() {
        val timer = Timer().newLocalTimer()

        lift = LiftComponent(timer)
        e = Entity()

        e.addComponent(lift)
    }

    @Test
    fun `Lift moving in XY axes with speed distance`() {
        lift.xAxisSpeedDistance(100.0, 300.0)
        lift.yAxisSpeedDistance(100.0, 300.0)

        lift.onUpdate(3.0)

        assertThat(e.x, `is`(300.0))
        assertThat(e.y, `is`(300.0))
    }

    @Test
    fun `Lift moving in XY axes with distance duration`() {
        lift.xAxisDistanceDuration(100.0, Duration.seconds(3.0))
        lift.yAxisDistanceDuration(100.0, Duration.seconds(3.0))

        lift.onUpdate(1.5)

        assertThat(e.x, `is`(50.0))
        assertThat(e.y, `is`(50.0))

        lift.onUpdate(1.5)

        assertThat(e.x, `is`(100.0))
        assertThat(e.y, `is`(100.0))
    }

    @Test
    fun `Lift moving in XY axes with speed duration`() {
        lift.xAxisSpeedDuration(100.0, Duration.seconds(3.0))
        lift.yAxisSpeedDuration(100.0, Duration.seconds(3.0))

        lift.onUpdate(3.0)

        assertThat(e.x, `is`(300.0))
        assertThat(e.y, `is`(300.0))
    }
}