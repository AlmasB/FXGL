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

package com.badlogic.gdx.ai.msg;

/** Any object implementing the {@code Telegraph} interface can act as the sender or the receiver of a {@link Telegram}.
 * @author davebaol */
public interface Telegraph {

	/** Handles the telegram just received.
	 * @param msg The telegram
	 * @return {@code true} if the telegram has been successfully handled; {@code false} otherwise. */
	public boolean handleMessage (Telegram msg);

}
