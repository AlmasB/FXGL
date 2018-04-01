/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.io.FS
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SaveLoadManagerTest {

    private lateinit var manager: SaveLoadManager

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
            // save load system relies on these to be present
            FXGL.getProperties().setValue("fs.profiledir", "testprofiles/")
            FXGL.getProperties().setValue("fs.profilename", "user.profile")
            FXGL.getProperties().setValue("fs.savedir", "saves/")
            FXGL.getProperties().setValue("fs.savefile.ext", ".sav")
            FXGL.getProperties().setValue("fs.datafile.ext", ".dat")

            cleanUp()
        }

        @AfterAll
        @JvmStatic fun cleanUp() {
            // ensure previous tests have been cleared
            FS.deleteDirectoryTask("testprofiles/").run()

            assertTrue(!Files.exists(Paths.get("testprofiles/")), "Profiles dir is present before")
        }
    }

    @BeforeEach
    fun setUp() {
        manager = SaveLoadManager("TestProfileName")
    }

    @Test
    fun `Test new - save - load`() {
        // we do a full clean before so it's important that
        // we keep the order, i.e. we save first so we have something to load from
        `Save new profile`()
        `Save game data`()
        `Load game data`()
    }

    fun `Save new profile`() {
        manager.saveProfileTask(UserProfile("TestApp", "TestVersion")).run()

        assertTrue(Files.exists(Paths.get("testprofiles/")), "Profiles dir was not created")

        assertTrue(Files.exists(Paths.get("testprofiles/TestProfileName/user.profile")), "Profile file was not created")

        assertTrue(Files.exists(Paths.get("testprofiles/TestProfileName/saves/")), "Saves dir was not created")
    }

    fun `Save game data`() {
        manager.saveTask(DataFile("TestData"), SaveFile("TestSave", LocalDateTime.now())).run()

        assertTrue(Files.exists(Paths.get("testprofiles/TestProfileName/saves/TestSave.sav")), "Save file was not created")

        assertTrue(Files.exists(Paths.get("testprofiles/TestProfileName/saves/TestSave.dat")), "Data file was not created")
    }

    fun `Load game data`() {
        val saveFile = manager.loadLastModifiedSaveFileTask().run()

        assertThat(saveFile, `is`(notNullValue()))

        val dataFile = manager.loadTask(saveFile).run()

        assertThat(dataFile, `is`(notNullValue()))

        val data = dataFile!!.data as String

        assertThat(data, `is`("TestData"))
    }
}