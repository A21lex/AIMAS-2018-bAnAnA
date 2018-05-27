package aimas;

import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.ExpandableAction;
import aimas.actions.expandable.AchieveGoalAction;
import aimas.actions.expandable.RemoveBoxAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.aiutils.*;
import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Entity;
import aimas.competition.InputLevelReader;

import java.io.IOException;
import java.util.*;


/**
 * Created by aleksandrs on 4/19/18.
 */

/**
 * Run everything.. basically can be used for testing for now.
 *
 */
public class Launcher {

    private static  Map<CoordinatesPair, Double> cellWeights;
    private static boolean isMA = false;
    public static void main(String[] args) {
        ArrayList<ArrayList<Cell>> level = new ArrayList<>();
        try {
            level = LevelReader.getLevel("res/levels/competition_levelsSP18/SABeTrayEd.lvl");
        }
        catch (IOException ex){
            // Nowhere to write to or nothing to read (latter unlikely)
        }
        System.err.println(LevelReader.getAgentCellCoords().get(0)); // just print first agent's position
        System.err.println("Level read successfully");

        // Try to solve level
        Node node = new Node(null);
        node.setLevel(level);
        node.setBoxCellCoords(Node.copyList(LevelReader.getBoxCellCoords()));
        node.setAgentCellCoords(Node.copyList(LevelReader.getAgentCellCoords()));
        node.setGoalCellCoords(Node.copyList(LevelReader.getGoalCellCoords()));
        node.setTunnelCellCoords(Node.copyList(LevelReader.getTunnelCellCoords()));
        node.setSpaceCells(Node.copyList(LevelReader.getSpaceCellCoords()));

        if (node.getAgents().size()>1){
            isMA = true;
        }
        if (isMA){
            List<String> Solution = MASolver.getSolutionForLevel(node);
            for (String string : Solution){
                System.out.println(string);
            }
        }
        else{
            World world =  new World(node);
            MapParser parser = new MapParser(node.getLevel());
            parser.parseMap(Node.getSpaceCellCoords(), node.getLevel());
            BdiHtnFsmSolver.cellWeights = parser.getCellWeights();
            SolveLevelAction solveLevel = new SolveLevelAction(node);
            Agent agent = world.getState().getAgents().get(0); // any for now
            ArrayList<Command> Solution = BdiHtnFsmSolver.HTNBDIFSM(agent, solveLevel, world);
            for (Command command : Solution){
                System.out.println(command.toString());
            }
            try{
                Thread.sleep(300);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            System.err.println("Solution length " + Solution.size());
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
            if (path.size() > 0) { // if path.size() == 0, it means action had already been completed, so do nothing
                curNode = path.get(0); // we are at last element of the totpath
            }
            System.out.println(curNode);
        }
        return path;
    }
}