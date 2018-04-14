/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.scene.FXGLScene
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AppStateMachineTest {

    private lateinit var stateMachine: AppStateMachine

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @BeforeEach
    fun setUp() {
        stateMachine = AppStateMachine(Loading, Play, Dialog, Intro, MainMenu, GameMenu, Initial)
    }

    @Test
    fun `Startup and get`() {
        assertTrue(stateMachine.currentState === Initial)
        assertTrue(stateMachine.dialogState === Dialog)
        assertTrue(stateMachine.loadingState === Loading)
        assertTrue(stateMachine.playState === Play)
        assertTrue(stateMachine.introState === Intro)
        assertTrue(stateMachine.mainMenuState === MainMenu)
        assertTrue(stateMachine.gameMenuState === GameMenu)
    }

    @Test
    fun `State checks`() {
        assertFalse(stateMachine.isInGameMenu)

        stateMachine.startGameMenu()

        assertTrue(stateMachine.isInGameMenu)

        assertFalse(stateMachine.isInPlay)

        stateMachine.startPlay()

        assertTrue(stateMachine.isInPlay)
    }

    @Test
    fun `Pop fails if no states to pop`() {
        assertThrows(IllegalStateException::class.java, {
            stateMachine.popState()
        })
    }

    @Test
    fun `Pop previously pushed state`() {
        stateMachine.pushState(Dialog)

        assertTrue(stateMachine.currentState === Dialog)

        stateMachine.popState()

        assertFalse(stateMachine.currentState === Dialog)
    }

    @Test
    fun `Start app states`() {
        stateMachine.startIntro()
        assertTrue(stateMachine.currentState === Intro)

        stateMachine.startLoad()
        assertTrue(stateMachine.currentState === Loading)

        stateMachine.startPlay()
        assertTrue(stateMachine.currentState === Play)

        stateMachine.startMainMenu()
        assertTrue(stateMachine.currentState === MainMenu)

        stateMachine.startGameMenu()
        assertTrue(stateMachine.currentState === GameMenu)
    }

    @Test
    fun `Listener app state`() {
        var count = 0

        val listener = object : StateChangeListener {
            override fun beforeEnter(state: State) {
                count++
                assertTrue(stateMachine.currentState === Initial)
                assertTrue(state === Play)
            }

            override fun entered(state: State) {
                count++
                assertTrue(stateMachine.currentState === state)
                assertTrue(state === Play)
            }

            override fun beforeExit(state: State) {
                count++
                assertTrue(stateMachine.currentState === Initial)
                assertTrue(state === Initial)
            }

            override fun exited(state: State) {
                count++
                assertTrue(stateMachine.currentState === Play)
                assertTrue(state === Initial)
            }
        }

        stateMachine.addListener(listener)
        stateMachine.startPlay()
        assertThat(count, `is`(4))

        stateMachine.removeListener(listener)
        stateMachine.startPlay()
        assertThat(count, `is`(4))
    }

    @Test
    fun `Listener push pop state`() {
        var count = 0

        val listener = object : StateChangeListener {
            override fun beforeEnter(state: State) {
                count++
                assertTrue(stateMachine.currentState === Initial)
                assertTrue(state === Dialog)
            }

            override fun entered(state: State) {
                count++
                assertTrue(stateMachine.currentState === state)
                assertTrue(state === Dialog)
            }

            override fun beforeExit(state: State) {
                if (state is SubState) {
                    count++
                    assertTrue(stateMachine.currentState === Dialog)
                    assertTrue(state === Dialog)
                }
            }

            override fun exited(state: State) {
                if (state is SubState) {
                    count++
                    assertTrue(stateMachine.currentState === Initial)
                    assertTrue(state === Dialog)
                }
            }
        }

        stateMachine.addListener(listener)

        stateMachine.pushState(Dialog)

        assertThat(count, `is`(2))

        stateMachine.popState()

        assertThat(count, `is`(4))
    }

    @Test
    fun `Throw if intro or menus not available`() {
        stateMachine = AppStateMachine(Loading, Play, Dialog, AppState.EMPTY, AppState.EMPTY, AppState.EMPTY, Initial)

        assertThrows(IllegalStateException::class.java, {
            stateMachine.introState
        })

        assertThrows(IllegalStateException::class.java, {
            stateMachine.mainMenuState
        })

        assertThrows(IllegalStateException::class.java, {
            stateMachine.gameMenuState
        })
    }

    @Test
    fun `update`() {
        var count = 0.0

        stateMachine = AppStateMachine(object : AppState(object : FXGLScene() {}) {
            override fun onUpdate(tpf: Double) {
                count += tpf
            }
        }, object : AppState(object : FXGLScene() {}) {
            override fun onUpdate(tpf: Double) {
                count -= tpf
            }
        }, Dialog, Intro, MainMenu, GameMenu, Initial)

        stateMachine.startPlay()

        stateMachine.onUpdate(1.0)

        assertThat(count, `is`(-1.0))

        stateMachine.startLoad()

        stateMachine.onUpdate(1.0)

        assertThat(count, `is`(0.0))
    }

    private object Loading : AppState(object : FXGLScene() {})
    private object Play : AppState(object : FXGLScene() {})
    private object Dialog : SubState()
    private object Intro : AppState(object : FXGLScene() {})
    private object MainMenu : AppState(object : FXGLScene() {})
    private object GameMenu : AppState(object : FXGLScene() {})
    private object Initial : AppState(object : FXGLScene() {})
}