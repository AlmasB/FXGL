package com.almasb.fxgl.net;

import java.io.Serializable;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.almasb.fxgl.FXGLLogger;

public class Server {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Server");

    private TCPConnectionThread tcpThread = new TCPConnectionThread();
    private UDPConnectionThread udpThread = new UDPConnectionThread();

    private int tcpPort, udpPort;

    private Map<Class<?>, DataParser<? super Serializable> > parsers = new HashMap<>();

    public Server() {
        this(NetworkConfig.DEFAULT_TCP_PORT, NetworkConfig.DEFAULT_UDP_PORT);
    }

    public Server(int tcpPort, int udpPort) {

    }

    public void start() {

    }

    public void stop() {

    }

    private class TCPConnectionThread extends Thread {
        private boolean running = false;

        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(tcpPort);) {

            }
            catch (Exception e) {

            }
        }
    }

    private class UDPConnectionThread extends Thread {
        private boolean running = false;

        @Override
        public void run() {

        }
    }
}
