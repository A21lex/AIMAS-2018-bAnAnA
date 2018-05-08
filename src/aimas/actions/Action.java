package aimas.actions;

import aimas.Node;

/**
 * Action
 */
public abstract class Action {
    ActionType actionType;
    public ActionType getType(){
        return actionType;
    }
    public abstract int heuristic(Node node);
    public abstract boolean isAchieved(Node node);
    // From Warm-Up assignment
    static int manhDist(int i1, int j1, int i2, int j2){ // between cells of a map, not between state nodes
        int diffI = Math.abs(i1 - i2);
        int diffJ = Math.abs(j1 - j2);
        return diffI + diffJ;
    }
}
