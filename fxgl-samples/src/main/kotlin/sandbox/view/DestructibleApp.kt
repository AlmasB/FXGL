/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.dsl.components.ExpireCleanComponent
import com.almasb.fxgl.dsl.components.ProjectileComponent
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.physics.PhysicsComponent
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef
import com.almasb.fxgl.physics.box2d.dynamics.BodyType
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.util.Duration

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DestructibleApp : GameApplication() {

    private enum class Type {
        PROJ, BRICK, SMALL
    }

    override fun initSettings(settings: GameSettings) {
        settings.setHeightFromRatio(16/9.0)
    }

    override fun initInput() {
        onKeyDown(KeyCode.F) {
            println(getGameWorld().entitiesCopy.size)
        }

        onKeyDown(KeyCode.G) {
            shoot()
        }
    }

    private fun shoot() {
        val physics = PhysicsComponent()
        physics.setBodyType(BodyType.DYNAMIC)
        physics.setOnPhysicsInitialized {
            physics.linearVelocity = Point2D(1.0, 0.0).multiply(1750.0)
        }

        entityBuilder()
                .type(Type.PROJ)
                .at(getInput().mousePositionWorld)
                .viewWithBBox(texture("rocket.png", 15.0 * 2, 4.0 * 2))
                //.with(ProjectileComponent(Point2D(1.0, 0.0), 1400.0))
                .with(physics)
                .with(ExpireCleanComponent(Duration.seconds(1.0)))
                .collidable()
                .buildAndAttach()
    }

    override fun initGame() {
        getGameScene().setBackgroundColor(Color.BLACK)

        run({ shoot() }, Duration.seconds(0.05))

        for (y in 0..6) {
            for (x in 8..11) {
                getGameWorld().addEntity(makeBrick(x * 64.0, y * 64.0))
            }
        }

        entityBuilder().buildScreenBoundsAndAttach(20.0)

        getPhysicsWorld().setGravity(0.0, 10.0 * getSettings().pixelsPerMeter)

        onCollisionBegin(Type.PROJ, Type.BRICK) { bullet, brick ->
            bullet.removeFromWorld()
            destroy(brick)
        }

        onCollisionBegin(Type.PROJ, Type.SMALL) { bullet, brick ->
            bullet.removeFromWorld()
            brick.removeFromWorld()
        }
    }

    private fun destroy(e: Entity) {
        val texture = e.viewComponent.children[0] as Texture

        val size = 8.0

        val rects = AASubdivision.divide(Rectangle2D(0.0, 0.0, 64.0, 64.0), 22, 8)

        rects.forEach {
            val sub = texture.subTexture(it)

            val physics = PhysicsComponent()
            physics.setBodyType(BodyType.DYNAMIC)

            val bd = BodyDef().also {
                it.isFixedRotation = false
                it.type = if (FXGLMath.randomBoolean(0.75)) BodyType.DYNAMIC else BodyType.STATIC
            }

            physics.setFixtureDef(FixtureDef().density(0.05f).restitution(0.05f))
            physics.setBodyDef(bd)
            physics.setOnPhysicsInitialized {
                physics.linearVelocity = FXGLMath.randomPoint2D().multiply(1000.0)
            }

            entityBuilder()
                    .at(e.position.add(it.minX, it.minY))
                    .type(Type.SMALL)
                    .viewWithBBox(sub)
                    .with(physics)
                    .collidable()
                    .buildAndAttach()
        }

        e.removeFromWorld()
    }

    private fun makeBrick(x: Double, y: Double): Entity {
        return entityBuilder()
                .at(x, y)
                .type(Type.BRICK)
                .viewWithBBox("brick.png")
                .with(PhysicsComponent())
                .collidable()
                .build()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(DestructibleApp::class.java, args)
        }
    }
}