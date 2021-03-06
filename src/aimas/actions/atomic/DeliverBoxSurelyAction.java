package aimas.actions.atomic;

import aimas.Command;
import aimas.Launcher;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.board.CoordinatesPair;
import aimas.actions.ActionType;
import aimas.actions.AtomicAction;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.Node;

import java.util.ArrayList;

/**
 * Atomic action DELIVER_BOX_SURELY. This is called when path betweeen box and "finish" is clear.
 */
public class DeliverBoxSurelyAction extends AtomicAction {
    Box box;

    public CoordinatesPair getFinish() {
        return finish;
    }

    CoordinatesPair finish; // move box to here

    public DeliverBoxSurelyAction(Box box, CoordinatesPair finish, Agent agent, Action parent){
        this.box = box;
        this.finish = finish;
        this.agent = agent;
        this.parent = parent;
        this.actionType = ActionType.DELIVER_BOX_SURELY;
        this.childrenActions = new ArrayList<>();
    }

    public Box getBox() {
        return box;
    }

    @Override
    public int heuristic(Node node) {
        int punishmentForMovingOtherBoxes = 0;
        int punishmentForPullingInstead = 0;
        if (node.getAction() != null) {
            if (node.getAction().actionCommandType == Command.CommandType.Push ||
                    node.getAction().actionCommandType == Command.CommandType.Pull) {
                if (!node.getBoxBeingMoved().equals(box)) {
                    punishmentForMovingOtherBoxes += 20;
                }
            }
            if (node.getAction().actionCommandType == Command.CommandType.Pull) punishmentForPullingInstead += 1; // 0 works for MA level
        }

        int h;
        if (Launcher.HEURISTIC_USED == Launcher.Heuristic.BFS){
            PathFinder.pathExists(node.getLevel(), box.getCoordinates(node), finish, true, false,
                    false);
            h = PathFinder.getFoundPath().size();
        }
        else if (Launcher.HEURISTIC_USED == Launcher.Heuristic.MANHATTAN){
            h = manhDist(box.getCoordinates(node).getX(), box.getCoordinates(node).getY(),
                    finish.getX(), finish.getY());
        }
        else{ // Default to BFS if no option specified
            PathFinder.pathExists(node.getLevel(), box.getCoordinates(node), finish, true, false,
                    false);
            h = PathFinder.getFoundPath().size();
        }

        return h + punishmentForMovingOtherBoxes + punishmentForPullingInstead;

    }

    @Override
    public boolean isAchieved(Node node) {
        return box.getCoordinates(node).equals(finish);
    }


    @Override
    public String toString() {
        return "DeliverBoxSurelyAction: moving Box " + box + " to cell " + finish;
    }
}
