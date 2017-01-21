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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Client side of the network connection.
 * <p>
 * Example:
 * <pre>
 *  // server ip
 * Client client = new Client("127.0.0.1");
 *  // add relevant parsers for messages from server
 * client.addParser(String.class, data -> System.out.println(data));
 *  // connect to server
 * client.connect();
 *  // send some messages
 * client.send("This is an example message");
 *  // when done, disconnect from server
 * client.disconnect();
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Client extends NetworkConnection {

    private static final Logger log = LogManager.getLogger(Client.class);

    private TCPConnectionThread tcpThread = new TCPConnectionThread();
    private UDPConnectionThread udpThread = new UDPConnectionThread();

    private CountDownLatch latch;

    private String serverIP;
    private InetAddress serverAddress;
    private int tcpPort, udpPort;

    /**
     * Constructs a new client with given server IP configuration.
     * No network operation is done at this point.
     *
     * @param serverIP ip of the server machine
     */
    public Client(String serverIP) {
        this(serverIP, NetworkConfig.DEFAULT_TCP_PORT, NetworkConfig.DEFAULT_UDP_PORT);
    }

    /**
     * Constructs a new client with given server IP and
     * tcp/udp ports configuration.
     * No network operation is done at this point.
     *
     * @param serverIP ip of the server machine
     * @param tcpPort tcp port to use
     * @param udpPort udp port to use
     */
    public Client(String serverIP, int tcpPort, int udpPort) {
        this.serverIP = serverIP;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        tcpThread.setDaemon(true);
        udpThread.setDaemon(true);
    }

    /**
     * Performs an actual connection to the server
     *
     * @return true if connected and all is OK, false if something failed
     * @throws Exception
     */
    public boolean connect() throws Exception {
        serverAddress = InetAddress.getByName(serverIP);

        latch = new CountDownLatch(2);
        tcpThread.start();
        udpThread.start();
        return latch.await(10, TimeUnit.SECONDS);
    }

    /**
     * Sends a message to server that client is
     * about to disconnect and shuts down connection threads.
     * <p>
     * Further calls to {@link #send(Serializable)} will
     * throw IllegalStateException
     */
    public void disconnect() {
        sendClosingMessage();

        tcpThread.running = false;
        udpThread.running = false;
    }

    @Override
    public void close() {
        disconnect();
    }

    @Override
    protected void sendUDP(Serializable data) throws Exception {
        if (udpThread.running) {
            byte[] buf = toByteArray(data);
            udpThread.outSocket.send(new DatagramPacket(buf, buf.length, serverAddress, udpPort));
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
        private ObjectOutputStream outputStream;
        private boolean running = false;

        @Override
        public void run() {
            try (Socket socket = new Socket(serverIP, tcpPort);
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
            } catch (Exception e) {
                log.warn("Exception during TCP connection execution: " + e.getMessage());
                running = false;
                return;
            }

            log.debug("TCP connection closed normally");
        }
    }

    private class UDPConnectionThread extends Thread {
        private DatagramSocket outSocket;
        private boolean running = false;

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket()) {
                outSocket = socket;
                latch.countDown();
                running = true;

                sendUDP(ConnectionMessage.OPEN);

                while (running) {
                    byte[] buf = new byte[16384];
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    socket.receive(datagramPacket);

                    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()))) {
                        Object data = in.readObject();
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
                log.warn("Exception during UDP connection execution: " + e.getMessage());
                running = false;
                return;
            }

            log.debug("UDP connection closed normally");
        }
    }
}
