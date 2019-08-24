/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.trade;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import dev.DeveloperWASDControl;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ShopSample extends GameApplication {

    private enum Type {
        PLAYER
    }

    private Entity player;

    public Entity getPlayer() {
        return player;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("ShopSample");
        settings.setVersion("0.1");
    }

    private ShopState shopState;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Open Shop Menu") {
            @Override
            protected void onActionBegin() {
                getGameController().pushSubScene(shopState);
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGame() {

        shopState = new ShopState();

        player = entityBuilder()
                .type(Type.PLAYER)
                .at(100, 100)
                .view(new Rectangle(40, 40))
                .with(new DeveloperWASDControl())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
