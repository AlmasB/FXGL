/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package s19physicsparticles;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsParticleComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleType;

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
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        initGround();
        initCloth();
        initLiquid();
    }

    private void initGround() {
        GameEntity ground = new GameEntity();
        ground.getPositionComponent().setValue(100, 500);
        ground.getMainViewComponent().setView(new EntityView(new Rectangle(800, 100)), true);
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
        GameEntity cloth = new GameEntity();
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

        GameEntity liquid = new GameEntity();
        liquid.getPositionComponent().setValue(300, 10);
        liquid.getBoundingBoxComponent().addHitBox(new HitBox("MAIN", BoundingShape.circle(35)));
        liquid.addComponent(ppComponent);

        getGameWorld().addEntities(liquid);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
