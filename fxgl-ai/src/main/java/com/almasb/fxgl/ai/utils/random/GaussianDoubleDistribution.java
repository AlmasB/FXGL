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
public final class GaussianDoubleDistribution extends DoubleDistribution {

	public static final GaussianDoubleDistribution STANDARD_NORMAL = new GaussianDoubleDistribution(0, 1);

	private final double mean;
	private final double standardDeviation;

	public GaussianDoubleDistribution (double mean, double standardDeviation) {
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	@Override
	public double nextDouble () {
		return mean + FXGLMath.getRandom().nextGaussian() * standardDeviation;
	}

	public double getMean () {
		return mean;
	}

	public double getStandardDeviation () {
		return standardDeviation;
	}

}
