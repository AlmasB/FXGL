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
public final class UniformLongDistribution extends LongDistribution {

	private final long low;
	private final long high;

	public UniformLongDistribution (long high) {
		this(0, high);
	}

	public UniformLongDistribution (long low, long high) {
		this.low = low;
		this.high = high;
	}

	@Override
	public long nextLong () {
		return low + (long)(FXGLMath.getRandom().nextDouble() * (high - low));
	}

	public long getLow () {
		return low;
	}

	public long getHigh () {
		return high;
	}

}
