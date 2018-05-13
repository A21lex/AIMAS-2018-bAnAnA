package aimas.actions.expandable;

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.aiutils.BoxAssigner;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolveLevelAction extends ExpandableAction {
    //potentially store boxes and cells to satisfy with these
    Node node;

    public SolveLevelAction(Node node){
        this.node = node;

        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.ACHIEVE_GOAL);
        this.canBeDecomposedTo = decomposedTo;
        this.parent = null; // top level node, so parent is null
        this.actionType = ActionType.SOLVE_LEVEL;
    }

    // Check if all the goal cells have on them a box of corresponding type; if not, return false
    @Override
    public boolean isAchieved(Node node) {
        for (CoordinatesPair coordinate : Node.getGoalCellCoords()){
            Cell cellAtCoordinate = node.getCellAtCoords(coordinate);
            if (cellAtCoordinate.getEntity() == null){
                return false;
            }
            Box boxAtCoordinate = (Box) cellAtCoordinate.getEntity();
            if (!(boxAtCoordinate.getLetter() == Character.toUpperCase(cellAtCoordinate.getGoalLetter()))){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Action> decompose(Node node) {

        if (isAchieved(node)) {
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        HashMap<Cell, Box> goalsBoxes = BoxAssigner.assignBoxesToGoals(node);
        /* Here we need to use GoalPrioritizer to make sure goal actions are added in the correct order */
        List<Action> expandedActions = new ArrayList<>();
        for (Cell goalCell : goalsBoxes.keySet()){
            expandedActions.add(new AchieveGoalAction(goalCell, goalsBoxes.get(goalCell), this));
        }
        return expandedActions;
    }
}
