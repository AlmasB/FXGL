/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.SingleAction;
import com.almasb.fxgl.core.math.FXGLMath;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PatrolTask extends SingleAction {

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
    public void onUpdate(double tpf) {
        getObject().translateTowards(selectedPoint, 60 * tpf);

        if (getObject().getPosition().distance(selectedPoint) < 5) {
            selectedPoint = FXGLMath.random(POINTS).get();
        }
    }
}
