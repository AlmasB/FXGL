/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * Created at 4:35:29 AM Jul 15, 2010
 */
package org.jbox2d.callbacks;

import com.almasb.gameutils.math.Vec2;
import javafx.scene.paint.Color;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.particle.ParticleColor;

/**
 * Implement this abstract class to allow JBox2d to automatically draw your physics for debugging
 * purposes. Not intended to replace your own custom rendering routines!
 *
 * @author Daniel Murphy
 */
public abstract class DebugDraw {

    /** Draw shapes */
    public static final int e_shapeBit = 1 << 1;
    /** Draw joint connections */
    public static final int e_jointBit = 1 << 2;
    /** Draw axis aligned bounding boxes */
    public static final int e_aabbBit = 1 << 3;
    /** Draw pairs of connected objects */
    public static final int e_pairBit = 1 << 4;
    /** Draw center of mass frame */
    public static final int e_centerOfMassBit = 1 << 5;
    /** Draw dynamic tree */
    public static final int e_dynamicTreeBit = 1 << 6;
    /** Draw only the wireframe for drawing performance */
    public static final int e_wireframeDrawingBit = 1 << 7;


    protected int m_drawFlags;
    protected IViewportTransform viewportTransform;

    public DebugDraw() {
        this(null);
    }

    public DebugDraw(IViewportTransform viewport) {
        m_drawFlags = 0;
        viewportTransform = viewport;
    }

    public void setViewportTransform(IViewportTransform viewportTransform) {
        this.viewportTransform = viewportTransform;
    }

    public void setFlags(int flags) {
        m_drawFlags = flags;
    }

    public int getFlags() {
        return m_drawFlags;
    }

    public void appendFlags(int flags) {
        m_drawFlags |= flags;
    }

    public void clearFlags(int flags) {
        m_drawFlags &= ~flags;
    }

    /**
     * Draw a closed polygon provided in CCW order. This implementation uses
     * {@link #drawSegment(Vec2, Vec2, Color)} to draw each side of the polygon.
     *
     * @param vertices
     * @param vertexCount
     * @param color
     */
    public void drawPolygon(Vec2[] vertices, int vertexCount, Color color) {
        if (vertexCount == 1) {
            drawSegment(vertices[0], vertices[0], color);
            return;
        }

        for (int i = 0; i < vertexCount - 1; i += 1) {
            drawSegment(vertices[i], vertices[i + 1], color);
        }

        if (vertexCount > 2) {
            drawSegment(vertices[vertexCount - 1], vertices[0], color);
        }
    }

    public abstract void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color argColor);

    /**
     * Draw a solid closed polygon provided in CCW order.
     *
     * @param vertices
     * @param vertexCount
     * @param color
     */
    public abstract void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color color);

    /**
     * Draw a circle.
     *
     * @param center
     * @param radius
     * @param color
     */
    public abstract void drawCircle(Vec2 center, float radius, Color color);

    /** Draws a circle with an axis */
    public void drawCircle(Vec2 center, float radius, Vec2 axis, Color color) {
        drawCircle(center, radius, color);
    }

    /**
     * Draw a solid circle.
     *
     * @param center
     * @param radius
     * @param axis
     * @param color
     */
    public abstract void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color color);

    /**
     * Draw a line segment.
     *
     * @param p1
     * @param p2
     * @param color
     */
    public abstract void drawSegment(Vec2 p1, Vec2 p2, Color color);

    /**
     * Draw a transform. Choose your own length scale
     *
     * @param xf
     */
    public abstract void drawTransform(Transform xf);

    /**
     * Draw a string.
     *
     * @param x
     * @param y
     * @param s
     * @param color
     */
    public abstract void drawString(float x, float y, String s, Color color);

    /**
     * Draw a particle array
     *
     * @param colors can be null
     */
    public abstract void drawParticles(Vec2[] centers, float radius, ParticleColor[] colors, int count);

    /**
     * Draw a particle array
     *
     * @param colors can be null
     */
    public abstract void drawParticlesWireframe(Vec2[] centers, float radius, ParticleColor[] colors,
                                                int count);

    /** Called at the end of drawing a world */
    public void flush() {
    }

    public void drawString(Vec2 pos, String s, Color color) {
        drawString(pos.x, pos.y, s, color);
    }

    public IViewportTransform getViewportTranform() {
        return viewportTransform;
    }

    /**
     * @param x
     * @param y
     * @param scale
     * @deprecated use the viewport transform in {@link #getViewportTranform()}
     */
    public void setCamera(float x, float y, float scale) {
        viewportTransform.setCamera(x, y, scale);
    }


    /**
     * @param argScreen
     * @param argWorld
     */
    public void getScreenToWorldToOut(Vec2 argScreen, Vec2 argWorld) {
        viewportTransform.getScreenToWorld(argScreen, argWorld);
    }

    /**
     * @param argWorld
     * @param argScreen
     */
    public void getWorldToScreenToOut(Vec2 argWorld, Vec2 argScreen) {
        viewportTransform.getWorldToScreen(argWorld, argScreen);
    }

    /**
     * Takes the world coordinates and puts the corresponding screen coordinates in argScreen.
     *
     * @param worldX
     * @param worldY
     * @param argScreen
     */
    public void getWorldToScreenToOut(float worldX, float worldY, Vec2 argScreen) {
        argScreen.set(worldX, worldY);
        viewportTransform.getWorldToScreen(argScreen, argScreen);
    }

    /**
     * takes the world coordinate (argWorld) and returns the screen coordinates.
     *
     * @param argWorld
     */
    public Vec2 getWorldToScreen(Vec2 argWorld) {
        Vec2 screen = new Vec2();
        viewportTransform.getWorldToScreen(argWorld, screen);
        return screen;
    }

    /**
     * Takes the world coordinates and returns the screen coordinates.
     *
     * @param worldX
     * @param worldY
     */
    public Vec2 getWorldToScreen(float worldX, float worldY) {
        Vec2 argScreen = new Vec2(worldX, worldY);
        viewportTransform.getWorldToScreen(argScreen, argScreen);
        return argScreen;
    }

    /**
     * takes the screen coordinates and puts the corresponding world coordinates in argWorld.
     *
     * @param screenX
     * @param screenY
     * @param argWorld
     */
    public void getScreenToWorldToOut(float screenX, float screenY, Vec2 argWorld) {
        argWorld.set(screenX, screenY);
        viewportTransform.getScreenToWorld(argWorld, argWorld);
    }

    /**
     * takes the screen coordinates (argScreen) and returns the world coordinates
     *
     * @param argScreen
     */
    public Vec2 getScreenToWorld(Vec2 argScreen) {
        Vec2 world = new Vec2();
        viewportTransform.getScreenToWorld(argScreen, world);
        return world;
    }

    /**
     * takes the screen coordinates and returns the world coordinates.
     *
     * @param screenX
     * @param screenY
     */
    public Vec2 getScreenToWorld(float screenX, float screenY) {
        Vec2 screen = new Vec2(screenX, screenY);
        viewportTransform.getScreenToWorld(screen, screen);
        return screen;
    }
}
