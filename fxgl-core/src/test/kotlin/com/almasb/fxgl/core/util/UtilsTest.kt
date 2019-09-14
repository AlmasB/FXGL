/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UtilsTest {

    @Test
    fun `Try catch root runs normall if no exception`() {
        var s = tryCatchRoot(Supplier { "Test" })
        assertThat(s, `is`("Test"))

        s = tryCatchRoot { "Hello" }
        assertThat(s, `is`("Hello"))
    }

    @Test
    fun `Try catch root throws root exception and not last exception`() {
        assertAll(
                Executable {
                    assertThrows<IllegalArgumentException> {

                        tryCatchRoot(Supplier {
                            try {
                                throw IllegalArgumentException()
                            } catch (e: Exception) {
                                throw RuntimeException(e)
                            }
                        })
                    }
                },

                Executable {
                    assertThrows<IllegalStateException> {

                        tryCatchRoot {
                            try {
                                throw IllegalStateException()
                            } catch (e: Exception) {
                                throw RuntimeException(e)
                            }
                        }
                    }
                }
        )
    }
}