/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.ui;

import com.almasb.sslogger.Logger;
import javafx.animation.*;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * A generic progress bar. Can be used to show
 * health, mana, experience, quest progress, reloading times, etc
 * <pre>
 * Example:
 *
 * ProgressBar hpBar = new ProgressBar();
 * hpBar.setMinValue(0);
 * hpBar.setMaxValue(1000);
 * hpBar.setCurrentValue(1000);
 * hpBar.setWidth(300);
 * hpBar.setLabelVisible(true);
 * hpBar.setLabelPosition(Position.RIGHT);
 * hpBar.setFill(Color.GREEN);
 *
 * addUINodes(hpBar);
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class ProgressBar extends Parent {

    private static final Logger log = Logger.get("FXGL.ProgressBar");

    private DoubleProperty minValue = new SimpleDoubleProperty(0.0);
    private DoubleProperty currentValue = new SimpleDoubleProperty(0.0);
    private DoubleProperty maxValue = new SimpleDoubleProperty(100.0);

    private DoubleProperty width = new SimpleDoubleProperty(200.0);
    private DoubleProperty height = new SimpleDoubleProperty(10.0);

    private Rectangle backgroundBar = new Rectangle();
    private Rectangle innerBar = new Rectangle();

    private Group barGroup = new Group();

    private Label label = new Label();
    private Position labelPosition = Position.BOTTOM;

    private Paint traceFill = Color.WHITE;

    private Timeline timeline = new Timeline();

    private ChangeListener<Number> update;

    public ProgressBar() {
        this(true);
    }

    public ProgressBar(boolean showChanges) {
        innerBar.setTranslateX(5);
        innerBar.setTranslateY(3);
        innerBar.setFill(Color.rgb(255, 239, 211));

        backgroundBar.widthProperty().bind(width);
        backgroundBar.heightProperty().bind(height);

        innerBar.heightProperty().bind(height.subtract(6));

        backgroundBar.arcWidthProperty().bind(width.divide(8));
        backgroundBar.arcHeightProperty().bind(width.divide(8));
        innerBar.arcWidthProperty().bind(width.divide(8));
        innerBar.arcHeightProperty().bind(width.divide(8));

        DropShadow ds = new DropShadow(10, Color.WHITE);
        ds.setInput(new Glow(0.3));
        ds.setWidth(50);
        backgroundBar.setEffect(ds);

        ds = new DropShadow(5, Color.GOLD);
        ds.setInput(new Glow(0.1));
        innerBar.setEffect(ds);

        label.setFont(Font.font(16));
        label.setTextFill(Color.GOLD);
        label.textProperty().bind(currentValue.asString("%.0f").concat("/").concat(maxValue.asString("%.0f")));

        update = (obs, oldValue, newValue) -> {
            if (!showChanges)
                return;

            double newWidth = (width.get() - 10) *
                    (currentValue.get() - minValue.get()) / (maxValue.get() - minValue.get());
            int diff = newValue.intValue() - oldValue.intValue();

            // text value animation

            if (diff != 0) { //allows reacting to width changes without showing a label
                Text text = new Text((diff > 0 ? "+" : "") + diff);
                text.setTranslateX(newWidth + (diff > 0 ? 5 : 25));
                text.setTranslateY(height.get() / 2);
                text.setFill(traceFill);
                text.setFont(Font.font(14));

                barGroup.getChildren().add(text);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.66), text);
                tt.setToY(0);

                FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
                ft.setToValue(0);

                ParallelTransition pt = new ParallelTransition(ft);
                pt.setOnFinished(e -> barGroup.getChildren().remove(text));
                pt.play();
            }

            // trace shown as a flash

            Rectangle trace = new Rectangle(Math.abs(newWidth - innerBar.getWidth()), height.get() - 6);
            trace.setArcWidth(innerBar.getArcWidth());
            trace.setArcHeight(innerBar.getArcHeight());
            trace.setTranslateX(Math.min(innerBar.getWidth(), newWidth));
            trace.setTranslateY(3);
            trace.setFill(traceFill);
            trace.setOpacity(0.55);
            trace.setEffect(new Glow(0.5));

            if (trace.getWidth() > 50) {
                barGroup.getChildren().add(trace);

                FadeTransition ft2 = new FadeTransition(Duration.seconds(0.5), trace);
                ft2.setToValue(0);
                ft2.setOnFinished(e -> barGroup.getChildren().remove(trace));
                ft2.play();
            }

            // smooth fill animation
            timeline.stop();
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.66),
                    new KeyValue(innerBar.widthProperty(), newWidth)));
            timeline.play();
        };

        currentValue.addListener(update);

        barGroup.getChildren().addAll(backgroundBar, innerBar);
        getChildren().addAll(barGroup, label);
        setLabelPosition(labelPosition);
        setLabelVisible(false);

        if (!showChanges) {
            innerBar.widthProperty().bind(width.subtract(10).multiply(new DoubleBinding() {
                {
                    super.bind(minValue, currentValue, maxValue);
                }

                @Override
                protected double computeValue() {
                    return (currentValue.get() - minValue.get())
                            / (maxValue.get() - minValue.get());
                }
            }));
        }
    }

    public void setBackgroundFill(Paint color) {
        backgroundBar.setFill(color);
    }

    public void setFill(Color color) {
        innerBar.setFill(color);
        DropShadow ds = new DropShadow(15, color);
        ds.setInput(new Glow(0.5));
        innerBar.setEffect(ds);
    }

    public void setLabelFill(Paint color) {
        label.setTextFill(color);
    }

    public void setTraceFill(Paint color) {
        traceFill = color;
    }

    public void setLabelVisible(boolean b) {
        if (!b) {
            getChildren().remove(label);
            barGroup.translateXProperty().unbind();
            barGroup.translateYProperty().unbind();
            barGroup.setTranslateX(0);
            barGroup.setTranslateY(0);
        } else if (!getChildren().contains(label)) {
            getChildren().add(label);
            setLabelPosition(labelPosition);
        }
    }

    public boolean isLabelVisible() {
        return getChildren().contains(label);
    }

    public void setLabelPosition(Position pos) {
        labelPosition = pos;
        if (!isLabelVisible())
            return;

        barGroup.translateXProperty().unbind();
        barGroup.translateYProperty().unbind();
        label.translateXProperty().unbind();
        label.translateYProperty().unbind();
        switch (pos) {
            case BOTTOM:
                barGroup.setTranslateX(0);
                barGroup.setTranslateY(0);
                label.translateXProperty().bind(width.divide(2).subtract(label.widthProperty().divide(2)));
                label.translateYProperty().bind(height);
                break;
            case LEFT:
                barGroup.translateXProperty().bind(label.widthProperty().add(10));
                barGroup.setTranslateY(0);
                label.setTranslateX(0);
                label.translateYProperty().bind(height.divide(2).subtract(label.heightProperty().divide(2)));
                break;
            case RIGHT:
                barGroup.setTranslateX(0);
                barGroup.setTranslateY(0);
                label.translateXProperty().bind(width.add(10));
                label.translateYProperty().bind(height.divide(2).subtract(label.heightProperty().divide(2)));
                break;
            case TOP:
                barGroup.setTranslateX(0);
                barGroup.translateYProperty().bind(label.heightProperty());
                label.translateXProperty().bind(width.divide(2).subtract(label.widthProperty().divide(2)));
                label.setTranslateY(0);
                break;
            default:
                log.warning("Unknown position: " + pos);
                break;
        }
    }

    public void setWidth(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Width must be > 0");

        width.set(value);
        update.changed(currentValue, currentValue.get(), currentValue.get());
    }

    public void setHeight(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Height must be > 0");

        height.set(value);
    }

    public void setMinValue(double value) {
        if (value > currentValue.get()) {
            log.warning("Current value < min value. Setting min value as current");
            currentValue.set(value);
        }

        if (value >= maxValue.get()) {
            log.warning("Min value >= max value. Setting max value to min value + 1");
            maxValue.set(value + 1);
        }

        minValue.set(value);
    }

    public DoubleProperty minValueProperty() {
        return minValue;
    }

    public void setCurrentValue(double value) {
        double newValue = value;

        if (value < minValue.get()) {
            log.warning("Current value < min value. Setting min value as current");
            newValue = minValue.get();
        } else if (value > maxValue.get()) {
            log.warning("Current value > max value. Setting max value as current");
            newValue = maxValue.get();
        }

        currentValue.set(newValue);
    }

    public double getCurrentValue() {
        return currentValue.get();
    }

    public DoubleProperty currentValueProperty() {
        return currentValue;
    }

    public void setMaxValue(double value) {
        if (value <= minValue.get()) {
            log.warning("Max value <= min value. Setting min value to max value - 1");
            minValue.set(value - 1);
        }

        if (value < currentValue.get()) {
            log.warning("Max value < current value. Setting current value to max");
            currentValue.set(value);
        }

        maxValue.set(value);
    }

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    public static ProgressBar makeHPBar() {
        ProgressBar bar = new ProgressBar();
        bar.setHeight(25);
        bar.setFill(Color.GREEN.brighter());
        bar.setTraceFill(Color.GREEN.brighter());
        bar.setLabelVisible(true);
        return bar;
    }

    public static ProgressBar makeSkillBar() {
        ProgressBar bar = new ProgressBar();
        bar.setHeight(25);
        bar.setFill(Color.BLUE.brighter().brighter());
        bar.setTraceFill(Color.BLUE);
        bar.setLabelVisible(true);
        return bar;
    }
}