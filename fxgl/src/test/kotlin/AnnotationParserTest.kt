/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

import client.game.TestEntityFactory
import client.game.TestGameApplication
import client.game.Uppercase.TestUppercaseGameApplication
import client.game.collision.TestCollisionHandler
import client.game.collision.TestCollisionHandler2
import client.game.collision.TestCollisionHandler3
import com.almasb.fxgl.annotation.AddCollisionHandler
import com.almasb.fxgl.annotation.AnnotationParser
import com.almasb.fxgl.annotation.SetEntityFactory
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnnotationParserTest {

    @Test
    fun `Parse`() {
        val parser = AnnotationParser(TestGameApplication::class.java)

        assertFalse(parser.isDisabled)

        parser.parse(SetEntityFactory::class.java, AddCollisionHandler::class.java)

        var list: List<Class<*>> = parser.getClasses(SetEntityFactory::class.java)
        assertThat(list.size, `is`(1))
        assertThat(list, hasItem(TestEntityFactory::class.java))

        list = parser.getClasses(AddCollisionHandler::class.java)
        assertThat(list.size, `is`(3))
        assertThat(list, hasItems<Class<*>>(
                TestCollisionHandler::class.java,
                TestCollisionHandler2::class.java,
                TestCollisionHandler3::class.java))
    }

    @Test
    fun `Disabled if no package`() {
        val parser = AnnotationParser(NoPackageSample::class.java)

        assertTrue(parser.isDisabled)

        assertThat(parser.getClasses(SetEntityFactory::class.java).size, `is`(0))
        
        parser.parse(SetEntityFactory::class.java, AddCollisionHandler::class.java)

        assertThat(parser.getClasses(SetEntityFactory::class.java).size, `is`(0))
    }

    @Test
    fun `Disabled if no uppercase letters in package name`() {
        val parser = AnnotationParser(TestUppercaseGameApplication::class.java)

        assertTrue(parser.isDisabled)
    }
}