/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package s31pacman;

import com.almasb.ents.Control;
import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import s31pacman.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EntityFactory {

    private static RenderLayer BG = new RenderLayer() {
        @Override
        public String name() {
            return "BG";
        }

        @Override
        public int index() {
            return 0;
        }
    };

    public static GameEntity newBlock(double x, double y) {
        EntityView view = new EntityView(new Rectangle(40, 40));
        view.setRenderLayer(BG);

        return Entities.builder()
                .type(EntityType.BLOCK)
                .viewFromNodeWithBBox(view)
                .at(x * PacmanApp.BLOCK_SIZE, y * PacmanApp.BLOCK_SIZE)
                .build();
    }

    public static GameEntity newCoin(double x, double y) {
        EntityView view = new EntityView(new Circle(20, Color.YELLOW));
        view.setRenderLayer(BG);

        return Entities.builder()
                .type(EntityType.COIN)
                .viewFromNodeWithBBox(view)
                .at(x * PacmanApp.BLOCK_SIZE, y * PacmanApp.BLOCK_SIZE)
                .with(new CollidableComponent(true))
                .build();
    }

    public static GameEntity newPlayer(double x, double y) {
        Rectangle view = new Rectangle(36, 36, Color.BLUE);
        view.setTranslateX(2);
        view.setTranslateY(2);

        return Entities.builder()
                .type(EntityType.PLAYER)
                .bbox(new HitBox("PLAYER_BODY", new Point2D(2, 2), BoundingShape.box(36, 36)))
                .viewFromNode(view)
                .at(x * PacmanApp.BLOCK_SIZE, y * PacmanApp.BLOCK_SIZE)
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    private static List<Class<? extends Control> > enemyControls = Arrays.asList(
            AStarEnemyControl.class,
            EnemyControl.class,
            //MirrorEnemyControl.class,
            CombinedControl.class,
            DiffEnemyControl.class
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
                return new AIControl("pacman_enemy1.tree");
            }

            control = enemyControls.get(indices.remove(0)).newInstance();
        } catch (Exception e) {
            // wont happen
        }

        return control;
    }

    public static GameEntity newEnemy(double x, double y) {
        Rectangle view = new Rectangle(36, 36, Color.RED);
        view.setTranslateX(2);
        view.setTranslateY(2);

        return Entities.builder()
                .type(EntityType.ENEMY)
                .bbox(new HitBox("ENEMY_BODY", new Point2D(2, 2), BoundingShape.box(36, 36)))
                .viewFromNode(view)
                .at(x * PacmanApp.BLOCK_SIZE, y * PacmanApp.BLOCK_SIZE)
                .with(new CollidableComponent(true))
                .with(getNextEnemyControl())
                .build();
    }
}
