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

import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.settings.SceneDimension;
import com.almasb.fxgl.ui.FXGLScene;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
import java.util.logging.Logger;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class Display /*implements UserProfileSavable*/ {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Display");

    private Stage stage;

    /**
     * Underlying JavaFX scene. We only use 1 scene to avoid
     * problems in fullscreen mode. Switching between scenes
     * in FS mode will otherwise temporarily toggle FS.
     */
    private Scene scene;
    private FXGLScene currentScene;

    private List<FXGLScene> scenes = new ArrayList<>();

    private final DoubleProperty targetWidth;
    private final DoubleProperty targetHeight;
    private final DoubleProperty scaledWidth;
    private final DoubleProperty scaledHeight;
    private final DoubleProperty scaleRatio;

    private String css = "";


    private ReadOnlyGameSettings settings;

    @Inject
    private EventBus eventBus;

    @Inject
    private Display(Stage stage, Scene scene, ReadOnlyGameSettings settings) {
        this.stage = stage;
        this.scene = scene;
        this.settings = settings;

        targetWidth = new SimpleDoubleProperty(settings.getWidth());
        targetHeight = new SimpleDoubleProperty(settings.getHeight());
        scaledWidth = new SimpleDoubleProperty();
        scaledHeight = new SimpleDoubleProperty();
        scaleRatio = new SimpleDoubleProperty();

        /*
         * Since FXGL scenes are not JavaFX nodes they don't get notified of events.
         * This is a desired behavior because we only have 1 JavaFX scene for all FXGL scenes.
         * So we copy the occurred event and reroute to whichever FXGL scene is current.
         */
        scene.addEventFilter(EventType.ROOT, event -> {
            Event copy = event.copyFor(null, null);
            currentScene.fireEvent(copy);
            //eventBus.fireEvent(copy);
        });

        computeSceneSettings(settings.getWidth(), settings.getHeight());
        computeScaledSize();

        log.finer("Service [Display] initialized");
    }

    public void registerScene(FXGLScene scene) {
        scenes.add(scene);
        Pane root = scene.getRoot();

        root.prefWidthProperty().bind(scaledWidth);
        root.prefHeightProperty().bind(scaledHeight);

        Scale scale = new Scale();
        scale.xProperty().bind(scaleRatio);
        scale.yProperty().bind(scaleRatio);
        root.getTransforms().setAll(scale);

        if (!css.isEmpty())
            root.getStylesheets().add(css);
    }

    public void setScene(FXGLScene scene) {
        currentScene = scene;
        this.scene.setRoot(scene.getRoot());
    }

    public FXGLScene getCurrentScene() {
        return currentScene;
    }

    public Rectangle2D getBounds() {
        return settings.isFullScreen()
                ? Screen.getPrimary().getBounds()
                : Screen.getPrimary().getVisualBounds();
    }

    /**
     * Saves a screenshot of the current main scene into a ".png" file
     *
     * @return true if the screenshot was saved successfully, false otherwise
     */
    public boolean saveScreenshot() {
        Image fxImage = scene.snapshot(null);
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
     * Attaches CSS to settings to be used by all FXGL scenes.
     *
     * @param width  target (app) width
     * @param height target (app) height
     * @return scene settings with computed values
     */
    public void computeSceneSettings(double width, double height) {
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

        // if CSS not set, use menu CSS
        String css = settings.getCSS();
        css = !css.isEmpty() ? css : settings.getMenuStyle().getCSS();

        String loadedCSS = GameApplication.getService(ServiceType.ASSET_LOADER).loadCSS(css);
        log.finer("Using CSS: " + css);

        this.css = loadedCSS;
        //return new SceneSettings(width, height, bounds, loadedCSS);
    }

    public final double getTargetWidth() {
        return targetWidth.get();
    }

    public final double getTargetHeight() {
        return targetHeight.get();
    }

    public final double getScaledWidth() {
        return scaledWidth.get();
    }

    public final double getScaledHeight() {
        return scaledHeight.get();
    }

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

    public void setNewTargetSize(double w, double h) {
        targetWidth.set(w);
        targetHeight.set(h);
        computeScaledSize();
    }

    private void setNewResolution(double w, double h) {
        setNewTargetSize(w, h);

        Parent root = scene.getRoot();
        // clear root of previous JavaFX scene
        scene.setRoot(new Pane());

        scene = new Scene(root);
        scene.addEventFilter(EventType.ROOT, event -> {
            Event copy = event.copyFor(null, null);
            currentScene.fireEvent(copy);
        });
        stage.setScene(scene);
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







//
//
//
//
//    private FXGLDialogBox dialogBox;
//
//    /**
//     * Settings for all scenes to apply.
//     */
//    private SceneSettings sceneSettings;
//
//    private GameApplication app;
//
//    private EventBus eventBus;
//
//    /**
//     * Constructs scene manager.
//     *
//     * @param app   instance of game application
//     * @param scene main scene
//     */
//    Display(GameApplication app, Scene scene) {
//        this.app = app;
//        this.scene = scene;
//        eventBus = GameApplication.getService(ServiceType.EVENT_BUS);
//

//

//
//        initDialogBox();
//
//
//    }
//
//    private void initDialogBox() {
//        dialogBox = UIFactory.getDialogBox();
//        dialogBox.setOnShown(e -> {
//            if (!isMenuOpen())
//                app.pause();
//
//            app.getInput().clearAllInput();
//        });
//        dialogBox.setOnHidden(e -> {
//            if (!isMenuOpen())
//                app.resume();
//        });
//    }
//

//

//
//    /**
//     * Shows given dialog and blocks execution of the game until the dialog is
//     * dismissed. The provided callback will be called with the dialog result as
//     * parameter when the dialog closes.
//     *
//     * @param dialog         JavaFX dialog
//     * @param resultCallback the function to be called
//     */
//    public <T> void showDialog(Dialog<T> dialog, Consumer<T> resultCallback) {
//        boolean paused = menuOpenProperty().get();
//
//        if (!paused)
//            app.pause();
//
//        app.getInput().clearAllInput();
//
//        dialog.initOwner(scene.getWindow());
//        dialog.setOnCloseRequest(e -> {
//            if (!paused)
//                app.resume();
//
//            resultCallback.accept(dialog.getResult());
//        });
//        dialog.show();
//    }
//
//    /**
//     * Shows a blocking (stops game execution) message box with OK button. On
//     * button press, the message box will be dismissed.
//     *
//     * @param message the message to show
//     */
//    public void showMessageBox(String message) {
//        dialogBox.showMessageBox(message);
//    }
//
//    /**
//     * Shows a blocking message box with YES and NO buttons. The callback is
//     * invoked with the user answer as parameter.
//     *
//     * @param message        message to show
//     * @param resultCallback the function to be called
//     */
//    public void showConfirmationBox(String message,
//                                    Consumer<Boolean> resultCallback) {
//        dialogBox.showConfirmationBox(message, resultCallback);
//    }
//
//    /**
//     * Shows a blocking message box with OK button and input field. The callback
//     * is invoked with the field text as parameter.
//     *
//     * @param message        message to show
//     * @param resultCallback the function to be called
//     */
//    public void showInputBox(String message, Consumer<String> resultCallback) {
//        dialogBox.showInputBox(message, resultCallback);
//    }
//

//
//    @Override
//    public void save(UserProfile profile) {
//        log.finer("Saving data to profile");
//
//        UserProfile.Bundle bundle = new UserProfile.Bundle("scene");
//        bundle.put("sizeW", sceneSettings.getTargetWidth());
//        bundle.put("sizeH", sceneSettings.getTargetHeight());
//
//        bundle.log();
//        profile.putBundle(bundle);
//    }
//
//    @Override
//    public void load(UserProfile profile) {
//        log.finer("Loading data from profile");
//
//        UserProfile.Bundle bundle = profile.getBundle("scene");
//        bundle.log();
//
//        setNewResolution(bundle.get("sizeW"), bundle.get("sizeH"));
//    }
}
