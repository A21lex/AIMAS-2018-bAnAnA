package aimas.actions.atomic;

import aimas.Command;
import aimas.CoordinatesPair;
import aimas.Node;
import aimas.actions.ActionType;
import aimas.actions.AtomicAction;

import java.util.List;

/**
 * Atomic action MOVE. This is called when path betweeen agent and "finish" is clear.
 */
public class MoveSurelyAction extends AtomicAction {

    CoordinatesPair finish;

    public MoveSurelyAction(CoordinatesPair finish){
        this.finish = finish;
        this.actionType = ActionType.MOVE_SURELY;
    }

    @Override
    public int heuristic(Node node) {
        CoordinatesPair agentCellCoord = node.getAgentCellCoords().get(0); // just take the only agent for now
        int agentRow = agentCellCoord.getX();
        int agentCol = agentCellCoord.getY();
        int h = manhDist(agentRow, agentCol, finish.getX(), finish.getY());
        if (node.getAction() != null) {
            if (node.getAction().actionType == Command.Type.Pull || node.getAction().actionType == Command.Type.Push) {
                h += 10; // punish the agent for moving boxes while moving from one cell to another
            }
        }
        return h;
    }

    @Override
    public boolean isAchieved(Node node) {
        return heuristic(node) == 0;
    }

    @Override
    public String toString() {
        return "MoveSurelyAction: moving to cell " + finish;
    }

}
