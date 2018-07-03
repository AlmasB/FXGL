/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.InGameWindow;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FontSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1000);
        settings.setHeight(700);
    }

    @Override
    protected void initUI() {
        addExampleFontUI(0, 20);
        addExampleFontGame(0, 55);
        addExampleFontMono(0, 120);
        addExampleFontText(0, 150);
    }

    private void addExampleFontUI(int x, int y) {
        Font fontUI = FXGLAssets.UI_FONT.newFont(20);

        Text text = new Text("This is UI_FONT: Lives: 3, Ammo: 99");
        text.setFont(fontUI);

        addUINode(text, x, y);
    }

    private void addExampleFontGame(int x, int y) {
        Font fontGame = FXGLAssets.UI_GAME_FONT.newFont(20);

        Text text = new Text("This is GAME_FONT: Blue Rectangle");
        text.setFont(fontGame);
        text.setTranslateX(45);
        text.setTranslateY(22);

        EntityView view = new EntityView(new Rectangle(40, 40, Color.BLUE));
        view.addNode(text);

        Entities.builder()
                .at(x, y)
                .viewFromNode(view)
                .buildAndAttach();
    }

    private void addExampleFontText(int x, int y) {
        Font fontText = FXGLAssets.UI_TEXT_FONT.newFont(16);

        Text text = new Text("This is TEXT_FONT: The MIT License (MIT)\n" +
                "\n" +
                "Copyright (c) 2015-2018 Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)\n" +
                "\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
                "of this software and associated documentation files (the \"Software\"), to deal\n" +
                "in the Software without restriction, including without limitation the rights\n" +
                "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
                "copies of the Software, and to permit persons to whom the Software is\n" +
                "furnished to do so, subject to the following conditions:\n" +
                "\n" +
                "The above copyright notice and this permission notice shall be included in all\n" +
                "copies or substantial portions of the Software.\n" +
                "\n" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
                "SOFTWARE.");

        //text.setFont(Font.font(16));
        text.setFont(fontText);

        VBox box = new VBox(text);
        box.setPadding(new Insets(10));

        // TODO: Bug? NONE doesn't work
        InGameWindow window = new InGameWindow("", InGameWindow.WindowDecor.NONE);
        window.setContentPane(box);
        window.setCanResize(false);
        window.setBackgroundColor(Color.color(0.95, 0.95, 0.95));

        // TODO: why do we have setPosition() and also can set via add ui node?
        window.setPosition(x, y);

        addUINode(window, 0, 0);
    }

    private void addExampleFontMono(int x, int y) {
        Font fontMono = FXGLAssets.UI_MONO_FONT.newFont(18);

        Text text = new Text("This is MONO_FONT: var e = new Entity();");
        text.setFont(fontMono);

        addUINode(text, x, y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
