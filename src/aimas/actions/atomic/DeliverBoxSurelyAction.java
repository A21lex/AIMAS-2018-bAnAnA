package aimas.actions.atomic;

import aimas.Command;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.board.CoordinatesPair;
import aimas.actions.ActionType;
import aimas.actions.AtomicAction;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.Node;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Atomic action DELIVER_BOX_SURELY. This is called when path betweeen box and "finish" is clear.
 */
public class DeliverBoxSurelyAction extends AtomicAction {
    Box box;
    CoordinatesPair finish; // move box to here

    public DeliverBoxSurelyAction(Box box, CoordinatesPair finish/*, Agent agent*/, Action parent){
        this.box = box;
        this.finish = finish;
        this.agent = agent;
        this.parent = parent;
        this.actionType = ActionType.DELIVER_BOX_SURELY;
        this.childrenActions = new ArrayList<>();
        this.numberOfAttempts = 0;
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
            if (node.getAction().actionCommandType == Command.CommandType.Pull) punishmentForPullingInstead += 5;
        }
        //PathFinder.pathExists(node.getLevel(), box.getCoordinates(node), finish, true,false,true);
       // if(PathFinder.getFoundPath().contains(node.getAgentCellCoords().get(0))) punishmentForPullingInstead += 5;
        return manhDist(box.getCoordinates(node).getX(), box.getCoordinates(node).getY(),
                finish.getX(), finish.getY()) + punishmentForMovingOtherBoxes
                + punishmentForPullingInstead;
    }
    // forcing git to see

    @Override
    public boolean isAchieved(Node node) {
        return box.getCoordinates(node).equals(finish);
    }


    @Override
    public String toString() {
        return "DeliverBoxSurelyAction: moving Box " + box + " to cell " + finish;
    }
}
