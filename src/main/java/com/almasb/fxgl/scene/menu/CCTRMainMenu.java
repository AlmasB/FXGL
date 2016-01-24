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
package com.almasb.fxgl.scene.menu;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.ui.UIFactory;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class CCTRMainMenu extends FXGLMenu {
    BlendMode mode = BlendMode.ADD;
    int i = 0;

    public CCTRMainMenu(GameApplication app) {
        super(app);

        int start = 100;

        Ellipse circle = new Ellipse(255, 155);
        Text title = UIFactory.newText(app.getSettings().getTitle(), 40);

        Button btnContinue = createActionButton("CONTINUE", () -> {
            title.setBlendMode(BlendMode.values()[++i]);
            System.out.println(BlendMode.values()[i]);
        });
        btnContinue.setTranslateX(300);
        btnContinue.setTranslateY(start);

        Button btnNew = createActionButton("NEW GAME", this::fireExit);
        btnNew.setTranslateX(330);
        btnNew.setTranslateY(start + 75);

        Button btnLoad = createActionButton("LOAD GAME", this::fireExit);
        btnLoad.setTranslateX(360);
        btnLoad.setTranslateY(start + 150);

        Button btnOptions = createActionButton("OPTIONS", this::fireExit);
        btnOptions.setTranslateX(370);
        btnOptions.setTranslateY(start + 225);

        Button btnExtra = createActionButton("EXTRA", this::fireExit);
        btnExtra.setTranslateX(375);
        btnExtra.setTranslateY(start + 300);

        Button btnCheats = createActionButton("CHEATS", this::fireExit);
        btnCheats.setTranslateX(360);
        btnCheats.setTranslateY(start + 375);

        Button btnExit = createActionButton("EXIT", this::fireExit);
        btnExit.setTranslateX(340);
        btnExit.setTranslateY(start + 375 + 75);


        title.setTranslateX(700);
        title.setTranslateY(275);

//        Light.Distant light = new Light.Distant();
//        light.setAzimuth(-30);
//        light.setColor(Color.BLUE);
//
//        Lighting l = new Lighting();
//        l.setLight(light);
//        l.setSurfaceScale(5.0f);


        circle.setTranslateX(950);
        circle.setTranslateY(280);
        circle.setFill(Color.ALICEBLUE);
        circle.setBlendMode(BlendMode.COLOR_BURN);
        circle.setOpacity(0.5);

        DropShadow shadow = new DropShadow(250, Color.AQUA);
        shadow.setInput(new Glow(1));
        circle.setEffect(shadow);
        title.setBlendMode(BlendMode.EXCLUSION);
        title.setEffect(shadow);


        // override menu children
        getRoot().getChildren().setAll(makeBackground(), circle, title,
                btnContinue, btnNew, btnLoad, btnOptions, btnExtra, btnCheats, btnExit);
    }

    private Node makeBackground() {
//        Texture bg = app.getAssetLoader().loadTexture("bg_cctr.jpg");
//        bg.setFitWidth(app.getWidth());
//        bg.setFitHeight(app.getHeight());
//        return bg;
        return new Rectangle(app.getWidth(), app.getHeight(), Color.BLUEVIOLET);
    }

    /**
     * Creates a new button with given name that performs given action on click/press.
     *
     * @param name  button name
     * @param action button action
     * @return new button
     */
    protected final Button createActionButton(String name, Runnable action) {
        Button btn = UIFactory.newButton(name);
        btn.setOnAction(e -> action.run());
        return btn;
    }
}
