package com.almasb.fxgl.io

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.nio.file.Files
import java.nio.file.Files.*
import java.nio.file.Paths.get as path

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FSTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            cleanUp()

            createDirectories(path("testdir"))
            createDirectories(path("testdir/testsubdir"))
            createDirectories(path("testdir/testsubdir/testsubsubdir"))
            createDirectories(path("testdir_empty"))

            createFile(path("testdir/testfile.txt"))
            createFile(path("testdir/testfile.json"))
            createFile(path("testdir/testsubdir/testfile2.json"))

            assertTrue(exists(path("testdir/testsubdir/testsubsubdir")), "test dir is not present before")
            assertTrue(exists(path("testdir/testfile.txt")), "test file is not present before")
            assertTrue(exists(path("testdir/testfile.json")), "test file is not present before")
        }

        @AfterAll
        @JvmStatic fun cleanUp() {
            // ensure previous tests have been cleared
            deleteIfExists(path("testdir/testsubdir/testsubsubdir"))
            deleteIfExists(path("testdir/testfile.txt"))
            deleteIfExists(path("testdir/testfile.json"))
            deleteIfExists(path("testdir/file.a"))
            deleteIfExists(path("testdir/file.b"))
            deleteIfExists(path("testdir/testsubdir/testfile2.json"))
            deleteIfExists(path("testdir/testsubdir"))
            deleteIfExists(path("testdir/somefile"))
            deleteIfExists(path("testdir/somedir"))
            deleteIfExists(path("testdir/testexist.txt"))
            deleteIfExists(path("testdir/testexist"))
            deleteIfExists(path("testdir"))
            deleteIfExists(path("somefile.data"))
            deleteIfExists(path("somefile.txt"))

            deleteIfExists(path("parentdir/childfile.dat"))
            deleteIfExists(path("parentdir"))

            deleteIfExists(path("testdir_empty"))

            assertTrue(!exists(path("testdir")), "test dir is present before")
        }
    }

    private val fs = FS(true)

    @Test
    fun `Exists correctly reports dirs and files`() {
        assertTrue(fs.exists("testdir"))
        assertTrue(fs.exists("testdir/"))
        assertFalse(fs.exists("testdir/testexist"))
        assertFalse(fs.exists("testdir/testexist.txt"))

        createDirectory(path("testdir/testexist"))
        assertTrue(fs.exists("testdir/testexist"))

        createDirectory(path("testdir/testexist.txt"))
        assertTrue(fs.exists("testdir/testexist.txt"))

        deleteIfExists(path("testdir/testexist"))
        assertFalse(fs.exists("testdir/testexist"))

        deleteIfExists(path("testdir/testexist.txt"))
        assertFalse(fs.exists("testdir/testexist.txt"))
    }

    @Test
    fun `Write to and read from binary file`() {
        val data = "Test FXGL FS!"

        assertFalse(exists(path("somefile.data")))

        fs.writeDataTask(data, "somefile.data").run()

        assertTrue(exists(path("somefile.data")))

        val data2 = fs.readDataTask<String>("somefile.data").run()

        assertThat(data, `is`(data2))
    }

    @Test
    fun `Write to and read from text file`() {
        val text = listOf("Test Line1", "Test Line2")

        assertFalse(exists(path("somefile.txt")))

        fs.writeDataTask(text, "somefile.txt").run()

        assertTrue(exists(path("somefile.txt")))

        assertThat(Files.readAllLines(path("somefile.txt")), `is`(text))
    }

    @Test
    fun `Write creates parent dirs if necessary and does not fail when parent already exists`() {
        val data = "Test FXGL FS!"

        assertFalse(exists(path("parentdir")))

        fs.writeDataTask(data, "parentdir/childfile.dat").run()

        assertTrue(exists(path("parentdir")))

        // parentdir exists, check we can write
        fs.writeDataTask(data, "parentdir/childfile.dat").run()
    }

    @Test
    fun `Load file names from a dir`() {
        val fileNames = fs.loadFileNamesTask("testdir", false).run()

        assertThat(fileNames, containsInAnyOrder("testfile.txt", "testfile.json"))

        val fileNames2 = fs.loadFileNamesTask("testdir", true).run()

        assertThat(fileNames2, containsInAnyOrder("testfile.txt", "testfile.json", "testsubdir/testfile2.json"))

        val fileNames3 = fs.loadFileNamesTask("testdir", true, listOf(FileExtension("json"))).run()

        assertThat(fileNames3, containsInAnyOrder("testfile.json", "testsubdir/testfile2.json"))

        val fileNames4 = fs.loadFileNamesTask("testdir", false, listOf(FileExtension("json"))).run()

        assertThat(fileNames4, containsInAnyOrder("testfile.json"))
    }

    @Test
    fun `Load dir names from a dir`() {
        val dirNames = fs.loadDirectoryNamesTask("testdir", false).run()

        assertThat(dirNames, containsInAnyOrder("testsubdir"))

        val dirNames2 = fs.loadDirectoryNamesTask("testdir", true).run()

        assertThat(dirNames2, containsInAnyOrder("testsubdir", "testsubdir/testsubsubdir"))
    }

    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    @Test
    fun `Load last modified file`() {
        fs.writeDataTask("a", "testdir/file.a").run()

        // this is to make sure that even on Unix systems we have a difference in timestamps
        Thread.sleep(1000)

        fs.writeDataTask("b", "testdir/file.b").run()

        val data = fs.loadLastModifiedFileTask<String>("testdir", false).run()
        assertThat(data, `is`("b"))

        val data2 = fs.loadLastModifiedFileTask<String>("testdir/", true).run()
        assertThat(data2, `is`("b"))

        deleteIfExists(path("testdir/file.a"))
        deleteIfExists(path("testdir/file.b"))
    }

    @Test
    fun `Load last modified file task does not throw if no file in dir`() {
        val data: Any? = fs.loadLastModifiedFileTask<Any>("testdir_empty", false).run()
        assertNull(data)
    }

    @Test
    fun `Create dir and delete dir`() {
        assertFalse(exists(path("testdir/somedir/")))

        fs.createDirectoryTask("testdir/somedir/").run()
        assertTrue(exists(path("testdir/somedir/")))

        fs.deleteDirectoryTask("testdir/somedir/").run()
        assertFalse(exists(path("testdir/somedir/")))
    }

    @Test
    fun `File or dir not present after delete`() {
        createFile(path("testdir/somefile"))
        assertTrue(exists(path("testdir/somefile")))

        fs.deleteFileTask("testdir/somefile").run()
        assertFalse(exists(path("testdir/somefile")))
    }

    @Test
    fun `Delete dir throws if dir does not exit`() {
        var exception = ""

        fs.deleteDirectoryTask("bla-bla-does-not-exist")
                .onFailure { exception = it.message!! }
                .run()

        assertTrue(exception.isNotEmpty())
    }
}
