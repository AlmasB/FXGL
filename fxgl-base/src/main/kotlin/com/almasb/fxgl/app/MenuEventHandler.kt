/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.app.FXGL.Companion.getLocalizedString
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
                .onSuccessKt { app.startLoadedGame(it) }
                .executeAsyncWithDialogFX(ProgressDialog("Loading..."))
    }

    override fun onResume() {
        app.stateMachine.startPlay()
    }

    private fun doSave(saveFileName: String) {
        val dataFile = app.saveState()
        val saveFile = SaveFile(saveFileName, LocalDateTime.now())

        saveLoadManager
                .saveTask(dataFile, saveFile)
                .onSuccessKt { hasSaves.value = true }
                .executeAsyncWithDialogFX(ProgressDialog("Saving data: $saveFileName"))
    }

    override fun onSave() {
        app.display.showInputBoxWithCancel("Enter save file name", InputPredicates.ALPHANUM, Consumer { saveFileName ->

            if (saveFileName.isEmpty())
                return@Consumer

            if (saveLoadManager.saveFileExists(saveFileName)) {
                app.display.showConfirmationBox("Overwrite save [$saveFileName]?", { yes ->

                    if (yes)
                        doSave(saveFileName)
                })
            } else {
                doSave(saveFileName)
            }
        })
    }

    override fun onLoad(saveFile: SaveFile) {
        app.display.showConfirmationBox("Load save [${saveFile.name}]?\nUnsaved progress will be lost!", { yes ->

            if (yes) {
                saveLoadManager
                        .loadTask(saveFile)
                        .onSuccessKt { app.startLoadedGame(it) }
                        .executeAsyncWithDialogFX(ProgressDialog("Loading: ${saveFile.name}"))
            }
        })
    }

    override fun onDelete(saveFile: SaveFile) {
        app.display.showConfirmationBox("Delete save [${saveFile.name}]?", { yes ->

            if (yes) {
                saveLoadManager
                        .deleteSaveFileTask(saveFile)
                        .executeAsyncWithDialogFX(ProgressDialog("Deleting: ${saveFile.name}"))
            }
        })
    }

    override fun onLogout() {
        app.display.showConfirmationBox("Log out?", { yes ->

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
        app.display.showConfirmationBox("Exit to Main Menu?\nUnsaved progress will be lost!", { yes ->

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
        app.mainWindow.fixAspectRatio()
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
                    .onFailureKt { error -> log.warning("Failed to save profile: ${profileName.value} - $error") }
                    .execute() // we execute synchronously to avoid incomplete save since we might be shutting down
        }
    }

    /* DIALOGS */

    private fun showMultiplayerDialog() {
        val btnHost = FXGL.getUIFactory().newButton("Host")
        btnHost.setOnAction {
            FXGL.getNet()
                    .hostMultiplayerTask()
                    .onSuccessKt { onNewGame() }
                    .onFailureKt { app.display.showErrorBox(it) }
                    .executeAsyncWithDialogFX(ProgressDialog("Hosting Game"))
        }

        val btnConnect = FXGL.getUIFactory().newButton("Connect...")
        btnConnect.setOnAction {
            app.display.showInputBox("Enter Server IP", {
                FXGL.getNet()
                        .connectMultiplayerTask(it)
                        .onSuccessKt { onNewGame() }
                        .executeAsyncWithDialogFX(ProgressDialog("Connecting to Game"))
            })
        }

        app.display.showBox("Multiplayer Options", FXGL.getUIFactory().newText(""), btnHost, btnConnect)
    }

    /**
     * Show profile dialog so that user selects existing or creates new profile.
     * The dialog is only dismissed when profile is chosen either way.
     */
    fun showProfileDialog() {
        val profilesBox = FXGL.getUIFactory().newChoiceBox(FXCollections.observableArrayList<String>())

        val btnNew = FXGL.getUIFactory().newButton("NEW")
        val btnSelect = FXGL.getUIFactory().newButton("SELECT")
        btnSelect.disableProperty().bind(profilesBox.valueProperty().isNull)
        val btnDelete = FXGL.getUIFactory().newButton("DELETE")
        btnDelete.disableProperty().bind(profilesBox.valueProperty().isNull)

        btnNew.setOnAction {
            app.display.showInputBox("New Profile", InputPredicates.ALPHANUM, Consumer { name ->
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
                    .onSuccessKt { profile ->
                        val ok = loadFromProfile(profile)

                        if (!ok) {
                            app.display.showErrorBox("Profile is corrupted: $name", { showProfileDialog() })
                        } else {
                            profileName.set(name)

                            saveLoadManager.loadLastModifiedSaveFileTask()
                                    .onSuccessKt { hasSaves.value = true }
                                    .onFailureKt { hasSaves.value = false }
                                    .executeAsyncWithDialogFX(ProgressDialog("Loading last save file"))
                        }
                    }
                    .onFailureKt { error ->
                        app.display.showErrorBox("Profile is corrupted: $name\nError: $error", { this.showProfileDialog() })
                    }
                    .executeAsyncWithDialogFX(ProgressDialog("Loading Profile: $name"))
        }

        btnDelete.setOnAction {
            val name = profilesBox.value

            SaveLoadManager.deleteProfileTask(name)
                    .onSuccessKt { showProfileDialog() }
                    .onFailureKt { error -> app.display.showErrorBox("$error", { showProfileDialog() }) }
                    .executeAsyncWithDialogFX(ProgressDialog("Deleting profile: $name"))
        }

        SaveLoadManager.loadProfileNamesTask()
                .onSuccessKt { names ->
                    profilesBox.items.addAll(names)

                    if (!profilesBox.items.isEmpty()) {
                        profilesBox.selectionModel.selectFirst()
                    }

                    app.display.showBox("Select profile or create new", profilesBox, btnSelect, btnNew, btnDelete)
                }
                .onFailureKt { error ->
                    log.warning("$error")

                    app.display.showBox("Select profile or create new", profilesBox, btnSelect, btnNew, btnDelete)
                }
                .executeAsyncWithDialogFX(ProgressDialog("Loading profiles"))
    }
}