package aimas.aiutils;

import aimas.*;
import aimas.actions.AtomicAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.board.CoordinatesPair;
import aimas.board.entities.Box;

import java.io.IOException;
import java.util.*;

/**
 * Created by aleksandrs on 4/14/18.
 */

/**
 * TODO: 58000 at SAmicromouseBoxAtStart.lvl - improve performance
 */
public class BestFirstSearch {

    public static void main(String[] args){
        Node start = new Node(null);
        try {
            start.setLevel(LevelReader.getLevel("testNode.lvl"));
        } catch (IOException e) {
            System.out.println("########");
            System.out.println("Probably incorrect path");
        }
        start.setBoxCellCoords(Node.copyList(LevelReader.getBoxCellCoords()));
        start.setAgentCellCoords(Node.copyList(LevelReader.getAgentCellCoords()));
        start.setGoalCellCoords(Node.copyList(LevelReader.getGoalCellCoords()));
        BestFirstSearch bfs = new BestFirstSearch();
        int h = bfs.heuristic(start, 'a');
        double startTime = System.nanoTime();
        ArrayList<Node> shortestPath = AStar(start, 'a');
        double timeElapsed = (System.nanoTime() - startTime) / 1000000000.0;
        //boolean solved = new BestFirstSearch().aStar(start,'a');
        System.out.println("Printing shortest path");
        for (int i = shortestPath.size()-1; i >= 0; i--) {
            if (shortestPath.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(shortestPath.get(i).getAction());
            }
        }
        System.out.println("the time elapsed: " + timeElapsed);
       /* for (Node pathNode : shortestPath){
            if (pathNode.getAction() != null) { // this is start node if action is null
                System.out.println(pathNode.getAction().toString());
            }
        }*/
        boolean test = true;
    }

    /**
     * A* function. Returns a list containing Nodes in the shortest path order.
     * @param startState Node where we are at the moment
     * @param goalToSatisfy Letter of the goal we would like to satisfy
     * @return ArrayList with Nodes of the shortest path order.
     */
    static ArrayList<Node> AStar(Node startState, char goalToSatisfy){
        if (!startState.isInLevel(goalToSatisfy)){
            System.err.println("The goal " + goalToSatisfy + " does not exist in the level.");
            System.err.println("A* quitting.");
            return new ArrayList<>();
        }
        startState.gScore = 0; // need this line to "chain" A* calls. New start state - new gScore.
        startState.setParent(null); // same as above. New start state's parent must be null in current implementation.
        ArrayList<Node> shortestPath = new ArrayList<>();
        HashSet<Node> visited = new HashSet<>();
        HashSet<Node> frontier = new HashSet<>();
        frontier.add(startState); // start node is the only one known initially
        startState.fScore = heuristic(startState, goalToSatisfy);
        Node currentNode;
        while (!frontier.isEmpty()){
            currentNode = getLowestFNode(frontier);
            //System.out.println("Going here: ");
            //System.out.println(currentNode.getAction()); //<< uncomment this to see steps taken while executing
            //System.out.println(currentNode);
            //System.err.println(Node.nodeCount); // debug
            if (currentNode.isSatisfied(goalToSatisfy)){
                System.err.println("satisfied h yeah");
                shortestPath = reconstructPath(currentNode);
                return shortestPath;
            }
            frontier.remove(currentNode);
            visited.add(currentNode);

//            ArrayList<Node> curNeighbours = currentNode.getNeighbourNodes(0);
            for (Node neighbour : currentNode.getNeighbourNodes(0)){
                if (visited.contains(neighbour)){ // Ignore nodes already evaluated
                    continue;
                }
                if (!frontier.contains(neighbour)){ // Discover a new node
                    frontier.add(neighbour);
                }
                // Distance from start to a neighbour
                int tentativeG = currentNode.gScore + 1; // neighbour is always 1 node away
                if (tentativeG >= neighbour.gScore){
                    continue;
                }
                neighbour.setParent(currentNode); // better path
                neighbour.gScore = tentativeG;
                neighbour.fScore = neighbour.gScore + heuristic(neighbour, goalToSatisfy);
            }

        }
        return shortestPath;
    }


    private static ArrayList<Node> reconstructPath(Node currentNode){
        return currentNode.extractPlan();
    }

    // Returns the Node with the lowest F value in frontier
    private static Node getLowestFNode(HashSet<Node> frontier){

        Node lowestFValueNode = frontier.iterator().next(); // get some Node from frontier
        for (Node node : frontier){
            if (node.fScore < lowestFValueNode.fScore){
                lowestFValueNode = node;
            }
        }
        return lowestFValueNode;
    }

    private static int heuristic(Node start, char goalToSatisfy){
        CoordinatesPair agentCellCoords = start.getAgentCellCoords().get(0); // just take the only agent for now
        int agentRow = agentCellCoords.getX();
        int agentCol = agentCellCoords.getY();

        int boxRow = 0;
        int boxCol = 0;
        ArrayList<CoordinatesPair> boxCellCoords = start.getBoxCellCoords();
        for (CoordinatesPair coordinatesPair : boxCellCoords){
            if (start.getCellAtCoords(coordinatesPair).getEntity() instanceof Box){
                Box box = (Box) start.getCellAtCoords(coordinatesPair).getEntity();
                if (box.getLetter() == Character.toUpperCase(goalToSatisfy)){
                    boxRow = coordinatesPair.getX();
                    boxCol = coordinatesPair.getY();
                    break;
                }
            }
        }
        int goalRow = 0;
        int goalCol = 0;
        ArrayList<CoordinatesPair> goalCellCoords = Node.getGoalCellCoords();
        for (CoordinatesPair coordinatesPair : goalCellCoords){
            if (start.getCellAtCoords(coordinatesPair).getGoalLetter() == goalToSatisfy){
                goalRow = coordinatesPair.getX();
                goalCol = coordinatesPair.getY();
                break;
            }
        }
        // Now we have coords of agent, box and goal we try to satisfy. Let's roll.
        int fromBoxToGoal = manhDist(boxRow, boxCol, goalRow, goalCol);
        int fromAgentToBox = manhDist(agentRow, agentCol, boxRow, boxCol);
        int h = fromBoxToGoal + fromAgentToBox;

        if (fromAgentToBox > 1){
            h += 10; // punish agent from going away from the box
        }
        // int fromAgentToGoal = manhDist(agentRow, agentCol, goalRow, goalCol);
//        System.out.println("Agent to box: " + fromAgentToBox);
//        System.out.println("Box to goal: " + fromBoxToGoal);
//        System.out.println();

        return h; //fromBoxToGoal + fromAgentToBox;
    }

    // From Warm-Up assignment
    public static int manhDist(int i1, int j1, int i2, int j2){ // between cells of a map, not between state nodes
        int diffI = Math.abs(i1 - i2);
        int diffJ = Math.abs(j1 - j2);
        return diffI + diffJ;
    }

    /** _____________________Overloaded A* methods______________________*/

    static ArrayList<Node> AStar(Node startState, Node finishState){
        startState.gScore = 0; // need this line to "chain" A* calls. New start state - new gScore.
        startState.setParent(null); // same as above. New start state's parent must be null in current implementation.
        ArrayList<Node> shortestPath = new ArrayList<>();
        HashSet<Node> visited = new HashSet<>();
        HashSet<Node> frontier = new HashSet<>();
        frontier.add(startState); // start node is the only one known initially
        startState.fScore = heuristic(startState);
        Node currentNode;
        while (!frontier.isEmpty()){
            currentNode = getLowestFNode(frontier);
            //System.out.println("Going here: ");
            //System.out.println(currentNode.getAction()); //<< uncomment this to see steps taken while executing
            //System.out.println(currentNode);
            //System.err.println(Node.nodeCount); // debug
            if (currentNode.equals(finishState)){
                System.out.println("satisfied h yeah");
                shortestPath = reconstructPath(currentNode);
                return shortestPath;
            }
            frontier.remove(currentNode);
            visited.add(currentNode);

//            ArrayList<Node> curNeighbours = currentNode.getNeighbourNodes(0);
            for (Node neighbour : currentNode.getNeighbourNodes(0)){
                if (visited.contains(neighbour)){ // Ignore nodes already evaluated
                    continue;
                }
                if (!frontier.contains(neighbour)){ // Discover a new node
                    frontier.add(neighbour);
                }
                // Distance from start to a neighbour
                int tentativeG = currentNode.gScore + 1; // neighbour is always 1 node away
                if (tentativeG >= neighbour.gScore){
                    continue;
                }
                neighbour.setParent(currentNode); // better path
                neighbour.gScore = tentativeG;
                neighbour.fScore = neighbour.gScore + heuristic(neighbour);
            }

        }
        return shortestPath;
    }
    // Implement this depending on the method
    private static int heuristic(Node start){
        return new Random().nextInt(10);
    }

    // A* which calculates path depending on the action. Each action has different implementation of heuristic
    // and isAchieved methods
    public static ArrayList<Node> AStar(Node startState, AtomicAction action){
        startState.gScore = 0; // need this line to "chain" A* calls. New start state - new gScore.
        startState.setParent(null); // same as above. New start state's parent must be null in current implementation.
        ArrayList<Node> shortestPath = new ArrayList<>();
        HashSet<Node> visited = new HashSet<>();
        HashSet<Node> frontier = new HashSet<>();
        frontier.add(startState); // start node is the only one known initially
        startState.fScore = action.heuristic(startState);
        Node currentNode;
        while (!frontier.isEmpty()){
            currentNode = getLowestFNode(frontier);
            //System.out.println("Going here: ");
            //System.out.println(currentNode.getAction()); //<< uncomment this to see steps taken while executing
            //System.out.println(currentNode);
           // System.err.println(Node.nodeCount); // debug
            if (action.isAchieved(currentNode)){
                //  System.out.println("satisfied h yeah");
                System.err.println(action.toString() + " is satisfied.");
                shortestPath = reconstructPath(currentNode);
                return shortestPath;
            }
            frontier.remove(currentNode);
            visited.add(currentNode);

//            ArrayList<Node> curNeighbours = currentNode.getNeighbourNodes(0);
            for (Node neighbour : currentNode.getNeighbourNodes(0)){
                if (visited.contains(neighbour)){ // Ignore nodes already evaluated
                    continue;
                }
//                if (action instanceof MoveSurelyAction && neighbour.getBoxBeingMoved() != null){
//                    continue; // this is an idea to completely shutdown attempts to move a box while "moving surely"
//                }
                if (!frontier.contains(neighbour)){ // Discover a new node
                    frontier.add(neighbour);
                }
                // Distance from start to a neighbour
                int tentativeG = currentNode.gScore + 1; // neighbour is always 1 node away
                if (tentativeG >= neighbour.gScore){
                    continue;
                }
                neighbour.setParent(currentNode); // better path
                neighbour.gScore = tentativeG;
                neighbour.fScore = neighbour.gScore + action.heuristic(neighbour);
            }

        }
        return shortestPath;
    }


}