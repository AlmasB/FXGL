/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.ecs.AbstractComponent;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.box2d.particle.ParticleGroupDef;
import javafx.scene.paint.Color;

/**
 * Adds physics particle properties to an entity.
 * By setting the definition each property can be fine-tuned.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
@Required(BoundingBoxComponent.class)
public class PhysicsParticleComponent extends AbstractComponent {
    private ParticleGroupDef definition = new ParticleGroupDef();
    private Color color = Color.BLACK;

    /**
     * Set particle group definition.
     *
     * @param definition particle definition
     */
    public void setDefinition(ParticleGroupDef definition) {
        this.definition = definition;
    }

    /**
     * @return definition
     */
    public ParticleGroupDef getDefinition() {
        return definition;
    }

    /**
     * Set particle color.
     *
     * @param color particle color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return particle color
     */
    public Color getColor() {
        return color;
    }
}
