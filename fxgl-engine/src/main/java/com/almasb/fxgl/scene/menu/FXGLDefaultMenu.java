/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.scene.menu;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.particle.ParticleSystem;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.settings.MenuItem;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FXGLButton;
import com.almasb.fxgl.core.util.Supplier;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.EnumSet;

import static com.almasb.fxgl.app.DSLKt.texture;
import static com.almasb.fxgl.app.FXGL.localizedStringProperty;
import static com.almasb.fxgl.core.math.FXGLMath.noise1D;
import static com.almasb.fxgl.core.math.FXGLMath.random;

/**
 * This is the default FXGL menu used if the users
 * don't provide their own. This class provides
 * common structures used in FXGL default menu style.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXGLDefaultMenu extends FXGLMenu {

    private ParticleSystem particleSystem;

    public FXGLDefaultMenu(GameApplication app, MenuType type) {
        super(app, type);

        MenuBox menu = type == MenuType.MAIN_MENU
                ? createMenuBodyMainMenu()
                : createMenuBodyGameMenu();

        double menuX = 50;
        double menuY = app.getHeight() / 2 - menu.getLayoutHeight() / 2;

        menuRoot.setTranslateX(menuX);
        menuRoot.setTranslateY(menuY);

        contentRoot.setTranslateX(app.getWidth() - 500);
        contentRoot.setTranslateY(menuY);

        // particle smoke
        Texture t = texture("particles/smoke.png", 128, 128).brighter().brighter();

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSourceImage(t.getImage());
        emitter.setSize(150, 220);
        emitter.setNumParticles(10);
        emitter.setEmissionRate(0.01);
        emitter.setVelocityFunction((i) -> new Point2D(random() * 2.5, -random() * random(80, 120)));
        emitter.setExpireFunction((i) -> Duration.seconds(random(4, 7)));
        emitter.setScaleFunction((i) -> new Point2D(0.15, 0.10));
        emitter.setSpawnPointFunction((i) -> new Point2D(random(0, app.getWidth() - 200), 120));

        particleSystem.addParticleEmitter(emitter, 0, app.getHeight());

        // TODO: rename, as this is not same as contentRoot
        getContentRoot().getChildren().add(3, particleSystem.getPane());

        menuRoot.getChildren().addAll(menu);
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

    private ObjectProperty<Color> titleColor;
    private double t = 0;

    @Override
    public void onUpdate(double tpf) {
        double frequency = 1.7;

        t += tpf * frequency;

        particleSystem.onUpdate(tpf);

        Color color = Color.color(1, 1, 1, noise1D(t));
        titleColor.set(color);
    }

    @Override
    protected Node createBackground(double width, double height) {
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.rgb(10, 1, 1));
        return bg;
    }

    @Override
    protected Node createTitleView(String title) {
        titleColor = new SimpleObjectProperty<>(Color.WHITE);

        Text text = FXGL.getUIFactory().newText(title.substring(0, 1), 50);
        text.setFill(null);
        text.strokeProperty().bind(titleColor);
        text.setStrokeWidth(1.5);

        Text text2 = FXGL.getUIFactory().newText(title.substring(1, title.length()), 50);
        text2.setFill(null);
        text2.setStroke(titleColor.getValue());
        text2.setStrokeWidth(1.5);

        double textWidth = text.getLayoutBounds().getWidth() + text2.getLayoutBounds().getWidth();

        Rectangle bg = new Rectangle(textWidth + 30, 65, null);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(4);
        bg.setArcWidth(25);
        bg.setArcHeight(25);

        ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(50);

        Texture t = texture("particles/trace_horizontal.png", 64, 64);

        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(t.getImage());
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setSize(18, 22);
        emitter.setNumParticles(2);
        emitter.setEmissionRate(0.2);
        emitter.setVelocityFunction((i) -> i % 2 == 0 ? new Point2D(random(-10, 0), random(0, 0)) : new Point2D(random(0, 10), random(0, 0)));
        emitter.setExpireFunction((i) -> Duration.seconds(random(4, 6)));
        emitter.setScaleFunction((i) -> new Point2D(-0.03, -0.03));
        emitter.setSpawnPointFunction((i) -> new Point2D(random(0, 0), random(0, 0)));
        emitter.setAccelerationFunction(() -> new Point2D(random(-1, 1), random(0, 0)));

        HBox box = new HBox(text, text2);
        box.setAlignment(Pos.CENTER);

        StackPane titleRoot = new StackPane();
        titleRoot.getChildren().addAll(bg, box);

        titleRoot.setTranslateX(app.getWidth() / 2 - (textWidth + 30) / 2);
        titleRoot.setTranslateY(50);

        particleSystem = new ParticleSystem();
        particleSystem.addParticleEmitter(emitter, app.getWidth() / 2 - 30, titleRoot.getTranslateY() + 34);

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
        log.debug("createMenuBodyMainMenu()");

        MenuBox box = new MenuBox();

        EnumSet<MenuItem> enabledItems = app.getSettings().getEnabledMenuItems();

        if (enabledItems.contains(MenuItem.SAVE_LOAD)) {
            MenuButton itemContinue = new MenuButton("menu.continue");
            itemContinue.setOnAction(e -> fireContinue());
            box.add(itemContinue);

            itemContinue.disableProperty().bind(listener.hasSavesProperty().not());
        }

        MenuButton itemNewGame = new MenuButton("menu.newGame");
        itemNewGame.setOnAction(e -> fireNewGame());
        box.add(itemNewGame);

        if (enabledItems.contains(MenuItem.SAVE_LOAD)) {
            MenuButton itemLoad = new MenuButton("menu.load");
            itemLoad.setMenuContent(this::createContentLoad);
            box.add(itemLoad);
        }

        MenuButton itemOptions = new MenuButton("menu.options");
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);

        if (enabledItems.contains(MenuItem.EXTRA)) {
            MenuButton itemExtra = new MenuButton("menu.extra");
            itemExtra.setChild(createExtraMenu());
            box.add(itemExtra);
        }

        if (enabledItems.contains(MenuItem.ONLINE)) {
            MenuButton itemMultiplayer = new MenuButton("menu.online");
            itemMultiplayer.setOnAction(e -> fireMultiplayer());
            box.add(itemMultiplayer);
        }

        MenuButton itemLogout = new MenuButton("menu.logout");
        itemLogout.setOnAction(e -> fireLogout());
        box.add(itemLogout);

        MenuButton itemExit = new MenuButton("menu.exit");
        itemExit.setOnAction(e -> fireExit());
        box.add(itemExit);

        return box;
    }

    protected MenuBox createMenuBodyGameMenu() {
        log.debug("createMenuBodyGameMenu()");

        MenuBox box = new MenuBox();

        EnumSet<MenuItem> enabledItems = app.getSettings().getEnabledMenuItems();

        MenuButton itemResume = new MenuButton("menu.resume");
        itemResume.setOnAction(e -> fireResume());
        box.add(itemResume);

        if (enabledItems.contains(MenuItem.SAVE_LOAD)) {
            MenuButton itemSave = new MenuButton("menu.save");
            itemSave.setOnAction(e -> fireSave());

            MenuButton itemLoad = new MenuButton("menu.load");
            itemLoad.setMenuContent(this::createContentLoad);

            box.add(itemSave);
            box.add(itemLoad);
        }

        MenuButton itemOptions = new MenuButton("menu.options");
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);

        if (enabledItems.contains(MenuItem.EXTRA)) {
            MenuButton itemExtra = new MenuButton("menu.extra");
            itemExtra.setChild(createExtraMenu());
            box.add(itemExtra);
        }

        MenuButton itemExit = new MenuButton("menu.mainMenu");
        itemExit.setOnAction(e -> fireExitToMainMenu());
        box.add(itemExit);

        return box;
    }

    protected MenuBox createOptionsMenu() {
        log.debug("createOptionsMenu()");

        MenuButton itemGameplay = new MenuButton("menu.gameplay");
        itemGameplay.setMenuContent(this::createContentGameplay);

        MenuButton itemControls = new MenuButton("menu.controls");
        itemControls.setMenuContent(this::createContentControls);

        MenuButton itemVideo = new MenuButton("menu.video");
        itemVideo.setMenuContent(this::createContentVideo);
        MenuButton itemAudio = new MenuButton("menu.audio");
        itemAudio.setMenuContent(this::createContentAudio);

        MenuButton btnRestore = new MenuButton("menu.restore");
        btnRestore.setOnAction(e -> {
            app.getDisplay().showConfirmationBox(FXGL.getLocalizedString("menu.settingsRestore"), yes -> {
                if (yes) {
                    switchMenuContentTo(EMPTY);
                    listener.restoreDefaultSettings();
                }
            });
        });

        return new MenuBox(itemGameplay, itemControls, itemVideo, itemAudio, btnRestore);
    }

    protected MenuBox createExtraMenu() {
        log.debug("createExtraMenu()");

        MenuButton itemAchievements = new MenuButton("menu.trophies");
        itemAchievements.setMenuContent(this::createContentAchievements);

        MenuButton itemCredits = new MenuButton("menu.credits");
        itemCredits.setMenuContent(this::createContentCredits);

        MenuButton itemFeedback = new MenuButton("menu.feedback");
        itemFeedback.setMenuContent(this::createContentFeedback);

        return new MenuBox(itemAchievements, itemCredits, itemFeedback);
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

        MenuBox(MenuButton... items) {

            for (MenuButton item : items) {
                add(item);
            }
        }

        void add(MenuButton item) {
            item.setParent(this);
            getChildren().addAll(item);
        }

        double getLayoutHeight() {
            return 10 * getChildren().size();
        }
    }

    private class MenuButton extends Pane {
        private MenuBox parent;
        private MenuContent cachedContent = null;

        private Polygon p = new Polygon(0,0, 220,0, 250,35, 0,35);
        private FXGLButton btn;

        MenuButton(String stringKey) {
            btn = new FXGLButton();
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle("-fx-background-color: transparent");
            btn.textProperty().bind(localizedStringProperty(stringKey));

            p.setMouseTransparent(true);

            Paint g = new LinearGradient(0, 1, 1, 0.2, true, CycleMethod.NO_CYCLE,
                    new Stop(0.6, Color.color(1, 0.8, 0, 0.34)),
                    new Stop(0.85, Color.color(1, 0.8, 0, 0.74)),
                    new Stop(1, Color.WHITE));

            p.fillProperty().bind(
                    Bindings.when(btn.pressedProperty()).then((Paint) Color.color(1, 0.8, 0, 0.75)).otherwise(g)
            );

            p.setStroke(Color.color(0.1, 0.1, 0.1, 0.15));
            p.setEffect(new GaussianBlur());

            // TODO: hover and/or focused?
            p.visibleProperty().bind(btn.hoverProperty());

            getChildren().addAll(btn, p);
        }

        public void setOnAction(EventHandler<ActionEvent> e) {
            btn.setOnAction(e);
        }

        public void setParent(MenuBox menu) {
            parent = menu;
        }

        public void setMenuContent(Supplier<MenuContent> contentSupplier) {

            btn.addEventHandler(ActionEvent.ACTION, event -> {
                if (cachedContent == null)
                    cachedContent = contentSupplier.get();

                switchMenuContentTo(cachedContent);
            });
        }

        public void setChild(MenuBox menu) {
            MenuButton back = new MenuButton("menu.back");
            menu.getChildren().add(0, back);

            back.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(MenuButton.this.parent));

            btn.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(menu));
        }
    }

    @Override
    protected Button createActionButton(String name, Runnable action) {
        MenuButton btn = new MenuButton(name);
        btn.addEventHandler(ActionEvent.ACTION, event -> action.run());

        return btn.btn;
    }

    @Override
    protected Button createActionButton(StringBinding name, Runnable action) {
        MenuButton btn = new MenuButton(name.getValue());
        btn.addEventHandler(ActionEvent.ACTION, event -> action.run());

        return btn.btn;
    }
}
