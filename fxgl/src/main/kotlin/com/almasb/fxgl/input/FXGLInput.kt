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

package com.almasb.fxgl.input

import com.almasb.easyio.serialization.Bundle
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.scene.Viewport
import com.almasb.fxgl.settings.UserProfile
import com.almasb.fxgl.time.UpdateEvent
import com.google.inject.Inject
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.input.*
import java.lang.reflect.Method
import java.util.*

class FXGLInput
@Inject private constructor() : com.almasb.fxgl.input.Input {

    private val ILLEGAL_KEYS = arrayOf(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.ALT)

    private val log = FXGL.getLogger(javaClass)

    /**
     * Cursor point in game coordinate space.
     */

    private var gameX = 0.0
    private var gameY = 0.0

    override fun getMousePositionWorld() = Point2D(gameX, gameY)

    /**
     * Cursor point in screen coordinate space.
     * Useful for UI manipulation.
     */

    private var sceneX = 0.0
    private var sceneY = 0.0

    override fun getMousePositionUI() = Point2D(sceneX, sceneY)

    /**
     * Action bindings.
     */
    private val bindings = LinkedHashMap<com.almasb.fxgl.input.UserAction, Trigger>()

    override fun getBindings() = bindings

    /**
     * Currently active actions.
     */
    private val currentActions = FXCollections.observableArrayList<com.almasb.fxgl.input.UserAction>()

    /**
     * If action events should be processed.
     */
    private var processActions = true

    override fun setProcessInput(process: Boolean) {
        processActions = process
    }

    override fun isProcessInput() = processActions

    /**
     * If input should be registered.
     */
    private var registerInput = true

    override fun setRegisterInput(register: Boolean) {
        registerInput = register
    }

    override fun isRegisterInput() = registerInput

    init {
        initActionListener()

        log.debug { "Service [Input] initialized" }
    }

    /**
     * Listen for any changes in currently active actions
     * and handle them appropriately.
     */
    private fun initActionListener() {
        currentActions.addListener { c: ListChangeListener.Change<out com.almasb.fxgl.input.UserAction> ->
            while (c.next()) {
                if (!processActions)
                    continue

                if (c.wasAdded()) {
                    c.addedSubList.forEach { it.onActionBegin() }
                } else if (c.wasRemoved()) {
                    c.removed.forEach { it.onActionEnd() }
                }
            }
        }
    }

    override fun onUpdateEvent(event: com.almasb.fxgl.time.UpdateEvent) {
        if (processActions) {
            currentActions.forEach { it.onAction() }
        }
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        if (!registerInput)
            return

        if (keyEvent.eventType == KeyEvent.KEY_PRESSED) {
            keys.put(keyEvent.code, true)
            handlePressed(keyEvent)
        } else if (keyEvent.eventType == KeyEvent.KEY_RELEASED) {
            keys.put(keyEvent.code, false)
            handleReleased(keyEvent)
        }
    }

    override fun onMouseEvent(mouseEvent: MouseEvent, viewport: Viewport, scaleRatio: Double) {
        if (!registerInput)
            return

        if (mouseEvent.eventType == MouseEvent.MOUSE_PRESSED) {
            buttons.put(mouseEvent.button, true)
            handlePressed(mouseEvent)
        } else if (mouseEvent.eventType == MouseEvent.MOUSE_RELEASED) {
            buttons.put(mouseEvent.button, false)
            handleReleased(mouseEvent)
        }

        sceneX = mouseEvent.sceneX
        sceneY = mouseEvent.sceneY

        gameX = sceneX / scaleRatio + viewport.getX()
        gameY = sceneY / scaleRatio + viewport.getY()
    }

    private fun isTriggered(trigger: Trigger, fxEvent: InputEvent): Boolean {
        if (fxEvent is MouseEvent && trigger is MouseTrigger
                && fxEvent.button == trigger.button && trigger.getModifier().isTriggered(fxEvent))
            return true

        if (fxEvent is KeyEvent && trigger is KeyTrigger
                && fxEvent.code == trigger.key && trigger.getModifier().isTriggered(fxEvent))
            return true

        return false
    }

    private fun handlePressed(event: InputEvent) {
        bindings.filter { isTriggered(it.value, event) && !currentActions.contains(it.key) }
                .forEach { currentActions.add(it.key) }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun handleReleased(event: InputEvent) {
        bindings.filter({ binding ->
            if (event is KeyEvent) {
                when (event.code) {
                    KeyCode.CONTROL -> return@filter binding.value.getModifier() == InputModifier.CTRL
                    KeyCode.SHIFT -> return@filter binding.value.getModifier() == InputModifier.SHIFT
                    KeyCode.ALT -> return@filter binding.value.getModifier() == InputModifier.ALT
                }
            }

            isTriggered(binding.value, event)
        })
        .forEach { currentActions.remove(it.key) }
    }

    override fun clearAll() {
        log.debug { "Clearing active input actions" }

        currentActions.clear()
        keys.clear()
        buttons.clear()
    }

    /**
     * Currently held keys.
     */
    private val keys = HashMap<KeyCode, Boolean>()

    override fun isHeld(key: KeyCode) = keys.getOrDefault(key, false)

    /**
     * Currently held buttons.
     */
    private val buttons = HashMap<MouseButton, Boolean>()

    override fun isHeld(button: MouseButton) = buttons.getOrDefault(button, false)

    override fun addAction(action: com.almasb.fxgl.input.UserAction, button: MouseButton, modifier: InputModifier) =
            addBinding(action, MouseTrigger(button, modifier))

    override fun addAction(action: com.almasb.fxgl.input.UserAction, key: KeyCode, modifier: InputModifier) {
        if (ILLEGAL_KEYS.contains(key))
            throw IllegalArgumentException("Cannot bind to illegal key: $key")

        addBinding(action, KeyTrigger(key, modifier))
    }

    private fun addBinding(action: com.almasb.fxgl.input.UserAction, trigger: Trigger) {
        if (bindings.containsKey(action))
            throw IllegalArgumentException("Action with name \"${action.name}\" already exists")

        if (bindings.containsValue(trigger))
            throw IllegalArgumentException("Trigger $trigger is already bound")

        bindings[action] = trigger
        log.debug { "Registered new binding: $action - $trigger" }
    }

    override fun rebind(action: com.almasb.fxgl.input.UserAction, key: KeyCode, modifier: InputModifier): Boolean {
        if (bindings.containsKey(action) && !bindings.containsValue(KeyTrigger(key, modifier))) {
            bindings[action] = KeyTrigger(key, modifier)
            return true
        }

        return false
    }

    override fun rebind(action: com.almasb.fxgl.input.UserAction, button: MouseButton, modifier: InputModifier): Boolean {
        if (bindings.containsKey(action) && !bindings.containsValue(MouseTrigger(button, modifier))) {
            bindings[action] = MouseTrigger(button, modifier)
            return true
        }

        return false
    }

    /* MOCKING */

    private fun makeKeyEvent(key: KeyCode, eventType: EventType<KeyEvent>, modifier: InputModifier) =
        KeyEvent(eventType, "", key.toString(), key,
                modifier == InputModifier.SHIFT,
                modifier == InputModifier.CTRL,
                modifier == InputModifier.ALT,
                false)

    /**
     * Mocks key press event. The behavior is equivalent to
     * user pressing and holding the [key] and [modifier].
     */
    override fun mockKeyPress(key: KeyCode, modifier: InputModifier) {
        log.debug { "Mocking key press: ${KeyTrigger(key, modifier)}" }
        handlePressed(makeKeyEvent(key, KeyEvent.KEY_PRESSED, modifier))
    }

    /**
     * Mocks key release event.
     * The behavior is equivalent to user releasing the [key] and [modifier].
     */
    override fun mockKeyRelease(key: KeyCode, modifier: InputModifier) {
        log.debug { "Mocking key release: ${KeyTrigger(key, modifier)}" }
        handleReleased(makeKeyEvent(key, KeyEvent.KEY_RELEASED, modifier))
    }

    private fun makeMouseEvent(btn: MouseButton, eventType: EventType<MouseEvent>,
                       gameX: Double, gameY: Double, modifier: InputModifier) =
        MouseEvent(eventType, gameX, gameY, gameX, gameY, btn, 0,
                modifier == InputModifier.SHIFT,
                modifier == InputModifier.CTRL,
                modifier == InputModifier.ALT,
                false, false, false, false, false, false, false, null)

    /**
     * Mocks mouse button press event.
     * Same as user pressing and holding the [button] + [modifier] at [gameX], [gameY].
     */
    override fun mockButtonPress(button: MouseButton, gameX: Double, gameY: Double, modifier: InputModifier) {
        log.debug { "Mocking button press: ${MouseTrigger(button, modifier)} at $gameX, $gameY" }

        this.gameX = gameX
        this.gameY = gameY
        handlePressed(makeMouseEvent(button, MouseEvent.MOUSE_PRESSED, gameX, gameY, modifier))
    }

    override fun mockButtonRelease(button: MouseButton, gameX: Double, gameY: Double, modifier: InputModifier) {
        log.debug { "Mocking button release: ${MouseTrigger(button, modifier)} at $gameX, $gameY" }

        this.gameX = gameX
        this.gameY = gameY
        handleReleased(makeMouseEvent(button, MouseEvent.MOUSE_RELEASED, gameX, gameY, modifier))
    }

    /* INPUT MAPPINGS */

    private val inputMappings = HashMap<String, InputMapping>()

    override fun addInputMapping(inputMapping: InputMapping) {
        inputMappings.put(inputMapping.actionName, inputMapping)
    }

    /**
     * Given an object, scans its methods for {@link OnUserAction} annotation
     * and creates UserActions from its data.
     *
     * @param instance the class instance to scan
     */
    override fun scanForUserActions(instance: Any) {
        val map = HashMap<String, HashMap<ActionType, Method> >()

        for (method in instance.javaClass.declaredMethods) {
            val action = method.getDeclaredAnnotation(com.almasb.fxgl.input.OnUserAction::class.java)
            if (action != null) {
                val mapping = map.getOrDefault(action.name, HashMap())
                if (mapping.isEmpty()) {
                    map[action.name] = mapping
                }

                mapping[action.type] = method
            }
        }

        map.forEach { name, mapping ->
            val action = object : com.almasb.fxgl.input.UserAction(name) {
                override fun onActionBegin() {
                    mapping[ActionType.ON_ACTION_BEGIN]?.invoke(instance)
                }

                override fun onAction() {
                    mapping[ActionType.ON_ACTION]?.invoke(instance)
                }

                override fun onActionEnd() {
                    mapping[ActionType.ON_ACTION_END]?.invoke(instance)
                }
            }

            val inputMapping: InputMapping = inputMappings[name]!!

            if (inputMapping.isKeyTrigger()) {
                addAction(action, inputMapping.getKeyTrigger(), inputMapping.modifier)
            } else {
                addAction(action, inputMapping.getButtonTrigger(), inputMapping.modifier)
            }
        }
    }

    override fun save(profile: com.almasb.fxgl.settings.UserProfile) {
        log.debug("Saving data to profile")

        val bundle = Bundle("input")
        bindings.forEach { bundle.put(it.key.toString(), it.value.toString()) }

        bundle.log()
        profile.putBundle(bundle)
    }

    override fun load(profile: com.almasb.fxgl.settings.UserProfile) {
        log.debug("Loading data from profile")

        val bundle = profile.getBundle("input")
        bundle.log()

        for (binding in bindings) {

            // if binding is not present in bundle, then we added some new binding thru code
            // it will saved on next serialization and will be found in bundle
            var triggerName: String? = bundle.get<String>("${binding.key}")
            if (triggerName == null)
                continue

            var modifierName = "NONE"

            val plusIndex = triggerName.indexOf("+")
            if (plusIndex != -1) {
                modifierName = triggerName.substring(0, plusIndex)
                triggerName = triggerName.substring(plusIndex + 1)
            }

            try {
                val key = KeyCode.getKeyCode(triggerName)
                rebind(binding.key, key, InputModifier.valueOf(modifierName))
            } catch (ignored: Exception) {
                try {
                    val btn = MouseButton.valueOf(triggerName)
                    rebind(binding.key, btn, InputModifier.valueOf(modifierName))
                } catch (e: Exception) {
                    log.warning("Undefined trigger name: " + triggerName)
                    throw IllegalArgumentException("Corrupt or incompatible user profile: " + e.message)
                }
            }
        }
    }
}