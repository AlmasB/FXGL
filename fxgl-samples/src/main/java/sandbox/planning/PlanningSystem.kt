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

package sandbox.planning

import org.jgrapht.Graph
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.Multigraph
import org.jgrapht.graph.Pseudograph
import org.jgrapht.graph.SimpleGraph
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PlanningSystem {

    private val operations = mutableSetOf<Op>()

    fun addOperation(operation: Op) {
        operations.add(operation)
    }

    fun createPlan(currentState: State, goalState: State): Plan {
        val earlyPlan = operations.find { it.preCondition().matches(currentState) && it.postCondition().matches(goalState) }

        if (earlyPlan != null) {
            return Plan(listOf(earlyPlan))
        }

        ////////////////////////////////////////////

//        val path = mutableListOf<Op>()
//
//        val availableOps = operations.toMutableList()
//
//        var nowState = currentState
//
//        var canMove = true
//        var done = false
//
//        while (canMove && !done) {
//            val op = availableOps.find { it.preCondition().matches(nowState) }
//
//            if (op == null) {
//                canMove = false
//            } else {
//                nowState = op.postCondition()
//                availableOps.remove(op)
//
//                path.add(op)
//
//                if (nowState.matches(goalState)) {
//                    done = true
//                }
//            }
//        }

        ////////////////////////////////////////////////////



//        val nodes = operations.toMutableList()
//        for (i in nodes.indices) {
//            var j = i + 1
//            while (j < nodes.size) {
//                val node1 = nodes[i]
//                val node2 = nodes[j]
//
//                graph.addEdge(node1, node2)
//
//                j++
//            }
//        }

        try {
            val graph = Pseudograph<State, Op>(Op::class.java)
//            {
//                override fun containsVertex(vertex: State): Boolean {
//                    return vertexSet().any { it.matches(vertex) }
//                }
//            }


            operations.forEach {
                val pre = it.preCondition()
                val post = it.postCondition()

                graph.addVertex(pre)
                graph.addVertex(post)
                graph.addEdge(pre, post, it)
            }

            println("CALLLLLLLLLLLLING")
            println(graph.containsVertex(currentState))

            val path = DijkstraShortestPath.findPathBetween(graph, currentState, goalState)

            return Plan(path?.edgeList ?: emptyList())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Plan(emptyList())
    }
}