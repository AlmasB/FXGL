/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.scene3d.Cuboid;
import com.almasb.fxgl.scene3d.SkyboxBuilder;
import com.almasb.fxgl.texture.ColoredTexture;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to build and use a skybox in 3D space.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SkyboxSample extends GameApplication {

    private Camera3D camera3D;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.set3D(true);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> {
            camera3D.moveForward();
        });
        onKey(KeyCode.S, () -> {
            camera3D.moveBack();
        });
        onKey(KeyCode.A, () -> {
            camera3D.moveLeft();
        });
        onKey(KeyCode.D, () -> {
            camera3D.moveRight();
        });

        onKey(KeyCode.L, () -> {
            getGameController().exit();
        });
    }

    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();
        camera3D.getPerspectiveCamera().setFarClip(500000);

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        var pixels = new ColoredTexture(1024, 1024, Color.RED)
                .pixels()
                .stream()
                .map(p -> p.copy(FXGLMath.randomColor()))
                .collect(Collectors.toList());

        // can be any image, including texture cube map
        var image = ImagesKt.fromPixels(1024, 1024, pixels);

        var skybox = new SkyboxBuilder(1024)
                .front(image)
                .back(image)
                .left(image)
                .right(image)
                .top(image)
                .bot(image)
//                .front("skybox/sk_06.png")
//                .back("skybox/sk_08.png")
//                .left("skybox/sk_05.png")
//                .right("skybox/sk_07.png")
//                .top("skybox/sk_02.png")
//                .bot("skybox/sk_10.png")
                .buildImageSkybox();

        entityBuilder()
                .view(skybox)
                .buildAndAttach();

        // cube entity
        var e = entityBuilder()
                .view(new Cuboid(2, 2, 2))
                .buildAndAttach();

        animationBuilder()
                .autoReverse(true)
                .repeatInfinitely()
                .duration(Duration.seconds(12))
                .rotate(e)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(0, 360, 0))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
