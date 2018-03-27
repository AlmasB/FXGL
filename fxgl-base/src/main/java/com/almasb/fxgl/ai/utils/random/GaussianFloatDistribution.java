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
public final class GaussianFloatDistribution extends FloatDistribution {

	public static final GaussianFloatDistribution STANDARD_NORMAL = new GaussianFloatDistribution(0, 1);

	private final float mean;
	private final float standardDeviation;

	public GaussianFloatDistribution (float mean, float standardDeviation) {
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	@Override
	public float nextFloat () {
		return mean + (float) FXGLMath.getRandom().nextGaussian() * standardDeviation;
	}

	public float getMean () {
		return mean;
	}

	public float getStandardDeviation () {
		return standardDeviation;
	}

}
