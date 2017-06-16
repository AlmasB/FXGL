/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Deck {

    boolean hasCards();

    Card drawCard();
}
