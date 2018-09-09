package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.Particle;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.texture;
import static com.almasb.fxgl.core.math.FXGLMath.*;

public class ParticleEquationSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    private ObjectMap<Particle, Double> indices = new ObjectMap<>();

    @Override
    protected void initGame() {
        Entities.builder()
                .viewFromNode(new Rectangle(getWidth(), getHeight()))
                .buildAndAttach();





        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setAllowParticleRotation(true);
        //emitter.setInterpolator(Interpolators.BOUNCE.EASE_OUT());
        //emitter.setScaleFunction(i -> Point2D.ZERO);
        emitter.setBlendMode(BlendMode.SRC_OVER);

        emitter.setVelocityFunction(i -> randomPoint2D().multiply(1.5));
        //emitter.setVelocityFunction(i -> new Point2D(1, -1).multiply(0.5));

        emitter.setSourceImage(texture("particles/light_01.png", 32, 32).brighter().brighter().saturate().getImage());
        emitter.setSize(1.5, 8.5);
        emitter.setNumParticles(15);
        emitter.setEmissionRate(1);
        emitter.setExpireFunction(i -> Duration.seconds(random(3,7)));
        emitter.setControl(p -> {

            double index = indices.get(p, random(0.001, 3.05));

            indices.put(p, index);

            double x = p.position.x;
            double y = p.position.y;

            double noiseValue = FXGLMath.noise3D(x / 300, y / 300, (1 - p.life) * index);
            double angle = toDegrees((noiseValue + 1) * Math.PI * 1.5);

            angle %= 360;

            if (randomBoolean(0.35)) {
                angle = map(angle, 0, 360, -100, 50);
            }
            //System.out.println(angle);

            Vec2 v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(random(0.05, 0.35));

            p.acceleration.set(v);
        });

        Entity e = new Entity();

        e.setPosition(-100, 600);
        e.addComponent(new ParticleComponent(emitter));

        getGameWorld().addEntity(e);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
