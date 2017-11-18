/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced.state;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.devtools.DeveloperWASDControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class StateSample extends GameApplication {

    private enum Type {
        PLAYER
    }

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("StateSample");
        settings.setVersion("0.1");






    }

    private ShopState shopState;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Open Shop Menu") {
            @Override
            protected void onActionBegin() {
                getStateMachine().pushState(new LockpickingState());
                //getStateMachine().pushState(shopState);
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGame() {

        shopState = new ShopState();

        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new DeveloperWASDControl())
                .buildAndAttach(getGameWorld());
    }

//    @Override
//    protected void onUpdate(double tpf) {
//        System.out.println("onUpdate: " + tpf);
//    }
//
//    @Override
//    protected void onPausedUpdate(double tpf) {
//        System.out.println("onPausedUpdate: " + tpf);
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
