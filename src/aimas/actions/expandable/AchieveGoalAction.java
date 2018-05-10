package aimas.actions.expandable;

import aimas.Cell;
import aimas.CoordinatesPair;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.actions.atomic.DeliverBoxSurelyAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.entities.Box;

import java.util.ArrayList;
import java.util.List;

public class AchieveGoalAction extends ExpandableAction {
    Cell goalCell; // this is what we want to achieve
    Box box; // .. with this box

    // something like: ClearPathToBox -> GetToBox -> MoveBox -> ClearPathToGoal -> GetBoxToGoal

    public AchieveGoalAction(Cell goalCell, Box box){
        this.goalCell = goalCell;
        this.box = box;
        this.actionType = ActionType.ACHIEVE_GOAL;
    }

    @Override
    public boolean isAchieved(Node node) {
        if (goalCell.getEntity() == null){
            return false;
        }
        if (goalCell.getEntity() instanceof Box) {
            Box boxOnCell = (Box) goalCell.getEntity();
            return goalCell.getGoalLetter() == Character.toLowerCase(boxOnCell.getLetter());
        }
        return false;
    }

    @Override
    public List<Action> decompose(Node node) {
        if (isAchieved(node)){
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        //Clear box - clear goal - gotoBox - deliverBox

        CoordinatesPair agentCellCoord = node.getAgentCellCoords().get(0); // just take the only agent for now
        Action clearBox = new ClearPathAction(agentCellCoord ,box.getCoordinates(node));
        Action clearGoal = new ClearPathAction(box.getCoordinates(node), goalCell.getCoordinates());
        Action gotobox = new MoveSurelyAction(box.getCoordinates(node));
        Action deliverbox = new DeliverBoxSurelyAction(box, goalCell.getCoordinates());
        List<Action> expandedActions = new ArrayList<>();
        expandedActions.add(clearBox);
        expandedActions.add(clearGoal);
        expandedActions.add(gotobox);
        expandedActions.add(deliverbox);
        return expandedActions;
    }
}
