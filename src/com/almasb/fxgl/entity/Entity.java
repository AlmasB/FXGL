package com.almasb.fxgl.entity;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;

/**
 * A generic FXGL game object
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public class Entity extends Parent {

    private List<Control> controls = new ArrayList<>();

    /**
     * Constructs an entity with given type
     * Type must NOT be an empty string or null
     *
     * @param type
     */
    public Entity(String type) {
        if (type.isEmpty())
            throw new IllegalArgumentException("Entity type cannot be empty");

        setProperty("type", type);
        setProperty("usePhysics", false);
        setGraphics(new Text("null"));
    }

    /**
     * Allow this entity to participate in collision detection
     *
     * @param b
     */
    public void setUsePhysics(boolean b) {
        setProperty("usePhysics", b);
    }

    /**
     * Translate (move) entity by vector (x, y)
     *
     * @param x
     * @param y
     */
    public void translate(double x, double y) {
        setTranslateX(getTranslateX() + x);
        setTranslateY(getTranslateY() + y);
    }

    /**
     * @return entity type
     */
    public String getType() {
        return getProperty("type");
    }

    /**
     *
     * @return graphics object associated with entity
     */
    public Node getGraphics() {
        return getChildren().get(0);
    }

    /**
     * Set graphical representation of entity
     * Each graphics object can only be associated with 1 entity
     * as per JavaFX scene graph specification
     *
     * @param graphics
     */
    public void setGraphics(Node graphics) {
        getChildren().clear();
        getChildren().add(graphics);
    }

    /**
     * Add behavior to entity
     *
     * @param control
     */
    public void addControl(Control control) {
        controls.add(control);
    }

    /**
     * Remove behavior from entity
     *
     * @param control
     */
    public void removeControl(Control control) {
        controls.remove(control);
    }

    /**
     * Do NOT call manually. It is called automatically
     * by FXGL GameApplication
     *
     * @param now
     */
    public final void onUpdate(long now) {
        controls.forEach(control -> control.onUpdate(this, now));
    }

    /**
     * Set a custom property
     *
     * <pre>
     * Example:
     *
     * player.setProperty("hp", 200);
     * player.setProperty("alive", true);
     * </pre>
     *
     * @param name
     * @param value
     */
    public void setProperty(String name, Object value) {
        getProperties().put(name, value);
    }

    /**
     * Get value of a custom property that was previously set
     * by {@link #setProperty(String, Object)}
     *
     * <pre>
     * Example:
     *
     * if (player.<Boolean>getProperty("alive")) {
     *      // property "alive" is true
     * }
     *
     * int hp = player.getProperty("hp");
     * </pre>
     *
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T)getProperties().get(name);
    }
}
