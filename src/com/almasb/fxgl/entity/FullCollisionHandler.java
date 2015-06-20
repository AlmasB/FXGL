package com.almasb.fxgl.entity;


public interface FullCollisionHandler extends CollisionHandler {
    public void onCollisionBegin(Entity a, Entity b);
    public void onCollisionEnd(Entity a, Entity b);
}
