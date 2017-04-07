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

import com.almasb.fxgl.annotation.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ViewportTest extends GameApplication {

    private PositionComponent playerPosition;
    private BoundingBoxComponent bbox;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ViewportTest");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
        input.addInputMapping(new InputMapping("Move Up", KeyCode.W));
        input.addInputMapping(new InputMapping("Move Down", KeyCode.S));
        input.addInputMapping(new InputMapping("Shoot", MouseButton.PRIMARY));
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        GameEntity entity = Entities.builder()
                .at(100, 100)
                .viewFromNodeWithBBox(new Rectangle(40, 40))
                .buildAndAttach(getGameWorld());

        playerPosition = entity.getPositionComponent();
        bbox = entity.getBoundingBoxComponent();
    }

    @Override
    protected void initPhysics() {}

    private Text debug;

    @Override
    protected void initUI() {
        debug = FXGL.getUIFactory().newText("", Color.BLUE, 24);
        debug.setTranslateX(200);
        debug.setTranslateY(350);

        getGameScene().addUINode(debug);
    }

    @Override
    public void onUpdate(double tpf) {
        debug.setText("Player is inside viewport: "
                + bbox.isWithin(getGameScene().getViewport().getVisibleArea()));
    }

    @OnUserAction(name = "Move Left", type = ActionType.ON_ACTION)
    public void moveLeft() {
        playerPosition.translate(-5, 0);
    }

    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
    public void moveRight() {
        playerPosition.translate(5, 0);
    }

    @OnUserAction(name = "Move Up", type = ActionType.ON_ACTION)
    public void moveUp() {
        playerPosition.translate(0, -5);
    }

    @OnUserAction(name = "Move Down", type = ActionType.ON_ACTION)
    public void moveDown() {
        playerPosition.translate(0, 5);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
