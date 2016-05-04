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

package s29networking;

import com.almasb.fxgl.app.ServerApplication;
import com.jme3.network.serializing.Serializer;
import javafx.application.Application;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ServerMain extends ServerApplication {

    public static void main(String[] args) throws Exception {
        Serializer.registerClass(UpdateMessage.class);
        Serializer.registerClass(CommandMessage.class);

        ServerMain app = new ServerMain();
        app.addMessageListener((source, m) -> {
            if (m instanceof CommandMessage) {
                CommandMessage message = (CommandMessage) m;

                // TODO:
            }
        });
        app.launch();

        Application.launch(NetworkingSample.class, args);
    }

    private double x = 0, dx = 1;

    @Override
    public void onUpdate(double tpf) {
        x += dx;

        if (x > 800) {
            dx = -1;
        } else if (x < 0) {
            dx = 1;
        }

        broadcast(new UpdateMessage(x));
    }
}
