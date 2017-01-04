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

/** The {@code Logger} interface provides an abstraction over logging facilities.
 * @author davebaol */
public interface Logger {

	/** Logs a debug message.
	 * @param tag used to identify the source of the log message.
	 * @param message the message to log. */
	public void debug(String tag, String message);

	/** Logs a debug message with the specified exception.
	 * @param tag used to identify the source of the log message.
	 * @param message the message to log.
	 * @param exception the exception to log. */
	public void debug(String tag, String message, Throwable exception);

	/** Logs an info message.
	 * @param tag used to identify the source of the log message.
	 * @param message the message to log. */
	public void info(String tag, String message);

	/** Logs an info message with the specified exception.
	 * @param tag used to identify the source of the log message.
	 * @param message the message to log.
	 * @param exception the exception to log. */
	public void info(String tag, String message, Throwable exception);

	/** Logs an error message.
	 * @param tag used to identify the source of the log message.
	 * @param message the message to log. */
	public void error(String tag, String message);

	/** Logs an error message with the specified exception.
	 * @param tag used to identify the source of the log message.
	 * @param message the message to log.
	 * @param exception the exception to log. */
	public void error(String tag, String message, Throwable exception);

}
