/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.input

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.input.virtual.VirtualButton
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputTest {

    private lateinit var input: Input

    @BeforeEach
    fun setUp() {
        input = Input()
    }

    @Test
    fun `Empty actions`() {
        val action = object : UserAction("T") {}

        action.begin()
        action.action()
        action.end()

        val listener = object : TriggerListener() {}
        listener.begin(KeyTrigger(KeyCode.A))
        listener.action(KeyTrigger(KeyCode.A))
        listener.end(KeyTrigger(KeyCode.A))
    }

    @Test
    fun `Action is not equal to String`() {
        val action = object : UserAction("T") {}

        assertFalse(action.equals("T"))
    }

    @Test
    fun `Action equality`() {
        val action1: UserAction = object : UserAction("Up") {}
        val action2: UserAction = object : UserAction("Up") {}
        assertTrue(action1 == action2)

        val action3: UserAction = object : UserAction("Down") {}
        assertFalse(action1 == action3)
    }

    @Test
    fun `Test registering input does not affect mocking`() {
        assertTrue(input.registerInput)

        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onActionEnd() {
                calls = -1
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))

        // this must not affect mocking
        input.registerInput = false

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))
    }

    @Test
    fun `Test processing input affects mocking`() {
        assertTrue(input.processInput)

        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onActionEnd() {
                calls = -1
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))

        // this must affect mocking
        input.processInput = false

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(-1))

        input.mockKeyRelease(KeyCode.A)
    }

    @Test
    fun `Mock with input modifiers`() {
        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onActionEnd() {
                calls = -1
            }
        }, KeyCode.A, InputModifier.CTRL)

        input.mockKeyPress(KeyCode.A, InputModifier.CTRL)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))

        // without input modifier, so input should ignore it
        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(-1))

        input.mockKeyPress(KeyCode.A, InputModifier.CTRL)
        assertThat(calls, `is`(1))

        // shift and alt should not trigger release
        input.mockKeyRelease(KeyCode.SHIFT)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.ALT)
        assertThat(calls, `is`(1))

        // control will since we specified it as input modifier
        input.mockKeyRelease(KeyCode.CONTROL)
        assertThat(calls, `is`(-1))
    }

    @Test
    fun `Input sequence`() {
        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls++
            }

            override fun onAction() {
                calls++
            }

            override fun onActionEnd() {
                calls = 99
            }
        }, InputSequence(KeyCode.A, KeyCode.D, KeyCode.F))

        input.mockKeyPress(KeyCode.A)
        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(0))

        input.mockKeyPress(KeyCode.D)
        input.mockKeyRelease(KeyCode.D)
        assertThat(calls, `is`(0))

        input.mockKeyPress(KeyCode.D)
        input.mockKeyRelease(KeyCode.D)
        assertThat(calls, `is`(0))

        input.mockKeyPress(KeyCode.F)
        input.mockKeyRelease(KeyCode.F)
        assertThat(calls, `is`(0))

        input.mockKeyPress(KeyCode.A)
        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(0))

        input.mockKeyPress(KeyCode.D)
        input.mockKeyRelease(KeyCode.D)
        assertThat(calls, `is`(0))

        input.mockKeyPress(KeyCode.F)
        assertThat(calls, `is`(1))

        input.update(1.0)
        assertThat(calls, `is`(2))

        input.update(1.0)
        assertThat(calls, `is`(3))

        input.update(1.0)
        assertThat(calls, `is`(4))

        input.mockKeyRelease(KeyCode.F)

        assertThat(calls, `is`(99))

        input.update(1.0)
        assertThat(calls, `is`(99))
    }

    @Test
    fun `Test update`() {
        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onAction() {
                calls++
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(0))

        input.update(0.016)
        assertThat(calls, `is`(1))

        input.update(0.016)
        assertThat(calls, `is`(2))

        input.mockKeyRelease(KeyCode.A)

        input.update(0.016)
        assertThat(calls, `is`(2))

        input.processInput = false

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(2))

        // process input is false so update shouldn't happen
        input.update(0.016)
        assertThat(calls, `is`(2))

        input.update(0.016)
        assertThat(calls, `is`(2))

        // turn back on
        input.processInput = true

        input.update(0.016)
        assertThat(calls, `is`(3))

        input.update(0.016)
        assertThat(calls, `is`(4))
    }

    @Test
    fun `Test mouse cursor in-game coordinates`() {
        assertThat(input.mouseXUI, `is`(0.0))
        assertThat(input.mouseYUI, `is`(0.0))
        assertThat(input.mouseXWorld, `is`(0.0))
        assertThat(input.mouseYWorld, `is`(0.0))

        input.mockButtonPress(MouseButton.PRIMARY, 100.0, 50.0)

        assertThat(input.mouseXUI, `is`(0.0))
        assertThat(input.mouseYUI, `is`(0.0))
        assertThat(input.mouseXWorld, `is`(100.0))
        assertThat(input.mouseYWorld, `is`(50.0))

        input.mockButtonPress(MouseButton.SECONDARY)

        assertThat(input.mouseXUI, `is`(0.0))
        assertThat(input.mouseYUI, `is`(0.0))
        assertThat(input.mouseXWorld, `is`(100.0))
        assertThat(input.mouseYWorld, `is`(50.0))

        input.mockButtonRelease(MouseButton.PRIMARY, 50.0, 30.0)

        assertThat(input.mouseXUI, `is`(0.0))
        assertThat(input.mouseYUI, `is`(0.0))
        assertThat(input.mouseXWorld, `is`(50.0))
        assertThat(input.mouseYWorld, `is`(30.0))

        input.mockButtonRelease(MouseButton.SECONDARY);

        assertThat(input.mouseXUI, `is`(0.0))
        assertThat(input.mouseYUI, `is`(0.0))
        assertThat(input.mouseXWorld, `is`(50.0))
        assertThat(input.mouseYWorld, `is`(30.0))

        assertThat(input.mousePositionUI, `is`(Point2D(0.0, 0.0)))
        assertThat(input.mousePositionWorld, `is`(Point2D(50.0, 30.0)))
    }

    @Test
    fun `Mouse vectors`() {
        input.mockButtonPress(MouseButton.PRIMARY, 100.0, 50.0)
        input.mockButtonRelease(MouseButton.PRIMARY)

        assertThat(input.getVectorToMouse(Point2D(10.0, 10.0)), `is`(Point2D(90.0, 40.0)))
        assertThat(input.getVectorFromMouse(Point2D(10.0, 10.0)), `is`(Point2D(-90.0, -40.0)))
    }

    @Test
    fun testKeyBinding() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, KeyCode.A)

        assertThat(input.allBindings.keys, hasItem(action))

        val trigger = input.allBindings[action]

        assertTrue(trigger is KeyTrigger)
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.A))
    }

    @Test
    fun testMouseBinding() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, MouseButton.PRIMARY)

        assertThat(input.allBindings.keys, hasItem(action))

        val trigger = input.allBindings[action]

        assertTrue(trigger is MouseTrigger)
        assertThat((trigger as MouseTrigger).button, `is`(MouseButton.PRIMARY))
    }

    @Test
    fun `Do not allow UserActions with same name`() {
        input.addAction(object : UserAction("Action1") {}, KeyCode.A)

        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Action1") {}, KeyCode.B)
        })
    }

    @Test
    fun `Do not allow bindings to same key`() {
        input.addAction(object : UserAction("Action1") {}, KeyCode.A)

        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Action2") {}, KeyCode.A)
        })
    }

    @Test
    fun `Do not allow bindings to same button`() {
        input.addAction(object : UserAction("Action1") {}, MouseButton.PRIMARY)

        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Action2") {}, MouseButton.PRIMARY)
        })
    }

    @Test
    fun `Do not allow binding to Ctrl`() {
        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Test") {}, KeyCode.CONTROL)
        })
    }

    @Test
    fun `Do not allow binding to Shift`() {
        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Test") {}, KeyCode.SHIFT)
        })
    }

    @Test
    fun `Do not allow binding to Alt`() {
        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Test") {}, KeyCode.ALT)
        })
    }

    @Test
    fun `Test rebind key`() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, KeyCode.A)
        input.addAction(object : UserAction("Action2") {}, KeyCode.B)

        // binding to existing key must not succeed
        var ok = input.rebind(action, KeyCode.B)
        assertThat(ok, `is`(false))

        // binding to non-existent action must not succeed
        ok = input.rebind(object : UserAction("Action3") {}, KeyCode.C)
        assertThat(ok, `is`(false))

        ok = input.rebind(action, KeyCode.C)
        assertThat(ok, `is`(true))

        val trigger = input.allBindings[action]

        assertTrue(trigger is KeyTrigger)
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.C))
    }

    @Test
    fun `Test rebind mouse button`() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, MouseButton.PRIMARY)
        input.addAction(object : UserAction("Action2") {}, MouseButton.SECONDARY)

        // binding to existing button must not succeed
        var ok = input.rebind(action, MouseButton.SECONDARY)
        assertThat(ok, `is`(false))

        ok = input.rebind(action, MouseButton.MIDDLE)
        assertThat(ok, `is`(true))

        val trigger = input.allBindings[action]

        assertTrue(trigger is MouseTrigger)
        assertThat((trigger as MouseTrigger).button, `is`(MouseButton.MIDDLE))
    }

    @Test
    fun `Trigger name property throws if not such action`() {
        assertThrows(IllegalArgumentException::class.java) {
            input.triggerNameProperty(object : UserAction("") {})
        }
    }

    @Test
    fun `Trigger name by action`() {
        val action = object : UserAction("Action") {}

        input.addAction(action, KeyCode.K)

        assertThat(input.getTriggerName(action), `is`("K"))
    }

    @Test
    fun `Action by name`() {
        val action = object : UserAction("Action") {}

        input.addAction(action, KeyCode.K)

        assertThat(input.getActionByName("Action"), `is`<UserAction>(action))
    }

    @Test
    fun `Action by name throws if action not found`() {
        assertThrows(IllegalArgumentException::class.java) {
            input.getActionByName("Action")
        }
    }

    @Test
    fun `Trigger name by action name`() {
        val action = object : UserAction("Action") {}

        input.addAction(action, KeyCode.K)

        assertThat(input.getTriggerName("Action"), `is`("K"))
    }

    @Test
    fun `Trigger by action`() {
        val action = object : UserAction("Action") {}

        input.addAction(action, KeyCode.K)

        assertThat(input.triggerProperty(action).value.name, `is`("K"))
    }

    @Test
    fun `Trigger by action throws if action not found`() {
        assertThrows(IllegalArgumentException::class.java) {
            input.triggerProperty(object : UserAction("Action") {})
        }
    }

    @Test
    fun `Fire JavaFX event`() {
        var count = 0

        val handler = EventHandler<Event> { count++ }

        assertAll(
                // filter
                Executable {
                    input.addEventFilter(EventType.ROOT, handler)

                    input.fireEventViaFilters(Event(EventType.ROOT))

                    assertThat(count, `is`(1))
                },

                Executable {
                    input.removeEventFilter(EventType.ROOT, handler)

                    input.fireEventViaFilters(Event(EventType.ROOT))

                    assertThat(count, `is`(1))
                },

                // handler
                Executable {
                    input.addEventHandler(EventType.ROOT, handler)

                    input.fireEventViaHandlers(Event(EventType.ROOT))

                    assertThat(count, `is`(2))
                },

                Executable {
                    input.removeEventHandler(EventType.ROOT, handler)

                    input.fireEventViaHandlers(Event(EventType.ROOT))

                    assertThat(count, `is`(2))
                },

                // hybrid
                Executable {
                    input.addEventFilter(EventType.ROOT, handler)

                    input.fireEventViaHandlers(Event(EventType.ROOT))

                    // no effect, firing handlers but only filter is registered
                    assertThat(count, `is`(2))

                    input.removeEventFilter(EventType.ROOT, handler)
                },

                Executable {
                    input.addEventHandler(EventType.ROOT, handler)

                    input.fireEventViaFilters(Event(EventType.ROOT))

                    // no effect, firing filters but only handler is registered
                    assertThat(count, `is`(2))
                }
        )
    }

    @Test
    fun `Concurrent access to filters and handlers`() {
        var count = 0

        var handler1: EventHandler<MouseEvent>? = null
        var handler2: EventHandler<MouseEvent>? = null

        handler1 = EventHandler<MouseEvent> {
            count++

            input.removeEventHandler(MouseEvent.ANY, handler1!!)
            input.addEventHandler(MouseEvent.ANY, handler2!!)

            assertThat(count, `is`(1))
        }

        handler2 = EventHandler<MouseEvent> {
            count++

            input.removeEventHandler(MouseEvent.ANY, handler2!!)

            assertThat(count, `is`(2))
        }

        input.addEventHandler(MouseEvent.ANY, handler1)

        val event = MouseEvent(MouseEvent.MOUSE_CLICKED, 10.0, 15.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        // removes handler1, adds handler2
        input.fireEventViaHandlers(event)

        // removes handler2
        input.fireEventViaHandlers(event)

        assertThat(count, `is`(2))
    }

    @Test
    fun `Consumed events do not travel any further`() {
        var count = 0

        var handler1: EventHandler<MouseEvent>? = null
        var handler2: EventHandler<MouseEvent>? = null

        handler1 = EventHandler<MouseEvent> {
            count++

            it.consume()

            assertThat(count, `is`(1))
        }

        handler2 = EventHandler<MouseEvent> {
            count++
        }

        input.addEventHandler(MouseEvent.ANY, handler1)
        input.addEventHandler(MouseEvent.ANY, handler2)

        val event = MouseEvent(MouseEvent.MOUSE_CLICKED, 10.0, 15.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        input.fireEventViaHandlers(event)

        // event should have been consumed by handler1, so count == 1
        assertThat(count, `is`(1))
    }

    @Test
    fun `On key event`() {
        var count = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                count++
            }

            override fun onActionEnd() {
                count--
            }
        }, KeyCode.A)

        // KEY_TYPED should just be ignored
        val e0 = KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.A, false, false, false, false)
        input.onKeyEvent(e0)
        assertThat(count, `is`(0))

        val e1 = KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.A, false, false, false, false)

        input.onKeyEvent(e1)
        assertThat(count, `is`(1))

        // should make no difference since already pressed
        input.onKeyEvent(e1)
        assertThat(count, `is`(1))

        // should make no difference since different key
        input.onKeyEvent(KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.B, false, false, false, false))
        assertThat(count, `is`(1))

        val e2 = KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.A, false, false, false, false)

        input.onKeyEvent(e2)
        assertThat(count, `is`(0))

        // should make no difference since already released
        input.onKeyEvent(e2)
        assertThat(count, `is`(0))

        input.registerInput = false

        // should now ignore any events

        input.onKeyEvent(e1)
        assertThat(count, `is`(0))

        input.onKeyEvent(e2)
        assertThat(count, `is`(0))
    }

    @Test
    fun `On mouse event`() {
        var count = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                count++
            }

            override fun onActionEnd() {
                count--
            }
        }, MouseButton.PRIMARY)

        // MOUSE_CLICKED should be ignored
        val e0 = MouseEvent(MouseEvent.MOUSE_CLICKED, 10.0, 15.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)
        input.onMouseEvent(e0, Point2D.ZERO, Point2D.ZERO, 1.0, 1.0, 1.0)
        assertThat(count, `is`(0))

        val e1 = MouseEvent(MouseEvent.MOUSE_PRESSED, 10.0, 15.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        input.onMouseEvent(e1, Point2D.ZERO, Point2D.ZERO, 1.0, 1.0, 1.0)
        assertThat(count, `is`(1))
        assertThat(input.mousePositionWorld, `is`(Point2D(10.0, 15.0)))
        assertThat(input.mousePositionUI, `is`(Point2D(10.0, 15.0)))

        val e2 = MouseEvent(MouseEvent.MOUSE_RELEASED, 10.0, 15.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        input.onMouseEvent(e2, Point2D.ZERO, Point2D.ZERO, 1.0, 1.0, 1.0)
        assertThat(count, `is`(0))

        input.onMouseEvent(MouseEventData(e1, Point2D.ZERO, Point2D(15.0, 15.0), 1.0, 1.0, 1.0))
        assertThat(count, `is`(1))

        // the viewport (15.0, 15.0) affects the position world, but not UI

        assertThat(input.mousePositionWorld, `is`(Point2D(10.0 + 15.0, 15.0 + 15.0)))
        assertThat(input.mousePositionUI, `is`(Point2D(10.0, 15.0)))

        // check the properties too

        assertThat(input.mouseXWorldProperty().value, `is`(10.0 + 15.0))
        assertThat(input.mouseYWorldProperty().value, `is`(15.0 + 15.0))
        assertThat(input.mouseXUIProperty().value, `is`(10.0))
        assertThat(input.mouseYUIProperty().value, `is`(15.0))

        input.onMouseEvent(MouseEventData(e2, Point2D.ZERO, Point2D(15.0, 15.0), 1.0, 1.0, 1.0))
        assertThat(count, `is`(0))

        input.registerInput = false

        // should now ignore any events

        input.onMouseEvent(e1, Point2D.ZERO, Point2D.ZERO, 1.0, 1.0, 1.0)
        assertThat(count, `is`(0))

        input.onMouseEvent(e2, Point2D.ZERO, Point2D.ZERO, 1.0, 1.0, 1.0)
        assertThat(count, `is`(0))
    }

    @Test
    fun `On mouse event with content root translation`() {
        val e0 = MouseEvent(MouseEvent.MOUSE_CLICKED, 10.0, 15.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        input.onMouseEvent(e0, Point2D(5.0, 15.0), Point2D.ZERO, 1.0, 1.0, 1.0)

        assertThat(input.mousePositionWorld, `is`(Point2D(5.0, 0.0)))
    }

    @Test
    fun `On mouse event with scaling and zoom`() {
        val e0 = MouseEvent(MouseEvent.MOUSE_CLICKED, 10.0, 15.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        // scale
        input.onMouseEvent(e0, Point2D.ZERO, Point2D.ZERO, 1.0, 2.0, 2.0)

        assertThat(input.mousePositionWorld, `is`(Point2D(5.0, 7.5)))

        // zoom
        input.onMouseEvent(e0, Point2D.ZERO, Point2D.ZERO, 0.5, 1.0, 1.0)

        assertThat(input.mousePositionWorld, `is`(Point2D(20.0, 30.0)))

        // scale and zoom

        input.onMouseEvent(e0, Point2D.ZERO, Point2D.ZERO, 2.0, 0.5, 0.5)

        assertThat(input.mousePositionWorld, `is`(Point2D(10.0, 15.0)))
    }

    @Test
    fun `Clear all`() {
        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onAction() {
                calls--
            }

            override fun onActionEnd() {
                calls = 999
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.update(0.016)
        assertThat(calls, `is`(0))

        input.update(0.016)
        assertThat(calls, `is`(-1))

        input.clearAll()

        input.update(0.016)
        assertThat(calls, `is`(999))



        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.update(0.016)
        assertThat(calls, `is`(0))

        // end should no longer fire
        input.processInput = false

        input.clearAll()
        assertThat(calls, `is`(0))
    }

    @Test
    fun `Trigger listeners`() {
        var resultBegin: Trigger? = null
        var resultAction: Trigger? = null
        var resultEnd: Trigger? = null

        val listener = object : TriggerListener() {
            override fun onActionBegin(trigger: Trigger) {
                resultBegin = trigger
            }

            override fun onAction(trigger: Trigger) {
                resultAction = trigger
            }

            override fun onActionEnd(trigger: Trigger) {
                resultEnd = trigger
            }
        }

        input.addTriggerListener(listener)

        assertNull(resultBegin)
        assertNull(resultAction)
        assertNull(resultEnd)

        input.mockButtonPress(MouseButton.SECONDARY)

        assertTrue(resultBegin is MouseTrigger)
        assertThat((resultBegin as MouseTrigger).button, `is`(MouseButton.SECONDARY))
        assertNull(resultAction)
        assertNull(resultEnd)

        resultBegin = null

        input.update(0.016)

        assertTrue(resultAction is MouseTrigger)
        assertThat((resultAction as MouseTrigger).button, `is`(MouseButton.SECONDARY))
        assertNull(resultBegin)
        assertNull(resultEnd)

        resultAction = null

        input.mockButtonRelease(MouseButton.SECONDARY)

        assertTrue(resultEnd is MouseTrigger)
        assertThat((resultEnd as MouseTrigger).button, `is`(MouseButton.SECONDARY))
        assertNull(resultBegin)
        assertNull(resultAction)

        resultEnd = null

        input.removeTriggerListener(listener)

        input.mockButtonPress(MouseButton.SECONDARY)

        assertNull(resultBegin)
        assertNull(resultAction)
        assertNull(resultEnd)
    }

    @ParameterizedTest
    @MethodSource("triggerValueProvider")
    fun `Input capture`(trigger: Trigger) {
        var calls = 0

        val capture = input.startCapture()

        if (trigger.isKey) {
            input.addAction(object : UserAction("Test") {
                override fun onActionBegin() {
                    calls++
                }

                override fun onAction() {
                    calls--
                }

                override fun onActionEnd() {
                    calls++
                }
            }, (trigger as KeyTrigger).key)
        } else {
            input.addAction(object : UserAction("Test") {
                override fun onActionBegin() {
                    calls++
                }

                override fun onAction() {
                    calls--
                }

                override fun onActionEnd() {
                    calls++
                }
            }, (trigger as MouseTrigger).button)
        }

        repeat(100) {
            input.update(0.016)

            if (trigger.isKey) {
                input.mockKeyPress((trigger as KeyTrigger).key)
                input.mockKeyRelease((trigger as KeyTrigger).key)
            } else {
                input.mockButtonPress((trigger as MouseTrigger).button)
                input.mockButtonRelease((trigger as MouseTrigger).button)
            }
        }

        assertThat(calls, `is`(200))

        input.stopCapture()

        input.applyCapture(capture)

        repeat(50) {
            input.update(0.016)
        }

        assertThat(calls, `is`(300))

        repeat(50) {
            input.update(0.016)
        }

        assertThat(calls, `is`(400))

        // input capture application should have stopped
        input.update(0.016)

        assertThat(calls, `is`(400))

        // check (de-)serialization

        val capture2 = InputCapture()
        val bundle = Bundle("test")

        capture.write(bundle)
        capture2.read(bundle)

        input.applyCapture(capture2)

        repeat(100) {
            input.update(0.016)
        }

        assertThat(calls, `is`(600))

        // input capture application should have stopped
        input.update(0.016)

        assertThat(calls, `is`(600))
    }

    @Test
    fun `Calling startCapture when capturing returns current capture`() {
        val capture1 = input.startCapture()
        val capture2 = input.startCapture()

        assertThat(capture1, `is`(capture2))

        input.stopCapture()

        val capture3 = input.startCapture()

        input.stopCapture()

        assertThat(capture1, `is`(not(capture3)))
    }

//    @Test
//    fun `Serialization`() {
//        val action = object : UserAction("Action") {}
//        val action2 = object : UserAction("Action2") {}
//
//        input.addAction(action, KeyCode.A)
//        input.addAction(action2, MouseButton.PRIMARY)
//
//        val profile = UserProfile("title", "version")
//
//        input.save(profile)
//
//        val input2 = Input()
//        input2.addAction(action, KeyCode.K)
//        input2.addAction(action2, KeyCode.C)
//        input2.load(profile)
//
//        assertThat(input2.getTriggerName(action), `is`("A"))
//        assertThat(input2.getTriggerName(action2), `is`("LMB"))
//    }
//
//    @Test
//    fun `Deserialize does not fail if actions are missing`() {
//        assertAll(
//                Executable {
//                    val action = object : UserAction("Action") {}
//
//                    val profile = UserProfile("title", "version")
//
//                    input.save(profile)
//
//                    val input2 = Input()
//                    input2.addAction(action, KeyCode.K)
//                    input2.load(profile)
//                },
//                Executable {
//                    val action = object : UserAction("Action") {}
//
//                    input.addAction(action, KeyCode.A)
//
//                    val profile = UserProfile("title", "version")
//
//                    input.save(profile)
//
//                    val input2 = Input()
//                    input2.load(profile)
//                }
//        )
//    }

    /* VIRTUAL */

    @Test
    fun `Virtual controller`() {
        var i = 0

        val action = object : UserAction("Action") {
            override fun onActionBegin() {
                i = 1
            }
        }

        input.addAction(action, KeyCode.C, VirtualButton.A)

        val controllers = arrayOf(
                input.createXboxVirtualController(),
                input.createPSVirtualController(),
                input.createVirtualDpad()
        )

        controllers.forEach { controller ->
            val view = controller.createView()

            assertThat(view.children.size, `is`(4))

            assertThat(i, `is`(0))

            controller.pressVirtual(VirtualButton.A)
            controller.releaseVirtual(VirtualButton.A)

            assertThat(i, `is`(1))

            i = 0
        }
    }

    @Test
    fun `Virtual controller view`() {
        val psView = input.createPSVirtualControllerView()
        val xboxView = input.createXboxVirtualControllerView()
        val dpadView = input.createVirtualDpadView()

        assertTrue(psView.children.isNotEmpty())
        assertTrue(xboxView.children.isNotEmpty())
        assertTrue(dpadView.children.isNotEmpty())
    }

    @Test
    fun `Virtual controller correctly handles UI interaction`() {
        var i = 0

        val action = object : UserAction("Action") {
            override fun onActionBegin() {
                i = 1
            }

            override fun onActionEnd() {
                i = 2
            }
        }

        input.addAction(action, KeyCode.C, VirtualButton.A)

        val controller = input.createXboxVirtualController()
        val viewForButtonA = controller.createViewDown()

        viewForButtonA.fireEvent(mousePressedEvent(MouseButton.PRIMARY, false, false, false))
        assertThat(i, `is`(1))

        viewForButtonA.fireEvent(mouseReleasedEvent(MouseButton.PRIMARY, false, false, false))
        assertThat(i, `is`(2))
    }

    @Test
    fun `Virtual joystick`() {
        val joystick = input.createVirtualJoystick()

        assertThat(joystick.center, `is`(Point2D(100.0, 100.0)))
        assertThat(joystick.vector, `is`(Point2D.ZERO))
        assertThat(joystick.vectorProperty().value, `is`(Point2D.ZERO))
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    fun `Virtual menu key`(isMenuEnabled: Boolean) {
        val view = input.createVirtualMenuKeyView(KeyCode.C, isMenuEnabled)

        var i = 0

        input.addEventHandler(KeyEvent.ANY, EventHandler {
            i++
        })

        view.fireEvent(mousePressedEvent(MouseButton.PRIMARY, true, true, true))

        if (isMenuEnabled) {
            assertThat(i, `is`(2))
        } else {
            assertThat(i, `is`(0))
        }
    }

    companion object {
        @Suppress("UNUSED")
        @JvmStatic fun triggerValueProvider(): Stream<Arguments> {
            return Stream.of(
                    Arguments.arguments(KeyTrigger(KeyCode.A)),
                    Arguments.arguments(MouseTrigger(MouseButton.SECONDARY))
            )
        }
    }
}
