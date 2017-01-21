/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package s06gameplay.events;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.event.EntityEvent;
import com.almasb.fxgl.event.EventTrigger;
import com.almasb.fxgl.event.Handles;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import common.PlayerControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use event triggers and fire custom events.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EventsSample extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    private GameEntity player, enemy;
    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("EventsSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);

        // 3. fire events manually if required
        input.addAction(new UserAction("Fire My Event") {
            @Override
            protected void onActionBegin() {
                getEventBus().fireEvent(new MyGameEvent(MyGameEvent.ANY));
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        playerControl = new PlayerControl();

        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                .bbox(new HitBox("PLAYER_BODY", BoundingShape.box(40, 40)))
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .with(playerControl)
                .build();

        enemy = Entities.builder()
                .type(Type.ENEMY)
                .at(200, 100)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .build();

        getGameWorld().addEntities(player, enemy);

        // 1. add event trigger and specify when and what to fire
        // you can use a custom event or pre-defined event / with custom event type
        getGameWorld().addEventTrigger(new EventTrigger<EntityEvent>(
                () -> player.getRightX() > enemy.getX(),
                () -> new EntityEvent(Events.PASSED, player, enemy)
                // by default the triggers fire once only
                // this can be changed in the constructor, just uncomment the following
                //, 2, Duration.seconds(1)
        ));

        // 2. add event handler using code OR
        getEventBus().addEventHandler(MyGameEvent.ANY, event -> {
            System.out.println("Code handler: " + event);
        });
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    // 3. add event handler using annotation
    // specify where event type object is located
    @Handles(eventClass = Events.class, eventType = "PASSED")
    public void onPassed(EntityEvent event) {
        System.out.println(event.getTriggerEntity() + " passed " + event.getTargetEntity());
    }

    // class detail can be omitted if class matches method type param
    // here it's MyGameEvent.class, so no need to explicitly state it
    @Handles(eventType = "ANY")
    public void onMyEvent(MyGameEvent event) {
        System.out.println("Annotation handler: " + event);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
