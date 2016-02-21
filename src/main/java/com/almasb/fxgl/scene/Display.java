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

package com.almasb.fxgl.scene;

import com.almasb.fxeventbus.EventBus;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.asset.CSS;
import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.event.DisplayEvent;
import com.almasb.fxgl.event.LoadEvent;
import com.almasb.fxgl.event.SaveEvent;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.settings.SceneDimension;
import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.settings.UserProfileSavable;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Display service. Provides access to dialogs and display settings.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class Display implements UserProfileSavable {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Display");

    private final Stage stage;

    /**
     * Underlying JavaFX scene. We only use 1 scene to avoid
     * problems in fullscreen mode. Switching between scenes
     * in FS mode will otherwise temporarily toggle FS.
     */
    private Scene fxScene;
    private final ReadOnlyObjectWrapper<FXGLScene> currentScene = new ReadOnlyObjectWrapper<>();

    private final List<FXGLScene> scenes = new ArrayList<>();

    private final DoubleProperty targetWidth;
    private final DoubleProperty targetHeight;
    private final DoubleProperty scaledWidth;
    private final DoubleProperty scaledHeight;
    private final DoubleProperty scaleRatio;

    private final CSS css;

    private final ReadOnlyGameSettings settings;

    private final EventBus eventBus;

    /*
     * Since FXGL scenes are not JavaFX nodes they don't get notified of events.
     * This is a desired behavior because we only have 1 JavaFX scene for all FXGL scenes.
     * So we copy the occurred event and reroute to whichever FXGL scene is current.
     */
    private final EventHandler<Event> fxToFXGLFilter = event -> {
        Event copy = event.copyFor(null, null);
        getCurrentScene().fireEvent(copy);
    };

    @Inject
    private Display(Stage stage, Scene fxScene, ReadOnlyGameSettings settings) {
        this.stage = stage;
        this.fxScene = fxScene;
        this.settings = settings;

        targetWidth = new SimpleDoubleProperty(settings.getWidth());
        targetHeight = new SimpleDoubleProperty(settings.getHeight());
        scaledWidth = new SimpleDoubleProperty();
        scaledHeight = new SimpleDoubleProperty();
        scaleRatio = new SimpleDoubleProperty();

        // if default css then use menu css, else use specified
        css = FXGLAssets.UI_CSS.isDefault()
                ? GameApplication.getService(ServiceType.ASSET_LOADER).loadCSS(settings.getMenuStyle().getCSSFileName())
                : FXGLAssets.UI_CSS;

        initStage();
        initDialogBox();

        fxScene.addEventFilter(EventType.ROOT, fxToFXGLFilter);

        computeSceneSettings(settings.getWidth(), settings.getHeight());
        computeScaledSize();

        eventBus = GameApplication.getService(ServiceType.EVENT_BUS);
        eventBus.addEventHandler(SaveEvent.ANY, event -> {
            save(event.getProfile());
        });

        eventBus.addEventHandler(LoadEvent.ANY, event -> {
            load(event.getProfile());
        });

        log.finer("Service [Display] initialized");
        log.finer("Using CSS: " + css);
    }

    /**
     * Configure main stage based on user settings.
     */
    private void initStage() {
        stage.setScene(fxScene);
        stage.setTitle(settings.getTitle() + " " + settings.getVersion());
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            e.consume();

            showConfirmationBox("Exit the game?", yes -> {
                if (yes)
                    eventBus.fireEvent(new DisplayEvent(DisplayEvent.CLOSE_REQUEST));
            });
        });
        stage.getIcons().add(FXGLAssets.UI_ICON);

        if (settings.isFullScreen()) {
            stage.setFullScreenExitHint("");
            // don't let the user to exit FS mode manually
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
        }

        stage.sizeToScene();
    }

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    public void registerScene(FXGLScene scene) {
        scenes.add(scene);
        Pane root = scene.getRoot();

        root.prefWidthProperty().bind(scaledWidth);
        root.prefHeightProperty().bind(scaledHeight);

        Scale scale = new Scale();
        scale.xProperty().bind(scaleRatio);
        scale.yProperty().bind(scaleRatio);
        root.getTransforms().setAll(scale);

        root.getStylesheets().add(css.getExternalForm());
    }

    /**
     * Set current FXGL scene. The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    public void setScene(FXGLScene scene) {
        if (getCurrentScene() != null) {
            getCurrentScene().activeProperty().set(false);
        }
        currentScene.set(scene);
        scene.activeProperty().set(true);

        fxScene.setRoot(scene.getRoot());
    }

    /**
     * @return current scene property
     */
    public ReadOnlyObjectProperty<FXGLScene> currentSceneProperty() {
        return currentScene.getReadOnlyProperty();
    }

    /**
     * @return current FXGL scene
     */
    public FXGLScene getCurrentScene() {
        return currentScene.get();
    }

    /**
     * Returns available (visual) bounds of the physical display.
     * If the game is running fullscreen then this returns maximum bounds
     * of the physical display.
     *
     * @return display bounds
     */
    public Rectangle2D getBounds() {
        return settings.isFullScreen()
                ? Screen.getPrimary().getBounds()
                : Screen.getPrimary().getVisualBounds();
    }

    /**
     * Saves a screenshot of the current scene into a ".png" file.
     *
     * @return true if the screenshot was saved successfully, false otherwise
     */
    public boolean saveScreenshot() {
        Image fxImage = fxScene.snapshot(null);
        BufferedImage img = SwingFXUtils.fromFXImage(fxImage, null);

        String fileName = "./" + settings.getTitle()
                + settings.getVersion() + LocalDateTime.now() + ".png";

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

    private List<SceneDimension> sceneDimensions = new ArrayList<>();

    /**
     *
     * @return a list of supported scene dimensions with 360, 480, 720 and 1080 heights
     */
    public List<SceneDimension> getSceneDimensions() {
        return new ArrayList<>(sceneDimensions);
    }

    /**
     * Computes scene settings based on target size and screen bounds.
     *
     * @param width  target (app) width
     * @param height target (app) height
     */
    private void computeSceneSettings(double width, double height) {
        Rectangle2D bounds = getBounds();

        int[] heights = {360, 480, 720, 1080};

        double ratio = width / height;
        for (int h : heights) {
            if (h <= bounds.getHeight() && h * ratio <= bounds.getWidth()) {
                sceneDimensions.add(new SceneDimension(h*ratio, h));
            } else {
                break;
            }
        }
    }

    /**
     * @return target width (requested width)
     */
    public final double getTargetWidth() {
        return targetWidth.get();
    }

    /**
     * @return target height (requested height)
     */
    public final double getTargetHeight() {
        return targetHeight.get();
    }

    /**
     * @return scaled width (actual width of the scenes)
     */
    public final double getScaledWidth() {
        return scaledWidth.get();
    }

    /**
     * @return scaled height (actual height of the scenes)
     */
    public final double getScaledHeight() {
        return scaledHeight.get();
    }

    /**
     * @return scale ratio (set width / settings width)
     */
    public final double getScaleRatio() {
        return scaleRatio.get();
    }

    /**
     * Computes scaled size of the output based on screen and target
     * resolutions.
     */
    private void computeScaledSize() {
        double newW = getTargetWidth();
        double newH = getTargetHeight();
        Rectangle2D bounds = getBounds();

        if (newW > bounds.getWidth() || newH > bounds.getHeight()) {
            log.finer("App size > screen size");

            double ratio = newW / newH;

            for (int newWidth = (int) bounds.getWidth(); newWidth > 0; newWidth--) {
                if (newWidth / ratio <= bounds.getHeight()) {
                    newW = newWidth;
                    newH = newWidth / ratio;
                    break;
                }
            }
        }

        scaledWidth.set(newW);
        scaledHeight.set(newH);
        scaleRatio.set(newW / settings.getWidth());

        log.finer("Target size: " + getTargetWidth() + "x" + getTargetHeight() + "@" + 1.0);
        log.finer("New size:    " + newW  + "x" + newH   + "@" + getScaleRatio());
    }

    /**
     * Performs actual change of output resolution.
     * It will create a new underlying JavaFX scene.
     *
     * @param w new width
     * @param h new height
     */
    private void setNewResolution(double w, double h) {
        targetWidth.set(w);
        targetHeight.set(h);
        computeScaledSize();

        Parent root = fxScene.getRoot();
        // clear listener
        fxScene.removeEventFilter(EventType.ROOT, fxToFXGLFilter);
        // clear root of previous JavaFX scene
        fxScene.setRoot(new Pane());

        // create and init new JavaFX scene
        fxScene = new Scene(root);
        fxScene.addEventFilter(EventType.ROOT, fxToFXGLFilter);
        stage.setScene(fxScene);
        if (settings.isFullScreen()) {
            stage.setFullScreen(true);
        }
    }

    /**
     * Set new scene dimension. This will change the video output
     * resolution and adapt all subsystems.
     *
     * @param dimension scene dimension
     */
    public void setSceneDimension(SceneDimension dimension) {
        if (sceneDimensions.contains(dimension)) {
            log.finer("Setting scene dimension: " + dimension);
            setNewResolution(dimension.getWidth(), dimension.getHeight());
        } else {
            log.warning(dimension + " is not supported!");
        }
    }

    private DialogPane newDialog;

    private void initDialogBox() {
        newDialog = new DialogPane(this);
        newDialog.setOnShown(() -> {
            fxScene.removeEventFilter(EventType.ROOT, fxToFXGLFilter);
            eventBus.fireEvent(new DisplayEvent(DisplayEvent.DIALOG_OPENED));
        });
        newDialog.setOnClosed(() -> {
            eventBus.fireEvent(new DisplayEvent(DisplayEvent.DIALOG_CLOSED));
            fxScene.addEventFilter(EventType.ROOT, fxToFXGLFilter);
        });
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
        eventBus.fireEvent(new DisplayEvent(DisplayEvent.DIALOG_OPENED));

        dialog.initOwner(stage);
        dialog.setOnCloseRequest(e -> {
            eventBus.fireEvent(new DisplayEvent(DisplayEvent.DIALOG_CLOSED));

            resultCallback.accept(dialog.getResult());
        });
        dialog.show();
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button. On
     * button press, the message box will be dismissed.
     *
     * @param message the message to show
     */
    public void showMessageBox(String message) {
        newDialog.showMessageBox(message);
    }

    /**
     * Shows a blocking message box with YES and NO buttons. The callback is
     * invoked with the user answer as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    public void showConfirmationBox(String message, Consumer<Boolean> resultCallback) {
        newDialog.showConfirmationBox(message, resultCallback);
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * @param resultCallback the function to be called
     */
    public void showInputBox(String message, Consumer<String> resultCallback) {
        newDialog.showInputBox(message, resultCallback);
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * @param filter  filter to validate input
     * @param resultCallback the function to be called
     */
    public void showInputBox(String message, Predicate<String> filter, Consumer<String> resultCallback) {
        newDialog.showInputBox(message, filter, resultCallback);
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param error the error to show
     */
    public void showErrorBox(Throwable error) {
        newDialog.showErrorBox(error);
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param errorMessage error message to show
     * @param callback the function to be called when dialog is dismissed
     */
    public void showErrorBox(String errorMessage, Runnable callback) {
        newDialog.showErrorBox(errorMessage, callback);
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) generic dialog.
     *
     * @param message the message
     * @param content the content
     * @param buttons buttons present
     */
    public void showBox(String message, Node content, Button... buttons) {
        newDialog.showBox(message, content, buttons);
    }

    @Override
    public void save(UserProfile profile) {
        log.finer("Saving data to profile");

        UserProfile.Bundle bundle = new UserProfile.Bundle("scene");
        bundle.put("sizeW", getTargetWidth());
        bundle.put("sizeH", getTargetHeight());

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
