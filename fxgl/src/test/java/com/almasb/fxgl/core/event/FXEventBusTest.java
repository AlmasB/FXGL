/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.event;

import com.almasb.fxgl.event.TestEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXEventBusTest {

    @Before
    public void localSetUp() {
        bus = new FXEventBus();
        calls = 0;
        threadID = Thread.currentThread().getId();
    }

    private EventBus bus;
    private int calls;
    private long threadID;

    @Test
    public void testFireEvent() {
        TestEvent e = new TestEvent(TestEvent.ANY, new Object());

        Subscriber subscriber = bus.addEventHandler(TestEvent.ANY, event -> {
            calls++;

            assertEquals("Handled event on a different thread", threadID, Thread.currentThread().getId());

            assertTrue("Received wrong event", e.getData() == event.getData()
                    && e.getEventType() == event.getEventType());
        });

        bus.fireEvent(e);

        // synchronous
        assertEquals(calls, 1);

        subscriber.unsubscribe();

        bus.fireEvent(e);

        assertEquals(calls, 1);
    }
}
