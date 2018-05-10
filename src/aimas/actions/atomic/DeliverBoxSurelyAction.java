package aimas.actions.atomic;

import aimas.Command;
import aimas.board.CoordinatesPair;
import aimas.actions.ActionType;
import aimas.actions.AtomicAction;
import aimas.board.entities.Box;
import aimas.Node;

/**
 * Atomic action DELIVER_BOX_SURELY. This is called when path betweeen box and "finish" is clear.
 */
public class DeliverBoxSurelyAction extends AtomicAction {
    Box box;
    CoordinatesPair finish; // move box to here

    public DeliverBoxSurelyAction(Box box, CoordinatesPair finish){
        this.box = box;
        this.finish = finish;
        this.actionType = ActionType.DELIVER_BOX_SURELY;
    }

    @Override
    public int heuristic(Node node) {
        int punishmentForMovingOtherBoxes = 0;
        if (node.getAction() != null) {
            if (node.getAction().actionCommandType == Command.CommandType.Push || node.getAction().actionCommandType == Command.CommandType.Pull) {
                if (!node.getBoxBeingMoved().equals(box)) {
                    punishmentForMovingOtherBoxes += 20;
                }
            }
        }
        return manhDist(box.getCoordinates(node).getX(), box.getCoordinates(node).getY(),
                finish.getX(), finish.getY()) + punishmentForMovingOtherBoxes;
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
