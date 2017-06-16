/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.app.FXGL;
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
        return getObject().getPosition().distance(400, 300) < 25;
    }

    @Override
    public void action() {

        double speed = 0.016 * 60 * 5;

        Point2D vector = new Point2D(400, 300).subtract(getObject().getPosition())
                .normalize()
                .multiply(speed);

        getObject().translate(vector);
    }
}
