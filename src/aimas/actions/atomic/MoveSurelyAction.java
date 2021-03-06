package aimas.actions.atomic;

import aimas.Command;
import aimas.Launcher;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.actions.ActionType;
import aimas.actions.AtomicAction;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Entity;

import java.util.ArrayList;

/**
 * Atomic action MOVE. This is called when path between agent and "finish" is clear.
 */
public class MoveSurelyAction extends AtomicAction {

    public CoordinatesPair getFinish() {
        return finish;
    }

    CoordinatesPair finish;

    public MoveSurelyAction(CoordinatesPair finish, Agent agent, Action parent){
        this.finish = finish;
        this.agent = agent;
        this.parent = parent;
        this.actionType = ActionType.MOVE_SURELY;
        this.childrenActions = new ArrayList<>();
        this.numberOfAttempts = 0;
    }

    @Override
    public int heuristic(Node node) {
        CoordinatesPair agentCellCoord = agent.getCoordinates(node);
        int agentRow = agentCellCoord.getX();
        int agentCol = agentCellCoord.getY();

        int h;
        if (Launcher.HEURISTIC_USED == Launcher.Heuristic.BFS){
            PathFinder.pathExists(node.getLevel(), agentCellCoord, finish, true, false,
                    false);
            h = PathFinder.getFoundPath().size();
        }
        else if(Launcher.HEURISTIC_USED == Launcher.Heuristic.MANHATTAN){
            h = manhDist(agentRow, agentCol, finish.getX(), finish.getY());
        }
        else{ // Default to BFS if no option specified
            PathFinder.pathExists(node.getLevel(), agentCellCoord, finish, true, false,
                    false);
            h = PathFinder.getFoundPath().size();
        }

        if (node.getAction() != null) {
            if (node.getAction().actionCommandType == Command.CommandType.Pull ||
                    node.getAction().actionCommandType == Command.CommandType.Push) {
                h += 25; // punish the agent for moving boxes while moving from one cell to another
            }
        }
        return h;
    }

    @Override
    public boolean isAchieved(Node node) {
        CoordinatesPair agentCellCoord = agent.getCoordinates(node);
        int agentRow = agentCellCoord.getX();
        int agentCol = agentCellCoord.getY();
        Entity potentialEntity = null;
        if (node.getCellAtCoords(finish).getEntity() != null) {
            potentialEntity = node.getCellAtCoords(finish).getEntity();
        }
        if (potentialEntity instanceof Box) {
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
