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
        this.list = Collections.unmodifiableList(list)
    }

    /**
     * Copy constructor.
     */
    constructor(copy: Credits) {
        list = copy.list
    }
}