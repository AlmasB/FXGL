/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.input.virtual.VirtualButton
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable
import com.almasb.fxgl.scene.Viewport
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
import java.lang.reflect.Method
import java.util.*

class Input : UserProfileSavable {

    private val ILLEGAL_KEYS = arrayOf(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.ALT)

    private val log = Logger.get(javaClass)

    /**
     * Cursor point in game coordinate space.
     */
    private var gameX = 0.0
    private var gameY = 0.0

    fun getMousePositionWorld() = Point2D(gameX, gameY)

    /**
     * Cursor point in screen coordinate space.
     * Useful for UI manipulation.
     */
    private var sceneX = 0.0
    private var sceneY = 0.0

    fun getMousePositionUI() = Point2D(sceneX, sceneY)

    fun getMouseXWorld() = getMousePositionWorld().x
    fun getMouseYWorld() = getMousePositionWorld().y

    fun getMouseXUI() = getMousePositionUI().x
    fun getMouseYUI() = getMousePositionUI().y

    /**
     * @param gamePosition point in game world
     * @return vector from given point to mouse cursor point
     */
    fun getVectorToMouse(gamePosition: Point2D): Point2D = getMousePositionWorld().subtract(gamePosition)

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

    fun triggerNameProperty(action: UserAction): ReadOnlyStringProperty {
        return triggerNames[action]?.readOnlyProperty ?: throw IllegalArgumentException("Action $action not found")
    }

    fun getTriggerName(action: UserAction): String = triggerNameProperty(action).value

    fun getTriggerByActionName(actionName: String): String = getTriggerName(getActionByName(actionName))

    fun getActionByName(actionName: String): UserAction = bindings.keys.find { it.name == actionName }
            ?: throw IllegalArgumentException("Action $actionName not found")

    private val triggers = hashMapOf<UserAction, ReadOnlyObjectWrapper<Trigger>>()

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
        if (processInput) {
            for (i in currentActions.indices) {
                currentActions[i].fireAction()
            }
        }
    }

    /**
     * Called on key event.
     */
    fun onKeyEvent(keyEvent: KeyEvent) {
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

    /**
     * Called on mouse event.
     *
     * @param event mouse event
     * @param viewport current viewport where the even occurred
     * @param scaleRatio scale ratio of the display where the event occurred
     */
    fun onMouseEvent(mouseEvent: MouseEvent, viewport: Viewport, scaleRatioX: Double, scaleRatioY: Double) {
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

        gameX = sceneX / scaleRatioX + viewport.getX()
        gameY = sceneY / scaleRatioY + viewport.getY()
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
                .forEach {
                    currentActions.add(it.key)

                    if (processInput)
                        it.key.fireActionBegin()
                }
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
        .forEach {
            currentActions.remove(it.key)

            if (processInput)
                it.key.fireActionEnd()
        }
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

    private val virtualButtons = hashMapOf<VirtualButton, KeyCode>()

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
        if (ILLEGAL_KEYS.contains(key))
            throw IllegalArgumentException("Cannot bind to illegal key: $key")

        addBinding(action, KeyTrigger(key, modifier))
    }

    @JvmOverloads fun addAction(action: UserAction, key: KeyCode, virtualButton: VirtualButton) {
        if (ILLEGAL_KEYS.contains(key))
            throw IllegalArgumentException("Cannot bind to illegal key: $key")

        addBinding(action, KeyTrigger(key, InputModifier.NONE))
        addVirtualButton(virtualButton, key)
    }

    private fun addBinding(action: UserAction, trigger: Trigger) {
        if (bindings.containsKey(action))
            throw IllegalArgumentException("Action with name \"${action.name}\" already exists")

        if (bindings.containsValue(trigger))
            throw IllegalArgumentException("Trigger $trigger is already bound")

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

    private fun addVirtualButton(virtualButton: VirtualButton, key: KeyCode) {
        virtualButtons[virtualButton] = key
    }

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

    internal fun pressVirtual(virtualButton: VirtualButton) {
        virtualButtons[virtualButton]?.let { mockKeyPress(it) }
    }

    internal fun releaseVirtual(virtualButton: VirtualButton) {
        virtualButtons[virtualButton]?.let { mockKeyRelease(it) }
    }

    /* MOCKING */

    internal fun mockKeyPressEvent(key: KeyCode, modifier: InputModifier = InputModifier.NONE) {
        fireEvent(makeKeyEvent(key, KeyEvent.KEY_PRESSED, modifier))
    }

    internal fun mockKeyRleaseEvent(key: KeyCode, modifier: InputModifier = InputModifier.NONE) {
        fireEvent(makeKeyEvent(key, KeyEvent.KEY_RELEASED, modifier))
    }

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

        this.gameX = gameX
        this.gameY = gameY
        handlePressed(makeMouseEvent(button, MouseEvent.MOUSE_PRESSED, gameX, gameY, modifier))
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

        this.gameX = gameX
        this.gameY = gameY
        handleReleased(makeMouseEvent(button, MouseEvent.MOUSE_RELEASED, gameX, gameY, modifier))
    }

    @JvmOverloads fun mockButtonRelease(button: MouseButton, inputModifier: InputModifier = InputModifier.NONE) {
        mockButtonRelease(button, getMouseXWorld(), getMouseYWorld(), inputModifier)
    }

    /* INPUT MAPPINGS */

    private val inputMappings = HashMap<String, InputMapping>()

    /**
     * Add input mapping. The actual implementation needs to be specified by
     * {@link OnUserAction} annotation.
     *
     * @param mapping the mapping
     */
    fun addInputMapping(inputMapping: InputMapping) {
        inputMappings.put(inputMapping.actionName, inputMapping)
    }

    /**
     * Given an object, scans its methods for {@link OnUserAction} annotation
     * and creates UserActions from its data.
     *
     * @param instance the class instance to scan
     */
    fun scanForUserActions(instance: Any) {
        val map = HashMap<String, HashMap<ActionType, Method> >()

        for (method in instance.javaClass.declaredMethods) {
            val action = method.getAnnotation(OnUserAction::class.java)
            if (action != null) {
                val mapping = map[action.name] ?: hashMapOf()
                if (mapping.isEmpty()) {
                    map[action.name] = mapping
                }

                mapping[action.type] = method
            }
        }

        map.forEach { (name, mapping) ->
            val action = object : UserAction(name) {
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

            val inputMapping: InputMapping = inputMappings[name] ?: throw IllegalStateException("No input mapping found for action $name")

            if (inputMapping.isKeyTrigger()) {
                addAction(action, inputMapping.getKeyTrigger(), inputMapping.modifier)
            } else {
                addAction(action, inputMapping.getButtonTrigger(), inputMapping.modifier)
            }
        }
    }

    override fun save(profile: UserProfile) {
        log.debug("Saving data to profile")

        val bundle = Bundle("input")
        bindings.forEach { bundle.put(it.key.toString(), it.value.toString()) }

        bundle.log()
        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        log.debug("Loading data from profile")

        val bundle = profile.getBundle("input")
        bundle.log()

        for (binding in bindings) {

            val action = binding.key

            // if binding is not present in bundle, then we added some new binding thru code
            // it will be saved on next serialization and will be found in bundle
            var triggerName: String? = bundle.get<String>("$action")
            if (triggerName == null)
                continue

            var modifierName = "NONE"

            val plusIndex = triggerName.indexOf("+")
            if (plusIndex != -1) {
                modifierName = triggerName.substring(0, plusIndex)
                triggerName = triggerName.substring(plusIndex + 1)
            }

            // if triggerName was CTRL+A, we end up with:
            // triggerName = A
            // modifierName = CTRL

            try {
                val key = KeyCode.getKeyCode(triggerName)
                rebind(action, key, InputModifier.valueOf(modifierName))
            } catch (ignored: Exception) {
                try {
                    val btn = MouseTrigger.buttonFromString(triggerName)
                    rebind(action, btn, InputModifier.valueOf(modifierName))
                } catch (e: Exception) {
                    log.warning("Undefined trigger name: " + triggerName)
                    throw IllegalArgumentException("Corrupt or incompatible user profile: " + e.message)
                }
            }
        }
    }
}