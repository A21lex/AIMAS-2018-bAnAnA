package aimas.aiutils;

import aimas.Command;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.ExpandableAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.board.entities.Agent;

import java.util.*;

/**
 * Call when need to solve MA if did not manage to incorporate it into state machine in time :)
 */
public class AlexSimpleMASASolver {

    public static List<String> getSolutionForLevel(Node node){
        World worldAlex = new World(node);
        ExpandableAction solveLevel = new SolveLevelAction(node);
        List<Action> actions = solveLevel.decompose(node);
        List<Action> actionsToPerform = new ArrayList<>();
        for (Action action : actions){
            ExpandableAction expandableAction = (ExpandableAction) action;
            for (Action subAction: expandableAction.decompose(node)){
                ExpandableAction expandableSubaction;
                if (subAction instanceof ExpandableAction) {
                    expandableSubaction = (ExpandableAction) subAction;
                    try {
                        for (int i = 0; i < 3; i++){
                            if (expandableSubaction.decompose(node).get(i) instanceof AtomicAction){
                                actionsToPerform.add(expandableSubaction.decompose(node).get(i));
                            }
                        }
                    }
                    catch (IndexOutOfBoundsException ex){
                        // likely we are at a node which is already achieved
                        // not as many actions, it's fine, continue
                        continue;
                    }
                }
                else { // is not expandable
                    actionsToPerform.add(subAction);
                }
            }
        }
        List<Node> path = getTotalPath(actionsToPerform, node);
        System.err.println("Printing total shortest path");
        for (int i = path.size() - 1; i >= 0; i--) {
            if (path.get(i).getAction() != null) { // if the action is null, this is a start node
                System.err.println(path.get(i).getAgentNumber()); // who did this action
                System.err.println(path.get(i).getAction().toString()); // what did he do
            }
        }
        // Check last node of solution on whether it is achieved
        System.err.println(solveLevel.isAchieved(path.get(0)) ? "Success" : "Failure");
        boolean test = true;
        // Record which agent did which move
        int maxMovesByAnyAgent = 0;
        List<Agent> agents = worldAlex.getState().getAgents();
        Map<Agent, List<Command>> agentsActions = new TreeMap<>(new Comparator<Agent>() {
            @Override
            public int compare(Agent agentOne, Agent agentTwo) {
                return agentOne.getNumber() - agentTwo.getNumber();
            }
        }); // who does what, using treemap to sort by keys
        for (Agent agent : agents){
            int movesCounter = 0;
            List<Command> commandList = new ArrayList<>();
            for (int i = path.size() - 1; i >= 0; i--){
                if (path.get(i).getAction() != null){
                    if (path.get(i).getAgentNumber() == agent.getNumber()){
                        commandList.add(path.get(i).getAction());
                        movesCounter++;
                    }
                }
            }
            if (maxMovesByAnyAgent < movesCounter){
                maxMovesByAnyAgent = movesCounter;
            }
            agentsActions.put(agent, commandList);
        }

        boolean testie = true;
        // Perform actions on the world performed by all the agents acting in it
        List<String> Solution = new ArrayList<>();

        // Go through ALL actions of all agents like so: AG1 move, AG2 move, ..., AGn move; Ag1 move2, Ag2 move2
        for (int i = 0; i < maxMovesByAnyAgent; i++){
            StringBuilder compositeCommand = new StringBuilder();
            compositeCommand.append("[");
            String regex = "\\[|\\]"; // to get rid of square brackets for separate moves
            for (Agent agent : agentsActions.keySet()){
                if (i >= agentsActions.get(agent).size()){
                    compositeCommand.append("NoOp");
                    compositeCommand.append(",");
                    continue; // there is no more moves to make for this agent, continue
                }
                Command command = agentsActions.get(agent).get(i); // executing this for now
                if (worldAlex.isAValidMove(agent.getNumber(), command)){
                    worldAlex.makeAMove(agent.getNumber(), command);
                    String commandWithoutSquareBrackets = command.toString().replaceAll(regex, "");
                    compositeCommand.append(commandWithoutSquareBrackets);
                    compositeCommand.append(",");
                }
                else{
                    //System.err.println("Wrong move detected");
                    agentsActions.get(agent).add(i,new Command(Command.Dir.N)); // shift commands of that agent by one to right

                    compositeCommand.append("NoOp");

                    compositeCommand.append(",");
                    // Conflict resolution here please
                }
            }
            if (compositeCommand.length() > 0){
                compositeCommand.setLength(compositeCommand.length()-1); // remove last comma
            }
            compositeCommand.append("]");
            Solution.add(compositeCommand.toString());
            compositeCommand.setLength(0); // clear sb
        }

        boolean grandfinale = true;
//        System.err.println("Printing overall result");
//        for (String command : Solution){
//            System.out.println(command);
//        }
        System.err.println(solveLevel.isAchieved(worldAlex.getState()) ? "Level solved" : "Level not solved");

        return Solution;
    }

    static List<Node> getTotalPath(List<Action> actionsToPerform, Node node){
        List<Node> path = new ArrayList<>();
        Node curNode = node;
        for (Action action : actionsToPerform){
            AtomicAction atomicAction = (AtomicAction) action; // is there a better way than casting all the time?
            ArrayList<Node> tempPath = BestFirstSearch.AStar(curNode, atomicAction);
            for (int i = tempPath.size() - 1; i >= 0; i--){
                path.add(0,tempPath.get(i)); // add to start of list
            }
            tempPath.clear();
            if (path.size() > 0) { // if path.size()==0, it means action had already been completed, so do nothing
                curNode = path.get(0); // we are at last element of the totpath
            }
            //System.out.println(curNode);
        }
        return path;
    }

}
