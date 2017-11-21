/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.GoalAction;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveTask extends GoalAction {

    public MoveTask() {
        super("Move");
    }

    @Override
    public boolean reachedGoal() {
        return getEntity().getPosition().distance(400, 300) < 25;
    }

    @Override
    public void onUpdate(double tpf) {

        double speed = tpf * 60 * 5;

        Point2D vector = new Point2D(400, 300).subtract(getEntity().getPosition())
                .normalize()
                .multiply(speed);

        getEntity().translate(vector);
    }
}
