/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import javafx.scene.paint.Color;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Card {

    public enum Suit {
        RED(Color.RED), ORANGE(Color.ORANGE), GREEN(Color.GREEN), BLUE(Color.BLUE);

        final Color color;

        Suit(Color color) {
            this.color = color;
        }
    }

    public enum Rank {
        ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"),
        SP_PLUS2("+2"), SP_PLUS4("+4"), SP_SKIP("X");

        final String name;

        Rank(String name) {
            this.name = name;
        }
    }

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean canUseOn(Card other) {
        return other.rank.equals(rank) || other.suit.equals(suit);
    }

    @Override
    public String toString() {
        return rank.name;
    }
}
