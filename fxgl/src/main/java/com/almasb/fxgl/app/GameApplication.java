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
package com.almasb.fxgl.app;

import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.gameplay.GameState;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.fxgl.scene.GameScene;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.scene.menu.MenuEventListener;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.service.MasterTimer;
import com.almasb.fxgl.service.impl.timer.FPSCounter;
import com.almasb.fxgl.time.UpdateEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * To use FXGL extend this class and implement necessary methods.
 * The initialization process can be seen below (irrelevant phases are omitted):
 * <p>
 * <ol>
 * <li>Instance fields of YOUR subclass of GameApplication</li>
 * <li>initSettings()</li>
 * <li>Services configuration (after this you can safely call FXGL.getService())</li>
 * <li>initAchievements()</li>
 * <li>initInput()</li>
 * <li>preInit()</li>
 * <p>The following phases are NOT executed on UI thread</p>
 * <li>initAssets()</li>
 * <li>initGameVars()</li>
 * <li>initGame() OR loadState()</li>
 * <li>initPhysics()</li>
 * <li>initUI()</li>
 * <li>Start of main game loop execution on UI thread</li>
 * </ol>
 * <p>
 * Unless explicitly stated, methods are not thread-safe and must be
 * executed on the JavaFX Application (UI) Thread.
 * By default all callbacks are executed on the JavaFX Application (UI) Thread.
 * <p>
 *     Callback / listener notes: instance of GameApplication will always be
 *     notified last along the chain of callbacks.
 *     However, as per documentation, events are always fired after listeners.
 * </p>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class GameApplication extends SimpleFXGLApplication {

    private AppStateMachine stateMachine;

    ApplicationState getState() {
        return stateMachine.getApplicationState();
    }

    void setState(ApplicationState state) {
        stateMachine.setState(state);
    }

    public void pushState(SubState state) {
        stateMachine.pushState(state);
    }

    public void popState() {
        stateMachine.popState();
    }

    private SceneFactory sceneFactory;

    public SceneFactory getSceneFactory() {
        return sceneFactory;
    }

    public void setSceneFactory(SceneFactory sceneFactory) {
        this.sceneFactory = sceneFactory;
    }

    private EventHandler<MouseEvent> mouseHandler = e -> {
        // TODO: incorrect viewport
        //System.out.println(e);

        stateMachine.getCurrentState().getInput().onMouseEvent(e, new Viewport(getWidth(), getHeight()), getDisplay().getScaleRatio());
    };

    private EventHandler<KeyEvent> keyHandler = e -> {
        //System.out.println(e);

        stateMachine.getCurrentState().getInput().onKeyEvent(e);
    };

    @Override
    void configureApp() {
        log.debug("Configuring GameApplication");

        long start = System.nanoTime();

        setSceneFactory(initSceneFactory());
        stateMachine = new AppStateMachine();

        playState = (PlayState) ApplicationState.PLAYING.state();

        getPrimaryStage().getScene().addEventFilter(KeyEvent.ANY, keyHandler);
        getPrimaryStage().getScene().addEventFilter(MouseEvent.ANY, mouseHandler);
        getPrimaryStage().getScene().addEventFilter(EventType.ROOT, e -> {
            getDisplay().getCurrentScene().fireEvent(e.copyFor(null, null));
        });

        getPrimaryStage().sceneProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("NEW SCENE: " + newValue);

            newValue.addEventFilter(KeyEvent.ANY, keyHandler);
            newValue.addEventFilter(MouseEvent.ANY, mouseHandler);
            newValue.addEventFilter(EventType.ROOT, e -> {
                getDisplay().getCurrentScene().fireEvent(e.copyFor(null, null));
            });
        });

        log.infof("Game configuration took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);

        Platform.runLater(this::startMainLoop);
    }

    private void startMainLoop() {
        log.debug("Starting main loop");

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                long frameStart = System.nanoTime();

                double tpf = tickStart(now);

                tick(tpf);
                onPostUpdate(tpf);

                tickEnd(System.nanoTime() - frameStart);
            }
        }.start();
    }
//        override fun onReset() {
//            app.gameWorld.reset()
//            app.masterTimer.reset()
//        }

    private ReadOnlyLongWrapper tick = new ReadOnlyLongWrapper();
    private ReadOnlyIntegerWrapper fps = new ReadOnlyIntegerWrapper();

    private FPSCounter fpsCounter = new FPSCounter();

    private double tickStart(long now) {
        tick.set(tick.get() + 1);

        fps.set(fpsCounter.update(now));

        // assume that fps is at least 5 to avoid subtle bugs
        // disregard minor fluctuations > 55 for smoother experience
        if (fps.get() < 5 || fps.get() > 55)
            fps.set(60);

        return 1.0 / fps.get();

//        val realTPF = (secondsToNanos(1.0).toDouble() / fps.value).toLong()
//
//        now += realTPF
//        playtime.value += realTPF
//
//        tpf = realTPF / 1000000000.0
    }

    private void tick(double tpf) {
        stateMachine.onUpdate(tpf);

        // TODO:
        getAudioPlayer().onUpdateEvent(new UpdateEvent(0, tpf));
    }

    private void tickEnd(long frameTook) {

    }




















    private void attachHandlers(ApplicationState state) {
        state.state().getScene().addEventHandler(KeyEvent.ANY, keyHandler);
        state.state().getScene().addEventHandler(MouseEvent.ANY, mouseHandler);
    }

    public void runPreInit() {
        initAchievements();
        initInput();
        preInit();
    }

//    @Override
//    public void pause() {
//        pushState(PauseSubState.INSTANCE);
//        super.pause();
//    }
//
//    @Override
//    public void resume() {
//        popState();
//        super.resume();
//    }

    /**
     * Initialize user application.
     */
    private void initApp(InitAppTask initTask) {
        log.debug("Initializing App");

        FXGL.getInstance(LoadingState.class).setInitTask(initTask);
        setState(ApplicationState.LOADING);
    }

    /**
     * (Re-)initializes the user application as new and starts the game.
     * Note: cannot be called during callbacks.
     */
    public void startNewGame() {
        log.debug("Starting new game");
        initApp(new InitAppTask(this));
    }

    /**
     * (Re-)initializes the user application from the given data file and starts the game.
     * Note: cannot be called during callbacks.
     *
     * @param dataFile save data to load from
     */
    protected void startLoadedGame(DataFile dataFile) {
        log.debug("Starting loaded game");
        initApp(new InitAppTask(this, dataFile));
    }

    /**
     * Handler for menu events.
     */
    private MenuEventHandler menuHandler;

    /**
     * @return menu event handler associated with this game
     * @throws IllegalStateException if menus are not enabled
     */
    public MenuEventListener getMenuListener() {
        if (!getSettings().isMenuEnabled())
            throw new IllegalStateException("Menus are not enabled");

        if (menuHandler == null)
            menuHandler = new MenuEventHandler(this);
        return menuHandler;
    }

    private PlayState playState;

    /**
     * @return game state
     */
    public final GameState getGameState() {
        return playState.getGameState();
    }

    /**
     * @return game world
     */
    public final GameWorld getGameWorld() {
        return playState.getGameWorld();
    }

    /**
     * @return physics world
     */
    public final PhysicsWorld getPhysicsWorld() {
        return playState.getPhysicsWorld();
    }

    /**
     * @return game scene
     */
    public final GameScene getGameScene() {
        return playState.getGameScene();
    }

    /**
     * @return game scene input
     */
    @Override
    public final Input getInput() {
        return playState.getInput();
    }

    @Override
    public StateTimer getMasterTimer() {
        return playState.getTimer();
    }
}
