package com.almasb.fxgl.io

import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FSTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            cleanUp()

            Files.createDirectories(Paths.get("testdir"))
            Files.createDirectories(Paths.get("testdir/testsubdir"))

            Files.createFile(Paths.get("testdir/testfile.txt"))
            Files.createFile(Paths.get("testdir/testfile.json"))

            assertTrue(Files.exists(Paths.get("testdir/testfile.txt")), "test file is not present before")
            assertTrue(Files.exists(Paths.get("testdir/testfile.json")), "test file is not present before")
        }

        @AfterAll
        @JvmStatic fun cleanUp() {
            // ensure previous tests have been cleared
            Files.deleteIfExists(Paths.get("testdir/testfile.txt"))
            Files.deleteIfExists(Paths.get("testdir/testfile.json"))
            Files.deleteIfExists(Paths.get("testdir/testsubdir"))
            Files.deleteIfExists(Paths.get("testdir/somefile"))
            Files.deleteIfExists(Paths.get("testdir"))

            assertTrue(!Files.exists(Paths.get("testdir")), "test dir is present before")
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