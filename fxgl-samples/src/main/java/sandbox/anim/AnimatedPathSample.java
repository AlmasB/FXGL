/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.anim;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.PathTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class AnimatedPathSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    private List<Point2D> points = new ArrayList<>();
    private Rectangle node;

    @Override
    protected void initGame() {
        node = new Rectangle(40, 40);

        var phantom = new Rectangle(1, 1);

        Shape shape = new Circle(80);
        shape.setTranslateX(200);
        shape.setTranslateY(200);


        shape = Shape.subtract(new Rectangle(200, 200), shape);

        //shape = Shape.union(shape, new Circle(35));

        addUINode(node, 100, 100);

//        PathTransition tt = new PathTransition(Duration.seconds(3), shape, phantom);
//        tt.play();
//
//        double t = 0.0;
//
//        while (t < 3) {
//            points.add(new Point2D(phantom.getTranslateX(), phantom.getTranslateY()));
//
//            t += 0.016;
//
//            tt.jumpTo(Duration.seconds(t));
//        }

        var time = System.nanoTime();


        animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .duration(Duration.seconds(5.0))
                .onFinished(() -> {
                    var end = System.nanoTime() - time;
                    System.out.println(end / 1000000000.0);
                })
                .translate(node)
                //.from(new Point2D(0, 0))
                //.to(new Point2D(100, 100))
                .alongPath(shape)
                .buildAndPlay();
    }

    private int index = 0;

    @Override
    protected void onUpdate(double tpf) {
//        if (index == points.size())
//            return;
//
//        var p = points.get(index++);
//        node.setTranslateX(p.getX());
//        node.setTranslateY(p.getY());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
