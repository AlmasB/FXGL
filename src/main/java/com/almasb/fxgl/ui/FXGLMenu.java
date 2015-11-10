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
package com.almasb.fxgl.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.event.InputBinding;
import com.almasb.fxgl.event.MenuEvent;
import com.almasb.fxgl.settings.SceneSettings;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.Version;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Subclass of Pane that can be populated to work as
 * a main/game menu
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class FXGLMenu extends FXGLScene {

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGLMenu");

    protected GameApplication app;
    private double menuX, menuY;

    private List<String> credits = new ArrayList<>();

    public FXGLMenu(GameApplication app, SceneSettings settings) {
        super(settings);
        this.app = app;

        populateCredits();

        MenuBox menu = createMenuBody();
        menuX = 50;
        menuY = app.getHeight() / 2 - menu.getLayoutHeight() / 2;

        // just a placeholder
        MenuBox menuContent = new MenuBox((int) app.getWidth() - 300 - 50);
        menuContent.setTranslateX(300);
        menuContent.setTranslateY(menu.getTranslateY());
        menuContent.setVisible(false);

        Title title = new Title(app.getSettings().getTitle());
        title.setTranslateX(app.getWidth() / 2 - title.getLayoutWidth() / 2);
        title.setTranslateY(menu.getTranslateY() / 2 - title.getLayoutHeight() / 2);

        Text version = UIFactory.newText("v" + app.getSettings().getVersion());
        version.setTranslateY(app.getHeight() - 2);

        getRoot().getChildren().addAll(createBackground(), title, version, menu, menuContent);
    }

    private void populateCredits() {
        addCredit("Powered by FXGL " + Version.getAsString());
        addCredit("Graphics Framework: JavaFX " + Version.getJavaFXAsString());
        addCredit("Physics Engine: JBox2d (jbox2d.org) " + Version.getJBox2DAsString());
        addCredit("FXGL Author: Almas Baimagambetov (AlmasB)");
        addCredit("https://github.com/AlmasB/FXGL");
    }

    protected Node createBackground() {
        Rectangle bg = new Rectangle(app.getWidth(), app.getHeight());
        bg.setFill(Color.rgb(10, 1, 1));
        return bg;
    }

    protected abstract MenuBox createMenuBody();

    /**
     *
     * @return menu content containing list of save files and load/delete buttons
     */
    protected final MenuContent createContentLoad() {
        ListView<String> list = new ListView<>();
        SaveLoadManager.INSTANCE.loadFileNames().ifPresent(names -> list.getItems().setAll(names));
        list.prefHeightProperty().bind(Bindings.size(list.getItems()).multiply(36));

        if (list.getItems().size() > 0) {
            list.getSelectionModel().selectFirst();
        }

        MenuItem btnLoad = new MenuItem("LOAD");
        btnLoad.setOnAction(e -> {
            String fileName = list.getSelectionModel().getSelectedItem();
            if (fileName == null)
                return;

            fireLoad(fileName);
        });
        MenuItem btnDelete = new MenuItem("DELETE");
        btnDelete.setOnAction(e -> {
            String fileName = list.getSelectionModel().getSelectedItem();
            if (fileName == null)
                return;

            UIFactory.getDialogBox().showMessageBox(SaveLoadManager.INSTANCE.delete(fileName)
                    ? "File was deleted" : "File couldn't be deleted");

            list.getItems().remove(fileName);
        });

        HBox hbox = new HBox(50, btnLoad, btnDelete);
        hbox.setAlignment(Pos.CENTER);

        return new MenuContent(list, hbox);
    }

    /**
     *
     * @return menu content containing input mappings (action -> key/mouse)
     */
    protected final MenuContent createContentControls() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(50);
        grid.setUserData(0);

        // add listener for new ones
        app.getInputManager().getBindings().addListener((ListChangeListener.Change<? extends InputBinding> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(binding -> addNewInputBinding(binding, grid));
                }
            }
        });

        // register current ones
        app.getInputManager().getBindings().forEach(binding -> addNewInputBinding(binding, grid));

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scroll.setMaxHeight(app.getHeight() / 2);
        scroll.setStyle("-fx-background: black;");

        HBox hbox = new HBox(scroll);
        hbox.setAlignment(Pos.CENTER);

        return new MenuContent(hbox);
    }

    private void addNewInputBinding(InputBinding binding, GridPane grid) {
        Text actionName = UIFactory.newText(binding.getAction().getName());

        Button triggerName = UIFactory.newButton("");
        triggerName.textProperty().bind(binding.triggerNameProperty());
        triggerName.setOnMouseClicked(event -> {
            Rectangle rect = new Rectangle(250, 100);
            rect.setStroke(Color.AZURE);

            Text text = UIFactory.newText("PRESS ANY KEY", 24);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(getRoot().getScene().getWindow());

            Scene scene = new Scene(new StackPane(rect, text));
            scene.setOnKeyPressed(e -> {
                app.getInputManager().rebind(binding.getAction(), e.getCode());
                stage.close();
            });
            scene.setOnMouseClicked(e -> {
                app.getInputManager().rebind(binding.getAction(), e.getButton());
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
     *
     * @return menu content containing music and sound volume sliders
     */
    protected final MenuContent createContentAudio() {
        Slider sliderMusic = new Slider(0, 1, 1);
        app.getAudioManager().globalMusicVolumeProperty().bindBidirectional(sliderMusic.valueProperty());

        Text textMusic = UIFactory.newText("Music Volume: ");
        Text percentMusic = UIFactory.newText("");
        percentMusic.textProperty().bind(sliderMusic.valueProperty().multiply(100).asString("%.0f"));

        Slider sliderSound = new Slider(0, 1, 1);
        app.getAudioManager().globalSoundVolumeProperty().bindBidirectional(sliderSound.valueProperty());

        Text textSound = UIFactory.newText("Sound Volume: ");
        Text percentSound = UIFactory.newText("");
        percentSound.textProperty().bind(sliderSound.valueProperty().multiply(100).asString("%.0f"));

        return new MenuContent(new HBox(textMusic, sliderMusic, percentMusic),
                new HBox(textSound, sliderSound, percentSound));
    }

    protected MenuBox createOptionsMenu() {
        MenuItem itemControls = new MenuItem("CONTROLS");
        itemControls.setMenuContent(createContentControls());

        MenuItem itemVideo = new MenuItem("VIDEO");
        MenuItem itemAudio = new MenuItem("AUDIO");
        itemAudio.setMenuContent(createContentAudio());

        return new MenuBox(200, itemControls, itemVideo, itemAudio);
    }

    protected MenuBox createExtraMenu() {
        MenuItem itemCredits = new MenuItem("CREDITS");
        itemCredits.setMenuContent(createContentCredits());

        return new MenuBox(200, itemCredits);
    }

    /**
     * Add a single line of credit text.
     *
     * @param text the text to append to credits list
     */
    protected final void addCredit(String text) {
        credits.add(text);
    }

    /**
     *
     * @return menu content containing a list of credits
     */
    protected final MenuContent createContentCredits() {
        return new MenuContent(credits.stream()
                .map(UIFactory::newText)
                .collect(Collectors.toList())
                .toArray(new Text[0]));
    }

    private void switchMenuTo(MenuBox menu) {
        Node oldMenu = getRoot().getChildren().get(3);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), oldMenu);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            menu.setTranslateX(menuX);
            menu.setTranslateY(menuY);
            menu.setOpacity(0);
            getRoot().getChildren().set(3, menu);
            oldMenu.setOpacity(1);

            FadeTransition ft2 = new FadeTransition(Duration.seconds(0.33), menu);
            ft2.setToValue(1);
            ft2.play();
        });
        ft.play();
    }

    private void switchMenuContentTo(MenuContent content) {
        content.setTranslateX(menuX * 2 + 200);
        content.setTranslateY(menuY);
        getRoot().getChildren().set(4, content);
    }

    protected static class Title extends StackPane {
        private Text text;

        public Title(String name) {
            text = UIFactory.newText(name, 50);

            Rectangle bg = new Rectangle(text.getLayoutBounds().getWidth() + 20, 60);
            bg.setStroke(Color.WHITE);
            bg.setStrokeWidth(2);
            bg.setFill(null);

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg, text);
        }

        public double getLayoutWidth() {
            return text.getLayoutBounds().getWidth() + 20;
        }

        public double getLayoutHeight() {
            return text.getLayoutBounds().getHeight() + 20;
        }
    }

    protected static class MenuBox extends VBox {
        public MenuBox(int width, MenuItem... items) {
            getChildren().add(createSeparator(width));

            for (MenuItem item : items) {
                item.setParent(this);
                getChildren().addAll(item, createSeparator(width));
            }
        }

        private Line createSeparator(int width) {
            Line sep = new Line();
            sep.setEndX(width);
            sep.setStroke(Color.DARKGREY);
            return sep;
        }

        public double getLayoutWidth() {
            return 200;
        }

        // TODO: FIX
        public double getLayoutHeight() {
            return 10 * getChildren().size();
        }
    }

    protected class MenuItem extends FXGLButton {
        private MenuBox parent;
        @SuppressWarnings("unused")
        private MenuBox child;
        private MenuContent menuContent;

        public MenuItem(String name) {
            super(name);
        }

        public void setParent(MenuBox menu) {
            parent = menu;
        }

        public void setMenuContent(MenuContent content) {
            menuContent = content;
            this.addEventHandler(ActionEvent.ACTION, event -> {
                switchMenuContentTo(menuContent);
            });
        }

        public void setChild(MenuBox menu) {
            child = menu;

            MenuItem back = new MenuItem("BACK");
            menu.getChildren().add(0, back);

            back.addEventHandler(ActionEvent.ACTION, event -> {
                switchMenuTo(MenuItem.this.parent);
            });

            this.addEventHandler(ActionEvent.ACTION, event -> {
                switchMenuTo(menu);
            });
        }

        public MenuBox getMenuParent() {
            return parent;
        }

        public MenuContent getMenuContent() {
            return menuContent;
        }

        public void setEnabled(boolean b) {
            this.setDisable(!b);
            this.setOpacity(b ? 1 : 0.33);
        }
    }

    protected class MenuContent extends VBox {
        public MenuContent(Node... items) {
            getChildren().add(createSeparator((int) app.getWidth() - 300 - 50));

            for (Node item : items) {
                getChildren().addAll(item, createSeparator((int) app.getWidth() - 300 - 50));
            }
        }

        private Line createSeparator(int width) {
            Line sep = new Line();
            sep.setEndX(width);
            sep.setStroke(Color.DARKGREY);
            return sep;
        }
    }

    protected void setContent(Node content) {

    }

    /**
     * Creates a new button with given name that performs given action on click/press.
     *
     * @param name  button name
     * @param action button action
     * @return new button
     */
    protected final Button createActionButton(String name, Runnable action) {
        Button btn = UIFactory.newButton(name);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    /**
     * Creates a new button with given name that sets given content on click/press.
     *
     * @param name  button name
     * @param content button content
     * @return new button
     */
    protected final Button createContentButton(String name, MenuContent content) {
        Button btn = UIFactory.newButton(name);
        btn.setUserData(content);
        btn.setOnAction(e -> setContent((Node)btn.getUserData()));
        return btn;
    }

    /**
     * Fires {@link MenuEvent#NEW_GAME} event.
     * Can only be fired from main menu.
     * Starts new game.
     */
    protected final void fireNewGame() {
        fireEvent(new MenuEvent(MenuEvent.NEW_GAME));
    }

    /**
     * Fires {@link MenuEvent#LOAD} event.
     * Lads the game state from last modified save file.
     */
    protected final void fireContinue() {
        fireEvent(new MenuEvent(MenuEvent.LOAD));
    }

    /**
     * Fires {@link MenuEvent#LOAD} event.
     * Loads the game state from previously saved file.
     *
     * @param fileName  name of the saved file
     */
    protected final void fireLoad(String fileName) {
        fireEvent(new MenuEvent(MenuEvent.LOAD, fileName));
    }

    /**
     * Fires {@link MenuEvent#SAVE} event.
     * Can only be fired from game menu. Saves current state of the game with given file name.
     *
     * @param fileName  name of the save file
     */
    protected final void fireSave(String fileName) {
        fireEvent(new MenuEvent(MenuEvent.SAVE, fileName));
    }

    /**
     * Fires {@link MenuEvent#RESUME} event.
     * Can only be fired from game menu. Will close the menu and unpause the game.
     */
    protected final void fireResume() {
        fireEvent(new MenuEvent(MenuEvent.RESUME));
    }

    /**
     * Fire {@link MenuEvent#EXIT} event.
     * If fired from game menu, app will clean up and enter main menu.
     * If fired from main menu, app will close.
     */
    protected final void fireExit() {
        fireEvent(new MenuEvent(MenuEvent.EXIT));
    }
}
