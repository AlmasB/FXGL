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

package com.badlogic.gdx.ai.utils;

/** A non-blocking semaphore that does not ensure the atomicity of its operations, meaning that it's not tread-safe.
 * 
 * @author davebaol */
public class SimpleNonBlockingSemaphore implements NonBlockingSemaphore {

	String name;
	int maxResources;
	int acquiredResources;

	/** Creates a {@code SimpleNonBlockingSemaphore} with the given name and number of resources.
	 * @param name the name of this semaphore
	 * @param maxResources the number of resources */
	public SimpleNonBlockingSemaphore (String name, int maxResources) {
		this.name = name;
		this.maxResources = maxResources;
		this.acquiredResources = 0;
	}

	@Override
	public boolean acquire () {
		return acquire(1);
	}

	@Override
	public boolean acquire (int resources) {
		if (acquiredResources + resources <= maxResources) {
			acquiredResources += resources;
			// System.out.println("sem." + name + ": acquired = TRUE, acquiredResources = " + acquiredResources);
			return true;
		}
		// System.out.println("sem." + name + ": acquired = FALSE, acquiredResources = " + acquiredResources);
		return false;
	}

	@Override
	public boolean release () {
		return release(1);
	}

	@Override
	public boolean release (int resources) {
		if (acquiredResources - resources >= 0) {
			acquiredResources -= resources;
			// System.out.println("sem." + name + ": released = TRUE, acquiredResources = " + acquiredResources);
			return true;
		}
		// System.out.println("sem." + name + ": released = FALSE, acquiredResources = " + acquiredResources);
		return false;
	}

	/** A concrete factory that can create instances of {@link SimpleNonBlockingSemaphore}.
	 * 
	 * @author davebaol */
	public static class Factory implements NonBlockingSemaphore.Factory {

		public Factory () {
		}

		@Override
		public NonBlockingSemaphore createSemaphore (String name, int maxResources) {
			return new SimpleNonBlockingSemaphore(name, maxResources);
		}

	}

}
