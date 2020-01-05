/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package ui;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.ui.FontType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FontSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initUI() {
        addExampleFontUI(0, 20);
        addExampleFontGame(0, 55);
        addExampleFontMono(0, 120);
        addExampleFontText(0, 150);
    }

    private void addExampleFontUI(int x, int y) {
        Font fontUI = getUIFactory().newFont(FontType.UI, 14.0);

        Text text = new Text("This is UI_FONT: Lives: 3, Ammo: 99");
        text.setFont(fontUI);

        addUINode(text, x, y);
    }

    private void addExampleFontGame(int x, int y) {
        Font fontGame = getUIFactory().newFont(FontType.GAME, 20.0);

        Text text = new Text("This is GAME_FONT: Blue Rectangle");
        text.setFont(fontGame);
        text.setTranslateX(45);
        text.setTranslateY(22);

        entityBuilder()
                .at(x, y)
                .view(new Rectangle(40, 40, Color.BLUE))
                .view(text)
                .buildAndAttach();
    }

    private void addExampleFontText(int x, int y) {
        Font fontText = getUIFactory().newFont(FontType.TEXT, 16.0);

        Text text = new Text("This is TEXT_FONT: The MIT License (MIT)\n" +
                "\n" +
                "Copyright (c) 2015-2020 Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)\n" +
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

        text.setFont(fontText);

        addUINode(text, x, y);
    }

    private void addExampleFontMono(int x, int y) {
        Font fontMono = getUIFactory().newFont(FontType.MONO, 18);

        Text text = new Text("This is MONO_FONT: var e = new Entity();");
        text.setFont(fontMono);

        addUINode(text, x, y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
