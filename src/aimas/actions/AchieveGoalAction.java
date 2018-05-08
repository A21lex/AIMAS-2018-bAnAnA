package aimas.actions;

import aimas.Cell;
import aimas.Node;
import aimas.entities.Box;

import java.util.List;

public class AchieveGoalAction extends Action {
    Cell goalCell; // this is what we want to achieve
    Box box; // .. with this box (determined elsewhere)
    List<Action> subActions; // Actions which will lead to achieving this goal
    // something like: ClearPathToBox -> GetToBox -> MoveBox -> ClearPathToGoal -> GetBoxToGoal

    public AchieveGoalAction(Cell goalCell, Box box){
        this.goalCell = goalCell;
        this.box = box;
        this.actionType = ActionType.ACHIEVE_GOAL;
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
