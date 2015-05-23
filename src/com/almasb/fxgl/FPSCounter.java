package com.almasb.fxgl;

import java.util.Arrays;

/*package-private*/ class FPSCounter {

    private static final int MAX_SAMPLES = 100;

    private final float[] values = new float[MAX_SAMPLES];
    private float sum = 0.0f;
    private int index = 0;

    public FPSCounter() {
        Arrays.fill(values, 0.0f);
    }

    public float count(float timeTookLastFrame) {
        sum -= values[index];
        sum += timeTookLastFrame;
        values[index] = timeTookLastFrame;
        if (++index == values.length)
            index = 0;

        return sum / values.length;
    }
}
