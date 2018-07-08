/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object SystemConfig {

    // STATIC - cannot be modified at runtime

    /**
     * where to look for latest stable project POM
     */
    val urlPOM = "https://raw.githubusercontent.com/AlmasB/FXGL/master/pom.xml"

    /**
     * project GitHub repo
     */
    val urlGithub = "https://github.com/AlmasB/FXGL"

    /**
     * link to Heroku leaderboard server
     */
    val urlLeaderboard = "http://fxgl-top.herokuapp.com/"

    /**
     * link to google forms feedback
     */
    val urlGoogleForms = "https://goo.gl/forms/6wrMnOBxTE1fEpOy2"

    /**
     * how often to check for updates
     */
    val versionCheckDays = 7

    /**
     * profiles are saved in this directory
     */
    val profileDir = "profiles/"

    /**
     * profile data is saved as this file
     */
    val profileName = "user.profile"

    /**
     * save files are saved in this directory
     */
    val saveDir = "saves/"

    val saveFileExt = ".sav"

    val dataFileExt = ".dat"

    // DYNAMIC - can be modified at runtime

    val devBBoxColor = SimpleObjectProperty<Color>(Color.web("#ff0000"))
    val devShowBBox = SimpleBooleanProperty(false)
    val devShowPosition = SimpleBooleanProperty(false)
}