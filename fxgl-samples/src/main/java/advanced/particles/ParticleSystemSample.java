/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package advanced.particles;

import com.almasb.fxgl.animation.EasingInterpolator;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Example of using particles and their configurations via an emitter.
 */
public class ParticleSystemSample extends GameApplication {

    private ParticleEmitter emitter;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(700 + 300);
        settings.setHeight(600);
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .view(new Rectangle(700, getAppHeight()))
                .buildAndAttach();

        emitter = ParticleEmitters.newFireEmitter();

        entityBuilder()
                .at((getAppWidth() - 300) / 2, getAppHeight() / 2)
                .with(new ParticleComponent(emitter))
                .buildAndAttach();
    }

    @Override
    protected void initUI() {
        Spinner<Integer> spinnerNumParticles = new Spinner<>(0, 100, 15, 1);
        emitter.numParticlesProperty().bind(spinnerNumParticles.valueProperty());

        Spinner<Double> spinnerRate = new Spinner<>(0.0, 1.0, 1.0, 0.05);
        emitter.emissionRateProperty().bind(spinnerRate.valueProperty());

        Spinner<Double> spinnerMinSize = new Spinner<>(1.0, 100.0, 9.0, 1);
        emitter.minSizeProperty().bind(spinnerMinSize.valueProperty());

        Spinner<Double> spinnerMaxSize = new Spinner<>(1.0, 100.0, 12.0, 1);
        emitter.maxSizeProperty().bind(spinnerMaxSize.valueProperty());

        Spinner<Double> spinnerVelX = new Spinner<>(-500.0, 500.0, 0.0, 10);
        Spinner<Double> spinnerVelY = new Spinner<>(-500.0, 500.0, -30.0, 10);

        spinnerVelX.setPrefWidth(65);
        spinnerVelY.setPrefWidth(65);

        spinnerVelX.valueProperty().subscribe(newX -> {
            emitter.setVelocityFunction(i -> new Point2D(newX, spinnerVelY.getValue()));
        });

        spinnerVelY.valueProperty().subscribe(newY -> {
            emitter.setVelocityFunction(i -> new Point2D(spinnerVelX.getValue(), newY));
        });

        Spinner<Double> spinnerAccelX = new Spinner<>(-150.0, 150.0, 0.0, 10);
        Spinner<Double> spinnerAccelY = new Spinner<>(-150.0, 150.0, 0.0, 10);

        spinnerAccelX.setPrefWidth(65);
        spinnerAccelY.setPrefWidth(65);

        spinnerAccelX.valueProperty().subscribe(newX -> {
            emitter.setAccelerationFunction(() -> new Point2D(newX, spinnerAccelY.getValue()));
        });

        spinnerAccelY.valueProperty().subscribe(newY -> {
            emitter.setAccelerationFunction(() -> new Point2D(spinnerAccelX.getValue(), newY));
        });

        ChoiceBox<BlendMode> choiceBlend = getUIFactoryService().newChoiceBox(
                FXCollections.observableArrayList(BlendMode.values())
        );

        emitter.blendModeProperty().bind(choiceBlend.valueProperty());
        choiceBlend.setValue(BlendMode.ADD);

        ChoiceBox<EasingInterpolator> choiceInterpolator = getUIFactoryService().newChoiceBox(
                FXCollections.observableArrayList(Interpolators.values())
        );

        choiceInterpolator.setValue(Interpolators.LINEAR);

        choiceInterpolator.valueProperty().addListener((observable, oldValue, newValue) -> {
            emitter.setInterpolator(newValue.EASE_OUT());
        });

        ColorPicker startColor = new ColorPicker((Color) emitter.getStartColor());
        ColorPicker endColor = new ColorPicker((Color) emitter.getEndColor());

        emitter.startColorProperty().bind(startColor.valueProperty());
        emitter.endColorProperty().bind(endColor.valueProperty());

        VBox vbox = new VBox(10);
        vbox.setTranslateX(700);
        vbox.getChildren().addAll(
                new HBox(10, new Text("Vel X and Y"), spinnerVelX, spinnerVelY),
                new HBox(10, new Text("Acc X and Y"), spinnerAccelX, spinnerAccelY),
                new Text("Number of Particles:"), spinnerNumParticles,
                new Text("Min Size:"), spinnerMinSize,
                new Text("Max Size:"), spinnerMaxSize,
                new Text("Emission Rate:"), spinnerRate,
                new Text("Start Color:"), startColor,
                new Text("End Color:"), endColor,
                choiceBlend,
                choiceInterpolator
        );

        getGameScene().addUINode(vbox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
