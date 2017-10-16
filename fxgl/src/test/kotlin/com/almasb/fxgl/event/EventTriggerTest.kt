/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EventTriggerTest {

    private lateinit var trigger: EventTrigger<TestEvent>

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