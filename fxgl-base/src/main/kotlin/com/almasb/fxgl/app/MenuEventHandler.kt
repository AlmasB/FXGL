/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.app.FXGL.Companion.getLocalizedString
import com.almasb.fxgl.app.FXGL.Companion.localizedStringProperty
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.saving.*
import com.almasb.fxgl.scene.ProgressDialog
import com.almasb.fxgl.scene.menu.MenuEventListener
import com.almasb.fxgl.util.Consumer
import com.almasb.fxgl.util.InputPredicates
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import java.time.LocalDateTime

/**
 * Handles events that happen within menus.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MenuEventHandler(private val app: GameApplication) : MenuEventListener, EventHandler<KeyEvent> {

    private val log = Logger.get(javaClass)

    private lateinit var saveLoadManager: SaveLoadManager

    override fun getSaveLoadManager(): SaveLoadManager {
        return saveLoadManager
    }

    /**
     * Stores the default profile data. This is used to restore default settings.
     */
    private lateinit var defaultProfile: UserProfile

    /**
     * Stores current selected profile name for this game.
     */
    private val profileName = ReadOnlyStringWrapper("")

    fun isProfileSelected() = profileName.value.isNotEmpty()

    private val hasSaves = ReadOnlyBooleanWrapper(false)

    override fun hasSavesProperty(): ReadOnlyBooleanProperty {
        return hasSaves.readOnlyProperty
    }

    init {
        app.addExitListener {
            saveProfile()
        }
    }

    fun generateDefaultProfile() {
        log.debug("generateDefaultProfile()")

        defaultProfile = createProfile()
    }

    override fun onNewGame() {
        app.startNewGame()
    }

    override fun onContinue() {
        saveLoadManager
                .loadLastModifiedSaveFileTask()
                .then { saveLoadManager.loadTask(it) }
                .onSuccess { app.startLoadedGame(it) }
                .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.loading")+"..."))
    }

    override fun onResume() {
        app.stateMachine.startPlay()
    }

    private fun doSave(saveFileName: String) {
        val dataFile = app.saveState()
        val saveFile = SaveFile(saveFileName, LocalDateTime.now())

        saveLoadManager
                .saveTask(dataFile, saveFile)
                .onSuccess { hasSaves.value = true }
                .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.savingData")+": $saveFileName"))
    }

    override fun onSave() {
        app.display.showInputBoxWithCancel(FXGL.getLocalizedString("menu.enterSaveName"), InputPredicates.ALPHANUM, Consumer { saveFileName ->

            if (saveFileName.isEmpty())
                return@Consumer

            if (saveLoadManager.saveFileExists(saveFileName)) {
                app.display.showConfirmationBox(FXGL.getLocalizedString("menu.overwrite")+" [$saveFileName]?", { yes ->

                    if (yes)
                        doSave(saveFileName)
                })
            } else {
                doSave(saveFileName)
            }
        })
    }

    override fun onLoad(saveFile: SaveFile) {
        app.display.showConfirmationBox(FXGL.getLocalizedString("menu.loadSave")+" [${saveFile.name}]?\n"+FXGL.getLocalizedString("menu.unsavedProgress"), { yes ->

            if (yes) {
                saveLoadManager
                        .loadTask(saveFile)
                        .onSuccess { app.startLoadedGame(it) }
                        .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.loading")+": ${saveFile.name}"))
            }
        })
    }

    override fun onDelete(saveFile: SaveFile) {
        app.display.showConfirmationBox(FXGL.getLocalizedString("menu.deleteSave")+"[${saveFile.name}]?", { yes ->

            if (yes) {
                saveLoadManager
                        .deleteSaveFileTask(saveFile)
                        .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.deleting")+": ${saveFile.name}"))
            }
        })
    }

    override fun onLogout() {
        app.display.showConfirmationBox(FXGL.getLocalizedString("menu.logOut"), { yes ->

            if (yes) {
                saveProfile()
                showProfileDialog()
            }
        })
    }

    override fun onMultiplayer() {
        showMultiplayerDialog()
    }

    override fun onExit() {
        app.display.showConfirmationBox(getLocalizedString("dialog.exitGame"), { yes ->

            if (yes)
                app.exit()
        })
    }

    override fun onExitToMainMenu() {
        app.display.showConfirmationBox(FXGL.getLocalizedString("menu.exitMainMenu")+"\n"+FXGL.getLocalizedString("menu.unsavedProgress"), { yes ->

            if (yes) {
                app.stateMachine.startMainMenu()
            }
        })
    }

    /* MENU KEY HANDLER */

    private var canSwitchGameMenu = true

    private fun onMenuKey(pressed: Boolean) {
        if (!pressed) {
            canSwitchGameMenu = true
            return
        }

        if (canSwitchGameMenu) {
            // we only care if menu key was pressed in one of these states
            if (app.stateMachine.isInGameMenu()) {
                canSwitchGameMenu = false
                onResume()

            } else if (app.stateMachine.isInPlay()) {
                canSwitchGameMenu = false
                app.stateMachine.startGameMenu()

            }
        }
    }

    override fun handle(event: KeyEvent) {
        if (event.code == FXGL.getSettings().menuKey) {
            onMenuKey(event.eventType == KeyEvent.KEY_PRESSED)
        }
    }

    fun fixAspectRatio() {
        app.fixAspectRatio()
    }

    override fun profileNameProperty(): ReadOnlyStringProperty {
        return profileName.readOnlyProperty
    }

    /**
     * @return user profile with current settings
     */
    fun createProfile(): UserProfile {
        log.debug("Creating default profile")

        val profile = UserProfile(app.settings.title, app.settings.version)

        app.eventBus.fireEvent(SaveEvent(profile))

        return profile
    }

    /**
     * @return true if loaded successfully, false if couldn't load
     */
    fun loadFromProfile(profile: UserProfile): Boolean {
        if (!profile.isCompatible(app.settings.title, app.settings.version))
            return false

        app.eventBus.fireEvent(LoadEvent(LoadEvent.LOAD_PROFILE, profile))
        return true
    }

    /**
     * Restores default settings, e.g. audio, video, controls.
     */
    override fun restoreDefaultSettings() {
        log.debug("restoreDefaultSettings()")

        app.eventBus.fireEvent(LoadEvent(LoadEvent.RESTORE_SETTINGS, defaultProfile))
    }

    fun saveProfile() {
        // if it is empty then we are running without menus
        if (!profileName.get().isEmpty()) {
            saveLoadManager.saveProfileTask(createProfile())
                    .onFailure { error -> "Failed to save profile: ${profileName.value} - $error" }
                    .run() // we execute synchronously to avoid incomplete save since we might be shutting down
        }
    }

    /* DIALOGS */

    private fun showMultiplayerDialog() {
        val btnHost = FXGL.getUIFactory().newButton(FXGL.localizedStringProperty("multiplayer.host"))
        btnHost.setOnAction {
            FXGL.getNet()
                    .hostMultiplayerTask()
                    .onSuccess { onNewGame() }
                    .onFailure { app.display.showErrorBox(it) }
                    .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("multiplayer.hosting")))
        }

        val btnConnect = FXGL.getUIFactory().newButton(FXGL.localizedStringProperty("multiplayer.connect"))
        btnConnect.setOnAction {
            app.display.showInputBox(FXGL.getLocalizedString("multiplayer.enterIp"), {
                FXGL.getNet()
                        .connectMultiplayerTask(it)
                        .onSuccess { onNewGame() }
                        .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("multiplayer.connecting")))
            })
        }

        app.display.showBox(FXGL.getLocalizedString("multiplayer.options"), FXGL.getUIFactory().newText(""), btnHost, btnConnect)
    }

    /**
     * Show profile dialog so that user selects existing or creates new profile.
     * The dialog is only dismissed when profile is chosen either way.
     */
    fun showProfileDialog() {
        val profilesBox = FXGL.getUIFactory().newChoiceBox(FXCollections.observableArrayList<String>())

        val btnNew = FXGL.getUIFactory().newButton(FXGL.localizedStringProperty("multiplayer.new"))
        val btnSelect = FXGL.getUIFactory().newButton(FXGL.localizedStringProperty("multiplayer.select"))
        btnSelect.disableProperty().bind(profilesBox.valueProperty().isNull)
        val btnDelete = FXGL.getUIFactory().newButton(FXGL.localizedStringProperty("menu.delete"))
        btnDelete.disableProperty().bind(profilesBox.valueProperty().isNull)

        btnNew.setOnAction {
            app.display.showInputBox(FXGL.getLocalizedString("profile.new"), InputPredicates.ALPHANUM, Consumer { name ->
                profileName.set(name)
                hasSaves.value = false
                saveLoadManager = SaveLoadManager(name)

                saveProfile()
            })
        }

        btnSelect.setOnAction {
            val name = profilesBox.value

            saveLoadManager = SaveLoadManager(name)

            saveLoadManager.loadProfileTask()
                    .onSuccess { profile ->
                        val ok = loadFromProfile(profile)

                        if (!ok) {
                            app.display.showErrorBox(FXGL.getLocalizedString("profile.corrupted")+": $name", { showProfileDialog() })
                        } else {
                            profileName.set(name)

                            saveLoadManager.loadLastModifiedSaveFileTask()
                                    .onSuccess { hasSaves.value = true }
                                    .onFailure { hasSaves.value = false }
                                    .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.loadingLast")))
                        }
                    }
                    .onFailure { error ->
                        app.display.showErrorBox(FXGL.getLocalizedString("profile.corrupted")+(": $name\nError: $error"), { this.showProfileDialog() })
                    }
                    .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("profile.loadingProfile")+": $name"))
        }

        btnDelete.setOnAction {
            val name = profilesBox.value

            SaveLoadManager.deleteProfileTask(name)
                    .onSuccess { showProfileDialog() }
                    .onFailure { error -> app.display.showErrorBox("$error", { showProfileDialog() }) }
                    .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("profile.deletingProfile")+": $name"))
        }

        SaveLoadManager.loadProfileNamesTask()
                .onSuccess { names ->
                    profilesBox.items.addAll(names)

                    if (!profilesBox.items.isEmpty()) {
                        profilesBox.selectionModel.selectFirst()
                    }

                    app.display.showBox(FXGL.getLocalizedString("profile.selectOrCreate"), profilesBox, btnSelect, btnNew, btnDelete)
                }
                .onFailure { error ->
                    log.warning("$error")

                    app.display.showBox(FXGL.getLocalizedString("profile.selectOrCreate"), profilesBox, btnSelect, btnNew, btnDelete)
                }
                .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("profile.loadingProfiles")))
    }
}