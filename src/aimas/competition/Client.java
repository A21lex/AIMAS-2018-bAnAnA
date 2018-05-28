package aimas.competition;

import aimas.Command;
import aimas.Launcher;
import aimas.MapParser;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.aiutils.MASolver;
import aimas.aiutils.BdiHtnFsmSolver;
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
   From the folder with server jar, and where competition folder has our jars:

   javac -cp ".:aimas/competition/*" aimas/competition/Client.java

   Then from server, in the quotes:

   java -cp ".:aimas/competition/*" aimas/competition/Client

   i.e something like:

   java -Dsun.java2d.opengl=true
   -jar server.jar -l levels/SAsokobanLevel96.lvl -c "java -cp ".:aimas/competition/*" aimas/competition/Client" -g

   Remember to replace all System.out.println calls in our program to stderr, else they will go to the server
 */
public class Client {

    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static boolean isMA = false;
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
        System.err.println("Heuristic used: " + Launcher.HEURISTIC_USED.toString());

        // Try to solve level
        Node node = new Node(null);
        node.setLevel(level);
        node.setBoxCellCoords(Node.copyList(InputLevelReader.getBoxCellCoords()));
        node.setAgentCellCoords(Node.copyList(InputLevelReader.getAgentCellCoords()));
        node.setGoalCellCoords(Node.copyList(InputLevelReader.getGoalCellCoords()));
        node.setTunnelCellCoords(Node.copyList(InputLevelReader.getTunnelCellCoords()));
        node.setSpaceCells(Node.copyList(InputLevelReader.getSpaceCellCoords()));

        if (node.getAgents().size()>1){
            isMA = true;
        }
        if (isMA){
            List<String> Solution = MASolver.getSolutionForLevel(node);
            for (String string : Solution){
                System.err.println(string);
            }
            for (String string : Solution){
                System.out.println(string);
            }
        }
        else{
            World world =  new World(node);
            MapParser parser = new MapParser(node.getLevel());
            parser.parseMap(Node.getSpaceCellCoords(), node.getLevel());
            BdiHtnFsmSolver.cellWeights = parser.getCellWeights();
            SolveLevelAction solveLevel = new SolveLevelAction(node, BdiHtnFsmSolver.cellWeights);
            Agent agent = world.getState().getAgents().get(0); // any for now
            ArrayList<Command> Solution = BdiHtnFsmSolver.HTNBDIFSM(agent, solveLevel, world);
            for (Command command : Solution){
                System.out.println(command.toString());
            }
        }
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
