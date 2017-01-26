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
import com.almasb.fxgl.parser.tiled.Layer;
import com.almasb.fxgl.parser.tiled.TiledMap;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

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

    private TiledMap map;
    private Image image;

    @Override
    protected void initGame() {
        map = getAssetLoader().loadJSON("tiled_map.json", TiledMap.class);

        String imageName = map.getTilesets().get(0).getImage();

        imageName = imageName.substring(imageName.lastIndexOf("/") + 1);

        System.out.println(imageName);

        image = getAssetLoader().loadTexture(imageName).getImage();
    }

    @Override
    protected void onUpdate(double tpf) {
        GraphicsContext g = getGameScene().getGraphicsContext();

        Layer bg = map.getLayers().get(0);

        for (int i = 0; i < bg.getData().size(); i++) {
            // -1 because firstgid is 1 ?
            int index = bg.getData().get(i) - 1;

            int tilex = index % map.getTilesets().get(0).getColumns();
            int tiley = index / map.getTilesets().get(0).getColumns();

            int x = i % bg.getWidth();
            int y = i / bg.getHeight();

            g.drawImage(image, tilex * 32, tiley * 32, 32, 32, x * 32, y * 32, 32, 32);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
