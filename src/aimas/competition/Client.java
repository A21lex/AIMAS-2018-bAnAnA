package aimas.competition;

import aimas.Command;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.ExpandableAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.aiutils.AlexSimpleMASASolver;
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

        List<String> Solution = AlexSimpleMASASolver.getSolutionForLevel(node);
        for (String string : Solution){
            System.out.println(string);
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
