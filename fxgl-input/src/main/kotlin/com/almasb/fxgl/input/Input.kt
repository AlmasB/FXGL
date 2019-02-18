/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import com.almasb.sslogger.Logger
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.collections.FXCollections
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.input.*

class Input {

    companion object {
        private val ILLEGAL_KEYS = arrayOf(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.ALT)

        private val log = Logger.get<Input>()

        @JvmStatic fun isIllegal(key: KeyCode) = key in ILLEGAL_KEYS
    }

    /**
     * Cursor point in game coordinate space.
     */
    var mouseXWorld = 0.0
        private set

    var mouseYWorld = 0.0
        private set

    val mousePositionWorld
        get() = Point2D(mouseXWorld, mouseYWorld)

    /**
     * Cursor point in screen coordinate space.
     * Useful for UI manipulation.
     */
    var mouseXUI = 0.0
        private set

    var mouseYUI = 0.0
        private set

    val mousePositionUI
        get() = Point2D(mouseXUI, mouseYUI)

    /**
     * @param gamePosition point in game world
     * @return vector from given point to mouse cursor point
     */
    fun getVectorToMouse(gamePosition: Point2D): Point2D = mousePositionWorld.subtract(gamePosition)

    /**
     * @param gamePosition point in game world
     * @return vector from mouse cursor point to given point
     */
    fun getVectorFromMouse(gamePosition: Point2D): Point2D = getVectorToMouse(gamePosition).multiply(-1.0)

    /**
     * @return registered action bindings
     */
    val bindings = LinkedHashMap<UserAction, Trigger>()

    private val triggerNames = hashMapOf<UserAction, ReadOnlyStringWrapper>()
    private val triggers = hashMapOf<UserAction, ReadOnlyObjectWrapper<Trigger>>()

    fun triggerNameProperty(action: UserAction): ReadOnlyStringProperty {
        return triggerNames[action]?.readOnlyProperty ?: throw IllegalArgumentException("Action $action not found")
    }

    fun getTriggerName(action: UserAction): String = triggerNameProperty(action).value

    fun getTriggerByActionName(actionName: String): String = getTriggerName(getActionByName(actionName))

    fun getActionByName(actionName: String): UserAction = bindings.keys.find { it.name == actionName }
            ?: throw IllegalArgumentException("Action $actionName not found")

    fun triggerProperty(action: UserAction): ReadOnlyObjectProperty<Trigger> {
        return triggers[action]?.readOnlyProperty ?: throw IllegalArgumentException("Action $action not found")
    }

    /**
     * Currently active actions.
     */
    private val currentActions = FXCollections.observableArrayList<UserAction>()

    /**
     * If action events should be processed.
     */
    var processInput = true

    /**
     * If input should be registered.
     */
    var registerInput = true

    private val eventHandlers = Group()

    /**
     * Add JavaFX event handler.
     *
     * @param eventType type of events to listen
     * @param eventHandler handler for events
     */
    fun <T : Event> addEventHandler(eventType: EventType<T>,
                                    eventHandler: EventHandler<in T>) {
        eventHandlers.addEventHandler(eventType, eventHandler)
    }

    /**
     * Remove JavaFX event handler.
     *
     * @param eventType type of events to listen
     * @param eventHandler handler for events
     */
    fun <T : Event> removeEventHandler(eventType: EventType<T>,
                                       eventHandler: EventHandler<in T>) {
        eventHandlers.removeEventHandler(eventType, eventHandler)
    }

    /**
     * Fire JavaFX event.
     *
     * @param event the JavaFX event
     */
    fun fireEvent(event: Event) {
        eventHandlers.fireEvent(event)
    }

    fun update(tpf: Double) {
        if (!processInput)
            return

        for (i in currentActions.indices) {
            currentActions[i].action()
        }
    }

    /**
     * Called on key event.
     */
    fun onKeyEvent(keyEvent: KeyEvent) {
        if (!registerInput)
            return

        if (keyEvent.eventType == KeyEvent.KEY_PRESSED) {
            keys[keyEvent.code] = true
            handlePressed(keyEvent)
        } else if (keyEvent.eventType == KeyEvent.KEY_RELEASED) {
            keys[keyEvent.code] = false
            handleReleased(keyEvent)
        }
    }

    fun onMouseEvent(eventData: MouseEventData) {
        onMouseEvent(eventData.event, eventData.viewportOrigin, eventData.scaleRatioX, eventData.scaleRatioY)
    }

    /**
     * Called on mouse event.
     *
     * @param event mouse event
     * @param viewport current viewport where the even occurred
     * @param scaleRatio scale ratio of the display where the event occurred
     */
    fun onMouseEvent(mouseEvent: MouseEvent, viewportOrigin: Point2D, scaleRatioX: Double, scaleRatioY: Double) {
        if (!registerInput)
            return

        if (mouseEvent.eventType == MouseEvent.MOUSE_PRESSED) {
            buttons[mouseEvent.button] = true
            handlePressed(mouseEvent)
        } else if (mouseEvent.eventType == MouseEvent.MOUSE_RELEASED) {
            buttons[mouseEvent.button] = false
            handleReleased(mouseEvent)
        }

        mouseXUI = mouseEvent.sceneX
        mouseYUI = mouseEvent.sceneY

        mouseXWorld = mouseXUI / scaleRatioX + viewportOrigin.x
        mouseYWorld = mouseYUI / scaleRatioY + viewportOrigin.y
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
        bindings.filter { (act, trigger) -> isTriggered(trigger, event) && act !in currentActions }
                .forEach { (act, _) ->
                    currentActions.add(act)

                    if (processInput)
                        act.begin()
                }
    }

    private fun handleReleased(event: InputEvent) {
        bindings.filter { (_, trigger) -> isReleased(event, trigger) }
                .forEach { (act, _) ->
                    currentActions.remove(act)

                    if (processInput)
                        act.end()
                }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun isReleased(event: InputEvent, trigger: Trigger): Boolean {
        if (event is KeyEvent) {
            when (event.code) {
                KeyCode.CONTROL -> return trigger.getModifier() == InputModifier.CTRL
                KeyCode.SHIFT -> return trigger.getModifier() == InputModifier.SHIFT
                KeyCode.ALT -> return trigger.getModifier() == InputModifier.ALT
            }
        }

        return isTriggered(trigger, event)
    }

    /**
     * Clears all active actions.
     * Releases all key presses and mouse clicks for a single frame.
     */
    fun clearAll() {
        log.debug("Clearing active input actions")

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
     * @return true iff key is currently (physically) held; mocking does not trigger this
     */
    fun isHeld(key: KeyCode): Boolean = keys.getOrDefault(key, false)

    /**
     * Currently held buttons.
     */
    private val buttons = HashMap<MouseButton, Boolean>()

    /**
     * @param button the button to check
     * @return true iff button is currently (physically) held; mocking does not trigger this
     */
    fun isHeld(button: MouseButton): Boolean = buttons.getOrDefault(button, false)

    //private val virtualButtons = hashMapOf<VirtualButton, KeyCode>()

    /**
     * Bind given action to a mouse button with special modifier key.
     */
    @JvmOverloads fun addAction(action: UserAction, button: MouseButton, modifier: InputModifier = InputModifier.NONE) =
            addBinding(action, MouseTrigger(button, modifier))

    /**
     * Bind given action to a keyboard key with special modifier key.
     *
     * @param action the action to bind
     * @param key the key
     * @param modifier the key modifier
     * @throws IllegalArgumentException if action with same name exists or key is in use
     */
    @JvmOverloads fun addAction(action: UserAction, key: KeyCode, modifier: InputModifier = InputModifier.NONE) {
        require(!isIllegal(key)) { "Cannot bind to illegal key: $key" }

        addBinding(action, KeyTrigger(key, modifier))
    }

//    @JvmOverloads fun addAction(action: UserAction, key: KeyCode, virtualButton: VirtualButton) {
//        require(!isIllegal(key)) { "Cannot bind to illegal key: $key" }
//
//        addBinding(action, KeyTrigger(key, InputModifier.NONE))
//        addVirtualButton(virtualButton, key)
//    }

    private fun addBinding(action: UserAction, trigger: Trigger) {
        require(!bindings.containsKey(action)) { "Action already exists: ${action.name}" }
        require(!bindings.containsValue(trigger)) { "Trigger is already bound: $trigger" }

        bindings[action] = trigger

        if (!triggers.containsKey(action)) {
            triggers[action] = ReadOnlyObjectWrapper(trigger)
        }

        if (!triggerNames.containsKey(action)) {
            triggerNames[action] = ReadOnlyStringWrapper("")
        }

        triggerNames[action]?.value = trigger.toString()

        log.debug("Registered new binding: $action - $trigger")
    }

//    private fun addVirtualButton(virtualButton: VirtualButton, key: KeyCode) {
//        virtualButtons[virtualButton] = key
//    }

    /**
     * @return true if rebound, false if action not found or there is another action bound to key
     */
    @JvmOverloads fun rebind(action: UserAction, key: KeyCode, modifier: InputModifier = InputModifier.NONE): Boolean {
        if (bindings.containsKey(action) && !bindings.containsValue(KeyTrigger(key, modifier))) {
            val newTrigger = KeyTrigger(key, modifier)

            bindings[action] = newTrigger
            triggers[action]?.value = newTrigger
            triggerNames[action]?.value = newTrigger.toString()
            return true
        }

        return false
    }

    /**
     * @return true if rebound, false if action not found or there is another action bound to mouse button
     */
    @JvmOverloads fun rebind(action: UserAction, button: MouseButton, modifier: InputModifier = InputModifier.NONE): Boolean {
        if (bindings.containsKey(action) && !bindings.containsValue(MouseTrigger(button, modifier))) {
            val newTrigger = MouseTrigger(button, modifier)

            bindings[action] = newTrigger
            triggers[action]?.value = newTrigger
            triggerNames[action]?.value = newTrigger.toString()
            return true
        }

        return false
    }

    /* VIRTUAL */

//    internal fun pressVirtual(virtualButton: VirtualButton) {
//        virtualButtons[virtualButton]?.let { mockKeyPress(it) }
//    }
//
//    internal fun releaseVirtual(virtualButton: VirtualButton) {
//        virtualButtons[virtualButton]?.let { mockKeyRelease(it) }
//    }

    /* MOCKING */

    private fun makeKeyEvent(key: KeyCode, eventType: EventType<KeyEvent>, modifier: InputModifier) =
        KeyEvent(eventType, "", key.toString(), key,
                modifier == InputModifier.SHIFT,
                modifier == InputModifier.CTRL,
                modifier == InputModifier.ALT,
                false)

    /**
     * Mocks key press event. The behavior is equivalent to
     * user pressing and holding the key with the modifier.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param key the key to mock
     * @param modifier key modifier
     */
    @JvmOverloads fun mockKeyPress(key: KeyCode, modifier: InputModifier = InputModifier.NONE) {
        log.debug("Mocking key press: ${KeyTrigger(key, modifier)}")
        handlePressed(makeKeyEvent(key, KeyEvent.KEY_PRESSED, modifier))
    }

    /**
     * Mocks key release event. The behavior is equivalent to
     * user releasing the key and the modifier.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param key the key to mock
     * @param modifier the modifier
     */
    @JvmOverloads fun mockKeyRelease(key: KeyCode, modifier: InputModifier = InputModifier.NONE) {
        log.debug("Mocking key release: ${KeyTrigger(key, modifier)}")
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
     * Mocks button press event. The behavior is equivalent to
     * user pressing and holding the button and the modifier at x, y.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     * This does not affect mouse UI coordinates but affects game world coordinates.
     *
     * @param button the button to mock
     * @param gameX x in game world
     * @param gameY y in game world
     * @param modifier the modifier
     */
    @JvmOverloads fun mockButtonPress(button: MouseButton, gameX: Double, gameY: Double, modifier: InputModifier = InputModifier.NONE) {
        log.debug("Mocking button press: ${MouseTrigger(button, modifier)} at $gameX, $gameY")

        mouseXWorld = gameX
        mouseYWorld = gameY
        handlePressed(makeMouseEvent(button, MouseEvent.MOUSE_PRESSED, gameX, gameY, modifier))
    }

    @JvmOverloads fun mockButtonPress(button: MouseButton, inputModifier: InputModifier = InputModifier.NONE) {
        mockButtonPress(button, mouseXWorld, mouseYWorld, inputModifier)
    }

    /**
     * Mocks button release event. The behavior is equivalent to
     * user releasing the button.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     * This does not affect mouse UI coordinates but affects game world coordinates.
     *
     * @param button the button to mock
     * @param gameX x in game world
     * @param gameY y in game world
     */
    @JvmOverloads fun mockButtonRelease(button: MouseButton, gameX: Double, gameY: Double, modifier: InputModifier = InputModifier.NONE) {
        log.debug("Mocking button release: ${MouseTrigger(button, modifier)} at $gameX, $gameY")

        mouseXWorld = gameX
        mouseYWorld = gameY
        handleReleased(makeMouseEvent(button, MouseEvent.MOUSE_RELEASED, gameX, gameY, modifier))
    }

    @JvmOverloads fun mockButtonRelease(button: MouseButton, inputModifier: InputModifier = InputModifier.NONE) {
        mockButtonRelease(button, mouseXWorld, mouseYWorld, inputModifier)
    }

//
//    fun save(profile: UserProfile) {
//        log.debug("Saving data to profile")
//
//        val bundle = Bundle("input")
//        bindings.forEach { bundle.put(it.key.toString(), it.value.toString()) }
//
//        bundle.log()
//        profile.putBundle(bundle)
//    }
//
//    fun load(profile: UserProfile) {
//        log.debug("Loading data from profile")
//
//        val bundle = profile.getBundle("input")
//        bundle.log()
//
//        for (binding in bindings) {
//
//            val action = binding.key
//
//            // if binding is not present in bundle, then we added some new binding thru code
//            // it will be saved on next serialization and will be found in bundle
//            var triggerName: String? = bundle.get<String>("$action")
//            if (triggerName == null)
//                continue
//
//            var modifierName = "NONE"
//
//            val plusIndex = triggerName.indexOf("+")
//            if (plusIndex != -1) {
//                modifierName = triggerName.substring(0, plusIndex)
//                triggerName = triggerName.substring(plusIndex + 1)
//            }
//
//            // if triggerName was CTRL+A, we end up with:
//            // triggerName = A
//            // modifierName = CTRL
//
//            try {
//                val key = KeyCode.getKeyCode(triggerName)
//                rebind(action, key, InputModifier.valueOf(modifierName))
//            } catch (ignored: Exception) {
//                try {
//                    val btn = MouseTrigger.buttonFromString(triggerName)
//                    rebind(action, btn, InputModifier.valueOf(modifierName))
//                } catch (e: Exception) {
//                    log.warning("Undefined trigger name: " + triggerName)
//                    throw IllegalArgumentException("Corrupt or incompatible user profile: " + e.message)
//                }
//            }
//        }
//    }
}