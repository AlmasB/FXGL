/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.io.FileSystemService
import com.almasb.fxgl.test.InjectInTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.invoke.MethodHandles
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

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
    fun `Test new - save - load`() {
        // we do a full clean before so it's important that
        // we keep the order, i.e. we save first so we have something to load from
        `Save game data`()
        `Load game data`()
        `Read file names`()
        `Delete game data`()
        `Delete profile`()
    }

    fun `Save game data`() {
        val bundle1 = Bundle("Hello")
        bundle1.put("id", 9)

        val data1 = DataFile()
        data1.putBundle(bundle1)

        val save1 = SaveFile("TestSave", "TestProfileName", "sav", LocalDateTime.now(), data1)

        saveLoadService.writeSaveFileTask(save1).run()

        assertTrue(saveLoadService.saveFileExists(SaveFile("TestSave", "TestProfileName", "sav")))
        assertFalse(saveLoadService.saveFileExists(SaveFile("TestSave2", "TestProfileName", "sav")))
        assertTrue(Files.exists(Paths.get("profiles/TestProfileName/TestSave.sav")), "Save file was not created")
    }

    fun `Load game data`() {
        val saveFile = saveLoadService.readSaveFileTask(SaveFile("TestSave", "TestProfileName", "sav")).run()

        assertThat(saveFile, `is`(notNullValue()))

        assertThat(saveFile.data.getBundle("Hello").get("id"), `is`(9))
    }

    fun `Read file names`() {
        val profileNames = saveLoadService.readProfileNamesTask().run()

        assertThat(profileNames, Matchers.contains("TestProfileName"))

        val saveFiles = saveLoadService.readSaveFilesTask("TestProfileName", "sav").run()

        assertThat(saveFiles.size, `is`(1))
        assertThat(saveFiles[0].name, `is`("TestSave"))
    }

    fun `Delete game data`() {
        assertTrue(saveLoadService.saveFileExists(SaveFile("TestSave", "TestProfileName", "sav")))

        saveLoadService.deleteSaveFileTask(SaveFile("TestSave", "TestProfileName", "sav")).run()

        assertFalse(saveLoadService.saveFileExists(SaveFile("TestSave", "TestProfileName", "sav")))
    }

    fun `Delete profile`() {
        assertTrue(Files.exists(Paths.get("profiles/TestProfileName")))

        saveLoadService.deleteProfileTask("TestProfileName").run()

        assertFalse(Files.exists(Paths.get("profiles/TestProfileName")))
    }
}