package aimas.actions.expandable;

import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.actions.atomic.DeliverBoxSurelyAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.List;

public class RemoveBoxAction extends ExpandableAction {

    Box box;
    CoordinatesPair start; // unblock path from here
    CoordinatesPair finish; // to here
    Agent agent; // by this agent

    public RemoveBoxAction(Box box, CoordinatesPair start, CoordinatesPair finish, Agent agent, Action parent){
        this.box = box;
        this.start = start;
        this.finish = finish;
        this.agent = agent;
        this.parent = parent;
        this.childrenActions = new ArrayList<>();

        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.CLEAR_PATH);
        decomposedTo.add(ActionType.CLEAR_PATH);
        decomposedTo.add(ActionType.MOVE_SURELY);
        decomposedTo.add(ActionType.DELIVER_BOX_SURELY);
        this.canBeDecomposedTo = decomposedTo;
        this.actionType = ActionType.REMOVE_BOX;
    }

    @Override
    public boolean isAchieved(Node node) {
        /**
         * Artur will implement this
         */
        return !PathFinder.getBoxesOnPath(node, start, finish,
                true, true, true).contains(box);
    }

    @Override
    public List<Action> decompose(Node node) {
        if (isAchieved(node)){
            System.err.println("RemoveBoxAction is already achieved for box " + box);
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        // ClearBox - ClearCell - GotoBox - DeliverBox

        Action clearBox = new ClearPathAction(start, box.getCoordinates(node), node, this);
        CoordinatesPair agentCellCoords = node.getAgentCellCoords().get(0); // just take the only agent for now
        Action clearCell = new ClearPathAction(box.getCoordinates(node), finish, node, this);
        Action gotoBox = new MoveSurelyAction(box.getCoordinates(node), agent,this);
        Action deliverBox = new DeliverBoxSurelyAction(box, finish, agent, this);
        List<Action> expandedActions = new ArrayList<>();

        // manually (not to traverse the tree for this purpose specifically)
        clearBox.setNumberAsChild(0);
        clearCell.setNumberAsChild(1);
        gotoBox.setNumberAsChild(2);
        deliverBox.setNumberAsChild(3);

        expandedActions.add(clearBox);
        expandedActions.add(clearCell);
        expandedActions.add(gotoBox);
        expandedActions.add(deliverBox);

        //childrenActions = expandedActions;

        return expandedActions;
    }

    @Override
    public String toString() {
        return "RemoveBoxAction: removing Box " + box + " from cell " + start + " to cell " + finish;
    }
}
