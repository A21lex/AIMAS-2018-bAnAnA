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

    public Box box;
    CoordinatesPair start; // unblock path from here
    CoordinatesPair finish; // to here

    public RemoveBoxAction(Box box, CoordinatesPair start, CoordinatesPair finish, Action parent){
        this.box = box;
        this.start = start;
        this.finish = finish;
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

        ((ClearPathAction)parent).fromHere = (
                (ClearPathAction)parent).updateCoordinates(((ClearPathAction)parent).first, node, start);
        ((ClearPathAction)parent).toThere =
                ((ClearPathAction)parent).updateCoordinates(((ClearPathAction) parent).second, node, finish);

        //System.out.println(box);
        //System.out.println("fromHere " + ((ClearPathAction)parent).fromHere);
        //System.out.println("toThere " + ((ClearPathAction)parent).toThere);



        System.out.println(PathFinder.getBoxesOnPath(node, ((ClearPathAction) parent).fromHere, ((ClearPathAction) parent).toThere,
                true, false, true, ((ClearPathAction) parent).exceptionBoxes).size());

        return !PathFinder.getBoxesOnPath(node, ((ClearPathAction)parent).fromHere, ((ClearPathAction)parent).toThere,
                true, false, true, ((ClearPathAction) parent).exceptionBoxes).contains(box);

       // if ((PathFinder.getBoxesOnPath(node, ((ClearPathAction)parent).fromHere, ((ClearPathAction)parent).toThere,
       //                true, false, false).contains(box))
       //         && PathFinder.getFoundPath().get(PathFinder.getFoundPath().size()-1).equals(box)) return true;
        //return false;
    }

    @Override
    public List<Action> decompose(Node node) {
        if (isAchieved(node)){
            System.err.println("RemoveBoxAction is already achieved for box " + box);
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        // ClearBox - ClearCell - GotoBox - DeliverBox

       /* Action clearBox = new ClearPathAction(start, box.getCoordinates(node), node, this);
        CoordinatesPair agentCellCoords = node.getAgentCellCoords().get(0); // just take the only agent for now
       
        List<Action> expandedActions = new ArrayList<>(); */

        CoordinatesPair parkingCellCoords = LevelReader.findParkingCell(node, box.getCoordinates(node));

        CoordinatesPair agentCellCoords = node.getAgentCellCoords().get(0); // just take the only agent for now
        Action clearBox = new ClearPathAction(agentCellCoords, box.getCoordinates(node), node, this);
        Action clearCell = new ClearPathAction(box.getCoordinates(node), parkingCellCoords, node, this);
        Action gotoBox = new MoveSurelyAction(box.getCoordinates(node), this);
        Action deliverBox = new DeliverBoxSurelyAction(box, parkingCellCoords, this);
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

        childrenActions = expandedActions;

        return expandedActions;
    }

    @Override
    public String toString() {
        return "RemoveBoxAction: removing Box " + box;
    }
}
