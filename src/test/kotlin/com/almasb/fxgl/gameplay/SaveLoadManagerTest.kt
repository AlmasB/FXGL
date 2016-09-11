/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.gameplay

import com.almasb.easyio.FS
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockServicesModule
import com.almasb.fxgl.event.NotificationEvent
import com.almasb.fxgl.io.DataFile
import com.almasb.fxgl.io.SaveFile
import com.almasb.fxgl.settings.UserProfile
import org.hamcrest.CoreMatchers.*
import org.junit.AfterClass
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
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
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.mockServices(MockServicesModule())
            // save load system relies on these to be present
            FXGL.setProperty("fs.profiledir", "testprofiles/")
            FXGL.setProperty("fs.profilename", "user.profile")
            FXGL.setProperty("fs.savedir", "saves/")
            FXGL.setProperty("fs.savefile.ext", ".sav")
            FXGL.setProperty("fs.datafile.ext", ".dat")

            cleanUp()
        }

        @AfterClass
        @JvmStatic fun cleanUp() {
            // ensure previous tests have been cleared
            FS.deleteDirectoryTask("testprofiles/").execute()

            assertTrue("Profiles dir is present before", !Files.exists(Paths.get("testprofiles/")))
        }
    }

    @Before
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
        manager.saveProfileTask(UserProfile("TestApp", "TestVersion")).execute()

        assertTrue("Profiles dir was not created",
                Files.exists(Paths.get("testprofiles/")))

        assertTrue("Profile file was not created",
                Files.exists(Paths.get("testprofiles/TestProfileName/user.profile")))

        assertTrue("Saves dir was not created",
                Files.exists(Paths.get("testprofiles/TestProfileName/saves/")))
    }

    fun `Save game data`() {
        manager.saveTask(DataFile("TestData"), SaveFile("TestSave", LocalDateTime.now())).execute()

        assertTrue("Save file was not created",
                Files.exists(Paths.get("testprofiles/TestProfileName/saves/TestSave.sav")))

        assertTrue("Data file was not created",
                Files.exists(Paths.get("testprofiles/TestProfileName/saves/TestSave.dat")))
    }

    fun `Load game data`() {
        val saveFile = manager.loadLastModifiedSaveFileTask().execute()

        assertThat(saveFile, `is`(notNullValue()))

        val dataFile = manager.loadTask(saveFile).execute()

        assertThat(dataFile, `is`(notNullValue()))

        val data = dataFile!!.data as String

        assertThat(data, `is`("TestData"))
    }
}