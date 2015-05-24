package com.almasb.fxgl.entity;

public interface EntityType {
    default public String getUniqueType() {
        return getClass().getCanonicalName() + "." + toString();
    }
}
