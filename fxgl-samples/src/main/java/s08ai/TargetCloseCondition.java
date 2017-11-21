/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.Condition;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TargetCloseCondition extends Condition {

    @Override
    public boolean evaluate() {

        return new Point2D(400, 300).distance(getEntity().getPosition()) < 400;
    }
}
