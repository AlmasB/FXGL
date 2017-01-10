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

package com.almasb.fxgl.app

import com.almasb.fxgl.event.FXGLEvent
import com.almasb.fxgl.event.LoadEvent
import com.almasb.fxgl.event.ProfileSelectedEvent
import com.almasb.fxgl.event.SaveEvent
import com.almasb.fxgl.gameplay.SaveLoadManager
import com.almasb.fxgl.saving.SaveFile
import com.almasb.fxgl.scene.ProgressDialog
import com.almasb.fxgl.scene.menu.MenuEventListener
import com.almasb.fxgl.service.impl.display.DialogPane
import com.almasb.fxgl.settings.UserProfile
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import java.time.LocalDateTime
import java.util.function.Consumer

/**
 * Handles events that happen within menus.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class MenuEventHandler(private val app: GameApplication) : MenuEventListener, EventHandler<KeyEvent> {

    private val log = FXGL.getLogger(javaClass)

    private lateinit var saveLoadManager: SaveLoadManager

    override fun getSaveLoadManager(): SaveLoadManager {
        return saveLoadManager
    }

    /**
     * Stores the default profile data. This is used to restore default settings.
     */
    private val defaultProfile: UserProfile

    /**
     * Stores current selected profile name for this game.
     */
    private val profileName = ReadOnlyStringWrapper("")

    fun isProfileSelected() = profileName.value.isNotEmpty()

    init {
        defaultProfile = createProfile()

        app.eventBus.addEventHandler(FXGLEvent.EXIT, { saveProfile() })
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
        app.resume()
    }

    private fun doSave(saveFileName: String) {
        val dataFile = app.saveState()
        val saveFile = SaveFile(saveFileName, LocalDateTime.now())

        saveLoadManager
                .saveTask(dataFile, saveFile)
                .executeAsyncWithDialogFX(ProgressDialog("Saving data: $saveFileName"))
    }

    override fun onSave() {
        app.display.showInputBoxWithCancel("Enter save file name", DialogPane.ALPHANUM, Consumer { saveFileName ->

            if (saveFileName.isEmpty())
                return@Consumer;

            if (saveLoadManager.saveFileExists(saveFileName)) {
                app.display.showConfirmationBox("Overwrite save [$saveFileName]?", { yes ->

                    if (yes)
                        doSave(saveFileName);
                });
            } else {
                doSave(saveFileName);
            }
        });
    }

    override fun onLoad(saveFile: SaveFile) {
        app.display.showConfirmationBox("Load save [${saveFile.name}]?\nUnsaved progress will be lost!", { yes ->

            if (yes) {
                saveLoadManager
                        .loadTask(saveFile)
                        .onSuccessKt { app.startLoadedGame(it) }
                        .executeAsyncWithDialogFX(ProgressDialog("Loading: ${saveFile.name}"));
            }
        });
    }

    override fun onDelete(saveFile: SaveFile) {
        app.display.showConfirmationBox("Delete save [${saveFile.name}]?", { yes ->

            if (yes) {
                saveLoadManager
                        .deleteSaveFileTask(saveFile)
                        .executeAsyncWithDialogFX(ProgressDialog("Deleting: ${saveFile.name}"));
            }
        });
    }

    override fun onLogout() {
        app.display.showConfirmationBox("Log out?", { yes ->

            if (yes) {
                saveProfile();
                showProfileDialog();
            }
        });
    }

    override fun onMultiplayer() {
        showMultiplayerDialog();
    }

    override fun onExit() {
        app.display.showConfirmationBox("Exit the game?", { yes ->

            if (yes)
                app.exit();
        });
    }

    override fun onExitToMainMenu() {
        app.display.showConfirmationBox("Exit to Main Menu?\nUnsaved progress will be lost!", { yes ->

            if (yes) {
                app.pause();
                app.reset();
                app.setState(ApplicationState.MAIN_MENU);
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
            if (app.getState() === ApplicationState.GAME_MENU) {
                canSwitchGameMenu = false
                app.resume()
            } else if (app.getState() === ApplicationState.PLAYING) {
                canSwitchGameMenu = false
                app.pause()
                app.setState(ApplicationState.GAME_MENU)
            } else {
                log.warning("Menu key pressed in unknown state: " + app.getState())
            }
        }
    }

    override fun handle(event: KeyEvent) {
        if (event.getCode() == FXGL.getSettings().getMenuKey()) {
            onMenuKey(event.getEventType() == KeyEvent.KEY_PRESSED)
        }
    }

    /**
     * @return profile name property (read-only)
     */
    override fun profileNameProperty(): ReadOnlyStringProperty {
        return profileName.readOnlyProperty
    }

    /**
     * Create a user profile with current settings.

     * @return user profile
     */
    fun createProfile(): UserProfile {
        val profile = UserProfile(app.settings.getTitle(), app.settings.getVersion())

        app.eventBus.fireEvent(SaveEvent(profile))

        return profile
    }

    /**
     * Load from given user profile.

     * @param profile the profile
     * *
     * @return true if loaded successfully, false if couldn't load
     */
    fun loadFromProfile(profile: UserProfile): Boolean {
        if (!profile.isCompatible(app.settings.getTitle(), app.settings.getVersion()))
            return false

        app.eventBus.fireEvent(LoadEvent(LoadEvent.LOAD_PROFILE, profile))
        return true
    }

    /**
     * Restores default settings, e.g. audio, video, controls.
     */
    override fun restoreDefaultSettings() {
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
            app.display.showInputBox("New Profile", DialogPane.ALPHANUM, Consumer { name ->
                profileName.set(name)
                saveLoadManager = SaveLoadManager(name)

                app.eventBus.fireEvent(ProfileSelectedEvent(name, false))

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
                                    .onSuccessKt { file -> app.eventBus.fireEvent(ProfileSelectedEvent(name, true)) }
                                    .onFailureKt { error -> app.eventBus.fireEvent(ProfileSelectedEvent(name, false)) }
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