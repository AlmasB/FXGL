/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.service;

import com.almasb.fxgl.app.FXGLExceptionHandler;
import com.almasb.fxgl.devtools.profiling.Profiler;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.service.impl.quest.FXGLQuestServiceProvider;
import com.almasb.fxgl.logging.FXGLLoggerFactory;
import com.almasb.fxgl.logging.LoggerFactory;
import com.almasb.fxgl.service.impl.asset.FXGLAssetLoader;
import com.almasb.fxgl.service.impl.audio.FXGLAudioPlayer;
import com.almasb.fxgl.service.impl.display.FXGLDisplay;
import com.almasb.fxgl.service.impl.event.FXGLEventBus;
import com.almasb.fxgl.service.impl.executor.FXGLExecutor;
import com.almasb.fxgl.service.impl.input.FXGLInput;
import com.almasb.fxgl.service.impl.net.FXGLNet;
import com.almasb.fxgl.service.impl.notification.SlidingNotificationService;
import com.almasb.fxgl.service.impl.pooler.FXGLPooler;
import com.almasb.fxgl.service.impl.qte.QTEProvider;
import com.almasb.fxgl.service.impl.timer.FXGLMasterTimer;
import com.almasb.fxgl.service.impl.ui.FXGLUIFactory;
import com.almasb.fxgl.time.FXGLLocalTimer;
import com.almasb.fxgl.time.LocalTimer;
import com.google.inject.Scope;
import com.google.inject.Scopes;

/**
 * Marks a service type.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface ServiceType<T> {

    /**
     * @return service interface/class
     */
    Class<T> service();

    /**
     * @return service implementation/provider
     */
    Class<? extends T> serviceProvider();

    /**
     * @return service scope
     */
    default Scope scope() {
        return Scopes.SINGLETON;
    }

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
            return FXGLAssetLoader.class;
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

        @Override
        public Scope scope() {
            return Scopes.NO_SCOPE;
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
            return FXGLInput.class;
        }
    };

    ServiceType<Display> DISPLAY = new ServiceType<Display>() {
        @Override
        public Class<Display> service() {
            return Display.class;
        }

        @Override
        public Class<? extends Display> serviceProvider() {
            return FXGLDisplay.class;
        }
    };

    ServiceType<Executor> EXECUTOR = new ServiceType<Executor>() {
        @Override
        public Class<Executor> service() {
            return Executor.class;
        }

        @Override
        public Class<? extends Executor> serviceProvider() {
            return FXGLExecutor.class;
        }
    };

    ServiceType<NotificationService> NOTIFICATION_SERVICE = new ServiceType<NotificationService>() {
        @Override
        public Class<NotificationService> service() {
            return NotificationService.class;
        }

        @Override
        public Class<? extends NotificationService> serviceProvider() {
            return SlidingNotificationService.class;
        }
    };

    ServiceType<LoggerFactory> LOGGER_FACTORY = new ServiceType<LoggerFactory>() {
        @Override
        public Class<LoggerFactory> service() {
            return LoggerFactory.class;
        }

        @Override
        public Class<? extends LoggerFactory> serviceProvider() {
            return FXGLLoggerFactory.class;
        }
    };

    ServiceType<AchievementManager> ACHIEVEMENT_MANAGER = new ServiceType<AchievementManager>() {
        @Override
        public Class<AchievementManager> service() {
            return AchievementManager.class;
        }

        @Override
        public Class<? extends AchievementManager> serviceProvider() {
            return AchievementManager.class;
        }
    };

    ServiceType<Profiler> PROFILER = new ServiceType<Profiler>() {
        @Override
        public Class<Profiler> service() {
            return Profiler.class;
        }

        @Override
        public Class<? extends Profiler> serviceProvider() {
            return Profiler.class;
        }

        @Override
        public Scope scope() {
            return Scopes.NO_SCOPE;
        }
    };

    ServiceType<Net> NET = new ServiceType<Net>() {
        @Override
        public Class<Net> service() {
            return Net.class;
        }

        @Override
        public Class<? extends Net> serviceProvider() {
            return FXGLNet.class;
        }
    };

    ServiceType<QTE> QTE = new ServiceType<QTE>() {
        @Override
        public Class<QTE> service() {
            return QTE.class;
        }

        @Override
        public Class<? extends QTE> serviceProvider() {
            return QTEProvider.class;
        }
    };

    ServiceType<Pooler> POOLER = new ServiceType<Pooler>() {
        @Override
        public Class<Pooler> service() {
            return Pooler.class;
        }

        @Override
        public Class<? extends Pooler> serviceProvider() {
            return FXGLPooler.class;
        }
    };

    ServiceType<ExceptionHandler> EXCEPTION_HANDLER = new ServiceType<ExceptionHandler>() {
        @Override
        public Class<ExceptionHandler> service() {
            return ExceptionHandler.class;
        }

        @Override
        public Class<? extends ExceptionHandler> serviceProvider() {
            return FXGLExceptionHandler.class;
        }
    };

    ServiceType<UIFactory> UI_FACTORY = new ServiceType<UIFactory>() {
        @Override
        public Class<UIFactory> service() {
            return UIFactory.class;
        }

        @Override
        public Class<? extends UIFactory> serviceProvider() {
            return FXGLUIFactory.class;
        }
    };

    ServiceType<FXGLQuestServiceProvider> QUEST_MANAGER = new ServiceType<FXGLQuestServiceProvider>() {
        @Override
        public Class<FXGLQuestServiceProvider> service() {
            return FXGLQuestServiceProvider.class;
        }

        @Override
        public Class<? extends FXGLQuestServiceProvider> serviceProvider() {
            return FXGLQuestServiceProvider.class;
        }
    };
}
