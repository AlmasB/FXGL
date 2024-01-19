/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;


/**
 * Define Diagonal Movement mode
 *
 * @author Jean-Rene Lavoie (jeanrlavoie@gmail.com)
 */
public enum Diagonal {

    NEVER, ALLOWED;

    public boolean is(Diagonal... diagonalMovements) {
        for(Diagonal diagonalMovement : diagonalMovements) {
            if(diagonalMovement.equals(this)) {
                return true;
            }
        }
        return false;
    }

}
