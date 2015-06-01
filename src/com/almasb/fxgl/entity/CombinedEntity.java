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
