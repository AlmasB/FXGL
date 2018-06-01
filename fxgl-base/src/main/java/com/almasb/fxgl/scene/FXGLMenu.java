/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.MenuEventHandler;
import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.gameplay.GameDifficulty;
import com.almasb.fxgl.gameplay.achievement.Achievement;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.Trigger;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.TriggerView;
import com.almasb.fxgl.saving.SaveFile;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.ui.FXGLScrollPane;
import com.almasb.fxgl.ui.FXGLSpinner;
import com.almasb.fxgl.util.Consumer;
import com.almasb.fxgl.util.Language;
import com.almasb.fxgl.util.Supplier;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.app.FXGL.*;
import static com.almasb.fxgl.app.SystemPropertyKey.FXGL_VERSION;
import static com.almasb.fxgl.util.BackportKt.forEach;

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

    protected static final Logger log = Logger.get("FXGL.Menu");

    protected final GameApplication app;

    protected final MenuType type;

    protected MenuEventHandler listener;

    protected final Pane menuRoot = new Pane();
    protected final Pane contentRoot = new Pane();

    protected final MenuContent EMPTY = new MenuContent();

    public FXGLMenu(GameApplication app, MenuType type) {
        this.app = app;
        this.type = type;
        this.listener = (MenuEventHandler) app.getMenuListener();

        getContentRoot().getChildren().addAll(
                createBackground(app.getWidth(), app.getHeight()),
                createTitleView(app.getSettings().getTitle()),
                createVersionView(makeVersionString()),
                menuRoot, contentRoot);

        // we don't data-bind the name because menu subclasses
        // might use some fancy UI without Text / Label
        listener.profileNameProperty().addListener((o, oldName, newName) -> {
            if (!oldName.isEmpty()) {
                // remove last node which *should* be profile view
                getContentRoot().getChildren().remove(getContentRoot().getChildren().size() - 1);
            }

            getContentRoot().getChildren().add(createProfileView(getLocalizedString("profile.profile")+": " + newName));
        });
    }

    /**
     * Switches current active menu body to given.
     *
     * @param menuBox parent node containing menu body
     */
    protected void switchMenuTo(Node menuBox) {
        // no default implementation
    }

    /**
     * Switches current active content to given.
     *
     * @param content menu content
     */
    protected void switchMenuContentTo(Node content) {
        // no default implementation
    }

    protected abstract Button createActionButton(String name, Runnable action);
    protected abstract Button createActionButton(StringBinding name, Runnable action);

    protected Button createContentButton(String name, Supplier<MenuContent> contentSupplier) {
        return createActionButton(name, () -> switchMenuContentTo(contentSupplier.get()));
    }

    protected Button createContentButton(StringBinding name, Supplier<MenuContent> contentSupplier) {
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
        log.debug("createContentLoad()");

        ListView<SaveFile> list = getUIFactory().newListView();

        final double FONT_SIZE = 16;

        list.setCellFactory(param -> {
            return new ListCell<SaveFile>() {
                @Override
                protected void updateItem(SaveFile item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {

                        Text text = getUIFactory().newText(item.toString());
                        text.setFont(FXGLAssets.UI_MONO_FONT.newFont(FONT_SIZE));

                        setGraphic(text);
                    }
                }
            };
        });

        list.setItems(listener.getSaveLoadManager().saveFiles());
        list.prefHeightProperty().bind(Bindings.size(list.getItems()).multiply(FONT_SIZE));

        // this runs async
        listener.getSaveLoadManager().querySaveFiles();

        Button btnLoad = getUIFactory().newButton(localizedStringProperty("menu.load"));
        btnLoad.disableProperty().bind(list.getSelectionModel().selectedItemProperty().isNull());

        btnLoad.setOnAction(e -> {
            SaveFile saveFile = list.getSelectionModel().getSelectedItem();

            fireLoad(saveFile);
        });

        Button btnDelete = getUIFactory().newButton(localizedStringProperty("menu.delete"));
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
        log.debug("createContentGameplay()");

        Spinner<GameDifficulty> difficultySpinner =
                new FXGLSpinner<>(FXCollections.observableArrayList(GameDifficulty.values()));
        difficultySpinner.increment();

        app.getGameState().gameDifficultyProperty().bind(difficultySpinner.valueProperty());

        String playtime = app.getGameplay().getStats().getPlaytimeHours() + "H "
                + app.getGameplay().getStats().getPlaytimeMinutes() + "M "
                + app.getGameplay().getStats().getPlaytimeSeconds() + "S";

        return new MenuContent(
                new HBox(25, getUIFactory().newText(localizedStringProperty("menu.difficulty").concat(":")), difficultySpinner),
                new HBox(25, getUIFactory().newText(localizedStringProperty("menu.playtime").concat(":")), getUIFactory().newText(playtime))
                );

    }

    /**
     * @return menu content containing input mappings (action -> key/mouse)
     */
    protected final MenuContent createContentControls() {
        log.debug("createContentControls()");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.getColumnConstraints().add(new ColumnConstraints(100, 100, 100, Priority.ALWAYS, HPos.LEFT, true));
        grid.getRowConstraints().add(new RowConstraints(40, 40, 40, Priority.ALWAYS, VPos.CENTER, true));

        // row 0
        grid.setUserData(0);

        forEach(app.getInput().getBindings(), (action, trigger) -> addNewInputBinding(action, trigger, grid));

        ScrollPane scroll = new FXGLScrollPane(grid);
        scroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scroll.setMaxHeight(app.getHeight() / 2.5);

        HBox hbox = new HBox(scroll);
        hbox.setAlignment(Pos.CENTER);

        return new MenuContent(hbox);
    }

    private void addNewInputBinding(UserAction action, Trigger trigger, GridPane grid) {
        Text actionName = getUIFactory().newText(action.getName(), Color.WHITE, 18.0);

        TriggerView triggerView = new TriggerView(trigger);
        triggerView.triggerProperty().bind(app.getInput().triggerProperty(action));

        triggerView.setOnMouseClicked(event -> {
            Rectangle rect = new Rectangle(250, 100);
            rect.setStroke(Color.AZURE);

            Text text = getUIFactory().newText(getLocalizedString("menu.pressAnyKey"), 24);

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

                if (rebound)
                    stage.close();
            });
            scene.setOnMouseClicked(e -> {

                boolean rebound = app.getInput().rebind(action, e.getButton(), InputModifier.from(e));

                if (rebound)
                    stage.close();
            });

            stage.setScene(scene);
            stage.show();
        });

        HBox hBox = new HBox();
        hBox.setPrefWidth(100);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(triggerView);

        int controlsRow = (int) grid.getUserData();
        grid.addRow(controlsRow++, actionName, hBox);
        grid.setUserData(controlsRow);
    }

    /**
     * https://github.com/AlmasB/FXGL/issues/493
     *
     * @return menu content with video settings
     */
    protected final MenuContent createContentVideo() {
        log.debug("createContentVideo()");

        ChoiceBox<Language> languageBox = getUIFactory().newChoiceBox(FXCollections.observableArrayList(Language.values()));
        languageBox.setValue(Language.ENGLISH);

        getMenuSettings().languageProperty().bind(languageBox.valueProperty());

        VBox vbox = new VBox();

        if (getSettings().isManualResizeEnabled()) {
            Button btnFixRatio = getUIFactory().newButton(localizedStringProperty("menu.fixRatio"));
            btnFixRatio.setOnAction(e -> {
                listener.fixAspectRatio();
            });

            vbox.getChildren().add(btnFixRatio);
        }

        if (getSettings().isFullScreenAllowed()) {
            CheckBox cbFullScreen = getUIFactory().newCheckBox();
            cbFullScreen.setSelected(false);
            cbFullScreen.selectedProperty().bindBidirectional(getMenuSettings().fullScreenProperty());

            vbox.getChildren().add(new HBox(25, getUIFactory().newText(getLocalizedString("menu.fullscreen")+": "), cbFullScreen));
        }

        return new MenuContent(
                new HBox(25, getUIFactory().newText(localizedStringProperty("menu.language").concat(":")), languageBox),
                vbox
        );
    }

    /**
     * @return menu content containing music and sound volume sliders
     */
    protected final MenuContent createContentAudio() {
        log.debug("createContentAudio()");

        Slider sliderMusic = new Slider(0, 1, 1);
        sliderMusic.valueProperty().bindBidirectional(app.getAudioPlayer().globalMusicVolumeProperty());

        Text textMusic = getUIFactory().newText(localizedStringProperty("menu.music.volume").concat(": "));
        Text percentMusic = getUIFactory().newText("");
        percentMusic.textProperty().bind(sliderMusic.valueProperty().multiply(100).asString("%.0f"));

        Slider sliderSound = new Slider(0, 1, 1);
        sliderSound.valueProperty().bindBidirectional(app.getAudioPlayer().globalSoundVolumeProperty());

        Text textSound = getUIFactory().newText(localizedStringProperty("menu.sound.volume").concat(": "));
        Text percentSound = getUIFactory().newText("");
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
        log.debug("createContentCredits()");

        ScrollPane pane = new FXGLScrollPane();
        pane.setPrefWidth(app.getWidth() * 3 / 5);
        pane.setPrefHeight(app.getHeight() / 2);
        pane.setStyle("-fx-background:black;");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(pane.getPrefWidth() - 15);

        List<String> credits = new ArrayList<>(getSettings().getCredits().getList());
        credits.add("");
        credits.add("Powered by FXGL " + getProperties().getString(FXGL_VERSION));
        credits.add("Author: Almas Baimagambetov");
        credits.add("https://github.com/AlmasB/FXGL");
        credits.add("");

        for (String credit : credits) {
            vbox.getChildren().add(getUIFactory().newText(credit));
        }

        pane.setContent(vbox);

        return new MenuContent(pane);
    }

    /**
     * @return menu content containing feedback options
     */
    protected final MenuContent createContentFeedback() {
        log.debug("createContentFeedback()");

        // url is a string key defined in system.properties
        Consumer<String> openBrowser = url -> {
            getNet()
                    .openBrowserTask(getProperties().getString(url))
                    .onFailure(error -> log.warning("Error opening browser: " + error))
                    .run();
        };

        Button btnGoogle = new Button("Google Forms");
        btnGoogle.setOnAction(e -> openBrowser.accept("url.googleforms"));

        Button btnSurveyMonkey = new Button("Survey Monkey");
        btnSurveyMonkey.setOnAction(e -> openBrowser.accept("url.surveymonkey"));

        VBox vbox = new VBox(15,
                getUIFactory().newText(getLocalizedString("menu.chooseFeedback"), Color.WHEAT, 18),
                btnGoogle,
                btnSurveyMonkey);
        vbox.setAlignment(Pos.CENTER);

        return new MenuContent(vbox);
    }

    /**
     * @return menu content containing a list of achievements
     */
    protected final MenuContent createContentAchievements() {
        log.debug("createContentAchievements()");

        MenuContent content = new MenuContent();

        for (Achievement a : app.getGameplay().getAchievementManager().getAchievements()) {
            CheckBox checkBox = new CheckBox();
            checkBox.setDisable(true);
            checkBox.selectedProperty().bind(a.achievedProperty());

            Text text = getUIFactory().newText(a.getName());
            Tooltip.install(text, new Tooltip(a.getDescription()));

            HBox box = new HBox(25, text, checkBox);
            box.setAlignment(Pos.CENTER_RIGHT);

            content.getChildren().add(box);
        }

        return content;
    }

    /**
     * A generic vertical box container for menu content
     * where each element is followed by a separator.
     */
    protected static class MenuContent extends VBox {
        public MenuContent(Node... items) {

            if (items.length > 0) {
                int maxW = (int) items[0].getLayoutBounds().getWidth();

                for (Node n : items) {
                    int w = (int) n.getLayoutBounds().getWidth();
                    if (w > maxW)
                        maxW = w;
                }

                getChildren().add(createSeparator(maxW));

                for (Node item : items) {
                    getChildren().addAll(item, createSeparator(maxW));
                }
            }

            sceneProperty().addListener((o, oldScene, newScene) -> {
                if (newScene != null) {
                    onOpen();
                } else {
                    onClose();
                }
            });
        }

        private Line createSeparator(int width) {
            if (width < 5) {
                width = 200;
            }

            Line sep = new Line();
            sep.setEndX(width);
            sep.setStroke(Color.DARKGREY);
            return sep;
        }

        private Runnable onOpen = null;
        private Runnable onClose = null;

        /**
         * Set on open handler.
         *
         * @param onOpenAction method to be called when content opens
         */
        public void setOnOpen(Runnable onOpenAction) {
            this.onOpen = onOpenAction;
        }

        /**
         * Set on close handler.
         *
         * @param onCloseAction method to be called when content closes
         */
        public void setOnClose(Runnable onCloseAction) {
            this.onClose = onCloseAction;
        }

        private void onOpen() {
            if (onOpen != null)
                onOpen.run();
        }

        private void onClose() {
            if (onClose != null)
                onClose.run();
        }
    }

    /**
     * Adds a UI node.
     *
     * @param node the node to add
     */
    protected final void addUINode(Node node) {
        getContentRoot().getChildren().add(node);
    }

    /**
     * Can only be fired from main menu.
     * Starts new game.
     */
    protected final void fireNewGame() {
        log.debug("fireNewGame()");

        listener.onNewGame();
    }

    /**
     * Loads the game state from last modified save file.
     */
    protected final void fireContinue() {
        log.debug("fireContinue()");

        listener.onContinue();
    }

    /**
     * Loads the game state from previously saved file.
     *
     * @param fileName name of the saved file
     */
    protected final void fireLoad(SaveFile fileName) {
        log.debug("fireLoad()");

        listener.onLoad(fileName);
    }

    /**
     * Can only be fired from game menu.
     * Saves current state of the game with given file name.
     */
    protected final void fireSave() {
        log.debug("fireSave()");

        listener.onSave();
    }

    /**
     * @param fileName name of the save file
     */
    protected final void fireDelete(SaveFile fileName) {
        log.debug("fireDelete()");

        listener.onDelete(fileName);
    }

    /**
     * Can only be fired from game menu.
     * Will close the menu and unpause the game.
     */
    protected final void fireResume() {
        log.debug("fireResume()");

        listener.onResume();
    }

    /**
     * Can only be fired from main menu.
     * Logs out the user profile.
     */
    protected final void fireLogout() {
        log.debug("fireLogout()");

        switchMenuContentTo(EMPTY);

        listener.onLogout();
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
     * App will clean up the world/the scene and exit.
     */
    protected final void fireExit() {
        log.debug("fireExit()");

        listener.onExit();
    }

    /**
     * App will clean up the world/the scene and enter main menu.
     */
    protected final void fireExitToMainMenu() {
        log.debug("fireExitToMainMenu()");

        listener.onExitToMainMenu();
    }
}
