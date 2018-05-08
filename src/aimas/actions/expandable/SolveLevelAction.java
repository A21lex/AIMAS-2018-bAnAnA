package aimas.actions.expandable;

import aimas.Cell;
import aimas.CoordinatesPair;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.entities.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolveLevelAction extends ExpandableAction {
    //potentially store boxes and cells to satisfy with these
    Node node;

    public SolveLevelAction(Node node){
        this.node = node;
        this.actionType = ActionType.SOLVE_LEVEL;
    }

    // Check if all the goal cells have on them a box of corresponding type; if not, return false
    @Override
    public boolean isAchieved(Node node) {
        for (CoordinatesPair coordinate : Node.getGoalCellCoords()){
            Cell cellAtCoordinate = node.getCellAtCoords(coordinate);
            Box boxAtCoordinate = (Box) cellAtCoordinate.getEntity();
            if (!(boxAtCoordinate.getLetter() == Character.toUpperCase(cellAtCoordinate.getGoalLetter()))){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Action> decompose(Node node) {
        List<CoordinatesPair> goalCoords = Node.getGoalCellCoords();
        List<Cell> goalCells = new ArrayList<>();
        for (CoordinatesPair goalCoord : goalCoords){
            goalCells.add(node.getCellAtCoords(goalCoord));
        }
        /* SOMEHOW GET THE BOXES WITH WHICH TO SATISFY EACH GOAL - maybe in a map? */
        List<Box> boxes;
        HashMap<Cell, Box> cellsBoxes = new HashMap<>();

        List<Action> expandedActions = new ArrayList<>();
        for (Cell goalCell : cellsBoxes.keySet()){
            expandedActions.add(new AchieveGoalAction(goalCell, cellsBoxes.get(goalCell)));
        }

        return expandedActions;
    }
}
