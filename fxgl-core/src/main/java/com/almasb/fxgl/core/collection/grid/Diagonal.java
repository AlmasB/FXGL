package com.almasb.fxgl.core.collection.grid;

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
