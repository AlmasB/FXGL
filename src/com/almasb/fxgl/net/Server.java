package com.almasb.fxgl.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.almasb.fxgl.FXGLLogger;

public class Server {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Server");

    private TCPConnectionThread tcpThread = new TCPConnectionThread();
    private UDPConnectionThread udpThread = new UDPConnectionThread();

    private InetAddress clientAddress;
    private int clientPort;
    private int tcpPort, udpPort;

    private Map<Class<?>, DataParser<? super Serializable> > parsers = new HashMap<>();

    public Server() {
        this(NetworkConfig.DEFAULT_TCP_PORT, NetworkConfig.DEFAULT_UDP_PORT);
    }

    public Server(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        tcpThread.setDaemon(true);
        udpThread.setDaemon(true);
    }

    public void start() {
        tcpThread.start();
        udpThread.start();
    }

    public void stop() {
        try {
            send(ConnectionMessage.CLOSING, NetworkProtocol.TCP);
        }
        catch (Exception e) {
            log.warning("Server TCP already stopped or error: " + e.getMessage());
        }

        try {
            send(ConnectionMessage.CLOSING, NetworkProtocol.UDP);
        }
        catch (Exception e) {
            log.warning("Server UDP already stopped or error: " + e.getMessage());
        }

        tcpThread.running = false;
        udpThread.running = false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> void addParser(Class<T> cl, DataParser<T> parser) {
        parsers.put(cl, (DataParser<? super Serializable>) parser);
    }

    public void send(Serializable data) throws Exception {
        send(data, NetworkProtocol.UDP);
    }

    public void send(Serializable data, NetworkProtocol protocol) throws Exception {
        if (protocol == NetworkProtocol.TCP)
            sendTCP(data);
        else
            sendUDP(data);
    }

    private void sendTCP(Serializable data) throws Exception {
        if (tcpThread.running) {
            tcpThread.outputStream.writeObject(data);
        }
        else {
            throw new IllegalStateException("Client TCP is not connected");
        }
    }

    private void sendUDP(Serializable data) throws Exception {
        if (udpThread.running) {
            byte[] buf = toByteArray(data);
            udpThread.outSocket.send(new DatagramPacket(buf, buf.length, clientAddress, clientPort));
        }
        else {
            throw new IllegalStateException("Client UDP is not connected");
        }
    }

    private static byte[] toByteArray(Serializable data) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutput oo = new ObjectOutputStream(baos)) {
            oo.writeObject(data);
        }

        return baos.toByteArray();
    }

    private class TCPConnectionThread extends Thread {
        private boolean running = false;
        private ObjectOutputStream outputStream;

        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(tcpPort);
                    Socket socket = server.accept();
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                outputStream = out;
                socket.setTcpNoDelay(true);
                running = true;

                while (running) {
                    Object data = in.readObject();
                    if (data == ConnectionMessage.CLOSE) {
                        running = false;
                        break;
                    }
                    if (data == ConnectionMessage.CLOSING) {
                        sendTCP(ConnectionMessage.CLOSE);
                        running = false;
                        break;
                    }

                    parsers.getOrDefault(data.getClass(), d -> {}).parse((Serializable)data);
                }

            }
            catch (Exception e) {
                log.warning("Exception during TCP connection execution");
                log.warning(FXGLLogger.errorTraceAsString(e));
                running = false;
                return;
            }

            log.info("TCP connection closed normally");
        }
    }

    private class UDPConnectionThread extends Thread {
        private DatagramSocket outSocket;
        private boolean running = false;

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket(udpPort)) {
                outSocket = socket;
                running = true;

                while (running) {
                    byte[] buf = new byte[16384];
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    socket.receive(datagramPacket);

                    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()))) {
                        Object data = in.readObject();

                        if (data == ConnectionMessage.OPEN) {
                            clientAddress = datagramPacket.getAddress();
                            clientPort = datagramPacket.getPort();
                        }
                        if (data == ConnectionMessage.CLOSE) {
                            running = false;
                            break;
                        }
                        if (data == ConnectionMessage.CLOSING) {
                            sendUDP(ConnectionMessage.CLOSE);
                            running = false;
                            break;
                        }

                        parsers.getOrDefault(data.getClass(), d -> {}).parse((Serializable)data);
                    }
                }
            }
            catch (Exception e) {
                log.warning("Exception during UDP connection execution");
                log.warning(FXGLLogger.errorTraceAsString(e));
                running = false;
                return;
            }

            log.info("UDP connection closed normally");
        }
    }
}
