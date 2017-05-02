/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
