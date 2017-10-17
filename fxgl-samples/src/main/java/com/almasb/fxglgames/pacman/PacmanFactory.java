/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.pacman.control.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Factory for creating in-game entities.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class PacmanFactory implements TextEntityFactory {

    @SpawnSymbol('1')
    public Entity newBlock(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(PacmanType.BLOCK)
                .viewFromNodeWithBBox(new EntityView(new Rectangle(40, 40), RenderLayer.BACKGROUND))
                .build();
    }

    @SpawnSymbol('0')
    public Entity newCoin(SpawnData data) {
        EntityView view = new EntityView(FXGL.getAssetLoader().loadTexture("pacman/coin.png"));
        view.setTranslateX(2.5);
        view.setRenderLayer(RenderLayer.BACKGROUND);

        return Entities.builder()
                .from(data)
                .type(PacmanType.COIN)
                .bbox(new HitBox("Main", BoundingShape.box(40, 40)))
                .viewFromNodeWithBBox(view)
                .with(new CollidableComponent(true))
//                .with(new CollidableComponent(true), new DrawableComponent(g -> {
//                    g.setFill(Color.YELLOW);
//                    g.fillOval(x * PacmanApp.BLOCK_SIZE, y * PacmanApp.BLOCK_SIZE, 40, 40);
//                }))
                .build();
    }

    @SpawnSymbol('P')
    public Entity newPlayer(SpawnData data) {

        Rectangle view = new Rectangle(36, 36, Color.BLUE);
        view.setTranslateX(2);
        view.setTranslateY(2);

//        Texture view = FXGL.getAssetLoader()
//                .loadTexture("pacman/player.png")
//                .toStaticAnimatedTexture(2, Duration.seconds(0.33));

        return Entities.builder()
                .from(data)
                .type(PacmanType.PLAYER)
                .bbox(new HitBox("PLAYER_BODY", new Point2D(2, 2), BoundingShape.box(36, 36)))
                .viewFromNode(view)
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    private static List<Class<? extends Control> > enemyControls = Arrays.asList(
            DiffEnemyControl.class,
            AStarEnemyControl.class,
            EnemyControl.class
    );

    private static List<Integer> indices = new ArrayList<>();

    private static void populateIndices() {
        IntStream.range(0, enemyControls.size())
                .forEach(indices::add);

        Collections.shuffle(indices);
    }

    private static Control getNextEnemyControl() {
        Control control = null;

        try {
            if (indices.isEmpty()) {
                populateIndices();
                return new AIControl("pacman/pacman_enemy1.tree");
            }

            control = enemyControls.get(indices.remove(0)).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // won't happen
        }

        return control;
    }

    @SpawnSymbol('E')
    public Entity newEnemy(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(PacmanType.ENEMY)
                .bbox(new HitBox("ENEMY_BODY", new Point2D(2, 2), BoundingShape.box(36, 36)))
                .with(new CollidableComponent(true))
                .with(getNextEnemyControl(), new PaletteChangingControl(FXGL.getAssetLoader().loadTexture("pacman/spritesheet.png")))
                .build();
    }

    @Override
    public char emptyChar() {
        return ' ';
    }

    @Override
    public int blockWidth() {
        return 40;
    }

    @Override
    public int blockHeight() {
        return 40;
    }
}
