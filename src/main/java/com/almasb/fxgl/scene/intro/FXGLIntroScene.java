/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.scene.intro;

import com.almasb.fxgl.scene.IntroScene;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.Version;
import javafx.animation.*;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

/**
 * This is the default FXGL Intro animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLIntroScene extends IntroScene {

    //private static final Logger log = FXGLLogger.getLogger("FXGL.IntroScene");

    private double w, h;

    private ParallelTransition animation;

    public FXGLIntroScene(ReadOnlyGameSettings settings) {
        super(settings);
        w = settings.getWidth();
        h = settings.getHeight();

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

        FireworksPane fireworks = new FireworksPane(w, h);

        Group content = new Group(fxglText, poweredText, version, fireworks);

        getRoot().getChildren().addAll(new Rectangle(w, h), content);

        double originX = w / 2 - f.getLayoutBounds().getWidth() * 4 / 2;
        double dx = f.getLayoutBounds().getWidth();

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), f);
        tt.setToX(originX);
        tt.setToY(h / 2);

        TranslateTransition tt2 = new TranslateTransition(Duration.seconds(1), x);
        tt2.setToX(originX + dx);
        tt2.setToY(h / 2);

        TranslateTransition tt3 = new TranslateTransition(Duration.seconds(1), g);
        tt3.setToX(originX + dx * 2);
        tt3.setToY(h / 2);

        TranslateTransition tt4 = new TranslateTransition(Duration.seconds(1), l);
        tt4.setToX(originX + dx * 3.3);
        tt4.setToY(h / 2);

        fireworks.play();

        animation = new ParallelTransition(tt, tt2, tt3, tt4);
        animation.setOnFinished(event -> {
            poweredText.setVisible(true);
            version.setVisible(true);

            RotateTransition rt = new RotateTransition(Duration.seconds(1), l);
            rt.setDelay(Duration.seconds(0.66));
            rt.setAxis(new Point3D(0, 0, 1));
            rt.setByAngle(-180);
            rt.setOnFinished(e -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(2.5), getRoot());
                ft.setToValue(0);
                ft.setOnFinished(e1 -> {
                    fireworks.stop();
                    finishIntro();
                });
                ft.play();
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

    @Override
    public void startIntro() {
        animation.play();
    }
}
