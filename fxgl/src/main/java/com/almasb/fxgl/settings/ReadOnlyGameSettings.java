/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.settings;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.util.Credits;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

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
    protected boolean fullScreen = false;
    protected boolean introEnabled = true;
    protected boolean menuEnabled = true;
    protected boolean profilingEnabled = true;
    protected boolean closeConfirmation = true;
    protected ApplicationMode appMode = ApplicationMode.DEVELOPER;
    protected KeyCode menuKey = KeyCode.ESCAPE;
    protected Credits credits = new Credits(Collections.emptyList());
    protected List<ServiceType<?> > services = new ArrayList<>();
    protected EnumSet<MenuItem> enabledMenuItems = EnumSet.noneOf(MenuItem.class);

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
        this.fullScreen = copy.fullScreen;
        this.introEnabled = copy.introEnabled;
        this.menuEnabled = copy.menuEnabled;
        this.profilingEnabled = copy.profilingEnabled;
        this.closeConfirmation = copy.closeConfirmation;
        this.appMode = copy.appMode;
        this.menuKey = copy.menuKey;
        this.credits = new Credits(copy.credits);
        this.services = copy.services;
        this.enabledMenuItems = copy.enabledMenuItems;
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

    public final boolean isFullScreen() {
        return fullScreen;
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

    public final boolean isCloseConfirmation() {
        return closeConfirmation;
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

    public final List<ServiceType<?>> getServices() {
        return services;
    }

    public final EnumSet<MenuItem> getEnabledMenuItems() {
        return enabledMenuItems;
    }

    @Override
    public String toString() {
        return "Title: " + title + '\n' +
                "Version: " + version + '\n' +
                "Width: " + width + '\n' +
                "Height: " + height + '\n' +
                "Fullscreen: " + fullScreen + '\n' +
                "Intro: " + introEnabled + '\n' +
                "Menus: " + menuEnabled + '\n' +
                "Profiling: " + profilingEnabled + '\n' +
                "App Mode: " + appMode + '\n' +
                "Menu Key: " + menuKey + '\n' +
                "Services: " + services;
    }
}
