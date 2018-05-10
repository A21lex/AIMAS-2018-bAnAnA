package aimas.actions.expandable;

import aimas.CoordinatesPair;
import aimas.Node;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.entities.Box;

import java.util.ArrayList;

/**
 * Simple action ClearPathAction
 */
public class ClearPathAction extends ExpandableAction {

    CoordinatesPair start; // from this cell
    CoordinatesPair finish; // to this cell

    public ClearPathAction(CoordinatesPair start, CoordinatesPair finish){
        this.start = start;
        this.finish = finish;
        this.actionType = ActionType.CLEAR_PATH;
    }

    @Override
    public boolean isAchieved(Node node) {
        return PathFinder.pathExists(node.getLevel(), start, finish,
                true, true, true);
    }

    @Override
    public ArrayList<Action> decompose(Node node){
        if (isAchieved(node)){
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        // List of boxes on path from start to  finish
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Artur will implement this<<<<<<<
        ArrayList<Box> boxes = PathFinder.getBoxesOnPath(node.getLevel(), start, finish,
                true, true, true);

        ArrayList<Action> expandedActions = new ArrayList<>();
        for (Box box : boxes){
            expandedActions.add(new RemoveBoxAction(box, start, finish));
        }
        // for every box in list of boxed: add remove(box) to expandedActions
        // will return corresponding actions or empty list if there are no boxes on path (although
        // in this case this method shouldn't be called at all)
        return expandedActions;
    }
}
