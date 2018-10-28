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

package com.almasb.fxgl.ai.utils;

/** A counting semaphore that does not block the thread when the requested resource is not available. No actual resource objects
 * are used; the semaphore just keeps a count of the number available and acts accordingly.
 * 
 * @author davebaol */
public interface NonBlockingSemaphore {

	/** Acquires a resource if available.
	 * <p>
	 * An invocation of this method yields exactly the same result as {@code acquire(1)}
	 * @return {@code true} if the resource has been acquired; {@code false} otherwise. */
	public boolean acquire();

	/** Acquires the specified number of resources if they all are available.
	 * @return {@code true} if all the requested resources have been acquired; {@code false} otherwise. */
	public boolean acquire(int resources);

	/** Releases a resource returning it to this semaphore.
	 * <p>
	 * An invocation of this method yields exactly the same result as {@code release(1)}
	 * @return {@code true} if the resource has been released; {@code false} otherwise. */
	public boolean release();

	/** Releases the specified number of resources returning it to this semaphore.
	 * @return {@code true} if all the requested resources have been released; {@code false} otherwise. */
	public boolean release(int resources);

	/** Abstract factory for creating concrete instances of classes implementing {@link NonBlockingSemaphore}.
	 * 
	 * @author davebaol */
	public interface Factory {

		/** Creates a semaphore with the specified name and resources.
		 * @param name the name of the semaphore
		 * @param maxResources the maximum number of resource
		 * @return the newly created semaphore. */
		public NonBlockingSemaphore createSemaphore(String name, int maxResources);
	}

}
