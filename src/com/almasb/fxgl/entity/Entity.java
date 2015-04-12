package com.almasb.fxgl.entity;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;

/**
 * A generic FXGL game object
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.1
 *
 */
public class Entity extends Parent {

    public static final String PR_TYPE = "PR_TYPE";
    public static final String PR_USE_PHYSICS = "PR_USE_PHYSICS";

    private List<Control> controls = new ArrayList<>();

    /**
     * Constructs an entity with given type
     * Type must NOT be an empty string or null
     *
     * @param type
     */
    public Entity(String type) {
        if (type == null || type.isEmpty())
            type = "undefined";

        setProperty(PR_TYPE, type);
        setGraphics(new Text("null"));
        setUsePhysics(false);
    }

    /**
     * Allow this entity to participate in collision detection
     *
     * @param b
     */
    public Entity setUsePhysics(boolean b) {
        setProperty(PR_USE_PHYSICS, b);
        return this;
    }

    /**
     *
     * @return entity position - translation from the parent's origin
     */
    public Point2D getPosition() {
        return new Point2D(getTranslateX(), getTranslateY());
    }

    /**
     * Equivalent to
     *
     * <pre>
     * setTranslateX()
     * setTranslateY()
     * </pre>
     *
     * @param x
     * @param y
     * @return this entity
     */
    public Entity setPosition(double x, double y) {
        setTranslateX(x);
        setTranslateY(y);
        return this;
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
     * Translate (move) entity by vector
     *
     * @param vector
     */
    public void translate(Point2D vector) {
        translate(vector.getX(), vector.getY());
    }

    /**
     * @return entity type
     */
    public String getType() {
        return getProperty(PR_TYPE);
    }

    /**
     *
     * @return graphics object associated with entity
     */
    public Node getGraphics() {
        return getChildren().get(0);
    }

    /**
     * Set graphics for this entity
     *
     * @param graphics
     * @return this entity
     */
    public Entity setGraphics(Node graphics) {
        getChildren().clear();
        getChildren().add(graphics);
        return this;
    }

    /**
     * Do NOT call prior to adding the entity to root
     *
     * @return width of the bounding box of this entity
     */
    public double getWidth() {
        return getBoundsInParent().getWidth();
    }

    /**
     * Do NOT call prior to adding the entity to root
     *
     * @return height of the bounding box of this entity
     */
    public double getHeight() {
        return getBoundsInParent().getHeight();
    }

    /**
     * Add behavior to entity
     *
     * @param control
     */
    public Entity addControl(Control control) {
        controls.add(control);
        return this;
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
     * Do NOT call manually. It is called automatically
     * by FXGL GameApplication when entity has been removed
     *
     */
    public final void onClean() {

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
    public Entity setProperty(String name, Object value) {
        getProperties().put(name, value);
        return this;
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
