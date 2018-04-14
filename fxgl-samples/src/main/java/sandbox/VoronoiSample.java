/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.extra.algorithm.VoronoiSubdivision;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class VoronoiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("VoronoiSample");
        settings.setVersion("0.1");




    }

    @Override
    protected void initInput() {

        onKey(KeyCode.Q, "dec", () -> inc("hp", -0.01));
        onKey(KeyCode.E, "inc", () -> inc("hp", +0.01));

        getInput().addAction(new UserAction("Voronoi") {
            @Override
            protected void onActionBegin() {

                VoronoiSubdivision.divide(getAppBounds(), 100, 2).forEach(p -> {

                    p.setStrokeWidth(1.5);
                    p.strokeProperty().bind(getop("stroke"));
                    p.setFill(Color.RED);

                    p.opacityProperty().bind(
                            new SimpleDoubleProperty(1).subtract(getdp("hp")).multiply(distance(p) / 500 * 3)

                    );

                    getGameScene().addUINode(p);
                });

                Text text = getUIFactory().newText("", Color.BLACK, 18.0);
                text.setTranslateX(50);
                text.setTranslateY(50);
                text.textProperty().bind(getdp("hp").asString("%.2f"));

                getGameScene().addUINode(text);
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("hp", 1.0);
        vars.put("stroke", Color.RED);
    }

    @Override
    protected void initGame() {
        getdp("hp").addListener((o, oldValue, newValue) -> {
            if (newValue.doubleValue() <= 1.0 && newValue.doubleValue() >= 0.0)
                set("stroke", Color.color(1, 0, 0, 1.0 - newValue.doubleValue()));
        });
    }

    private double distance(Polygon p) {
        return new Point2D(400, 300).distance(p.getPoints().get(0), p.getPoints().get(1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
