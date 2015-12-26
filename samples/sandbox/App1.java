/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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
package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.control.CircularMovementControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class App1 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic FXGL App");
        settings.setVersion("0.1devel");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addAction(new UserAction("Azimuth up") {
            @Override
            protected void onAction() {
                light.setY(light.getY() - 5);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Azimuth down") {
            @Override
            protected void onAction() {
                light.setY(light.getY() + 5);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Azimuth left") {
            @Override
            protected void onAction() {
                light.setX(light.getX() - 5);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Azimuth right") {
            @Override
            protected void onAction() {
                light.setX(light.getX() + 5);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Print") {
            @Override
            protected void onActionBegin() {
                log.finer(light.getX() + " " + light.getY());
            }
        }, KeyCode.SPACE);

        input.addAction(new UserAction("Prind") {
            @Override
            protected void onActionBegin() {
                addLight(light);
            }
        }, KeyCode.P);
    }

    private void addLight(Light light) {
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSpecularConstant(1.0);
        lighting.setSurfaceScale(5.0);
        getGameScene().getRoot().getChildren().get(0).setEffect(lighting);
    }

    @Override
    protected void initAssets() {
    }

    private void randomWait() {
        try {
            Thread.sleep((long)(Math.random() * 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Light.Point light;

    @Override
    protected void initGame() {

        light = new Light.Point();
        light.setColor(Color.WHITE);
        light.setX(100);
        light.setY(100);
        light.setZ(50);


        Light.Point light2 = new Light.Point();
        light2.setColor(Color.WHITE);
        light2.setZ(50);
        addLight(light2);

        Entity entity = Entity.noType();
        entity.setPosition(100, 100);
        entity.setSceneView(getAssetLoader().loadTexture("brick.png"));
//        entity.getSceneView().ifPresent(view -> {
//            Lighting lighting = new Lighting();
//            lighting.setLight(light2);
//            lighting.setSpecularConstant(1.0);
//            lighting.setSurfaceScale(5.0);
//
//            view.setEffect(lighting);
//        });

        Entity entity2 = Entity.noType();
        entity2.setPosition(400, 100);
        entity2.setSceneView(new Rectangle(40, 40, Color.BLUE));
        entity2.getSceneView().ifPresent(view -> {
            //view.setEffect(lighting);
        });
        entity2.addControl(new CircularMovementControl(1, 50));

        Entity entity3 = Entity.noType();
        entity3.setPosition(300, 200);
        entity3.setSceneView(getAssetLoader().loadTexture("brick.png"));
        entity3.getSceneView().ifPresent(view -> {
            //view.setEffect(lighting);
        });

        getGameWorld().addEntities(entity2);

        Group group = new Group();

        EntityView view = new EntityView(entity);
        view.addNode(getAssetLoader().loadTexture("brick.png"));

        EntityView view2 = new EntityView(entity3);
        view2.addNode(getAssetLoader().loadTexture("brick.png"));

        group.getChildren().addAll(view, view2);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSpecularConstant(1.0);
        lighting.setSurfaceScale(5.0);
        group.setEffect(lighting);
    }

    @Override
    protected void initPhysics() {
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void onUpdate() {}

    public static void main(String[] args) {
        launch(args);
    }
}
