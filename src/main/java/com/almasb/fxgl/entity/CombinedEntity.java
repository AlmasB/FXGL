/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.entity;

/**
 * An entity that is created by combining several entities
 *
 * Use this when the entity is represented by non regular graphics
 * or when "hit-box" collision detection is required
 *
 * Example:
 * <pre>
 *       Entity playerHead = new Entity(Type.PLAYER_HEAD);
 *       playerHead.setPosition(0, 0)
 *                  .setUsePhysics(true)
 *                  .setGraphics(new Rectangle(10, 40));
 *
 *       Entity playerBody = new Entity(Type.PLAYER_BODY);
 *       playerBody.setPosition(10, 40)
 *                  .setUsePhysics(true)
 *                  .setGraphics(new Rectangle(40, 40));
 *
 *       CombinedEntity player = new CombinedEntity(Type.PLAYER);
 *       player.attach(playerHead);
 *       player.attach(playerBody);
 *
 *       // note only the combined entity needs to be added
 *       addEntities(player);
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public class CombinedEntity extends Entity {

    public CombinedEntity(EntityType type) {
        super(type);
        getChildren().clear();
    }

    public void attach(Entity e) {
        getChildren().add(e);
        e.translateXProperty().bind(translateXProperty().add(e.getTranslateX()));
        e.translateYProperty().bind(translateYProperty().add(e.getTranslateY()));
    }
}
