/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.net;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;
import javafx.scene.control.CheckBox;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use TCP server/client.
 * Run this sample twice: first select server, second select not server (client).
 * Then check the check box on the server, the client check box will also be checked.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NetworkingSample extends GameApplication {

    private boolean isServer;

    private Server<Bundle> server;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        var cb = new CheckBox();
        cb.selectedProperty().addListener((o, old, isSelected) -> {
            var bundle = new Bundle("CheckBoxData");
            bundle.put("isSelected", isSelected);

            if (isServer)
                server.broadcast(bundle);
        });

        addUINode(cb, 100, 100);

        runOnce(() -> {
            getDialogService().showConfirmationBox("Is Server?", answer -> {
                isServer = answer;

                if (isServer) {
                    server = getNetService().newTCPServer(55555);
                    server.startAsync();
                } else {
                    var client = getNetService().newTCPClient("localhost", 55555);
                    client.setOnConnected(connection -> {
                        connection.addMessageHandlerFX((conn, message) -> {
                            boolean isSelected = message.get("isSelected");

                            cb.setSelected(isSelected);
                        });
                    });
                    client.connectAsync();
                }
            });
        }, Duration.seconds(0.2));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
