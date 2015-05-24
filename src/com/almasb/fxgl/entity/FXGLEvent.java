package com.almasb.fxgl.entity;


public class FXGLEvent {

    private FXGLEventType type;
    private Entity source, target;

    public FXGLEvent(FXGLEventType type) {
        this(type, null);
    }

    public FXGLEvent(FXGLEventType type, Entity source) {
        this.type = type;
        this.source = source;
    }

    public FXGLEventType getType() {
        return type;
    }

    /**
     *
     * @return the entity which triggered the event
     */
    public Entity getSource() {
        return source;
    }

    /* package-private */ void setSource(Entity source) {
        this.source = source;
    }

    /**
     *
     * @return the entity on which the event was fired by
     *          calling {@link Entity#fireFXGLEvent(FXGLEvent)}
     */
    public Entity getTarget() {
        return target;
    }

    /* package-private */ void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return type.getUniqueType();
    }
}
