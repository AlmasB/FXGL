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

package com.almasb.fxgl.input

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.ServiceType
import com.almasb.fxgl.event.FXGLEvent
import com.almasb.fxgl.event.LoadEvent
import com.almasb.fxgl.event.SaveEvent
import com.almasb.fxgl.event.UpdateEvent
import com.almasb.fxgl.settings.UserProfile
import com.almasb.fxgl.settings.UserProfileSavable
import com.almasb.fxgl.util.FXGLLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.input.*
import java.lang.reflect.Method
import java.util.*

@Singleton
class Input @Inject private constructor() : UserProfileSavable {

    private val log = FXGLLogger.getLogger("FXGL.Input")

    var gameXY = Point2D.ZERO
        private set

    var screenXY = Point2D.ZERO
        private set

    fun getGameX() = gameXY.x
    fun getGameY() = gameXY.y

    fun getScreenX() = screenXY.x
    fun getScreenY() = screenXY.y

    /**
     * @param gamePosition point in game world
     *
     * @return vector from given point to mouse cursor point
     */
    fun getVectorToCursor(gamePosition: Point2D) = gameXY.subtract(gamePosition)

    /**
     * @param gamePosition point in game world
     *
     * @return vector from mouse cursor point to given point
     */
    fun getVectorFromCursor(gamePosition: Point2D) = getVectorToCursor(gamePosition).multiply(-1.0)

    /**
     * Action bindings.
     */
    val bindings = LinkedHashMap<UserAction, Trigger>()

    /**
     * Currently active actions.
     */
    private val currentActions = FXCollections.observableArrayList<UserAction>()

    /**
     * If action events should be processed.
     */
    var processActions = true

    /**
     * If input should be registered.
     */
    var registerInput = true

    init {
        initActionListener()
        initEventHandlers()

        log.finer("Service [Input] initialized")
    }

    /**
     * Listen for any changes in currently active actions
     * and handle them appropriately.
     */
    private fun initActionListener() {
        currentActions.addListener { c: ListChangeListener.Change<out UserAction> ->
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

    /**
     * Hook into the global event bus and handle internal events.
     */
    private fun initEventHandlers() {
        val eventBus = GameApplication.getService(ServiceType.EVENT_BUS)
        eventBus.addEventHandler(UpdateEvent.ANY) {
            if (processActions) {
                currentActions.forEach { it.onAction() }
            }
        }

        eventBus.addEventHandler(FXGLEvent.PAUSE, { clearAllInput() })
        eventBus.addEventHandler(FXGLEvent.RESUME, { clearAllInput() })
        eventBus.addEventHandler(FXGLEvent.RESET, { clearAllInput() })

        eventBus.addEventHandler(FXGLInputEvent.ANY) { event ->
            if (!registerInput)
                return@addEventHandler

            if (event.fxEvent is MouseEvent) {
                val mouseEvent = event.fxEvent
                if (mouseEvent.eventType == MouseEvent.MOUSE_PRESSED) {
                    buttons.put(mouseEvent.button, true)
                    handlePressed(mouseEvent)
                } else if (mouseEvent.eventType == MouseEvent.MOUSE_RELEASED) {
                    buttons.put(mouseEvent.button, false)
                    handleReleased(mouseEvent)
                }

                gameXY = event.gameXY
                screenXY = Point2D(mouseEvent.sceneX, mouseEvent.sceneY)
            } else {
                val keyEvent = event.fxEvent as KeyEvent
                if (keyEvent.eventType == KeyEvent.KEY_PRESSED) {
                    keys.put(keyEvent.code, true)
                    handlePressed(keyEvent)
                } else if (keyEvent.eventType == KeyEvent.KEY_RELEASED) {
                    keys.put(keyEvent.code, false)
                    handleReleased(keyEvent)
                }
            }
        }

        eventBus.addEventHandler(SaveEvent.ANY) { event -> save(event.profile) }

        eventBus.addEventHandler(LoadEvent.ANY) { event -> load(event.profile) }
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

    /**
     * Clears all input, that is releases all key presses and mouse clicks
     * for a single frame.
     */
    fun clearAllInput() {
        log.finer { "Clearing active input actions" }

        currentActions.clear()
        keys.clear()
        buttons.clear()
    }

    /**
     * Currently held keys.
     */
    private val keys = HashMap<KeyCode, Boolean>()

    /**
     * @param key the key to check
     *
     * @return true iff key is currently held
     */
    fun isHeld(key: KeyCode) = keys.getOrDefault(key, false)

    /**
     * Currently held buttons.
     */
    private val buttons = HashMap<MouseButton, Boolean>()

    /**
     * @param button the button to check
     *
     * @return true iff button is currently held
     */
    fun isHeld(button: MouseButton) = buttons.getOrDefault(button, false)

    /**
     * Bind [action] to a mouse [btn].
     *
     * @throws IllegalArgumentException if action with same name exists
     */
    fun addAction(action: UserAction, btn: MouseButton) = addAction(action, btn, InputModifier.NONE)

    /**
     * Bind given action to a mouse button with special modifier key.
     *
     * @param action the action to bind
     * @param button the mouse button
     *
     * @param modifier the key modifier
     *
     * @throws IllegalArgumentException if action with same name exists
     */
    fun addAction(action: UserAction, button: MouseButton, modifier: InputModifier) {
        if (bindings.containsKey(action))
            throw IllegalArgumentException("Action with name \"$action\" already exists")

        val trigger = MouseTrigger(button, modifier)

        if (bindings.containsValue(trigger))
            throw IllegalArgumentException("Button \"$button\" is already bound")

        bindings.put(action, trigger)
        log.finer { "Registered new binding: $action - $trigger" }
    }

    /**
     * Bind given action to a keyboard key.
     *
     * @param action the action to bind
     * @param key the key
     * @throws IllegalArgumentException if action with same name exists
     */
    fun addAction(action: UserAction, key: KeyCode) = addAction(action, key, InputModifier.NONE)

    /**
     * Bind given action to a keyboard key with special modifier key.
     *
     * @param action the action to bind
     * @param key the key
     * @param modifier the key modifier
     * @throws IllegalArgumentException if action with same name exists
     */
    fun addAction(action: UserAction, key: KeyCode, modifier: InputModifier) {
        if (bindings.containsKey(action))
            throw IllegalArgumentException("Action with name \"${action.name}\" already exists")

        val trigger = KeyTrigger(key, modifier)

        // TODO: check if ctrl + w same as w
        if (bindings.containsValue(trigger))
            throw IllegalArgumentException("Key $key is already bound")

        bindings.put(action, trigger)
        log.finer { "Registered new binding: $action - $trigger" }
    }

    /**
     * Rebinds an action to given key.
     *
     * @param action the user action
     * @param key the key to rebind to
     * @return true if rebound, false if action not found or
     * there is another action bound to key
     */
    fun rebind(action: UserAction, key: KeyCode): Boolean {
        if (bindings.containsKey(action) && !bindings.containsValue(KeyTrigger(key))) {
            bindings.put(action, KeyTrigger(key))
            return true
        }

        return false
    }

    /**
     * Rebinds an action to given mouse button.
     *
     * @param action the user action
     * @param button the mouse button
     * @return true if rebound, false if action not found or
     * there is another action bound to mouse button
     */
    fun rebind(action: UserAction, button: MouseButton): Boolean {
        if (bindings.containsKey(action) && !bindings.containsValue(MouseTrigger(button))) {
            bindings.put(action, MouseTrigger(button))
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
     * user pressing and holding the [key].
     */
    fun mockKeyPress(key: KeyCode) = mockKeyPress(key, InputModifier.NONE)

    /**
     * Mocks key press event. The behavior is equivalent to
     * user pressing and holding the [key] and [modifier].
     */
    fun mockKeyPress(key: KeyCode, modifier: InputModifier) {
        log.finer { "Mocking key press: ${KeyTrigger(key, modifier)}" }
        handlePressed(makeKeyEvent(key, KeyEvent.KEY_PRESSED, modifier))
    }

    /**
     * Mocks key release event.
     * The behavior is equivalent to user releasing the [key].
     */
    fun mockKeyRelease(key: KeyCode) = mockKeyRelease(key, InputModifier.NONE)

    /**
     * Mocks key release event.
     * The behavior is equivalent to user releasing the [key] and [modifier].
     */
    fun mockKeyRelease(key: KeyCode, modifier: InputModifier) {
        log.finer { "Mocking key release: ${KeyTrigger(key, modifier)}" }
        handleReleased(makeKeyEvent(key, KeyEvent.KEY_RELEASED, modifier))
    }

    fun makeMouseEvent(btn: MouseButton, eventType: EventType<MouseEvent>,
                       gameX: Double, gameY: Double, modifier: InputModifier) =
        MouseEvent(eventType, gameX, gameY, gameX, gameY, btn, 0,
                modifier == InputModifier.SHIFT,
                modifier == InputModifier.CTRL,
                modifier == InputModifier.ALT,
                false, false, false, false, false, false, false, null)

    /**
     * Mocks mouse button press event.
     * Same as user pressing and holding the [button] at [gameX], [gameY].
     */
    fun mockButtonPress(button: MouseButton, gameX: Double, gameY: Double) =
            mockButtonPress(button, gameX, gameY, InputModifier.NONE)

    /**
     * Mocks mouse button press event.
     * Same as user pressing and holding the [button] + [modifier] at [gameX], [gameY].
     */
    fun mockButtonPress(button: MouseButton, gameX: Double, gameY: Double, modifier: InputModifier) {
        log.finer { "Mocking button press: ${MouseTrigger(button, modifier)}" }
        handlePressed(makeMouseEvent(button, MouseEvent.MOUSE_PRESSED, gameX, gameY, modifier))
    }

    /**
     * Mocks mouse button release event.
     * Same as user releasing the [button].
     */
    fun mockButtonRelease(button: MouseButton) =
            mockButtonRelease(button, InputModifier.NONE)

    /**
     * Mocks mouse button release event.
     * Same as user releasing the [button] + [modifier].
     */
    fun mockButtonRelease(button: MouseButton, modifier: InputModifier) {
        log.finer { "Mocking button release: ${MouseTrigger(button, modifier)}" }
        handleReleased(makeMouseEvent(button, MouseEvent.MOUSE_RELEASED, 0.0, 0.0, modifier))
    }

    /* INPUT MAPPINGS */

    private val inputMappings = HashMap<String, InputMapping>()

    /**
     * Add input mapping. The actual implementation needs to be specified by
     * {@link OnUserAction} annotation.
     *
     * @param inputMapping the mapping
     */
    fun addInputMapping(inputMapping: InputMapping) = inputMappings.put(inputMapping.actionName, inputMapping)

    private fun getInputMappingByName(actionName: String) = inputMappings[actionName]

    /**
     * Given an object scans its methods for {@link OnUserAction} annotation
     * and creates UserActions from its data.
     *
     * @param instance the class instance to scan
     */
    fun scanForUserActions(instance: Any) {
        val map = HashMap<String, HashMap<ActionType, Method> >()

        for (method in instance.javaClass.declaredMethods) {
            val action = method.getDeclaredAnnotation(OnUserAction::class.java)
            if (action != null) {
                val mapping = map.getOrDefault(action.name, HashMap())
                if (mapping.isEmpty()) {
                    map.put(action.name, mapping)
                }

                mapping.put(action.type, method)
            }
        }

        map.forEach { name, mapping ->
            val onAction = mapping[ActionType.ON_ACTION]
            val onActionBegin = mapping[ActionType.ON_ACTION_BEGIN]
            val onActionEnd = mapping[ActionType.ON_ACTION_END]

            val action = object : UserAction(name) {
                override fun onActionBegin() {
                    onActionBegin?.invoke(instance)
                }

                override fun onAction() {
                    onAction?.invoke(instance)
                }

                override fun onActionEnd() {
                    onActionEnd?.invoke(instance)
                }
            }

            val inputMapping: InputMapping = getInputMappingByName(name)!!

            if (inputMapping.isKeyTrigger()) {
                addAction(action, inputMapping.getKeyTrigger(), inputMapping.modifier)
            } else {
                addAction(action, inputMapping.getButtonTrigger(), inputMapping.modifier)
            }
        }
    }

    override fun save(profile: UserProfile) {
        log.finer("Saving data to profile")

        val bundle = UserProfile.Bundle("input")
        bindings.forEach { bundle.put(it.key.toString(), it.value.toString()) }

        bundle.log()
        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        log.finer("Loading data from profile")

        val bundle = profile.getBundle("input")
        bundle.log()

        for (binding in bindings) {
            var triggerName = bundle.get<String>(binding.key.name)

            val plusIndex = triggerName.indexOf("+")
            if (plusIndex != -1) {
                triggerName = triggerName.substring(plusIndex + 1)
            }

            try {
                val key = KeyCode.getKeyCode(triggerName)
                rebind(binding.key, key)
            } catch (ignored: Exception) {
                try {
                    val btn = MouseButton.valueOf(triggerName)
                    rebind(binding.key, btn)
                } catch (e: Exception) {
                    log.warning("Undefined trigger name: " + triggerName)
                    throw IllegalArgumentException("Corrupt or incompatible user profile: " + e.message)
                }
            }
        }
    }
}