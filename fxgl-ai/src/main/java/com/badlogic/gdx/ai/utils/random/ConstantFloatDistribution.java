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

package com.badlogic.gdx.ai.utils.random;

/** @author davebaol */
public final class ConstantFloatDistribution extends FloatDistribution {

	public static final ConstantFloatDistribution NEGATIVE_ONE = new ConstantFloatDistribution(-1);
	public static final ConstantFloatDistribution ZERO = new ConstantFloatDistribution(0);
	public static final ConstantFloatDistribution ONE = new ConstantFloatDistribution(1);
	public static final ConstantFloatDistribution ZERO_POINT_FIVE = new ConstantFloatDistribution(.5f);

	private final float value;

	public ConstantFloatDistribution (float value) {
		this.value = value;
	}

	@Override
	public float nextFloat () {
		return value;
	}

	public float getValue () {
		return value;
	}

}
