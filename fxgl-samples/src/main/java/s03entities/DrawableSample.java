/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.DrawableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.function.BiConsumer;

/**
 * Drawing entities directly to the graphics context.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DrawableSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("DrawableSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {
        BiConsumer<GraphicsContext, Entity> drawing = (g, entity) -> {
            Point2D pos = entity.getComponent(PositionComponent.class).getValue();

            g.setFill(Color.BLUE);
            g.fillRect(pos.getX(), pos.getY(), 40, 40);
        };


        Entity entity = new Entity();
        entity.addComponent(new PositionComponent(400, 300));
        entity.addComponent(new DrawableComponent(drawing));

        Entity entity2 = new Entity();
        entity2.addComponent(new PositionComponent(750, 300));
        entity2.addComponent(new DrawableComponent(drawing));

        getGameWorld().addEntities(entity, entity2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
