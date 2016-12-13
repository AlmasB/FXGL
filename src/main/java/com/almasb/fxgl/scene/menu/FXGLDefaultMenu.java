/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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
package com.almasb.fxgl.scene.menu;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.event.ProfileSelectedEvent;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * This is the default FXGL menu used if the users
 * don't provide their own. This class provides
 * common structures used in FXGL default menu style.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXGLDefaultMenu extends FXGLMenu {

    public FXGLDefaultMenu(GameApplication app, MenuType type) {
        super(app, type);

        MenuBox menu = type == MenuType.MAIN_MENU
                ? createMenuBodyMainMenu()
                : createMenuBodyGameMenu();

        double menuX = 50;
        double menuY = app.getHeight() / 2 - menu.getLayoutHeight() / 2;

        menuRoot.setTranslateX(menuX);
        menuRoot.setTranslateY(menuY);

        contentRoot.setTranslateX(menuX * 2 + 200);
        contentRoot.setTranslateY(menuY);

        menuRoot.getChildren().add(menu);
        contentRoot.getChildren().add(EMPTY);

        activeProperty().addListener((observable, wasActive, isActive) -> {
            if (!isActive) {
                // the scene is no longer active so reset everything
                // so that next time scene is active everything is loaded properly
                switchMenuTo(menu);
                switchMenuContentTo(EMPTY);
            }
        });
    }

    @Override
    protected Node createBackground(double width, double height) {
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.rgb(10, 1, 1));
        return bg;
    }

    @Override
    protected Node createTitleView(String title) {
        Text text = FXGL.getUIFactory().newText(title, 50);

        Rectangle bg = new Rectangle(text.getLayoutBounds().getWidth() + 20, 60, null);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2);

        StackPane titleRoot = new StackPane();
        titleRoot.getChildren().addAll(bg, text);

        titleRoot.setTranslateX(app.getWidth() / 2 - (text.getLayoutBounds().getWidth() + 20) / 2);
        titleRoot.setTranslateY(50);
        return titleRoot;
    }

    @Override
    protected Node createVersionView(String version) {
        Text view = FXGL.getUIFactory().newText(version);
        view.setTranslateY(app.getHeight() - 2);
        return view;
    }

    @Override
    protected Node createProfileView(String profileName) {
        Text view = FXGL.getUIFactory().newText(profileName);
        view.setTranslateY(app.getHeight() - 2);
        view.setTranslateX(app.getWidth() - view.getLayoutBounds().getWidth());
        return view;
    }

    protected MenuBox createMenuBodyMainMenu() {
        MenuItem itemContinue = new MenuItem("CONTINUE");
        itemContinue.setOnAction(e -> fireContinue());

        MenuItem itemNewGame = new MenuItem("NEW GAME");
        itemNewGame.setOnAction(e -> fireNewGame());

        MenuItem itemLoad = new MenuItem("LOAD");
        itemLoad.setMenuContent(this::createContentLoad);

        MenuItem itemOptions = new MenuItem("OPTIONS");
        itemOptions.setChild(createOptionsMenu());

        MenuItem itemExtra = new MenuItem("EXTRA");
        itemExtra.setChild(createExtraMenu());

        MenuItem itemMultiplayer = new MenuItem("ONLINE");
        itemMultiplayer.setOnAction(e -> fireMultiplayer());

        MenuItem itemLogout = new MenuItem("LOGOUT");
        itemLogout.setOnAction(e -> fireLogout());

        MenuItem itemExit = new MenuItem("EXIT");
        itemExit.setOnAction(e -> fireExit());

        app.getEventBus().addEventHandler(ProfileSelectedEvent.ANY, event -> {
            itemContinue.setDisable(!event.hasSaves());
        });

        return new MenuBox(200, itemContinue, itemNewGame, itemLoad,
                itemOptions, itemExtra, itemMultiplayer, itemLogout, itemExit);
    }

    protected MenuBox createMenuBodyGameMenu() {
        MenuItem itemResume = new MenuItem("RESUME");
        itemResume.setOnAction(e -> fireResume());

        MenuItem itemSave = new MenuItem("SAVE");
        itemSave.setOnAction(e -> fireSave());

        MenuItem itemLoad = new MenuItem("LOAD");
        itemLoad.setMenuContent(this::createContentLoad);

        MenuItem itemOptions = new MenuItem("OPTIONS");
        itemOptions.setChild(createOptionsMenu());

        MenuItem itemExtra = new MenuItem("EXTRA");
        itemExtra.setChild(createExtraMenu());

        MenuItem itemExit = new MenuItem("MAIN MENU");
        itemExit.setOnAction(e -> fireExitToMainMenu());

        return new MenuBox(200, itemResume, itemSave, itemLoad, itemOptions, itemExtra, itemExit);
    }

    protected MenuBox createOptionsMenu() {
        MenuItem itemGameplay = new MenuItem("GAMEPLAY");
        itemGameplay.setMenuContent(this::createContentGameplay);

        MenuItem itemControls = new MenuItem("CONTROLS");
        itemControls.setMenuContent(this::createContentControls);

        MenuItem itemVideo = new MenuItem("VIDEO");
        itemVideo.setMenuContent(this::createContentVideo);
        MenuItem itemAudio = new MenuItem("AUDIO");
        itemAudio.setMenuContent(this::createContentAudio);

        MenuItem btnRestore = new MenuItem("RESTORE");
        btnRestore.setOnAction(e -> {
            app.getDisplay().showConfirmationBox("Settings will be restored to default", yes -> {
                if (yes) listener.restoreDefaultSettings();
            });
        });

        return new MenuBox(200, itemGameplay, itemControls, itemVideo, itemAudio, btnRestore);
    }

    protected MenuBox createExtraMenu() {
        MenuItem itemAchievements = new MenuItem("TROPHIES");
        itemAchievements.setMenuContent(this::createContentAchievements);

        MenuItem itemCredits = new MenuItem("CREDITS");
        itemCredits.setMenuContent(this::createContentCredits);

        MenuItem itemFeedback = new MenuItem("FEEDBACK");
        itemFeedback.setMenuContent(this::createContentFeedback);

        return new MenuBox(200, itemAchievements, itemCredits, itemFeedback);
    }

    @Override
    protected void switchMenuTo(Node menu) {
        Node oldMenu = menuRoot.getChildren().get(0);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), oldMenu);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            menu.setOpacity(0);
            menuRoot.getChildren().set(0, menu);
            oldMenu.setOpacity(1);

            FadeTransition ft2 = new FadeTransition(Duration.seconds(0.33), menu);
            ft2.setToValue(1);
            ft2.play();
        });
        ft.play();
    }

    @Override
    protected void switchMenuContentTo(Node content) {
        contentRoot.getChildren().set(0, content);
    }

    private static class MenuBox extends VBox {
        MenuBox(int width, MenuItem... items) {
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

        double getLayoutHeight() {
            return 10 * getChildren().size();
        }
    }

    private class MenuItem extends FXGLButton {
        private MenuBox parent;

        MenuItem(String name) {
            super(name);
        }

        public void setParent(MenuBox menu) {
            parent = menu;
        }

        public void setMenuContent(Supplier<MenuContent> contentSupplier) {
            this.addEventHandler(ActionEvent.ACTION, event -> switchMenuContentTo(contentSupplier.get()));
        }

        public void setChild(MenuBox menu) {
            MenuItem back = new MenuItem("BACK");
            menu.getChildren().add(0, back);

            back.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(MenuItem.this.parent));

            this.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(menu));
        }
    }

    @Override
    protected Button createActionButton(String name, Runnable action) {
        MenuItem btn = new MenuItem(name);
        btn.addEventHandler(ActionEvent.ACTION, event -> action.run());

        return btn;
    }
}
