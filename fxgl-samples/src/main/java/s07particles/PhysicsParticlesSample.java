/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s07particles;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsParticleComponent;
import com.almasb.fxgl.physics.box2d.particle.ParticleGroupDef;
import com.almasb.fxgl.physics.box2d.particle.ParticleType;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;

/**
 * Shows how to use physics particles.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PhysicsParticlesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("PhysicsParticlesSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        initGround();
        initCloth();
        initLiquid();
    }

    private void initGround() {
        Entity ground = new Entity();
        ground.getPositionComponent().setValue(100, 500);
        ground.getViewComponent().setView(new EntityView(new Rectangle(800, 100)), true);
        ground.addComponent(new PhysicsComponent());

        getGameWorld().addEntity(ground);
    }

    private void initCloth() {
        // 1. define how particles should behave
        ParticleGroupDef groupDef = new ParticleGroupDef();
        groupDef.setTypes(EnumSet.of(ParticleType.ELASTIC));

        // 2. create component and set data
        PhysicsParticleComponent ppComponent = new PhysicsParticleComponent();
        ppComponent.setDefinition(groupDef);
        ppComponent.setColor(Color.DARKGREEN.brighter());

        // 3. create entity, place it and specify volume of particles via bounding box
        Entity cloth = new Entity();
        cloth.getPositionComponent().setValue(150, 10);
        cloth.getBoundingBoxComponent().addHitBox(new HitBox("MAIN", BoundingShape.box(75, 150)));

        // 4. add component
        cloth.addComponent(ppComponent);

        getGameWorld().addEntity(cloth);
    }

    private void initLiquid() {
        ParticleGroupDef groupDef = new ParticleGroupDef();
        groupDef.setTypes(EnumSet.of(ParticleType.VISCOUS, ParticleType.TENSILE));

        PhysicsParticleComponent ppComponent = new PhysicsParticleComponent();
        ppComponent.setDefinition(groupDef);
        ppComponent.setColor(Color.BLUE.brighter());

        Entity liquid = new Entity();
        liquid.getPositionComponent().setValue(300, 10);
        liquid.getBoundingBoxComponent().addHitBox(new HitBox("MAIN", BoundingShape.circle(35)));
        liquid.addComponent(ppComponent);

        getGameWorld().addEntities(liquid);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
