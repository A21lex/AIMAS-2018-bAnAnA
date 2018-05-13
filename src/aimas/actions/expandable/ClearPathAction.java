package aimas.actions.expandable;

import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Entity;

import java.util.ArrayList;

/**
 * Simple action ClearPathAction
 */
public class ClearPathAction extends ExpandableAction {

    CoordinatesPair start; // from this cell
    CoordinatesPair finish; // to this cell
    Node node; // at this state of the level
    Entity first; // potential entity at start (box/agent)
    Entity second; // potential entity at finish (box/agent)

    public ClearPathAction(CoordinatesPair start, CoordinatesPair finish, Node node, Action parent){
        this.start = start;
        this.finish = finish;
        this.node = node;
        this.parent = parent;

        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.REMOVE_BOX);
        this.canBeDecomposedTo = decomposedTo;

        this.actionType = ActionType.CLEAR_PATH;

        // Get potential entities (agent/box) at start and finish cells

        if (node.getCellAtCoords(this.start).getEntity() != null){
            first = node.getCellAtCoords(this.start).getEntity();
        }
        if (node.getCellAtCoords(this.finish).getEntity() != null){
            second = node.getCellAtCoords(this.finish).getEntity();
        }

    }

    @Override
    public boolean isAchieved(Node node) {
        CoordinatesPair fromHere = start;
        CoordinatesPair toThere = finish;

        if (first != null){
            if (first instanceof Box){
                Box box = (Box) first;
                fromHere = box.getCoordinates(node);
            }
            else if (first instanceof Agent){
                Agent agent = (Agent) first;
                fromHere = agent.getCoordinates(node);
            }
        }
        if (second != null){
            if (second instanceof Box){
                Box box = (Box) second;
                toThere = box.getCoordinates(node);
            }
            else if (second instanceof Agent){
                Agent agent = (Agent) second;
                toThere = agent.getCoordinates(node);
            }
        }
        // If path exists from current position of entity to current position of another entity
        // or cell in case there are no entities, return true
        return PathFinder.pathExists(node.getLevel(), fromHere, toThere,
                true, true, true);
    }

    @Override
    public ArrayList<Action> decompose(Node node){
        if (isAchieved(node)){
            System.err.println("ClearPathAction is already achieved for " + start + " to " + finish);
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }

        // List of boxes on path from start to  finish
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Artur will implement this<<<<<<<
        ArrayList<Box> boxes = PathFinder.getBoxesOnPath(node.getLevel(), start, finish,
                true, true, true);

        ArrayList<Action> expandedActions = new ArrayList<>();
        for (Box box : boxes){
            expandedActions.add(new RemoveBoxAction(box, start, finish, this));
        }
        // for every box in list of boxed: add remove(box) to expandedActions
        // will return corresponding actions or empty list if there are no boxes on path (although
        // in this case this method shouldn't be called at all)
        return expandedActions;
    }
}
