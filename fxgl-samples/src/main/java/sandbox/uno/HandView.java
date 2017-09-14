/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import javafx.scene.layout.HBox;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HandView extends HBox {

    private Hand hand;

    public HandView(Hand hand) {
        this.hand = hand;

//        hand.cardsProperty().addListener((ListChangeListener<? super Gam>) c -> {
//            while (c.next()) {
//                if (c.wasAdded()) {
//                    c.getAddedSubList().forEach(card -> {
//                        getChildren().add(new CardView(card));
//                    });
//                } else if (c.wasRemoved()) {
//                    c.getRemoved().forEach(card -> {
//
//                    });
//                }
//            }
//        });
    }
}
