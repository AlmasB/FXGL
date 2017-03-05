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
public final class ConstantLongDistribution extends LongDistribution {

	public static final ConstantLongDistribution NEGATIVE_ONE = new ConstantLongDistribution(-1);
	public static final ConstantLongDistribution ZERO = new ConstantLongDistribution(0);
	public static final ConstantLongDistribution ONE = new ConstantLongDistribution(1);

	private final long value;

	public ConstantLongDistribution (long value) {
		this.value = value;
	}

	@Override
	public long nextLong () {
		return value;
	}

	public long getValue () {
		return value;
	}

}
