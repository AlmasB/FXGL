package com.almasb.easyio.serialization

import org.apache.logging.log4j.LogManager
import java.io.Serializable
import java.util.*

/**
 * Bundle is used to store values mapped with certain keys.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Bundle(val name: String) : Serializable {

    companion object {
        private val serialVersionUID = 1L

        private val log = LogManager.getLogger(Bundle::class.java)
    }

    private val data = HashMap<String, Any>()

    /**
     * Store a [value] with given [key].
     */
    fun put(key: String, value: Serializable) {
        data.put(name + "." + key, value)
    }

    /**
     * Retrieve a value with given [key].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T {
        return data[name + "." + key] as T
    }

    /**
     * Logs contents of the bundle.
     */
    fun log() {
        log.debug("Logging bundle: $name")
        data.forEach { k, v -> log.debug("$k=$v") }
    }
}