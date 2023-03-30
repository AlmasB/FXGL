/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import dev.DeveloperWASDControl;
import intermediate.levels.text.TextLevelEntityFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;

/**
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class IsoTiledMapSample extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER, NPC
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1900);
        settings.setHeight(1000);
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
        getGameScene().setBackgroundColor(Color.AQUA);
        getGameWorld().addEntityFactory(new TextLevelEntityFactory());

        Level level = getAssetLoader().loadLevel("tmx/iso/iso.tmx", new TMXLevelLoader());

        getGameWorld().setLevel(level);

        var e = entityBuilder()
                .at(150, 150)
                .view(new Rectangle(100, 100, Color.BLUE))
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        getGameScene().getViewport().setBounds(0, 0, 50800 - 30, 50100 - 30);
        getGameScene().getViewport().bindToEntity(e, getAppWidth() / 2, getAppHeight() / 2);

        //getGameScene().getViewport().setZoom(0.2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
