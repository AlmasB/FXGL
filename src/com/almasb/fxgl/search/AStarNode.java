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
