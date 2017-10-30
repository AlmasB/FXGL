/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Pos;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.app.DSLKt.texture;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ColoringSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ColoringSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    @Override
    protected void initUI() {
        //getGameScene().setBackgroundColor(Color.BLACK);

        Texture original = texture("bird.png");

//        long start = System.nanoTime();
//
//        original.blend(new Rectangle(100, 100, Color.RED), BlendMode.ADD);
//
//        System.out.println(System.nanoTime() - start);
//
//        start = System.nanoTime();
//
//        original.manualBlend(new Rectangle(100, 100, Color.RED), BlendMode.ADD);
//
//        System.out.println(System.nanoTime() - start);


        HBox hbox = new HBox(20,
                makeBox("Original", original),
                makeBox("Alpha: black", original.transparentColor(Color.BLACK)),
                makeBox("Grayscale", original.toGrayscale()),
                makeBox("Darker", original.darker()),
                makeBox("Brighter", original.brighter())
        );

        HBox hbox2 = new HBox(20,
                makeBox("Blend: red", original.blend(new Rectangle(100, 100, Color.RED), BlendMode.ADD)),
                makeBox("Blend: red", original.blend(new Rectangle(200, 100, Color.BLUE), BlendMode.RED)),
                //makeBox("Colorize: red", original.toColor(Color.RED)),
                makeBox("Multiply: red", original.multiplyColor(Color.RED))
        );

        hbox.setTranslateY(100);
        hbox2.setTranslateY(300);

        getGameScene().addUINodes(hbox, hbox2);
    }

    private VBox makeBox(String header, Texture texture) {
        VBox vbox = new VBox(10,
                getUIFactory().newText(header, Color.BLACK, 24),
                texture);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
