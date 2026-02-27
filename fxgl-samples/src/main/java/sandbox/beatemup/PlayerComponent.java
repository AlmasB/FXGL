/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.beatemup;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PlayerComponent extends Component {

    private AnimatedTexture textureLowerBody;
    private AnimatedTexture textureUpperBody;

    private AnimationChannel channelAttack;
    private AnimationChannel channelRun;

    private LocalTimer attackTimer = newLocalTimer();
    private boolean isAttacking = false;

    private Point2D prev;

    @Override
    public void onAdded() {
        var w = 536 / 4.0;
        var h = 495 / 4.0;

        channelAttack = new AnimationChannel(
                IntStream.rangeClosed(0, 9)
                        .mapToObj(i -> "beatemup/ninja/Attack__00" + i + ".png")
                        .map(imageName -> imageUpperBody(imageName, w, h))
                        .toList(),

                Duration.seconds(0.35)
        );

        var w2 = 363 / 4.0;
        var h2 = 458 / 4.0;

        channelRun = new AnimationChannel(
                IntStream.rangeClosed(0, 9)
                        .mapToObj(i -> "beatemup/ninja/Run__00" + i + ".png")
                        .map(imageName -> imageLowerBody(imageName, w2, h2))
                        .toList(),

                Duration.seconds(0.3)
        );

        textureLowerBody = new AnimatedTexture(channelRun);
        textureUpperBody = new AnimatedTexture(channelAttack);

        textureLowerBody.setTranslateY(h * 2 / 4);

        entity.getViewComponent().addChild(textureLowerBody);
        entity.getViewComponent().addChild(textureUpperBody);

        entity.setScaleOrigin(new Point2D(w2 / 2.0, h2 / 2.0));

        prev = entity.getPosition();
    }

    private Image imageLowerBody(String name, double w, double h) {
        var image = image(name, w, h);
        image = ImagesKt.subImage(image, new Rectangle2D(0, h / 2.0, w, h / 2.0));
        return image;
    }

    private Image imageUpperBody(String name, double w, double h) {
        var image = image(name, w, h);
        image = ImagesKt.subImage(image, new Rectangle2D(0, 0, w, h  * 2/3));
        return image;
    }

    @Override
    public void onUpdate(double tpf) {
        if (isAttacking) {
            if (attackTimer.elapsed(Duration.seconds(0.35))) {
                isAttacking = false;
            }
        }

        if (!entity.getPosition().equals(prev)) {
            textureLowerBody.loopNoOverride(channelRun);

            var v = entity.getPosition().subtract(prev);

            if (v.getX() > 0) {
                entity.setScaleX(1);
            } else {
                entity.setScaleX(-1);
            }

        } else {
            if (textureLowerBody.getAnimationChannel() == channelRun) {
                textureLowerBody.stop();
            }
        }

        prev = entity.getPosition();
    }

    public void attack() {
        if (isAttacking)
            return;

        isAttacking = true;
        attackTimer.capture();
        textureUpperBody.playAnimationChannel(channelAttack);
    }
}