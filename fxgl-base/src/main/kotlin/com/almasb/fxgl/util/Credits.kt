/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util


import java.util.*

/**
 * Simple data structure to contain a list of credits.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Credits {

    /**
     * Unmodifiable list of credits.
     */
    val list: List<String>

    /**
     * Constructs credits from given list of names.
     */
    constructor(list: List<String>) {
        val newList = ArrayList(list)
        with(newList) {
            add("")
            add("Powered by FXGL ${Version.getAsString()}")
            add("Author: Almas Baimagambetov")
            add("https://github.com/AlmasB/FXGL")
            add("")
        }

        this.list = Collections.unmodifiableList(newList)
    }

    /**
     * Copy constructor.
     */
    constructor(copy: Credits) {
        list = copy.list
    }
}