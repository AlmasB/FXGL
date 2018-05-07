/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

/**
 * Generates SystemPropertyKey.java using keys from system.properties
 * to make property accesses typesafe.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
fun main(args: Array<String>) {
    val bundle = ResourceBundle.getBundle("com.almasb.fxgl.app.system")
    val keys = bundle.keySet().sorted()

    val javaFile = Paths.get("fxgl-base/src/main/java/com/almasb/fxgl/app/SystemPropertyKey.java")

    val lines = arrayListOf(
            "// DO NOT MODIFY! THIS FILE IS AUTO-GENERATED\n",
            "package com.almasb.fxgl.app;\n",
            "public final class SystemPropertyKey {\n"
    )

    lines.addAll(keys.map { "    public static final String ${it.replace(".", "_").toUpperCase()} = \"$it\";\n" })

    lines.add("}\n")

    Files.deleteIfExists(javaFile)

    Files.write(javaFile, lines, StandardOpenOption.CREATE_NEW)
}