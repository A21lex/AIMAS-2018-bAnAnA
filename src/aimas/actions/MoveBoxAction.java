package aimas.actions;

import aimas.Cell;
import aimas.CoordinatesPair;
import aimas.entities.Box;
import aimas.Node;

public class MoveBoxAction extends Action {
    Box box;
    CoordinatesPair finish; // move box to here

    public MoveBoxAction(Box box, CoordinatesPair finish){
        this.box = box;
        this.finish = finish;
        this.actionType = ActionType.MOVE_BOX;
    }

    @Override
    public ActionType getType() {
        return this.actionType;
    }

    @Override
    public int heuristic(Node node) {
        return manhDist(box.getCoordinates(node).getX(), box.getCoordinates(node).getY(),
                finish.getX(), finish.getY());
    }

    @Override
    public boolean isAchieved(Node node) {
        return box.getCoordinates(node).equals(finish);
    }

    @Override
    public String toString() {
        return "MoveBoxAction: moving Box " + box + " to cell " + finish;
    }
}
