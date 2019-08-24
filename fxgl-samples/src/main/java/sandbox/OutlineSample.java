/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Group;
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
@SuppressWarnings("PMD.UnusedPrivateField")
public class OutlineSample extends GameApplication {

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
        //getGameScene().setBackgroundColor(Color.BLACK);

        Texture ship = texture("brick.png");

        var outlineSize = 1;

        var ship1 = ship.copy();
        ship1.setTranslateX(-outlineSize);

        var ship2 = ship.copy();
        ship2.setTranslateX(outlineSize);

        var ship3 = ship.copy();
        ship3.setTranslateY(-outlineSize);

        var ship4 = ship.copy();
        ship4.setTranslateY(outlineSize);

        var group = new Group(ship1.toColor(Color.BLACK), ship2.toColor(Color.BLACK), ship3.toColor(Color.BLACK), ship4.toColor(Color.BLACK), ship.copy());


        addUINode(ship, 100, 100);
        addUINode(group, 300, 100);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
