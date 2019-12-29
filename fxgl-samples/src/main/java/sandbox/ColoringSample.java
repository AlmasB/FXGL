/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ColoringSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(920);
    }

    @Override
    protected void initUI() {
        getGameScene().setBackgroundColor(Color.color(0.75, 0.75, 0.75));

        Texture original = texture("tank_enemy.png");

        LinearGradient gradient = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.YELLOW));

        HBox hbox = new HBox(20,
                makeBox("Original", original),
                makeBox("Outline", original.outline(Color.BLUE)),
                makeBox("Darker", original.darker()),
                makeBox("Brighter", original.brighter()),
                makeBox("Invert", original.invert()),
                makeBox("Saturate", original.saturate().saturate().saturate())
        );

        HBox hbox2 = new HBox(20,
                makeBox("Soft: self", original.blend(original.copy(), BlendMode.SOFT_LIGHT)),
                makeBox("Add: red", original.blend(new Rectangle(original.getWidth(), original.getHeight(), Color.RED), BlendMode.ADD)),
                makeBox("Red on blue", original.blend(new Rectangle(original.getWidth(), original.getHeight(), Color.BLUE), BlendMode.RED)),
                makeBox("Color: red", original.toColor(Color.RED)),
                makeBox("Mult: red", original.multiplyColor(Color.RED)),
                makeBox("Screen: RY", original.blend(new Rectangle(original.getWidth(), original.getHeight(), gradient), BlendMode.SCREEN))
        );

        Texture brick1 = texture("grass.png");
        brick1.setScaleX(1.5);
        brick1.setScaleY(1.5);

        Texture brick2 = texture("grass.png");
        brick2.setScaleX(1.5);
        brick2.setScaleY(1.5);

        Texture brick3 = texture("grass.png");
        brick3.setScaleX(1.5);
        brick3.setScaleY(1.5);

        Texture brick4 = texture("grass.png");
        brick4.setScaleX(1.5);
        brick4.setScaleY(1.5);

        Texture ship1 = original.copy();
        ship1.setScaleX(0.7);
        ship1.setScaleY(0.7);

        HBox hbox3 = new HBox(20);
        HBox hbox4 = new HBox(20);

        for (var blend : BlendMode.values()) {
            try {
                hbox3.getChildren().add(makeBox(blend.toString(), original.blend(ImagesKt.resize(brick1.getImage(), (int) original.getWidth(), (int) original.getHeight()), blend)));
                hbox4.getChildren().add(makeBox(blend.toString() + "-1", brick1.blend(ImagesKt.resize(original.getImage(), (int) brick1.getWidth(), (int) brick1.getHeight()), blend)));
            } catch (Exception e) {

            }

//            makeBox("Texture 2", texture("grass.png")),
//                    ,
//                    makeBox("Diff^-1", brick3.blend(ImagesKt.resize(ship1.getImage(), (int) brick3.getWidth(), (int) brick3.getHeight()), BlendMode.DIFFERENCE)),
//                    makeBox("Overlay", original.blend(ImagesKt.resize(brick2.getImage(), (int) original.getWidth(), (int) original.getHeight()), BlendMode.OVERLAY)),
//                    makeBox("Exclusion", original.blend(ImagesKt.resize(brick4.getImage(), (int) original.getWidth(), (int) original.getHeight()), BlendMode.EXCLUSION)),
//                    makeBox("SoftLight", original.blend(ImagesKt.resize(brick4.getImage(), (int) original.getWidth(), (int) original.getHeight()), BlendMode.SOFT_LIGHT))
        }

        hbox.setTranslateY(50);
        hbox2.setTranslateY(250);
        hbox3.setTranslateY(450);
        hbox4.setTranslateY(620);

        Text t = getUIFactory().newText("Texture v Texture Blend", Color.WHITE, 24);
        centerText(t, 150, 450);

        getGameScene().addUINodes(hbox, hbox2, hbox3, hbox4, t);
    }

    private VBox makeBox(String header, Node texture) {
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
