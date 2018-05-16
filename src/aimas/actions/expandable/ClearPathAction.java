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
    Agent agent; // by this agent

    //writing quickly now
    public CoordinatesPair fromHere;
    public CoordinatesPair toThere;
    public ArrayList<Box> exceptionBoxes;

    public ClearPathAction(CoordinatesPair start, CoordinatesPair finish, Agent agent, Node node, Action parent){
        this.start = start;
        this.finish = finish;
        this.node = node;
        this.parent = parent;
        this.agent = agent;
        this.childrenActions = new ArrayList<>();
        this.exceptionBoxes = new ArrayList<>();
        //boolean referToFirstEntity = false;
        //boolean referToSecondEntity = false;
        this.numberOfAttempts = 0;

        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.REMOVE_BOX);
        this.canBeDecomposedTo = decomposedTo;

        this.actionType = ActionType.CLEAR_PATH;

        // Get potential entities (agent/box) at start and finish cells

        if (node.getCellAtCoords(this.start).getEntity() != null){
            first = node.getCellAtCoords(this.start).getEntity();
            if (first instanceof Box && !(second instanceof Box)) exceptionBoxes.add((Box) first);
        }
        if (node.getCellAtCoords(this.finish).getEntity() != null){
            second = node.getCellAtCoords(this.finish).getEntity();
            if (second instanceof Box && !(first instanceof Box)) exceptionBoxes.add((Box) second);
        }

        fromHere = updateCoordinates(first, node, start);
        toThere = updateCoordinates(second,node,finish);

    }

    @Override
    public boolean isAchieved(Node node) {
      /*  CoordinatesPair fromHere = start;
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
        }  */

        fromHere = updateCoordinates(first, node, start);
        toThere = updateCoordinates(second,node,finish);
        //System.out.println("* "+fromHere);
       // System.out.println("* "+toThere);

        // If path exists from current position of entity to current position of another entity
        // or cell in case there are no entities, return true
       // return PathFinder.pathExists(node.getLevel(), fromHere, toThere,
        //        true, true, true);
        return PathFinder.pathExists(node.getLevel(), fromHere, toThere,
                true, false, true);
    }

    public CoordinatesPair updateCoordinates(Entity entity, Node node, CoordinatesPair initalCoordPair){
        if (entity != null){
            if (entity instanceof Box){
                Box box = (Box) entity;
                return box.getCoordinates(node);
            }
            else if (entity instanceof Agent){
                Agent agent = (Agent) entity;
                return agent.getCoordinates(node);
            }
        }
        return initalCoordPair;
    }

    @Override
    public ArrayList<Action> decompose(Node node){
        if (isAchieved(node)){
            //System.err.println("ClearPathAction is already achieved for " + start + " to " + finish);
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }
        System.out.println("not achieved node");
        /*for (CoordinatesPair cp : PathFinder.getFoundPath()){
            System.out.println(node.getCellAtCoords(cp).getEntity());
        }*/
        System.out.println(fromHere);
        System.out.println(toThere);
        System.out.println( PathFinder.pathExists(node.getLevel(), fromHere, toThere,
                true, false, false));

       // System.out.println("never getting here");

        // List of boxes on path from start to  finish
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Artur will implement this<<<<<<<
        //ArrayList<Box> boxes = PathFinder.getBoxesOnPath(node.getLevel(), start, finish,
         //       true, true, true);
        ArrayList<Box> boxes = PathFinder.getBoxesOnPath(node, start, finish,
                true, false, false, exceptionBoxes); // ingoring other agents on the path for now

        ArrayList<Action> expandedActions = new ArrayList<>();
        int i = 0; // nmber of action as a child of current action
        for (Box box : boxes){
            CoordinatesPair parkingCell = finish;// Alina's magic function (not here actually)
            expandedActions.add(new RemoveBoxAction(box, start, parkingCell , agent, this));
            expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
            i++;
        }
        System.out.println(boxes.size());
        // for every box in list of boxed: add remove(box) to expandedActions
        // will return corresponding actions or empty list if there are no boxes on path (although
        // in this case this method shouldn't be called at all)

        childrenActions = expandedActions;

        return expandedActions;
    }
    @Override
    public String toString() {
        return "ClearPathAction: clearing path from cell " + fromHere + " to cell " + toThere;
    }
}
