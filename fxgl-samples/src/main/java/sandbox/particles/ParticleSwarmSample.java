/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLDefaultMenu;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.FollowComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleSwarmSample extends GameApplication {

    private List<Entity> particles;

    private Entity mainEntity;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1800);
        settings.setHeight(1000);
        settings.setScaleAffectedOnResize(false);
        settings.setPreserveResizeRatio(true);

        //settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new TTMainMenu();
            }
        });
    }

    public static class TTMainMenu extends FXGLDefaultMenu {

        public TTMainMenu() {
            super(MenuType.MAIN_MENU);
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }
    }

    @Override
    protected void initInput() {

    }


    @Override
    protected void initGame() {
        getGameScene().setCursorInvisible();
        getGameScene().setBackgroundColor(Color.BLACK);

        var capacity = getAppWidth() / 3 * getAppHeight() / 3;

        particles = new ArrayList<>(capacity);

        for (int y = 1; y < 3; y++) {
            for (int x = 1; x < 3; x++) {
                var line1 = new Line(x * getAppWidth() / 3.0, 0, x * getAppWidth() / 3.0, getAppHeight());
                var line2 = new Line(0, y * getAppHeight() / 3.0, getAppWidth(), y * getAppHeight() / 3.0);

                line1.setStrokeWidth(3);
                line2.setStrokeWidth(3);

//                addUINode(line1);
//                addUINode(line2);
            }
        }

        var size = 16;

        mainEntity = new Entity();
        getGameWorld().addEntity(mainEntity);

        var nextEntity = mainEntity;

        var color = FXGLMath.randomColorHSB(0.8, 0.9);

        for (int y = 0; y < getAppHeight() / 3 / size; y++) {
            for (int x = 0; x < getAppWidth() / 3 / size; x++) {


                var e = new Entity();


                var c1 = FXGLMath.randomColorHSB(0.8, 0.9).deriveColor(0, 1, random(0.65, 0.95), random(0.65, 0.95)).brighter().brighter();
                var c2 = color.deriveColor(0, 1, random(0.65, 0.95), random(0.65, 0.95));

                var rect = new Circle(size / 2.0, c1);
                rect.setLayoutX(x*size);
                rect.setLayoutY(y*size);

                var texture = texture("particles/circle_05.png", 32, 32);



                e.setPosition(x*size, y*size);


                particles.add(e);

                getGameWorld().addEntity(e);

                var dist = e.distance(nextEntity);

                e.addComponent(new FollowComponent(nextEntity, random(300, 1000), Math.max(dist + 5, 0), random(0.0, 20.0)));



                if (FXGLMath.randomBoolean(0.32)) {
                    nextEntity = e;
                    texture = texture.multiplyColor(c1);
                } else {
                    nextEntity = getGameWorld().getRandom((en) -> en.distance(e) < 55).get();
                    texture = texture.multiplyColor(c2);
                }

//                texture.setScaleX(4 * size );
//                texture.setScaleY(4 * size );
                texture.setBlendMode(BlendMode.ADD);
                e.getViewComponent().addChild(texture);
            }
        }

//        animationBuilder()
//                .repeatInfinitely()
//                .delay(Duration.seconds(random(2.0, 2.5)))
//                .duration(Duration.seconds(random(1.0, 6.5)))
//                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
//                //.interpolator(FXGLMath.random(Interpolators.values()).get().EASE_OUT())
//                .translate(mainEntity)
//                .from(new Point2D(0, 0))
//                .to(new Point2D(getAppWidth(), getAppHeight()))
//                .buildAndPlay();
    }

    @Override
    protected void onUpdate(double tpf) {
        mainEntity.setPosition(getInput().getMousePositionWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
