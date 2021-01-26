/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.profile

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.io.FileSystemService
import com.almasb.fxgl.test.InjectInTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.invoke.MethodHandles
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SaveLoadServiceTest {

    private lateinit var saveLoadService: SaveLoadService

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            cleanUp()
        }

        @AfterAll
        @JvmStatic fun cleanUp() {

            // ensure previous tests have been cleared
            Paths.get("profiles/").toFile().deleteRecursively()

            assertTrue(!Files.exists(Paths.get("profiles/")), "Profiles dir is present before")
        }
    }

    @BeforeEach
    fun setUp() {
        saveLoadService = SaveLoadService()

        InjectInTest.inject(MethodHandles.lookup(), saveLoadService, "fs", FileSystemService().also { it.onInit() })
    }

    @Test
    fun `Test write - read`() {
        // we do a full clean before so it's important that
        // we keep the order, i.e. we save first so we have something to load from
        `Write game data`()
        `Read game data`()
        `Read file names`()
        `Delete game data`()
    }

    @Test
    fun `Test save write - read load`() {
        var count = 0

        val handler = object : SaveLoadHandler {
            override fun onSave(data: DataFile) {
                val bundle = Bundle("Test")
                bundle.put("int", 99)

                data.putBundle(bundle)

                count++
            }

            override fun onLoad(data: DataFile) {
                val bundle = data.getBundle("Test")
                val intValue = bundle.get<Int>("int")

                assertThat(intValue, `is`(99))

                count++
            }
        }

        saveLoadService.addHandler(handler)

        assertFalse(saveLoadService.saveFileExists("profiles/savewrite1.sav"))

        saveLoadService.saveAndWriteTask("profiles/savewrite1.sav").run()

        assertTrue(saveLoadService.saveFileExists("profiles/savewrite1.sav"))
        assertThat(count, `is`(1))

        saveLoadService.readAndLoadTask("profiles/savewrite1.sav").run()

        assertThat(count, `is`(2))

        saveLoadService.removeHandler(handler)

        // handler removed, so no callbacks beyond this point
        saveLoadService.saveAndWriteTask("profiles/savewrite1.sav").run()

        assertThat(count, `is`(2))

        saveLoadService.deleteSaveFileTask("profiles/savewrite1.sav").run()

        assertFalse(saveLoadService.saveFileExists("profiles/savewrite1.sav"))
    }

    @Test
    fun `Last modified save file`() {
        saveLoadService.saveAndWriteTask("profiles/s/latest.sav").run()

        val result = saveLoadService.readLastModifiedSaveFileTask("profiles/s/", ".sav").run()

        assertTrue(!result.isEmpty)
        assertThat(result.get().name, `is`("profiles/s/latest.sav"))

        saveLoadService.deleteSaveFileTask("profiles/s/latest.sav").run()

        assertFalse(saveLoadService.saveFileExists("profiles/s/latest.sav"))

        // Last modified save file returns optional empty if no matching files found
        val result2 = saveLoadService.readLastModifiedSaveFileTask("profiles/", ".blabla").run()

        assertTrue(result2.isEmpty)
    }

    fun `Write game data`() {
        val bundle1 = Bundle("Hello")
        bundle1.put("id", 9)

        val data1 = DataFile()
        data1.putBundle(bundle1)

        saveLoadService.writeTask("profiles/TestSave.sav", data1).run()

        assertTrue(saveLoadService.saveFileExists("profiles/TestSave.sav"))
    }

    fun `Read game data`() {
        val saveFile = saveLoadService.readTask("profiles/TestSave.sav").run()

        assertNotNull(saveFile)

        assertThat(saveFile.data.getBundle("Hello").get("id"), `is`(9))
    }

    fun `Read file names`() {
        saveLoadService.writeTask("profiles/TestSave2.sav", DataFile()).run()

        assertTrue(saveLoadService.saveFileExists("profiles/TestSave2.sav"))

        val saveFiles = saveLoadService.readSaveFilesTask("profiles", "sav").run()

        assertThat(saveFiles.size, `is`(2))

        assertThat(saveFiles.map { it.name }, containsInAnyOrder("profiles/TestSave.sav", "profiles/TestSave2.sav"))
    }

    fun `Delete game data`() {
        assertTrue(saveLoadService.saveFileExists("profiles/TestSave.sav"))

        saveLoadService.deleteSaveFileTask("profiles/TestSave.sav").run()

        assertFalse(saveLoadService.saveFileExists("profiles/TestSave.sav"))
    }
}