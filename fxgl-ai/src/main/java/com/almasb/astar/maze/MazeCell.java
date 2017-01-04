package com.almasb.astar.maze;

/**
 * Represents a single cell in a maze.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MazeCell {
    private int x, y;
    private boolean topWall = false, leftWall = false;

    public MazeCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x coordinate of this cell in the grid
     */
    public int getX() {
        return x;
    }

    /**
     * @return y coordinate of this cell in the grid
     */
    public int getY() {
        return y;
    }

    /**
     * @param leftWall left wall for this cell
     */
    public void setLeftWall(boolean leftWall) {
        this.leftWall = leftWall;
    }

    /**
     * @param topWall top wall for this cell
     */
    public void setTopWall(boolean topWall) {
        this.topWall = topWall;
    }

    /**
     * @return if left wall is present
     */
    public boolean hasLeftWall() {
        return leftWall;
    }

    /**
     * @return if top wall is present
     */
    public boolean hasTopWall() {
        return topWall;
    }
}
