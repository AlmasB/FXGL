///*
// * The MIT License (MIT)
// *
// * FXGL - JavaFX Game Library
// *
// * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package sandbox;
//
//import com.almasb.fxgl.app.ApplicationMode;
//import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.entity.EntityType;
//import com.almasb.fxgl.input.ActionType;
//import com.almasb.fxgl.input.Input;
//import com.almasb.fxgl.input.InputMapping;
//import com.almasb.fxgl.input.OnUserAction;
//import com.almasb.fxgl.settings.GameSettings;
//import com.almasb.fxgl.ui.UIFactory;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
//
//public class ViewportTest extends GameApplication {
//
//    private enum Type implements EntityType {
//        PLAYER
//    }
//
//    private Entity player;
//
//    @Override
//    protected void initSettings(GameSettings settings) {
//        settings.setWidth(800);
//        settings.setHeight(600);
//        settings.setTitle("Basic FXGL Application");
//        settings.setVersion("0.1developer");
//        settings.setFullScreen(false);
//        settings.setIntroEnabled(false);
//        settings.setMenuEnabled(false);
//        settings.setShowFPS(true);
//        settings.setApplicationMode(ApplicationMode.DEVELOPER);
//    }
//
//    @Override
//    protected void initInput() {
//        // 1. get input service
//        Input input = getInput();
//
//        // 2. add input mappings (action name -> trigger name)
//        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
//        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
//        input.addInputMapping(new InputMapping("Move Up", KeyCode.W));
//        input.addInputMapping(new InputMapping("Move Down", KeyCode.S));
//        input.addInputMapping(new InputMapping("Shoot", MouseButton.PRIMARY));
//    }
//
//    @Override
//    protected void initAssets() {}
//
//    @Override
//    protected void initGame() {
//        player = new Entity(Type.PLAYER);
//        player.setPosition(100, 100);
//
//        Rectangle graphics = new Rectangle(40, 40);
//        player.setSceneView(graphics);
//
//        getGameWorld().addEntity(player);
//    }
//
//    @Override
//    protected void initPhysics() {}
//
//    private Text debug;
//
//    @Override
//    protected void initUI() {
//        debug = UIFactory.newText("", Color.BLUE, 24);
//        debug.setTranslateX(100);
//        debug.setTranslateY(150);
//
//        getGameScene().addUINode(debug);
//    }
//
//    @Override
//    public void onWorldUpdate() {
//        debug.setText("Player is inside viewport: "
//                + player.isWithin(getGameScene().getViewport().getVisibleArea()));
//    }
//
//    // 3. specify which method to call on each action
//
//    @OnUserAction(name = "Move Left", type = ActionType.ON_ACTION)
//    public void moveLeft() {
//        player.translate(-5, 0);
//    }
//
//    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
//    public void moveRight() {
//        player.translate(5, 0);
//    }
//
//    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION_BEGIN)
//    public void moveRightStart() {
//        log.info("start right");
//    }
//
//    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION_END)
//    public void moveRightStop() {
//        log.info("stop right");
//    }
//
//    @OnUserAction(name = "Move Up", type = ActionType.ON_ACTION)
//    public void moveUp() {
//        player.translate(0, -5);
//    }
//
//    @OnUserAction(name = "Move Down", type = ActionType.ON_ACTION)
//    public void moveDown() {
//        player.translate(0, 5);
//    }
//
//    @OnUserAction(name = "Shoot", type = ActionType.ON_ACTION_BEGIN)
//    public void shoot() {
//        log.info("Shooting");
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
