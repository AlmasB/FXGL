/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.io.SaveFile
import com.almasb.fxgl.scene.DialogPane
import com.almasb.fxgl.scene.ProgressDialog
import com.almasb.fxgl.scene.menu.MenuEventListener
import java.time.LocalDateTime
import java.util.function.Consumer

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MenuEventHandler(private val app: GameApplication) : MenuEventListener {

    override fun onNewGame() {
        app.startNewGame()
    }

    override fun onContinue() {
        app.saveLoadManager
                .loadLastModifiedSaveFileTask()
                .then { app.saveLoadManager.loadTask(it) }
                .onSuccess(Consumer { app.startLoadedGame(it) })
                .executeAsyncWithDialogFX(ProgressDialog("Loading..."))
    }

    override fun onResume() {
        app.resume()
    }

    private fun doSave(saveFileName: String) {
        val dataFile = app.saveState()
        val saveFile = SaveFile(saveFileName, LocalDateTime.now())

        app.saveLoadManager
                .saveTask(dataFile, saveFile)
                .executeAsyncWithDialogFX(ProgressDialog("Saving data: $saveFileName"))
    }

    override fun onSave() {
        app.display.showInputBoxWithCancel("Enter save file name", DialogPane.ALPHANUM, Consumer { saveFileName ->

            if (saveFileName.isEmpty())
                return@Consumer;

            if (app.saveLoadManager.saveFileExists(saveFileName)) {
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
                app.saveLoadManager
                        .loadTask(saveFile)
                        .onSuccess(Consumer { app.startLoadedGame(it) })
                        .executeAsyncWithDialogFX(ProgressDialog("Loading: ${saveFile.name}"));
            }
        });
    }

    override fun onDelete(saveFile: SaveFile) {
        app.display.showConfirmationBox("Delete save [${saveFile.name}]?", { yes ->

            if (yes) {
                app.saveLoadManager
                        .deleteSaveFileTask(saveFile)
                        .executeAsyncWithDialogFX(ProgressDialog("Deleting: ${saveFile.name}"));
            }
        });
    }

    override fun onLogout() {
        app.display.showConfirmationBox("Log out?", { yes ->

            if (yes) {
                app.saveProfile();
                app.showProfileDialog();
            }
        });
    }

    override fun onMultiplayer() {
        app.showMultiplayerDialog();
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
        });
    }
}