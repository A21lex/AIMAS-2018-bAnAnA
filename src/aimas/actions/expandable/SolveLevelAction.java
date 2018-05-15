package aimas.actions.expandable;

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.ActionType;
import aimas.actions.ExpandableAction;
import aimas.aiutils.BoxAssigner;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolveLevelAction extends ExpandableAction {
    //potentially store boxes and cells to satisfy with these
    Node node;

    public SolveLevelAction(Node node){
        this.node = node;

        ArrayList<ActionType> decomposedTo = new ArrayList<>();
        decomposedTo.add(ActionType.ACHIEVE_GOAL);
        this.canBeDecomposedTo = decomposedTo;
        this.parent = null; // top level node, so parent is null
        this.childrenActions = new ArrayList<>();
        this.actionType = ActionType.SOLVE_LEVEL;
        this.numberAsChild = -1; // emergency case
    }

    // Check if all the goal cells have on them a box of corresponding type; if not, return false
    @Override
    public boolean isAchieved(Node node) {
        for (CoordinatesPair coordinate : Node.getGoalCellCoords()){
            Cell cellAtCoordinate = node.getCellAtCoords(coordinate);
            if (cellAtCoordinate.getEntity() == null){
                return false;
            }
            Box boxAtCoordinate = (Box) cellAtCoordinate.getEntity();
            if (!(boxAtCoordinate.getLetter() == Character.toUpperCase(cellAtCoordinate.getGoalLetter()))){
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
        HashMap<Agent,List<Task>> agentTasks = TaskDistrubitor.assignTasksToAgents(node,goalsBoxes);
        int i = 0;
        for (Agent agent : agentTasks.keySet()){
            // for every agent, create goals corresponding to what he can do
            for (Task task : agentTasks.get(agent)){
                expandedActions.add(new AchieveGoalAction(task.getGoal(), task.getBox(), agent, this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }
        //dbca - test
        /* Here we need to use GoalPrioritizer to make sure goal actions are added in the correct order */

        //bacdfegh
        /*for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='b'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }
        for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='a'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }
        for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='c'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }
        for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='d'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }

        for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='f'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }
        for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='e'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }
        for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='g'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        }
        for (Cell cell : goalsBoxes.keySet()){
            if (cell.getGoalLetter()=='h'){
                expandedActions.add(new AchieveGoalAction(cell, goalsBoxes.get(cell), this));
                expandedActions.get(expandedActions.size()-1).setNumberAsChild(i);
                i++;
            }
        } */
        //expandedActions.add(new AchieveGoalAction())
        childrenActions = expandedActions;

        return expandedActions;
    }
    @Override
    public String toString() {
        return "SolveLevelAction: solving level";
    }
}
