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

package com.badlogic.gdx.ai;

/** @author davebaol */
public class DefaultTimepiece implements Timepiece {

	private float time;
	private float deltaTime;
	private float maxDeltaTime;

	public DefaultTimepiece () {
		this(Float.POSITIVE_INFINITY);
	}

	public DefaultTimepiece (float maxDeltaTime) {
		this.time = 0f;
		this.deltaTime = 0f;
		this.maxDeltaTime = maxDeltaTime;
	}

	@Override
	public float getTime () {
		return time;
	}

	@Override
	public float getDeltaTime () {
		return deltaTime;
	}

	@Override
	public void update (float deltaTime) {
		this.deltaTime = (deltaTime > maxDeltaTime ? maxDeltaTime : deltaTime);
		this.time += this.deltaTime;
	}

}
