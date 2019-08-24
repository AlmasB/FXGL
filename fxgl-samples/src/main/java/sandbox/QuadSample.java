/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * http://blog.studiominiboss.com/pixelart #21
 * 1.
 * 2.
 * 3.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class QuadSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("QuadSample");
        settings.setVersion("0.1");
    }

    private List<Texture> originals = new ArrayList<>();
    private List<Texture> textures = new ArrayList<>();
    private List<Texture> textures2 = new ArrayList<>();

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        Texture ship = texture("player2.png");

        double h = 7;

        System.out.println(h);

        for (int i = 0; i < 10; i++) {
            Texture quad = ship.subTexture(new Rectangle2D(0, i*h, ship.getImage().getWidth(), h));
            quad.setTranslateY(100 + i*h);

            originals.add(quad);

            textures.add(quad.copy());
            textures2.add(quad.toColor(Color.RED));

            getGameScene().addUINode(quad);
        }
    }

    private int j = 0;

    @Override
    protected void onUpdate(double tpf) {

        Texture t = originals.get(j);

        t.setImage(textures2.get(j).getImage());

        if (j > 0) {
            originals.get(j-1).setImage(textures.get(j-1).getImage());
        }

        j++;

        if (j == 10) {
            j = 0;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
