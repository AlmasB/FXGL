package com.almasb.fxgl.entity;

public class CollisionPair extends Pair<EntityType> {

    private CollisionHandler handler;

    public CollisionPair(EntityType a, EntityType b, CollisionHandler handler) {
        super(a, b);
        this.handler = handler;
    }

    public CollisionHandler getHandler() {
        return handler;
    }
}
