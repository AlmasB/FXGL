package intermediate.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGL.runOnce;
import static com.almasb.fxgl.dsl.FXGL.texture;

public class ParticleScaleSample extends GameApplication{
        @Override
        protected void initSettings(GameSettings settings) {
            settings.setWidth(1280);
            settings.setHeight(720);
        }

        @Override
        protected void initGame() {
            getGameScene().setBackgroundColor(Color.BLACK);

            spawnMinor(new Point2D(450, 300), false);
            spawnMinor(new Point2D(250, 300), true);
        }

        private void spawnMinor(Point2D p, Boolean corrected) {
            var emitter = ParticleEmitters.newExplosionEmitter(17);
            if (corrected){
                emitter.setCorrected(true);
            }
            emitter.setEntityScaleFunction(() -> new Point2D(1, 2));
            emitter.setExpireFunction(i -> Duration.seconds(10));
            emitter.setSize(10, 20);
            entityBuilder()
                    .at(p)
                    .with(new ParticleComponent(emitter))
                    .with(new ExpireCleanComponent(Duration.seconds(3)).animateOpacity())
                    .zIndex(100)
                    .buildAndAttach();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
