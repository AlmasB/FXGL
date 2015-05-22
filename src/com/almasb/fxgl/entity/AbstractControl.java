package com.almasb.fxgl.entity;

public abstract class AbstractControl implements Control {

    protected Entity entity;

    /* package-private */ void setEntity(Entity entity) {
        this.entity = entity;
        initEntity(entity);
    }

    protected abstract void initEntity(Entity entity);
}
