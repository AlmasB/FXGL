/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.entity.Entity
import javafx.geometry.Rectangle2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ViewportTest {

    @Test
    fun `Visible area`() {
        val viewport = Viewport(800.0, 600.0)

        assertThat(viewport.visibleArea, `is`(Rectangle2D(0.0, 0.0, 800.0, 600.0)))

        viewport.x = 300.0
        viewport.y = 300.0

        assertThat(viewport.visibleArea, `is`(Rectangle2D(300.0, 300.0, 800.0, 600.0)))

        viewport.setZoom(2.0)

        assertThat(viewport.visibleArea, `is`(Rectangle2D(300.0, 300.0, 400.0, 300.0)))
    }

    @Test
    fun `Binding`() {
        val entity = Entity()

        val viewport = Viewport(800.0, 600.0)

        viewport.bindToEntity(entity, 0.0, 0.0)

        entity.x = 150.0

        viewport.onUpdate(0.016)

        assertThat(viewport.x, `is`(150.0))

        // unbind
        viewport.unbind()

        entity.x = 300.0
        viewport.onUpdate(0.016)
        assertThat(viewport.x, `is`(150.0))
    }
}