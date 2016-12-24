/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sandbox

import com.almasb.fxgl.parser.json.JSONEntity
import com.almasb.fxgl.parser.json.JSONWorld
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class KotlinTest {
}

fun main(args: Array<String>) {

    val mapper = ObjectMapper()

    val world = JSONWorld("Level1", arrayListOf(
            JSONEntity("Player", 300.0, 400.0),
            JSONEntity("EnemyArcher", 200.0, 55.0)
    ))

    //mapper.writeValue(File("level1.json"), world)

    val world2 = mapper.readValue<JSONWorld>(File("level1.json"), JSONWorld::class.java)

    println(world2.name)
    println(world2.entities)
}