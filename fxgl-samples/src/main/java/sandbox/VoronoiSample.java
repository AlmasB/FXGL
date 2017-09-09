/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
//import sandbox.voronoi.Voronoi;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class VoronoiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("VoronoiSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    @Override
    protected void initGame() {
//        Voronoi diagram = new Voronoi(50);
//        diagram.generateVoronoi(
//                new double[] { 100, 200, 250, 450 },
//                new double[] { 100, 5, 33, 400 },
//                0, 800, 0, 600
//        ).forEach(edge -> {
//            getGameScene().addUINode(new Line(edge.x1, edge.y1, edge.x2, edge.y2));
//
//            System.out.println(edge.site1 + " " + edge.site2);
////            getGameScene().getGraphicsContext().setStroke(Color.BLACK);
////            getGameScene().getGraphicsContext().strokeLine();
//        });
//
//        getGameScene().addUINode(new Circle(100, 100, 5));
//        getGameScene().addUINode(new Circle(200, 5, 5));
//        getGameScene().addUINode(new Circle(250, 33, 5));
//        getGameScene().addUINode(new Circle(450, 400, 5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
