package aimas.actions.expandable;

import aimas.aiutils.BdiHtnFsmSolver;
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

    public Box box;
    CoordinatesPair start; // unblock path from here
    CoordinatesPair finish; // to here

    public Agent getAgent() {
        return agent;
    }

    Agent agent; // by this agent
    ArrayList<CoordinatesPair> blackList;

    public RemoveBoxAction(Box box, CoordinatesPair start, CoordinatesPair finish, Agent agent, Action parent){
        this.box = box;
        this.start = start;
        this.finish = finish;
        this.agent = agent;
        this.parent = parent;
        this.childrenActions = new ArrayList<>();
        this.blackList = new ArrayList<>();
        this.numberOfAttempts = 0;

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

        ((ClearPathAction)parent).fromHere = (
                (ClearPathAction)parent).updateCoordinates(((ClearPathAction)parent).first, node, start);
        ((ClearPathAction)parent).toThere =
                ((ClearPathAction)parent).updateCoordinates(((ClearPathAction) parent).second, node, finish);

        System.err.println(PathFinder.getBoxesOnPath(node, ((ClearPathAction) parent).fromHere, ((ClearPathAction) parent).toThere,
                true, false, true, ((ClearPathAction) parent).exceptionBoxes).size());

        return !PathFinder.getBoxesOnPath(node, ((ClearPathAction)parent).fromHere, ((ClearPathAction)parent).toThere,
                true, false, false, ((ClearPathAction) parent).exceptionBoxes).contains(box);

    }

    @Override
    public List<Action> decompose(Node node) {
        if (isAchieved(node)){
            System.err.println("RemoveBoxAction is already achieved for box " + box);
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        // ClearBox - ClearCell - GotoBox - DeliverBox

        CoordinatesPair parkingCellCoords = triggerParkingCellSearch(node);
        System.err.println("****");
        System.err.println(node);

        System.err.println(blackList);

        CoordinatesPair agentCellCoords = agent.getCoordinates(node);
        Action clearBox = new ClearPathAction(agentCellCoords, box.getCoordinates(node), agent, node, this);
        Action clearCell = new ClearPathAction(box.getCoordinates(node), parkingCellCoords, agent, node, this);
        Action gotoBox = new MoveSurelyAction(box.getCoordinates(node), agent, this);
        Action deliverBox = new DeliverBoxSurelyAction(box, parkingCellCoords, agent, this);
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

    public CoordinatesPair triggerParkingCellSearch(Node node){
        CoordinatesPair parkingCellCoords = BdiHtnFsmSolver.findParkingCell(node, box.getCoordinates(node), this,
                blackList);
        if(!blackList.contains(parkingCellCoords))blackList.add(parkingCellCoords);
        return parkingCellCoords;
    }

    @Override
    public String toString() {
        return "RemoveBoxAction: removing Box " + box;
    }
}