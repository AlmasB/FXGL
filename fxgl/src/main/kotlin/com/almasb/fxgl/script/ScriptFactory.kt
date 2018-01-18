/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.script

import com.almasb.fxgl.script.js.JS
import java.nio.file.Files
import java.nio.file.Path

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScriptFactory {

    companion object {
        @JvmStatic fun fromCode(scriptCode: String): Script {
            return JS(scriptCode)
        }

        @JvmStatic fun fromFile(file: Path): Script {
            return JS(Files.readAllLines(file).joinToString("\n", "", "\n"))
        }
    }
}