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

import com.almasb.fxeventbus.EventBus
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
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.input.*
import java.util.*
import java.util.function.Consumer

open class Input @Inject private constructor() : UserProfileSavable {

    private val log = FXGLLogger.getLogger("FXGL.Input")

    /**
     * Holds mouse state information
     */
    val mouse = Mouse()

    /**
     * Contains a list of user actions and keys/mouse buttons which trigger those
     * actions.
     */
    val bindings = FXCollections.observableArrayList<InputBinding>()
        get() = FXCollections.unmodifiableObservableList(field)

    /**
     * Currently active actions.
     */
    private val currentActions = FXCollections.observableArrayList<UserAction>()

    var processActions = true

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

            if (event.event is MouseEvent) {
                val mouseEvent = event.event as MouseEvent
                if (mouseEvent.eventType == MouseEvent.MOUSE_PRESSED) {
                    buttons.put(mouseEvent.button, true)
                    handlePressed(mouseEvent)
                } else if (mouseEvent.eventType == MouseEvent.MOUSE_RELEASED) {
                    buttons.put(mouseEvent.button, false)
                    handleReleased(mouseEvent)
                }

                mouse.update(mouseEvent)
                mouse.gameXY = event.gameXY
            } else {
                val keyEvent = event.event as KeyEvent
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

    private fun handlePressed(event: InputEvent) {
        bindings.filter { it.isTriggered(event) }
                .map { it.action }
                .filter { !currentActions.contains(it) }
                .forEach { currentActions.add(it) }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun handleReleased(event: InputEvent) {
        bindings.filter({ binding ->
            if (event is KeyEvent) {
                when (event.code) {
                    KeyCode.CONTROL -> return@filter binding.trigger.getModifier() == InputModifier.CTRL
                    KeyCode.SHIFT -> return@filter binding.trigger.getModifier() == InputModifier.SHIFT
                    KeyCode.ALT -> return@filter binding.trigger.getModifier() == InputModifier.ALT
                }
            }

            binding.isTriggered(event)
        })
        .map { it.action }
        .forEach { currentActions.remove(it) }
    }

    /**
     * Clears all input, that is releases all key presses and mouse clicks
     * for a single frame.
     */
    fun clearAllInput() {
        log.finer("Clearing active input actions")

        currentActions.clear()
        keys.clear()
        buttons.clear()

        mouse.leftPressed = false
        mouse.rightPressed = false
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

    /* MOCKING */

    //private KeyEvent makeKeyEvent(KeyCode key, EventType<KeyEvent> eventType, InputModifier modifier) {
        //        return new KeyEvent(eventType, "", key.toString(), key,
        //                modifier == InputModifier.SHIFT,
        //                modifier == InputModifier.CTRL,
        //                modifier == InputModifier.ALT, false);
        //    }
        //
        //    /**
        //     * Mocks key press event. The behavior is equivalent to
        //     * user pressing and holding the key.
        //     *
        //     * @param key the key
        //     * @param modifier key modifier
        //     */
        //    public void mockKeyPress(KeyCode key, InputModifier modifier) {
        //        log.finer("Mocking key press: " + key + " + " + modifier);
        //        handlePressedNew(makeKeyEvent(key, KeyEvent.KEY_PRESSED, modifier));
        //    }
        //
        //    /**
        //     * Mocks key press event. The behavior is equivalent to
        //     * user pressing and holding the key.
        //     *
        //     * @param key the key
        //     */
        //    public void mockKeyPress(KeyCode key) {
        //        mockKeyPress(key, InputModifier.NONE);
        //    }
        //
        //    /**
        //     * Mocks key release event. The behavior is equivalent to
        //     * user releasing the key.
        //     *
        //     * @param key the key
        //     * @param modifier key modifier
        //     */
        //    public void mockKeyRelease(KeyCode key, InputModifier modifier) {
        //        log.finer("Mocking key release: " + key + " + " + modifier);
        //        handleReleasedNew(makeKeyEvent(key, KeyEvent.KEY_RELEASED, modifier));
        //    }
        //
        //    /**
        //     * Mocks key release event. The behavior is equivalent to
        //     * user releasing the key.
        //     *
        //     * @param key the key
        //     */
        //    public void mockKeyRelease(KeyCode key) {
        //        mockKeyRelease(key, InputModifier.NONE);
        //    }
        //
        //    private MouseEvent makeMouseEvent(MouseButton btn, EventType<MouseEvent> eventType,
        //                                      double gameX, double gameY, InputModifier modifier) {
        //        return new MouseEvent(eventType, gameX, gameY,
        //                gameX, gameY, btn, 0,
        //                modifier == InputModifier.SHIFT,
        //                modifier == InputModifier.CTRL,
        //                modifier == InputModifier.ALT,
        //                false, false, false, false, false, false, false, null);
        //    }
        //
        //    /**
        //     * Mocks mouse button press event. The behavior is equivalent to
        //     * user pressing and holding the button at given X Y.
        //     *
        //     * @param btn mouse button
        //     * @param modifier mouse button modifier
        //     */
        //    public void mockButtonPress(MouseButton btn, double gameX, double gameY, InputModifier modifier) {
        //        log.finer("Mocking button press: " + btn + " + " + modifier);
        //        handlePressedNew(makeMouseEvent(btn, MouseEvent.MOUSE_PRESSED, gameX, gameY, modifier));
        //    }
        //
        //    /**
        //     * Mocks mouse button press event. The behavior is equivalent to
        //     * user pressing and holding the button at given X Y.
        //     *
        //     * @param btn mouse button
        //     * @param gameX x location of cursor
        //     * @param gameY y location of cursor
        //     */
        //    public void mockButtonPress(MouseButton btn, double gameX, double gameY) {
        //        mockButtonPress(btn, gameX, gameY, InputModifier.NONE);
        //    }
        //
        //    /**
        //     * Mocks mouse button release event. The behavior is equivalent to
        //     * user releasing the button.
        //     *
        //     * @param btn mouse button
        //     * @param modifier mouse button modifier
        //     */
        //    public void mockButtonRelease(MouseButton btn, InputModifier modifier) {
        //        log.finer("Mocking button release: " + btn + " + " + modifier);
        //        handleReleasedNew(makeMouseEvent(btn, MouseEvent.MOUSE_RELEASED, 0, 0, modifier));
        //    }
        //
        //    /**
        //     * Mocks mouse button release event. The behavior is equivalent to
        //     * user releasing the button.
        //     *
        //     * @param btn mouse button
        //     */
        //    public void mockButtonRelease(MouseButton btn) {
        //        mockButtonRelease(btn, InputModifier.NONE);
        //    }



    /* INPUT MAPPINGS */

//    private Map<String, InputMapping> inputMappings = new HashMap<>();
//
//    /**
//     * Add input mapping. The actual implementation needs to be specified by
//     * {@link OnUserAction} annotation.
//     *
//     * @param inputMapping the mapping
//     */
//    public void addInputMapping(InputMapping inputMapping) {
//        inputMappings.put(inputMapping.getActionName(), inputMapping);
//    }
//
//    private InputMapping getInputMappingByName(String actionName) {
//        return inputMappings.get(actionName);
//    }
//
//    /**
//     * Given an object scans its methods for {@link OnUserAction} annotation
//     * and creates UserActions from its data.
//     *
//     * @param instance the class instance to scan
//     */
//    public void scanForUserActions(Object instance) {
//        Map<String, Map<ActionType, Method> > map = new HashMap<>();
//
//        for (Method method : instance.getClass().getDeclaredMethods()) {
//            OnUserAction action = method.getDeclaredAnnotation(OnUserAction.class);
//            if (action != null) {
//                Map<ActionType, Method> mapping = map.getOrDefault(action.name(), new HashMap<>());
//                if (mapping.isEmpty()) {
//                    map.put(action.name(), mapping);
//                }
//                mapping.put(action.type(), method);
//            }
//        }
//
//        map.forEach((name, mapping) -> {
//            Method onAction = mapping.get(ActionType.ON_ACTION);
//            Method onActionBegin = mapping.get(ActionType.ON_ACTION_BEGIN);
//            Method onActionEnd = mapping.get(ActionType.ON_ACTION_END);
//
//            UserAction action = new UserAction(name) {
//                @Override
//                protected void onActionBegin() {
//                    if (onActionBegin != null) {
//                        try {
//                            onActionBegin.invoke(instance);
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//
//                @Override
//                protected void onAction() {
//                    if (onAction != null) {
//                        try {
//                            onAction.invoke(instance);
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//
//                @Override
//                protected void onActionEnd() {
//                    if (onActionEnd != null) {
//                        try {
//                            onActionEnd.invoke(instance);
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            };
//
//
//            InputMapping inputMapping = getInputMappingByName(name);
//            if (inputMapping.isKeyTrigger()) {
//                addAction(action, inputMapping.getKeyTrigger(), inputMapping.getModifier());
//            } else if (inputMapping.isButtonTrigger()) {
//                addAction(action, inputMapping.getButtonTrigger(), inputMapping.getModifier());
//            }
//        });
//    }





    override fun save(profile: UserProfile) {
        log.finer("Saving data to profile")

        val bundle = UserProfile.Bundle("input")
        bindings.forEach { bundle.put(it.getActionName(), it.getTriggerName()) }

        bundle.log()
        profile.putBundle(bundle)
    }

    // TODO: refactor
    override fun load(profile: UserProfile) {
//        log.finer("Loading data from profile")
//
//        val bundle = profile.getBundle("input")
//        bundle.log()
//
//        for (binding in bindings) {
//            var triggerName = bundle.get<String>(binding.getActionName())
//            val plusIndex = triggerName.indexOf("+")
//            if (plusIndex != -1) {
//                triggerName = triggerName.substring(plusIndex + 1)
//            }
//
//            binding.removeTriggers()
//            try {
//                val key = KeyCode.getKeyCode(triggerName)
//                binding.setTrigger(key)
//            } catch (ignored: Exception) {
//                try {
//                    val btn = MouseButton.valueOf(triggerName)
//                    binding.setTrigger(btn)
//                } catch (e: Exception) {
//                    log.warning("Undefined trigger name: " + triggerName)
//                    throw IllegalArgumentException("Corrupt or incompatible user profile: " + e.message)
//                }
//
//            }
//
//        }
    }
}