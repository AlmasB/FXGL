/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.entity.Entity
import dev.SandboxGameApp.AIType.GUARD
import javafx.beans.property.IntegerProperty
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SandboxGameApp : GameApplication() {
    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 720
            height = 640
            title = "Kotlin Game"
        }
    }

    companion object {
        val conditionals = hashMapOf<() -> Boolean, () -> Unit>()
    }

    override fun initGameVars(vars: MutableMap<String, Any>) {
        vars["score"] = 0
    }

    private enum class AIType {
        IDLE, GUARD
    }

    override fun initGame() {
        val player = entityBuilder().at(100.0, 100.0)
                .viewWithBBox(Rectangle(20.0, 20.0))
                .with(DeveloperWASDControl())
                .buildAndAttach()

        val enemy = entityBuilder().at(100.0, 100.0)
                .viewWithBBox(Rectangle(20.0, 20.0, Color.RED))
                .buildAndAttach()

        val gate = entityBuilder().at(300.0, 100.0)
                .viewWithBBox(Rectangle(20.0, 20.0, Color.RED))
                .buildAndAttach()

        val t = Text("HELLO WORLD")
        t.textProperty().bind(getip("score").asString())

        addUINode(t, 100.0, 200.0)

        // when e.x > 150 then println("Hello")

        val invisibleBlock = entityBuilder().at(300.0, 100.0)
                .viewWithBBox(Rectangle(20.0, 20.0, Color.RED))
                .buildAndAttach()


        `when` { player.x > 150 } then { enemy AI GUARD }

        `when` { player collidesWith enemy } then { getip("score") decrease 1 }

        `when` { gate `is` "open" } then { invisibleBlock.removeFromWorld() }
    }

    private infix fun Entity.`is`(propName: String): Boolean {
        return this.getBoolean(propName)
    }

    private infix fun IntegerProperty.decrease(value: Int) {
        this.value -= value
    }

    private infix fun Entity.AI(aiType: AIType) {

    }

    private infix fun Entity.collidesWith(other: Entity): Boolean {
        return this.isColliding(other)
    }

    infix fun remove(e: Entity) {

    }

    private infix fun `when`(condition: () -> Boolean): ConditionBuilder {
        return ConditionBuilder(condition)
    }

    private class ConditionBuilder(private val condition: () -> Boolean) {
        infix fun then(action: () -> Unit) {
            conditionals[condition] = action
        }
    }

    override fun onUpdate(tpf: Double) {
        conditionals.forEach { condition, action ->
            if (condition())
                action()
        }
    }
}



fun main() {
    GameApplication.launch(SandboxGameApp::class.java, emptyArray())
}