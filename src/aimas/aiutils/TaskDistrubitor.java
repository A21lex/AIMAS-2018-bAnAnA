package aimas.aiutils;

import aimas.Node;
import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDistrubitor {
    // Keeps track of which agent is responsible for which tasks
    private static Map<Agent, List<Task>> agentTasks = new HashMap<>();

    public static Map<Agent, List<Task>> getAgentTasks() {
        return agentTasks;
    }


    /**
     * Decide which agent has to satisfy each goal
     * @param node State of the level for which calculation takes place
     * @param goalsBoxes Pairs of goals/boxes obtained from BoxAssigner
     * @return
     */
    public static HashMap<Agent, List<Task>> assignTasksToAgents(Node node, HashMap<Cell, Box> goalsBoxes){
        HashMap<Agent, List<Task>> aTasks = new HashMap<>();
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
                    if (!isAlreadyAssigned(task)) {
                        if (!task.isAchieved(node)) {
                            availableTasks.add(task);
                        }
                    }
                }
            }
            agentTasks.put(agent, availableTasks); // record which agent has which tasks at the moment
            aTasks.put(agent, availableTasks);
        }
        return aTasks;
    }

    private static boolean areOfSameColor(Agent agent, Box box){
        return agent.getColor() == box.getColor();
    }

    // Check if a task is already assigned to any agent
    private static boolean isAlreadyAssigned(Task task){
        for (Agent agentWithTask : agentTasks.keySet()){
            if (agentTasks.get(agentWithTask).contains(task)){
                return true;
            }
        }
        return false;
    }

}

