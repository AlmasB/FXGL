/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.nio.ByteBuffer;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WritableImageSample extends GameApplication {

    private byte[] rawBytes = new byte[4*600*600];

    private ByteBuffer buffer;
    private PixelBuffer<ByteBuffer> pixelBuffer;
    private WritableImage image;

    private WritableImage fxImage;

    private Canvas canvas;

    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {
        buffer = ByteBuffer.allocate(4 * 600 * 600);
        pixelBuffer = new PixelBuffer<>(600, 600, buffer, PixelFormat.getByteBgraPreInstance());
        image = new WritableImage(pixelBuffer);

        canvas = new Canvas(600, 600);

        fxImage = new WritableImage(600, 600);

        //FXGL.addUINode(new ImageView(fxImage));
        FXGL.addUINode(new ImageView(image));
    }

    @Override
    protected void onUpdate(double tpf) {
//        for (int y = 0; y < fxImage.getHeight(); y++) {
//            for (int x = 0; x < fxImage.getWidth(); x++) {
//                fxImage.getPixelWriter().setColor(x, y, FXGLMath.randomColor());
//
//                //canvas.getGraphicsContext2D().setFill(FXGLMath.randomColor());
//                //canvas.getGraphicsContext2D().fillRect(x, y, 1, 1);
//            }
//        }




        for (int i = 0; i < rawBytes.length; i++) {
            rawBytes[i] = (byte) FXGLMath.random(0, 255);
        }

        buffer.clear();
        buffer.put(rawBytes);
        buffer.rewind();

        pixelBuffer.updateBuffer(buffer -> {
            return null;
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
