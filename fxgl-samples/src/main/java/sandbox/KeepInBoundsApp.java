/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.WaypointMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import dev.DeveloperWASDControl;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class KeepInBoundsApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth((int) (1600 * 0.95));
        settings.setHeight((int) (900 * 0.95));
    }

    @Override
    protected void initInput() {
//        getInput().addTriggerListener(new TriggerListener() {
//            @Override
//            protected void onKeyBegin(KeyTrigger keyTrigger) {
//                System.out.println("BEGIN: " + keyTrigger.getKey());
//            }
//
//            @Override
//            protected void onKey(KeyTrigger keyTrigger) {
//                System.out.println("ACTION: " + keyTrigger.getKey());
//            }
//
//            @Override
//            protected void onKeyEnd(KeyTrigger keyTrigger) {
//                System.out.println("END: " + keyTrigger.getKey());
//            }
//
//            @Override
//            protected void onButtonBegin(MouseTrigger mouseTrigger) {
//                System.out.println("BEGIN BTN " + mouseTrigger.getButton());
//            }
//        });

        getInput().addAction(new UserAction("Run") {
            @Override
            protected void onDoubleActionBegin() {
                // called when 'I' is pressed twice within a specified interval
            }
        }, KeyCode.I);
    }

    private int numProjectiles = 1;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKGRAY);


        //addText("17.1", 100, 50);
        //addText("17.2", 300, 50);

//
//
//
        var size = 45;

        var waypointComp = new WaypointMoveComponent(250, List.of(
                new Point2D(100, 150),
                new Point2D(300, 400),
                new Point2D(500, 250)
        ));

        waypointComp.atDestinationProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Was: " + oldValue);
            System.out.println("Is:  " + newValue);
        });

        onKeyDown(KeyCode.O, () -> {
            waypointComp.move(List.of(
                    new Point2D(500, 250),
                    new Point2D(150, 350),
                    new Point2D(200, 400),
                    new Point2D(200, 150)
            ));
        });

        var e = entityBuilder()
                .at(200, 200)
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                .with(new DeveloperWASDControl())
                .with(waypointComp)
                //.with(new KeepOnScreenComponent())
                //.with(new KeepInBoundsComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight())))
                .buildAndAttach();



//
        entityBuilder()
                .at(0, 0)
                .viewWithBBox(new Rectangle(size, size, Color.BLACK))
                .buildAndAttach();
//
        entityBuilder()
                .at(getAppWidth() - size, 0)
                .viewWithBBox(new Rectangle(size, size, Color.BLACK))
                .buildAndAttach();
//
//        var color = FXGLMath.randomColorHSB(0.8, 0.9).deriveColor(0, 1, random(0.65, 0.95), random(0.65, 0.95)).brighter().brighter();
//
//        run(() -> {
//
//            for (int i = 0; i < numProjectiles; i++) {
//                var view = new Circle(5, 5, 5, color);
//                view.setStroke(Color.RED);
//                view.setBlendMode(BlendMode.ADD);
//
//                var proj = entityBuilder()
//                        .at(20, 500)
//                        .view(view)
//                        .buildAndAttach();
//
//                animationBuilder()
//                        .duration(Duration.seconds(random(0.9, 2.95)))
//                        .onFinished(() -> proj.removeFromWorld())
//                        .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
//                        .translate(proj)
//                        .alongPath(new CubicCurve(
//                                20, 500,
//                                random(0, getAppWidth()), random(0, getAppHeight()),
//                                random(0, getAppWidth()), random(0, getAppHeight()),
//                                1400, 500
//                        ))
//                        .buildAndPlay();
//            }
//
//            numProjectiles += 4;
//
//        }, Duration.seconds(1));
//
        entityBuilder()
                .at(0, getAppHeight() - size)
                .viewWithBBox(new Rectangle(size, size, Color.BLACK))
                .buildAndAttach();

        entityBuilder()
                .at(getAppWidth() - size, getAppHeight() - size)
                .viewWithBBox(new Rectangle(size, size, Color.BLACK))
                .buildAndAttach();

        entityBuilder()
                .at(getAppWidth() + size * 3, getAppHeight() - size)
                .viewWithBBox(new Rectangle(size, size, Color.BLACK))
                .buildAndAttach();

        entityBuilder()
                .at(getAppWidth() / 2.0, getAppHeight() + size * 4)
                .viewWithBBox(new Rectangle(size, size, Color.BLACK))
                .buildAndAttach();

        getGameScene().getViewport().bindToFit(getGameWorld().getEntities().toArray(new Entity[0]));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
