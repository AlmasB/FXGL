/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.ai.pathfinding.maze.Maze;
import com.almasb.fxgl.ai.pathfinding.maze.MazeCell;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.app.DSLKt.texture;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Iso2DSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1800);
        settings.setHeight(920);
        settings.setTitle("Iso2DSample");
        settings.setVersion("0.1");




    }

    @Override
    protected void initInput() {
        DSLKt.onKey(KeyCode.A, "a", () -> {
            getGameScene().getViewport().setX(getGameScene().getViewport().getX() - 5);
        });

        DSLKt.onKey(KeyCode.D, "d", () -> {
            getGameScene().getViewport().setX(getGameScene().getViewport().getX() + 5);
        });

        DSLKt.onKey(KeyCode.W, "w", () -> {
            getGameScene().getViewport().setY(getGameScene().getViewport().getY() - 5);
        });

        DSLKt.onKey(KeyCode.S, "s", () -> {
            getGameScene().getViewport().setY(getGameScene().getViewport().getY() + 5);
        });
    }

    private Maze maze;

    @Override
    protected void initGame() {
        maze = new Maze(10, 10);



        // 256 / 149 ?

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                MazeCell cell = maze.getMazeCell(x, y);

                makeRoom(cell, 2);
            }
        }
    }

    private void makeRoom(MazeCell cell, int scaleRatio) {
        double scale = 5;
        double dx = 16;
        double dy = 18.5;

        double ox = -150;

        int oldX = cell.getX() * scaleRatio;
        int oldY = cell.getY() * scaleRatio;

        for (int y = oldY; y < oldY + scaleRatio; y++) {
            for (int x = oldX; x < oldX + scaleRatio; x++) {

                // room "ground"
                {
                    Texture texture = texture("iso/stone.png", 32 * scale, 64 * scale);

                    Point2D iso = toIso(new Point2D(x, y));

                    texture.setTranslateX(getWidth() / 2 + iso.getX() * dx * scale);
                    texture.setTranslateY(ox + iso.getY() * dy * scale);

                    getGameScene().addGameView(new EntityView(texture));
                }

                if (x == oldX && y == oldY && cell.hasLeftWall() && cell.hasTopWall()) {
                    Texture texture = texture("iso/cornerTopLeft.png", 32 * scale, 64 * scale);

                    Point2D iso = toIso(new Point2D(x, y));

                    texture.setTranslateX(getWidth() / 2 + iso.getX() * dx * scale);
                    texture.setTranslateY(ox + iso.getY() * dy * scale);

                    getGameScene().addGameView(new EntityView(texture));
                } else {
                    if (x == oldX && cell.hasLeftWall()) {
                        Texture texture = texture("iso/leftWall.png", 32 * scale, 64 * scale);

                        Point2D iso = toIso(new Point2D(x, y));

                        texture.setTranslateX(getWidth() / 2 + iso.getX() * dx * scale);
                        texture.setTranslateY(ox + iso.getY() * dy * scale);

                        getGameScene().addGameView(new EntityView(texture));
                    }

                    if (y == oldY && cell.hasTopWall()) {
                        Texture texture = texture("iso/topWall.png", 32 * scale, 64 * scale);

                        Point2D iso = toIso(new Point2D(x, y));

                        texture.setTranslateX(getWidth() / 2 + iso.getX() * dx * scale);
                        texture.setTranslateY(ox + iso.getY() * dy * scale);

                        getGameScene().addGameView(new EntityView(texture));
                    }
                }




            }
        }





//
//        if (x == 9) {
//            Texture texture = texture("iso/leftWall.png", 32 * scale, 64 * scale);
//
//            Point2D iso = toIso(new Point2D(x+1, y));
//
//            texture.setTranslateX(getWidth() / 2 + iso.getX() * dx * scale);
//            texture.setTranslateY(ox + iso.getY() * dy * scale);
//
//            getGameScene().addGameView(new EntityView(texture));
//        }
//
//        if (y == 9) {
//            Texture texture = texture("iso/topWall.png", 32 * scale, 64 * scale);
//
//            Point2D iso = toIso(new Point2D(x, y+1));
//
//            texture.setTranslateX(getWidth() / 2 + iso.getX() * dx * scale);
//            texture.setTranslateY(ox + iso.getY() * dy * scale);
//
//            getGameScene().addGameView(new EntityView(texture));
//        }
    }

    private Point2D toIso(Point2D p) {
        return new Point2D(p.getX() - p.getY(), (p.getX() + p.getY()) / 2);
    }

    @Override
    protected void initUI() {
//        Texture block = texture("iso/block (1).png", 504 / 10, 540 /  10);
//        Texture block2 = texture("iso/block (1).png", 504 / 10, 540 /  10);
//
//        // 50 x 54
//
//        block.setTranslateX(100);
//        block.setTranslateY(100);
//
//        block2.setTranslateX(125);
//        block2.setTranslateY(115);
//
//        getGameScene().addGameView(new EntityView(block));
//        getGameScene().addGameView(new EntityView(block2));

//        double scale = 1.0;
//
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                Texture block = DSLKt.texture(FXGLMath.randomBoolean() ? "iso/block (1).png" : "iso/block (3).png", 50 * scale, 54 * scale);
//
//                Point2D iso = toIso(new Point2D(j, i));
//
//                block.setTranslateX(getWidth() / 2  + iso.getX() * 25 * scale);
//                block.setTranslateY(getHeight() / 4 + iso.getY() * 31 * scale);
//
//                getGameScene().addGameView(new EntityView(block));
//            }
//        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
