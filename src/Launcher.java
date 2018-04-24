import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by aleksandrs on 4/19/18.
 */

/**
 * Run everything.. basically can be used for testing for now.
 */
public class Launcher {

    public static void main(String[] args) {
        Node start = new Node(null);
        try{
            start.setLevel(LevelReader.getLevel("testNode.lvl"));
        }
        catch (IOException e){
            System.out.println("########");
            System.out.println("Probably incorrect path");
        }
        // The lines below are mandatory as nodes are compared using box/agent coords!
        start.setBoxCellCoords(Node.copyList(LevelReader.getBoxCellCoords()));
        start.setAgentCellCoords(Node.copyList(LevelReader.getAgentCellCoords()));
        start.setGoalCellCoords(Node.copyList(LevelReader.getGoalCellCoords()));
        start.setTunnelCellCoords(Node.copyList(LevelReader.getTunnelCellCoords()));

        //test tunnels
        System.out.println("tunnels:");
        System.out.println(start.getTunnelCellCoords());

        double startTime = System.nanoTime();
        ArrayList<Node> shortestPath = BestFirstSearch.AStar(start, 'a');
        double timeElapsed = (System.nanoTime() - startTime) / 1000000000.0;
        System.out.println("Printing shortest path");
        for (int i = shortestPath.size()-1; i >= 0; i--) {
            if (shortestPath.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(shortestPath.get(i).getAction());
            }
        }
        System.out.println("the time elapsed: " + timeElapsed);

        // TODO: Test GoalPrioritizer in combination with A*

    }
}
