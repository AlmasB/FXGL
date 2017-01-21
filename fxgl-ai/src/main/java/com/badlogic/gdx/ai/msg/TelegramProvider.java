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

/** Telegram providers respond to {@link MessageDispatcher#addListener} by providing optional {@link Telegram#extraInfo} to be sent
 * in a Telegram of a given type to the newly registered {@link Telegraph}.
 * @author avianey */
public interface TelegramProvider {
	/** Provides {@link Telegram#extraInfo} to dispatch immediately when a {@link Telegraph} is registered for the given message
	 * type.
	 * @param msg the message type to provide
	 * @param receiver the newly registered Telegraph. Providers can provide different info depending on the targeted Telegraph.
	 * @return extra info to dispatch in a Telegram or null if nothing to dispatch
	 * @see com.badlogic.gdx.ai.msg.MessageDispatcher#addListener(Telegraph, int)
	 * @see com.badlogic.gdx.ai.msg.MessageDispatcher#addListeners(Telegraph, int...) */
	Object provideMessageInfo (int msg, Telegraph receiver);
}
