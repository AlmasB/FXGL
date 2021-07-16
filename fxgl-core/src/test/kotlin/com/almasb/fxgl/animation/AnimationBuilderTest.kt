/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.animation

import com.almasb.fxgl.core.Updatable
import com.almasb.fxgl.core.UpdatableRunner
import com.almasb.fxgl.test.RunWithFX
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.stream.Stream

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class AnimationBuilderTest {

    private lateinit var scene: UpdatableRunner
    private lateinit var builder: AnimationBuilder
    private lateinit var e: MockEntity
    private lateinit var node: Node

    @BeforeEach
    fun setUp() {
        scene = makeRunner()
        builder = AnimationBuilder(scene)

        e = MockEntity()
        node = Rectangle()
    }

    @Test
    fun `Duration`() {
        val anim = builder.duration(Duration.seconds(2.0))
                .translate(e)
                .from(Point2D(10.0, 10.0))
                .to(Point2D(50.0, 50.0))
                .build()

        anim.start()

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(10.0))

        anim.onUpdate(0.5)

        assertThat(e.x, `is`(20.0))
        assertThat(e.y, `is`(20.0))

        anim.onUpdate(0.5)

        assertThat(e.x, `is`(30.0))
        assertThat(e.y, `is`(30.0))

        anim.onUpdate(0.5)

        assertThat(e.x, `is`(40.0))
        assertThat(e.y, `is`(40.0))

        anim.onUpdate(0.5)

        assertThat(e.x, `is`(50.0))
        assertThat(e.y, `is`(50.0))
    }

    @Test
    fun `Delay`() {
        val anim = builder.delay(Duration.seconds(2.0))
                .translate(e)
                .from(Point2D(10.0, 10.0))
                .to(Point2D(50.0, 50.0))
                .build()

        anim.start()

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(10.0))

        anim.onUpdate(1.0)

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(10.0))

        anim.onUpdate(1.0)

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(10.0))

        anim.onUpdate(1.0)

        assertThat(e.x, `is`(50.0))
        assertThat(e.y, `is`(50.0))
    }

    @Test
    fun `Repeat`() {
        val anim = builder.repeat(3)
                .fade(e)
                .from(0.0)
                .to(1.0)
                .build()

        anim.start()

        assertThat(e.opacity, `is`(0.0))
        anim.onUpdate(1.0)
        assertThat(e.opacity, `is`(1.0))

        // Testing if the animation restarts.
        anim.onUpdate(0.1)
        assertThat(e.opacity, `is`(0.1))

        anim.onUpdate(0.9)
        assertThat(e.opacity, `is`(1.0))

        // Finishing another loop.
        anim.onUpdate(1.0)
        assertThat(e.opacity, `is`(1.0))

        // Test if the animation had stopped.
        anim.onUpdate(0.1)
        assertThat(e.opacity, `is`(1.0))
    }

    @Test
    fun `Repeat Infinite`() {
        val anim = builder.repeatInfinitely()
                .fade(node)
                .from(0.0)
                .to(1.0)
                .build()

        anim.start()
        for (i in 0..19) {
            anim.onUpdate(0.1)
            assertThat(node.opacity, `is`(0.1))

            anim.onUpdate(0.9)
            assertThat(node.opacity, `is`(1.0))
        }
    }

    @Test
    fun `On Finished`() {
        var count = 0

        val anim = builder.repeat(2)
                .onFinished(Runnable { count++ })
                .onCycleFinished(Runnable { count-- })
                .fade(e)
                .from(0.0)
                .to(1.0)
                .build()

        anim.start()

        anim.onUpdate(1.0)

        // on cycle finished
        assertThat(count, `is`(-1))

        anim.onUpdate(1.0)

        // on cycle finished + on finished
        assertThat(count, `is`(-1))
    }

    @Test
    fun `Reverse`() {
        val anim = builder.autoReverse(true).repeat(2)
                .fade(e)
                .from(0.0)
                .to(1.0)
                .build()

        anim.start()
        assertThat(e.opacity, `is`(0.0))

        anim.onUpdate(1.0)
        assertThat(e.opacity, `is`(1.0))

        anim.onUpdate(0.1)
        assertThat(e.opacity, `is`(0.9))

        anim.onUpdate(0.9)
        assertThat(e.opacity, `is`(0.0))
    }

    @Test
    fun `With interpolator`() {
        val anim = builder.interpolator(Interpolators.QUADRATIC.EASE_IN())
                .fade(e)
                .from(0.0)
                .to(1.0)
                .build()

        anim.start()
        assertThat(e.opacity, `is`(0.0))

        anim.onUpdate(0.5)
        assertThat(e.opacity, `is`(0.25))

        anim.onUpdate(0.3)
        assertThat(e.opacity, closeTo(0.64, 0.01))

        anim.onUpdate(0.2)
        assertThat(e.opacity, `is`(1.0))
    }

    @Test
    fun `Translate`() {
        val anim = builder.translate(e)
                .from(Point2D(10.0, 10.0))
                .to(Point2D(50.0, 50.0))
                .build()

        anim.start()

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(10.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.x, `is`(50.0))
        assertThat(e.y, `is`(50.0))
    }

    @Test
    fun `Translate 3D`() {
        val anim = builder.translate(e)
                .from(Point3D(10.0, 10.0, 5.0))
                .to(Point3D(50.0, 50.0, 3.0))
                .build()

        anim.start()

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(10.0))
        assertThat(e.z, `is`(5.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.x, `is`(50.0))
        assertThat(e.y, `is`(50.0))
        assertThat(e.z, `is`(3.0))
    }

    @ParameterizedTest
    @MethodSource("pathProvider")
    fun `Translate along a path`(path: Shape) {
        val anim = builder.translate(e)
                .alongPath(path)
                .build()

        anim.start()

        assertThat(e.x, closeTo(10.0, 0.05))
        assertThat(e.y, closeTo(10.0, 0.05))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.x, `is`(55.0))
        assertThat(e.y, `is`(33.0))
    }

    @Test
    fun `Fade`() {
        val anim = builder.fade(e)
                .from(0.0)
                .to(1.0)
                .build()

        anim.start()

        assertThat(e.opacity, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.opacity, `is`(1.0))
    }

    @Test
    fun `Rotate entity`() {
        val anim = builder.rotate(e)
                .from(0.0)
                .to(180.0)
                .build()

        anim.start()

        assertThat(e.rotation, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.rotation, `is`(180.0))
    }

    @Test
    fun `Rotate node`() {
        val anim = builder.rotate(node)
                .from(0.0)
                .to(180.0)
                .build()

        anim.start()

        assertThat(node.rotate, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(node.rotate, `is`(180.0))
    }

    @Test
    fun `Rotate node with origin`() {
        val anim = builder.rotate(node)
                .origin(Point2D(1.0, 5.0))
                .from(0.0)
                .to(180.0)
                .build()

        anim.start()

        // rotate should have been added
        val rotate = node.transforms[0] as Rotate

        assertThat(rotate.pivotX, `is`(1.0))
        assertThat(rotate.pivotY, `is`(5.0))

        assertThat(rotate.angle, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(rotate.angle, `is`(180.0))

        val anim2 = builder.rotate(node)
                .origin(Point2D(5.0, 5.0))
                .from(0.0)
                .to(180.0)
                .build()

        anim2.start()

        val rotate2 = node.transforms[0] as Rotate

        assertThat(rotate2.pivotX, `is`(5.0))
        assertThat(rotate2.pivotY, `is`(5.0))

        assertThat(rotate, `is`(rotate2))
    }

    @Test
    fun `Rotate entity with origin`() {
        val anim = builder.rotate(e)
                .origin(Point2D(1.0, 5.0))
                .from(0.0)
                .to(180.0)
                .build()

        anim.start()

        assertThat(e.rotationOriginValue.x, `is`(1.0))
        assertThat(e.rotationOriginValue.y, `is`(5.0))

        assertThat(e.rotation, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.rotation, `is`(180.0))
    }

    @Test
    fun `Rotate entity 3D`() {
        val anim = builder.rotate(e)
                .origin(Point3D(0.0, 0.0, 0.0))
                .from(Point3D(0.0, 2.0, 4.0))
                .to(Point3D(4.0, 5.0, -2.0))
                .build()

        anim.start()

        assertThat(e.rotationX, `is`(0.0))
        assertThat(e.rotationY, `is`(2.0))
        assertThat(e.rotationZ, `is`(4.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.rotationX, `is`(4.0))
        assertThat(e.rotationY, `is`(5.0))
        assertThat(e.rotationZ, `is`(-2.0))
    }

    @Test
    fun `Rotate node 3D`() {
        val anim = builder.rotate(node)
                .from(Point3D(0.0, 2.0, 4.0))
                .to(Point3D(4.0, 5.0, -2.0))
                .build()

        anim.start()

        // rotate should have been added
        val rZ = node.transforms[0] as Rotate
        val rY = node.transforms[1] as Rotate
        val rX = node.transforms[2] as Rotate

        assertThat(rX.angle, `is`(0.0))
        assertThat(rY.angle, `is`(2.0))
        assertThat(rZ.angle, `is`(4.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(rX.angle, `is`(4.0))
        assertThat(rY.angle, `is`(5.0))
        assertThat(rZ.angle, `is`(-2.0))

        // use 2nd builder, the transforms should remain

        val anim2 = AnimationBuilder(makeRunner())
                .rotate(node)
                .from(Point3D(10.0, 12.0, 14.0))
                .to(Point3D(14.0, 15.0, -12.0))
                .build()

        anim2.start()

        val rZ2 = node.transforms[0] as Rotate
        val rY2 = node.transforms[1] as Rotate
        val rX2 = node.transforms[2] as Rotate

        assertThat(rZ2, `is`(rZ))
        assertThat(rY2, `is`(rY))
        assertThat(rX2, `is`(rX))

        assertThat(rX.angle, `is`(10.0))
        assertThat(rY.angle, `is`(12.0))
        assertThat(rZ.angle, `is`(14.0))

        anim2.onUpdate(0.5)
        anim2.onUpdate(0.5)

        assertThat(rX.angle, `is`(14.0))
        assertThat(rY.angle, `is`(15.0))
        assertThat(rZ.angle, `is`(-12.0))
    }

    @Test
    fun `Scale entity`() {
        val anim = builder.scale(e)
                .from(Point2D(1.0, 1.0))
                .to(Point2D(3.0, 3.0))
                .build()

        anim.start()

        assertThat(e.scaleX, `is`(1.0))
        assertThat(e.scaleY, `is`(1.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.scaleX, `is`(3.0))
        assertThat(e.scaleY, `is`(3.0))
    }

    @Test
    fun `Scale entity 3D`() {
        val anim = builder.scale(e)
                .origin(Point3D(0.0, 0.0, 0.0))
                .from(Point3D(1.0, 1.0, 0.5))
                .to(Point3D(3.0, 3.0, 2.0))
                .build()

        anim.start()

        assertThat(e.scaleX, `is`(1.0))
        assertThat(e.scaleY, `is`(1.0))
        assertThat(e.scaleZ, `is`(0.5))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.scaleX, `is`(3.0))
        assertThat(e.scaleY, `is`(3.0))
        assertThat(e.scaleZ, `is`(2.0))
    }

    @Test
    fun `Scale node`() {
        val anim = builder.scale(node)
                .from(Point2D(1.0, 1.0))
                .to(Point2D(3.0, 3.0))
                .build()

        anim.start()

        assertThat(node.scaleX, `is`(1.0))
        assertThat(node.scaleY, `is`(1.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(node.scaleX, `is`(3.0))
        assertThat(node.scaleY, `is`(3.0))
    }

    @Test
    fun `Scale with origin`() {
        val anim = builder.scale(node)
                .origin(Point2D(3.0, 5.0))
                .from(Point2D(1.0, 1.0))
                .to(Point2D(3.0, 3.0))
                .build()

        anim.start()

        // scale should have been added
        val scale = node.transforms[0] as Scale

        assertThat(scale.pivotX, `is`(3.0))
        assertThat(scale.pivotY, `is`(5.0))

        assertThat(scale.x, `is`(1.0))
        assertThat(scale.y, `is`(1.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(scale.x, `is`(3.0))
        assertThat(scale.y, `is`(3.0))

        val anim2 = builder.scale(node)
                .origin(Point2D(5.0, 5.0))
                .from(Point2D(1.0, 1.0))
                .to(Point2D(3.0, 3.0))
                .build()

        anim2.start()

        val scale2 = node.transforms[0] as Scale

        assertThat(scale2.pivotX, `is`(5.0))
        assertThat(scale2.pivotY, `is`(5.0))

        assertThat(scale, `is`(scale2))
    }

    @Test
    fun `Scale entity with origin`() {
        val anim = builder.scale(e)
                .origin(Point2D(3.0, 5.0))
                .from(Point2D(1.0, 1.0))
                .to(Point2D(3.0, 3.0))
                .build()

        anim.start()

        assertThat(e.scaleOriginValue.x, `is`(3.0))
        assertThat(e.scaleOriginValue.y, `is`(5.0))

        assertThat(e.scaleX, `is`(1.0))
        assertThat(e.scaleY, `is`(1.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.scaleX, `is`(3.0))
        assertThat(e.scaleY, `is`(3.0))
    }

    @Test
    fun `JavaFX property animation`() {
        val rect = Rectangle()

        val anim = builder.animate(rect.widthProperty())
                .from(10.0)
                .to(110.0)
                .build()

        anim.start()
        assertThat(rect.width, `is`(10.0))

        anim.onUpdate(0.5)
        assertThat(rect.width, `is`(60.0))

        anim.onUpdate(0.5)
        assertThat(rect.width, `is`(110.0))

        // animation stopped at this point
        anim.onUpdate(0.5)
        assertThat(rect.width, `is`(110.0))
    }

    @Test
    fun `Generic animated value animation`() {
        val color = AnimatedColor(Color.BLACK, Color.WHITE)

        var value = Color.BLACK

        val anim = builder.animate(color)
                .onProgress(Consumer { value = it })
                .build()

        anim.start()
        assertThat(value, `is`(Color.BLACK))

        anim.onUpdate(0.5)
        assertThat(value.red, `is`(0.5))
        assertThat(value.green, `is`(0.5))
        assertThat(value.blue, `is`(0.5))

        anim.onUpdate(0.5)
        assertThat(value, `is`(Color.WHITE))

        // animation stopped at this point
        anim.onUpdate(0.5)
        assertThat(value, `is`(Color.WHITE))
    }

    @Test
    fun `Bulk animations Rotate`() {
        val anim = builder.rotate(listOf(e, node))
                .from(0.0)
                .to(180.0)
                .build()

        anim.start()

        assertThat(e.rotation, `is`(0.0))
        assertThat(node.rotate, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.rotation, `is`(180.0))
        assertThat(node.rotate, `is`(180.0))
    }

    @Test
    fun `Bulk animations Translate`() {
        val anim = builder.translate(listOf(e, node))
                .from(Point2D(10.0, 10.0))
                .to(Point2D(50.0, 50.0))
                .build()

        anim.start()

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(10.0))
        assertThat(node.translateX, `is`(10.0))
        assertThat(node.translateY, `is`(10.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.x, `is`(50.0))
        assertThat(e.y, `is`(50.0))
        assertThat(node.translateX, `is`(50.0))
        assertThat(node.translateY, `is`(50.0))
    }

    @Test
    fun `Bulk animations Fade`() {
        val anim = builder.fade(listOf(e, node))
                .from(0.0)
                .to(1.0)
                .build()

        anim.start()

        assertThat(e.opacity, `is`(0.0))
        assertThat(node.opacity, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.opacity, `is`(1.0))
        assertThat(node.opacity, `is`(1.0))
    }

    @Test
    fun `Bulk animations Scale`() {
        val anim = builder.scale(listOf(e, node))
                .from(Point2D(1.0, 1.0))
                .to(Point2D(3.0, 3.0))
                .build()

        anim.start()

        assertThat(e.scaleX, `is`(1.0))
        assertThat(e.scaleY, `is`(1.0))

        assertThat(node.scaleX, `is`(1.0))
        assertThat(node.scaleY, `is`(1.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.scaleX, `is`(3.0))
        assertThat(e.scaleY, `is`(3.0))

        assertThat(node.scaleX, `is`(3.0))
        assertThat(node.scaleY, `is`(3.0))
    }

    @Test
    fun `Bulk animations throw if object is not Node or Entity`() {
        assertThrows<IllegalArgumentException> {
            builder.translate(listOf(""))
        }

        assertThrows<IllegalArgumentException> {
            builder.rotate(listOf(""))
        }

        assertThrows<IllegalArgumentException> {
            builder.scale(listOf(""))
        }

        assertThrows<IllegalArgumentException> {
            builder.fade(listOf(""))
        }
    }

    @Test
    fun `Build and play`() {
        val scene = makeRunner()

        builder.translate(node)
                .from(Point2D(10.0, 10.0))
                .to(Point2D(-10.0, 10.0))
                .buildAndPlay(scene)

        assertThat(node.translateX, `is`(10.0))
        assertThat(node.translateY, `is`(10.0))

        scene.update(0.5)

        assertThat(node.translateX, `is`(0.0))
        assertThat(node.translateY, `is`(10.0))

        scene.update(0.5)

        assertThat(node.translateX, `is`(-10.0))
        assertThat(node.translateY, `is`(10.0))

        // use scene with constructor

        AnimationBuilder(scene).translate(node)
                .from(Point2D(10.0, 10.0))
                .to(Point2D(-10.0, 10.0))
                .buildAndPlay()

        assertThat(node.translateX, `is`(10.0))
        assertThat(node.translateY, `is`(10.0))

        scene.update(0.5)

        assertThat(node.translateX, `is`(0.0))
        assertThat(node.translateY, `is`(10.0))

        scene.update(0.5)

        assertThat(node.translateX, `is`(-10.0))
        assertThat(node.translateY, `is`(10.0))
    }

    @Test
    fun `Build and play does not throw if no scene`() {
        assertDoesNotThrow {
            AnimationBuilder()
                    .translate(node)
                    .from(Point2D(10.0, 10.0))
                    .to(Point2D(-10.0, 10.0))
                    .buildAndPlay()
        }
    }

    @Test
    fun `Fade in Entity`() {
        val anim = builder.fadeIn(e)
                .build()

        anim.start()

        assertThat(e.opacity, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.opacity, `is`(1.0))
    }

    @Test
    fun `Fade in Node`() {
        val anim = builder.fadeIn(node)
                .build()

        anim.start()

        assertThat(node.opacity, `is`(0.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(node.opacity, `is`(1.0))
    }

    @Test
    fun `Fade out Entity`() {
        val anim = builder.fadeOut(e)
                .build()

        anim.start()

        assertThat(e.opacity, `is`(1.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(e.opacity, `is`(0.0))
    }

    @Test
    fun `Fade out Node`() {
        val anim = builder.fadeOut(node)
                .build()

        anim.start()

        assertThat(node.opacity, `is`(1.0))

        anim.onUpdate(0.5)
        anim.onUpdate(0.5)

        assertThat(node.opacity, `is`(0.0))
    }

    @Test
    fun `Bobble down`() {
        val anim = builder.bobbleDown(node)
                .build()

        anim.start()

        assertThat(node.translateX, `is`(0.0))
        assertThat(node.translateY, `is`(0.0))

        anim.onUpdate(0.05)
        anim.onUpdate(0.05)

        assertThat(node.translateX, `is`(0.0))
        assertThat(node.translateY, `is`(not(0.0)))
    }

    private fun makeRunner(): MockRunner = MockRunner()

    private class MockRunner : UpdatableRunner {
        private val listeners = CopyOnWriteArrayList<Updatable>()

        override fun addListener(updatable: Updatable) {
            listeners += updatable
        }

        override fun removeListener(updatable: Updatable) {
            listeners -= updatable
        }

        fun update(tpf: Double) {
            listeners.forEach { it.onUpdate(tpf) }
        }
    }

    private class MockEntity : Animatable {
        private val propX = SimpleDoubleProperty()
        private val propY = SimpleDoubleProperty()
        private val propZ = SimpleDoubleProperty()

        private val propRotX = SimpleDoubleProperty()
        private val propRotY = SimpleDoubleProperty()
        private val propRotZ = SimpleDoubleProperty()

        private val propScaleX = SimpleDoubleProperty()
        private val propScaleY = SimpleDoubleProperty()
        private val propScaleZ = SimpleDoubleProperty()

        private val propOpacity = SimpleDoubleProperty()

        var scaleOriginValue: Point3D = Point3D.ZERO
        var rotationOriginValue: Point3D = Point3D.ZERO

        val x get() = propX.value
        val y get() = propY.value
        val z get() = propZ.value

        val rotation get() = propRotZ.value

        val rotationX get() = propRotX.value
        val rotationY get() = propRotY.value
        val rotationZ get() = propRotZ.value

        val scaleX get() = propScaleX.value
        val scaleY get() = propScaleY.value
        val scaleZ get() = propScaleZ.value

        val opacity get() = propOpacity.value

        override fun xProperty(): DoubleProperty = propX
        override fun yProperty(): DoubleProperty = propY
        override fun zProperty(): DoubleProperty = propZ

        override fun scaleXProperty(): DoubleProperty = propScaleX
        override fun scaleYProperty(): DoubleProperty = propScaleY
        override fun scaleZProperty(): DoubleProperty = propScaleZ

        override fun rotationXProperty(): DoubleProperty = propRotX
        override fun rotationYProperty(): DoubleProperty = propRotY
        override fun rotationZProperty(): DoubleProperty = propRotZ

        override fun opacityProperty(): DoubleProperty = propOpacity

        override fun setScaleOrigin(pivotPoint: Point3D) {
            scaleOriginValue = pivotPoint
        }

        override fun setRotationOrigin(pivotPoint: Point3D) {
            rotationOriginValue = pivotPoint
        }
    }

    companion object {
        @Suppress("UNUSED")
        @JvmStatic fun pathProvider(): Stream<Arguments> {
            return Stream.of(
                    Arguments.arguments(QuadCurve(10.0, 10.0, 100.0, 50.0, 55.0, 33.0)),
                    Arguments.arguments(CubicCurve(10.0, 10.0, 50.0, 300.0, 100.0, 250.0, 55.0, 33.0)),
                    Arguments.arguments(Path().also {
                        it.elements += listOf(
                                MoveTo(10.0, 10.0),
                                LineTo(10.0, 50.0),
                                LineTo(45.0, 33.0),
                                LineTo(55.0, 33.0)
                        )
                    })
            )
        }
    }
}