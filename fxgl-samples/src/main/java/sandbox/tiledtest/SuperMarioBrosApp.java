/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiledtest;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.input.virtual.VirtualControllerOverlay;
import com.almasb.fxgl.input.virtual.VirtualControllerStyle;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.spawn;
import static com.almasb.fxgl.app.DSLKt.texture;
import static com.almasb.fxgl.core.math.FXGLMath.random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SuperMarioBrosApp extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1400);
        settings.setHeight(700);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(MarioComponent.class).left();
            }
        }, KeyCode.A, VirtualButton.X);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(MarioComponent.class).right();
            }
        }, KeyCode.D, VirtualButton.B);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(MarioComponent.class).jump();
            }
        }, KeyCode.W, VirtualButton.Y);

        getInput().addAction(new UserAction("Activate") {
            @Override
            protected void onActionBegin() {
                spawn("bullet", player.getPosition());
//                getGameWorld().getCollidingEntities(player)
//                        .stream()
//                        .filter(e -> e.hasComponent(ActivatorComponent.class))
//                        .map(e -> e.getComponent(ActivatorComponent.class))
//                        .forEach(c -> c.activate(player));
            }
        }, KeyCode.F, VirtualButton.A);
    }

    @Override
    protected void initGame() {

        getGameScene().setBackgroundColor(Color.rgb(92, 148, 252));

        getGameWorld().addEntityFactory(new TiledFactory());
        getGameWorld().setLevelFromMap("marioHD.tmx");

        player = spawn("player", 15, 10);

        getGameScene().getViewport().setBounds(0, 0, 212*70, getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 2, getHeight() / 2);


//        Entities.builder()
//                .at(500, 80)
//                .viewFromNode(texture("particles/trace_02_rotated.png"))
//                .buildAndAttach();


        ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(250);

        Texture t = texture("particles/trace_02_rotated.png", 64, 64);

        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(t.getImage());
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setSize(16, 64);
        emitter.setNumParticles(16);
        emitter.setEmissionRate(0.01);
        emitter.setVelocityFunction(i -> new Point2D(Math.cos(i), Math.sin(i)).multiply(100));
        emitter.setExpireFunction((i) -> Duration.seconds(random(5, 5)));
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
//        emitter.setScaleFunction((i) -> new Point2D(-0.03, -0.03));
//        emitter.setSpawnPointFunction((i) -> new Point2D(random(-5, 5), random(0, 0)));
        emitter.setAccelerationFunction(() -> new Point2D(random(0,0), random(20, 25)));
        emitter.setAllowParticleRotation(true);

        Entity e = new Entity();
        e.setPosition(250, 300);


        player.addComponent(new ParticleComponent(emitter));

        getGameWorld().addEntity(e);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 1750);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EType.PLAYER, EType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity pc, Entity enemy) {
                if (pc.getY() < enemy.getY()) {
                    //System.out.println("KILL");

                    player.getComponent(MarioComponent.class).jump();
                    enemy.removeFromWorld();

                } else {
                    System.out.println("DEAD");
                }
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EType.PLAYER, EType.CRATE) {
            @Override
            protected void onCollisionBegin(Entity pc, Entity crate) {
                if (pc.getY() > crate.getBottomY()) {
                    crate.getComponent(CrateComponent.class).bump();
                }
            }
        });
    }

    @Override
    protected void initUI() {
        VirtualControllerOverlay vcOverlay = new VirtualControllerOverlay(VirtualControllerStyle.XBOX);
        vcOverlay.setTranslateY(500);

        getGameScene().addUINodes( vcOverlay);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
