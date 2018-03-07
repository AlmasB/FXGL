/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.DrawableComponent;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.util.BiConsumer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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




    }

    @Override
    protected void initGame() {
        BiConsumer<GraphicsContext, Entity> drawing = (g, entity) -> {
            Point2D pos = entity.getPosition();

            g.setFill(Color.BLUE);
            g.fillRect(pos.getX(), pos.getY(), 40, 40);
        };


        Entity entity = new Entity();
        entity.setPosition(400, 300);
        entity.addComponent(new DrawableComponent(drawing));

        Entity entity2 = new Entity();
        entity2.setPosition(750, 300);
        entity2.addComponent(new DrawableComponent(drawing));

        getGameWorld().addEntities(entity, entity2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
