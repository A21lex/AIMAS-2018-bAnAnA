package aimas;

import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.entities.Box;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by aleksandrs on 4/19/18.
 */

/**
 * Run everything.. basically can be used for testing for now.
 *
 */
public class Launcher {

    public static void main(String[] args) {
        Node start = new Node(null);
        try{
            start.setLevel(LevelReader.getLevel("res/levels/test_levels/SAanagram.lvl"));
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

        goalsToAchieve.add('w');
        goalsToAchieve.add('u');
//        goalsToAchieve.add('d');
//        goalsToAchieve.add('c');
//        goalsToAchieve.add('d');
//        goalsToAchieve.add('a');


        /*ArrayList<Node> totalShortestPath = new ArrayList<>(); // combine paths from A* and GP to solve level
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
        }*/

        // Testing new action stuff

        List<CoordinatesPair> boxCoords = start.getBoxCellCoords();
        List<Box> boxes = new ArrayList<>();
        AtomicAction gettobox = new MoveSurelyAction(new CoordinatesPair(3, 10));
        ArrayList<Node> fdf = BestFirstSearch.AStar(start, gettobox);
        /*// Get a box to operate on
        List<CoordinatesPair> boxCoords = start.getBoxCellCoords();
        List<Box> boxes = new ArrayList<>();
        for (CoordinatesPair coordinate : boxCoords){
            boxes.add((Box) start.getCellAtCoords(coordinate).getEntity());
        }
        List<Action> actions = new ArrayList<>();
        for (Box box : boxes){
            // do some stuff for ALL boxes in the level
            actions.add(new ClearPathToBox(box));
            actions.add(new GetToBoxAction(box));
            actions.add(new MoveBoxAction(box, new CoordinatesPair(3, 10)));

        }
        List<Node> path = getTotalPath(actions, start);

        *//*Box box = boxes.get(8); //take U box in list of coords

        List<Action> actionsToPerform = new ArrayList<>();
        // Define some actions
        Action clearPathToBox = new ClearPathToBox(box);
        Action getToBoxAction = new GetToBoxAction(box);
        Action moveBoxAction = new MoveBoxAction(box, new CoordinatesPair(3, 10));
        actionsToPerform.add(clearPathToBox);
        actionsToPerform.add(getToBoxAction);
        actionsToPerform.add(moveBoxAction);

        List<Node> path = new ArrayList<>();

        path = getTotalPath(actionsToPerform, start);*//*
        System.out.println("Printing total shortest path");
        for (int i = path.size() - 1; i >= 0; i--) {
            if (path.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(path.get(i).getAction().toString());
                //System.out.println(totalShortestPath.get(i));
            }
        }*/



//        ArrayList<Node> stufftest = BestFirstSearch.AStar(start, clearPathToBox);
//        for (int i = stufftest.size() - 1; i >= 0; i--) {
//            if (stufftest.get(i).getAction() != null) { // if the action is null, this is a start node
//                System.out.println(stufftest.get(i).getAction().toString());
//                System.out.println(stufftest.get(i));
//            }
//        }
//
//
//        System.out.println(getToBoxAction.getType());
//        ArrayList<Node> testshortpathtobox = BestFirstSearch.AStar(start, getToBoxAction);
//        for (int i = testshortpathtobox.size() - 1; i >= 0; i--) {
//            if (testshortpathtobox.get(i).getAction() != null) { // if the action is null, this is a start node
//                System.out.println(testshortpathtobox.get(i).getAction().toString());
//                System.out.println(testshortpathtobox.get(i));
//            }
//        }
//
//        ArrayList<Node> last = BestFirstSearch.AStar(start, moveBoxAction);
//        for (int i = last.size() -1; i >= 0; i--){
//            if (last.get(i).getAction() != null){
//                System.out.println(last.get(i).getAction().toString());
//                System.out.println(last.get(i));
//            }
//        }





    }

    /*static List<Node> getTotalPath(List<Action> actionsToPerform, Node node){
        List<Node> path = new ArrayList<>();
        Node curNode = node;
        for (Action action : actionsToPerform){
            ArrayList<Node> tempPath = BestFirstSearch.AStar(curNode, action);
            for (int i = tempPath.size() - 1; i >= 0; i--){
                path.add(0,tempPath.get(i)); // add to start of list
            }
            tempPath.clear();
            if (path.size() > 0) { // if path.size()==0, it means action had already been completed, so do nothing
                curNode = path.get(0); // we are at last element of the totpath
            }
            System.out.println(curNode);
        }
        return path;
    }*/


}