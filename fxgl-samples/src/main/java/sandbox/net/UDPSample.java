/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.net;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.logging.ConsoleOutput;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.logging.LoggerConfig;
import com.almasb.fxgl.logging.LoggerLevel;
import com.almasb.fxgl.net.NetService;
import com.almasb.fxgl.net.Server;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class UDPSample {

    public static void main(String[] args) throws Exception {
        Logger.configure(new LoggerConfig());
        Logger.addOutput(new ConsoleOutput(), LoggerLevel.DEBUG);

        var server = new NetService().newUDPServer(55555);
        server.setOnConnected(connection -> {
            connection.addMessageHandler((conn, message) -> {
                System.out.println("Server got: " + message);

                if (message.getName().equals("Bye!!!")) {
                    System.out.println("Stopping now");
                    server.stop();
                }
            });
        });

        var client = new NetService().newUDPClient("localhost", 55555);
        client.setOnConnected(connection -> {
            connection.addMessageHandler((conn, message) -> {
                System.out.println("Client got: " + message);

                conn.send(new Bundle("Bye!!!"));

                client.disconnect();
            });
        });

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Client connecting");

            client.connectTask().run();
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Broadcasting hello");

            server.broadcast(new Bundle("HELLO!"));
        }).start();

        server.startTask().run();
    }
}






//        new Thread(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            System.out.println("Client sending hello");
//
//            client.getConnections().forEach(c -> c.send(new Bundle("Client says hello")));
//        }).start();