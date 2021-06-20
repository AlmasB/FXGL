/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.core.math.FXGLMath;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class InfiniteDeck implements Deck {

    private Card.Suit[] suits = Card.Suit.values();
    private Card.Rank[] ranks = Card.Rank.values();

    @Override
    public boolean hasCards() {
        return true;
    }

    @Override
    public Card drawCard() {
        return new Card(suits[FXGLMath.random(0, suits.length-1)], ranks[FXGLMath.random(0, ranks.length-1)]);
    }
}
