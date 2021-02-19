/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import dev.DeveloperWASDControl;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * PoC for a time-manipulation game.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TimeGameApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1066);
    }

    private Entity player;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            var buffer = player.getComponent(TimeBufferComponent.class).buffer;
            Point2D pos = buffer.getFirst();

            //player.setPosition(pos);
            animationBuilder()
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .translate(player)
                    .from(player.getPosition())
                    .to(pos)
                    .buildAndPlay();

            var e = spawnRect(pos);
            e.addComponent(new TimerBufferApplierComponent(new ArrayDeque<>(buffer)));
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        player = spawnRect(Point2D.ZERO);
        player.addComponent(new DeveloperWASDControl());
    }

    private Entity spawnRect(Point2D pos) {
        return entityBuilder()
                .at(pos)
                .view(new Rectangle(40, 40, FXGLMath.randomColor()))
                .with(new TimeBufferComponent())
                .buildAndAttach();
    }

    private static class TimeBufferComponent extends Component {
        private Deque<Point2D> buffer = new ArrayDeque<>(300);

        @Override
        public void onUpdate(double tpf) {
            buffer.addLast(entity.getPosition());

            if (buffer.size() > 300) {
                buffer.removeFirst();
            }
        }
    }

    private static class TimerBufferApplierComponent extends Component {
        private Deque<Point2D> buffer;

        private TimerBufferApplierComponent(Deque<Point2D> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void onUpdate(double tpf) {
            if (buffer.isEmpty())
                return;

            Point2D p = buffer.removeFirst();

            entity.setPosition(p);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
