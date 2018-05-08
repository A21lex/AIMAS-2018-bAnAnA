package aimas.actions;

import aimas.Cell;
import aimas.Node;

public class ClearPathToGoalAction extends Action {
    private Cell goalCell;

    public ClearPathToGoalAction(Cell goalCell){
        this.goalCell = goalCell;
        this.actionType = ActionType.FREE_PATH_TO_GOAL;
    }

    @Override
    public ActionType getType() {
        return this.actionType;
    }

    @Override
    public int heuristic(Node node) {
        return 0;
    }

    @Override
    public boolean isAchieved(Node node) {
        return false;
    }
}
