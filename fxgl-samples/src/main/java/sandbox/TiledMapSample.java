/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ui.FXGLCheckBox;

import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TiledMapSample extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER, NPC
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("TiledMapSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("someInt", 0);
        vars.put("someDouble", 0.0);
        vars.put("someVec2", new Vec2(15.0, 33.0));
        vars.put("someString", "Hello FXGL World");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new MyEntityFactory());

        var level = setLevelFromMap("tmx/map_with_gid_objects.tmx");

        var map = new HashMap<String, Integer>();
        map.put("h", 1);
        map.put("kjj", 2);

        System.out.println(map);

        System.out.println(level.getProperties());

//        Level level = getAssetLoader().loadLevel("tmx/map_with_gid_objects.tmx", new TMXLevelLoader());
//
//        getGameWorld().setLevel(level);

        addUINode(new FXGLCheckBox(), 200, 400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
