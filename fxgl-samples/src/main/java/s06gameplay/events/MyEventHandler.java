/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.events;

import com.almasb.fxgl.event.Handles;
import com.almasb.fxgl.event.SetEventHandler;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEventHandler
public class MyEventHandler {

    @Handles(eventType = "LOW_HP")
    public void onLowHP(MyGameEvent event) {
        System.out.println("Handled: " + event);
    }
}
