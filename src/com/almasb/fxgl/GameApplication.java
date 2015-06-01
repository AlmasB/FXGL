package com.almasb.fxgl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.entity.CollisionHandler;
import com.almasb.fxgl.entity.CombinedEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.FXGLEvent;

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
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").warning("Unhandled Exception");
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").warning(FXGLLogger.errorTraceAsString(error));
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
    private Map<String, CollisionHandler> collisionHandlers = new HashMap<>();

    private List<Entity> tmpAddList = new ArrayList<>();
    private List<Entity> tmpRemoveList = new ArrayList<>();

    private AnimationTimer timer;
    private ScheduledExecutorService scheduleThread = Executors.newSingleThreadScheduledExecutor();

    private Map<KeyCode, Boolean> keys = new HashMap<>();
    private Map<KeyCode, Runnable> keyPressActions = new HashMap<>();
    private Map<KeyCode, Runnable> keyTypedActions = new HashMap<>();

    /**
     * Holds mouse state information
     */
    protected MouseState mouse = new MouseState();

    protected AssetManager assetManager = new AssetManager();

    /**
     * Current time in nanoseconds, equal to "now"
     * Used for convenience as "now" can't be accessed outside {@link #onUpdate(long)}
     */
    protected long currentTime = 0;

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
    protected abstract void initGame(Pane gameRoot);

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
    protected abstract void onUpdate(long now);

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

        mainStage = primaryStage;

        mainMenuRoot = new Pane();
        gameRoot = new Pane();
        uiRoot = new Pane();
        root = new Pane(gameRoot, uiRoot);
        root.setPrefSize(settings.getWidth(), settings.getHeight());

        try {
            initAssets();
        }
        catch (Exception e) {
            log.finer("Exception occurred during initAssets() - " + e.getMessage());
            exit();
        }

        initMainMenu(mainMenuRoot);
        initGame(gameRoot);
        initUI(uiRoot);
        initInput();

        mainMenuScene = new Scene(mainMenuRoot);
        mainScene = new Scene(root);
        mainScene.setOnKeyPressed(event -> {
            if (!isPressed(event.getCode()) && keyTypedActions.containsKey(event.getCode())) {
                keys.put(event.getCode(), true);
                keyTypedActions.get(event.getCode()).run();
            }
            else {
                keys.put(event.getCode(), true);
            }

        });
        mainScene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

        mainScene.setOnMousePressed(mouse::update);
        mainScene.setOnMouseDragged(mouse::update);
        mainScene.setOnMouseReleased(mouse::update);
        mainScene.setOnMouseMoved(mouse::update);

        boolean menuEnabled = mainMenuRoot.getChildren().size() > 0;

        primaryStage.setScene(menuEnabled ? mainMenuScene : mainScene);
        // 6 and 29 seem to be the frame lengths, at least on W8
        primaryStage.setWidth(settings.getWidth() + 6);
        primaryStage.setHeight(settings.getHeight() + 29);
        primaryStage.setTitle(settings.getTitle() + " " + settings.getVersion());
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            exit();
        });
        primaryStage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long startNanos = System.nanoTime();
                long realFPS = now - currentTime;

                currentTime = now;
                processInput();

                List<Entity> collidables = gameRoot.getChildren().stream()
                        .map(node -> (Entity)node)
                        .filter(entity -> entity.getProperty(Entity.PR_USE_PHYSICS))
                        .collect(Collectors.toList());

                for (int i = 0; i < collidables.size(); i++) {
                    Entity e1 = collidables.get(i);

                    for (int j = i + 1; j < collidables.size(); j++) {
                        Entity e2 = collidables.get(j);

                        String key = e1.getType() + "," + e2.getType();
                        CollisionHandler handler = collisionHandlers.get(key);
                        if (handler == null) {
                            key = e2.getType() + "," + e1.getType();
                            handler = collisionHandlers.get(key);
                        }

                        if (handler != null && e1.getBoundsInParent().intersects(e2.getBoundsInParent())) {
                            if (key.startsWith(e1.getType()))
                                handler.onCollision(e1, e2);
                            else
                                handler.onCollision(e2, e1);
                        }
                    }
                }


                onUpdate(now);

                gameRoot.getChildren().addAll(tmpAddList);
                tmpAddList.clear();

                gameRoot.getChildren().removeAll(tmpRemoveList);
                tmpRemoveList.forEach(entity -> entity.onClean());
                tmpRemoveList.clear();

                gameRoot.getChildren().stream().map(node -> (Entity)node).forEach(entity -> entity.onUpdate(now));

                fpsPerformance = Math.round(fpsPerformanceCounter.count(SECOND / (System.nanoTime() - startNanos)));
                fps = Math.round(fpsCounter.count(SECOND / realFPS));
            }
        };

        postInit();

        if (!menuEnabled)
            timer.start();
    }

    /**
     * Registers a collision handler
     * The order in which the types are passed to this method
     * decides the order of objects being passed into the collision handler
     *
     * <pre>
     * Example:
     *
     * addCollisionHandler("bullet", "enemy", (bullet, enemy) -> {
     *      // CODE CALLED ON COLLISION
     * });
     *
     * OR
     *
     * addCollisionHandler("enemy", "bullet", (enemy, bullet) -> {
     *      // CODE CALLED ON COLLISION
     * });
     *
     * </pre>
     *
     * @param typeA
     * @param typeB
     * @param handler
     */
    protected void addCollisionHandler(String typeA, String typeB, CollisionHandler handler) {
        collisionHandlers.put(typeA + "," + typeB, handler);
    }

    /**
     * Registers a collision handler
     * The order in which the types are passed to this method
     * decides the order of objects being passed into the collision handler
     *
     * @param typeA
     * @param typeB
     * @param handler
     */
    protected void addCollisionHandler(EntityType typeA, EntityType typeB, CollisionHandler handler) {
        addCollisionHandler(typeA.getUniqueType(), typeB.getUniqueType(), handler);
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
    protected List<Entity> getAllEntities() {
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
    protected List<Entity> getEntities(String... types) {
        List<String> list = Arrays.asList(types);
        return gameRoot.getChildren().stream()
                .map(node -> (Entity)node)
                .filter(entity -> list.contains(entity.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given
     * arguments
     *
     * @param types
     * @return
     */
    protected List<Entity> getEntities(EntityType... types) {
        List<String> list = Arrays.asList(types).stream()
                .map(EntityType::getUniqueType)
                .collect(Collectors.toList());

        return gameRoot.getChildren().stream()
                .map(node -> (Entity)node)
                .filter(entity -> list.contains(entity.getType()))
                .collect(Collectors.toList());
    }

    protected List<Entity> getEntitiesInRange(Rectangle2D selection, String... types) {
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
    protected void addEntities(Entity... entities) {
        for (Entity e : entities) {
            if (e instanceof CombinedEntity) {
                tmpAddList.addAll(e.getChildrenUnmodifiable()
                        .stream().map(node -> (Entity)node)
                        .collect(Collectors.toList()));
            }
            else
                tmpAddList.add(e);
        }

        //tmpAddList.addAll(Arrays.asList(entities));
    }

    /**
     * Remove an entity from the scenegraph
     *
     * This is safe to be called from any thread
     *
     * @param entity
     */
    protected void removeEntity(Entity entity) {
        tmpRemoveList.add(entity);
    }

    private void processInput() {
        keyPressActions.forEach((key, action) -> {if (isPressed(key)) action.run();});
    }

    /**
     * @param key
     * @return
     *          true iff key is currently pressed
     */
    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    /**
     * Add an action that is executed constantly
     * WHILE the key is physically pressed
     *
     * @param key
     * @param action
     */
    protected void addKeyPressBinding(KeyCode key, Runnable action) {
        keyPressActions.put(key, action);
    }

    /**
     * Add an action that is executed only ONCE
     * per single physical key press
     *
     * @param key
     * @param action
     */
    protected void addKeyTypedBinding(KeyCode key, Runnable action) {
        keyTypedActions.put(key, action);
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
    protected void pause() {
        timer.stop();
    }

    /**
     * Resumes the main loop execution
     */
    protected void resume() {
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
        keys.keySet().forEach(key -> keys.put(key, false));
        mouse.leftPressed = false;
        mouse.rightPressed = false;
        mainStage.setScene(mainMenuScene);
    }

    /**
     * This method will be automatically called when main window is closed
     * This method will shutdown the threads and close the logger
     *
     * You can call this method when you want to quit the application manually
     * from the game
     */
    protected void exit() {
        log.finer("Closing");
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
    protected void runAtIntervalWhile(Runnable action, double interval, BooleanProperty whileCondition) {
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
    protected void runOnceAfter(Runnable action, double delay) {
        scheduleThread.schedule(() -> Platform.runLater(action), (long)delay, TimeUnit.NANOSECONDS);
    }

    /**
     * Register an FXGL event
     *
     * Add specific entity types if the event should be targetting
     * a group. Otherwise all entities will be notified of the event
     *
     * @param event
     * @param types
     */
    protected void postFXGLEvent(FXGLEvent event, String... types) {
        if (types.length == 0) {
            gameRoot.getChildren().stream()
                .map(node -> (Entity) node)
                .forEach(e -> e.fireFXGLEvent(event));
        }
        else {
            getEntities(types).forEach(e -> e.fireFXGLEvent(event));
        }
    }

    public static class MouseState {
        public double x, y;
        public boolean leftPressed, rightPressed;
        private MouseEvent event;

        private void update(MouseEvent event) {
            this.event = event;
            this.x = event.getSceneX();
            this.y = event.getSceneY();
            if (leftPressed) {
                if (event.getButton() == MouseButton.PRIMARY && isReleased(event)) {
                    leftPressed = false;
                }
            }
            else {
                leftPressed = event.getButton() == MouseButton.PRIMARY && isPressed(event);
            }

            if (rightPressed) {
                if (event.getButton() == MouseButton.SECONDARY && isReleased(event)) {
                    rightPressed = false;
                }
            }
            else {
                rightPressed = event.getButton() == MouseButton.SECONDARY && isPressed(event);
            }
        }

        private boolean isPressed(MouseEvent event) {
            return event.getEventType() == MouseEvent.MOUSE_PRESSED
                    || event.getEventType() == MouseEvent.MOUSE_DRAGGED;
        }

        private boolean isReleased(MouseEvent event) {
            return event.getEventType() == MouseEvent.MOUSE_RELEASED
                    || event.getEventType() == MouseEvent.MOUSE_MOVED;
        }

        public MouseEvent getEvent() {
            return event;
        }
    }
}
