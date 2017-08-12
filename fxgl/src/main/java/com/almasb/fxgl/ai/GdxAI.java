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

package com.almasb.fxgl.ai;

import com.almasb.fxgl.core.logging.Logger;

/** Environment class holding references to the {@link Timepiece}, {@link Logger} instances. The references
 * are held in static fields which allows static access to all sub systems.
 * <p>
 * Basically, this class is the locator of the service locator design pattern. The locator contains references to the services and
 * encapsulates the logic that locates them. Being a decoupling pattern, the service locator provides a global point of access to
 * a set of services without coupling users to the concrete classes that implement them.
 * <p>
 * The gdx-ai framework internally uses the service locator to give you the ability to use the framework out of a libgdx
 * application. In this scenario, the libgdx jar must still be in the classpath but you don't need native libraries since the
 * libgdx environment is not initialized at all.
 * <p>
 * Also, this service locator automatically configures itself with proper service providers in the situations below:
 * <ul>
 * <li>Libgdx application: if a running libgdx environment (regardless of the particular backend) is detected.</li>
 * <li>Non-libgdx desktop application: if no running libgdx environment is found.</li>
 * </ul>
 * This means that if you want to use gdx-ai in Android (or any other non desktop platform) out of a lbgdx application then you
 * have to implement and set proper providers.
 * 
 * @author davebaol */
public final class GdxAI {

	private GdxAI () {
	}

	private static Timepiece timepiece = new DefaultTimepiece();

	/** Returns the timepiece service. */
	public static Timepiece getTimepiece () {
		return timepiece;
	}

	/** Sets the timepiece service. */
	public static void setTimepiece (Timepiece timepiece) {
		GdxAI.timepiece = timepiece;
	}

	/**
	 * @return logger service
	 */
	public static Logger getLogger() {
		return Logger.get(GdxAI.class);
	}
}
