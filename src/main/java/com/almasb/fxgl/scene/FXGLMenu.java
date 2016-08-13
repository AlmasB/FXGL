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
package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.event.MenuDataEvent;
import com.almasb.fxgl.event.MenuEvent;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.gameplay.GameDifficulty;
import com.almasb.fxgl.input.*;
import com.almasb.fxgl.io.SaveFile;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.scene.menu.MenuEventListener;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.settings.SceneDimension;
import com.almasb.fxgl.ui.FXGLSpinner;
import com.almasb.fxgl.ui.UIFactory;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * This is a base class for main/game menus. It provides several
 * convenience methods for those who just want to extend an existing menu.
 * It also allows for implementors to build menus from scratch. Freshly
 * build menus can interact with FXGL by calling fire* methods.
 *
 * Both main and game menus <strong>should</strong> have the following items:
 * <ul>
 *     <li>Background</li>
 *     <li>Title</li>
 *     <li>Version</li>
 *     <li>Profile name</li>
 *     <li>Menu Body</li>
 *     <li>Menu Content</li>
 * </ul>
 *
 * However, in reality a menu can contain anything.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class FXGLMenu extends FXGLScene {

    /**
     * The logger.
     */
    protected static final Logger log = FXGL.getLogger("FXGL.Menu");

    protected final GameApplication app;

    protected final MenuType type;

    protected final Pane menuRoot = new Pane();
    protected final Pane contentRoot = new Pane();

    protected final MenuContent EMPTY = new MenuContent();

    public FXGLMenu(GameApplication app, MenuType type) {
        this.app = app;
        this.type = type;

        getRoot().getChildren().addAll(
                createBackground(app.getWidth(), app.getHeight()),
                createTitleView(app.getSettings().getTitle()),
                createVersionView(makeVersionString()),
                menuRoot, contentRoot);

        // we don't data-bind the name because menu subclasses
        // might use some fancy UI without Text / Label
        app.profileNameProperty().addListener((o, oldName, newName) -> {
            if (!oldName.isEmpty()) {
                // remove last node which *should* be profile view
                getRoot().getChildren().remove(getRoot().getChildren().size() - 1);
            }

            getRoot().getChildren().add(createProfileView("Profile: " + newName));
        });
    }

    /**
     * Switches current active menu body to given.
     *
     * @param menuBox parent node containing menu body
     */
    protected void switchMenuTo(Node menuBox) {}

    /**
     * Switches current active content to given.
     *
     * @param content menu content
     */
    protected void switchMenuContentTo(Node content) {}

    protected Button createActionButton(String name, Runnable action) {
        return null;
    }

    protected Button createContentButton(String name, Supplier<MenuContent> contentSupplier) {
        return createActionButton(name, () -> switchMenuContentTo(contentSupplier.get()));
    }

    /**
     * @return full version string
     */
    private String makeVersionString() {
        return "v" + app.getSettings().getVersion()
                + (app.getSettings().getApplicationMode() == ApplicationMode.RELEASE
                ? "" : "-" + app.getSettings().getApplicationMode());
    }

    /**
     * Create menu background.
     *
     * @param width width of the app
     * @param height height of the app
     * @return menu background UI object
     */
    protected abstract Node createBackground(double width, double height);

    /**
     * Create view for the app title.
     *
     * @param title app title
     * @return UI object
     */
    protected abstract Node createTitleView(String title);

    /**
     * Create view for version string.
     *
     * @param version version string
     * @return UI object
     */
    protected abstract Node createVersionView(String version);

    /**
     * Create view for profile name.
     *
     * @param profileName profile user name
     * @return UI object
     */
    protected abstract Node createProfileView(String profileName);

    /**
     * @return menu content containing list of save files and loadTask/delete buttons
     */
    protected final MenuContent createContentLoad() {
        ListView<SaveFile> list = new ListView<>();

        list.setItems(app.getSaveLoadManager().saveFiles());
        list.prefHeightProperty().bind(Bindings.size(list.getItems()).multiply(36));

        // this runs async
        app.getSaveLoadManager().querySaveFiles();

        Button btnLoad = UIFactory.newButton("LOAD");
        btnLoad.disableProperty().bind(list.getSelectionModel().selectedItemProperty().isNull());

        btnLoad.setOnAction(e -> {
            SaveFile saveFile = list.getSelectionModel().getSelectedItem();

            fireLoad(saveFile);
        });

        Button btnDelete = UIFactory.newButton("DELETE");
        btnDelete.disableProperty().bind(list.getSelectionModel().selectedItemProperty().isNull());

        btnDelete.setOnAction(e -> {
            SaveFile saveFile = list.getSelectionModel().getSelectedItem();

            fireDelete(saveFile);
        });

        HBox hbox = new HBox(50, btnLoad, btnDelete);
        hbox.setAlignment(Pos.CENTER);

        return new MenuContent(list, hbox);
    }

    /**
     * @return menu content with difficulty and playtime
     */
    protected final MenuContent createContentGameplay() {
        Spinner<GameDifficulty> difficultySpinner =
                new FXGLSpinner<>(FXCollections.observableArrayList(GameDifficulty.values()));
        difficultySpinner.increment();

        app.getGameWorld().gameDifficultyProperty().bind(difficultySpinner.valueProperty());

        return new MenuContent(new HBox(25, UIFactory.newText("DIFFICULTY:"), difficultySpinner),
                UIFactory.newText("PLAYTIME: " + app.getMasterTimer().getPlaytimeHours() + "H "
                    + app.getMasterTimer().getPlaytimeMinutes() + "M "
                    + app.getMasterTimer().getPlaytimeSeconds() + "S"));
    }

    /**
     * @return menu content containing input mappings (action -> key/mouse)
     */
    protected final MenuContent createContentControls() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(50);

        // row 0
        grid.setUserData(0);

        app.getInput().getBindings().forEach((action, trigger) -> addNewInputBinding(action, trigger, grid));

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scroll.setMaxHeight(app.getHeight() / 2);
        scroll.setStyle("-fx-background: black;");

        HBox hbox = new HBox(scroll);
        hbox.setAlignment(Pos.CENTER);

        return new MenuContent(hbox);
    }

    private void addNewInputBinding(UserAction action, Trigger trigger, GridPane grid) {
        Text actionName = UIFactory.newText(action.getName());
        Button triggerName = UIFactory.newButton(trigger.toString());

        triggerName.setOnMouseClicked(event -> {
            Rectangle rect = new Rectangle(250, 100);
            rect.setStroke(Color.AZURE);

            Text text = UIFactory.newText("PRESS ANY KEY", 24);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(getRoot().getScene().getWindow());

            Scene scene = new Scene(new StackPane(rect, text));
            scene.setOnKeyPressed(e -> {
                // ignore illegal keys, however they may be part of a different event
                // which is correctly processed further because code will be different
                if (e.getCode() == KeyCode.CONTROL
                        || e.getCode() == KeyCode.SHIFT
                        || e.getCode() == KeyCode.ALT)
                    return;

                boolean rebound = app.getInput().rebind(action, e.getCode(), InputModifier.from(e));

                if (!rebound)
                    return;

                // TODO: we manually set name here, would be nice to have data-bind
                triggerName.setText(new KeyTrigger(e.getCode(), InputModifier.from(e)).toString());
                stage.close();
            });
            scene.setOnMouseClicked(e -> {

                boolean rebound = app.getInput().rebind(action, e.getButton(), InputModifier.from(e));

                if (!rebound)
                    return;

                triggerName.setText(new MouseTrigger(e.getButton(), InputModifier.from(e)).toString());
                stage.close();
            });

            stage.setScene(scene);
            stage.show();
        });

        int controlsRow = (int) grid.getUserData();
        grid.addRow(controlsRow++, actionName, triggerName);
        grid.setUserData(controlsRow);

        GridPane.setHalignment(actionName, HPos.RIGHT);
        GridPane.setHalignment(triggerName, HPos.LEFT);
    }

    /**
     * @return menu content with video settings
     */
    protected final MenuContent createContentVideo() {
        Spinner<SceneDimension> spinner =
                new Spinner<>(FXCollections.observableArrayList(app.getDisplay().getSceneDimensions()));

        Button btnApply = UIFactory.newButton("Apply");
        btnApply.setOnAction(e -> {
            SceneDimension dimension = spinner.getValue();
            app.getDisplay().setSceneDimension(dimension);
        });

        return new MenuContent(new HBox(50, UIFactory.newText("Resolution"), spinner), btnApply);
    }

    /**
     * @return menu content containing music and sound volume sliders
     */
    protected final MenuContent createContentAudio() {
        Slider sliderMusic = new Slider(0, 1, 1);
        sliderMusic.valueProperty().bindBidirectional(app.getAudioPlayer().globalMusicVolumeProperty());

        Text textMusic = UIFactory.newText("Music Volume: ");
        Text percentMusic = UIFactory.newText("");
        percentMusic.textProperty().bind(sliderMusic.valueProperty().multiply(100).asString("%.0f"));

        Slider sliderSound = new Slider(0, 1, 1);
        sliderSound.valueProperty().bindBidirectional(app.getAudioPlayer().globalSoundVolumeProperty());

        Text textSound = UIFactory.newText("Sound Volume: ");
        Text percentSound = UIFactory.newText("");
        percentSound.textProperty().bind(sliderSound.valueProperty().multiply(100).asString("%.0f"));

        HBox hboxMusic = new HBox(15, textMusic, sliderMusic, percentMusic);
        HBox hboxSound = new HBox(15, textSound, sliderSound, percentSound);

        hboxMusic.setAlignment(Pos.CENTER_RIGHT);
        hboxSound.setAlignment(Pos.CENTER_RIGHT);

        return new MenuContent(hboxMusic, hboxSound);
    }

    /**
     * @return menu content containing a list of credits
     */
    protected final MenuContent createContentCredits() {
        ScrollPane pane = new ScrollPane();
        pane.setPrefWidth(app.getWidth() * 3 / 5);
        pane.setPrefHeight(app.getHeight() / 2);
        pane.setStyle("-fx-background:black;");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        FXGL.getSettings()
                .getCredits()
                .getList()
                .stream()
                .map(UIFactory::newText)
                .forEach(vbox.getChildren()::add);

        pane.setContent(vbox);

        return new MenuContent(pane);
    }

    /**
     * @return menu content containing a list of achievements
     */
    protected final MenuContent createContentAchievements() {
        MenuContent content = new MenuContent();

        for (Achievement a : app.getAchievementManager().getAchievements()) {
            CheckBox checkBox = new CheckBox();
            checkBox.setDisable(true);
            checkBox.selectedProperty().bind(a.achievedProperty());

            Text text = UIFactory.newText(a.getName());
            Tooltip.install(text, new Tooltip(a.getDescription()));

            HBox box = new HBox(25, text, checkBox);
            box.setAlignment(Pos.CENTER_RIGHT);

            content.getChildren().add(box);
        }

        return content;
    }

    /**
     * @return menu content containing multiplayer options
     */
    protected final MenuContent createContentMultiplayer() {
        return new MenuContent(UIFactory.newText("TODO: MULTIPLAYER"));
    }

    /**
     * A generic vertical box container for menu content
     * where each element is followed by a separator.
     */
    protected static class MenuContent extends VBox {
        public MenuContent(Node... items) {

            if (items.length > 0) {
                int maxW = Arrays.asList(items)
                        .stream()
                        .mapToInt(n -> (int) n.getLayoutBounds().getWidth())
                        .max()
                        .orElse(0);

                getChildren().add(createSeparator(maxW));

                for (Node item : items) {
                    getChildren().addAll(item, createSeparator(maxW));
                }
            }
        }

        private Line createSeparator(int width) {
            Line sep = new Line();
            sep.setEndX(width);
            sep.setStroke(Color.DARKGREY);
            return sep;
        }

        public double getLayoutHeight() {
            return 10 * getChildren().size();
        }
    }

    /**
     * Adds a UI node.
     *
     * @param node the node to add
     */
    protected final void addUINode(Node node) {
        getRoot().getChildren().add(node);
    }

    private MenuEventListener listener;

    /**
     * Set main listener for menu events.
     *
     * @param listener menu listener
     */
    public void setListener(MenuEventListener listener) {
        this.listener = listener;
    }

    private void fireMenuEvent(Event event) {
        app.getEventBus().fireEvent(event);
    }

    /**
     * Fires {@link MenuEvent#NEW_GAME} event.
     * Can only be fired from main menu.
     * Starts new game.
     */
    protected final void fireNewGame() {
        log.debug("fireNewGame()");

        listener.onNewGame();
        fireMenuEvent(new MenuEvent(MenuEvent.NEW_GAME));
    }

    /**
     * Fires {@link MenuEvent#CONTINUE} event.
     * Lads the game state from last modified save file.
     */
    protected final void fireContinue() {
        log.debug("fireContinue()");

        listener.onContinue();
        fireMenuEvent(new MenuEvent(MenuEvent.CONTINUE));
    }

    /**
     * Fires {@link MenuDataEvent#LOAD} event.
     * Loads the game state from previously saved file.
     *
     * @param fileName  name of the saved file
     */
    protected final void fireLoad(SaveFile fileName) {
        log.debug("fireLoad()");

        listener.onLoad(fileName);
        //fireMenuEvent(new MenuDataEvent(MenuDataEvent.LOAD, fileName));
    }

    /**
     * Fires {@link MenuEvent#SAVE} event.
     * Can only be fired from game menu. Saves current state of the game with given file name.
     */
    protected final void fireSave() {
        log.debug("fireSave()");

        listener.onSave();
        fireMenuEvent(new MenuEvent(MenuEvent.SAVE));
    }

    /**
     * Fires {@link MenuDataEvent#DELETE} event.
     *
     * @param fileName name of the save file
     */
    protected final void fireDelete(SaveFile fileName) {
        log.debug("fireDelete()");

        listener.onDelete(fileName);
        //fireMenuEvent(new MenuDataEvent(MenuDataEvent.DELETE, fileName));
    }

    /**
     * Fires {@link MenuEvent#RESUME} event.
     * Can only be fired from game menu.
     * Will close the menu and unpause the game.
     */
    protected final void fireResume() {
        log.debug("fireResume()");

        listener.onResume();
        fireMenuEvent(new MenuEvent(MenuEvent.RESUME));
    }

    /**
     * Fires {@link MenuEvent#LOGOUT} event.
     * Can only be fired from main menu.
     * Logs out the user profile.
     */
    protected final void fireLogout() {
        log.debug("fireLogout()");

        switchMenuContentTo(EMPTY);

        listener.onLogout();
        fireMenuEvent(new MenuEvent(MenuEvent.LOGOUT));
    }

    /**
     * Call multiplayer access in main menu.
     * Currently not supported.
     */
    protected final void fireMultiplayer() {
        log.debug("fireMultiplayer()");

        listener.onMultiplayer();
    }

    /**
     * Fire {@link MenuEvent#EXIT} event.
     * App will clean up the world/the scene and exit.
     */
    protected final void fireExit() {
        log.debug("fireExit()");

        listener.onExit();
        fireMenuEvent(new MenuEvent(MenuEvent.EXIT));
    }

    /**
     * Fire {@link MenuEvent#EXIT_TO_MAIN_MENU} event.
     * App will clean up the world/the scene and enter main menu.
     */
    protected final void fireExitToMainMenu() {
        log.debug("fireExitToMainMenu()");

        listener.onExitToMainMenu();
        fireMenuEvent(new MenuEvent(MenuEvent.EXIT_TO_MAIN_MENU));
    }
}
