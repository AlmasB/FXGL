/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tictactoe.control.enemy;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxglgames.tictactoe.TicTacToeApp;
import com.almasb.fxglgames.tictactoe.TileCombo;
import com.almasb.fxglgames.tictactoe.TileEntity;
import com.almasb.fxglgames.tictactoe.TileValue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * A decent AI but can be easily defeated by analyzing the pattern.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RuleBasedControl extends EnemyControl {

    private List<Predicate<TileCombo>> aiPredicates = Arrays.asList(
            c -> c.isTwoThirds(TileValue.O),
            c -> c.isTwoThirds(TileValue.X),
            c -> c.isOneThird(TileValue.O),
            c -> c.isOpen(),
            c -> c.getFirstEmpty() != null
    );

    @Override
    public void makeMove() {
        List<TileCombo> combos = FXGL.<TicTacToeApp>getAppCast().getCombos();

        TileEntity tile = aiPredicates.stream()
                .map(predicate -> {
                    return combos.stream()
                            .filter(predicate)
                            .findAny()
                            .map(TileCombo::getFirstEmpty)
                            .orElse(null);
                })
                .filter(t -> t != null)
                .findFirst()
                // should not happen
                .orElseThrow(() -> new IllegalStateException("No empty tiles"));

        tile.getControl().mark(TileValue.O);
    }
}
