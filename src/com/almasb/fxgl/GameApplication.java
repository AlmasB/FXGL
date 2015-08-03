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
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.effect.ParticleManager;
import com.almasb.fxgl.entity.CombinedEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.FXGLEvent;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.QTEManager;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.physics.PhysicsManager;
import com.almasb.fxgl.ui.FXGLGameMenu;
import com.almasb.fxgl.ui.FXGLMainMenu;
import com.almasb.fxgl.ui.Menu;
import com.almasb.fxgl.util.FPSCounter;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.TimerAction;
import com.almasb.fxgl.util.TimerAction.TimerType;
import com.almasb.fxgl.util.Version;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * To use FXGL extend this class and implement necessary methods.
 *
 * Unless explicitly stated, methods are not thread-safe and must be
 * executed on JavaFX Application Thread.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
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

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGL.GameApplication");

    /**
     * A second in nanoseconds
     */
    public static final long SECOND = 1000000000;

    /**
     * Time per single frame in nanoseconds
     */
    public static final long TIME_PER_FRAME = SECOND / 60;

    /**
     * A minute in nanoseconds
     */
    public static final long MINUTE = 60 * SECOND;

    /**
     * Settings for this game instance
     */
    private GameSettings settings = new GameSettings();

    /*
     * All scene graph roots. Although uiRoot is drawn on top of gameRoot,
     * they are at the same level in the scene graph.
     *
     *                  mainStage
     *                      |
     *                      |
     *                  mainScene
     *                      |
     *                      |
     *                    root         <--> (mainMenu root)
     *                   /    \
     *                  /      \
     *              gameRoot   uiRoot  <--> (gameMenu root)
     *
     */
    /**
     * The root for entities (game objects)
     */
    private Pane gameRoot = new Pane();

    /**
     * The overlay root above {@link #gameRoot}. Contains UI elements, native JavaFX nodes.
     * May also contain entities as Entity is a subclass of Parent.
     * uiRoot isn't affected by viewport movement.
     */
    private Group uiRoot = new Group();

    /**
     * THE root of the {@link #mainScene}. Contains {@link #gameRoot} and {@link #uiRoot} in this order.
     */
    private Pane root = new Pane(gameRoot, uiRoot);

    /**
     * Main menu, this is the menu shown at the start of game
     */
    private Menu mainMenu;

    /**
     * In-game menu, this is shown when menu key pressed during the game
     */
    private Menu gameMenu;

    /**
     * The key that triggers opening/closing game menu
     */
    private KeyCode menuKey = KeyCode.ESCAPE;

    /**
     * Game scene
     */
    private Scene mainScene = new Scene(root);

    /**
     * Game window
     */
    private Stage mainStage;

    /**
     * These are current width and height of the scene
     * NOT the window
     *
     * TODO: we don't need these, set the scene size and use that instead.
     */
    private double currentWidth, currentHeight;

    private List<Entity> tmpAddList = new ArrayList<>();
    private List<Entity> tmpRemoveList = new ArrayList<>();

    /**
     * The main loop timer
     */
    private AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long internalTime) {
            processUpdate(internalTime);
        }
    };

    /**
     * List for all timer based actions
     */
    private List<TimerAction> timerActions = new CopyOnWriteArrayList<>();

    /*
     * Various managers that handle different aspects of the application
     */
    protected final InputManager inputManager = new InputManager(this);

    /**
     * Used for loading various assets
     */
    protected final AssetManager assetManager = AssetManager.INSTANCE;

    protected final PhysicsManager physicsManager = new PhysicsManager(this);

    protected final ParticleManager particleManager = new ParticleManager(this);

    protected final QTEManager qteManager = new QTEManager(this);

    protected final SaveLoadManager saveLoadManager = SaveLoadManager.INSTANCE;

    /**
     * Default random number generator
     */
    protected final Random random = new Random();

    /**
     * Current time for this tick in nanoseconds. Also time elapsed
     * from the start of game. This time does not change while the game is paused
     */
    protected long now = 0;

    /**
     * Current tick. It is also number of ticks since start of game
     */
    protected long tick = 0;

    /**
     * These are used to approximate FPS value
     */
    private FPSCounter fpsCounter = new FPSCounter();
    private FPSCounter fpsPerformanceCounter = new FPSCounter();

    /**
     * Used as delta from internal JavaFX timestamp to calculate render FPS
     */
    private long fpsTime = 0;

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
     * Override to use your custom main menu
     *
     * @return
     */
    protected Menu initMainMenu() {
        return new FXGLMainMenu(this);
    }

    /**
     * Override to use your custom game menu
     *
     * @return
     */
    protected Menu initGameMenu() {
        return new FXGLGameMenu(this);
    }

    /**
     * Initialize game assets, such as Texture, AudioClip, Music
     *
     * @throws Exception
     */
    protected abstract void initAssets() throws Exception;

    /**
     * Called when user selects "save" from menu
     *
     * Default implementation returns null
     *
     * @return data with required info about current state
     */
    public Serializable saveState() {
        log.warning("Called saveState(), but it wasn't overriden!");
        return null;
    }

    /**
     * Called when user selects "continue" or "load" from menu
     *
     * @param data
     */
    public void loadState(Serializable data) {
        log.warning("Called loadState(), but it wasn't overriden!");
    }

    /**
     * Initialize game objects
     */
    protected abstract void initGame();

    /**
     * Initiliaze collision handlers, physics properties
     */
    protected abstract void initPhysics();

    /**
     * Initiliaze UI objects
     */
    protected abstract void initUI();

    /**
     * Initiliaze input, i.e.
     * bind key presses / key typed, bind mouse
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
     * This is called AFTER all init methods complete
     * and BEFORE the main loop starts
     *
     * It is safe to use any protected fields at this stage
     */
    protected void postInit() {

    }

    /**
     * Set preferred sizes to roots and set
     * stage properties
     */
    private void applySettings() {
        currentWidth = settings.getWidth();
        currentHeight = settings.getHeight();

        Rectangle2D bounds = settings.isFullScreen() ? Screen.getPrimary().getBounds() : Screen.getPrimary().getVisualBounds();

        if (settings.getWidth() <= bounds.getWidth()
                && settings.getHeight() <= bounds.getHeight()) {
            root.setPrefSize(settings.getWidth(), settings.getHeight());
        }
        else {
            double ratio = settings.getWidth() * 1.0 / settings.getHeight();

            for (int newWidth = (int)bounds.getWidth(); newWidth > 0; newWidth--) {
                if (newWidth / ratio <= bounds.getHeight()) {
                    root.setPrefSize(newWidth, (int)(newWidth / ratio));

                    double newSizeRatio = newWidth * 1.0 / settings.getWidth();
                    root.getTransforms().add(new Scale(newSizeRatio, newSizeRatio));
                    sizeRatio = newSizeRatio;
                    break;
                }
            }
        }

        mainStage.setTitle(settings.getTitle() + " " + settings.getVersion());
        mainStage.setResizable(false);

        try {
            String iconName = settings.getIconFileName();
            if (!iconName.isEmpty()) {
                Image icon = assetManager.loadAppIcon(iconName);
                mainStage.getIcons().add(icon);
            }
        }
        catch (Exception e) {
            log.warning("Failed to load app icon: " + e.getMessage());
        }

        // ensure the window frame is just right for the scene size
        mainStage.setScene(mainScene);
        mainStage.sizeToScene();

        if (settings.isFullScreen()) {
            mainStage.setFullScreenExitHint("");
            // we don't want the user to be able to exit full screen manually
            // but only through settings menu
            // so we set key combination to something obscure which isn't likely to be pressed
            mainStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("Shortcut+>"));
            mainStage.setFullScreen(true);
        }
    }

    /**
     * Ensure managers are of legal state and ready
     */
    private void initManagers() {
        // we do this to be able to request focus
        root.setFocusTraversable(true);
        gameRoot.setFocusTraversable(true);
        gameRoot.requestFocus();

        inputManager.init(mainScene);
        qteManager.init();
        mainScene.addEventHandler(KeyEvent.KEY_RELEASED, qteManager::keyReleasedHandler);
    }

    /**
     * Initialize user application
     */
    private void initApp() {
        try {
            initAssets();
            initGame();
            initPhysics();
            initUI();
            initInput();

            postInit();
        }
        catch (Exception e) {
            log.severe("Exception occurred during initialization: " + e.getMessage());

            Arrays.asList(e.getStackTrace())
                .stream()
                .map(StackTraceElement::toString)
                .filter(s -> !s.contains("Unknown Source") && !s.contains("Native Method"))
                .map(s -> "Cause: " + s)
                .forEachOrdered(log::severe);
            exit();
        }
    }

    /**
     * Opens and shows the actual window. Configures what parts of scenes
     * need to be shown and in which
     * order based on the subclass implementation of certain init methods
     */
    private void configureAndShowStage() {
        boolean menuEnabled = settings.isMenuEnabled();

        if (menuEnabled) {
            mainScene.addEventHandler(KeyEvent.KEY_PRESSED, menuKeyPressedHandler);
            mainScene.addEventHandler(KeyEvent.KEY_RELEASED, menuKeyReleasedHandler);
            mainScene.setRoot(mainMenu.getRoot());
        }

        mainStage.setOnCloseRequest(event -> exit());
        mainStage.show();

        if (settings.isIntroEnabled()) {
            Intro intro = initIntroVideo();
            intro.onFinished = () -> {
                if (menuEnabled) {
                    mainScene.setRoot(mainMenu.getRoot());
                }
                else {
                    startNewGame();
                }
            };

            mainScene.setRoot(intro);
            intro.startIntro();
        }
        else {
            if (!menuEnabled) {
                startNewGame();
            }
        }
    }

    @Override
    public final void init() {
        instance = this;
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        log.finer("start()");
        // capture the reference to primaryStage so we can access it
        mainStage = primaryStage;

        initSettings(settings);
        applySettings();

        initManagers();

        mainMenu = initMainMenu();
        gameMenu = initGameMenu();

        configureAndShowStage();
    }

    /**
     * This is the internal FXGL update tick,
     * executed 60 times a second ~ every 0.166 (6) seconds
     *
     * @param internalTime - The timestamp of the current frame given in nanoseconds (from JavaFX)
     */
    private void processUpdate(long internalTime) {
        long startNanos = System.nanoTime();
        long realFPS = internalTime - fpsTime;
        fpsTime = internalTime;

        timerActions.forEach(action -> action.update(now));
        timerActions.removeIf(TimerAction::isExpired);

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
        now += TIME_PER_FRAME;
    }

    /**
     * Call this to manually start the game.
     * To be used ONLY in menus.
     */
    public final void startNewGame() {
        initApp();

        tick = 0;
        now = 0;

        mainScene.setRoot(root);
        timer.start();
    }

    /**
     * Pauses the main loop execution
     */
    public final void pause() {
        timer.stop();
    }

    /**
     * Resumes the main loop execution
     */
    public final void resume() {
        timer.start();
    }

    /**
     * Exits the current game and opens main menu
     * Does nothing if menu is disabled in settings
     */
    public final void exitToMainMenu() {
        if (!settings.isMenuEnabled())
            return;

        pause();

        timerActions.clear();
        gameRoot.getChildren().stream()
                .filter(e -> e instanceof PhysicsEntity)
                .map(e -> (PhysicsEntity)e)
                .forEach(physicsManager::destroyBody);
        gameRoot.getChildren().forEach(entity -> ((Entity)entity).onClean());

        uiRoot.getChildren().clear();

        tmpAddList.clear();
        tmpRemoveList.clear();

        inputManager.clearAllInput();

        root.getChildren().remove(gameMenu.getRoot());
        root.getChildren().add(uiRoot);
        isGameMenuOpen = false;

        mainScene.setRoot(mainMenu.getRoot());
        mainMenu.getRoot().requestFocus();
    }

    private boolean isGameMenuOpen = false;
    private boolean canSwitchGameMenu = true;

    private EventHandler<KeyEvent> menuKeyPressedHandler = e -> {
        if (e.getCode() == menuKey) {
            if (canSwitchGameMenu) {
                if (isGameMenuOpen) {
                    closeGameMenu();
                }
                else {
                    openGameMenu();
                }
                canSwitchGameMenu = false;
            }
        }
    };

    private EventHandler<KeyEvent> menuKeyReleasedHandler = e -> {
        if (e.getCode() == menuKey) {
            canSwitchGameMenu = true;
        }
    };

    /**
     * Pauses the game and opens in-game menu
     * Does nothing if menu is disabled in settings
     */
    public final void openGameMenu() {
        if (!settings.isMenuEnabled())
            return;

        pause();

        inputManager.clearAllInput();
        root.getChildren().remove(uiRoot);
        root.getChildren().add(gameMenu.getRoot());
        gameMenu.getRoot().requestFocus();

        isGameMenuOpen = true;
    }

    /**
     * Closes the game menu and resumes the game
     * Does nothing if menu is disabled in settings
     */
    public final void closeGameMenu() {
        if (!settings.isMenuEnabled())
            return;

        inputManager.clearAllInput();
        root.getChildren().remove(gameMenu.getRoot());
        root.getChildren().add(uiRoot);
        gameRoot.requestFocus();

        resume();

        isGameMenuOpen = false;
    }

    /**
     * This method will be automatically called when main window is closed.
     *
     * You can call this method when you want to quit the application manually
     * from the game
     */
    public final void exit() {
        log.finer("Closing Normally");
        onExit();
        FXGLLogger.close();
        Platform.exit();
    }

    /**
     * Set the key which will open/close game menu.
     * By default it's KeyCode.ESCAPE
     *
     * @param key
     */
    protected final void setMenuKey(KeyCode key) {
        menuKey = key;
    }

    /**
     * Sets viewport origin. Use it for camera movement
     *
     * Do NOT use if the viewport was bound
     *
     * @param x
     * @param y
     */
    public final void setViewportOrigin(int x, int y) {
        gameRoot.setLayoutX(-x);
        gameRoot.setLayoutY(-y);
    }

    /**
     * Note: viewport origin, like anything in a scene, has top-left origin point
     *
     * @return viewport origin
     */
    public final Point2D getViewportOrigin() {
        return new Point2D(-gameRoot.getLayoutX(), -gameRoot.getLayoutY());
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distX and distY represent bound distance between entity and viewport origin
     *
     * <pre>
     * Example:
     *
     * bindViewportOrigin(player, (int) (getWidth() / 2), (int) (getHeight() / 2));
     *
     * the code above centers the camera on player
     * For most platformers / side scrollers use:
     *
     * bindViewportOriginX(player, (int) (getWidth() / 2));
     *
     * </pre>
     *
     * @param entity
     * @param distX
     * @param distY
     */
    protected final void bindViewportOrigin(Entity entity, int distX, int distY) {
        gameRoot.layoutXProperty().bind(entity.translateXProperty().negate().add(distX));
        gameRoot.layoutYProperty().bind(entity.translateYProperty().negate().add(distY));
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distX represent bound distance in X axis between entity and viewport origin
     *
     * @param entity
     * @param distX
     */
    protected final void bindViewportOriginX(Entity entity, int distX) {
        gameRoot.layoutXProperty().bind(entity.translateXProperty().negate().add(distX));
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distY represent bound distance in Y axis between entity and viewport origin
     *
     * @param entity
     * @param distY
     */
    protected final void bindViewportOriginY(Entity entity, int distY) {
        gameRoot.layoutYProperty().bind(entity.translateYProperty().negate().add(distY));
    }

    /**
     * Set true if UI elements should forward mouse events
     * to the game layer
     *
     * @param b
     * @defaultValue false
     */
    protected final void setUIMouseTransparent(boolean b) {
        uiRoot.setMouseTransparent(b);
    }

    /**
     *
     * @return  a list of ALL entities currently registered in the application
     */
    public final List<Entity> getAllEntities() {
        return gameRoot.getChildren().stream()
                .map(node -> (Entity)node)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given
     * arguments
     *
     * @param type
     * @param types
     * @return
     */
    public final List<Entity> getEntities(EntityType type, EntityType... types) {
        List<String> list = Arrays.asList(types).stream()
                .map(EntityType::getUniqueType)
                .collect(Collectors.toList());
        list.add(type.getUniqueType());

        return gameRoot.getChildren().stream()
                .map(node -> (Entity)node)
                .filter(entity -> list.contains(entity.getTypeAsString()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given arguments and
     * which are partially or entirely
     * in the specified rectangular selection.
     *
     * @param selection Rectangle2D that describes the selection box
     * @param type
     * @param types
     * @return
     */
    public final List<Entity> getEntitiesInRange(Rectangle2D selection, EntityType type, EntityType... types) {
        Entity boundsEntity = Entity.noType();
        boundsEntity.setPosition(selection.getMinX(), selection.getMinY());
        boundsEntity.setGraphics(new Rectangle(selection.getWidth(), selection.getHeight()));

        return getEntities(type, types).stream()
                .filter(entity -> entity.getBoundsInParent().intersects(boundsEntity.getBoundsInParent()))
                .collect(Collectors.toList());
    }

    /**
     * Add an entity/entities to the scene graph.
     *
     * @param entities to add
     */
    public final void addEntities(Entity... entities) {
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

            double expire = e.getExpireTime();
            if (expire > 0)
                runOnceAfter(() -> removeEntity(e), expire);
        }
    }

    /**
     * Remove given entity from the scene graph.
     *
     * @param entity to remove
     */
    public final void removeEntity(Entity entity) {
        tmpRemoveList.add(entity);
    }

    /**
     * Add a node to the UI overlay.
     *
     * @param n
     * @param nodes
     */
    public final void addUINodes(Node n, Node... nodes) {
        uiRoot.getChildren().add(n);
        uiRoot.getChildren().addAll(nodes);
    }

    /**
     * Remove given node from the UI overlay.
     *
     * @param n
     */
    public final void removeUINode(Node n) {
        uiRoot.getChildren().remove(n);
    }

    /**
     * The Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval.
     *
     * Note: the scheduled action will not run while the game is paused.
     *
     * @param action the action
     * @param interval time in nanoseconds
     */
    public final void runAtInterval(Runnable action, double interval) {
        timerActions.add(new TimerAction(now, interval, action, TimerType.INDEFINITE));
    }

    /**
     * The Runnable action will be scheduled for execution iff
     * whileCondition is initially true. If that's the case
     * then the Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval
     *
     * The action will be removed from schedule when whileCondition becomes {@code false}.
     *
     * Note: the scheduled action will not run while the game is paused
     *
     * @param action
     * @param interval
     * @param whileCondition
     */
    public final void runAtIntervalWhile(Runnable action, double interval, BooleanProperty whileCondition) {
        if (!whileCondition.get()) {
            return;
        }
        TimerAction act = new TimerAction(now, interval, action, TimerType.INDEFINITE);
        timerActions.add(act);

        whileCondition.addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                act.expire();
        });
    }

    /**
     * The Runnable action will be executed once after given delay
     *
     * Note: the scheduled action will not run while the game is paused
     *
     * @param action
     * @param delay
     */
    public final void runOnceAfter(Runnable action, double delay) {
        timerActions.add(new TimerAction(now, delay, action, TimerType.ONCE));
    }

    /**
     * Fires an FXGL event on all entities whose type
     * matches given arguments
     *
     * @param event
     * @param type
     * @param types
     */
    public final void fireFXGLEvent(FXGLEvent event, EntityType type, EntityType... types) {
        getEntities(type, types).forEach(e -> e.fireFXGLEvent(event));
    }

    /**
     * Fires an FXGL event on all entities registered in the application
     *
     * @param event
     */
    public final void fireFXGLEvent(FXGLEvent event) {
        getAllEntities().forEach(e -> e.fireFXGLEvent(event));
    }

    /**
     * Saves a screenshot of the current main scene into a ".png" file
     *
     * @return  true if the screenshot was saved successfully, false otherwise
     */
    public final boolean saveScreenshot() {
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
    public final double getWidth() {
        return currentWidth;
    }

    /**
     *
     * @return height of the main scene
     */
    public final double getHeight() {
        return currentHeight;
    }

    /**
     * Returns the visual area within the application window,
     * excluding window borders.
     *
     * Equivalent to new Rectangle2D(0, 0, getWidth(), getHeight()).
     *
     * @return screen bounds
     */
    public final Rectangle2D getScreenBounds() {
        return new Rectangle2D(0, 0, getWidth(), getHeight());
    }

    /**
     *
     * @return current tick since the start of game
     */
    public final long getTick() {
        return tick;
    }

    /**
     *
     * @return current time since start of game in nanoseconds
     */
    public final long getNow() {
        return now;
    }

    /**
     *
     * @return pre-configured settings before game started
     */
    public final GameSettings getSettings() {
        return settings;
    }

    /**
     *
     * @return true if game menu is open, false otherwise
     */
    public boolean isGameMenuOpen() {
        return isGameMenuOpen;
    }

    private double sizeRatio = 1.0;

    public final double getSizeRatio() {
        return sizeRatio;
    }

    private static GameApplication instance;

    /**
     * This will be set during initialization, AFTER instance fields and BEFORE initSettings().
     * Internally FXGL modules must NOT rely on this.
     *
     * @return singleton instance of the current game application
     */
    public static final GameApplication getInstance() {
        return instance;
    }
}
