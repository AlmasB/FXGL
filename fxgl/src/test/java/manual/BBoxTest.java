/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package manual;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BBoxTest extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BBoxTest");
        settings.setMenuEnabled(false);
        settings.setIntroEnabled(false);
        settings.setFullScreenAllowed(false);
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initGame() {
        Entity e1 = new Entity();
        e1.setPosition(new Point2D(100, 100));
        e1.getBoundingBoxComponent().addHitBox(new HitBox("HEAD", new Point2D(50, 0), BoundingShape.box(50, 80)));
        e1.getBoundingBoxComponent().addHitBox(new HitBox("ARM", BoundingShape.box(30, 30)));
        e1.setView(new Rectangle(100, 100));

        Entity e2 = new Entity();
        e2.getPositionComponent().setValue(50, 50);
        e2.getBoundingBoxComponent().addHitBox(new HitBox("ARM", BoundingShape.circle(20)));
        e2.getViewComponent().setView(new Rectangle(40, 60, Color.YELLOWGREEN));

        getGameWorld().addEntities(e1, e2);
    }

    @Override
    protected void initPhysics() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {

    }
}
