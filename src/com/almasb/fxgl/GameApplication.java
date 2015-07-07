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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.effect.ParticleManager;
import com.almasb.fxgl.entity.CombinedEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.FXGLEvent;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.QTEManager;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.physics.PhysicsManager;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * To use FXGL extend this class and implement necessary methods
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public abstract class GameApplication extends Application {

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").severe("Unhandled Exception");
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").severe(FXGLLogger.errorTraceAsString(error));
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").severe("Closing due to Unhandled Exception");
            FXGLLogger.close();
            System.exit(0);
        });
        FXGLLogger.init(Level.ALL);
        Version.print();
    }

    protected static final Logger log = FXGLLogger.getLogger("FXGL.GameApplication");

    /**
     * A second in nanoseconds
     */
    public static final long SECOND = 1000000000;

    /**
     * A minute in nanoseconds
     */
    public static final long MINUTE = 60 * SECOND;

    private GameSettings settings = new GameSettings();

    /**
     * Reference to game scene
     */
    protected Scene mainScene;

    /**
     * Reference to game window
     */
    protected Stage mainStage;

    private Scene mainMenuScene;

    private Pane root, gameRoot, uiRoot, mainMenuRoot;

    /**
     * These are current width and height of the scene
     * NOT the window
     */
    private double currentWidth, currentHeight;

    private List<Entity> tmpAddList = new ArrayList<>();
    private List<Entity> tmpRemoveList = new ArrayList<>();

    private AnimationTimer timer;
    private ScheduledExecutorService scheduleThread = Executors.newSingleThreadScheduledExecutor();

    protected final InputManager inputManager = new InputManager();

    /**
     * Used for loading various assets
     */
    protected final AssetManager assetManager = AssetManager.INSTANCE;

    protected final PhysicsManager physicsManager = new PhysicsManager(this);

    protected final ParticleManager particleManager = new ParticleManager(this);

    protected final QTEManager qteManager = new QTEManager(this);

    protected final SaveLoadManager saveLoadManager = new SaveLoadManager();

    /**
     * Default random number generator
     */
    protected final Random random = new Random();

    /**
     * Current time for this tick in nanoseconds
     */
    protected long now = 0;

    /**
     * Current tick
     */
    protected long tick = 0;

    private FPSCounter fpsCounter = new FPSCounter();
    private FPSCounter fpsPerformanceCounter = new FPSCounter();

    /**
     * Average render FPS
     */
    protected int fps = 0;

    /**
     * Average performance FPS
     */
    protected int fpsPerformance = 0;

    /**
     * Initialize game settings
     *
     * @param settings
     */
    protected abstract void initSettings(GameSettings settings);

    /**
     * Override to use your custom intro video
     *
     * @return
     */
    protected Intro initIntroVideo() {
        return new FXGLIntro(getWidth(), getHeight());
    }

    /**
     * Initialize game assets, such as Texture, AudioClip, Music
     *
     * @throws Exception
     */
    protected abstract void initAssets() throws Exception;

    /**
     * Initialize game objects, key bindings, collision handlers
     *
     * @param gameRoot
     */
    protected abstract void initGame();

    /**
     * Initiliaze UI objects
     *
     * @param uiRoot
     */
    protected abstract void initUI(Pane uiRoot);

    /**
     * Initiliaze input, i.e.
     * bind key presses / key typed
     */
    protected abstract void initInput();

    /**
     * Main loop update phase, most of game logic and clean up
     *
     * @param now
     */
    protected abstract void onUpdate();

    /**
     * Default implementation does nothing
     *
     * Override to add your own cleanup
     */
    protected void onExit() {

    }

    /**
     * Override this method to initialize custom
     * main menu
     *
     * If you do override it make sure to call {@link #startGame()}
     * to start the game
     *
     * @param mainMenuRoot
     */
    protected void initMainMenu(Pane mainMenuRoot) {

    }

    /**
     * This is called AFTER all init methods complete
     * and BEFORE the main loop starts
     *
     * It is safe to use any protected fields at this stage
     */
    protected void postInit() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.finer("start()");
        initSettings(settings);

        currentWidth = settings.getWidth();
        currentHeight = settings.getHeight();

        mainStage = primaryStage;
        primaryStage.setTitle(settings.getTitle() + " " + settings.getVersion());
        primaryStage.setResizable(false);

        mainMenuRoot = new Pane();
        gameRoot = new Pane();
        uiRoot = new Pane();
        root = new Pane(gameRoot, uiRoot);
        root.setPrefSize(settings.getWidth(), settings.getHeight());

        // init all managers here before anything else
        qteManager.init();

        try {
            initAssets();
        }
        catch (Exception e) {
            log.finer("Exception occurred during initAssets() - " + e.getMessage());
            exit();
        }

        initMainMenu(mainMenuRoot);
        initGame();
        initUI(uiRoot);
        initInput();

        mainMenuScene = new Scene(mainMenuRoot);
        mainScene = new Scene(root);
        inputManager.init(mainScene);

        mainScene.addEventHandler(KeyEvent.KEY_RELEASED, qteManager::keyReleasedHandler);

        // ensure the window frame is just right for the scene size
        primaryStage.setScene(mainScene);
        primaryStage.sizeToScene();

        boolean menuEnabled = mainMenuRoot.getChildren().size() > 0;

        primaryStage.setScene(menuEnabled ? mainMenuScene : mainScene);
        primaryStage.setOnCloseRequest(event -> exit());
        primaryStage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now_) {
                processUpdate(now_);
            }
        };

        postInit();

        if (settings.isIntroEnabled()) {
            Intro intro = initIntroVideo();
            if (intro == null)
                intro = new FXGLIntro(getWidth(), getHeight());
            intro.onFinished = () -> {
                if (menuEnabled)
                    mainMenuScene.setRoot(mainMenuRoot);
                else {
                    mainScene.setRoot(root);
                    timer.start();
                }
            };

            if (menuEnabled)
                mainMenuScene.setRoot(intro);
            else
                mainScene.setRoot(intro);

            intro.startIntro();
        }
        else {
            if (!menuEnabled)
                timer.start();
        }
    }

    /**
     * This is the internal FXGL update tick,
     * executed 60 times a second ~ every 0.166 (6) seconds
     *
     * @param now_ - The timestamp of the current frame given in nanoseconds
     */
    private void processUpdate(long now_) {
        long startNanos = System.nanoTime();
        long realFPS = now_ - now;

        now = now_;

        inputManager.onUpdate(now);
        physicsManager.onUpdate(now);

        onUpdate();

        gameRoot.getChildren().addAll(tmpAddList);
        tmpAddList.clear();

        gameRoot.getChildren().removeAll(tmpRemoveList);
        tmpRemoveList.stream()
                    .filter(e -> e instanceof PhysicsEntity)
                    .map(e -> (PhysicsEntity)e)
                    .forEach(physicsManager::destroyBody);
        tmpRemoveList.forEach(entity -> entity.onClean());
        tmpRemoveList.clear();

        gameRoot.getChildren().stream().map(node -> (Entity)node).forEach(entity -> entity.onUpdate(now));

        fpsPerformance = Math.round(fpsPerformanceCounter.count(SECOND / (System.nanoTime() - startNanos)));
        fps = Math.round(fpsCounter.count(SECOND / realFPS));

        tick++;
    }

    /**
     * Sets viewport origin. Use it for camera movement
     *
     * Do NOT use if the viewport was bound
     *
     * @param x
     * @param y
     */
    protected void setViewportOrigin(int x, int y) {
        gameRoot.setLayoutX(-x);
        gameRoot.setLayoutY(-y);
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distX and distY represent bound distance between entity and viewport origin
     *
     * <pre>
     * Example:
     *
     * bindViewportOrigin(player, 640, 360);
     *
     * if the game is 1280x720, the code above centers the camera on player
     * For most platformers / side scrollers use:
     *
     * bindViewportOriginX(player, 640);
     *
     * </pre>
     *
     * @param entity
     * @param distX
     * @param distY
     */
    protected void bindViewportOrigin(Entity entity, int distX, int distY) {
        gameRoot.layoutXProperty().bind(entity.translateXProperty().negate().add(distX));
        gameRoot.layoutYProperty().bind(entity.translateYProperty().negate().add(distY));
    }

    protected void bindViewportOriginX(Entity entity, int distX) {
        gameRoot.layoutXProperty().bind(entity.translateXProperty().negate().add(distX));
    }

    protected void bindViewportOriginY(Entity entity, int distY) {
        gameRoot.layoutYProperty().bind(entity.translateYProperty().negate().add(distY));
    }

    /**
     *
     * @return  a list of ALL entities currently registered in {@link #gameRoot}
     */
    public List<Entity> getAllEntities() {
        return gameRoot.getChildren().stream()
                .map(node -> (Entity)node)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given
     * arguments
     *
     * @param types
     * @return
     */
    public List<Entity> getEntities(EntityType... types) {
        List<String> list = Arrays.asList(types).stream()
                .map(EntityType::getUniqueType)
                .collect(Collectors.toList());

        return gameRoot.getChildren().stream()
                .map(node -> (Entity)node)
                .filter(entity -> list.contains(entity.getTypeAsString()))
                .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesInRange(Rectangle2D selection, EntityType... types) {
        Entity boundsEntity = Entity.noType();
        boundsEntity.setPosition(selection.getMinX(), selection.getMinY());
        boundsEntity.setGraphics(new Rectangle(selection.getWidth(), selection.getHeight()));

        return getEntities(types).stream()
                .filter(entity -> entity.getBoundsInParent().intersects(boundsEntity.getBoundsInParent()))
                .collect(Collectors.toList());
    }

    /**
     * Add an entity/entities to the scenegraph
     *
     * This is safe to be called from any thread
     *
     * @param entities
     */
    public void addEntities(Entity... entities) {
        for (Entity e : entities) {
            if (e instanceof CombinedEntity) {
                tmpAddList.addAll(e.getChildrenUnmodifiable()
                        .stream().map(node -> (Entity)node)
                        .collect(Collectors.toList()));
            }
            else if (e instanceof PhysicsEntity) {
                physicsManager.createBody((PhysicsEntity) e);
                tmpAddList.add(e);
            }
            else
                tmpAddList.add(e);
        }
    }

    /**
     * Remove an entity from the scenegraph
     *
     * This is safe to be called from any thread
     *
     * @param entity
     */
    public void removeEntity(Entity entity) {
        tmpRemoveList.add(entity);
    }

    /**
     * Equivalent to uiRoot.getChildren().add()
     *
     * @param n
     */
    public void addUINode(Node n) {
        uiRoot.getChildren().add(n);
    }

    /**
     * Equivalent to uiRoot.getChildren().remove()
     *
     * @param n
     */
    public void removeUINode(Node n) {
        uiRoot.getChildren().remove(n);
    }



    /**
     * Call this to manually start the game when you
     * override {@link #initMainMenu(Pane)}
     */
    protected void startGame() {
        mainStage.setScene(mainScene);
        timer.start();
    }

    /**
     * Pauses the main loop execution
     */
    public void pause() {
        timer.stop();
    }

    /**
     * Resumes the main loop execution
     */
    public void resume() {
        timer.start();
    }

    /**
     * Pauses the game and opens main menu
     * Does nothing if main menu was not initialized
     */
    protected void openMainMenu() {
        if (mainMenuRoot.getChildren().size() == 0)
            return;

        timer.stop();

        // we are changing our scene so it is intuitive that
        // all input gets cleared
        inputManager.clearAllInput();
        mainStage.setScene(mainMenuScene);
    }

    /**
     * This method will be automatically called when main window is closed
     * This method will shutdown the threads and close the logger
     *
     * You can call this method when you want to quit the application manually
     * from the game
     */
    protected final void exit() {
        log.finer("Closing Normally");
        onExit();
        scheduleThread.shutdown();
        FXGLLogger.close();
        Platform.exit();
    }

    /**
     * The Runnable action will be scheduled for execution iff
     * whileCondition is initially true. If that's the case
     * then the action will be run instantly and then after given interval
     * until whileCondition becomes false
     *
     * The action will be executed on JavaFX Application Thread
     *
     * @param action
     * @param interval
     * @param whileCondition
     */
    public void runAtIntervalWhile(Runnable action, double interval, BooleanProperty whileCondition) {
        if (!whileCondition.get()) {
            return;
        }

        ScheduledFuture<?> task = scheduleThread.scheduleAtFixedRate(
                () -> Platform.runLater(action), 0, (long)interval, TimeUnit.NANOSECONDS);

        whileCondition.addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                task.cancel(false);
        });
    }

    /**
     * The Runnable action will be executed once after given delay
     *
     * The action will be executed on JavaFX Application Thread
     *
     * Do NOT queue frequent tasks
     *
     * @param action
     * @param delay
     */
    public void runOnceAfter(Runnable action, double delay) {
        scheduleThread.schedule(() -> Platform.runLater(action), (long)delay, TimeUnit.NANOSECONDS);
    }

    /**
     * Register an FXGL event
     *
     * Add specific entity types if the event should be targeting
     * a group. Otherwise all entities will be notified of the event
     *
     * @param event
     * @param types
     */
    public void postFXGLEvent(FXGLEvent event, EntityType... types) {
        if (types.length == 0) {
            gameRoot.getChildren().stream()
                .map(node -> (Entity) node)
                .forEach(e -> e.fireFXGLEvent(event));
        }
        else {
            getEntities(types).forEach(e -> e.fireFXGLEvent(event));
        }
    }

    /**
     * Saves a screenshot of the current main scene into a .png file
     *
     * @return  true if the screenshot was saved successfully, false otherwise
     */
    protected boolean saveScreenshot() {
        Image fxImage = mainScene.snapshot(null);
        BufferedImage img = SwingFXUtils.fromFXImage(fxImage, null);

        String fileName = "./" + settings.getTitle() + settings.getVersion()
                + LocalDateTime.now() + ".png";

        fileName = fileName.replace(":", "_");

        try (OutputStream os = Files.newOutputStream(Paths.get(fileName))) {
            return ImageIO.write(img, "png", os);
        }
        catch (Exception e) {
            log.finer("Exception occurred during saveScreenshot() - " + e.getMessage());
        }

        return false;
    }

    /**
     *
     * @return width of the main scene
     */
    public double getWidth() {
        return currentWidth;
    }

    /**
     *
     * @return height of the main scene
     */
    public double getHeight() {
        return currentHeight;
    }

    /**
     *
     * @return current tick since the start of game
     */
    public long getTick() {
        return tick;
    }
}
