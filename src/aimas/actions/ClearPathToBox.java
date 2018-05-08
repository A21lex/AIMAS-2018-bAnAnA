package aimas.actions;

import aimas.CoordinatesPair;
import aimas.PathFinder;
import aimas.entities.Box;
import aimas.Node;

import java.util.List;
import java.util.Random;

/**
 * Action to be called when PathFinder.pathExists does not find a clear path from agent to box
 */
public class ClearPathToBox extends Action {
    private Box box;
    List<Action> subActions;

    public ClearPathToBox(Box box){
        this.box = box;
        this.actionType = ActionType.FREE_PATH_TO_BOX;
    }

    @Override
    public int heuristic(Node node) {
        return new Random().nextInt(10); // change this to something like: if fewer boxes on the way, it's good
    }

    @Override
    public boolean isAchieved(Node node) {
        CoordinatesPair agentCoords = node.getAgentCellCoords().get(0); // get agent 0 for now
        return PathFinder.pathExists(node.getLevel(), agentCoords, box.getCoordinates(node),
                true, true, true);
    }

    @Override
    public String toString() {
        return "ClearPathToBox action: clear path to box " + box;
    }
}
