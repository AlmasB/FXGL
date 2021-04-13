/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import com.almasb.fxgl.input.virtual.*
import com.almasb.fxgl.logging.Logger
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import javafx.collections.FXCollections
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class Input {

    companion object {
        private val ILLEGAL_KEYS = arrayOf(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.ALT)

        private val log = Logger.get<Input>()

        /**
         * How many keys to remember.
         */
        private const val QUEUE_SIZE = 50

        @JvmStatic fun isIllegal(key: KeyCode) = key in ILLEGAL_KEYS
    }

    private val mouseXWorldProp = ReadOnlyDoubleWrapper()
    private val mouseYWorldProp = ReadOnlyDoubleWrapper()
    private val mouseXUIProp = ReadOnlyDoubleWrapper()
    private val mouseYUIProp = ReadOnlyDoubleWrapper()

    fun mouseXWorldProperty(): ReadOnlyDoubleProperty = mouseXWorldProp.readOnlyProperty
    fun mouseYWorldProperty(): ReadOnlyDoubleProperty = mouseYWorldProp.readOnlyProperty

    fun mouseXUIProperty(): ReadOnlyDoubleProperty = mouseXUIProp.readOnlyProperty
    fun mouseYUIProperty(): ReadOnlyDoubleProperty = mouseYUIProp.readOnlyProperty

    /**
     * Cursor point in game coordinate space.
     */
    var mouseXWorld: Double
        get() = mouseXWorldProp.value
        private set(value) { mouseXWorldProp.value = value }

    var mouseYWorld: Double
        get() = mouseYWorldProp.value
        private set(value) { mouseYWorldProp.value = value }

    val mousePositionWorld
        get() = Point2D(mouseXWorld, mouseYWorld)

    /**
     * Cursor point in screen coordinate space.
     * Useful for UI manipulation.
     */
    var mouseXUI: Double
        get() = mouseXUIProp.value
        private set(value) { mouseXUIProp.value = value }

    var mouseYUI: Double
        get() = mouseYUIProp.value
        private set(value) { mouseYUIProp.value = value }

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
     * Registered action bindings.
     */
    private val bindings = LinkedHashMap<UserAction, ObservableTrigger>()
    private val sequenceBindings = LinkedHashMap<UserAction, InputSequence>()

    /**
     * A new copy map of existing bindings.
     */
    val allBindings: Map<UserAction, Trigger>
        get() = bindings.mapValues { (_, obsTrigger) -> obsTrigger.trigger.value }

    fun getTriggerName(action: UserAction): String = triggerNameProperty(action).value

    fun getTriggerName(actionName: String): String = getTriggerName(getActionByName(actionName))

    fun triggerNameProperty(action: UserAction) = bindings[action]?.name?.readOnlyProperty
            ?: throw IllegalArgumentException("Action $action not found")

    fun triggerProperty(action: UserAction) = bindings[action]?.trigger?.readOnlyProperty
            ?: throw IllegalArgumentException("Action $action not found")

    fun getActionByName(actionName: String): UserAction = bindings.keys.find { it.name == actionName }
            ?: throw IllegalArgumentException("Action $actionName not found")

    /**
     * Currently active actions.
     */
    private val currentActions = FXCollections.observableArrayList<UserAction>()

    private val activeTriggers = arrayListOf<Trigger>()
    private val listeners = arrayListOf<TriggerListener>()

    private val inputQueue = ArrayDeque<KeyCode>()

    /**
     * If action events should be processed.
     */
    var processInput = true

    /**
     * If input should be registered.
     */
    var registerInput = true

    private var isCapturing = false
    private var currentCapture: InputCapture? = null
    private val captureAppliers = arrayListOf<InputCapture.CaptureApplier>()

    private val eventFilters = HashMap<EventType<out Event>, MutableList<EventHandler<out Event>>>()
    private val eventHandlers = HashMap<EventType<out Event>, MutableList<EventHandler<out Event>>>()

    /**
     * Add JavaFX event filter.
     *
     * @param eventType type of events to listen
     * @param eventHandler filter for events
     */
    fun <T : Event> addEventFilter(eventType: EventType<T>,
                                   eventHandler: EventHandler<in T>) {
        addHandler(eventType, eventHandler, eventFilters)
    }

    /**
     * Remove JavaFX event filter.
     *
     * @param eventType type of events to listen
     * @param eventHandler filter for events
     */
    fun <T : Event> removeEventFilter(eventType: EventType<T>,
                                      eventHandler: EventHandler<in T>) {
        removeHandler(eventType, eventHandler, eventFilters)
    }

    /**
     * Add JavaFX event handler.
     *
     * @param eventType type of events to listen
     * @param eventHandler handler for events
     */
    fun <T : Event> addEventHandler(eventType: EventType<T>,
                                    eventHandler: EventHandler<in T>) {
        addHandler(eventType, eventHandler, eventHandlers)
    }

    /**
     * Remove JavaFX event handler.
     *
     * @param eventType type of events to listen
     * @param eventHandler handler for events
     */
    fun <T : Event> removeEventHandler(eventType: EventType<T>,
                                       eventHandler: EventHandler<in T>) {
        removeHandler(eventType, eventHandler, eventHandlers)
    }

    private fun <T : Event> addHandler(eventType: EventType<T>,
                                       eventHandler: EventHandler<in T>,
                                       map: MutableMap<EventType<*>, MutableList<EventHandler<*>>>) {
        val handlers = map.getOrDefault(eventType, CopyOnWriteArrayList<EventHandler<*>>())
        handlers += eventHandler

        map[eventType] = handlers
    }

    private fun <T : Event> removeHandler(eventType: EventType<T>,
                                          eventHandler: EventHandler<in T>,
                                          map: MutableMap<EventType<*>, MutableList<EventHandler<*>>>) {
        map[eventType]?.let {
            it.remove(eventHandler)

            // if the list of handlers is empty for [eventType], then remove the mapping
            if (it.isEmpty())
                map.remove(eventType)
        }
    }

    /**
     * Fire JavaFX event via filters.
     *
     * @param event the JavaFX event
     */
    fun fireEventViaFilters(event: Event) {
        fire(event, eventFilters)
    }

    /**
     * Fire JavaFX event via handlers.
     *
     * @param event the JavaFX event
     */
    fun fireEventViaHandlers(event: Event) {
        fire(event, eventHandlers)
    }

    private fun fire(event: Event,
                     map: MutableMap<EventType<*>, MutableList<EventHandler<*>>>) {
        if (map.isEmpty())
            return

        var eventType = event.eventType

        do {
            map[eventType]?.forEach {

                // if event is consumed, there is no point in going further
                if (event.isConsumed) {
                    return
                }

                (it as EventHandler<Event>).handle(event)
            }

            eventType = eventType.superType

        } while (eventType != null)
    }

    /**
     * Fire JavaFX event via handlers.
     *
     * @param event the JavaFX event
     */
    @Deprecated("Use [fireEventViaHandlers]", ReplaceWith("fireEventViaHandlers(event)"))
    fun fireEvent(event: Event) {
        fireEventViaHandlers(event)
    }

    fun update(tpf: Double) {
        if (isCapturing) {
            currentCapture!!.update(tpf)
        }

        if (!processInput)
            return

        for (i in currentActions.indices) {
            currentActions[i].action()
        }

        activeTriggers.forEach { trigger ->
            listeners.forEach {
                it.action(trigger)
            }
        }

        captureAppliers.forEach { it.update(tpf) }
    }

    /**
     * Called automatically by FXGL on key event.
     */
    fun onKeyEvent(keyEvent: KeyEvent) {
        if (!registerInput)
            return

        if (keyEvent.eventType == KeyEvent.KEY_PRESSED) {
            handlePressed(keyEvent)
        } else if (keyEvent.eventType == KeyEvent.KEY_RELEASED) {
            handleReleased(keyEvent)
        }
    }

    /**
     * Called automatically by FXGL on mouse event.
     */
    fun onMouseEvent(eventData: MouseEventData) {
        onMouseEvent(eventData.event, eventData.contentRootTranslation, eventData.viewportOrigin, eventData.viewportZoom, eventData.scaleRatioX, eventData.scaleRatioY)
    }

    /**
     * Called automatically by FXGL on mouse event.
     */
    fun onMouseEvent(mouseEvent: MouseEvent, contentRootTranslation: Point2D, viewportOrigin: Point2D, viewportZoom: Double, scaleRatioX: Double, scaleRatioY: Double) {
        if (!registerInput)
            return

        if (mouseEvent.eventType == MouseEvent.MOUSE_PRESSED) {
            handlePressed(mouseEvent)
        } else if (mouseEvent.eventType == MouseEvent.MOUSE_RELEASED) {
            handleReleased(mouseEvent)
        }

        mouseXUI = (mouseEvent.sceneX - contentRootTranslation.x) / scaleRatioX / viewportZoom
        mouseYUI = (mouseEvent.sceneY - contentRootTranslation.y) / scaleRatioY / viewportZoom

        mouseXWorld = mouseXUI + viewportOrigin.x
        mouseYWorld = mouseYUI + viewportOrigin.y
    }

    private fun handlePressed(event: InputEvent) {
        val newTrigger = if (event.eventType == MouseEvent.MOUSE_PRESSED) {
            MouseTrigger((event as MouseEvent).button)
        } else {
            KeyTrigger((event as KeyEvent).code).also {
                inputQueue.addLast(it.key)

                while (inputQueue.size > QUEUE_SIZE) {
                    inputQueue.removeFirst()
                }
            }
        }

        if (newTrigger !in activeTriggers) {
            activeTriggers += newTrigger

            listeners.forEach {
                it.begin(newTrigger)
            }
        }

        bindings.filter { (act, trigger) -> act !in currentActions && trigger.isTriggered(event) }
                .forEach { (act, _) ->
                    currentActions.add(act)

                    if (processInput)
                        act.begin()
                }

        if (event.eventType == KeyEvent.KEY_PRESSED) {
            sequenceBindings.filter { (act, seq) -> act !in currentActions && seq.matches(inputQueue) }
                    .forEach { (act, _) ->
                        currentActions.add(act)

                        if (processInput)
                            act.begin()
                    }
        }
    }

    private fun handleReleased(event: InputEvent) {
        val releasedTriggers = activeTriggers.filter { it.isReleased(event) }
        releasedTriggers.forEach { trigger ->
            listeners.forEach {
                it.end(trigger)
            }
        }

        activeTriggers -= releasedTriggers

        bindings.filter { (act, trigger) -> act in currentActions && trigger.isReleased(event) }
                .forEach { (act, _) ->
                    currentActions.remove(act)

                    if (processInput)
                        act.end()
                }

        if (event.eventType == KeyEvent.KEY_RELEASED) {
            val key = (event as KeyEvent).code

            sequenceBindings.filter { (act, seq) -> act in currentActions && seq.lastKey == key }
                    .forEach { (act, _) ->
                        currentActions.remove(act)

                        if (processInput)
                            act.end()
                    }
        }
    }

    fun addTriggerListener(triggerListener: TriggerListener) {
        listeners += triggerListener
    }

    fun removeTriggerListener(triggerListener: TriggerListener) {
        listeners -= triggerListener
    }

    /**
     * Clears all active actions.
     * Releases all key presses and mouse clicks for a single frame.
     * This fires onActionEnd() on all such released actions.
     *
     * Note: if this is called while the trigger is pressed,
     * then once the trigger is released, a single onActionEnd() will fire.
     */
    fun clearAll() {
        log.debug("Clearing active input actions")

        if (processInput) {
            currentActions.forEach { it.end() }
        }

        currentActions.clear()
        activeTriggers.clear()

        stopCapture()
        captureAppliers.clear()
    }

    fun addAction(action: UserAction, sequence: InputSequence) {
        sequenceBindings[action] = sequence
    }

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

    private fun addBinding(action: UserAction, trigger: Trigger) {
        require(!bindings.containsKey(action)) { "Action already exists: ${action.name}" }
        require(!isTriggerBound(trigger)) { "Trigger is already bound: $trigger" }

        bindings[action] = ObservableTrigger(trigger)

        log.debug("Registered new binding: $action - $trigger")
    }

    /**
     * @return true if rebound, false if action not found or there is another action bound to key
     */
    @JvmOverloads fun rebind(action: UserAction, key: KeyCode, modifier: InputModifier = InputModifier.NONE) =
            rebind(action, KeyTrigger(key, modifier))

    /**
     * @return true if rebound, false if action not found or there is another action bound to mouse button
     */
    @JvmOverloads fun rebind(action: UserAction, button: MouseButton, modifier: InputModifier = InputModifier.NONE) =
            rebind(action, MouseTrigger(button, modifier))

    private fun rebind(action: UserAction, newTrigger: Trigger): Boolean {
        // if no such action or trigger already bound
        if (!bindings.containsKey(action) || isTriggerBound(newTrigger))
            return false

        bindings[action]!!.trigger.value = newTrigger
        return true
    }

    private fun isTriggerBound(trigger: Trigger) = bindings.values.any { it.trigger.value == trigger }

    /* CAPTURE */

    /**
     * Calling this when capture already started has no effect.
     *
     * @return a capture object that is populated with input data until [stopCapture]
     */
    fun startCapture(): InputCapture {
        if (isCapturing)
            return currentCapture!!

        isCapturing = true
        currentCapture = InputCapture()
        addTriggerListener(currentCapture!!)

        return currentCapture!!
    }

    /**
     * Calling this without calling [startCapture] first has no effect.
     */
    fun stopCapture() {
        if (isCapturing) {
            isCapturing = false
            removeTriggerListener(currentCapture!!)
        }
    }

    fun applyCapture(capture: InputCapture) {
        captureAppliers += InputCapture.CaptureApplier(this, capture)
    }

    /* VIRTUAL */

    private val virtualButtons = hashMapOf<VirtualButton, KeyCode>()

    fun addAction(action: UserAction, key: KeyCode, virtualButton: VirtualButton) {
        addAction(action, key, InputModifier.NONE, virtualButton)
    }

    fun addAction(action: UserAction, key: KeyCode, modifier: InputModifier, virtualButton: VirtualButton) {
        require(!isIllegal(key)) { "Cannot bind to illegal key: $key" }

        addBinding(action, KeyTrigger(key, modifier))
        addVirtualButton(virtualButton, key)
    }

    private fun addVirtualButton(virtualButton: VirtualButton, key: KeyCode) {
        virtualButtons[virtualButton] = key
    }

    fun pressVirtual(virtualButton: VirtualButton) {
        virtualButtons[virtualButton]?.let { mockKeyPress(it) }
    }

    fun releaseVirtual(virtualButton: VirtualButton) {
        virtualButtons[virtualButton]?.let { mockKeyRelease(it) }
    }

    fun createVirtualJoystick(): VirtualJoystick = FXGLVirtualJoystick(this)

    /**
     * Creates a view containing virtual dpad (4 directional controls).
     */
    fun createVirtualDpadView() = createVirtualDpadView(FXGLVirtualDpad(this))

    fun createVirtualDpadView(dpad: VirtualDpad): Group {
        return dpad.createView()
    }

    fun createXboxVirtualControllerView() = createVirtualControllerView(XboxVirtualController(this))

    fun createPSVirtualControllerView() = createVirtualControllerView(PSVirtualController(this))

    fun createVirtualControllerView(controller: VirtualController): Group {
        return controller.createView()
    }

    fun createXboxVirtualController() = XboxVirtualController(this)

    fun createPSVirtualController() = PSVirtualController(this)

    fun createVirtualDpad() = FXGLVirtualDpad(this)

    @JvmOverloads fun createVirtualMenuKeyView(menuKey: KeyCode, isMenuEnabled: Boolean = false): Node {
        return createVirtualMenuKeyView(FXGLVirtualMenuKey(this, menuKey, isMenuEnabled))
    }

    fun createVirtualMenuKeyView(virtualKey: VirtualMenuKey): Node {
        return virtualKey.createViewAndAttachHandler()
    }

    /* MOCKING */

    internal fun mockKeyPressEvent(key: KeyCode, modifier: InputModifier = InputModifier.NONE) {
        fireEvent(makeKeyEvent(key, KeyEvent.KEY_PRESSED, modifier))
    }

    internal fun mockKeyReleaseEvent(key: KeyCode, modifier: InputModifier = InputModifier.NONE) {
        fireEvent(makeKeyEvent(key, KeyEvent.KEY_RELEASED, modifier))
    }

    fun mockTriggerPress(trigger: Trigger) {
        when (trigger) {
            is KeyTrigger -> mockKeyPress(trigger.key, trigger.modifier)
            is MouseTrigger -> mockButtonPress(trigger.button, trigger.modifier)
        }
    }

    fun mockTriggerRelease(trigger: Trigger) {
        when (trigger) {
            is KeyTrigger -> mockKeyRelease(trigger.key, trigger.modifier)
            is MouseTrigger -> mockButtonRelease(trigger.button, trigger.modifier)
        }
    }

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

    private fun makeKeyEvent(key: KeyCode, eventType: EventType<KeyEvent>, modifier: InputModifier) =
        KeyEvent(eventType, "", key.toString(), key,
                modifier == InputModifier.SHIFT,
                modifier == InputModifier.CTRL,
                modifier == InputModifier.ALT,
                false)

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

    private fun makeMouseEvent(btn: MouseButton, eventType: EventType<MouseEvent>,
                               gameX: Double, gameY: Double, modifier: InputModifier) =
            MouseEvent(eventType, gameX, gameY, gameX, gameY, btn, 0,
                    modifier == InputModifier.SHIFT,
                    modifier == InputModifier.CTRL,
                    modifier == InputModifier.ALT,
                    false, false, false, false, false, false, false, null)
}