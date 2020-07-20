/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.messagehandlers;

import com.almasb.fxgl.net.MessageHandler;

/**
 * TODO: check out / in work
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public abstract class StringMessageHandler extends MessageHandler<String> {

//    private PrintWriter out;
//    private DataInputStream in;

//    @Override
//    public final void onInitialize(OutputStream out, InputStream in) {
//        try {
//            this.out = new PrintWriter(out, true);
//            this.in = new DataInputStream(in);
//        } catch (Exception e) {
//            log.warning("Cannot construct out / in", e);
//        }
//    }
//
//    @Override
//    public final void send(String message) {
//        try {
//            out.write(message);
//        } catch (Exception e) {
//            log.warning("Error during write(): "+ e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public final void read() throws EOFException {
//        if (in == null) {
//            log.warning("Input stream is not initialized");
//            return;
//        }
//
//        try {
//            var string = in.readUTF();
//
//            received(string);
//
//        } catch (EOFException e) {
//            throw e;
//        } catch (Exception e) {
//            log.warning("Error during read(): "+ e.getMessage(), e);
//        }
//    }
}

