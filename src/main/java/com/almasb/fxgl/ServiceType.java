/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl;

import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.audio.FXGLAudioPlayer;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.event.FXGLEventBus;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.scene.Display;
import com.almasb.fxgl.time.FXGLLocalTimer;
import com.almasb.fxgl.time.FXGLMasterTimer;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.MasterTimer;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface ServiceType<T> {
    Class<T> service();
    Class<? extends T> serviceProvider();

    ServiceType<AudioPlayer> AUDIO_PLAYER = new ServiceType<AudioPlayer>() {
        @Override
        public Class<AudioPlayer> service() {
            return AudioPlayer.class;
        }

        @Override
        public Class<? extends AudioPlayer> serviceProvider() {
            return FXGLAudioPlayer.class;
        }
    };

    ServiceType<AssetLoader> ASSET_LOADER = new ServiceType<AssetLoader>() {
        @Override
        public Class<AssetLoader> service() {
            return AssetLoader.class;
        }

        @Override
        public Class<? extends AssetLoader> serviceProvider() {
            return AssetLoader.class;
        }
    };

    ServiceType<LocalTimer> LOCAL_TIMER = new ServiceType<LocalTimer>() {
        @Override
        public Class<LocalTimer> service() {
            return LocalTimer.class;
        }

        @Override
        public Class<? extends LocalTimer> serviceProvider() {
            return FXGLLocalTimer.class;
        }
    };

    ServiceType<MasterTimer> MASTER_TIMER = new ServiceType<MasterTimer>() {
        @Override
        public Class<MasterTimer> service() {
            return MasterTimer.class;
        }

        @Override
        public Class<? extends MasterTimer> serviceProvider() {
            return FXGLMasterTimer.class;
        }
    };

    ServiceType<EventBus> EVENT_BUS = new ServiceType<EventBus>() {
        @Override
        public Class<EventBus> service() {
            return EventBus.class;
        }

        @Override
        public Class<? extends EventBus> serviceProvider() {
            return FXGLEventBus.class;
        }
    };

    ServiceType<Input> INPUT = new ServiceType<Input>() {
        @Override
        public Class<Input> service() {
            return Input.class;
        }

        @Override
        public Class<? extends Input> serviceProvider() {
            return Input.class;
        }
    };

    ServiceType<Display> DISPLAY = new ServiceType<Display>() {
        @Override
        public Class<Display> service() {
            return Display.class;
        }

        @Override
        public Class<? extends Display> serviceProvider() {
            return Display.class;
        }
    };
}
