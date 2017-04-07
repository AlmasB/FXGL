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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jbox2d.common.JBoxUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Murphy
 */
public abstract class BasicPerformanceTest {
    public enum ResultFormat {
        MILLISECONDS(1000000, "Milliseconds"), MICROSECONDS(1000, "Microseconds"), NANOSECONDS(1,
                "Nanoseconds");

        private final int divisor;
        private final String name;

        ResultFormat(int divisor, String name) {
            assert (divisor != 0);
            this.divisor = divisor;
            this.name = name;
        }
    }

    private ResultFormat format = ResultFormat.MICROSECONDS;
    private final int numTests, iters, frames;
    protected final DescriptiveStatistics[] stats;
    private ArrayList<Integer> testOrder = new ArrayList<>();

    public BasicPerformanceTest(int numTests, int iters, int frames) {
        this.numTests = numTests;
        this.iters = iters;
        this.frames = frames;
        stats = new DescriptiveStatistics[numTests];
        for (int i = 0; i < numTests; i++) {
            stats[i] = new DescriptiveStatistics(iters * frames + 1);
            testOrder.add(i);
        }
    }

    public void setFormat(ResultFormat format) {
        this.format = format;
    }

    public void go() {
        long prev, after;
        // warmup
        println("Warmup");
        int warmupIters = iters / 10;
        for (int i = 0; i < warmupIters; i++) {
            println(i * 100.0 / warmupIters + "%");
            Collections.shuffle(testOrder);
            for (int test = 0; test < numTests; test++) {
                setupTest(test);
            }
            for (int j = 0; j < frames; j++) {
                Collections.shuffle(testOrder);
                for (int test = 0; test < numTests; test++) {
                    int runningTest = testOrder.get(test);
                    preStep(runningTest);
                    step(runningTest);
                }
            }
        }
        println("Testing");
        for (int i = 0; i < iters; i++) {
            println(i * 100.0 / iters + "%");
            for (int test = 0; test < numTests; test++) {
                setupTest(test);
            }
            for (int j = 0; j < frames; j++) {
                Collections.shuffle(testOrder);
                for (int test = 0; test < numTests; test++) {
                    int runningTest = testOrder.get(test);
                    preStep(runningTest);
                    prev = System.nanoTime();
                    step(runningTest);
                    after = System.nanoTime();
                    stats[runningTest].addValue((after - prev));
                }
            }
        }
        printResults();
    }

    public void printResults() {
        printf("%-20s%20s%20s%20s\n", "Test Name", format.name + " Avg", "StdDev", "95% Interval");
        for (int i = 0; i < numTests; i++) {
            double mean = stats[i].getMean() / format.divisor;
            double stddev = stats[i].getStandardDeviation() / format.divisor;
            double diff = 1.96 * stddev / JBoxUtils.sqrt(stats[i].getN());
            printf("%-20s%20.3f%20.3f  (%7.3f,%7.3f)\n", getTestName(i), mean, stddev, mean - diff, mean
                    + diff);
        }
    }

    public void setupTest(int testNum) {
    }

    public void preStep(int testNum) {
    }

    public abstract void step(int testNum);

    public abstract String getTestName(int testNum);

    public int getFrames(int testNum) {
        return 0;
    }

    // override to change output
    public void println(String s) {
        System.out.println(s);
    }

    public void printf(String s, Object... args) {
        System.out.printf(s, args);
    }
}
