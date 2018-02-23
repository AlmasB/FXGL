/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.animation.EasingInterpolator;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.LevelText;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.app.DSLKt.set;

/**
 * Shows how to use interpolators.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InterpolatorSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InterpolatorSample");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("canPlay", true);
    }

    @Override
    protected void initGame() {
        LevelText text = new LevelText("Level 1");
        text.animateIn();

        player = Entities.builder()
                .viewFromNode(text)
                .buildAndAttach(getGameWorld());
    }

    private ToggleGroup group = new ToggleGroup();
    private ToggleGroup group2 = new ToggleGroup();

    @Override
    protected void initUI() {
        VBox vbox = new VBox(5);

        for (EasingInterpolator interpolator : Interpolators.values()) {
            Button btn = new Button(interpolator.toString());
            btn.disableProperty().bind(getGameState().booleanProperty("canPlay").not());
            btn.setOnAction(e -> playAnimation(interpolator));

            vbox.getChildren().add(btn);
        }

        RadioButton btn1 = new RadioButton("Ease In");
        RadioButton btn2 = new RadioButton("Ease Out");
        RadioButton btn3 = new RadioButton("Ease In Out");
        btn1.setToggleGroup(group);
        btn2.setToggleGroup(group);
        btn3.setToggleGroup(group);
        btn2.setSelected(true);

        RadioButton btn4 = new RadioButton("Translate");
        RadioButton btn5 = new RadioButton("Scale");
        RadioButton btn6 = new RadioButton("Rotate");
        btn4.setToggleGroup(group2);
        btn5.setToggleGroup(group2);
        btn6.setToggleGroup(group2);
        btn4.setSelected(true);

        vbox.getChildren().addAll(btn1, btn2, btn3, btn4, btn5, btn6);

        vbox.setTranslateX(650);
        getGameScene().addUINode(vbox);
    }

    private void playAnimation(EasingInterpolator interpolator) {
        set("canPlay", false);

        Interpolator ease = getEase(interpolator);

        String animType = ((RadioButton) group2.getSelectedToggle()).getText();

        switch (animType) {
            case "Translate":
                Entities.animationBuilder()
                        .interpolator(ease)
                        .duration(Duration.seconds(2.3))
                        .onFinished(() -> set("canPlay", true))
                        .translate(player)
                        .from(new Point2D(0, -150))
                        .to(new Point2D(0, 300))
                        .buildAndPlay();
                break;
            case "Scale":
                Entities.animationBuilder()
                        .interpolator(ease)
                        .autoReverse(true)
                        .repeat(2)
                        .duration(Duration.seconds(1.15))
                        .onFinished(() -> set("canPlay", true))
                        .scale(player)
                        .from(new Point2D(1, 1))
                        .to(new Point2D(0, 0))
                        .buildAndPlay();
                break;
            case "Rotate":
                Entities.animationBuilder()
                        .interpolator(ease)
                        .duration(Duration.seconds(2.3))
                        .onFinished(() -> set("canPlay", true))
                        .rotate(player)
                        .rotateFrom(0)
                        .rotateTo(360)
                        .buildAndPlay();
                break;
        }


    }

    private Interpolator getEase(EasingInterpolator interpolator) {
        String name = ((RadioButton) group.getSelectedToggle()).getText();

        if (name.equals("Ease In")) {
            return interpolator.EASE_IN();
        } else if (name.equals("Ease Out")) {
            return interpolator.EASE_OUT();
        } else {
            return interpolator.EASE_IN_OUT();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
