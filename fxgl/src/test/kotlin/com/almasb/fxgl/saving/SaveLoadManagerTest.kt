/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving

import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.test.RunWithFX
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class SaveLoadManagerTest {

    private lateinit var saveLoadManager: SaveLoadManager
    private lateinit var manager: ProfileManager

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
        saveLoadManager = SaveLoadManager(GameSettings().toReadOnly())
        manager = saveLoadManager.getProfileManager("TestProfileName")
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

        assertTrue(Files.exists(Paths.get("profiles/")), "Profiles dir was not created")

        assertTrue(Files.exists(Paths.get("profiles/TestProfileName/user.profile")), "Profile file was not created")

        assertTrue(Files.exists(Paths.get("profiles/TestProfileName/saves/")), "Saves dir was not created")
    }

    fun `Save game data`() {
        manager.saveTask(DataFile("TestData"), SaveFile("TestSave", LocalDateTime.now())).run()

        assertTrue(Files.exists(Paths.get("profiles/TestProfileName/saves/TestSave.sav")), "Save file was not created")

        assertTrue(Files.exists(Paths.get("profiles/TestProfileName/saves/TestSave.dat")), "Data file was not created")
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