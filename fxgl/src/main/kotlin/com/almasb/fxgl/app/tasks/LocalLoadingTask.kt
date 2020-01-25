/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.tasks

import com.almasb.fxgl.core.EngineTask
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.localization.Language
import com.almasb.fxgl.localization.LocalizationService
import com.almasb.fxgl.ui.FontType
import com.almasb.fxgl.ui.UIFactoryService
import com.almasb.sslogger.Logger
import javafx.beans.property.ObjectProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LocalLoadingTask : EngineTask() {

    private val log = Logger.get(javaClass)

    @Inject("language")
    private lateinit var language: ObjectProperty<Language>

    private lateinit var local: LocalizationService
    private lateinit var uiFactoryService: UIFactoryService

    override fun onInit() {
        initAndLoadLocalization()
        initAndRegisterFontFactories()

        // TODO: refactor
        IOTask.setDefaultExecutor(executor)
        IOTask.setDefaultFailAction { display.showErrorBox(it) }
    }

    private fun initAndLoadLocalization() {
        log.debug("Loading localizations")

        Language.builtInLanguages.forEach {
            local.addLanguageData(it, assetLoader.loadResourceBundle("languages/${it.name.toLowerCase()}.properties"))
        }

        local.selectedLanguageProperty().bind(language)
    }

    private fun initAndRegisterFontFactories() {
        log.debug("Registering font factories with UI factory")

        val uiFactory = uiFactoryService

        uiFactory.registerFontFactory(FontType.UI, assetLoader.loadFont(settings.fontUI))
        uiFactory.registerFontFactory(FontType.GAME, assetLoader.loadFont(settings.fontGame))
        uiFactory.registerFontFactory(FontType.MONO, assetLoader.loadFont(settings.fontMono))
        uiFactory.registerFontFactory(FontType.TEXT, assetLoader.loadFont(settings.fontText))
    }
}