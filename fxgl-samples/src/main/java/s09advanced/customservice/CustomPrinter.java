/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced.customservice;

import com.google.inject.Singleton;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
public class CustomPrinter implements Printer {
    @Override
    public void print(String message) {
        System.out.println("Printer says: "+ message);
    }
}
