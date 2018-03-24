/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.reflect.Annotation;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.entity.animation.AnimationBuilder;
import com.almasb.fxgl.parser.tiled.Layer;
import com.almasb.fxgl.parser.tiled.TiledMap;
import com.almasb.fxgl.parser.tiled.Tileset;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.util.Named;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;

import static com.almasb.fxgl.util.BackportKt.forEach;

/**
 * Helper class with static convenience methods.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Entities {

    private static final Logger log = Logger.get(Entities.class);

    private Entities() {}

    /**
     * Create an entity with bounding box around the screen with given thickness.
     *
     * @param thickness thickness of hit boxes around the screen
     * @return entity with screen bounds
     */
    public static Entity makeScreenBounds(double thickness) {
        double w = FXGL.getSettings().getWidth();
        double h = FXGL.getSettings().getHeight();

        Entity bounds = new Entity();

        bounds.getBoundingBoxComponent().addHitBox(new HitBox("LEFT",  new Point2D(-thickness, 0), BoundingShape.box(thickness, h)));
        bounds.getBoundingBoxComponent().addHitBox(new HitBox("RIGHT", new Point2D(w, 0), BoundingShape.box(thickness, h)));
        bounds.getBoundingBoxComponent().addHitBox(new HitBox("TOP",   new Point2D(0, -thickness), BoundingShape.box(w, thickness)));
        bounds.getBoundingBoxComponent().addHitBox(new HitBox("BOT",   new Point2D(0, h), BoundingShape.box(w, thickness)));

        bounds.addComponent(new PhysicsComponent());

        return bounds;
    }

    /**
     * @return new entity builder
     */
    public static EntityBuilder builder() {
        return new EntityBuilder();
    }

    /**
     * @return new entity builder
     */
    public static EntityBuilder builder(Class<? extends Enum> types) {
        return new EntityBuilder(types);
    }

    /**
     * @return new animation builder
     */
    public static AnimationBuilder animationBuilder() {
        return new AnimationBuilder();
    }

    /**
     * Provides fluent API for building entities.
     */
    public static class EntityBuilder {
        private Entity entity = new Entity();

        private Class<? extends Enum> types = null;

        private EntityBuilder() {
        }

        private EntityBuilder(Class<? extends Enum> types) {
            this.types = types;
        }

        public EntityBuilder from(SpawnData data) {
            at(data.getX(), data.getY());

            if (types != null) {
                try {
                    String type = data.get("type").toString().toUpperCase();

                    boolean found = false;

                    for (Object t : types.getEnumConstants()) {
                        if (t.toString().toUpperCase().equals(type)) {
                            type((Enum<?>) t);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        log.warningf("[%s] type was not found in enum %s. Spawning without type", type, types);
                    }

                } catch (IllegalArgumentException e) {
                    // ignore if type key not found
                }
            }

            forEach(data.getData(), entry -> entity.setProperty(entry.key, entry.value));
            return this;
        }

        public EntityBuilder type(Enum<?> type) {
            entity.setType(type);
            return this;
        }

        public EntityBuilder at(double x, double y) {
            entity.setPosition(x, y);
            return this;
        }

        public EntityBuilder at(Point2D position) {
            return at(position.getX(), position.getY());
        }

        public EntityBuilder at(Vec2 position) {
            return at(position.x, position.y);
        }

        public EntityBuilder rotate(double angle) {
            entity.setRotation(angle);
            return this;
        }

        public EntityBuilder bbox(HitBox box) {
            entity.getBoundingBoxComponent().addHitBox(box);
            return this;
        }

        public EntityBuilder viewFromNode(Node view) {
            entity.getViewComponent().setView(view);
            return this;
        }

        public EntityBuilder viewFromNodeWithBBox(Node view) {
            entity.getViewComponent().setView(view, true);
            return this;
        }

        public EntityBuilder viewFromTexture(String textureName) {
            entity.getViewComponent().setTexture(textureName);
            return this;
        }

        public EntityBuilder viewFromTextureWithBBox(String textureName) {
            entity.getViewComponent().setTexture(textureName, true);
            return this;
        }

        public EntityBuilder renderLayer(RenderLayer layer) {
            entity.getViewComponent().setRenderLayer(layer);
            return this;
        }

        /**
         * Generates view from tiles with {@link RenderLayer#DEFAULT}.
         *
         * @param map parsed Tiled map
         * @param layerName layer name as specified by Tiled
         * @return builder
         */
        public EntityBuilder viewFromTiles(TiledMap map, String layerName) {
            return viewFromTiles(map, layerName, RenderLayer.DEFAULT);
        }

        /**
         * Generates view from tiles.
         *
         * @param map parsed Tiled map
         * @param layerName layer name as specified by Tiled
         * @param renderLayer created view will use this render layer
         * @return builder
         */
        public EntityBuilder viewFromTiles(TiledMap map, String layerName, RenderLayer renderLayer) {
            entity.getViewComponent().setView(tilesToView(map, layerName), false);
            entity.getViewComponent().setRenderLayer(renderLayer);

            // https://github.com/AlmasB/FXGL/issues/474
            //entity.xProperty().bind(FXGL.getApp().getGameScene().getViewport().xProperty());
            //entity.yProperty().bind(FXGL.getApp().getGameScene().getViewport().yProperty());
            return this;
        }

        private Node tilesToView(TiledMap map, String layerName) {
            Layer layer = map.getLayerByName(layerName);

            // https://github.com/AlmasB/FXGL/issues/474

            //return new TiledLayerView(map, layer);

            WritableImage buffer = new WritableImage(
                    layer.getWidth() * map.getTilewidth(),
                    layer.getHeight() * map.getTileheight()
            );

            for (int i = 0; i < layer.getData().size(); i++) {

                int gid = layer.getData().get(i);

                // empty tile
                if (gid == 0)
                    continue;

                Tileset tileset = findTileset(gid, map.getTilesets());

                // we offset because data is encoded as continuous
                gid -= tileset.getFirstgid();

                // image source
                int tilex = gid % tileset.getColumns();
                int tiley = gid / tileset.getColumns();

                // image destination
                int x = i % layer.getWidth();
                int y = i / layer.getWidth();

                int w = map.getTilewidth();
                int h = map.getTileheight();

                Image sourceImage = loadTilesetImage(tileset);

                buffer.getPixelWriter().setPixels(x * w, y * h,
                        w, h, sourceImage.getPixelReader(),
                        tilex * w + tileset.getMargin() + tilex * tileset.getSpacing(),
                        tiley * h + tileset.getMargin() + tiley * tileset.getSpacing());
            }

            return new ImageView(buffer);
        }

        public EntityBuilder with(Component... components) {
            for (Component c : components)
                entity.addComponent(c);
            return this;
        }

        public EntityBuilder with(Control... controls) {
            for (Control c : controls)
                entity.addControl(c);
            return this;
        }

        @SafeVarargs
        public final EntityBuilder with(Class<? extends Control>... controlClasses) {
            for (Class<? extends Control> controlClass : controlClasses) {

                try {
                    for (Constructor<?> ctor : controlClass.getDeclaredConstructors()) {

                        Control c;

                        if (ctor.getParameterCount() == 0) {
                            // no-arg ctor
                            c = (Control) ctor.newInstance();
                        } else {

                            Object[] args = new Object[ctor.getParameterCount()];
                            int i = 0;

                            for (Parameter param : ctor.getParameters()) {
                                Named a = param.getAnnotation(Named.class);

                                Object value = entity.getProperty(a.value());

                                args[i++] = value;
                            }

                            c = (Control) ctor.newInstance(args);
                        }

                        entity.addControl(c);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Couldn't create instance of " + controlClass, e);
                }
            }

            return this;
        }

        /**
         * Add a property to entity being built.
         */
        public EntityBuilder with(String propertyKey, Object propertyValue) {
            entity.setProperty(propertyKey, propertyValue);
            return this;
        }

        /**
         * Finishes building entity.
         *
         * @return entity
         */
        public Entity build() {
            return entity;
        }

        /**
         * Finishes building the entity and attaches it to default game world.
         *
         * @return entity
         */
        public Entity buildAndAttach() {
            return buildAndAttach(FXGL.getApp().getGameWorld());
        }

        /**
         * Finishes building the entity and attaches it to given world.
         *
         * @param world the world to attach entity to
         * @return entity
         */
        public Entity buildAndAttach(GameWorld world) {
            world.addEntity(entity);
            return entity;
        }
    }

    /**
     * Finds tileset where gid is located.
     *
     * @param gid tile id
     * @param tilesets all tilesets
     * @return tileset
     */
    private static Tileset findTileset(int gid, List<Tileset> tilesets) {
        return tilesets.stream()
                .filter(tileset ->
                        gid >= tileset.getFirstgid() && gid < tileset.getFirstgid() + tileset.getTilecount())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Tileset for gid=" + gid + " not found"));
    }

    private static Image loadTilesetImage(Tileset tileset) {
        String imageName = tileset.getImage();
        imageName = imageName.substring(imageName.lastIndexOf("/") + 1);

        return tileset.getTransparentcolor().isEmpty()
                ? FXGL.getAssetLoader().loadTexture(imageName).getImage()
                : FXGL.getAssetLoader().loadTexture(imageName,
                Color.web(tileset.getTransparentcolor())).getImage();
    }
}
