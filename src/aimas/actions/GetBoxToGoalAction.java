package aimas.actions;

import aimas.Cell;
import aimas.entities.Box;
import aimas.Node;

public class GetBoxToGoalAction extends Action {
    Box box;
    Cell goalCell;

    public GetBoxToGoalAction(Box box, Cell goalCell){
        this.box = box;
        this.goalCell = goalCell;
        this.actionType = ActionType.GET_BOX_TO_GOAL;
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
