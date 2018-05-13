package aimas.actions;

import aimas.Node;

import java.util.List;

/**
 * Action - non-atomic
 */
public abstract class Action {
    protected ActionType actionType;
    protected Action parent; // each action's parent is the action which generated that action;
    // null if it itself is the parent
    public ActionType getType(){
        return actionType;
    }
    //public abstract int heuristic(Node node);
    public abstract boolean isAchieved(Node node);

    public Action getParent(){
        return this.parent;
    }
    public void setParent(Action parent){
        this.parent = parent;
    }

    // From Warm-Up assignment
    protected static int manhDist(int i1, int j1, int i2, int j2){ // between cells of a map, not between state nodes
        int diffI = Math.abs(i1 - i2);
        int diffJ = Math.abs(j1 - j2);
        return diffI + diffJ;
    }
}
