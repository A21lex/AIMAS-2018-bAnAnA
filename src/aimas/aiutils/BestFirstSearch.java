package aimas.aiutils;

import aimas.*;
import aimas.actions.AtomicAction;
import aimas.board.entities.Agent;

import java.util.*;

public class BestFirstSearch {

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

    // From Warm-Up assignment
    public static int manhDist(int i1, int j1, int i2, int j2){ // between cells of a map, not between state nodes
        int diffI = Math.abs(i1 - i2);
        int diffJ = Math.abs(j1 - j2);
        return diffI + diffJ;
    }

    /** _____________________ A* method______________________*/

    // A* which calculates path depending on the action. Each action has different implementation of heuristic
    // and isAchieved methods
    public static ArrayList<Node> AStar(Node startState, AtomicAction action){
        startState.gScore = 0; // need this line to "chain" A* calls. New start state - new gScore.
        startState.setParent(null); // same as above. New start state's parent must be null.
        Agent agent = action.getAgent();
        ArrayList<Node> shortestPath = new ArrayList<>();
        HashSet<Node> visited = new HashSet<>();
        HashSet<Node> frontier = new HashSet<>();
        frontier.add(startState); // start node is the only one known initially
        startState.fScore = action.heuristic(startState);
        Node currentNode;
        while (!frontier.isEmpty()){
            currentNode = getLowestFNode(frontier);
            //System.err.println("Going here: ");
            //System.err.println(currentNode.getAction()); //<< uncomment this to see steps taken while executing
            //System.err.println(currentNode);
            //System.err.println(Node.nodeCount); // debug
            if (action.isAchieved(currentNode)){
                System.err.println(action.toString() + " is satisfied.");
                shortestPath = reconstructPath(currentNode);
                return shortestPath;
            }
            frontier.remove(currentNode);
            visited.add(currentNode);

            for (Node neighbour : currentNode.getNeighbourNodes(agent)){
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
                neighbour.fScore = neighbour.gScore + action.heuristic(neighbour);
            }
        }
        return shortestPath;
    }

}