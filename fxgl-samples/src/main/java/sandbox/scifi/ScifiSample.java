/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.gameplay.qte.QTE;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsParticleComponent;
import com.almasb.fxgl.physics.box2d.particle.ParticleGroupDef;
import com.almasb.fxgl.physics.box2d.particle.ParticleType;
import com.almasb.fxgl.physics.handler.CollectibleHandler;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.ui.InGamePanel;
import com.almasb.fxgl.ui.LevelText;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import static com.almasb.fxgl.app.DSLKt.geti;
import static com.almasb.fxgl.app.DSLKt.set;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScifiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("Feature Showcase");
        settings.setVersion("0.1");
        settings.setFullScreen(true);
        settings.setIntroEnabled(true);
        settings.setMenuEnabled(true);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private PlayerControl playerControl;

    @Override
    protected void initAchievements() {
        getGameplay().getAchievementManager().registerAchievement(new Achievement("Collector", "Collect 10 coins"));
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }

            @Override
            protected void onActionEnd() {
                playerControl.stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }

            @Override
            protected void onActionEnd() {
                playerControl.stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Animate Level Text") {
            @Override
            protected void onActionBegin() {
                levelText.animateIn();
            }

            @Override
            protected void onActionEnd() {
                levelText.animateOut();
            }
        }, KeyCode.G);

        getInput().addAction(new UserAction("Open/Close Panel") {
            @Override
            protected void onActionBegin() {
                if (panel.isOpen())
                    panel.close();
                else
                    panel.open();
            }
        }, KeyCode.TAB);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("coins", 0);
    }

    @Override
    protected void initGame() {

        // assets from https://raventale.itch.io/parallax-background
        getGameScene().addGameView(new ParallaxBackgroundView(Arrays.asList(
                new ParallaxTexture(getAssetLoader().loadTexture("bg_10.png", getWidth(), getHeight()), 1.0),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_9.png", getWidth(), getHeight()), 0.05),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_8.png", getWidth(), getHeight()), 0.1),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_7.png", getWidth(), getHeight()), 0.3),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_6.png", getWidth(), getHeight()), 0.45),
                //new ParallaxTexture(getAssetLoader().loadTexture("bg_5.png", getWidth(), getHeight()), 0.45),
                //new ParallaxTexture(getAssetLoader().loadTexture("bg_4.png", getWidth(), getHeight()), 0.6),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_3.png", getWidth(), getHeight()), 0.5),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_2.png", getWidth(), getHeight()), 0.7),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_1.png", getWidth(), getHeight()), 0.8),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_0.png", getWidth(), getHeight()), 0.9)
        ), Orientation.HORIZONTAL, BG));

        nextLevel();

        getGameplay().getAchievementManager()
                .getAchievementByName("Collector")
                .bind(getGameState().intProperty("coins"), 10);

        //getMasterTimer().runOnceAfter(this::seerCutscene, Duration.seconds(7));
    }

    private int level = 1;

    private RenderLayer BG = new RenderLayer() {
        @Override
        public String name() {
            return "bg";
        }

        @Override
        public int index() {
            return 900;
        }
    };

    private void nextLevel() {
        getGameWorld().setLevelFromMap("mario" + level + ".json");

        Entity player = getGameWorld().getEntitiesByType(ScifiType.PLAYER).get(0);
        playerControl = player.getControl(PlayerControl.class);

        getGameScene().getViewport().setBounds(0, 0, 3000, getHeight());
        getGameScene().getViewport().bindToEntity(player, 500, 0);

        level++;
        if (level == 4)
            level = 1;

        initUI();
    }

    private void seerCutscene() {
        // TODO: on cutscene end()
        // TODO: also why does player keep moving, input not cleared?
        getGameplay().getCutsceneManager().startCutscene("seer.txt");

        AnimatedTexture anim = getAssetLoader().loadTexture("player.png").toAnimatedTexture(3, Duration.seconds(0.5));

        // TODO: wrong behavior?
        anim.start(getStateMachine().getCurrentState());
        anim.setScaleX(-3);
        anim.setScaleY(3);
        anim.setTranslateX(getWidth() / 2);
        anim.setTranslateY(getHeight() / 2);

        getGameScene().addUINode(anim);

        Animation<?> anim2 = getUIFactory().fadeOut(anim, Duration.seconds(1.75));
        anim2.setOnFinished(() -> {
            getGameScene().removeUINode(anim);

            QTE qte = getGameplay().getQTE();

            qte.start(yes -> {

            }, Duration.seconds(3), KeyCode.W, KeyCode.W, KeyCode.D, KeyCode.A);
        });
        anim2.startInPlayState();

        addRain();
        initLiquid();
    }

    private void addRain() {
        ParticleEmitter emitter = ParticleEmitters.newRainEmitter(getWidth());
        emitter.setSourceImage(getAssetLoader().loadTexture("rain.png").multiplyColor(Color.LIGHTBLUE).getImage());

        Entity rain = Entities.builder()
                .with(new ParticleControl(emitter))
                .buildAndAttach(getGameWorld());

        rain.getPositionComponent().xProperty().bind(getGameScene().getViewport().xProperty());
        rain.getPositionComponent().yProperty().bind(getGameScene().getViewport().yProperty());
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ScifiType.PLAYER, ScifiType.PLATFORM) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxA.getName().equals("lower")) {
                    playerControl.canJump = true;
                }
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ScifiType.PLAYER, ScifiType.PORTAL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                nextLevel();
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollectibleHandler(ScifiType.PLAYER, ScifiType.COIN, "drop.wav",
                (c) -> set("coins", geti("coins") + 1)));
    }

    private LevelText levelText;
    private InGamePanel panel;

    @Override
    protected void initUI() {
        getGameScene().removeUINode(levelText);
        getGameScene().removeUINode(panel);

        levelText = new LevelText("Level " + (level-1));
        getGameScene().addUINode(levelText);

        panel = new InGamePanel();

        Text text = getUIFactory().newText("No Network Connection", Color.BLACK, 24.0);
        text.setTranslateX(getWidth() / 3 / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setTranslateY(getHeight() / 2);
        panel.getChildren().add(text);

        getGameScene().addUINode(panel);
    }

    private void initLiquid() {
        ParticleGroupDef groupDef = new ParticleGroupDef();
        groupDef.setTypes(EnumSet.of(ParticleType.WATER));

        PhysicsParticleComponent ppComponent = new PhysicsParticleComponent();
        ppComponent.setDefinition(groupDef);
        ppComponent.setColor(Color.BLUE.brighter());

        Entity liquid = new Entity();
        liquid.setPosition(playerControl.getEntity().getComponent(PositionComponent.class).getValue().subtract(0, 650));
        liquid.getBoundingBoxComponent().addHitBox(new HitBox("MAIN", BoundingShape.circle(55)));
        liquid.addComponent(ppComponent);

        getGameWorld().addEntities(liquid);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
