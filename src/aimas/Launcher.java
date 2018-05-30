package aimas;

import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.aiutils.*;
import aimas.board.Cell;
import aimas.board.entities.Agent;

import java.io.IOException;
import java.util.*;

/**
 * Run solver on a text file. Can be used for testing.
 *
 */
public class Launcher {
    // Some options for testing
    // using Manhattan distance or bfs calculated shorted path length for guiding A*
    public enum Heuristic{
        BFS, MANHATTAN
    }
    public static final Heuristic HEURISTIC_USED = Heuristic.BFS;

    //private static  Map<CoordinatesPair, Double> cellWeights;
    private static boolean isMA = false;
    public static void main(String[] args) {
        ArrayList<ArrayList<Cell>> level = new ArrayList<>();
        try {
            level = LevelReader.getLevel("res/levels/competition_levelsSP18/SAZEROagent.lvl");
            //level = LevelReader.getLevel("res/levels/test_levels/SAboXboXboX.lvl");
            //level = LevelReader.getLevel("res/levels/test_levels/test.lvl");
        }
        catch (IOException ex){
            // Nowhere to write to or nothing to read (latter unlikely)
        }
        System.err.println(LevelReader.getAgentCellCoords().get(0)); // just print first agent's position
        System.err.println("Level read successfully");
        System.err.println("Heuristic used: " + HEURISTIC_USED.toString());

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
            SolveLevelAction solveLevel = new SolveLevelAction(node, BdiHtnFsmSolver.cellWeights);
            Agent agent = world.getState().getAgents().get(0);
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

}