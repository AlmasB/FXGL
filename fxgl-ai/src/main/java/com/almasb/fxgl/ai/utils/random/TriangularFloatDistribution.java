/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.almasb.fxgl.ai.utils.random;

import com.almasb.fxgl.core.math.FXGLMath;

/**
 * @author davebaol
 */
public final class TriangularFloatDistribution extends FloatDistribution {

    private final float low;
    private final float high;
    private final float mode;

    public TriangularFloatDistribution(float high) {
        this(-high, high);
    }

    public TriangularFloatDistribution(float low, float high) {
        this(low, high, (low + high) * .5f);
    }

    public TriangularFloatDistribution(float low, float high, float mode) {
        this.low = low;
        this.high = high;
        this.mode = mode;
    }

    @Override
    public float nextFloat() {
        if (-low == high && mode == 0)
            return (float) FXGLMath.randomTriangular(high); // It's faster
        else
            return (float) FXGLMath.randomTriangular(low, high, mode);
    }

    public float getLow() {
        return low;
    }

    public float getHigh() {
        return high;
    }

    public float getMode() {
        return mode;
    }

}
