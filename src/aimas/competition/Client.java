package aimas.competition;

import aimas.Command;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.ExpandableAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.aiutils.BestFirstSearch;
import aimas.aiutils.World;
import aimas.board.Cell;
import aimas.board.entities.Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Client to run against levels
 */
/*
Basic usage for the server is either of:
   $ java -jar server.jar -c <command> -l <level path> <arguments>
   $ java -jar server.jar -o <file>

   Command to run our program will be as follows:
   First compile:
   javac aimas/competition/Client.java
   Then run:
   java -Dsun.java2d.opengl=true -jar server.jar -l levels/SAsimple1.lvl
 -c "java aimas.competition.Client" -g -p

   Remember to replace all System.out.println calls in our program to stderr, else they will go to the server
 */
public class Client {

    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        System.err.println("Hello from Client. I am sending this using the error outputstream.");
        ArrayList<ArrayList<Cell>> level = new ArrayList<>();
        try {
            level = InputLevelReader.getLevel(in);
        }
        catch (IOException ex){
            // Nowhere to write to or nothing to read (latter unlikely)
        }
        System.err.println(InputLevelReader.getAgentCellCoords().get(0)); // just print first agent's position
        System.err.println("Level read successfully");

        // Try to solve level using the simplest technique we have for now
        Node node = new Node(null);
        node.setLevel(level);
        node.setBoxCellCoords(Node.copyList(InputLevelReader.getBoxCellCoords()));
        node.setAgentCellCoords(Node.copyList(InputLevelReader.getAgentCellCoords()));
        node.setGoalCellCoords(Node.copyList(InputLevelReader.getGoalCellCoords()));
        node.setTunnelCellCoords(Node.copyList(InputLevelReader.getTunnelCellCoords()));
        World world = new World(node);
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
                        actionsToPerform.add(expandableSubaction.decompose(node).get(0));
                        actionsToPerform.add(expandableSubaction.decompose(node).get(1));

                        actionsToPerform.add(expandableSubaction.decompose(node).get(2));
                        actionsToPerform.add(expandableSubaction.decompose(node).get(3));
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
        System.err.println("Outputting total shortest path and who planned it");
        for (int i = path.size() - 1; i >= 0; i--) {
            if (path.get(i).getAction() != null) { // if the action is null, this is a start node
                System.err.println(path.get(i).getAgentNumber()); // who did this action
                System.err.println(path.get(i).getAction().toString());
            }
        }

        // Record which agent did which move
        int maxMovesByAnyAgent = 0;
        List<Agent> agents = world.getState().getAgents();
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
                if (world.isAValidMove(agent.getNumber(), command)){
                    world.makeAMove(agent.getNumber(), command);
                    String commandWithoutSquareBrackets = command.toString().replaceAll(regex, "");
                    compositeCommand.append(commandWithoutSquareBrackets);
                    compositeCommand.append(",");
                }
                else{
                    System.err.println("Wrong move detected");
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

        System.err.println("Outputting overall result");
        for (String command : Solution){
            System.out.println(command);
        }
        System.err.println(solveLevel.isAchieved(world.getState()) ? "Level solved" : "Level not solved");
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
            //System.err.println(curNode);
        }
        return path;
    }
}
