package aimas.actions.atomic;

import aimas.Command;
import aimas.CoordinatesPair;
import aimas.Node;
import aimas.actions.ActionType;
import aimas.actions.AtomicAction;
import aimas.entities.Box;
import aimas.entities.Entity;

import java.util.List;

/**
 * Atomic action MOVE. This is called when path betweeen agent and "finish" is clear.
 */
public class MoveSurelyAction extends AtomicAction {

    CoordinatesPair finish;
    //Entity potentialEntity; // if we "move surely" to a box, this helps to move to one cell earlier than the box itself

    public MoveSurelyAction(CoordinatesPair finish){
        this.finish = finish;
        this.actionType = ActionType.MOVE_SURELY;

//        if (node.getCellAtCoords(finish).getEntity() != null) {
//            this.potentialEntity = node.getCellAtCoords(finish).getEntity();
//        }
    }

    @Override
    public int heuristic(Node node) {
        CoordinatesPair agentCellCoord = node.getAgentCellCoords().get(0); // just take the only agent for now
        int agentRow = agentCellCoord.getX();
        int agentCol = agentCellCoord.getY();
        int h = manhDist(agentRow, agentCol, finish.getX(), finish.getY());
        if (node.getAction() != null) {
            if (node.getAction().actionType == Command.Type.Pull || node.getAction().actionType == Command.Type.Push) {
                h += 25; // punish the agent for moving boxes while moving from one cell to another
            }
        }
        return h;
    }

    @Override
    public boolean isAchieved(Node node) {
        CoordinatesPair agentCellCoord = node.getAgentCellCoords().get(0); // just take the only agent for now
        int agentRow = agentCellCoord.getX();
        int agentCol = agentCellCoord.getY();
        Entity potentialEntity = null;
        if (node.getCellAtCoords(finish).getEntity() != null) {
            potentialEntity = node.getCellAtCoords(finish).getEntity();
        }
        if (potentialEntity instanceof Box) { // instanceof checks for null by default, no need for explicit check
            // if we need to "move surely" to a box, we stop when we are close to it, not when we are at its cell
            return manhDist(agentRow, agentCol, finish.getX(), finish.getY()) == 1;
        }
        return manhDist(agentRow, agentCol, finish.getX(), finish.getY()) == 0;
    }

    @Override
    public String toString() {
        return "MoveSurelyAction: moving to cell " + finish;
    }

}
