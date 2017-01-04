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

package com.badlogic.gdx.ai.btree;

/** A {@code TaskCloneException} is thrown when an exception occurs during task cloning. See {@link Task#cloneTask()}
 * 
 * @author davebaol */
@SuppressWarnings("serial")
public class TaskCloneException extends RuntimeException {

	/** Constructs a new {@code TaskCloneException} with null as its detail message. The cause is not initialized, and may
	 * subsequently be initialized by a call to {@link #initCause(Throwable)}. */
	public TaskCloneException () {
		super();
	}

	/** Constructs a new {@code TaskCloneException} with the specified detail message. The cause is not initialized, and may
	 * subsequently be initialized by a call to {@link #initCause(Throwable)}.
	 * @param message the detail message which is saved for later retrieval by the {@link #getMessage()} method. */
	public TaskCloneException (String message) {
		super(message);
	}

	/** Constructs a new {@code TaskCloneException} with the specified cause and a detail message of (
	 * {@code cause==null ? null : cause.toString()}) (which typically contains the class and detail message of cause).
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). A {@code null} value is
	 *           permitted, and indicates that the cause is nonexistent or unknown. */
	public TaskCloneException (Throwable cause) {
		super(cause);
	}

	/** Constructs a new {@code TaskCloneException} with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is not automatically incorporated in this runtime exception's
	 * detail message.
	 * @param message the detail message which is saved for later retrieval by the {@link #getMessage()} method.
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). A {@code null} value is
	 *           permitted, and indicates that the cause is nonexistent or unknown. */
	public TaskCloneException (String message, Throwable cause) {
		super(message, cause);
	}

}
