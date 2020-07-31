/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.net;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.chat.ChatService;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ChatSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.addEngineService(ChatService.class);
        settings.getCSSList().add("chat.css");

        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    private ChatService getChatService() {
        return getService(ChatService.class);
    }

    @Override
    protected void initGame() {
        addUINode(getUIFactoryService().newText("GAME WORLD AREA", Color.BLACK, 46.0), 120, 250);


        runOnce(() -> {

            var btnHost = getUIFactoryService().newButton("Host...");
            btnHost.setOnAction(e -> {
                System.out.println("Host");

                getChatService().startServer(55555);

            });

            var btnConnect = getUIFactoryService().newButton("Connect...");
            btnConnect.setOnAction(e -> {
                System.out.println("Connect");

                getChatService().startClient("localhost", 55555);
            });

            getDialogService().showBox("Chat Development", new Rectangle(40, 40, Color.BLUE), btnHost, btnConnect);


            //getSceneService().pushSubScene(new ChatSubScene(getAppWidth(), getAppHeight()));
        }, Duration.seconds(0.5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
