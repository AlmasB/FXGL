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

package s29networking;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.event.FXGLEvent;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.jme3.network.Client;
import com.jme3.network.Network;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Shows how to use networking in multiplayer games.
 */
public class NetworkingSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("NetworkingSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private Client client;
    private UpdateMessage lastMessage = null;

    @Override
    protected void preInit() {
        try {
            client = Network.connectToServer("Test", 1, "localhost", 55555, 55554);
            client.addMessageListener((source, message) -> {
                lastMessage = (UpdateMessage) message;
            }, UpdateMessage.class);

            getEventBus().addEventHandler(FXGLEvent.EXIT, e -> client.close());

            client.start();
        } catch (Exception e) {
            getDefaultCheckedExceptionHandler().handle(e);
            exit();
        }
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Ask for time", KeyCode.F));
    }

    @Override
    protected void initAssets() {}

    private GameEntity player;

    @Override
    protected void initGame() {
        player = Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void initPhysics() {}

    private Text uiText;

    @Override
    protected void initUI() {
        uiText = new Text();
        uiText.setFont(Font.font(18));

        uiText.setTranslateX(400);
        uiText.setTranslateY(300);

        getGameScene().addUINode(uiText);
    }

    @Override
    public void onUpdate(double tpf) {
        if (lastMessage != null) {
            player.getPositionComponent().setX(lastMessage.getX());
        }
    }

    @OnUserAction(name = "Ask for time", type = ActionType.ON_ACTION_BEGIN)
    public void askServerForTime() {
        // just send empty message
        client.send(new UpdateMessage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
