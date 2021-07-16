/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.entity.Entity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Hand {

    private String name;

    private ObservableList<Entity> cards = FXCollections.observableArrayList();

    private Runnable changeCallback = EmptyRunnable.INSTANCE;

    public Hand(String name) {
        this.name = name;
    }

    public void setChangeCallback(Runnable changeCallback) {
        this.changeCallback = changeCallback;
    }

    public ObservableList<Entity> cardsProperty() {
        return cards;
    }

    public void addCard(Entity card) {
        cards.add(card);

        changeCallback.run();
    }

    public void removeCard(Entity card) {
        cards.remove(card);

        changeCallback.run();
    }

    @Override
    public String toString() {
        return "Hand{" +
                "name='" + name + '\'' +
                '}';
    }
}
