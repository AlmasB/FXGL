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
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.almasb.fxgl.effect.ParticleEntity;
import com.almasb.fxgl.entity.CombinedEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.FXGLEvent;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.ui.Menu;
import com.almasb.fxgl.util.FXGLLogger;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.util.Duration;

/**
 * Handles everything to do with modifying the scene.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public final class SceneManager extends FXGLManager {

    private static final Logger log = FXGLLogger.getLogger("SceneManager");

    //private static final int GAME_ROOT_LAYER = 0;
    private static final int UI_ROOT_LAYER = 2;

    /**
     * Root for entities
     */
    private Group gameRoot = new Group();

    private Canvas particlesCanvas = new Canvas();

    private GraphicsContext particlesGC = particlesCanvas.getGraphicsContext2D();

    /**
     * The overlay root above {@link #gameRoot}. Contains UI elements, native JavaFX nodes.
     * May also contain entities as Entity is a subclass of Parent.
     * uiRoot isn't affected by viewport movement.
     */
    private Group uiRoot = new Group();

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
     * THE root of the {@link #mainScene}. Contains {@link #gameRoot} and {@link #uiRoot} in this order.
     */
    private Pane root = new Pane(gameRoot, particlesCanvas, uiRoot);

    /**
     * Game scene
     */
    private Scene scene = new Scene(root);

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

    /*package-private*/ SceneManager() {
    }

    /*package-private*/ void init() {
        setPrefSize(app.getWidth(), app.getHeight());

        particlesCanvas.setWidth(app.getWidth());
        particlesCanvas.setHeight(app.getHeight());
        particlesCanvas.setMouseTransparent(true);

        if (app.isMenuEnabled())
            configureMenu();

        if (app.isFPSShown()) {
            Text fpsText = new Text();
            fpsText.setFill(Color.AZURE);
            fpsText.setFont(Font.font(24));
            fpsText.setTranslateY(app.getHeight() - 40);
            fpsText.textProperty().bind(app.getTimerManager().fpsProperty().asString("FPS: [%d]\n")
                    .concat(app.getTimerManager().performanceFPSProperty().asString("Performance: [%d]")));
            addUINodes(fpsText);
        }
    }

    /*package-private*/ void setPrefSize(double width, double height) {
        Rectangle2D bounds = app.isFullScreen()
                ? Screen.getPrimary().getBounds()
                : Screen.getPrimary().getVisualBounds();

        if (app.getWidth() <= bounds.getWidth()
                && app.getHeight() <= bounds.getHeight()) {
            root.setPrefSize(app.getWidth(), app.getHeight());
        }
        else {
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
        }
    }

    /*package-private*/ void configureMenu() {
        mainMenu = app.initMainMenu();
        gameMenu = app.initGameMenu();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, menuKeyPressedHandler);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, menuKeyReleasedHandler);
        scene.setRoot(mainMenu.getRoot());
        isMainMenuOpen = true;
    }

    /*package-private*/ void onStageShow() {
        if (app.isIntroEnabled()) {
            Intro intro = app.initIntroVideo();
            intro.onFinished = () -> {
                if (app.isMenuEnabled()) {
                    scene.setRoot(mainMenu.getRoot());
                }
                else {
                    app.startNewGame();
                }
            };

            scene.setRoot(intro);
            intro.startIntro();
        }
        else {
            if (!app.isMenuEnabled()) {
                app.startNewGame();
            }
        }
    }

    /**
     *
     * @return JavaFX scene
     */
    /*package-private*/ Scene getScene() {
        return scene;
    }

    /**
     * Equals user system width / target width
     */
    private double sizeRatio = 1.0;

    /**
     * Returns the size ratio of the screen
     * resolution over the target resolution
     *
     * @return
     */
    public final double getSizeRatio() {
        return sizeRatio;
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
                gameRoot.getChildren().addAll(e.getChildrenUnmodifiable()
                        .stream().map(node -> (Entity)node)
                        .collect(Collectors.toList()));
            }
            else if (e instanceof PhysicsEntity) {
                app.getPhysicsManager().createBody((PhysicsEntity) e);
                gameRoot.getChildren().add(e);
            }
            else
                gameRoot.getChildren().add(e);

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
        gameRoot.getChildren().removeAll(removeQueue);
        removeQueue.stream()
                    .filter(e -> e instanceof PhysicsEntity)
                    .map(e -> (PhysicsEntity)e)
                    .forEach(app.getPhysicsManager()::destroyBody);
        removeQueue.forEach(Entity::clean);
        removeQueue.clear();
    }

    /**
     * Called by GameApplication to update state of entities
     * and the scene graph.
     */
    @Override
    protected void onUpdate(long now) {
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
     * Sets viewport origin. Use it for camera movement
     *
     * Do NOT use if the viewport was bound
     *
     * @param x
     * @param y
     */
    public void setViewportOrigin(int x, int y) {
        gameRoot.setLayoutX(-x);
        gameRoot.setLayoutY(-y);
    }

    /**
     * Note: viewport origin, like anything in a scene, has top-left origin point
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
     * distX represent bound distance in X axis between entity and viewport origin
     *
     * @param entity
     * @param distX
     */
    public void bindViewportOriginX(Entity entity, int distX) {
        gameRoot.layoutXProperty().bind(entity.translateXProperty().negate().add(distX));
    }

    /**
     * Binds the viewport origin so that it follows the given entity
     * distY represent bound distance in Y axis between entity and viewport origin
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
    public void setCursor(String imageName, Point2D hotspot) {
        try {
            getScene().setCursor(new ImageCursor(app.getAssetManager().loadCursorImage(imageName),
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

    private boolean isMainMenuOpen = false;
    private boolean isGameMenuOpen = false;
    private boolean canSwitchGameMenu = true;

    /**
     *
     * @return true if in main menu
     */
    public boolean isMainMenuOpen() {
        return isMainMenuOpen;
    }

    /**
     *
     * @return true if game menu is open, false otherwise
     */
    public boolean isGameMenuOpen() {
        return isGameMenuOpen;
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

    private EventHandler<KeyEvent> menuKeyPressedHandler = e -> {
        if (isMainMenuOpen())
            return;

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
     * Pauses the game and opens in-game menu.
     * Does nothing if menu is disabled in settings
     */
    public void openGameMenu() {
        if (!app.isMenuEnabled())
            return;

        app.pause();
        app.getInputManager().clearAllInput();
        root.getChildren().set(UI_ROOT_LAYER, gameMenu.getRoot());

        isGameMenuOpen = true;
    }

    /**
     * Closes the game menu and resumes the game.
     * Does nothing if menu is disabled in settings
     */
    public void closeGameMenu() {
        if (!app.isMenuEnabled())
            return;

        app.getInputManager().clearAllInput();
        root.getChildren().set(UI_ROOT_LAYER, uiRoot);
        app.resume();

        isGameMenuOpen = false;
    }

    /*package-private*/ void returnFromMainMenu() {
        scene.setRoot(root);
        isMainMenuOpen = false;
    }

    /**
     * Exits the current game and opens main menu.
     * Does nothing if menu is disabled in settings
     */
    public void exitToMainMenu() {
        if (!app.isMenuEnabled())
            return;

        app.pause();
        app.getTimerManager().clearActions();

        entities.stream()
                .filter(e -> e instanceof PhysicsEntity)
                .map(e -> (PhysicsEntity)e)
                .forEach(app.getPhysicsManager()::destroyBody);
        entities.forEach(entity -> ((Entity)entity).clean());
        entities.clear();

        gameRoot.getChildren().clear();
        uiRoot.getChildren().clear();

        addQueue.clear();
        removeQueue.clear();

        app.getInputManager().clearAllInput();
        root.getChildren().set(UI_ROOT_LAYER, uiRoot);

        isGameMenuOpen = false;
        isMainMenuOpen = true;

        scene.setRoot(mainMenu.getRoot());
    }

    /**
     * Saves a screenshot of the current main scene into a ".png" file
     *
     * @return  true if the screenshot was saved successfully, false otherwise
     */
    public boolean saveScreenshot() {
        Image fxImage = scene.snapshot(null);
        BufferedImage img = SwingFXUtils.fromFXImage(fxImage, null);

        String fileName = "./" + app.getTitle() + app.getVersion()
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
