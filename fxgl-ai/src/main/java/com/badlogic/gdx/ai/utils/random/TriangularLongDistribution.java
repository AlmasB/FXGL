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
public final class TriangularLongDistribution extends LongDistribution {

	private final long low;
	private final long high;
	private final double mode;

	public TriangularLongDistribution (long high) {
		this(-high, high);
	}

	public TriangularLongDistribution (long low, long high) {
		this(low, high, (low + high) * .5);
	}

	public TriangularLongDistribution (long low, long high, double mode) {
		this.low = low;
		this.high = high;
		this.mode = mode;
	}

	@Override
	public long nextLong () {
		double r;
		if (-low == high && mode == 0)
			r = TriangularDoubleDistribution.randomTriangular(high); // It's faster
		else
			r = TriangularDoubleDistribution.randomTriangular(low, high, mode);
		return Math.round(r);
	}

	public long getLow () {
		return low;
	}

	public long getHigh () {
		return high;
	}

	public double getMode () {
		return mode;
	}

}
