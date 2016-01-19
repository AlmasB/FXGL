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
//package sandbox;
//
//import com.almasb.fxgl.app.ApplicationMode;
//import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.asset.AnimationChannel;
//import com.almasb.fxgl.asset.DynamicAnimatedTexture;
//import com.almasb.fxgl.asset.Texture;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.entity.EntityType;
//import com.almasb.fxgl.input.Input;
//import com.almasb.fxgl.input.UserAction;
//import com.almasb.fxgl.settings.GameSettings;
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.geometry.HorizontalDirection;
//import javafx.geometry.Rectangle2D;
//import javafx.geometry.VerticalDirection;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.scene.transform.Scale;
//import javafx.util.Duration;
//
///**
// * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
// */
//public class WalkerApp extends GameApplication {
//
//    private enum Type implements EntityType {
//        PLAYER
//    }
//
//    private enum AnimChannel implements AnimationChannel {
//        IDLE(new Rectangle2D(0, 0, 290 * 10, 500)),
//        RUN(new Rectangle2D(0, 500, 376 * 10, 520)),
//        ATTACK(new Rectangle2D(0, 500 + 520, 524 * 10, 565)),
//        JUMP(new Rectangle2D(0, 500 + 520 + 565, 399 * 10, 543)),
//        THROW(new Rectangle2D(0, 500 + 520 + 565 + 543, 383 * 10, 514));
//
//        private Rectangle2D area;
//
//        AnimChannel(Rectangle2D area) {
//            this.area = area;
//        }
//
//        @Override
//        public Rectangle2D area() {
//            return area;
//        }
//
//        @Override
//        public int frames() {
//            return 10;
//        }
//
//        @Override
//        public Duration duration() {
//            return Duration.seconds(0.33);
//        }
//    }
//
//    private Entity player;
//
//    @Override
//    protected void initSettings(GameSettings settings) {
//        settings.setWidth(1280);
//        settings.setHeight(720);
//        settings.setTitle("Walker");
//        settings.setVersion("0.1dev");
//        settings.setFullScreen(false);
//        settings.setIntroEnabled(false);
//        settings.setMenuEnabled(false);
//        settings.setShowFPS(true);
//        settings.setApplicationMode(ApplicationMode.DEVELOPER);
//    }
//
//    @Override
//    protected void initInput() {
//        Input input = getInput();
//
//        input.addAction(new UserAction("Move Left") {
//            @Override
//            protected void onAction() {
//                player.translate(-3, 0);
//                playerSprite.setAnimationChannel(AnimChannel.RUN);
//            }
//
//            @Override
//            protected void onActionBegin() {
//                player.setXFlipped(true, 145 * 0.25);
//            }
//
//            @Override
//            protected void onActionEnd() {
//                playerSprite.setAnimationChannel(AnimChannel.IDLE);
//            }
//        }, KeyCode.A);
//
//        input.addAction(new UserAction("Move Right") {
//            @Override
//            protected void onAction() {
//                playerSprite.setAnimationChannel(AnimChannel.RUN);
//                player.translate(3, 0);
//            }
//
//            @Override
//            protected void onActionBegin() {
//                player.setXFlipped(false);
//            }
//
//            @Override
//            protected void onActionEnd() {
//                playerSprite.setAnimationChannel(AnimChannel.IDLE);
//            }
//        }, KeyCode.D);
//
//        input.addAction(new UserAction("Move Up") {
//            @Override
//            protected void onAction() {
//                player.translate(0, -5);
//            }
//        }, KeyCode.UP);
//
//        input.addAction(new UserAction("Jump") {
//            @Override
//            protected void onActionBegin() {
//                playerSprite.setAnimationChannel(AnimChannel.JUMP);
//            }
//
//            @Override
//            protected void onAction() {
//                player.translate(0, -1);
//            }
//
//            @Override
//            protected void onActionEnd() {
//                //playerSprite.setAnimationChannel(AnimChannel.IDLE);
//            }
//        }, KeyCode.W);
//
//        input.addAction(new UserAction("Move Down") {
//            @Override
//            protected void onAction() {
//                player.translate(0, 5);
//            }
//        }, KeyCode.S);
//
//        input.addAction(new UserAction("Attack") {
//            @Override
//            protected void onAction() {
//                playerSprite.setAnimationChannel(AnimChannel.ATTACK);
//            }
//
//            @Override
//            protected void onActionBegin() {
//
//            }
//
//            @Override
//            protected void onActionEnd() {
//                //playerSprite.setAnimationChannel(AnimChannel.IDLE);
//            }
//        }, MouseButton.PRIMARY);
//
//        input.addAction(new UserAction("Projectile") {
//            @Override
//            protected void onAction() {
//                playerSprite.setAnimationChannel(AnimChannel.THROW);
//            }
//
//            @Override
//            protected void onActionBegin() {
//
//            }
//
//            @Override
//            protected void onActionEnd() {
//                //playerSprite.setAnimationChannel(AnimChannel.IDLE);
//            }
//        }, MouseButton.SECONDARY);
//    }
//
//    @Override
//    protected void initAssets() {
//        getAssetLoader().cache();
//    }
//
//    @Override
//    protected void initGame() {
//        initPlayer();
//
//    }
//
//    @Override
//    protected void initPhysics() {
//
//    }
//
//    private IntegerProperty score = new SimpleIntegerProperty(0);
//
//    @Override
//    protected void initUI() {
//        Text scoreText = new Text();
//        scoreText.setFont(Font.font(18));
//        scoreText.setTranslateX(1100);
//        scoreText.setTranslateY(50);
//        scoreText.textProperty().bind(score.asString("Score: %d"));
//
//        getGameScene().addUINode(scoreText);
//    }
//
//    @Override
//    protected void onWorldUpdate() {
//
//    }
//
//    private DynamicAnimatedTexture playerSprite;
//
//    private void initPlayer() {
//        player = new Entity(Type.PLAYER);
//        player.setValue(0, getHeight() / 3);
//        player.setCollidable(true);
//
//        playerSprite = buildSpriteSheet();
//        playerSprite.getTransforms().setAll(new Scale(0.25, 0.25));
//
//        player.setSceneView(playerSprite);
//
//        getGameWorld().addEntity(player);
//    }
//
//    private Texture spriteAttack, spriteRun, spriteIdle,
//            spriteJump, spriteThrow, spriteFull;
//
//    private DynamicAnimatedTexture buildSpriteSheet() {
//        spriteAttack = getAssetLoader().loadTexture("Attack__000.png");
//        for (int i = 1; i <= 9; i++) {
//            spriteAttack = spriteAttack.superTexture(getAssetLoader().loadTexture("Attack__00" + i + ".png"), HorizontalDirection.RIGHT);
//        }
//
//        spriteRun = getAssetLoader().loadTexture("Run__000.png");
//        for (int i = 1; i <= 9; i++) {
//            spriteRun = spriteRun.superTexture(getAssetLoader().loadTexture("Run__00" + i + ".png"), HorizontalDirection.RIGHT);
//        }
//
//        spriteIdle = getAssetLoader().loadTexture("Idle__000.png");
//        for (int i = 1; i <= 9; i++) {
//            spriteIdle = spriteIdle.superTexture(getAssetLoader().loadTexture("Idle__00" + i + ".png"), HorizontalDirection.RIGHT);
//        }
//
//        spriteJump = getAssetLoader().loadTexture("Jump__000.png");
//        for (int i = 1; i <= 9; i++) {
//            spriteJump = spriteJump.superTexture(getAssetLoader().loadTexture("Jump__00" + i + ".png"), HorizontalDirection.RIGHT);
//        }
//
//        spriteThrow = getAssetLoader().loadTexture("Throw__000.png");
//        for (int i = 1; i <= 9; i++) {
//            spriteThrow = spriteThrow.superTexture(getAssetLoader().loadTexture("Throw__00" + i + ".png"), HorizontalDirection.RIGHT);
//        }
//
//        spriteFull = spriteIdle
//                .superTexture(spriteRun, VerticalDirection.DOWN)
//                .superTexture(spriteAttack, VerticalDirection.DOWN)
//                .superTexture(spriteJump, VerticalDirection.DOWN)
//                .superTexture(spriteThrow, VerticalDirection.DOWN);
//
//        return spriteFull.toDynamicAnimatedTexture(AnimChannel.IDLE, AnimChannel.values());
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
