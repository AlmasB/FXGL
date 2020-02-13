/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalApp extends GameApplication {

    private enum Type {
        PLAYER, CRYSTAL
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Crystal Chase");
        settings.setWidthFromRatio(16/9.0);
        settings.setManualResizeEnabled(true);
    }

    private List<Node> pixels;

    double delayIndex = 0;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            delayIndex = 0.0;

            pixels.stream()
                    .sorted(Comparator.comparingDouble(p -> p.getLayoutY()))
                    .forEach(p -> {
                        //p.setBlendMode(BlendMode.ADD);

                        animationBuilder()
                                .duration(Duration.seconds(0.2))
                                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                                .onFinished(() -> {
                                    animationBuilder()
                                            .delay(Duration.seconds(random(delayIndex, delayIndex + 0.1)))
                                            .duration(Duration.seconds(2.5))
                                            .interpolator(Interpolators.BOUNCE.EASE_IN_OUT())
                                            .translate(p)
                                            .alongPath(new CubicCurve(p.getTranslateX(), p.getTranslateY(),
                                                    random(200, 300), random(-200, 20),
                                                    random(600, 700), random(500, 700),
                                                    650, 150))
                                            .buildAndPlay();

                                    delayIndex += 0.0001;
                                })
                                .scale(p)
                                .from(new Point2D(1, 1))
                                .to(new Point2D(3, 3))
                                .buildAndPlay();
                    });

//            pixels.parallelStream()
//                    .forEach(p -> {
//
//                    });
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        var texture = texture("robot_stand.png").subTexture(new Rectangle2D(50, 30, 200, 220));

        pixels = ImagesKt.toPixels(texture.getImage())
                .stream()
                .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .map(p -> {
                    var r = new Rectangle(1, 1, p.getColor());
                    r.setLayoutX(p.getX());
                    r.setLayoutY(p.getY());
                    return r;
                })
                .collect(Collectors.toList());

        pixels.forEach(p -> {
            p.getProperties().put("vel", new Vec2());
            addUINode(p, 250, 150);
        });
    }

    double t = 0.0;
    boolean up = true;

    @Override
    protected void onUpdate(double tpf) {
        if (up) {
            t += tpf;
        } else {
            t -= tpf;
        }

        if (t > 7) {
            up = false;
        }

        if (t < 1) {
            up = true;
        }

        // perlin noise
//        pixels.forEach(p -> {
//            var x = p.getTranslateX();
//            var y = p.getTranslateY();
//
//            var noiseValue = FXGLMath.noise2D(x * 0.002 * t, y * 0.002 * t);
//            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);
//
//            angle %= 360.0;
//
//            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 25));
//
//            Vec2 velocity = (Vec2) p.getProperties().get("vel");
//
//            var vx = velocity.x * 0.8f + v.x * 0.2f;
//            var vy = velocity.y * 0.8f + v.y * 0.2f;
//
//            velocity.x = vx;
//            velocity.y = vy;
//
//            p.setTranslateX(x + velocity.x);
//            p.setTranslateY(y + velocity.y);
//        });
    }

    private void spawnCrystal() {

        var e = entityBuilder().at(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 55, getAppHeight() - 55)))
                .type(Type.CRYSTAL)
                .view(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(0.66)).loop())
                .with(new DraggableComponent())
                .build();

        e.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            despawnWithScale(e, Duration.seconds(1), Interpolators.ELASTIC.EASE_IN());
        });

        spawnWithScale(e, Duration.seconds(1), Interpolators.ELASTIC.EASE_OUT());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
