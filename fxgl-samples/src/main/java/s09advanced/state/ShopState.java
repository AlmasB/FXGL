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

package s09advanced.state;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.State;
import com.almasb.fxgl.app.SubState;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.service.impl.input.FXGLInput;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ShopState extends SubState {

    private Node view = new ShopView();

    public ShopState() {

        view.setTranslateX(200);
        view.setTranslateY(200);

        getInput().addAction(new UserAction("Hello") {
            @Override
            protected void onAction() {
                view.setTranslateX(view.getTranslateX() + 5);
            }
        }, KeyCode.D);

        getChildren().add(view);
    }

    @Override
    public void onEnter(@NotNull State prevState) {

    }

    @Override
    public void onExit() {

    }

    @Override
    public void onUpdate(double tpf) {

    }

    @NotNull
    @Override
    public String getName() {
        return "ShopState";
    }

    private static class ShopView extends StackPane {
        private VBox box;

        public ShopView() {

            int width = 300;
            int height = 300;

            Rectangle bg = new Rectangle(width, height);
            bg.setFill(Colors.MENU_BG);

            Rectangle lineTop = new Rectangle(width, 2);
            lineTop.setFill(Colors.MENU_BORDER);
            lineTop.setStroke(Color.BLACK);

            Rectangle lineBot = new Rectangle(width, 2);
            lineBot.setTranslateY(height - 2);
            lineBot.setFill(Colors.MENU_BORDER);
            lineBot.setStroke(Color.BLACK);

            box = new VBox(5);
            box.setTranslateX(25);
            box.setTranslateY(25);

            Button btn = new Button("X");
            btn.setOnAction(e -> FXGL.getApp().popState());

            getChildren().addAll(bg, lineTop, lineBot, box, btn);
        }
    }
}
