package aimas.actions.expandable;

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.actions.atomic.DeliverBoxSurelyAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.List;

public class AchieveGoalAction extends ExpandableAction {
    Cell goalCell; // this is what we want to achieve
    Box box; // .. with this box



    public AchieveGoalAction(Cell goalCell, Box box, Action parent){
        this.goalCell = goalCell;
        this.box = box;
        this.parent = parent;
        this.childrenActions = new ArrayList<>();

        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.CLEAR_PATH);
        decomposedTo.add(ActionType.CLEAR_PATH);
        decomposedTo.add(ActionType.MOVE_SURELY);
        decomposedTo.add(ActionType.DELIVER_BOX_SURELY);
        this.canBeDecomposedTo = decomposedTo;
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
        Action clearBox = new ClearPathAction(agentCellCoord ,box.getCoordinates(node), node, this);
        Action clearGoal = new ClearPathAction(box.getCoordinates(node), goalCell.getCoordinates(), node, this);
        Action gotobox = new MoveSurelyAction(box.getCoordinates(node), this);
        Action deliverbox = new DeliverBoxSurelyAction(box, goalCell.getCoordinates(), this);
        List<Action> expandedActions = new ArrayList<>();

        // manually (not to traverse the tree for this purpose specifically)
        clearBox.setNumberAsChild(0);
        clearGoal.setNumberAsChild(1);
        gotobox.setNumberAsChild(2);
        deliverbox.setNumberAsChild(3);

        expandedActions.add(clearBox);
        expandedActions.add(clearGoal);
        expandedActions.add(gotobox);
        expandedActions.add(deliverbox);

        childrenActions = expandedActions;

        return expandedActions;
    }
    @Override
    public String toString() {
        return "AchieveGoalAction: achieving goal " + goalCell.toStringPrime() + " with box " + box;
    }
}
