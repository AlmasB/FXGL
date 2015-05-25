package com.almasb.fxgl.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.Callable;

/**
 * A* search logic
 *
 * @author AlmasB (almaslvl@gmail.com)
 * @version 1.0
 */
public class AStarLogic /*implements Callable<List<AStarNode> >*/ {

    // TODO: make easily calculable on BG thread
//    private AStarNode[][] mGrid;
//
//
//    public AStarLogic() {
//
//    }
//
//    public AStarLogic(AStarNode[][] grid) {
//
//    }
//
//    @Override
//    public List<AStarNode> call() throws Exception {
//
//
//        return null;
//    }

    /**
     * Since the equality check is based on references
     * start and target must be elements of the array
     *
     * @param grid
     * @param start
     * @param target
     * @param busyNodes
     * @return
     */
    public List<AStarNode> getPath(AStarNode[][] grid, AStarNode start, AStarNode target, AStarNode... busyNodes) {
        if (target.getNodeValue() == 1) // the target is an unwalkable node
            return new ArrayList<AStarNode>();  // return empty path

        List<AStarNode> open = new ArrayList<AStarNode>();
        List<AStarNode> closed = new ArrayList<AStarNode>();
        List<AStarNode> path = new ArrayList<AStarNode>();

        AStarNode current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            AStarNode[] temp = getNeighbors(current, grid, busyNodes);

            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == target) {
                    target.setParent(current);
                    found = true;
                    closed.add(target);
                    break;
                }

                if (!closed.contains(temp[i])) {
                    if (open.contains(temp[i])) {
                        int newG = current.getGCost() + 10;

                        if (newG < temp[i].getGCost()) {
                            temp[i].setParent(current);
                            temp[i].setGCost(newG);
                        }
                    }
                    else {
                        temp[i].setParent(current);
                        temp[i].setGCost(current.getGCost() + 10);
                        open.add(temp[i]);
                    }
                }
            }

            if (!found) {
                closed.add(current);
                open.remove(current);
                current = getSmallest(open);
                if (current == null)
                    return new ArrayList<AStarNode>();
            }
        }

        AStarNode tmp = target;
        path.add(tmp);
        while (!path.contains(start)) {
            tmp = tmp.getParent();
            if (tmp == start)
                break;
            path.add(tmp);
        }

        closed.clear();

        for (int i = path.size() - 1; i >= 0; i--)
            closed.add(path.get(i));

        return closed;
    }

    //TODO: make it pretty
    /*public List<AStarNode> getPath(AStarNode[][] grid, AStarNode start, AStarNode target) {
        if (target.getNodeValue() == 1) // the target is an unwalkable node
            return new ArrayList<AStarNode>();  // return empty path

        List<AStarNode> open = new ArrayList<AStarNode>();
        List<AStarNode> closed = new ArrayList<AStarNode>();
        List<AStarNode> path = new ArrayList<AStarNode>();

        AStarNode current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            AStarNode[] temp = getNeighbors(current, grid);

            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == target) {
                    target.setParent(current);
                    found = true;
                    closed.add(target);
                    break;
                }

                if (!closed.contains(temp[i])) {
                    if (open.contains(temp[i])) {
                        int newG = current.getGCost() + 10;

                        if (newG < temp[i].getGCost()) {
                            temp[i].setParent(current);
                            temp[i].setGCost(newG);
                        }
                    }
                    else {
                        temp[i].setParent(current);
                        temp[i].setGCost(current.getGCost() + 10);
                        open.add(temp[i]);
                    }
                }
            }

            if (!found) {
                closed.add(current);
                open.remove(current);
                current = getSmallest(open);
                if (current == null)
                    return new ArrayList<AStarNode>();
            }
        }

        AStarNode tmp = target;
        path.add(tmp);
        while (!path.contains(start)) {
            tmp = tmp.getParent();
            if (tmp == start)
                break;
            path.add(tmp);
        }

        closed.clear();

        for (int i = path.size() - 1; i >= 0; i--)
            closed.add(path.get(i));

        return closed;
    }*/

    public AStarNode getSmallest(List<AStarNode> open) {
        if (open.size() == 0) {
            System.out.println("No path found. Returning null");
            return null;
        }

        AStarNode min = open.get(0);

        for (AStarNode n : open)
            if (n.getFCost() < min.getFCost())
                min = n;

        return min;
    }

    /*public AStarNode[] getNeighbors(AStarNode n, AStarNode[][] grid) {
        int x = n.getX();
        int y = n.getY();
        int x1 = x - 1;
        int x2 = x + 1;
        int y1 = y - 1;
        int y2 = y + 1;

        boolean b1 = x1 >= 0 && grid[x1][y].getNodeValue() != 1;
        boolean b2 = x2 < grid.length && grid[x2][y].getNodeValue() != 1;
        boolean b3 = y1 >= 0 && grid[x][y1].getNodeValue() != 1;
        boolean b4 = y2 < grid[0].length && grid[x][y2].getNodeValue() != 1;

        int count = 0;
        if (b1)
            count++;
        if (b2)
            count++;
        if (b3)
            count++;
        if (b4)
            count++;

        AStarNode[] res = new AStarNode[count];
        int i = 0;

        if (b1)
            res[i++] = grid[x1][y];

        if (b2)
            res[i++] = grid[x2][y];

        if (b3)
            res[i++] = grid[x][y1];

        if (b4)
            res[i++] = grid[x][y2];

        return res;
    }*/

    public AStarNode[] getNeighbors(AStarNode n, AStarNode[][] grid, AStarNode... busyNodes) {
        int x = n.getX();
        int y = n.getY();
        int x1 = x - 1;
        int x2 = x + 1;
        int y1 = y - 1;
        int y2 = y + 1;

        boolean b1 = x1 >= 0 && grid[x1][y].getNodeValue() != 1 && !contains(x1, y, busyNodes);
        boolean b2 = x2 < grid.length && grid[x2][y].getNodeValue() != 1 && !contains(x2, y, busyNodes);
        boolean b3 = y1 >= 0 && grid[x][y1].getNodeValue() != 1 && !contains(x, y1, busyNodes);
        boolean b4 = y2 < grid[0].length && grid[x][y2].getNodeValue() != 1 && !contains(x, y2, busyNodes);

        int count = 0;
        if (b1)
            count++;
        if (b2)
            count++;
        if (b3)
            count++;
        if (b4)
            count++;

        AStarNode[] res = new AStarNode[count];
        int i = 0;

        if (b1)
            res[i++] = grid[x1][y];

        if (b2)
            res[i++] = grid[x2][y];

        if (b3)
            res[i++] = grid[x][y1];

        if (b4)
            res[i++] = grid[x][y2];

        return res;
    }

    private boolean contains(int x, int y, AStarNode... nodes) {
        for (AStarNode n : nodes)
            if (n.getX() == x && n.getY() == y)
                return true;

        return false;
    }
}
