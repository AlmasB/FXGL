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

package manual;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;

public class SceneScaleTest extends GameApplication {

    private enum Type {
        TEST
    }

    private Entity player, enemy;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(2560);
        settings.setHeight(1440);
        settings.setTitle("SceneScaleTest");
        settings.setVersion("0.1developer");
        settings.setFullScreen(true);
        settings.setIntroEnabled(true);
        settings.setMenuEnabled(true);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

//        input.addAction(new UserAction("Move Left") {
//            @Override
//            protected void onActionBegin() {
//                player.translate(-40, 0);
//            }
//        }, KeyCode.A);
//
//        input.addAction(new UserAction("Move Right") {
//            @Override
//            protected void onActionBegin() {
//                player.translate(40, 0);
//            }
//        }, KeyCode.D);
//
//        input.addAction(new UserAction("XY Test") {
//            @Override
//            protected void onActionBegin() {
//                getDisplay().showMessageBox(input.getMouse().getMouseXWorld() + "," + input.getMouse().getMouseYWorld());
////                log.info(input.getMouse().x + " " + input.getMouse().y);
////                log.info(input.getMouse().screenX + " " + input.getMouse().screenY);
//            }
//        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initAssets() {
    }

    @Override
    protected void initGame() {
        createEntity(0, 0);
        createEntity(0, getHeight() - 40);
        createEntity(getWidth() - 40, 0);
        createEntity(getWidth() - 40, getHeight() - 40);
        player = createEntity(getWidth() / 2, getHeight() / 2);
    }

    @Override
    protected void initPhysics() {
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {
    }

    private Entity createEntity(double x, double y) {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(x, y));
        //e.addComponent(new MainViewComponent(new Rectangle(40, 40)));

        getGameWorld().addEntity(e);
        return e;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
