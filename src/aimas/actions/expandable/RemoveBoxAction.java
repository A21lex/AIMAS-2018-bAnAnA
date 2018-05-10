package aimas.actions.expandable;

import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.actions.atomic.DeliverBoxSurelyAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.List;

public class RemoveBoxAction extends ExpandableAction {

    Box box;
    CoordinatesPair start; // unblock path from here
    CoordinatesPair finish; // to here

    public RemoveBoxAction(Box box, CoordinatesPair start, CoordinatesPair finish){
        this.box = box;
        this.start = start;
        this.finish = finish;
        this.actionType = ActionType.REMOVE_BOX;
    }

    @Override
    public boolean isAchieved(Node node) {
        /**
         * Artur will implement this
         */
        return !PathFinder.getBoxesOnPath(node.getLevel(), start, finish,
                true, true, true).contains(box);
    }

    @Override
    public List<Action> decompose(Node node) {
        if (isAchieved(node)){
            System.err.println("RemoveBoxAction is already achieved for box " + box);
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        // ClearBox - ClearCell - GotoBox - DeliverBox

        Action clearBox = new ClearPathAction(start, box.getCoordinates(node), node);
        CoordinatesPair agentCellCoords = node.getAgentCellCoords().get(0); // just take the only agent for now
        Action clearCell = new ClearPathAction(box.getCoordinates(node), finish, node);
        Action gotoBox = new MoveSurelyAction(box.getCoordinates(node));
        Action deliverBox = new DeliverBoxSurelyAction(box, finish);
        List<Action> expandedActions = new ArrayList<>();
        expandedActions.add(clearBox);
        expandedActions.add(clearCell);
        expandedActions.add(gotoBox);
        expandedActions.add(deliverBox);
        return expandedActions;
    }
}
