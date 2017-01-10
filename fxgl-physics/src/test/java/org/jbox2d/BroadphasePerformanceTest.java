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

package org.jbox2d;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.collision.broadphase.DynamicTreeFlatNodes;
import org.jbox2d.dynamics.World;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.pooling.normal.DefaultWorldPool;
import org.jbox2d.worlds.PerformanceTestWorld;
import org.jbox2d.worlds.PistonWorld;

public class BroadphasePerformanceTest extends BasicPerformanceTest {

    private static int NUM_TESTS = 2;
    private PerformanceTestWorld world;

    public BroadphasePerformanceTest(int iters, PerformanceTestWorld world) {
        super(NUM_TESTS, iters, 1000);
        this.world = world;
        setFormat(ResultFormat.MILLISECONDS);
    }

    public static void main(String[] args) {
        BroadphasePerformanceTest benchmark = new BroadphasePerformanceTest(10, new PistonWorld());
        benchmark.go();
    }

    public void setupTest(int testNum) {
        World w;
        IWorldPool pool = new DefaultWorldPool(50, 50);
        if (testNum == 0) {
            w = new World(new Vec2(0.0f, -10.0f), pool);
        } else {
            w = new World(new Vec2(0, -10), pool, new DynamicTreeFlatNodes());
        }
        world.setupWorld(w);
    }

    @Override
    public void step(int testNum) {
        world.step();
    }

    @Override
    public String getTestName(int testNum) {
        switch (testNum) {
            case 0:
                return "Normal";
            case 1:
                return "Flat";
            default:
                return "";
        }
    }
}
