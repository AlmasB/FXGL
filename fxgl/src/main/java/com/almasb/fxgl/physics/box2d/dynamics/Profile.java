/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.physics.box2d.common.JBoxUtils;

import java.util.List;

public class Profile {
    private static final int LONG_AVG_NUMS = 20;
    private static final float LONG_FRACTION = 1f / LONG_AVG_NUMS;
    private static final int SHORT_AVG_NUMS = 5;
    private static final float SHORT_FRACTION = 1f / SHORT_AVG_NUMS;

    public static class ProfileEntry {
        float longAvg;
        float shortAvg;
        float min;
        float max;
        float accum;

        public ProfileEntry() {
            min = Float.MAX_VALUE;
            max = -Float.MAX_VALUE;
        }

        public void record(float value) {
            longAvg = longAvg * (1 - LONG_FRACTION) + value * LONG_FRACTION;
            shortAvg = shortAvg * (1 - SHORT_FRACTION) + value * SHORT_FRACTION;
            min = JBoxUtils.min(value, min);
            max = JBoxUtils.max(value, max);
        }

        public void startAccum() {
            accum = 0;
        }

        public void accum(float value) {
            accum += value;
        }

        public void endAccum() {
            record(accum);
        }

        @Override
        public String toString() {
            return String.format("%.2f (%.2f) [%.2f,%.2f]", shortAvg, longAvg, min, max);
        }
    }

    public final ProfileEntry step = new ProfileEntry();
    public final ProfileEntry stepInit = new ProfileEntry();
    public final ProfileEntry collide = new ProfileEntry();
    public final ProfileEntry solveParticleSystem = new ProfileEntry();
    public final ProfileEntry solve = new ProfileEntry();
    public final ProfileEntry solveInit = new ProfileEntry();
    public final ProfileEntry solveVelocity = new ProfileEntry();
    public final ProfileEntry solvePosition = new ProfileEntry();
    public final ProfileEntry broadphase = new ProfileEntry();
    public final ProfileEntry solveTOI = new ProfileEntry();

    public void toDebugStrings(List<String> strings) {
        strings.add("Profile:");
        strings.add(" step: " + step);
        strings.add("  init: " + stepInit);
        strings.add("  collide: " + collide);
        strings.add("  particles: " + solveParticleSystem);
        strings.add("  solve: " + solve);
        strings.add("   solveInit: " + solveInit);
        strings.add("   solveVelocity: " + solveVelocity);
        strings.add("   solvePosition: " + solvePosition);
        strings.add("   broadphase: " + broadphase);
        strings.add("  solveTOI: " + solveTOI);
    }
}
