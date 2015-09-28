/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.ui;

import java.util.Random;
import java.util.logging.Logger;

import com.almasb.fxgl.settings.SceneSettings;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.Version;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * This is the default FXGL Intro animation
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLIntroScene extends IntroScene {

    private static final Logger log = FXGLLogger.getLogger("FXGLIntroScene");

    private double w, h;

    private ParallelTransition animation;

    private Random random = new Random();

    public FXGLIntroScene(SceneSettings settings) {
        super(settings);

        w = settings.getTargetWidth();
        h = settings.getTargetHeight();

        Text f = makeLetter("F");
        Text x = makeLetter("X");
        Text g = makeLetter("G");
        Text l = makeLetter("L");

        x.setTranslateY(h + 70);

        g.setTranslateX(w);

        l.setTranslateX(w + 70);
        l.setTranslateY(h);
        l.setRotate(180);

        Group fxglText = new Group(f, x, g, l);

        Text poweredText = makePoweredBy();
        Text version = makeVersion();

        getRoot().getChildren().addAll(new Rectangle(w, h), fxglText, poweredText, version);

        double originX = w / 2 - f.getLayoutBounds().getWidth() * 4 / 2;
        double dx = f.getLayoutBounds().getWidth();

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), f);
        tt.setToX(originX);
        tt.setToY(h / 2);

        TranslateTransition tt2 = new TranslateTransition(Duration.seconds(1), x);
        tt2.setToX(originX + dx);
        tt2.setToY(h / 2);

        TranslateTransition tt3 = new TranslateTransition(Duration.seconds(1), g);
        tt3.setToX(originX + dx*2);
        tt3.setToY(h / 2);

        TranslateTransition tt4 = new TranslateTransition(Duration.seconds(1), l);
        tt4.setToX(originX + dx*3.3);
        tt4.setToY(h / 2);

        animation = new ParallelTransition(tt, tt2, tt3, tt4);
        animation.setOnFinished(event -> {
            poweredText.setVisible(true);
            version.setVisible(true);

            RotateTransition rt = new RotateTransition(Duration.seconds(1), l);
            rt.setDelay(Duration.seconds(0.66));
            rt.setAxis(new Point3D(0, 0, 1));
            rt.setByAngle(-180);
            rt.setOnFinished(e -> {

                Light.Point light = new Light.Point();
                light.setX(-300);
                light.setY(0);
                light.setZ(100);

                Lighting lighting = new Lighting();
                lighting.setLight(light);
                lighting.setSurfaceScale(2.0);

                fxglText.setEffect(lighting);

                Timeline timeline = new Timeline();
                KeyFrame frame = new KeyFrame(Duration.seconds(2.5),
                        new KeyValue(light.xProperty(), 300),
                        new KeyValue(light.zProperty(), -10));
                timeline.getKeyFrames().add(frame);
                timeline.play();

                double t = 0;
                for (int i = 0; i < 50; i++) {
                    Circle c = new Circle(1);
                    c.setFill(Color.GOLD);
                    c.setTranslateX(-5);
                    c.setTranslateY(h / 2 + 10);

                    getRoot().getChildren().add(c);

                    PathTransition pt = new PathTransition(Duration.seconds(1 + t), getPath(f, l), c);
                    if (i == 49)
                        pt.setOnFinished(e2 -> finishIntro());

                    pt.play();

                    t += 0.05;
                }
            });
            rt.play();
        });
    }

    private Text makeLetter(String letter) {
        Text text = new Text(letter);
        text.setFill(Color.WHITESMOKE);
        text.setFont(UIFactory.newFont(72));
        return text;
    }

    private Text makeVersion() {
        Text text = new Text(Version.getAsString() + " by AlmasB");
        text.setVisible(false);
        text.setFont(UIFactory.newFont(18));
        text.setFill(Color.ALICEBLUE);
        text.setTranslateY(h - 5);
        return text;
    }

    private Text makePoweredBy() {
        Text text = new Text("Powered By");
        text.setVisible(false);
        text.setFont(UIFactory.newFont(18));
        text.setFill(Color.WHITE);
        text.setTranslateX(w / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setTranslateY(h / 2 - 80);
        return text;
    }

    private Shape getPath(Node f, Node l) {
        Path path = new Path();

        MoveTo moveTo = new MoveTo();
        moveTo.setX(0);
        moveTo.setY(h / 2 + 10 + random.nextInt(15));

        HLineTo hLineTo = new HLineTo();
        hLineTo.setX(l.getTranslateX() + 30 + random.nextInt(15));

        LineTo lineTo = new LineTo();
        lineTo.setX(l.getTranslateX() + 30 + random.nextInt(15));
        lineTo.setY(l.getTranslateY() - 70 - random.nextInt(15));

        HLineTo hLineTo2 = new HLineTo();
        hLineTo2.setX(f.getTranslateX() - 10 - random.nextInt(15));

        LineTo lineTo2 = new LineTo();
        lineTo2.setX(f.getTranslateX() - 10 - random.nextInt(15));
        lineTo2.setY(f.getTranslateY() + 10 + random.nextInt(15));

        HLineTo hLineTo3 = new HLineTo();
        hLineTo3.setX(w + 50);

        path.getElements().addAll(moveTo, hLineTo, lineTo,
                hLineTo2, lineTo2, hLineTo3);

        return path;
    }

    @Override
    public void startIntro() {
        animation.play();
    }
}
