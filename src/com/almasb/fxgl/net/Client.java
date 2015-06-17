package com.almasb.fxgl.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.almasb.fxgl.FXGLLogger;

public class Client {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Client");

    private TCPConnectionThread connThread = new TCPConnectionThread();

    private String serverIP;
    private int tcpPort, udpPort;

    private Map<Class<?>, DataParser<? super Serializable> > parsers = new HashMap<>();

    public Client(String serverIP) {
        this(serverIP, NetworkConfig.DEFAULT_TCP_PORT, NetworkConfig.DEFAULT_UDP_PORT);
    }

    public Client(String serverIP, int tcpPort, int udpPort) {
        this.serverIP = serverIP;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;


        // TODO: do udp socket
    }

    public void connect() {
        connThread.setDaemon(true);
        connThread.running = true;
        connThread.start();
    }

    public void disconnect() {
        connThread.running = false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> void addParser(Class<T> cl, DataParser<T> parser) {
        parsers.put(cl, (DataParser<? super Serializable>) parser);
    }

    public void send(Serializable data) throws Exception {
        if (connThread.running) {
            connThread.outputStream.writeObject(data);
        }
    }

    private class TCPConnectionThread extends Thread {
        private ObjectOutputStream outputStream;
        private boolean running = false;

        @Override
        public void run() {
            try (Socket socket = new Socket(serverIP, tcpPort);
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                outputStream = out;
                socket.setTcpNoDelay(true);

                while (running) {
                    Object data = in.readObject();
                    parsers.getOrDefault(data.getClass(), d -> {}).parse((Serializable)data);
                }
            }
            catch (Exception e) {
                if (running) {
                    log.warning("Exception during TCP connection execution");
                    log.warning(FXGLLogger.errorTraceAsString(e));
                }
            }

            log.info("TCP connection closed normally");
        }
    }

    private class UDPConnectionThread extends Thread {
        @Override
        public void run() {

        }
    }
}
