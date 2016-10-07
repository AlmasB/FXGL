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

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.event.ProfileSelectedEvent;
import com.almasb.fxgl.scene.FXGLMenu;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.function.Supplier;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CCTRMenu extends FXGLMenu {

    public CCTRMenu(GameApplication app, MenuType type) {
        super(app, type);

        Node menuBody = type == MenuType.MAIN_MENU
                ? createMenuBodyMainMenu()
                : createMenuBodyGameMenu();

        contentRoot.setTranslateX(app.getWidth() / 2 - 50);
        contentRoot.setTranslateY(app.getHeight() / 2 - 100);

        menuRoot.getChildren().add(menuBody);
        contentRoot.getChildren().add(EMPTY);

        activeProperty().addListener((observable, wasActive, isActive) -> {
            if (!isActive) {
                // the scene is no longer active so reset everything
                // so that next time scene is active everything is loaded properly
                switchMenuTo(menuBody);
                switchMenuContentTo(EMPTY);
            }
        });
    }

    @Override
    protected Node createBackground(double width, double height) {
        return new Rectangle(width, height, Color.BLUEVIOLET);
    }

    @Override
    protected Node createTitleView(String title) {
        Text titleView = FXGL.getUIFactory().newText(app.getSettings().getTitle(), 18);
        titleView.setTranslateY(30);
        return titleView;
    }

    @Override
    protected Node createVersionView(String version) {
        Text view = FXGL.getUIFactory().newText(version, 16);
        view.setTranslateX(app.getWidth() - view.getLayoutBounds().getWidth());
        view.setTranslateY(20);
        return view;
    }

    @Override
    protected Node createProfileView(String profileName) {
        Text view = FXGL.getUIFactory().newText(profileName, 24);
        view.setTranslateX(app.getWidth() - view.getLayoutBounds().getWidth());
        view.setTranslateY(50);
        return view;
    }

    protected Node createMenuBodyMainMenu() {
        double midY = app.getHeight() / 2;

        double distance = midY - 50;

        Button btnContinue = createActionButton("CONTINUE", this::fireContinue);
        Button btn1 = createActionButton("NEW GAME", this::fireNewGame);
        Button btn2 = createContentButton("LOAD", this::createContentLoad);
        Button btn3 = createContentButton("OPTIONS", () -> new MenuContent(makeOptionsMenu()));
        Button btn4 = createContentButton("EXTRA", () -> new MenuContent(makeExtraMenu()));
        Button btn5 = createActionButton("ONLINE", this::fireMultiplayer);
        Button btn6 = createActionButton("LOGOUT", this::fireLogout);
        Button btn7 = createActionButton("EXIT", this::fireExit);

        Group group = new Group(btnContinue, btn1, btn2, btn3, btn4, btn5, btn6, btn7);

        double dtheta = Math.PI / (group.getChildren().size() - 1);
        double angle = Math.PI / 2;

        int i = 0;
        for (Node n : group.getChildren()) {

            Point2D vector = new Point2D(Math.cos(angle), -Math.sin(angle))
                    .normalize()
                    .multiply(distance)
                    .add(0, midY);

            n.setTranslateX(vector.getX() - (i == 0 || i == 7 ? 0 : 100));
            n.setTranslateY(vector.getY());

            angle -= dtheta;

            // slightly hacky way to get a nice looking radial menu
            // we assume that there are 8 items
            if (i == 0 || i == group.getChildren().size() - 2) {
                angle -= dtheta / 2;
            } else if (i == 2 || i == 4) {
                angle += dtheta / 4;
            } else if (i == 3) {
                angle += dtheta / 2;
            }

            i++;
        }

        app.getEventBus().addEventHandler(ProfileSelectedEvent.ANY, event -> {
            btnContinue.setDisable(!event.hasSaves());
        });

        return group;
    }

    protected Node createMenuBodyGameMenu() {
        double midY = app.getHeight() / 2;

        double distance = midY - 50;

        Button btnContinue = createActionButton("RESUME", this::fireResume);
        Button btn1 = createActionButton("SAVE", this::fireSave);
        Button btn2 = createContentButton("LOAD", this::createContentLoad);
        Button btn3 = createContentButton("OPTIONS", () -> new MenuContent(makeOptionsMenu()));
        Button btn4 = createContentButton("EXTRA", () -> new MenuContent(makeExtraMenu()));
        Button btn5 = createActionButton("MAIN MENU", this::fireExitToMainMenu);

        Group group = new Group(btnContinue, btn1, btn2, btn3, btn4, btn5);

        double dtheta = Math.PI / (group.getChildren().size() - 1);
        double angle = Math.PI / 2;

        int i = 0;
        for (Node n : group.getChildren()) {

            Point2D vector = new Point2D(Math.cos(angle), -Math.sin(angle))
                    .normalize()
                    .multiply(distance)
                    .add(0, midY);

            n.setTranslateX(vector.getX() - (i == 0 || i == 5 ? 0 : 100));
            n.setTranslateY(vector.getY());

            angle -= dtheta;

            i++;
        }

        return group;
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

    @Override
    protected void switchMenuContentTo(Node content) {
        contentRoot.getChildren().set(0, content);
    }

    /**
     * Creates a new button with given name that performs given action on click/press.
     *
     * @param name  button name
     * @param action button action
     * @return new button
     */
    @Override
    protected final Button createActionButton(String name, Runnable action) {
        Button btn = FXGL.getUIFactory().newButton(name);
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
    @SuppressWarnings("unchecked")
    @Override
    protected final Button createContentButton(String name, Supplier<MenuContent> contentSupplier) {
        Button btn = FXGL.getUIFactory().newButton(name);
        btn.setUserData(contentSupplier);
        btn.setOnAction(e -> switchMenuContentTo(((Supplier<MenuContent>)btn.getUserData()).get()));
        return btn;
    }
}
