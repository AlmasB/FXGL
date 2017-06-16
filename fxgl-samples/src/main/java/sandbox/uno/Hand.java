/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.entity.GameEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Hand {

    private ObservableList<GameEntity> cards = FXCollections.observableArrayList();

    public ObservableList<GameEntity> cardsProperty() {
        return FXCollections.unmodifiableObservableList(cards);
    }

    public void addCard(GameEntity card) {
        cards.add(card);
    }

    public void removeCard(GameEntity card) {
        cards.remove(card);
    }
}
