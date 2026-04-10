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

public class ParticleScaleSample2 extends GameApplication{
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        //this circle has a difference in the x and the y points in the scale
        spawnMinor(new Point2D(450, 300), false);

        //this cricle has the same x and y points in the scale
        spawnMinor(new Point2D(450, 300), true);

        //both of their locations are the same, they should spawn in the same space. but because the x point is affected by the scale, the circles appear in different locations.
        //this shows the biggest issue with this bug, the x value is being affected by the y scale, when it should not be. 
    }

    private void spawnMinor(Point2D p, Boolean pointsTheSame) {
        var emitter = ParticleEmitters.newExplosionEmitter(17);

        //this sets the new points to be either the same or different
        if (pointsTheSame) {
            emitter.setEntityScaleFunction(() -> new Point2D(1, 1));
        } else {
            emitter.setEntityScaleFunction(() -> new Point2D(1, 2));
        }
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
