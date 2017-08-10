/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.Action;
import com.almasb.fxgl.core.math.FXGLMath;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PatrolTask extends Action {

    private static final Point2D[] POINTS = new Point2D[] {
            new Point2D(300, 300),
            new Point2D(500, 500),
            new Point2D(450, 250)
    };

    private Point2D selectedPoint = POINTS[0];

    public PatrolTask() {
        super("Patrol");
    }

    @Override
    public void action() {
        getObject().translateTowards(selectedPoint, 1);

        if (getObject().getPosition().distance(selectedPoint) < 5) {
            selectedPoint = POINTS[FXGLMath.random(0, 2)];
        }
    }
}
