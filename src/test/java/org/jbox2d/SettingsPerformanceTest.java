/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.jbox2d;

import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.worlds.PerformanceTestWorld;
import org.jbox2d.worlds.PistonWorld;

public class SettingsPerformanceTest extends BasicPerformanceTest {

    private static int NUM_TESTS = 14;
    private PerformanceTestWorld world;

    public SettingsPerformanceTest(int iters, PerformanceTestWorld world) {
        super(NUM_TESTS, iters, 300);
        this.world = world;
    }

    public static void main(String[] args) {
        SettingsPerformanceTest benchmark = new SettingsPerformanceTest(10, new PistonWorld());
        benchmark.go();
    }

    @Override
    public void setupTest(int testNum) {
        World w = new World(new Vec2(0, -10));
        world.setupWorld(w);
    }

    @Override
    public void preStep(int testNum) {
        Settings.FAST_ABS = testNum == 1;
        Settings.FAST_ATAN2 = testNum == 2;
        Settings.FAST_CEIL = testNum == 3;
        Settings.FAST_FLOOR = testNum == 4;
        Settings.FAST_ROUND = testNum == 5;
        Settings.SINCOS_LUT_ENABLED = testNum == 6;

        if (testNum == 7) {
            Settings.FAST_ABS = true;
            Settings.FAST_ATAN2 = true;
            Settings.FAST_CEIL = true;
            Settings.FAST_FLOOR = true;
            Settings.FAST_ROUND = true;
            Settings.SINCOS_LUT_ENABLED = true;
        }

        if (testNum > 7) {
            Settings.FAST_ABS = testNum != 8;
            Settings.FAST_ATAN2 = testNum != 9;
            Settings.FAST_CEIL = testNum != 10;
            Settings.FAST_FLOOR = testNum != 11;
            Settings.FAST_ROUND = testNum != 12;
            Settings.SINCOS_LUT_ENABLED = testNum != 13;
        }
    }

    @Override
    public void step(int testNum) {
        world.step();
    }

    @Override
    public String getTestName(int testNum) {
        switch (testNum) {
            case 0:
                return "No optimizations";
            case 1:
                return "Fast abs";
            case 2:
                return "Fast atan2";
            case 3:
                return "Fast ceil";
            case 4:
                return "Fast floor";
            case 5:
                return "Fast round";
            case 6:
                return "Sincos lookup table";
            case 7:
                return "All optimizations on";
            case 8:
                return "no Fast abs";
            case 9:
                return "no Fast atan2";
            case 10:
                return "no Fast ceil";
            case 11:
                return "no Fast floor";
            case 12:
                return "no Fast round";
            case 13:
                return "no Sincos lookup";
            default:
                return "";
        }
    }
}
