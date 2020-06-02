/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.generated.BuildProperties
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class BuildPropertiesTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Build properties are correctly parsed by maven`() {
        assertThat(BuildProperties.VERSION, not(containsString("project.version")))
        assertThat(BuildProperties.BUILD, not(containsString("timestamp")))
    }
}