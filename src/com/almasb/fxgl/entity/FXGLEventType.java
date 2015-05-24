package com.almasb.fxgl.entity;

public interface FXGLEventType {
    default public String getUniqueType() {
        return getClass().getCanonicalName() + "." + toString();
    }
}
