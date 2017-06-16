/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving;

/**
 * Allows services to save/load their state using a user profile.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface UserProfileSavable {

    /**
     * Called during profile save. Allows a class
     * to serialize its state to the given profile.
     *
     * @param profile the profile to save to
     */
    void save(UserProfile profile);

    /**
     * Called during profile load. Allows a class
     * load its state from the given frofile.
     *
     * @param profile the profile to load from
     */
    void load(UserProfile profile);
}
