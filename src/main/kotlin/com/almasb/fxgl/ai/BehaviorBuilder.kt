/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.ai

import com.almasb.fxgl.entity.GameEntity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.branch.Sequence
import com.badlogic.gdx.ai.btree.branch.Selector
import java.util.function.Supplier

/**
 * API INCOMPLETE:
 * only if - then - else
 * is supported
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class BehaviorBuilder {

    private val root = Selector<GameEntity>()

    private lateinit var lastSequence: Sequence<GameEntity>
    private lateinit var lastSelector: Selector<GameEntity>

    init {
        lastSelector = root
    }

    private fun getLastChild() = root.getChild(root.childCount - 1)

    fun ifCondition(condition: Condition) {

    }

    fun ifCondition(condition: Supplier<Boolean>): BehaviorBuilder {

//        val selector = Selector<GameEntity>()
//
//        val sequence = Sequence<GameEntity>()
//        sequence.addChild(object : Condition() {
//            override fun evaluate(): Boolean {
//                return condition.get()
//            }
//        })
//
//        selector.addChild(sequence)

        //lastSelector = selector
        //lastSequence = sequence

        return this
    }

    fun thenDo(action: Runnable): BehaviorBuilder {

        // after if
        //val lastSequence = getLastChild() as Sequence

        lastSequence.addChild(object : Action() {
            override fun action() {
                action.run()
            }
        })

        return this
    }

    fun thenDoChain(action: (BehaviorBuilder) -> BehaviorBuilder): BehaviorBuilder {

        val selector = Selector<GameEntity>()
        val sequence = Sequence<GameEntity>()


        selector.addChild(sequence)

        return this
    }

    fun elseDo(action: Runnable): BehaviorBuilder {

        // after if then
        //val lastSelector = getLastChild() as Selector

        lastSelector.addChild(object : Action() {
            override fun action() {
                action.run()
            }
        })

        return this
    }

    fun build(): BehaviorTree<GameEntity> {
        return BehaviorTree(root)
    }
}