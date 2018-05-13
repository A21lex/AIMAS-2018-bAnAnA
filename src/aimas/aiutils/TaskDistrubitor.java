package aimas.aiutils;

import aimas.Node;
import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskDistrubitor {
    /**
     * Decide which agent has to satisfy each goal
     * @param node State of the level for which calculation takes place
     * @param goalsBoxes Pairs of goals/boxes obtained from BoxAssigner
     * @return
     */
    public static HashMap<Agent, List<Task>> assignTasksToAgents(Node node, HashMap<Cell, Box> goalsBoxes){
        HashMap<Agent, List<Task>> agentsTasks = new HashMap<>();
        // Convert HashMap<Cell, Box> to List of Tasks
        List<Task> tasks = new ArrayList<>();
        for (Cell goalCell : goalsBoxes.keySet()){
            tasks.add(new Task(goalCell, goalsBoxes.get(goalCell)));
        }
        // Assign tasks to agents
        ArrayList<Agent> agents = node.getAgents();

        for (Agent agent : agents){
            // Check which tasks each agent can potentially execute
            List<Task> availableTasks = new ArrayList<>();
            for (Task task : tasks){
                if (areOfSameColor(agent, task.box)){
                    // Agent and box are of same color
                    availableTasks.add(task);
                }
            }
            agentsTasks.put(agent, availableTasks);
        }
        return agentsTasks;
    }

    private static boolean areOfSameColor(Agent agent, Box box){
        return agent.getColor() == box.getColor();
    }

}

class Task{


    Cell goal;
    Box box;

    public Task(Cell goal, Box box) {
        this.goal = goal;
        this.box = box;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public Cell getGoal() {
        return goal;
    }

    public void setGoal(Cell goal) {
        this.goal = goal;
    }
}