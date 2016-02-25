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
package com.almasb.fxgl.net;

import com.almasb.fxgl.util.FXGLLogger;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * MultiServer for multiple concurrent network connections (clients)
 * <p>
 * Since there isn't a 1 to 1 connection, {@link #stop()} must be explicitly
 * called to attempt a clean shutdown of the server
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class MultiServer extends NetworkConnection {
    private static final Logger log = FXGLLogger.getLogger("FXGL.MultiServer");

    private TCPConnectionThread tcpThread = new TCPConnectionThread();
    private UDPConnectionThread udpThread = new UDPConnectionThread();

    private final List<TCPThread> tcpThreads = Collections.synchronizedList(new ArrayList<>());

    private final List<FullInetAddress> addresses = Collections.synchronizedList(new ArrayList<>());
    private int tcpPort, udpPort;

    /**
     * Constructs and configures a multi server with default ports
     * No network operation is done at this point.
     */
    public MultiServer() {
        this(NetworkConfig.DEFAULT_TCP_PORT, NetworkConfig.DEFAULT_UDP_PORT);
    }

    /**
     * Constructs and configures a multi server with specified ports
     * No network operation is done at this point.
     *
     * @param tcpPort tcp port to use
     * @param udpPort udp port to use
     */
    public MultiServer(int tcpPort, int udpPort) {
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
        try {
            tcpThread.server.close();
        } catch (IOException ignored) {
        }

        tcpThreads.forEach(t -> t.running = false);
        udpThread.running = false;
    }

    @Override
    protected void sendUDP(Serializable data) throws Exception {
        if (udpThread.running) {
            byte[] buf = toByteArray(data);
            synchronized (addresses) {
                for (FullInetAddress addr : addresses) {
                    try {
                        udpThread.outSocket.send(new DatagramPacket(buf, buf.length, addr.address, addr.port));
                    } catch (Exception e) {
                        log.warning("Failed to send UDP message: " + e.getMessage());
                    }
                }
            }
        } else {
            throw new IllegalStateException("UDP connection not active");
        }
    }

    @Override
    protected void sendTCP(Serializable data) throws Exception {
        synchronized (tcpThreads) {
            tcpThreads.stream().filter(tcpThread -> tcpThread.running).forEach(tcpThread -> {
                try {
                    tcpThread.outputStream.writeObject(data);
                } catch (Exception e) {
                    log.warning("Failed to send TCP message: " + e.getMessage());
                }
            });
        }
    }

    private class TCPConnectionThread extends Thread {
        private boolean running = true;
        private ServerSocket server;

        @Override
        public void run() {
            try {
                server = new ServerSocket(tcpPort);
            } catch (Exception e) {
                log.warning("Exception during TCP connection creation: " + e.getMessage());
                running = false;
                return;
            }

            while (running) {
                try {
                    Socket socket = server.accept();
                    socket.setTcpNoDelay(true);

                    TCPThread t = new TCPThread(socket);
                    t.setDaemon(true);
                    tcpThreads.add(t);
                    t.start();
                } catch (Exception e) {
                    log.warning("Exception during TCP connection execution: " + e.getMessage());
                }
            }

            try {
                server.close();
            } catch (Exception ignored) {
            }
            log.info("TCP connection closed normally");
        }
    }

    private class TCPThread extends Thread {
        private boolean running = false;
        private ObjectOutputStream outputStream;
        private Socket socket;

        public TCPThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
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
                        outputStream.writeObject(ConnectionMessage.CLOSE);
                        running = false;
                        break;
                    }

                    parsers.getOrDefault(data.getClass(), d -> {
                    }).parse((Serializable) data);
                }
            } catch (Exception e) {
                log.warning("Exception during TCP connection execution: " + e.getMessage());
                running = false;
                tcpThreads.remove(this);
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                return;
            }

            tcpThreads.remove(this);
            try {
                socket.close();
            } catch (IOException ignored) {
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
                    try {
                        byte[] buf = new byte[16384];
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        socket.receive(datagramPacket);

                        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()))) {
                            Object data = in.readObject();
                            FullInetAddress addr = new FullInetAddress(datagramPacket.getAddress(), datagramPacket.getPort());

                            if (data == ConnectionMessage.OPEN) {
                                if (!addresses.contains(addr)) {
                                    addresses.add(addr);
                                }
                            }
                            if (data == ConnectionMessage.CLOSE) {
                                addresses.remove(addr);
                                continue;
                            }
                            if (data == ConnectionMessage.CLOSING) {
                                byte[] sendBuf = toByteArray(ConnectionMessage.CLOSE);
                                udpThread.outSocket.send(new DatagramPacket(sendBuf, sendBuf.length, addr.address, addr.port));
                                continue;
                            }

                            parsers.getOrDefault(data.getClass(), d -> {
                            }).parse((Serializable) data);
                        }
                    } catch (Exception e) {
                        log.warning("Exception during UDP connection execution: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warning("Exception during UDP connection execution: " + e.getMessage());
                running = false;
                return;
            }

            log.info("UDP connection closed normally");
        }
    }

    private static class FullInetAddress {
        private InetAddress address;
        private int port;

        public FullInetAddress(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof FullInetAddress) {
                FullInetAddress other = (FullInetAddress) obj;
                return this.address.getHostAddress().equals(
                        other.address.getHostAddress())
                        && this.port == other.port;
            }
            return false;
        }
    }
}
