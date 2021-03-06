package aimas.actions.expandable;

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.actions.atomic.DeliverBoxSurelyAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.List;

public class AchieveGoalAction extends ExpandableAction {
    public Cell goalCell; // this is what we want to achieve
    public Box box; // .. with this box
    Agent agent; // .. by this agent

    public Agent getAgent() {
        return agent;
    }


    public AchieveGoalAction(Cell goalCell, Box box, Agent agent, Action parent){
        this.goalCell = goalCell;
        this.box = box;
        this.agent = agent;
        this.parent = parent;
        this.childrenActions = new ArrayList<>();

        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.CLEAR_PATH);
        decomposedTo.add(ActionType.CLEAR_PATH);
        decomposedTo.add(ActionType.MOVE_SURELY);
        decomposedTo.add(ActionType.DELIVER_BOX_SURELY);
        this.canBeDecomposedTo = decomposedTo;
        this.actionType = ActionType.ACHIEVE_GOAL;
        this.numberOfAttempts = 0;
    }

    @Override
    public boolean isAchieved(Node node) {
        Cell newGoalCell = node.getCellAtCoords(goalCell.getCoordinates());
        if (newGoalCell.getEntity() == null){
            return false;
        }
        if (newGoalCell.getEntity() instanceof Box) {
            //System.err.println(goalCell);
            Box boxOnCell = (Box) newGoalCell.getEntity();
            return newGoalCell.getGoalLetter() == Character.toLowerCase(boxOnCell.getLetter());
        }
        return false;
    }

    @Override
    public List<Action> decompose(Node node) {
        if (isAchieved(node)){
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        //Clear box - clear goal - gotoBox - deliverBox
        //CoordinatesPair agentCellCoord = agent.getCoordinates(node);
        CoordinatesPair agentCellCoord = agent.getCoordinates(node);
        Action clearBox = new ClearPathAction(agentCellCoord ,box.getCoordinates(node), agent, node, this);
        Action clearGoal = new ClearPathAction(box.getCoordinates(node), goalCell.getCoordinates(), agent, node, this);
        Action gotobox = new MoveSurelyAction(box.getCoordinates(node), agent, this);
        Action deliverbox = new DeliverBoxSurelyAction(box, goalCell.getCoordinates(), agent, this);
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

    public boolean equals(AchieveGoalAction achieveGoalAction){
        if (this.box.equals(achieveGoalAction.box) && this.goalCell.equals(achieveGoalAction.goalCell)) return true;
        return false;
    }
}
