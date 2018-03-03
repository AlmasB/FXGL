/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.settings;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.ExceptionHandler;
import com.almasb.fxgl.app.FXGLExceptionHandler;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.gameplay.notification.NotificationView;
import com.almasb.fxgl.gameplay.notification.XboxNotificationView;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.ui.DialogFactory;
import com.almasb.fxgl.ui.FXGLDialogFactory;
import com.almasb.fxgl.ui.FXGLUIFactory;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.Credits;
import javafx.scene.input.KeyCode;
import javafx.stage.StageStyle;

import java.util.Collections;
import java.util.EnumSet;

/**
 * A copy of GameSettings with public getters only.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ReadOnlyGameSettings {

    protected String title = "Untitled";
    protected String version = "0.0";
    protected int width = 800;
    protected int height = 600;
    protected boolean fullScreenAllowed = false;
    protected boolean introEnabled = false;
    protected boolean menuEnabled = false;
    protected boolean profilingEnabled = false;
    protected boolean closeConfirmation = false;
    protected boolean singleStep = false;
    protected ApplicationMode appMode = ApplicationMode.DEVELOPER;
    protected KeyCode menuKey = KeyCode.ESCAPE;
    protected Credits credits = new Credits(Collections.emptyList());
    protected EnumSet<MenuItem> enabledMenuItems = EnumSet.noneOf(MenuItem.class);
    protected StageStyle stageStyle = StageStyle.DECORATED;
    protected boolean manualResizeEnabled = false;

    /* CUSTOMIZABLE SERVICES BELOW */

    protected SceneFactory sceneFactory = new SceneFactory();
    protected DialogFactory dialogFactory = new FXGLDialogFactory();
    protected UIFactory uiFactory = new FXGLUIFactory();
    protected Class<? extends NotificationView> notificationViewClass = XboxNotificationView.class;
    protected ExceptionHandler exceptionHandler = new FXGLExceptionHandler();

    private ExceptionHandler exceptionHandlerWrapper = new ExceptionHandler() {
        private Logger log = Logger.get("ExceptionHandler");

        @Override
        public void handle(Throwable e) {
            log.warning("Caught Exception: ", e);
            exceptionHandler.handle(e);
        }
    };

    // when adding extra fields, remember to add them to copy constructor

    /**
     * Constructs game settings with default parameters.
     */
    ReadOnlyGameSettings() {
    }

    /**
     * Constructs new game settings with parameters
     * copied from given.
     *
     * @param copy game settings to copy from
     */
    ReadOnlyGameSettings(ReadOnlyGameSettings copy) {
        this.title = copy.title;
        this.version = copy.version;
        this.width = copy.width;
        this.height = copy.height;
        this.fullScreenAllowed = copy.fullScreenAllowed;
        this.introEnabled = copy.introEnabled;
        this.menuEnabled = copy.menuEnabled;
        this.profilingEnabled = copy.profilingEnabled;
        this.closeConfirmation = copy.closeConfirmation;
        this.appMode = copy.appMode;
        this.menuKey = copy.menuKey;
        this.credits = new Credits(copy.credits);
        this.enabledMenuItems = copy.enabledMenuItems;
        this.singleStep = copy.singleStep;
        this.stageStyle = copy.stageStyle;
        this.manualResizeEnabled = copy.manualResizeEnabled;

        this.sceneFactory = copy.sceneFactory;
        this.dialogFactory = copy.dialogFactory;
        this.uiFactory = copy.uiFactory;
        this.notificationViewClass = copy.notificationViewClass;
        this.exceptionHandler = copy.exceptionHandler;

        this.exceptionHandlerWrapper = copy.exceptionHandlerWrapper;
    }

    public final String getTitle() {
        return title;
    }

    public final String getVersion() {
        return version;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final boolean isFullScreenAllowed() {
        return fullScreenAllowed;
    }

    public final boolean isIntroEnabled() {
        return introEnabled;
    }

    public final boolean isMenuEnabled() {
        return menuEnabled;
    }

    public final boolean isProfilingEnabled() {
        return profilingEnabled;
    }

    public final boolean isSingleStep() {
        return singleStep;
    }

    public final boolean isCloseConfirmation() {
        return closeConfirmation;
    }

    public final boolean isManualResizeEnabled() {
        return manualResizeEnabled;
    }

    public final ApplicationMode getApplicationMode() {
        return appMode;
    }

    public final KeyCode getMenuKey() {
        return menuKey;
    }

    public final Credits getCredits() {
        return credits;
    }

    public final EnumSet<MenuItem> getEnabledMenuItems() {
        return enabledMenuItems;
    }

    public final StageStyle getStageStyle() {
        return stageStyle;
    }

    public final SceneFactory getSceneFactory() {
        return sceneFactory;
    }

    public final DialogFactory getDialogFactory() {
        return dialogFactory;
    }

    public final UIFactory getUIFactory() {
        return uiFactory;
    }

    public final Class<? extends NotificationView> getNotificationViewFactory() {
        return notificationViewClass;
    }

    public final ExceptionHandler getExceptionHandler() {
        return exceptionHandlerWrapper;
    }

    @Override
    public String toString() {
        return "Title: " + title + '\n' +
                "Version: " + version + '\n' +
                "Width: " + width + '\n' +
                "Height: " + height + '\n' +
                "Fullscreen: " + fullScreenAllowed + '\n' +
                "Intro: " + introEnabled + '\n' +
                "Menus: " + menuEnabled + '\n' +
                "Profiling: " + profilingEnabled + '\n' +
                "Single step:" + singleStep + '\n' +
                "App Mode: " + appMode + '\n' +
                "Menu Key: " + menuKey + '\n' +
                "Stage Style: " + stageStyle + '\n' +
                "Scene Factory: " + sceneFactory.getClass() + '\n' +
                "Dialog Factory: " + dialogFactory.getClass() + '\n' +
                "UI Factory: " + uiFactory.getClass() + '\n' +
                "Notification Service: " + notificationViewClass + '\n' +
                "Exception Handler: " + exceptionHandler.getClass();
    }
}
