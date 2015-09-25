/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.search;

/**
 * A* Node
 *
 * @author AlmasB (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public class AStarNode {

    private AStarNode parent;
    private int x, y;
    private int gCost, hCost;
    private int nodeValue;

    public AStarNode(int x, int y, int hCost, int value) {
        this.x = x;
        this.y = y;
        this.hCost = hCost;
        this.nodeValue = value;
    }

    public void setParent(AStarNode parent) {
        this.parent = parent;
    }

    public AStarNode getParent() {
        return parent;
    }

    public void setHCost(int hCost) {
        this.hCost = hCost;
    }

    public int getHCost() {
        return hCost;
    }

    /*package-private*/ void setGCost(int gCost) {
        this.gCost = gCost;
    }

    public int getGCost() {
        return gCost;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setNodeValue(int nodeValue) {
        this.nodeValue = nodeValue;
    }

    public int getNodeValue() {
        return nodeValue;
    }

    public int getFCost() {
        return gCost + hCost;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")" + " G:" + gCost + " H:" + hCost + "\n"
                + " NodeValue:" + nodeValue + " Parent:" + (parent == null ? "null" : "(" + parent.x + "," + parent.y + ")");
    }
}
