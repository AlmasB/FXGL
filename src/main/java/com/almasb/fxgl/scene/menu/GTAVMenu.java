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
package com.almasb.fxgl.scene.menu;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.ui.UIFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.function.Supplier;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class GTAVMenu extends FXGLMenu {

    private HBox contentBox = new HBox(10);

    public GTAVMenu(GameApplication app, MenuType type) {
        super(app, type);

        // placeholders
        contentBox.getChildren().addAll(new Pane(), new Pane());
        contentBox.setAlignment(Pos.TOP_CENTER);

        VBox vbox = new VBox(50,
                makeTitle(),
                makeMenuBar(),
                contentBox);
        vbox.setPrefSize(app.getWidth(), app.getHeight());
        vbox.setAlignment(Pos.TOP_CENTER);

        getRoot().getChildren().setAll(makeBackground(), vbox);
    }

    private Node makeBackground() {
        return new Rectangle(app.getWidth(), app.getHeight(), Color.BROWN);
    }

    private Text makeTitle() {
        Text title = UIFactory.newText(app.getSettings().getTitle(), 24);
        // TODO: version + profile
        return title;
    }

    private HBox makeMenuBar() {
        ToggleButton tb1 = new ToggleButton("MAIN MENU");
        ToggleButton tb2 = new ToggleButton("OPTIONS");
        ToggleButton tb3 = new ToggleButton("EXTRA");
        tb1.setFont(UIFactory.newFont(18));
        tb2.setFont(UIFactory.newFont(18));
        tb3.setFont(UIFactory.newFont(18));

        ToggleGroup group = new ToggleGroup();
        tb1.setToggleGroup(group);
        tb2.setToggleGroup(group);
        tb3.setToggleGroup(group);

        tb1.setUserData(type == MenuType.MAIN_MENU ? createMenuBodyMainMenu() : createMenuBodyGameMenu());
        tb2.setUserData(makeOptionsMenu());
        tb3.setUserData(makeExtraMenu());

        group.selectedToggleProperty().addListener((obs, old, newToggle) -> {
            if (newToggle == null) {
                group.selectToggle(old);
                return;
            }
            contentBox.getChildren().set(0, (Node)newToggle.getUserData());
        });
        group.selectToggle(tb1);

        HBox hbox = new HBox(10, tb1, tb2, tb3);
        hbox.setAlignment(Pos.TOP_CENTER);
        return hbox;
    }

    private VBox createMenuBodyMainMenu() {
        Button btnContinue = createActionButton("CONTINUE", this::fireContinue);
        Button btnNew = createActionButton("NEW GAME", this::fireNewGame);
        Button btnLoad = createContentButton("LOAD GAME", this::createContentLoad);
        Button btnExit = createActionButton("EXIT", this::fireExit);

        return new VBox(10, btnContinue, btnNew, btnLoad, btnExit);
    }

    private VBox createMenuBodyGameMenu() {
        Button btnResume = createActionButton("RESUME", this::fireResume);
        Button btnSave = createActionButton("SAVE", this::fireSave);
        Button btnLoad = createContentButton("LOAD", this::createContentLoad);
        Button btnExit = createActionButton("EXIT", () -> {
            app.getDisplay().showConfirmationBox("Exit to Main Menu?\nAll unsaved progress will be lost!", yes -> {
                if (yes)
                    fireExitToMainMenu();
            });
        });

        return new VBox(10, btnResume, btnSave, btnLoad, btnExit);
    }

    private VBox makeOptionsMenu() {
        Button btnGameplay = createContentButton("GAMEPLAY", this::createContentGameplay);
        Button btnControls = createContentButton("CONTROLS", this::createContentControls);
        Button btnVideo = createContentButton("VIDEO", this::createContentVideo);
        Button btnAudio = createContentButton("AUDIO", this::createContentAudio);

        return new VBox(10, btnGameplay, btnControls, btnVideo, btnAudio);
    }

    private VBox makeExtraMenu() {
        Button btnCredits = createContentButton("CREDITS", this::createContentCredits);
        Button btnTrophies = createContentButton("TROPHIES", this::createContentAchievements);

        return new VBox(10, btnCredits, btnTrophies);
    }

    protected void setContent(Node content) {
        contentBox.getChildren().set(1, content);
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
     * @param contentSupplier content supplier
     * @return new button
     */
    protected final Button createContentButton(String name, Supplier<MenuContent> contentSupplier) {
        Button btn = UIFactory.newButton(name);
        btn.setUserData(contentSupplier);
        btn.setOnAction(e -> setContent(((Supplier<MenuContent>)btn.getUserData()).get()));
        return btn;
    }
}
