package aimas;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by aleksandrs on 4/19/18.
 */

/**
 * Run everything.. basically can be used for testing for now.
 * TODO fix bug: now commands are correct but nodes are wrong in the loop below
 * TODO ONLY in case we try to chain goals: otherwise works fine now... fml...
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

        /*double startTime = System.nanoTime();
        ArrayList<Node> shortestPath = BestFirstSearch.AStar(start, 'd');
        double timeElapsed = (System.nanoTime() - startTime) / 1000000000.0;
        System.out.println("Printing shortest path");
        for (int i = shortestPath.size()-1; i >= 0; i--) {
            if (shortestPath.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(shortestPath.get(i).getAction());
            }
        }
        System.out.println("the time elapsed: " + timeElapsed);*/
        // TODO: Boxes can be set as immutable if put in dead end cells
        // TODO2: Make A* have a pending list of goals which had been disachieved
        // TODO3: Make boxes immutable/unmovable once they satisfy a goal
        // Let's say we have the following list of goals to achieve
        ArrayList<Character> goalsToAchieve = new ArrayList<>();
          //goalsToAchieve.add('w');

//        goalsToAchieve.add('b');
//        goalsToAchieve.add('c');
//        goalsToAchieve.add('d');
        goalsToAchieve.add('a');

        ArrayList<Node> totalShortestPath = new ArrayList<>(); // combine paths from A* and GP to solve level
        Node curNode = start;
        for (Character goal : goalsToAchieve){
            ArrayList<Node> tempPath = BestFirstSearch.AStar(curNode, goal);
            for (int i = tempPath.size() - 1; i >= 0; i--){
                totalShortestPath.add(0,tempPath.get(i)); // add to start of list
            }
            tempPath.clear();
            curNode = totalShortestPath.get(0); // we are at last element of the totpath
            System.out.println(curNode);
        }

        System.out.println("Printing total shortest path");
        for (int i = totalShortestPath.size() - 1; i >= 0; i--) {
            if (totalShortestPath.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(totalShortestPath.get(i).getAction().toString());
                //System.out.println(totalShortestPath.get(i));
            }
        }
    }
}
