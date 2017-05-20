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

/** @author davebaol */
public final class TriangularDoubleDistribution extends DoubleDistribution {

	private final double low;
	private final double high;
	private final double mode;

	public TriangularDoubleDistribution (double high) {
		this(-high, high);
	}

	public TriangularDoubleDistribution (double low, double high) {
		this(low, high, (low + high) * .5);
	}

	public TriangularDoubleDistribution (double low, double high, double mode) {
		this.low = low;
		this.high = high;
		this.mode = mode;
	}

	@Override
	public double nextDouble () {
		if (-low == high && mode == 0) return randomTriangular(high); // It's faster
		return randomTriangular(low, high, mode);
	}

	public double getLow () {
		return low;
	}

	public double getHigh () {
		return high;
	}

	public double getMode () {
		return mode;
	}

	/** Returns a triangularly distributed random number between {@code -high} (exclusive) and {@code high} (exclusive), where values
	 * around zero are more likely.
	 * <p>
	 * This is an optimized version of {@link #randomTriangular(float, float, float) randomTriangular(-high, high, 0)}
	 * @param high the upper limit */
	static double randomTriangular (double high) {
		return FXGLMath.randomTriangular() * high;
	}

	/** Returns a triangularly distributed random number between {@code low} (inclusive) and {@code high} (exclusive), where values
	 * around {@code mode} are more likely.
	 * @param low the lower limit
	 * @param high the upper limit
	 * @param mode the point around which the values are more likely */
	static double randomTriangular (double low, double high, double mode) {
		double u = FXGLMath.random();
		double d = high - low;
		if (u <= (mode - low) / d) return low + Math.sqrt(u * d * (mode - low));
		return high - Math.sqrt((1 - u) * d * (high - mode));
	}

}
