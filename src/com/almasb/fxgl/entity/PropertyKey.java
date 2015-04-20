package com.almasb.fxgl.entity;

public interface PropertyKey {
    default public String getUniqueKey() {
        return getClass().getCanonicalName() + "." + toString();
    }
}
