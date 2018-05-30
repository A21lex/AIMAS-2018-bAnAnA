package aimas.actions.expandable;

import aimas.aiutils.Task;
import aimas.aiutils.TaskDistributor;
import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.aiutils.BoxAssigner;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolveLevelAction extends ExpandableAction {
    //potentially store boxes and cells to satisfy with these
    Node node;
    Map<CoordinatesPair, Double> cellWeights;

    public SolveLevelAction(Node node, Map<CoordinatesPair, Double> cellWeights){
        this.node = node;
        this.cellWeights = cellWeights;
        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.ACHIEVE_GOAL);
        this.canBeDecomposedTo = decomposedTo;
        this.parent = null; // top level node, so parent is null
        this.childrenActions = new ArrayList<>();
        this.actionType = ActionType.SOLVE_LEVEL;
        this.numberAsChild = -1; // emergency case
        this.numberOfAttempts = 0;
    }

    // Check if all the goal cells have on them a box of corresponding type; if not, return false
    @Override
    public boolean isAchieved(Node node) {
        for (CoordinatesPair coordinate : Node.getGoalCellCoords()){
            Cell cellAtCoordinate = node.getCellAtCoords(coordinate);
            if (cellAtCoordinate.getEntity() == null){
                return false;
            }
            if (cellAtCoordinate.getEntity() instanceof Box) {
                Box boxAtCoordinate = (Box) cellAtCoordinate.getEntity();
                if (!(boxAtCoordinate.getLetter() == Character.toUpperCase(cellAtCoordinate.getGoalLetter()))) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Action> decompose(Node node) {

        if (isAchieved(node)) {
            return new ArrayList<>(); // if is already achieved, zero actions are required..
        }
        List<Action> expandedActions = new ArrayList<>();
        HashMap<Cell, Box> goalsBoxes = BoxAssigner.assignBoxesToGoals(node);
        HashMap<Agent,List<Task>> agentTasks = TaskDistributor.assignTasksToAgents(node, goalsBoxes);
        for (Agent agent : agentTasks.keySet()){
            // for every agent, create goals corresponding to what he can do
            for (Task task : agentTasks.get(agent)){
                expandedActions = addPrioritizedGoal(expandedActions, new AchieveGoalAction(task.getGoal(),
                        task.getBox(), agent, this));
            }
        }
        for (int j = 0; j < expandedActions.size(); j++){
            expandedActions.get(j).setNumberAsChild(j);
        }

        childrenActions = expandedActions;

        return expandedActions;
    }

    public List<Action> addPrioritizedGoal(List<Action> expandedActions, AchieveGoalAction action){
        double curWeight = cellWeights.get(action.goalCell.getCoordinates());

        for (int i = 0; i<expandedActions.size(); i++){
            AchieveGoalAction curExpAction = (AchieveGoalAction)expandedActions.get(i);
            if (curWeight < cellWeights.get(curExpAction.goalCell.getCoordinates())) {
                expandedActions.add(i,action);
                return expandedActions;
            }
        }
        expandedActions.add(action);
        return expandedActions;
    }

    @Override
    public String toString() {
        return "SolveLevelAction: solving level";
    }
}
