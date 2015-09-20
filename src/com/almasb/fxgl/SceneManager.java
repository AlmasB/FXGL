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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.effect.ParticleEntity;
import com.almasb.fxgl.entity.CombinedEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.FXGLEvent;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.event.MenuEvent;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.ui.FXGLDialogBox;
import com.almasb.fxgl.ui.FXGLMenuFactory;
import com.almasb.fxgl.ui.Intro;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.UpdateTickListener;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Dialog;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Handles everything to do with modifying the scene.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public final class SceneManager implements UpdateTickListener {

    private static final Logger log = FXGLLogger.getLogger("FXGL.SceneManager");

    /**
     * Root for entities, it is affected by viewport movement.
     */
    private Group gameRoot = new Group();

    /**
     * Canvas for particles to accelerate drawing.
     */
    private Canvas particlesCanvas = new Canvas();

    /**
     * Graphics context for drawing particles.
     */
    private GraphicsContext particlesGC = particlesCanvas.getGraphicsContext2D();

    /**
     * The overlay root above {@link #gameRoot}. Contains UI elements, native JavaFX nodes.
     * May also contain entities as Entity is a subclass of Parent.
     * uiRoot isn't affected by viewport movement.
     */
    private Group uiRoot = new Group();

    /**
     * THE root of the {@link #gameScene}. Contains {@link #gameRoot}, {@link #particlesCanvas}
     * and {@link #uiRoot} in this order.
     */
    private Pane root = new Pane(gameRoot, particlesCanvas, uiRoot);

    /*
     * FXGL Scene Graph
     *
     * Scenes: gameScene <-> mainMenu <-> gameMenu
     *            |
     *         gameRoot (entities)
     *         (render layers from low to high index)
     *            |
     *         particles layers
     *            |
     *         uiRoot
     */

    /**
     * Game scene
     */
    private Scene gameScene = new Scene(root);

    /**
     * Main menu, this is the menu shown at the start of game
     */
    private Scene mainMenu;

    /**
     * In-game menu, this is shown when menu key pressed during the game
     */
    private Scene gameMenu;

    /**
     * Is menu enabled in settings
     */
    private boolean isMenuEnabled;

    /**
     * The key that triggers opening/closing game menu
     */
    private KeyCode menuKey = KeyCode.ESCAPE;

    /**
     * The dialog box used to communicate with the user.
     */
    private FXGLDialogBox dialogBox;

    /**
     * List of entities currently in the scene graph.
     */
    private List<Entity> entities = new ArrayList<>();

    /**
     * List of entities waiting to be added to scene graph.
     */
    private List<Entity> addQueue = new ArrayList<>();

    /**
     * List of entities waiting to be removed from scene graph.
     */
    private List<Entity> removeQueue = new ArrayList<>();

    /**
     * Game application instance.
     */
    private GameApplication app;

    /**
     * Main stage.
     */
    private Stage stage;

    /**
     * FXGL CSS.
     */
    private String fxglCSS;

    /**
     * Constructs scene manager.
     *
     * @param app instance of game application
     * @param stage main stage
     */
    /*package-private*/ SceneManager(GameApplication app, Stage stage) {
        this.app = app;
        this.stage = stage;

        try {
            fxglCSS = AssetManager.INSTANCE.loadCSS("fxgl_dark.css");
            root.getStylesheets().add(fxglCSS);
            dialogBox = UIFactory.getDialogBox();
            dialogBox.setOnShown(e -> {
                if (!menuOpenProperty().get())
                    app.pause();

                app.getInputManager().clearAllInput();
            });
            dialogBox.setOnHidden(e -> {
                if (!menuOpenProperty().get())
                    app.resume();
            });
        }
        catch (Exception e) {
            log.warning("Failed to init FXGL scene manager style. Using defaults. Error: " + e.getMessage());
        }

        isMenuEnabled = app.getSettings().isMenuEnabled();
        menuOpen = new ReadOnlyBooleanWrapper(isMenuEnabled);

        setPrefSize(app.getWidth(), app.getHeight());

        particlesCanvas.setWidth(app.getWidth());
        particlesCanvas.setHeight(app.getHeight());
        particlesCanvas.setMouseTransparent(true);
    }

    /**
     * Set preferred size to game scene root and stage.
     * Computes {@link #sizeRatio} and scales the root
     * if necessary
     *
     * @param width
     * @param height
     */
    private void setPrefSize(double width, double height) {
        Rectangle2D bounds = app.getSettings().isFullScreen()
                ? Screen.getPrimary().getBounds()
                : Screen.getPrimary().getVisualBounds();

        if (app.getWidth() <= bounds.getWidth()
                && app.getHeight() <= bounds.getHeight()) {
            root.setPrefSize(app.getWidth(), app.getHeight());
        }
        else {
            log.finer("App size > screen size");

            double ratio = app.getWidth() * 1.0 / app.getHeight();

            for (int newWidth = (int)bounds.getWidth(); newWidth > 0; newWidth--) {
                if (newWidth / ratio <= bounds.getHeight()) {
                    root.setPrefSize(newWidth, (int)(newWidth / ratio));

                    double newSizeRatio = newWidth * 1.0 / app.getWidth();
                    root.getTransforms().clear();
                    root.getTransforms().add(new Scale(newSizeRatio, newSizeRatio));
                    sizeRatio = newSizeRatio;
                    break;
                }
            }

            log.finer("Size ratio: " + sizeRatio);
        }

        stage.setScene(gameScene);
        stage.sizeToScene();
    }

    private boolean canSwitchGameMenu = true;

    /**
     * Applies FXGL CSS to menu roots.
     * Scales menu roots appropriately based on {@link #sizeRatio}.
     * Registers event handlers to menus.
     */
    private void configureMenu() {
        menuOpenProperty().addListener((obs, oldState, newState) -> {
            if (newState.booleanValue()) {
                log.finer("Playing State -> Menu State");
                app.onMenuOpen();
            }
            else {
                log.finer("Menu State -> Playing State");
                app.onMenuClose();
            }
        });

        FXGLMenuFactory menuFactory = app.initMenuFactory();

        mainMenu = new Scene(menuFactory.newMainMenu(app));
        gameMenu = new Scene(menuFactory.newGameMenu(app));

        if (sizeRatio != 1.0) {
            log.finer("Scaing menu scenes with ratio: " + sizeRatio);
            mainMenu.getRoot().getTransforms().add(new Scale(sizeRatio, sizeRatio));
            gameMenu.getRoot().getTransforms().add(new Scale(sizeRatio, sizeRatio));
        }
        mainMenu.getStylesheets().add(fxglCSS);
        gameMenu.getStylesheets().add(fxglCSS);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (isMenuEnabled && event.getCode() == menuKey && canSwitchGameMenu) {
                openGameMenu();
                canSwitchGameMenu = false;
            }
        });
        gameScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == menuKey)
                canSwitchGameMenu = true;
        });


        gameMenu.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == menuKey && canSwitchGameMenu) {
                closeGameMenu();
                canSwitchGameMenu = false;
            }
        });
        gameMenu.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == menuKey)
                canSwitchGameMenu = true;
        });


        mainMenu.addEventHandler(MenuEvent.NEW_GAME, event -> {
            app.startNewGame();
            setScene(gameScene);
        });
        mainMenu.addEventHandler(MenuEvent.LOAD, event -> {
            String saveFileName = event.getData().map(name -> (String)name).orElse("");
            if (!saveFileName.isEmpty()) {
                try {
                    Serializable data = SaveLoadManager.INSTANCE.load(saveFileName);
                    clearSceneGraph();
                    app.loadState(data);
                    app.startNewGame();
                    setScene(gameScene);
                }
                catch (Exception e) {
                    log.warning("Failed to load save data: " + e.getMessage());
                    showMessageBox("Failed to load save data: " + e.getMessage());
                }
            }
            else {
                SaveLoadManager.INSTANCE.loadLastModifiedFile().ifPresent(data -> {
                    clearSceneGraph();
                    app.loadState((Serializable) data);
                    app.startNewGame();
                    setScene(gameScene);
                });
            }
        });

        mainMenu.addEventHandler(MenuEvent.EXIT, event -> {
            app.exit();
        });

        gameMenu.addEventHandler(MenuEvent.RESUME, event -> {
            this.closeGameMenu();
        });
        gameMenu.addEventHandler(MenuEvent.SAVE, event -> {
            String saveFileName = event.getData().map(name -> (String)name).orElse("");
            if (!saveFileName.isEmpty()) {
                try {
                    SaveLoadManager.INSTANCE.save(app.saveState(), saveFileName);
                }
                catch (Exception e) {
                    log.warning("Failed to save game data: " + e.getMessage());
                    showMessageBox("Failed to save game data: " + e.getMessage());
                }
            }
        });
        gameMenu.addEventHandler(MenuEvent.LOAD, event -> {
            String saveFileName = event.getData().map(name -> (String)name).orElse("");
            if (!saveFileName.isEmpty()) {
                try {
                    Serializable data = SaveLoadManager.INSTANCE.load(saveFileName);
                    clearSceneGraph();
                    app.loadState(data);
                    app.startNewGame();
                    setScene(gameScene);
                }
                catch (Exception e) {
                    log.warning("Failed to load save data: " + e.getMessage());
                    showMessageBox("Failed to load save data: " + e.getMessage());
                }
            }
            else {
                SaveLoadManager.INSTANCE.loadLastModifiedFile().ifPresent(data -> {
                    clearSceneGraph();
                    app.loadState((Serializable) data);
                    app.startNewGame();
                    setScene(gameScene);
                });
            }
        });
        gameMenu.addEventHandler(MenuEvent.EXIT, event -> {
            this.exitToMainMenu();
        });
    }

    /**
     * Called right after the main stage is shown.
     */
    /*package-private*/ void onStageShow() {
        if (isMenuEnabled)
            configureMenu();

        if (app.getSettings().isIntroEnabled()) {
            log.finer("Intro is enabled");
            Intro intro = app.initIntroVideo();
            intro.getTransforms().add(new Scale(sizeRatio, sizeRatio));
            intro.setOnFinished(() -> {
                gameScene.setRoot(root);

                if (isMenuEnabled) {
                    setScene(mainMenu);
                }
                else {
                    app.startNewGame();
                    setScene(gameScene);
                }
            });

            gameScene.setRoot(intro);
            intro.startIntro();
        }
        else {
            log.finer("Intro not enabled");
            if (isMenuEnabled) {
                setScene(mainMenu);
            }
            else {
                app.startNewGame();
                setScene(gameScene);
            }
        }

        log.finer("Scene size: " + stage.getScene().getWidth() + "," + stage.getScene().getHeight());
        log.finer("Stage size: " + stage.getWidth() + "," + stage.getHeight());
    }

    /**
     * Changes current scene to given scene.
     *
     * @param scene
     */
    private void setScene(Scene scene) {
        menuOpen.set(scene == mainMenu || scene == gameMenu);

        stage.setScene(scene);
    }

    /**
     * Equals user system width / target width.
     */
    private double sizeRatio = 1.0;

    /**
     * Returns the size ratio of the screen
     * resolution over the target resolution
     *
     * @return
     */
    public double getSizeRatio() {
        return sizeRatio;
    }

    /**
     *
     * @return game scene
     */
    public Scene getGameScene() {
        return gameScene;
    }

    /**
     * Returns render group for entity based on entity's
     * render layer. If no such group exists, a new group
     * will be created for that layer and placed
     * in the scene graph according to its layer index.
     *
     * @param e
     * @return
     */
    private Group getRenderLayerFor(Entity e) {
        Integer renderLayer = e.getRenderLayer().index();
        Group group = gameRoot.getChildren()
                .stream()
                .filter(n -> (int)n.getUserData() == renderLayer)
                .findAny()
                .map(n -> (Group)n)
                .orElse(new Group());


        if (group.getUserData() == null) {
            log.finer("Creating render group for layer: " + e.getRenderLayer().asString());
            group.setUserData(renderLayer);
            gameRoot.getChildren().add(group);
        }

        List<Node> tmpGroups = new ArrayList<>(gameRoot.getChildren());
        tmpGroups.sort((g1, g2) -> (int)g1.getUserData() - (int)g2.getUserData());

        gameRoot.getChildren().setAll(tmpGroups);

        return group;
    }

    /**
     * Add entity(-ies) to the scene graph.
     * The entity(-ies) will be added in the next tick.
     *
     * @param entity to add
     * @param entities to add
     */
    public void addEntities(Entity entity, Entity... entities) {
        addQueue.add(entity);
        for (Entity e : entities)
            addQueue.add(e);
    }

    /**
     * Remove given entity from the scene graph.
     * The entity will be removed in the next tick.
     *
     * @param entity to remove
     */
    public void removeEntity(Entity entity) {
        removeQueue.add(entity);
    }

    /**
     * Add a node to the UI overlay.
     *
     * @param n
     * @param nodes
     */
    public void addUINodes(Node node, Node... nodes) {
        uiRoot.getChildren().add(node);
        uiRoot.getChildren().addAll(nodes);
    }

    /**
     * Remove given node from the UI overlay.
     *
     * @param n
     */
    public void removeUINode(Node n) {
        uiRoot.getChildren().remove(n);
    }

    /**
     * Returns a list of entities whose type matches given
     * arguments. If no arguments were given, returns list
     * of ALL entities currently registered in the scene graph.
     *
     * @param types
     * @return
     */
    public List<Entity> getEntities(EntityType... types) {
        if (types.length == 0)
            return new ArrayList<>(entities);

        List<String> list = Arrays.asList(types).stream()
                .map(EntityType::getUniqueType)
                .collect(Collectors.toList());

        return entities.stream()
                .filter(entity -> list.contains(entity.getTypeAsString()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given arguments and
     * which are partially or entirely
     * in the specified rectangular selection.
     *
     * If no arguments were given, a list of all entities satisfying the
     * requirement is returned.
     *
     * @param selection Rectangle2D that describes the selection box
     * @param types
     * @return
     */
    public List<Entity> getEntitiesInRange(Rectangle2D selection, EntityType... types) {
        Entity boundsEntity = Entity.noType();
        boundsEntity.setPosition(selection.getMinX(), selection.getMinY());
        boundsEntity.setGraphics(new Rectangle(selection.getWidth(), selection.getHeight()));

        return getEntities(types).stream()
                .filter(entity -> entity.getBoundsInParent().intersects(boundsEntity.getBoundsInParent()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given arguments and
     * which have the given render layer index
     *
     * If no arguments were given, a list of all entities satisfying the
     * requirement (i.e. render layer) is returned.
     *
     * @param layer
     * @param types
     * @return
     */
    public List<Entity> getEntitiesByLayer(RenderLayer layer, EntityType... types) {
        return getEntities(types).stream()
                .filter(e -> e.getRenderLayer().index() == layer.index())
                .collect(Collectors.toList());
    }

    /**
     * Returns the closest entity to the given entity with given type.
     * If no types were specified, the closest entity is returned. The given
     * entity itself is never returned.
     *
     * If there no entities satisfying the requirement, {@link Optional#empty()}
     * is returned.
     *
     * @param entity
     * @param types
     * @return
     */
    public Optional<Entity> getClosestEntity(Entity entity, EntityType... types) {
        return getEntities(types).stream()
                .filter(e -> e != entity)
                .sorted((e1, e2) -> (int)e1.distance(entity) - (int)e2.distance(entity))
                .findFirst();
    }

    /**
     * Returns an entity at given position. The position x and y
     * must equal to entity's position x and y.
     *
     * Returns {@link Optional#empty()} if no entity was found at
     * given position.
     *
     * @param position
     * @return
     */
    public Optional<Entity> getEntityAt(Point2D position) {
        return app.getSceneManager()
                    .getEntities()
                    .stream()
                    .filter(e -> e.getPosition().equals(position))
                    .findAny();
    }

    /**
     * This is where we actually add the entities to the scene graph,
     * which were pushed
     * to waiting queue by {@link #addEntities(Entity...)}
     * in the previous tick. We also clear the queue.
     */
    private void registerPendingEntities() {
        for (Entity e : addQueue) {
            entities.add(e);

            // TODO: check combined
            if (e instanceof CombinedEntity) {
                getRenderLayerFor(e).getChildren().addAll(e.getChildrenUnmodifiable()
                        .stream().map(node -> (Entity)node)
                        .collect(Collectors.toList()));
            }
            else if (e instanceof PhysicsEntity) {
                app.getPhysicsManager().createBody((PhysicsEntity) e);
                getRenderLayerFor(e).getChildren().add(e);
            }
            else {
                getRenderLayerFor(e).getChildren().add(e);
            }

            Duration expire = e.getExpireTime();
            if (expire != Duration.ZERO)
                app.getTimerManager().runOnceAfter(() -> removeEntity(e), expire);
        }

        addQueue.clear();
    }

    /**
     * This is where we actually remove the entities from the scene graph,
     * which were pushed to waiting queue for removal by {@link #removeEntity(Entity)}
     * in the previous tick.
     * If entity is a PhysicsEntity, its physics properties get destroyed.
     * Finally, entity's onClean() will be called
     *
     * We also clear the queue.
     */
    private void removePendingEntities() {
        entities.removeAll(removeQueue);

        removeQueue.forEach(e -> {
            getRenderLayerFor(e).getChildren().remove(e);
        });

        removeQueue.stream()
                    .filter(e -> e instanceof PhysicsEntity)
                    .map(e -> (PhysicsEntity)e)
                    .forEach(app.getPhysicsManager()::destroyBody);
        removeQueue.forEach(Entity::clean);
        removeQueue.clear();
    }

    /**
     * Cleans all registered entities. Clears add and remove queues.
     * Clears gameRoot and uiRoot.
     */
    private void clearSceneGraph() {
        entities.stream()
            .filter(e -> e instanceof PhysicsEntity)
            .map(e -> (PhysicsEntity) e)
            .forEach(app.getPhysicsManager()::destroyBody);

        entities.forEach(entity -> ((Entity) entity).clean());
        entities.clear();

        gameRoot.getChildren().clear();
        uiRoot.getChildren().clear();

        addQueue.clear();
        removeQueue.clear();
    }

    /**
     * Called by GameApplication to update state of entities
     * and the scene graph.
     */
    @Override
    public void onUpdate(long now) {
        registerPendingEntities();
        removePendingEntities();

        particlesGC.setGlobalAlpha(1);
        particlesGC.setGlobalBlendMode(BlendMode.SRC_OVER);
        particlesGC.clearRect(0, 0, app.getWidth(), app.getHeight());

        entities.forEach(e -> {
            e.update(now);

            if (e instanceof ParticleEntity) {
                ((ParticleEntity)e).renderParticles(particlesGC, getViewportOrigin());
            }
        });
    }

    /**
     * Sets viewport origin. Use it for camera movement.
     *
     * Do NOT use if the viewport was bound.
     *
     * @param x
     * @param y
     */
    public void setViewportOrigin(int x, int y) {
        gameRoot.setLayoutX(-x);
        gameRoot.setLayoutY(-y);
    }

    /**
     * Note: viewport origin, like anything in a scene, has top-left origin point.
     *
     * @return viewport origin
     */
    public Point2D getViewportOrigin() {
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
    public void bindViewportOrigin(Entity entity, int distX, int distY) {
        gameRoot.layoutXProperty().bind(entity.translateXProperty().negate().add(distX));
        gameRoot.layoutYProperty().bind(entity.translateYProperty().negate().add(distY));
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distX represent bound distance in X axis between entity and viewport origin.
     *
     * @param entity
     * @param distX
     */
    public void bindViewportOriginX(Entity entity, int distX) {
        gameRoot.layoutXProperty().bind(entity.translateXProperty().negate().add(distX));
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distY represent bound distance in Y axis between entity and viewport origin.
     *
     * @param entity
     * @param distY
     */
    public void bindViewportOriginY(Entity entity, int distY) {
        gameRoot.layoutYProperty().bind(entity.translateYProperty().negate().add(distY));
    }

    /**
     * Set true if UI elements should forward mouse events
     * to the game layer
     *
     * @param b
     * @defaultValue false
     */
    public void setUIMouseTransparent(boolean b) {
        uiRoot.setMouseTransparent(b);
    }

    /**
     * Sets global game cursor using given name to find
     * the image cursor within assets/ui/cursors/.
     * Hotspot is location of the pointer end on the image.
     *
     * @param imageName
     * @param hotspot
     */
    public void setGameCursor(String imageName, Point2D hotspot) {
        try {
            gameScene.setCursor(new ImageCursor(app.getAssetManager().loadCursorImage(imageName),
                    hotspot.getX(), hotspot.getY()));
        }
        catch (Exception e) {
            log.warning("Failed to set cursor: " + e.getMessage());
        }
    }

    /**
     * Fires an FXGL event on all entities whose type
     * matches given arguments. If types were not given,
     * fires an FXGL event on all entities registered in the scene graph.
     *
     * @param event
     * @param types
     */
    public void fireFXGLEvent(FXGLEvent event, EntityType... types) {
        getEntities(types).forEach(e -> e.fireFXGLEvent(event));
    }

    private ReadOnlyBooleanWrapper menuOpen;

    /**
     *
     * @return property tracking if any is open
     */
    public ReadOnlyBooleanProperty menuOpenProperty() {
        return menuOpen.getReadOnlyProperty();
    }

    /**
     *
     * @return true if any menu is open
     */
    public boolean isMenuOpen() {
        return menuOpen.get();
    }

    /**
     * Set the key which will open/close game menu.
     *
     * @param key
     * @defaultValue KeyCode.ESCAPE
     */
    public void setMenuKey(KeyCode key) {
        menuKey = key;
    }

    /**
     * Pauses the game and opens in-game menu.
     * Does nothing if menu is disabled in settings
     */
    private void openGameMenu() {
        app.pause();
        setScene(gameMenu);
    }

    /**
     * Closes the game menu and resumes the game.
     * Does nothing if menu is disabled in settings
     */
    private void closeGameMenu() {
        setScene(gameScene);
        app.resume();
    }

    /**
     * Exits the current game and opens main menu.
     * Does nothing if menu is disabled in settings
     */
    private void exitToMainMenu() {
        app.pause();
        app.getTimerManager().clearActions();

        clearSceneGraph();

        setScene(mainMenu);
    }

    /**
     * Shows given dialog and blocks execution of the game
     * until the dialog is dismissed. The provided callback
     * will be called with the dialog result as parameter when the dialog
     * closes.
     *
     * @param dialog
     * @param resultCallback
     */
    public <T> void showDialog(Dialog<T> dialog, Consumer<T> resultCallback) {
        boolean paused = menuOpenProperty().get();

        if (!paused)
            app.pause();

        app.getInputManager().clearAllInput();

        dialog.initOwner(gameScene.getWindow());
        dialog.setOnCloseRequest(e -> {
            if (!paused)
                app.resume();

            resultCallback.accept(dialog.getResult());
        });
        dialog.show();
    }

    /**
     * Shows a blocking (stops game execution) message box
     * with OK button. On button press, the message box
     * will be dismissed.
     *
     * @param message the message to show
     */
    public void showMessageBox(String message) {
        dialogBox.showMessageBox(message);
    }

    /**
     * Shows a blocking message box with YES and NO buttons.
     * The callback is invoked with the user answer as parameter.
     *
     * @param message
     * @param resultCallback
     */
    public void showConfirmationBox(String message, Consumer<Boolean> resultCallback) {
        dialogBox.showConfirmationBox(message, resultCallback);
    }

    /**
     * Shows a blocking message box with OK button and input field.
     * The callback is invoked with the field text as parameter.
     *
     * @param message
     * @param resultCallback
     */
    public void showInputBox(String message, Consumer<String> resultCallback) {
        dialogBox.showInputBox(message, resultCallback);
    }

    /**
     * Saves a screenshot of the current main scene into a ".png" file
     *
     * @return  true if the screenshot was saved successfully, false otherwise
     */
    public boolean saveScreenshot() {
        Image fxImage = gameScene.snapshot(null);
        BufferedImage img = SwingFXUtils.fromFXImage(fxImage, null);

        String fileName = "./" + app.getSettings().getTitle() + app.getSettings().getVersion()
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
}
