package com.almasb.easyio.serialization

/**
 * Marks a type as serializable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface SerializableType {

    /**
     * Write state to [bundle].
     */
    fun write(bundle: Bundle)

    /**
     * Read state from [bundle].
     */
    fun read(bundle: Bundle)
}