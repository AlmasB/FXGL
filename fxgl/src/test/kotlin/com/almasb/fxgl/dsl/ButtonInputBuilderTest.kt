package com.almasb.fxgl.dsl

import com.almasb.fxgl.input.Input
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ButtonInputBuilderTest {

    private lateinit var input: Input
    private val timePerFrame = 0.016

    @BeforeEach
    fun setUp() {
        input = Input()
    }

    @Test
    fun `onActionBegin is called when mouse button is pressed`() {
        var count = 0
        val expectedCount = 1

        ButtonInputBuilder(input, MouseButton.PRIMARY)
            .onActionBegin { count++ }

        input.mockButtonPress(MouseButton.PRIMARY)
        input.update(timePerFrame)

        assertEquals(expectedCount, count)
    }

    @Test
    fun `onAction is called while mouse button is held`() {
        var count = 0
        val expectedCount = 3

        ButtonInputBuilder(input, MouseButton.PRIMARY)
            .onAction { count++ }

        input.mockButtonPress(MouseButton.PRIMARY)

        input.update(timePerFrame)
        input.update(timePerFrame)
        input.update(timePerFrame)

        assertEquals(expectedCount, count)
    }

    @Test
    fun `onActionEnd is called when mouse button is released`() {
        var count = 0
        val expectedCount = 1

        ButtonInputBuilder(input, MouseButton.PRIMARY)
            .onActionEnd { count++ }

        input.mockButtonPress(MouseButton.PRIMARY)
        input.update(timePerFrame)

        input.mockButtonRelease(MouseButton.PRIMARY)
        input.update(timePerFrame)

        assertEquals(expectedCount, count)
    }

    @Test
    fun `all callbacks can be chained`() {
        var beginCount = 0
        var actionCount = 0
        var endCount = 0

        val expectedBeginCount = 1
        val expectedActionCount = 2
        val expectedEndCount = 1

        ButtonInputBuilder(input, MouseButton.PRIMARY)
            .onActionBegin { beginCount++ }
            .onAction { actionCount++ }
            .onActionEnd { endCount++ }

        input.mockButtonPress(MouseButton.PRIMARY)

        input.update(timePerFrame)
        input.update(timePerFrame)

        input.mockButtonRelease(MouseButton.PRIMARY)
        input.update(timePerFrame)

        assertEquals(expectedBeginCount, beginCount)
        assertEquals(expectedActionCount, actionCount)
        assertEquals(expectedEndCount, endCount)
    }

    @Test
    fun `FXGL onBtnBuilder with custom input supports onActionBegin`() {
        var count = 0
        val expectedCount = 1

        FXGL.onBtnBuilder(input, MouseButton.PRIMARY)
            .onActionBegin { count++ }

        input.mockButtonPress(MouseButton.PRIMARY)
        input.update(timePerFrame)

        assertEquals(expectedCount, count)
    }

    @Test
    fun `FXGL onBtnBuilder with custom input supports onAction`() {
        var count = 0
        val expectedCount = 2

        FXGL.onBtnBuilder(input, MouseButton.PRIMARY)
            .onAction { count++ }

        input.mockButtonPress(MouseButton.PRIMARY)

        input.update(timePerFrame)
        input.update(timePerFrame)

        assertEquals(expectedCount, count)
    }

    @Test
    fun `FXGL onBtnBuilder with custom input supports onActionEnd`() {
        var count = 0
        val expectedCount = 1

        FXGL.onBtnBuilder(input, MouseButton.PRIMARY)
            .onActionEnd { count++ }

        input.mockButtonPress(MouseButton.PRIMARY)
        input.update(timePerFrame)

        input.mockButtonRelease(MouseButton.PRIMARY)
        input.update(timePerFrame)

        assertEquals(expectedCount, count)
    }
}