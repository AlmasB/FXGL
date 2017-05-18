/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.event

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import org.junit.BeforeClass

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EventTriggerTest {

    private lateinit var trigger: EventTrigger<TestEvent>

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    // do not run until event trigger has been refactored to new model
//    @Test
//    fun `Event does not fire after reaching limit`() {
//        var count = 0
//
//        trigger = EventTrigger(object : EventCondition {
//            override fun isTrue(): Boolean {
//                return true
//            }
//        }, object : EventProducer<TestEvent> {
//            override fun produce(): TestEvent {
//                count++
//                return TestEvent(TestEvent.ANY)
//            }
//        }, limit = 3, interval = Duration.millis(10.0))
//
//        trigger.fire()
//        assertThat(count, `is`(1))
//        assertThat(trigger.reachedLimit(), `is`(false))
//
//        trigger.fire()
//        assertThat(count, `is`(2))
//        assertThat(trigger.reachedLimit(), `is`(false))
//
//        trigger.fire()
//        assertThat(count, `is`(3))
//        assertThat(trigger.reachedLimit(), `is`(true))
//
//        trigger.fire()
//        assertThat(count, `is`(3))
//        assertThat(trigger.reachedLimit(), `is`(true))
//    }

    // we cannot run this test because there is no mock master timer that we can drive manually
//    @Test
//    fun `Event fires after given interval`() {
//        var count = 0
//
//        trigger = EventTrigger(object : EventCondition {
//            override fun isTrue(): Boolean {
//                return true
//            }
//        }, object : EventProducer<TestEvent> {
//            override fun produce(): TestEvent {
//                count++
//                return TestEvent(TestEvent.ANY)
//            }
//        }, limit = 3, interval = Duration.millis(10.0))
//
//        trigger.onUpdateEvent(UpdateEvent(0, 0.016))
//        assertThat(count, `is`(1))
//
//        trigger.onUpdateEvent(UpdateEvent(0, 0.016))
//        assertThat(count, `is`(2))
//
//        trigger.onUpdateEvent(UpdateEvent(0, 0.016))
//        assertThat(count, `is`(3))
//
//        trigger.onUpdateEvent(UpdateEvent(0, 0.016))
//        assertThat(count, `is`(3))
//    }
}