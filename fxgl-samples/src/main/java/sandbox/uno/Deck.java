/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.core.math.FXGLMath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Deck {

    boolean hasCards();

    Card drawCard();

    static Deck newInfiniteDeck() {
        return new Deck() {
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
        };
    }

    static Deck newStandardDeck() {
        return new Deck() {
            private List<Card> cards = new ArrayList<>();

            {
                for (var suit : Card.Suit.values()) {
                    for (var rank : Card.Rank.values()) {
                        cards.add(new Card(suit, rank));
                    }
                }

                Collections.shuffle(cards);
            }

            @Override
            public boolean hasCards() {
                return !cards.isEmpty();
            }

            @Override
            public Card drawCard() {
                return cards.remove(cards.size() - 1);
            }
        };
    }
}
