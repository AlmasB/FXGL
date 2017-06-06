/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.events;

import com.almasb.fxgl.entity.EntityEvent;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Events {

    public static final EventType<EntityEvent> PASSED = new EventType<>(EntityEvent.ANY, "PASSED_EVENT");
}
