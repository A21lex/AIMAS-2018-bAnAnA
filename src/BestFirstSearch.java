import java.io.IOException;
import java.util.*;

/**
 * Created by aleksandrs on 4/14/18.
 */

/**
 * TODO: make this work for our purpose (search through level, goal satisfiability, etc)
 */
public class BestFirstSearch {

    public static void main(String[] args){
        Node start = new Node(null);
        LevelReader lr = new LevelReader();
        try {
            start.setLevel(lr.getLevel("testNode.lvl"));
        } catch (IOException e) {
            System.out.println("########");
            System.out.println("Probably incorrect path");
        }
        start.setBoxCellCoords(start.copyList(LevelReader.getBoxCellCoords()));
        start.setAgentCellCoords(start.copyList(LevelReader.getAgentCellCoords()));
        start.setGoalCellCoords(start.copyList(LevelReader.getGoalCellCoords()));
        BestFirstSearch bfs = new BestFirstSearch();
        int h = bfs.heuristic(start, 'a');
        double startTime = System.nanoTime();
        ArrayList<Node> shortestPath = new BestFirstSearch().AStar(start, 'a');
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

    // A* function. Returns a list containing Nodes in the shortest path order.

    /**
     * A* function. Returns a list containing Nodes in the shortest path order.
     * @param startState Node where we are at the moment
     * @param goalToSatisfy Letter of the goal we would like to satisfy
     * @return ArrayList with Nodes of the shortest path order.
     */
    ArrayList<Node> AStar(Node startState, char goalToSatisfy){
        ArrayList<Node> shortestPath = new ArrayList<>();
       /* Comparator<Node> comparator = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return 0;
            }
        };*/
        // The set of nodes already evaluated
        HashSet<Node> visited = new HashSet<>();
        // The set of currently discovered nodes that are not evaluated yet
        HashSet<Node> frontier = new HashSet<>();
        frontier.add(startState); // start node is the only one known initially
        // For each node, which node it can be most efficiently be reached from
        // Will eventually contain the most efficient previous step.
        HashMap<Node, Node> cameFrom = new HashMap<>();
        // Each node has a g value - cost of getting from the start node to that node
        HashMap<Node, Integer> gScore = new HashMap<>();
        gScore.put(startState, 0); //it costs 0 t get from startnode to itself
        //getOrDefault(key, defaultValue) <- for default infinity values (thanks to Java 8!)
        // For each node, the total cost of getting from the start node to the goal
        // by passing by that node. For the first node it is completely heuristic.
        HashMap<Node, Integer> fScore = new HashMap<>();
        fScore.put(startState, heuristic(startState, goalToSatisfy));
        Node currentNode;
        int loopCounter = 0;
        while (!frontier.isEmpty()){
            loopCounter++;
            try
            {Thread.sleep(0);}
            catch (Exception e)
            {e.printStackTrace();}
            //System.out.println("While loop number " + loopCounter);
            currentNode = getLowestFNode(fScore);
            //System.out.println("Going here: ");
            //System.out.println(currentNode.getAction()); << uncomment this to see steps taken while executing
            if (currentNode.isSatisfied(goalToSatisfy)){
                System.out.println("satisfied h yeah");
                shortestPath = reconstructPath(cameFrom, currentNode);
                return shortestPath;
            }
            frontier.remove(currentNode);
            visited.add(currentNode);

            ArrayList<Node> curNeighbours = currentNode.getNeighbourNodes(0);
            for (Node neighbour : curNeighbours){
                if (visited.contains(neighbour)){ // Ignore nodes already evaluated
                    continue;
                }
                if (!frontier.contains(neighbour)){ // Discover a new node
                    frontier.add(neighbour);
                }
                // Distance from start to a neighbour
                int tentativeG = gScore.get(currentNode) + 1; // neighbour is always 1 node away
                if (tentativeG >= gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
                    continue;
                }
                cameFrom.put(neighbour, currentNode);
                gScore.put(neighbour,tentativeG);
                fScore.put(neighbour, (gScore.getOrDefault(neighbour, Integer.MAX_VALUE)
                        + heuristic(neighbour, goalToSatisfy)));
            }

        }
        return shortestPath;
    }

    static ArrayList<Node> reconstructPath(HashMap<Node, Node> cameFrom, Node currentNode){
        ArrayList<Node> totalPath = new ArrayList<>();
        totalPath.add(currentNode);
        while (cameFrom.keySet().contains(currentNode)){
            currentNode = cameFrom.get(currentNode);
            totalPath.add(currentNode);
        }
        return  totalPath;
    }

    // Returns the Node with the lowest F value
    private Node getLowestFNode(HashMap<Node, Integer> fScore){
        Node lowestFValueNode = (Node) fScore.keySet().toArray()[0]; // get some Node
        Integer lowestFValue = fScore.get(lowestFValueNode); // and its F value
        ArrayList<Node> bestOptionNodes = new ArrayList<>();
        for (Node thisNode : fScore.keySet()){
            if (fScore.get(thisNode) < lowestFValue){
                lowestFValue = fScore.get(thisNode);
            }
        }
        //System.out.println("lowest f = " + lowestFValue) ;
        for (Node thisNode: fScore.keySet()){
            if (Objects.equals(fScore.get(thisNode), lowestFValue)){
                //System.out.println("another node with lowest f value");
                //System.out.println(thisNode.getAction());
                bestOptionNodes.add(thisNode);
            }
        }
        //System.out.println("printing best moves ");
        //for (Node node: bestOptionNodes){
            //System.out.println(node.getAction());
        //}
        Collections.shuffle(bestOptionNodes);
        //System.out.println("returning random best move " + bestOptionNodes.get(0).getAction());
        return bestOptionNodes.get(0);

    }

    int heuristic(Node start, char goalToSatisfy){
        ArrayList<ArrayList<Cell>> level = start.getLevel();
        Cell.CoordinatesPair agentCellCoords = start.getAgentCellCoords().get(0); // just take the only agent for now
        Cell agentCell = start.getCellAtCoords(agentCellCoords);
        //Cell agentCell = LevelReader.getAgentCellCoords().get(0);
        int agentRow = agentCell.getI();
        int agentCol = agentCell.getJ();

        ArrayList<Cell.CoordinatesPair> boxCellCoords = start.getBoxCellCoords();
        ArrayList<Cell> boxCells = new ArrayList<>();
        for (Cell.CoordinatesPair coordinatesPair : boxCellCoords){
            boxCells.add(start.getCellAtCoords(coordinatesPair));
        }
        int boxRow = 0;
        int boxCol = 0;
        for (Cell cell: boxCells){
            if (cell.getEntity() instanceof Box){
                Box box = (Box) cell.getEntity();
                // let's try to satisfy given goal with any box that matches the goal
                if (box.getLetter()==Character.toUpperCase(goalToSatisfy)){
                    boxRow = cell.getI();
                    boxCol = cell.getJ();
                    break; // got our box
                }
            }
        }
        ArrayList<Cell.CoordinatesPair> goalCellCoords = LevelReader.getGoalCellCoords();
        ArrayList<Cell> goalCells = new ArrayList<>();
        for (Cell.CoordinatesPair coordinatesPair : goalCellCoords){
            goalCells.add(start.getCellAtCoords(coordinatesPair));
        }
        int goalRow = 0;
        int goalCol = 0;
        for (Cell cell: goalCells){
            if (cell.getGoalLetter()==goalToSatisfy){
                goalRow = cell.getI();
                goalCol = cell.getJ();
                break;
            }
        }
        // Now we have coords of agent, box and goal we try to satisfy. Let's roll.
        int fromBoxToGoal = manhDist(boxRow, boxCol, goalRow, goalCol);
        int fromAgentToBox = manhDist(agentRow, agentCol, boxRow, boxCol);
        int h = fromBoxToGoal + fromAgentToBox;

        return h;
    }

    // From Warm-Up assignment
    public int manhDist(int i1, int j1, int i2, int j2){ // between cells of a map, not between state nodes
        int diffI = Math.abs(i1 - i2);
        int diffJ = Math.abs(j1 - j2);
        return diffI + diffJ;
    }

}
