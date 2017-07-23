package com.almasb.fxgl.io

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.hasItems
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FSTest {

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            cleanUp()

            Files.createDirectories(Paths.get("testdir"))
            Files.createDirectories(Paths.get("testdir/testsubdir"))

            Files.createFile(Paths.get("testdir/testfile.txt"))
            Files.createFile(Paths.get("testdir/testfile.json"))

            assertTrue("test file is not present before", Files.exists(Paths.get("testdir/testfile.txt")))
            assertTrue("test file is not present before", Files.exists(Paths.get("testdir/testfile.json")))
        }

        @AfterClass
        @JvmStatic fun cleanUp() {
            // ensure previous tests have been cleared
            Files.deleteIfExists(Paths.get("testdir/testfile.txt"))
            Files.deleteIfExists(Paths.get("testdir/testfile.json"))
            Files.deleteIfExists(Paths.get("testdir/testsubdir"))
            Files.deleteIfExists(Paths.get("testdir/somefile"))
            Files.deleteIfExists(Paths.get("testdir"))

            assertTrue("test dir is present before", !Files.exists(Paths.get("testdir")))
        }
    }

    @Test
    fun `Load file names from a dir`() {
        val fileNames = FS.loadFileNamesTask("testdir", false).execute()

        assertThat(fileNames, hasItems("testfile.txt", "testfile.json"))
    }

    @Test
    fun `Load dir names from a dir`() {
        val dirNames = FS.loadDirectoryNamesTask("testdir", false).execute()

        assertThat(dirNames, hasItems("testsubdir"))
    }

    @Test
    fun `File not present after delete`() {
        Files.createFile(Paths.get("testdir/somefile"))
        assertTrue(Files.exists(Paths.get("testdir/somefile")))

        FS.deleteFileTask("testdir/somefile").execute()
        assertFalse(Files.exists(Paths.get("testdir/somefile")))
    }
}