/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.asset.AssetLoader
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.name.Names
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

/**
 * Represents the entire FXGL infrastructure.
 * Can be used to pass internal properties (key-value pair) around.
 * Can be used for communication between non-related parts.
 * Not to be abused.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGL {

    companion object {
        private lateinit var internalSettings: ReadOnlyGameSettings

        private var initGuard = false

        @JvmStatic fun getSettings() = internalSettings

        @JvmStatic fun configure(gameSettings: ReadOnlyGameSettings, stage: Stage, app: FXGLApplication) {
            if (initGuard)
                throw IllegalStateException("FXGL is already configured")

            initGuard = true

            internalSettings = gameSettings
            configureServices(stage)

            injector.injectMembers(app)
        }

        /**
         * Dependency injector.
         */
        private lateinit var injector: Injector

        /**
         * Obtain an instance of a service.
         * It may be expensive to use this in a loop.
         * Store a reference to the instance instead.
         *
         * @param type service type
         *
         * @param  type
         *
         * @return service
         */
        @JvmStatic fun <T> getService(type: ServiceType<T>) = injector.getInstance(type.service())

        private fun configureServices(stage: Stage) {
            injector = Guice.createInjector(object : ServicesModule() {
                private val scene = Scene(Pane())

                // TODO: autobind property names from .properties
                override fun configure() {
                    bind(Double::class.java)
                            .annotatedWith(Names.named("appWidth"))
                            .toInstance(internalSettings.getWidth().toDouble())

                    bind(Double::class.java)
                            .annotatedWith(Names.named("appHeight"))
                            .toInstance(internalSettings.getHeight().toDouble())

                    bind(Int::class.java)
                            .annotatedWith(Names.named("asset.cache.size"))
                            .toInstance(getInt("asset.cache.size"))

                    bind(ReadOnlyGameSettings::class.java).toInstance(internalSettings)
                    bind(ApplicationMode::class.java).toInstance(internalSettings.getApplicationMode())

                    // this actually configures services (written in java due to kotlin's confusion over "to")
                    super.configure()
                }

                @Provides
                internal fun primaryScene(): Scene {
                    return scene
                }

                @Provides
                internal fun primaryStage(): Stage {
                    return stage
                }
            })
        }

        /* CONVENIENCE FUNCTIONS */
        // TODO: cache those into fields and return them instead

        @JvmStatic fun getLogger(name: String) = getService(ServiceType.LOGGER_FACTORY).newLogger(name)
        @JvmStatic fun getLogger(caller: Class<*>) = getService(ServiceType.LOGGER_FACTORY).newLogger(caller)

        private val _assetLoader: AssetLoader by lazy { getService(ServiceType.ASSET_LOADER) }
        @JvmStatic fun getAssetLoader() = _assetLoader

        @JvmStatic fun getEventBus() = getService(ServiceType.EVENT_BUS)

        /**
         * Get value of an int property.

         * @param key property key
         * *
         * @return int value
         */
        @JvmStatic fun getInt(key: String) = Integer.parseInt(getProperty(key))

        /**
         * Get value of a double property.

         * @param key property key
         * *
         * @return double value
         */
        @JvmStatic fun getDouble(key: String) = java.lang.Double.parseDouble(getProperty(key))

        /**
         * Get value of a boolean property.

         * @param key property key
         * *
         * @return boolean value
         */
        @JvmStatic fun getBoolean(key: String) = java.lang.Boolean.parseBoolean(getProperty(key))

        /**
         * @param key property key
         * *
         * @return property value
         */
        @JvmStatic fun getProperty(key: String) = System.getProperty("FXGL.$key")
                ?: throw IllegalArgumentException("Key \"$key\" not found!")

        /**
         * Set an int, double, boolean or String property.
         *
         * @param key property key
         *
         * @param value property value
         */
        @JvmStatic fun setProperty(key: String, value: Any) {
            System.setProperty("FXGL.$key", value.toString())
        }
    }
}