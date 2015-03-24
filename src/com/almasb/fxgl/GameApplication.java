package com.almasb.fxgl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.almasb.fxgl.entity.CollisionHandler;
import com.almasb.fxgl.entity.Entity;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class GameApplication extends Application {

    static {
        FXGLLogger.init(Level.ALL);
        Version.print();
    }

    /**
     * A second in nanoseconds
     */
    protected static final long SECOND = 1000000000;

    private GameSettings settings = new GameSettings();

    private Pane root, gameRoot, uiRoot;
    private Map<String, CollisionHandler> collisionHandlers = new HashMap<>();

    private AnimationTimer timer;

    private Map<KeyCode, Boolean> keys = new HashMap<>();
    private Map<KeyCode, Runnable> keyPressActions = new HashMap<>();
    private Map<KeyCode, Runnable> keyTypedActions = new HashMap<>();

    protected AssetManager assetManager = new AssetManager();
    protected long currentTime = 0;

    protected abstract void initSettings(GameSettings settings);
    protected abstract void initGame(Pane gameRoot);
    protected abstract void initUI(Pane uiRoot);
    protected abstract void onUpdate(long now);

    @Override
    public void start(Stage primaryStage) throws Exception {
        initSettings(settings);

        gameRoot = new Pane();
        uiRoot = new Pane();
        root = new Pane(gameRoot, uiRoot);
        root.setPrefSize(settings.getWidth(), settings.getHeight());

        initGame(gameRoot);
        initUI(uiRoot);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (!isPressed(event.getCode()) && keyTypedActions.containsKey(event.getCode())) {
                keyTypedActions.get(event.getCode()).run();
            }

            keys.put(event.getCode(), true);
        });
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

        primaryStage.setScene(scene);
        primaryStage.setTitle(settings.getTitle() + " " + settings.getVersion());
        primaryStage.setResizable(false);
        primaryStage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                currentTime = now;
                processInput();

                List<Entity> collidables = gameRoot.getChildren().stream()
                        .map(node -> (Entity)node)
                        .filter(entity -> entity.getProperty("usePhysics"))
                        .collect(Collectors.toList());

                for (int i = 0; i < collidables.size(); i++) {
                    Entity e1 = collidables.get(i);

                    for (int j = i + 1; j < collidables.size(); j++) {
                        Entity e2 = collidables.get(j);

                        String key = makeCollisionKey(e1.getType(), e2.getType());
                        CollisionHandler handler = collisionHandlers.get(key);

                        if (handler != null && e1.getBoundsInParent().intersects(e2.getBoundsInParent())) {
                            if (key.startsWith(e1.getType()))
                                handler.onCollision(e1, e2);
                            else
                                handler.onCollision(e2, e1);
                        }
                    }
                }

                onUpdate(now);
                gameRoot.getChildren().stream().map(node -> (Entity)node).forEach(entity -> entity.onUpdate(now));
            }
        };
        timer.start();
    }

    private String makeCollisionKey(String a, String b) {
        String key;
        if (a.compareTo(b) <= 0) {
            key = a + "," + b;
        }
        else {
            key = b + "," + a;
        }
        return key;
    }

    protected void addCollisionHandler(String typeA, String typeB, CollisionHandler handler) {
        collisionHandlers.put(makeCollisionKey(typeA, typeB), handler);
    }

    protected void setViewportOrigin(int x, int y) {
        gameRoot.setLayoutX(-x);
        gameRoot.setLayoutY(-y);
    }

    protected List<Entity> getEntities(String type) {
        return gameRoot.getChildren().stream()
                .map(node -> (Entity)node)
                .filter(entity -> entity.getType().equals(type))
                .collect(Collectors.toList());
    }

    protected void addEntities(Entity... entities) {
        gameRoot.getChildren().addAll(entities);
    }

    protected void removeEntity(Entity entity) {
        gameRoot.getChildren().remove(entity);
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

    protected void addKeyPressBinding(KeyCode key, Runnable action) {
        keyPressActions.put(key, action);
    }

    protected void addKeyTypedBinding(KeyCode key, Runnable action) {
        keyTypedActions.put(key, action);
    }

    protected void pause() {
        timer.stop();
    }

    protected void resume() {
        timer.start();
    }

    protected void exit() {
        Platform.exit();
    }
}
