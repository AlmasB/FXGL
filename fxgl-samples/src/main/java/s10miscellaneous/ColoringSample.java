/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.settings.GameSettings;
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




    }

    @Override
    protected void initUI() {
        getGameScene().setBackgroundColor(Color.color(0.75, 0.75, 0.75));

        Texture original = texture("player2.png");

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

        LinearGradient gradient = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.YELLOW));

        EntityView view = new EntityView();

        Texture outline1 = original.toColor(Color.BLACK);
        view.addNode(outline1);
        outline1.setTranslateX(1);

        Texture outline2 = original.toColor(Color.BLACK);
        view.addNode(outline2);
        outline2.setTranslateX(-1);

        Texture outline3 = original.toColor(Color.BLACK);
        view.addNode(outline3);
        outline3.setTranslateY(1);

        Texture outline4 = original.toColor(Color.BLACK);
        view.addNode(outline4);
        outline4.setTranslateY(-1);

        view.addNode(original.copy());

        HBox hbox = new HBox(20,
                makeBox("Original", original),
                makeBox("Outline", view),
                //makeBox("Grayscale", original.toGrayscale()),
                makeBox("Darker", original.darker()),
                makeBox("Brighter", original.brighter()),
                makeBox("Invert", original.invert()),
                makeBox("Saturate", original.saturate().saturate().saturate())
        );

        HBox hbox2 = new HBox(20,
                makeBox("Soft: self", original.blend(original.copy(), BlendMode.SOFT_LIGHT)),
                makeBox("Add: red", original.blend(new Rectangle(200, 100, Color.RED), BlendMode.ADD)),
                makeBox("Red on blue", original.blend(new Rectangle(200, 100, Color.BLUE), BlendMode.RED)),
                makeBox("Color: red", original.toColor(Color.RED)),
                makeBox("Mult: red", original.multiplyColor(Color.RED)),
                makeBox("Screen: RY", original.blend(new Rectangle(200, 100, gradient), BlendMode.SCREEN))
        );



        Texture brick1 = texture("brick.png");
        brick1.setScaleX(1.5);
        brick1.setScaleY(1.5);

        Texture brick2 = texture("brick.png");
        brick2.setScaleX(1.5);
        brick2.setScaleY(1.5);

        Texture brick3 = texture("brick.png");
        brick3.setScaleX(1.5);
        brick3.setScaleY(1.5);

        Texture brick4 = texture("brick.png");
        brick4.setScaleX(1.5);
        brick4.setScaleY(1.5);

        Texture ship1 = original.copy();
        ship1.setScaleX(0.7);
        ship1.setScaleY(0.7);

        HBox hbox3 = new HBox(20,
                makeBox("Texture 2", texture("brick.png")),
                makeBox("Diff", original.blend(brick1, BlendMode.DIFFERENCE)),
                makeBox("Diff^-1", brick3.blend(ship1, BlendMode.DIFFERENCE)),
                makeBox("Overlay", original.blend(brick2, BlendMode.OVERLAY)),
                makeBox("Exclusion", original.blend(brick4, BlendMode.EXCLUSION))
        );

        hbox.setTranslateY(50);
        hbox2.setTranslateY(250);
        hbox3.setTranslateY(450);

        Text t = getUIFactory().newText("Texture v Texture Blend", Color.WHITE, 24);
        getUIFactory().centerText(t, getWidth() / 2, getHeight() - 150);

        getGameScene().addUINodes(hbox, hbox2, hbox3, t);
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
