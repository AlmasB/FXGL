/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.fsm

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface State<T> {

    val isSubState: Boolean

    /**
     * Only honored if [isSubState] returns true.
     * Allow this substate to be active together with (sub)state above in the state machine
     * hierarchy.
     */
    val isAllowConcurrency: Boolean

    /**
     * Called before this state is to be set active and this state does not form
     * a part of the hierarchy. If it does, only [onEnter] is called.
     */
    fun onCreate()

    /**
     * Called after this state is removed from the hierarchy.
     */
    fun onDestroy()

    /**
     * Called after transitioning from [prevState].
     */
    fun onEnteredFrom(prevState: T)

    /**
     * Called before transitioning to [nextState].
     */
    fun onExitingTo(nextState: T)
}