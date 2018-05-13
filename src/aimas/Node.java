package aimas; /**
 * Created by aleksandrs on 4/14/18.
 */


import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.Type;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Color;
import aimas.board.entities.Entity;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Class for storing current node in the state space
 * Stores level state and keeps track of number of nodes generated
 *
 * Also contains getNeighbourNodes method inspired by the Warm-Up Assignment
 */
public class Node {
    /*public static void main(String[] args) {
        ArrayList<ArrayList<Cell>> level = new ArrayList<>();
        try {
            level = LevelReader.getLevel("testNode.lvl");
        } catch (IOException e) {
            System.out.println("########");
            System.out.println("Probably incorrect path.");
        }
        ArrayList<Cell.CoordinatesPair> agentCellCoords = LevelReader.getAgentCellCoords();
        ArrayList<Cell.CoordinatesPair> boxCellCoords = LevelReader.getBoxCellCoords();
        ArrayList<Cell.CoordinatesPair> goalCellCoords = LevelReader.getGoalCellCoords();

        Node node = new Node(null);
        ArrayList<ArrayList<Cell>> newLevel = Node.copyLevel(level);
        System.out.println("NEW level");
        System.out.println(newLevel);
        //Node node = new Node(null);
        node.setLevel(level);
        node.setAgentCellCoords(agentCellCoords);
        node.setBoxCellCoords(boxCellCoords);
        node.setGoalCellCoords(goalCellCoords);
        //Node node2 = new Node(null);
        //node2.setLevel(level);
        //boolean equals = node.equals(node2);

        System.out.println(node);

        ArrayList<Node> testArrayOfNodes = node.getNeighbourNodes(0);
        for (int i = 0; i < testArrayOfNodes.size(); i++){
            System.out.println("Node " + i);
            System.out.println(testArrayOfNodes.get(i));
        }
        System.out.println(node.getAgents());
        for (Node thisNode : testArrayOfNodes){
            if (thisNode.isSatisfied('a')){
                System.out.println("this node is satisfied");
                Node satisfiedNode = thisNode;
                boolean test = true;
            }
        }
        //boolean test = true;

    }*/

    private Node parent;
    private ArrayList<ArrayList<Cell>> level;
    public Command action; // command which let to this node
    // Each node has its own copy of the following data structures as they change for every state
    // (at least the agent)
    public ArrayList<CoordinatesPair> boxCellCoords = new ArrayList<>();
    public ArrayList<CoordinatesPair> agentCellCoords = new ArrayList<>();
    // And one goalCellCoords is shared by all the nodes - goals do not move from state to state
    private static ArrayList<CoordinatesPair> goalCellCoords = new ArrayList<>();
    private static ArrayList<CoordinatesPair> tunnelCellCoords = new ArrayList<>();

    public Box getBoxBeingMoved() {
        return boxBeingMoved;
    }

    public Box boxBeingMoved; // if this node is a result of a Push/Pull action, keep track of which box was moved

    private int agentNumber; // keep track of who is working with this node

    public int getAgentNumber() {
        return agentNumber;
    }

    public static int nodeCount; // total amount of nodes generated

    public int gScore; // for A*
    public int fScore; // for A*

    public Node(Node parent) {
        this.parent = parent;
        if (parent == null){ // g of start node is 0
            this.gScore = 0;
        }
        else {
            this.gScore = Integer.MAX_VALUE; // as node is unknown when generated
        }
        this.fScore = Integer.MAX_VALUE; // same as above
        nodeCount++;
    }
    public void setParent(Node parent){
        this.parent = parent;
    }
    public Node getParent(){
        return this.parent;
    }
    public ArrayList<Agent> getAgents(){
        ArrayList<Agent> agents = new ArrayList<>();
        ArrayList<ArrayList<Cell>> level = this.level;
        for (ArrayList<Cell> row : level){
            for (Cell cell: row){
                Entity entity = cell.getEntity();
                if (entity instanceof Agent){
                    agents.add((Agent) entity);
                }
            }
        }
        return agents;
    }
    // Check if there is a goal with this letter somewhere in the level
    public boolean isInLevel(char goalLetter){
        for (ArrayList<Cell> row : this.level){
            for (Cell cell : row){
                if (cell.getGoalLetter() == goalLetter){
                    return true;
                }
            }
        }
        return false;
    }

    private void setAction(Command action){
        this.action = action;
    }

    public Command getAction(){
        return this.action;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public ArrayList<ArrayList<Cell>> getLevel() {
        return level;
    }

    public void setLevel(ArrayList<ArrayList<Cell>> level) {
        this.level = level;
    }

    public ArrayList<CoordinatesPair> getAgentCellCoords(){
        return agentCellCoords;
    }

    public ArrayList<CoordinatesPair> getBoxCellCoords(){
        return boxCellCoords;
    }

    public static ArrayList<CoordinatesPair> getGoalCellCoords() {
        return goalCellCoords;
    }

    ArrayList<CoordinatesPair> getTunnelCellCoords(){
        return tunnelCellCoords;
    }

    public void setAgentCellCoords(ArrayList<CoordinatesPair> agentCellCoords){
        this.agentCellCoords = agentCellCoords;
    }
    public void setBoxCellCoords(ArrayList<CoordinatesPair> boxCellCoords){
        this.boxCellCoords = boxCellCoords;
    }

    public void setGoalCellCoords(ArrayList<CoordinatesPair> goalCellCoords){
        this.goalCellCoords = goalCellCoords;
    }

    public void setTunnelCellCoords(ArrayList<CoordinatesPair> tunnelCellCoords){
        this.tunnelCellCoords = tunnelCellCoords;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    // Return cell of this node's level with the specific coordinates
    public Cell getCellAtCoords(CoordinatesPair coordinatesPair){
        return level.get(coordinatesPair.getX()).get(coordinatesPair.getY());
    }

    /**
     * Essentially a remake of getExpandedNodes() from Warm-Up assignment
     * @param agentNumber Number of agent movements of whom we consider
     * @return List of nodes available by making any legal move by the agent
     */
    public ArrayList<Node> getNeighbourNodes(int agentNumber){
        ArrayList<Node> neighbourNodes = new ArrayList<>(Command.EVERY.length);

        // Neighbour nodes are nodes which can result from movements of the agent from whose
        // point of view we are looking

        // Find the agent in the current node
        int curAgentRow = 0;
        int curAgentCol = 0;
        CoordinatesPair curAgentCellCoords = null;
        Agent curAgent = null;
        Color curAgentColor = null;
        ArrayList<CoordinatesPair> agentCellCoords = this.getAgentCellCoords();
        for (CoordinatesPair thisCellCoords: agentCellCoords){
            curAgent = (Agent) getCellAtCoords(thisCellCoords).getEntity();
            if (curAgent.getNumber() == agentNumber){
                curAgentRow = thisCellCoords.getX();
                curAgentCol = thisCellCoords.getY();
                curAgentCellCoords = thisCellCoords;
                curAgentColor = curAgent.getColor();
                break; // found our agent
            }
        }
        for (Command c: Command.EVERY){
            int newAgentRow = curAgentRow + Command.dirToRowChange(c.dir1);
            int newAgentCol = curAgentCol + Command.dirToColChange(c.dir1);
            if (c.actionCommandType == Command.CommandType.Move) {
                if (this.cellIsFree(newAgentRow, newAgentCol)) {
                    Node n = new Node(this); // create a child node with curr node as the parent
                    n.action = c; // action c led us to the new node
                    n.agentNumber = agentNumber;
                    ArrayList<ArrayList<Cell>> updatedLevel = copyLevel(this.getLevel());

                    updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(null);
                    updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
                    // Add level to the new node
                    n.setLevel(updatedLevel);
                    n.agentCellCoords = copyList(getAgentCellCoords()); // containers are now also in new node
                    n.boxCellCoords = copyList(getBoxCellCoords());
                    // And add the node to the list of neighbors
                    neighbourNodes.add(n);

                    CoordinatesPair updatedAgentCellCoords = new CoordinatesPair(newAgentRow, newAgentCol);
                    n.getAgentCellCoords().remove(curAgentCellCoords);
                    // Only add coords if not already in the list
                    if (!n.getAgentCellCoords().contains(updatedAgentCellCoords)) {
                        n.getAgentCellCoords().add(updatedAgentCellCoords);
                    }

                }
            }
            else if (c.actionCommandType == Command.CommandType.Push){
                // Make sure there is a box to move
                if (this.boxAt(newAgentRow, newAgentCol)){
                    int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                    int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
                    Cell curBoxCell = this.getLevel().get(newAgentRow).get(newAgentCol);
                    CoordinatesPair curBoxCellCoords = new CoordinatesPair(curBoxCell);
                    // And that new cell of the box is free
                    if (this.cellIsFree(newBoxRow, newBoxCol)){
                        // And check whether colors of box and agent match
                        Box curBox = (Box) this.getLevel().get(newAgentRow).get(newAgentCol).getEntity();
                        if (!(curBox.getColor() == curAgentColor)){
                            continue; // if colors do not match, go for next action
                        }
                        Node n = new Node(this);
                        n.action = c;
                        n.agentNumber = agentNumber;
                        // Update level
                        /*ArrayList<ArrayList<Cell>> updatedLevel =
                                (ArrayList<ArrayList<Cell>>) this.getLevel().clone();*/
                        ArrayList<ArrayList<Cell>> updatedLevel = copyLevel(this.getLevel());
                        // Agent moves to his new location
                        updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(null);
                        updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
                        // Box moves to its new location
                        updatedLevel.get(newBoxRow).get(newBoxCol).setEntity(curBox);
                        n.boxBeingMoved = curBox; // keep track of box being moved
                        //curBox.setCoordinates(new CoordinatesPair(newBoxRow, newBoxCol));
                        // (no need to remove it from previous location as it is done when agent is moved)
                        n.setLevel(updatedLevel); // set new level to new node
                        n.agentCellCoords = copyList(getAgentCellCoords()); // give current agent cell coords to new node
                        n.boxCellCoords = copyList(getBoxCellCoords()); // ..same with box cell coords
                        // Add the node to the list of neighbours
                        neighbourNodes.add(n);

                        // Update agent and box cell containers
                        CoordinatesPair updatedAgentCellCoords =
                                new CoordinatesPair(newAgentRow, newAgentCol);
                        n.getAgentCellCoords().remove(curAgentCellCoords); // remove cur cell coord from new node
                        if (!n.getAgentCellCoords().contains(updatedAgentCellCoords)) {
                            n.getAgentCellCoords().add(updatedAgentCellCoords); // add updated cell coord to new node
                        }
                        CoordinatesPair updatedBoxCellCoords =
                                new CoordinatesPair(newBoxRow, newBoxCol);
                        n.getBoxCellCoords().remove(curBoxCellCoords);
                        if (!n.getBoxCellCoords().contains(updatedBoxCellCoords)) {
                            n.getBoxCellCoords().add(updatedBoxCellCoords);
                        }
                    }
                }
            }
            else if (c.actionCommandType == Command.CommandType.Pull){
                // Cell is free where the agent is going
                if (this.cellIsFree(newAgentRow, newAgentCol)){
                    int boxRow = curAgentRow + Command.dirToRowChange(c.dir2);
                    int boxCol = curAgentCol + Command.dirToColChange(c.dir2);
                    // and there is a box in dir2 of the agent
                    if (this.boxAt(boxRow, boxCol)){
                        // And check whether color of box and agent match
                        Box curBox = (Box) this.getLevel().get(boxRow).get(boxCol).getEntity();
                        if (!(curBox.getColor() == curAgentColor)){
                            continue; // if colors do not match, go for next action
                        }
                        Cell curBoxCell = this.getLevel().get(boxRow).get(boxCol);
                        CoordinatesPair curBoxCellCoords = new CoordinatesPair(curBoxCell);
                        Node n = new Node(this);
                        n.action = c;
                        n.agentNumber = agentNumber;
                        // Update level
                        /*ArrayList<ArrayList<Cell>> updatedLevel =
                                (ArrayList<ArrayList<Cell>>) this.getLevel().clone();*/
                        ArrayList<ArrayList<Cell>> updatedLevel = copyLevel(this.getLevel());
                        // Box moves to its new location (which is current agent location in this case)
                        //curBox.setCoordinates(new CoordinatesPair(curAgentRow, curAgentCol));
                        updatedLevel.get(boxRow).get(boxCol).setEntity(null);
                        updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(curBox);
                        n.boxBeingMoved = curBox; // keep track of box being moved
                        // Agent moves to his new location
                        // (no need to remove him from the previous location as it is done when the box is moved)
                        updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
                        n.setLevel(updatedLevel);
                        n.agentCellCoords = copyList(getAgentCellCoords()); // give current agent cell coords to new node
                        n.boxCellCoords = copyList(getBoxCellCoords()); // ..same with box cell coords
                        // Add the node to the list of neighbours
                        neighbourNodes.add(n);

                        // Update agent and box cell containers
                        CoordinatesPair updatedAgentCellCoords =
                                new CoordinatesPair(newAgentRow, newAgentCol);
                        n.getAgentCellCoords().remove(curAgentCellCoords);
                        if (!n.getAgentCellCoords().contains(updatedAgentCellCoords)) {
                            n.getAgentCellCoords().add(updatedAgentCellCoords);
                        }

                        CoordinatesPair updatedBoxCellCoords =
                                new CoordinatesPair(curAgentRow, curAgentCol);
                        n.getBoxCellCoords().remove(curBoxCellCoords);
                        if (!n.getBoxCellCoords().contains(updatedBoxCellCoords)) {
                            n.getBoxCellCoords().add(updatedBoxCellCoords);
                        }
                    }
                }
            }

        }
        //Collections.shuffle(neighbourNodes, RND);
        return neighbourNodes;
    }
    private static final Random RND = new Random(2);

    public boolean cellIsFree(int row, int col){
        if (this.getLevel().get(row).get(col).getType().equals(Type.WALL)
                || this.getLevel().get(row).get(col).getEntity() != null){
            return false;
        }
        return true;
    }

    public boolean boxAt(int row, int col){
        if (this.getLevel().get(row).get(col).getEntity() instanceof Box){
            return true;
        }
        return false;
    }

    char getGoalLetter(int row, int col){
        return this.getLevel().get(row).get(col).getGoalLetter();
    }

    // Check if the goal with the given character is satisfied at this node
    public boolean isSatisfied(char goalLetter){
        ArrayList<CoordinatesPair> goalCoords = Node.getGoalCellCoords();
        ArrayList<Cell> goalCells = new ArrayList<>();
        for (CoordinatesPair coordinatesPair : goalCoords){
            goalCells.add(getCellAtCoords(coordinatesPair));
        }
        for (Cell cell : goalCells){
            if (cell.getGoalLetter() == goalLetter && cell.getEntity() instanceof Box){
                Box box = (Box) cell.getEntity();
                if (Character.toLowerCase(box.getLetter()) == goalLetter){
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Node> extractPlan(){
        ArrayList<Node> plan = new ArrayList<>();
        Node n = this;
        while (n.parent!=null){
            plan.add(n);
            n = n.parent;
        }
        return plan;
    }

    /**
     * Use this to copy a level - else cells of a new level refer to the cells of the original
     * ... lovely OOP and Java in particular
     * @param oldLevel Old ArrayList<ArrayList<Cell>> representing a level
     * @return New level just like the old one
     */
    public static ArrayList<ArrayList<Cell>> copyLevel(ArrayList<ArrayList<Cell>> oldLevel){
        ArrayList<ArrayList<Cell>> newLevel = new ArrayList<>();
        for (ArrayList<Cell> arrayList : oldLevel){
            ArrayList<Cell> newRow = new ArrayList<>();
            for (Cell cell: arrayList){
                Cell newCell = new Cell(cell);
                newRow.add(newCell);
            }
            newLevel.add(newRow);
        }
        return newLevel;
    }

    /**
     * Same as above for an arrayList
     * @param oldList Old ArrayList<Cell.CoordinatesPair> representing a list of coords
     * @return Cloned list
     */
    public static ArrayList<CoordinatesPair> copyList(ArrayList<CoordinatesPair> oldList){
        ArrayList<CoordinatesPair> clonedList = new ArrayList<>(oldList.size());
        for (CoordinatesPair coordinatesPair : oldList){
            clonedList.add(new CoordinatesPair(coordinatesPair));
        }
        return clonedList;
    }

    @Override
    public boolean equals(Object o){ // if agents and boxes are in same location, consider two nodes as same state
        if (o == this){
            return true;
        }
        if (o instanceof Node){
            Node p = (Node) o;
            return getAgentCellCoords().equals(p.getAgentCellCoords()) &&
                    getBoxCellCoords().equals(p.getBoxCellCoords());
            //return getLevel().equals(p.getLevel()); //<- this is super performance heavy so don't use it!
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxCellCoords, agentCellCoords);
    }

    @Override
    public String toString(){
        return getLevelString(level);
    }

    /**
     * Prints the level in a specified format
     * @param level Level stored in the current node
     * @return Prints the goal letter if the cell is a goal, the entity name if cell is occupied by one,
     * or the cell type (space/wall) is none of the above holds
     */
    private String getLevelString(ArrayList<ArrayList<Cell>> level){
        StringBuilder stringBuilder = new StringBuilder();
        for (ArrayList<Cell> row: level){
            for (Cell cell: row){
                //System.err.print(cell + " ");
                if (cell.getEntity() != null){
                    stringBuilder.append(cell.getEntity()).append(" ");
                }
                else if(cell.isGoal()){
                    stringBuilder.append(cell.getGoalLetter()).append(" ");
                }
                else{
                    stringBuilder.append(cell.getType().equals(Type.WALL) ? '+' : ' ').append(" ");
                }
                //stringBuilder.append(cell.getType()).append(" ");
            }
            //System.err.println();
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
