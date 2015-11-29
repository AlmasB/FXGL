/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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
package com.almasb.fxgl;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.event.FXGLEvent;
import com.almasb.fxgl.event.MenuEvent;
import com.almasb.fxgl.settings.SceneSettings;
import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.settings.UserProfileSavable;
import com.almasb.fxgl.ui.*;
import com.almasb.fxgl.util.FXGLLogger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Manages interactions between FXGL scenes and allows
 * creating scene-independent dialog boxes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class SceneManager implements UserProfileSavable {

    private static final Logger log = FXGLLogger.getLogger("FXGL.SceneManager");

    /**
     * Game scene, this is where all in-game objects are shown.
     */
    private GameScene gameScene;

    /**
     * Intro scene, this is shown when the application started,
     * before menus and game.
     */
    private IntroScene introScene;

    /**
     * Main menu, this is the menu shown at the start of game
     */
    private FXGLScene mainMenuScene;

    /**
     * In-game menu, this is shown when menu key pressed during the game
     */
    private FXGLScene gameMenuScene;

    /**
     * Reference to current shown scene.
     */
    private FXGLScene currentScene;

    /**
     * Is menu enabled in settings
     */
    private boolean isMenuEnabled;

    /**
     * The dialog box used to communicate with the user.
     */
    private FXGLDialogBox dialogBox;

    /**
     * Game application instance.
     */
    private GameApplication app;

    /**
     * Underlying JavaFX scene. We only use 1 scene to avoid
     * problems in fullscreen mode. Switching between scenes
     * in FS mode will otherwise temporarily toggle FS.
     */
    private Scene scene;

    /**
     * Settings for all scenes to apply.
     */
    private SceneSettings sceneSettings;

    private EventBus eventBus;

    /**
     * Constructs scene manager.
     *
     * @param app   instance of game application
     * @param scene main scene
     */
    @Inject
    SceneManager(GameApplication app, Scene scene) {
        this.app = app;
        this.scene = scene;
        eventBus = GameApplication.getService(ServiceType.EVENT_BUS);

        /*
         * Since FXGL scenes are not JavaFX nodes they don't get notified of events.
         * This is a desired behavior because we only have 1 JavaFX scene for all FXGL scenes.
         * So we copy the occurred event and reroute to whichever FXGL scene is current.
         */
        scene.addEventFilter(EventType.ROOT, event -> {
            Event copy = event.copyFor(null, null);
            currentScene.fireEvent(copy);
            if (currentScene == gameScene) {
                eventBus.fireEvent(copy);
            }
        });

        isMenuEnabled = app.getSettings().isMenuEnabled();
        menuOpen = new ReadOnlyBooleanWrapper(isMenuEnabled);

        sceneSettings = computeSceneSettings(app.getWidth(), app.getHeight());
        gameScene = new GameScene(sceneSettings);
        initDialogBox();

        log.finer("Service [Display] initialized");
    }

    private void initDialogBox() {
        dialogBox = UIFactory.getDialogBox();
        dialogBox.setOnShown(e -> {
            if (!isMenuOpen())
                app.pause();

            app.getInput().clearAllInput();
        });
        dialogBox.setOnHidden(e -> {
            if (!isMenuOpen())
                app.resume();
        });
    }

    private List<SceneSettings.SceneDimension> sceneDimensions = new ArrayList<>();

    /**
     *
     * @return a list of supported scene dimensions with 360, 480, 720 and 1080 heights
     */
    public List<SceneSettings.SceneDimension> getSceneDimensions() {
        return new ArrayList<>(sceneDimensions);
    }

    /**
     * Computes scene settings based on target size and screen bounds.
     * Attaches CSS to settings to be used by all FXGL scenes.
     *
     * @param width  target (app) width
     * @param height target (app) height
     * @return scene settings with computed values
     */
    private SceneSettings computeSceneSettings(double width, double height) {
        Rectangle2D bounds = app.getSettings().isFullScreen()
                ? Screen.getPrimary().getBounds()
                : Screen.getPrimary().getVisualBounds();

        int[] heights = {360, 480, 720, 1080};

        double ratio = width / height;
        for (int h : heights) {
            if (h <= bounds.getHeight() && h * ratio <= bounds.getWidth()) {
                sceneDimensions.add(new SceneSettings.SceneDimension(h*ratio, h));
            } else {
                break;
            }
        }

        // if CSS not set, use menu CSS
        String css = app.getSettings().getCSS();
        css = !css.isEmpty() ? css : app.getSettings().getMenuStyle().getCSS();

        String loadedCSS = GameApplication.getService(ServiceType.ASSET_LOADER).loadCSS(css);
        log.finer("Using CSS: " + css);

        return new SceneSettings(width, height, bounds, loadedCSS);
    }

    private boolean canSwitchGameMenu = true;

    /**
     * Creates Main and Game menu scenes.
     * Registers event handlers to menus.
     */
    private void configureMenu() {
        menuOpenProperty().addListener((obs, wasOpen, isOpen) -> {
            if (isOpen) {
                log.finer("Playing State -> Menu State");
                app.onMenuOpen();
            } else {
                log.finer("Menu State -> Playing State");
                app.onMenuClose();
            }
        });

        MenuFactory menuFactory = app.initMenuFactory();

        mainMenuScene = menuFactory.newMainMenu(app, sceneSettings);
        gameMenuScene = menuFactory.newGameMenu(app, sceneSettings);

        eventBus.addEventHandler(MenuEvent.PAUSE, event -> setScene(gameMenuScene));
        eventBus.addEventHandler(MenuEvent.RESUME, event -> setScene(gameScene));
        eventBus.addEventHandler(FXGLEvent.INIT_APP_COMPLETE, event -> setScene(gameScene));
        eventBus.addEventHandler(MenuEvent.EXIT_TO_MAIN_MENU, event -> setScene(mainMenuScene));
    }

    /**
     * @return game scene
     */
    public GameScene getGameScene() {
        return gameScene;
    }

    /**
     * Changes current scene to given scene.
     *
     * @param scene the scene to set as active
     */
    private void setScene(FXGLScene scene) {
        currentScene = scene;

        menuOpen.set(scene == mainMenuScene || scene == gameMenuScene);

        this.scene.setRoot(scene.getRoot());
    }

    private void setNewResolution(double w, double h) {
        sceneSettings.setNewTargetSize(w, h);

        Parent root = scene.getRoot();
        scene.setRoot(new Pane());
        Stage stage = (Stage) scene.getWindow();

        scene = new Scene(root);
        scene.addEventFilter(EventType.ROOT, event -> {
            Event copy = event.copyFor(null, null);
            currentScene.fireEvent(copy);
        });
        stage.setScene(scene);
        if (app.getSettings().isFullScreen()) {
            stage.setFullScreen(true);
        }
    }

    /**
     * Set new scene dimension. This will change the video output
     * resolution and adapt all subsystems.
     *
     * @param dimension scene dimension
     */
    public void setSceneDimension(SceneSettings.SceneDimension dimension) {
        if (sceneDimensions.contains(dimension)) {
            log.finer("Setting scene dimension: " + dimension);
            setNewResolution(dimension.getWidth(), dimension.getHeight());
        } else {
            log.warning(dimension + " is not supported!");
        }
    }

    private ReadOnlyBooleanWrapper menuOpen;

    /**
     * @return property tracking if any menu is open
     */
    public ReadOnlyBooleanProperty menuOpenProperty() {
        return menuOpen.getReadOnlyProperty();
    }

    /**
     * @return true if any menu is open
     */
    public boolean isMenuOpen() {
        return menuOpen.get();
    }

    /**
     * Shows given dialog and blocks execution of the game until the dialog is
     * dismissed. The provided callback will be called with the dialog result as
     * parameter when the dialog closes.
     *
     * @param dialog         JavaFX dialog
     * @param resultCallback the function to be called
     */
    public <T> void showDialog(Dialog<T> dialog, Consumer<T> resultCallback) {
        boolean paused = menuOpenProperty().get();

        if (!paused)
            app.pause();

        app.getInput().clearAllInput();

        dialog.initOwner(scene.getWindow());
        dialog.setOnCloseRequest(e -> {
            if (!paused)
                app.resume();

            resultCallback.accept(dialog.getResult());
        });
        dialog.show();
    }

    /**
     * Shows a blocking (stops game execution) message box with OK button. On
     * button press, the message box will be dismissed.
     *
     * @param message the message to show
     */
    public void showMessageBox(String message) {
        dialogBox.showMessageBox(message);
    }

    /**
     * Shows a blocking message box with YES and NO buttons. The callback is
     * invoked with the user answer as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    public void showConfirmationBox(String message,
                                    Consumer<Boolean> resultCallback) {
        dialogBox.showConfirmationBox(message, resultCallback);
    }

    /**
     * Shows a blocking message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    public void showInputBox(String message, Consumer<String> resultCallback) {
        dialogBox.showInputBox(message, resultCallback);
    }

    /**
     * Saves a screenshot of the current main scene into a ".png" file
     *
     * @return true if the screenshot was saved successfully, false otherwise
     */
    public boolean saveScreenshot() {
        Image fxImage = scene.snapshot(null);
        BufferedImage img = SwingFXUtils.fromFXImage(fxImage, null);

        String fileName = "./" + app.getSettings().getTitle()
                + app.getSettings().getVersion() + LocalDateTime.now() + ".png";

        fileName = fileName.replace(":", "_");

        try (OutputStream os = Files.newOutputStream(Paths.get(fileName))) {
            return ImageIO.write(img, "png", os);
        } catch (Exception e) {
            log.finer(
                    "Exception occurred during saveScreenshot() - "
                            + e.getMessage());
        }

        return false;
    }

    @Override
    public void save(UserProfile profile) {
        log.finer("Saving data to profile");

        UserProfile.Bundle bundle = new UserProfile.Bundle("scene");
        bundle.put("sizeW", sceneSettings.getTargetWidth());
        bundle.put("sizeH", sceneSettings.getTargetHeight());

        bundle.log();
        profile.putBundle(bundle);
    }

    @Override
    public void load(UserProfile profile) {
        log.finer("Loading data from profile");

        UserProfile.Bundle bundle = profile.getBundle("scene");
        bundle.log();

        setNewResolution(bundle.get("sizeW"), bundle.get("sizeH"));
    }
}
