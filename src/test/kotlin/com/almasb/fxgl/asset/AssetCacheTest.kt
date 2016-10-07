/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.asset

import com.almasb.gameutils.Disposable
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AssetCacheTest {

    private lateinit var cache: AssetCache

    @Before
    fun setUp() {
        cache = AssetCache(5)
    }

    @Test
    fun `Test disposable`() {
        val asset1 = DisposableAsset()
        val asset2 = DisposableAsset()
        val asset3 = DisposableAsset()
        val asset4 = DisposableAsset()
        val asset5 = DisposableAsset()

        cache.put("Test1", asset1)
        cache.put("Test2", asset2)
        cache.put("Test3", asset3)
        cache.put("Test4", asset4)
        cache.put("Test5", asset5)

        assertThat(asset1.disposed, `is`(false))
        assertThat(asset2.disposed, `is`(false))
        assertThat(asset3.disposed, `is`(false))
        assertThat(asset4.disposed, `is`(false))
        assertThat(asset5.disposed, `is`(false))

        val asset6 = DisposableAsset()

        cache.put("Test6", asset6)

        assertThat(asset1.disposed, `is`(true))
        assertThat(asset2.disposed, `is`(false))
        assertThat(asset3.disposed, `is`(false))
        assertThat(asset4.disposed, `is`(false))
        assertThat(asset5.disposed, `is`(false))
        assertThat(asset6.disposed, `is`(false))
    }

    private class DisposableAsset : Disposable {
        var disposed = false

        override fun dispose() {
            disposed = true
        }
    }
}