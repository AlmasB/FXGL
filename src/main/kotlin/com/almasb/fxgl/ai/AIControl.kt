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

import com.almasb.ents.AbstractControl
import com.almasb.ents.Entity
import com.almasb.fxgl.entity.GameEntity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AIControl
private constructor() : AbstractControl() {

    private lateinit var behaviorTree: BehaviorTree<GameEntity>

    /**
     * Constructs AI control with given behavior tree.
     */
    constructor(behaviorTree: BehaviorTree<GameEntity>) : this() {
        this.behaviorTree = behaviorTree
    }

    /**
     * Constructs AI control with behavior tree parsed from the asset.
     */
    constructor(treeName: String) : this() {

        var tree = parsedTreesCache[treeName]

        if (tree == null) {
            tree = BehaviorTreeParser<GameEntity>().parse(javaClass.getResourceAsStream("/assets/ai/$treeName"), null)
            parsedTreesCache[treeName] = tree
        }

        this.behaviorTree = tree!!.cloneTask() as BehaviorTree<GameEntity>
    }

    companion object {
        //private val btreeLibManager = BehaviorTreeLibraryManager.getInstance()

        private val parsedTreesCache = HashMap<String, BehaviorTree<GameEntity> >()
    }

    override fun onAdded(entity: Entity) {

//        val libraryManager = BehaviorTreeLibraryManager.getInstance()
//
//        val actualBehavior = BehaviorTree<GameEntity>(createDogBehavior())
//        libraryManager.library.registerArchetypeTree("guard", actualBehavior)
//
//        tree = libraryManager.createBehaviorTree<GameEntity>("guard", enemy)

        behaviorTree.`object` = entity as GameEntity
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        behaviorTree.step()
    }
}