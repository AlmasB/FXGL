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
public final class UniformDoubleDistribution extends DoubleDistribution {

	private final double low;
	private final double high;

	public UniformDoubleDistribution (double high) {
		this(0, high);
	}

	public UniformDoubleDistribution (double low, double high) {
		this.low = low;
		this.high = high;
	}

	public double nextDouble () {
		return low + FXGLMath.getRandom().nextDouble() * (high - low);
	}

	public double getLow () {
		return low;
	}

	public double getHigh () {
		return high;
	}

}
