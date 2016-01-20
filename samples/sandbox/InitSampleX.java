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

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.MainViewComponent;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class InitSampleX extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER
    }

    // make the field instance level
    // but do NOT init here for properly functioning save-load system
    private GameEntity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InitSampleX");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("O", KeyCode.K));
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        //EntityView.turnOnDebugBBox(Color.RED);
        MainViewComponent.turnOnDebugBBox(Color.RED);

        player = new GameEntity();
        //player.getPositionComponent().setValue(100, 100);
        //player.getBoundingBoxComponent().addHitBox(new HitBox("ARM", new BoundingBox(0, 0, 40, 40)));

        //player.addControl(new ExpireCleanControl(Duration.seconds(3)));

        getGameWorld().addEntity(player);

//        // 2. create entity and add necessary components
//        player = new Entity();
//
//        // set entity position to x = 100, y = 100
//        player.addComponent(new PositionComponent(100, 100));
//
//        // 3. create graphics for entity
//        Rectangle graphics = new Rectangle(40, 40);
//
//        // set graphics to entity
//        player.addComponent(new MainViewComponent(graphics));
//
//        // 4. add entity to game world
//        getGameWorld().addEntity(player);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate() {}

    @OnUserAction(name = "O", type = ActionType.ON_ACTION_BEGIN)
    public void o() {
        if (player.hasComponent(MainViewComponent.class)) {
            player.removeComponent(MainViewComponent.class);
        } else {
            player.addComponent(new MainViewComponent(new EntityView(new Rectangle(Math.random()*40 + 20, 40) )));
        }

//        player.getBoundingBoxComponent()
//                .addHitBox(new HitBox("HEAD", new BoundingBox(20, 20, 40, 50)));
//
//        player.getMainViewComponent().setGraphics(new EntityView(player, new Rectangle(Math.random()*40 + 20, 40)));
//
//        player.getPositionComponent().setValue(300, 500);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
