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

package com.almasb.fxgl.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Server side of the network connection.
 * <p>
 * Example:
 * <pre>
 *  // create server object with default params, note: no network operation yet
 * Server server = new Server();
 *  // add relevant parsers for messages from client
 * server.addParser(String.class, data -> System.out.println(data));
 *  // actual network operation
 * server.start();
 *  // send some messages
 * server.send("This is an example message");
 *  // when done, stop the server
 * server.stop();
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Server extends NetworkConnection {

    private static final Logger log = LogManager.getLogger(Server.class);

    private TCPConnectionThread tcpThread = new TCPConnectionThread();
    private UDPConnectionThread udpThread = new UDPConnectionThread();

    private InetAddress clientAddress;
    private int clientPort;
    private int tcpPort, udpPort;

    /**
     * Constructs and configures a single-client server with default ports
     * No network operation is done at this point.
     */
    public Server() {
        this(NetworkConfig.DEFAULT_TCP_PORT, NetworkConfig.DEFAULT_UDP_PORT);
    }

    /**
     * Constructs and configures a single-client server with default ports
     * No network operation is done at this point.
     */
    public Server(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        tcpThread.setDaemon(true);
        udpThread.setDaemon(true);
    }

    /**
     * Starts the server. This performs an actual network operation
     * of binding to ports and listening for incoming connections.
     */
    public void start() {
        tcpThread.start();
        udpThread.start();
    }

    private CountDownLatch latch = new CountDownLatch(2);

    public boolean startAndWait(long seconds) {
        tcpThread.start((int) seconds);
        udpThread.start((int) seconds);

        boolean result = false;

        try {
            result = latch.await(seconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("startAndWait(" + seconds + "): " + e);
        }

        if (!result)
            throw new RuntimeException("Client did not connect in " + seconds + " seconds");

        return true;
    }

    /**
     * Sends a message to all connected clients that
     * the server is about to shut down. Then stops the server
     * and the connection threads.
     * <p>
     * Further calls to {@link #send(Serializable)} will
     * throw IllegalStateException
     */
    public void stop() {
        sendClosingMessage();

        tcpThread.running = false;
        udpThread.running = false;
    }

    @Override
    public void close() {
        stop();
    }

    public boolean isConnected() {
        return tcpThread.running || udpThread.running;
    }

    @Override
    protected void sendUDP(Serializable data) throws Exception {
        if (udpThread.running) {
            byte[] buf = toByteArray(data);
            udpThread.outSocket.send(new DatagramPacket(buf, buf.length, clientAddress, clientPort));
        } else {
            throw new IllegalStateException("UDP connection not active");
        }
    }

    @Override
    protected void sendTCP(Serializable data) throws Exception {
        if (tcpThread.running) {
            tcpThread.outputStream.writeObject(data);
        } else {
            throw new IllegalStateException("Client TCP is not connected");
        }
    }

    private class TCPConnectionThread extends Thread {
        private boolean running = false;
        private ObjectOutputStream outputStream;
        private int timeoutSeconds = 0;

        void start(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            start();
        }

        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(tcpPort)) {
                server.setSoTimeout(timeoutSeconds * 1000);

                try (Socket socket = server.accept();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    outputStream = out;
                    socket.setTcpNoDelay(true);
                    latch.countDown();
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

                        parsers.getOrDefault(data.getClass(), d -> {
                        }).parse((Serializable) data);
                    }
                }
            } catch (Exception e) {
                log.warn("Exception during TCP connection execution: " + e);
                running = false;
                return;
            }

            log.debug("TCP connection closed normally");
        }
    }

    private class UDPConnectionThread extends Thread {
        private DatagramSocket outSocket;
        private boolean running = false;
        private int timeoutSeconds = 0;

        void start(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            start();
        }

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket(udpPort)) {
                outSocket = socket;
                socket.setSoTimeout(timeoutSeconds * 1000);
                latch.countDown();
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

                        parsers.getOrDefault(data.getClass(), d -> {
                        }).parse((Serializable) data);
                    }
                }
            } catch (Exception e) {
                log.warn("Exception during UDP connection execution: " + e);
                running = false;
                return;
            }

            log.debug("UDP connection closed normally");
        }
    }
}
