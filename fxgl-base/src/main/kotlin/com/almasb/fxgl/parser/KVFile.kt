/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.util.Predicate
import java.util.*

/**
 * Represents a simple key value file, similar to java.util.Properties.
 * However, it is easier to work with in the fxgl asset management context.
 *
 * Example of a .kv file:
 *
 * hp = 100.50
 * level = 5
 * name = Test Name
 * canJump = true
 *
 * Only primitive types and String are supported.
 * The resulting data type will be determined by the field type with matching name.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class KVFile {

    companion object {

        private val log = Logger.get("KVFile")

        /**
         * A factory constructor for KVFile, which also populates the
         * created instance with key-value data from given object using
         * its declared fields.
         *
         * @param data object to convert to kv file
         * @return kv file
         */
        @JvmStatic fun from(data: Any): KVFile {
            try {
                val file = KVFile()

                for (f in data.javaClass.declaredFields) {
                    f.isAccessible = true

                    file.entries.add(Pair(f.name, f.get(data).toString()))
                }

                return file
            } catch (e: Exception) {
                throw IllegalArgumentException("Cannot create KVFile from: $data", e)
            }
        }
    }

    private var entries = ArrayList<Pair<String, String>>()

    private val validEntry = Predicate<Array<String>> { kv ->
        if (kv.size != 2) {
            log.warning("Syntax error: " + Arrays.toString(kv))
            return@Predicate false
        }

        return@Predicate true
    }

    /**
     * Constructs KVFile from lines of plain text.
     * Each line must be in format:
     *
     * key = value
     *
     * Empty spaces are ignored before, after and between tokens.
     *
     * @param fileLines list of lines from file
     */
    constructor(fileLines: List<String>) {
        entries.addAll(
                fileLines.map { it.split("=", limit = 2) }
                        .filter { it.size == 2 }
                        .map { it[0].trim() to it[1].trim() }
        )
    }

    private constructor() {}

    @Throws(Exception::class)
    private fun setKV(instance: Any, key: String, value: String) {
        val field = instance.javaClass.getDeclaredField(key)
        field.isAccessible = true

        when (field.type.simpleName) {
            "int" -> field.setInt(instance, Integer.parseInt(value))
            "short" -> field.setShort(instance, java.lang.Short.parseShort(value))
            "long" -> field.setLong(instance, java.lang.Long.parseLong(value))
            "byte" -> field.setByte(instance, java.lang.Byte.parseByte(value))
            "double" -> field.setDouble(instance, java.lang.Double.parseDouble(value))
            "float" -> field.setFloat(instance, java.lang.Float.parseFloat(value))
            "boolean" -> field.setBoolean(instance, java.lang.Boolean.parseBoolean(value))
            "char" -> field.setChar(instance, if (value.length > 0) value[0] else ' ')
            "String" -> field.set(instance, value)
            else -> {
                log.warning("Unknown field type: " + field.type.simpleName)
                log.warning("Only primitive data types and String are supported!")
            }
        }
    }

    /**
     * Converts an instance of KVFile to instance of the data
     * structure and populates its fields with appropriate values.
     *
     * @param type data structure type
     * @return instance of type
     */
    fun <T> to(type: Class<T>): T {
        try {
            val instance = type.newInstance()

            for (kv in entries)
                setKV(instance as Any, kv.first, kv.second)

            return instance
        } catch (e: Exception) {
            throw IllegalArgumentException("Cannot parse KVFile to: $type", e)
        }
    }

    override fun toString() = "KVFile [entries=$entries]"
}
