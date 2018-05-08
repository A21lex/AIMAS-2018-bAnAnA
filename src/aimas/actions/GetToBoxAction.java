package aimas.actions;

import aimas.Command;
import aimas.CoordinatesPair;
import aimas.entities.Box;
import aimas.Node;

public class GetToBoxAction extends Action {
    private Box box;
    //private Cell.CoordinatesPair boxCoords;

    public GetToBoxAction(Box box){
        this.box = box;
        this.actionType = ActionType.GET_TO_BOX;
        //this.boxCoords = box.getCoordinates(node);
    }

    // If manh dist between box and agent is 1 it means agent is next to the box. There is no exceptions to this.
    @Override
    public boolean isAchieved(Node node){
        CoordinatesPair agentCellCoords = node.getAgentCellCoords().get(0); // just take the only agent for now
        int agentRow = agentCellCoords.getX();
        int agentCol = agentCellCoords.getY();
        if (manhDist(box.getCoordinates(node).getX(), box.getCoordinates(node).getY(), agentRow, agentCol) == 1){
            return true;
        }
        return false;
    }

    @Override
    public int heuristic(Node node) {
        CoordinatesPair agentCellCoords = node.getAgentCellCoords().get(0); // just take the only agent for now
        int agentRow = agentCellCoords.getX();
        int agentCol = agentCellCoords.getY();

        CoordinatesPair boxCellCoords = box.getCoordinates(node);
        int boxRow = boxCellCoords.getX();
        int boxCol = boxCellCoords.getY();

        int fromAgentToBox = manhDist(agentRow, agentCol, boxRow, boxCol);
        if (!(node.getAction() == null)) {
            if (node.getAction().actionType.equals(Command.Type.Pull) ||
                    node.getAction().actionType.equals(Command.Type.Push)) {
                fromAgentToBox += 10; // let's say we punish agent for trying to move other boxes while moving to
                // a clear box
            }
        }


        return fromAgentToBox;
    }

    @Override
    public String toString() {
        return "GetToBox action: get to box " + box;
    }
}
