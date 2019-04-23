/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.entity.components.BooleanComponent
import com.almasb.fxgl.entity.components.DoubleComponent
import com.almasb.fxgl.entity.components.IntegerComponent
import com.almasb.fxgl.entity.components.StringComponent
import com.almasb.fxgl.core.serialization.Bundle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ComponentHelperTest {

    @Test
    fun `ctor`() {
        ComponentHelper()
    }
}