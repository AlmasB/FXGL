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
//import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.entity.EntityType;
//import com.almasb.fxgl.input.*;
//import com.almasb.fxgl.physics.HitBox;
//import com.almasb.fxgl.physics.PhysicsEntity;
//import com.almasb.fxgl.settings.GameSettings;
//import javafx.animation.Animation;
//import javafx.animation.TranslateTransition;
//import javafx.geometry.BoundingBox;
//import javafx.geometry.Point3D;
//import javafx.scene.*;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.scene.paint.Color;
//import javafx.scene.paint.PhongMaterial;
//import javafx.scene.shape.Box;
//import javafx.scene.transform.Rotate;
//import javafx.scene.transform.Translate;
//import javafx.util.Duration;
//import org.jbox2d.dynamics.BodyType;
//import org.jbox2d.dynamics.FixtureDef;
//
//import java.util.Random;
//
//import static java.lang.Math.*;
//
///**
// * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
// */
//public class Test3d extends GameApplication {
//
//    private enum Type implements EntityType {
//        PLAYER, ENEMY, BOX, CRATE
//    }
//
//    @Override
//    protected void initSettings(GameSettings settings) {
//        settings.setMenuEnabled(true);
//        settings.setIntroEnabled(false);
////        settings.setWidth(1920);.
////        settings.setHeight(1080);
////        settings.setFullScreen(true);
//    }
//
//    private PhysicsEntity e1, e2;
//
//    @Override
//    protected void initInput() {
//        Input input = getInput();
//        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
//        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
//        input.addInputMapping(new InputMapping("Move Up", KeyCode.W));
//        input.addInputMapping(new InputMapping("Move Down", KeyCode.S));
//        input.addInputMapping(new InputMapping("Rotate Left", KeyCode.LEFT));
//        input.addInputMapping(new InputMapping("Rotate Right", KeyCode.RIGHT));
//        input.addInputMapping(new InputMapping("Rotate Up", KeyCode.UP));
//        input.addInputMapping(new InputMapping("Rotate Down", KeyCode.DOWN));
//        input.addInputMapping(new InputMapping("Debug", KeyCode.K));
//
//        input.addAction(new UserAction("Spawn") {
//            @Override
//            protected void onActionBegin() {
//
//
//            }
//        }, MouseButton.PRIMARY, InputModifier.ALT);
//
//        mouse = input.getMouse();
//    }
//
//    @Override
//    protected void initAssets() {
//
//    }
//
//    @Override
//    protected void initGame() {
//        getAudioPlayer().setGlobalSoundVolume(0);
//
//        e1 = spawn(300, 100);
//        e2 = spawn(330, 0);
//
//
//        PhysicsEntity ground = new PhysicsEntity(Type.CRATE);
//        ground.setValue(0, 500);
//        ground.addHitBox(new HitBox("BODY", new BoundingBox(0, 0, 800, 100)));
//        //ground.setSceneView(new Rectangle(800, 100));
//
//        getGameWorld().addEntity(ground);
//    }
//
//    private PhysicsEntity spawn(double xx, double yy) {
//        PhysicsEntity entity = new PhysicsEntity(Type.CRATE);
//
//        entity.addHitBox(new HitBox("BODY", new BoundingBox(0, 0, 40, 40)));
//
//        entity.setBodyType(BodyType.DYNAMIC);
//        entity.setValue(xx, yy);
//
//        FixtureDef fd = new FixtureDef();
//        fd.setDensity(0.05f);
//        fd.setRestitution(0.3f);
//
//        entity.setFixtureDef(fd);
//
//        getGameWorld().addEntity(entity);
//        return entity;
//    }
//
//    @Override
//    protected void initPhysics() {
//
//    }
//
//    @Override
//    protected void initUI() {
//        getGameScene().addUINode(createContent());
//    }
//
//    private Mouse mouse;
//
//    @Override
//    protected void onWorldUpdate() {
//        if (mouse.isRightPressed()) {
//            double dx = mouse.getMouseXUI() - x;
//            if (dx > 2) {
//                rotateRight();
//            } else if (dx < -2) {
//                rotateLeft();
//            }
//
//            double dy = mouse.getMouseYUI() - y;
//            if (dy > 2) {
//                rotateDown();
//            } else if (dy < -2) {
//                rotateUp();
//            }
//        }
//
//        x = mouse.getMouseXUI();
//        y = mouse.getMouseYUI();
//    }
//
//    PerspectiveCamera camera;
//
//    Translate translate;
//    Rotate rotate;
//    Rotate rotateVertical;
//
//    Group worldRoot = new Group();
//
//    private double x, y;
//
//    private Parent createContent() {
//        Cube c = new Cube(1, Color.GREEN);
//        c.setTranslateX(-1);
//        c.setRotationAxis(Rotate.Y_AXIS);
//        c.setRotate(45);
//
//        Cube c2 = new Cube(1, Color.BLUE);
//        c2.setTranslateX(1);
//        c2.setRotationAxis(Rotate.Y_AXIS);
//        c2.setRotate(45);
//
//        Cube c3 = new Cube(1, Color.RED);
//        c3.setTranslateZ(5);
//        c3.setRotationAxis(Rotate.Y_AXIS);
//        //c3.setRotate(45);
//
//        Cube flyingCube = new Cube(2, Color.SILVER);
//        flyingCube.setTranslateX(-10);
//        flyingCube.setTranslateY(-3);
//        flyingCube.setTranslateZ(5);
//
//        TranslateTransition fly = new TranslateTransition(Duration.seconds(5), flyingCube);
//        fly.setToX(10);
//        fly.setToY(-7);
//        fly.setToZ(-2);
//        fly.setAutoReverse(true);
//        fly.setCycleCount(Animation.INDEFINITE);
//        fly.play();
//
//
//        camera = new PerspectiveCamera(true);
//        translate = new Translate(0, 0, -10);
//        rotate = new Rotate(0, new Point3D(0, 1, 0));
//        rotateVertical = new Rotate(0, new Point3D(1, 0, 0));
//        camera.getTransforms().addAll(translate, rotate, rotateVertical);
//        initAngles();
//
//        PointLight light = new PointLight(Color.WHITE);
//        light.setTranslateX(3);
//        light.setTranslateZ(-5);
//
//        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), light);
//        tt.setFromX(-3);
//        tt.setToX(15);
//        tt.setAutoReverse(true);
//        tt.setCycleCount(Animation.INDEFINITE);
//
//        AmbientLight globalLight = new AmbientLight(Color.WHITE.deriveColor(0, 1, 0.2, 1));
//
//
//        placeCube(new Point3D(5, 0, 0));
//        placeCube(new Point3D(-10, 0, 0));
//        placeCube(new Point3D(0, 0, -20));
//
//        Cube ground = new Cube(10, Color.BROWN);
//        ground.setTranslateY(6);
//        ground.setTranslateZ(-5);
//
////        for (int z = -5; z < 0; z++) {
////            for (int x = -2; x < 2; x++) {
////                placeCube(new Point3D(x*2, 6, z*2));
////            }
////        }
//
//        worldRoot.getChildren().addAll(c, c2, c3, ground, flyingCube, globalLight, light);
//
//        SubScene subScene = new SubScene(worldRoot, getWidth(), getHeight(), true, SceneAntialiasing.BALANCED);
//        subScene.setCamera(camera);
//        subScene.setFill(Color.ALICEBLUE);
//
//        tt.play();
//
//        return new Group(subScene);
//    }
//
//    private void placeCube(Point3D point) {
//        Random random = new Random();
//        Cube cube = new Cube(1, Color.rgb(random.nextInt(150) + 100, random.nextInt(150) + 100, random.nextInt(250)));
//        cube.setTranslateX(point.getX());
//        cube.setTranslateY(point.getY());
//        cube.setTranslateZ(point.getZ());
//        worldRoot.getChildren().add(cube);
//    }
//
//    private static class Cube extends Box {
//        public Cube(double size, Color color) {
//            super(size, size, size);
//            setMaterial(new PhongMaterial(color));
//        }
//    }
//
//    double speed = 0.17;
//    Point3D direction = new Point3D(0, 0, 10).normalize();
//    Point3D up = new Point3D(0, 1, 0);
//
//    double horAngle, verAngle;
//
//    private void initAngles() {
//        Point3D horDirection = new Point3D(direction.getX(), 0, direction.getZ()).normalize();
//
//        if (horDirection.getZ() > 0) {
//            horAngle = horDirection.getX() >= 0
//                    ? 360 - toDegrees(asin(horDirection.getZ()))
//                    : 180 + toDegrees(asin(horDirection.getZ()));
//        } else {
//            horAngle = horDirection.getX() >= 0
//                    ? toDegrees(asin(-horDirection.getZ()))
//                    : 90 + toDegrees(asin(-horDirection.getZ()));
//        }
//
//        verAngle = -toDegrees(asin(direction.getY()));
//
//        adjustDirection();
//    }
//
//    private void adjustDirection() {
//        Point3D vAxis = new Point3D(0, 1, 0);
//
//        Point3D view = new Point3D(1, 0, 0);
//        view = rotateVectorAround(view, horAngle, vAxis).normalize();
//
//        Point3D hAxis = vAxis.crossProduct(view).normalize();
//        view = rotateVectorAround(view, verAngle, hAxis).normalize();
//
//        direction = view;
//        up = direction.crossProduct(hAxis).normalize();
//    }
//
//    private Point3D rotateVectorAround(Point3D vector, double angle, Point3D axis) {
//        double sinHalfAngle = sin(toRadians(angle / 2));
//        double cosHalfAngle = cos(toRadians(angle / 2));
//
//        double rx = axis.getX() * sinHalfAngle;
//        double ry = axis.getY() * sinHalfAngle;
//        double rz = axis.getZ() * sinHalfAngle;
//        double rw = cosHalfAngle;
//
//        Quaternion rotation = new Quaternion(rx, ry, rz, rw);
//        Quaternion conjugate = rotation.conjugate();
//
//        Quaternion w = rotation.multiply(vector).multiply(conjugate);
//
//        return new Point3D(w.getX(), w.getY(), w.getZ());
//    }
//
//    @OnUserAction(name = "Move Left", type = ActionType.ON_ACTION)
//    public void moveLeft() {
//        Point3D left = up.crossProduct(direction)
//                .normalize()
//                .multiply(speed);
//        translate.setX(translate.getX() - left.getX());
//        translate.setZ(translate.getZ() - left.getZ());
//    }
//
//    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
//    public void moveRight() {
//        Point3D right = direction.crossProduct(up)
//                .normalize()
//                .multiply(speed);
//        translate.setX(translate.getX() - right.getX());
//        translate.setZ(translate.getZ() - right.getZ());
//    }
//
//    @OnUserAction(name = "Move Up", type = ActionType.ON_ACTION)
//    public void moveUp() {
//        Point3D tmp = direction.multiply(speed);
//        tmp = new Point3D(tmp.getX(), 0, tmp.getZ());
//        translate.setX(translate.getX() + tmp.getX());
//        translate.setZ(translate.getZ() + tmp.getZ());
//    }
//
//    @OnUserAction(name = "Move Down", type = ActionType.ON_ACTION)
//    public void moveDown() {
//        Point3D tmp = direction.multiply(speed);
//        tmp = new Point3D(tmp.getX(), 0, tmp.getZ());
//        translate.setX(translate.getX() - tmp.getX());
//        translate.setZ(translate.getZ() - tmp.getZ());
//    }
//
//    @OnUserAction(name = "Rotate Right", type = ActionType.ON_ACTION)
//    public void rotateRight() {
//        rotate.setAngle(rotate.getAngle() + 1);
//        horAngle++;
//        adjustDirection();
//    }
//
//    @OnUserAction(name = "Rotate Left", type = ActionType.ON_ACTION)
//    public void rotateLeft() {
//        rotate.setAngle(rotate.getAngle() - 1);
//        horAngle--;
//        adjustDirection();
//    }
//
//    @OnUserAction(name = "Rotate Up", type = ActionType.ON_ACTION)
//    public void rotateUp() {
//        rotateVertical.setAngle(rotateVertical.getAngle() + 1);
//        verAngle++;
//        adjustDirection();
//    }
//
//    @OnUserAction(name = "Rotate Down", type = ActionType.ON_ACTION)
//    public void rotateDown() {
//        rotateVertical.setAngle(rotateVertical.getAngle() - 1);
//        verAngle--;
//        adjustDirection();
//    }
//
//
//    @OnUserAction(name = "Debug", type = ActionType.ON_ACTION_BEGIN)
//    public void debug() {
//        log.info("vAngle: " + verAngle + " hAngle: " + horAngle
//            + " direction: " + direction + " up: " + up);
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
