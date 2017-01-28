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

package sandbox.scifi;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.devtools.DeveloperWASDControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.parser.tiled.TiledMap;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScifiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(640);
        settings.setHeight(640);
        settings.setTitle("ScifiSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {
        TiledMap map = getAssetLoader().loadJSON("tiled_map.json", TiledMap.class);

        Entities.builder()
                .viewFromTiles(map, "Background", RenderLayer.BACKGROUND)
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .viewFromTiles(map, "Foreground", RenderLayer.BACKGROUND)
                //.with(new PhysicsComponent())
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .viewFromTiles(map, "Top")
                .buildAndAttach(getGameWorld());

        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        GameEntity player = Entities.builder()
                .at(200, 150)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.BLUE))
                //.with(physics)
                //.with(new DeveloperWASDControl())
                .buildAndAttach(getGameWorld());

        player.getViewComponent().setRenderLayer(new RenderLayer() {
            @Override
            public String name() {
                return "PLAYER";
            }

            @Override
            public int index() {
                return RenderLayer.BACKGROUND.index() + 100;
            }
        });

        getGameWorld().setLevelFromMap(map);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 20);
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
