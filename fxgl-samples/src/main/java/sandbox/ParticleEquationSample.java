package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.shape.Rectangle;

public class ParticleEquationSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {
        Entities.builder()
                .viewFromNode(new Rectangle(getWidth(), getHeight()))
                .buildAndAttach();

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setAllowParticleRotation(true);

        Entity e = new Entity();

        e.setPosition(300, 300);
        e.addComponent(new ParticleComponent(emitter));

        getGameWorld().addEntity(e);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
