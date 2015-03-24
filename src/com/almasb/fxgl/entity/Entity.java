package com.almasb.fxgl.entity;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;

public class Entity extends Parent {

    private List<Control> controls = new ArrayList<>();

    public Entity(String type) {
        if (type.isEmpty())
            throw new IllegalArgumentException("Entity type cannot be empty");

        setProperty("type", type);
        setProperty("usePhysics", false);
        setGraphics(new Text("null"));
    }

    public void setUsePhysics(boolean b) {
        setProperty("usePhysics", b);
    }

    public void translate(double x, double y) {
        setTranslateX(getTranslateX() + x);
        setTranslateY(getTranslateY() + y);
    }

    public String getType() {
        return getProperty("type");
    }

    public Node getGraphics() {
        return getChildren().get(0);
    }

    public void setGraphics(Node graphics) {
        getChildren().clear();
        getChildren().add(graphics);
    }

    public void addControl(Control control) {
        controls.add(control);
    }

    public void removeControl(Control control) {
        controls.remove(control);
    }

    public final void onUpdate(long now) {
        controls.forEach(control -> control.onUpdate(this, now));
    }

    public void setProperty(String name, Object value) {
        getProperties().put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T)getProperties().get(name);
    }
}
