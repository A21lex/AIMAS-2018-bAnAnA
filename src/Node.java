/**
 * Created by aleksandrs on 4/14/18.
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class for storing current node in the state space
 * Stores level state and keeps track of number of nodes generated
 *
 * Also contains getNeighbourNodes method inspired by the Warm-Up Assignment
 */
public class Node {
    public static void main(String[] args) {
        ArrayList<ArrayList<Cell>> level = new ArrayList<>();
        LevelReader lr = new LevelReader();
        try {
            level = lr.getLevel("testNode.lvl");
        } catch (IOException e) {
            System.out.println("########");
            System.out.println("Probably incorrect path.");
        }
        ArrayList<Cell.CoordinatesPair> agentCellCoords = LevelReader.getAgentCellCorrds();
        ArrayList<Cell.CoordinatesPair> boxCellCoords = LevelReader.getBoxCellCoords();
        Node node = new Node(null);
        ArrayList<ArrayList<Cell>> newLevel = node.copyLevel(level);
        System.out.println("NEW level");
        System.out.println(newLevel);
        //Node node = new Node(null);
        node.setLevel(level);
        node.setAgentCellCoords(agentCellCoords);
        node.setBoxCellCoords(boxCellCoords);

        //Node node2 = new Node(null);
        //node2.setLevel(level);
        //boolean equals = node.equals(node2);

        System.out.println(node);

        ArrayList<Node> testArrayOfNodes = node.getNeighbourNodes(0);
        System.out.println(testArrayOfNodes);
        System.out.println(node.getAgents());
        boolean test = true;

    }
    private Node parent;
    private ArrayList<ArrayList<Cell>> level;
    private int g; // length of the path from the start node to the current node
    private Command action; // command which let to this node
    // Each node has its own copy of the following data structures as they change for every state
    // (at least for agent)...
    private ArrayList<Cell.CoordinatesPair> boxCellCoords = new ArrayList<>();
    private ArrayList<Cell.CoordinatesPair> agentCellCoords = new ArrayList<>();


    private static int nodeCount; // total amount of nodes generated

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

    public Node(Node parent) {
        this.parent = parent;
        if (parent == null){
            this.g = 0;  // this is initial node
        }
        else {
            this.g = parent.g() + 1;
        }
        nodeCount++;
    }

    public int g(){
        return this.g;
    }

    public void setG(int g){
        this.g = g;
    }

    public Command c(){
        return this.action;
    }

    public void setAction(Command action){
        this.action = action;
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

    public ArrayList<Cell.CoordinatesPair> getAgentCellCoords(){
        return agentCellCoords;
    }

    public ArrayList<Cell.CoordinatesPair> getBoxCellCoords(){
        return boxCellCoords;
    }

    public void setAgentCellCoords(ArrayList<Cell.CoordinatesPair> agentCellCoords){
        this.agentCellCoords = agentCellCoords;
    }
    public void setBoxCellCoords(ArrayList<Cell.CoordinatesPair> boxCellCoords){
        this.boxCellCoords = boxCellCoords;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    // Return cell of this node's level with the specific coordinates
    public Cell getCellAtCoords(Cell.CoordinatesPair coordinatesPair){
        return level.get(coordinatesPair.getX()).get(coordinatesPair.getY());
    }

    /**
     * Essentially a remake of getExpandedNodes() from Warm-Up assignment
     * @param agentNumber Number of agent movements of whom we consider
     * @return List of nodes available by making any legal move by the agent
     */
    ArrayList<Node> getNeighbourNodes(int agentNumber){
        ArrayList<Node> neighbourNodes = new ArrayList<>(Command.EVERY.length);

        // Neighbour nodes are nodes which can result from movements of the agent from whose
        // point of view we are looking

        // Find the agent in the current node
        int curAgentRow = 0;
        int curAgentCol = 0;
        Cell.CoordinatesPair curAgentCellCoords = null;
        Agent curAgent = null;
        ArrayList<Cell.CoordinatesPair> agentCellCoords = this.getAgentCellCoords();
        for (Cell.CoordinatesPair thisCellCoords: agentCellCoords){
            curAgent = (Agent) getCellAtCoords(thisCellCoords).getEntity();
            if (curAgent.getNumber() == agentNumber){
                curAgentRow = thisCellCoords.getX();
                curAgentCol = thisCellCoords.getY();
                curAgentCellCoords = thisCellCoords;
                break; // found our agent
            }
        }
        for (Command c: Command.EVERY){
            int newAgentRow = curAgentRow + Command.dirToRowChange(c.dir1);
            int newAgentCol = curAgentCol + Command.dirToColChange(c.dir1);
            if (c.actionType == Command.Type.Move) {
                if (this.cellIsFree(newAgentRow, newAgentCol)) {
                    Node n = new Node(this); // create a child node with curr node as the parent
                    n.setAction(c); // action c led us to the new node

                    // Update level
                    /*ArrayList<ArrayList<Cell>> updatedLevel =
                            (ArrayList<ArrayList<Cell>>) this.getLevel().clone();*/
                    ArrayList<ArrayList<Cell>> updatedLevel = copyLevel(this.getLevel());

                    updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(null);
                    updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
                    // Add level to the new node
                    n.setLevel(updatedLevel);
                    n.agentCellCoords = copyList(getAgentCellCoords()); // containers are now also in new node
                    n.boxCellCoords = copyList(getBoxCellCoords());
                    // And add the node to the list of neighbors
                    neighbourNodes.add(n);


                    // Update agent cell container
                    char goalLetter =
                            getGoalLetter(newAgentRow, newAgentCol);
                    Cell.CoordinatesPair updatedAgentCellCoords = new Cell.CoordinatesPair(newAgentRow, newAgentCol);
                    n.getAgentCellCoords().remove(curAgentCellCoords);
                    // Only add coords if not already in the list
                    if (!n.getAgentCellCoords().contains(updatedAgentCellCoords))
                    n.getAgentCellCoords().add(updatedAgentCellCoords);
                    //LevelReader.getAgentCellCorrds().remove(curAgentCellCoords);
                    //LevelReader.getAgentCellCorrds().add(updatedAgentCellCoords);
                    // Update cur agent coords
                    //curAgentCellCoords = new Cell.CoordinatesPair(newAgentRow, newAgentCol);

                }
            }
            else if (c.actionType == Command.Type.Push){
                // Make sure there is a box to move
                if (this.boxAt(newAgentRow, newAgentCol)){
                    int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                    int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
                    Cell curBoxCell = this.getLevel().get(newAgentRow).get(newAgentCol);
                    Cell.CoordinatesPair curBoxCellCoords = new Cell.CoordinatesPair(curBoxCell);
                    // And that new cell of the box is free
                    if (this.cellIsFree(newBoxRow, newBoxCol)){
                        Node n = new Node(this);
                        n.setAction(c);
                        // Update level
                        /*ArrayList<ArrayList<Cell>> updatedLevel =
                                (ArrayList<ArrayList<Cell>>) this.getLevel().clone();*/
                        ArrayList<ArrayList<Cell>> updatedLevel = copyLevel(this.getLevel());
                        // Agent moves to his new location
                        updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(null);
                        updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
                        // Box moves to its new location
                        Box curBox = (Box) this.getLevel().get(newAgentRow).get(newAgentCol).getEntity();
                        updatedLevel.get(newBoxRow).get(newBoxCol).setEntity(curBox);
                        // (no need to remove it from previous location as it is done when agent is moved)
                        n.setLevel(updatedLevel); // set new level to new node
                        n.agentCellCoords = copyList(getAgentCellCoords()); // give current agent cell coords to new node
                        n.boxCellCoords = copyList(getBoxCellCoords()); // ..same with box cell coords
                        // Add the node to the list of neighbours
                        neighbourNodes.add(n);

                        // Update agent and box cell containers
                        char newAgentCellGoalLetter = getGoalLetter(newAgentRow, newAgentCol);
                        char newBoxCellGoalLetter = getGoalLetter(newBoxRow, newBoxCol);
                        Cell.CoordinatesPair updatedAgentCellCoords =
                                new Cell.CoordinatesPair(newAgentRow, newAgentCol);
                        n.getAgentCellCoords().remove(curAgentCellCoords); // remove cur cell coord from new node
                        if (!n.getAgentCellCoords().contains(updatedAgentCellCoords))
                        n.getAgentCellCoords().add(updatedAgentCellCoords); // add updated cell coord to new node
                        //LevelReader.getAgentCellCorrds().remove(curAgentCellCoords);
                        //LevelReader.getAgentCellCorrds().add(updatedAgentCellCoords);
                        Cell.CoordinatesPair updatedBoxCellCoords =
                                new Cell.CoordinatesPair(newBoxRow, newBoxCol);
                        n.getBoxCellCoords().remove(curBoxCellCoords);
                        if (!n.getBoxCellCoords().contains(updatedBoxCellCoords))
                        n.getBoxCellCoords().add(updatedBoxCellCoords);
                        //LevelReader.getBoxCellCoords().remove(curBoxCellCoords);
                        //LevelReader.getBoxCellCoords().add(updatedBoxCellCoords);
                        // Update cur agent coords
                        //curAgentCellCoords = new Cell.CoordinatesPair(newAgentRow, newAgentCol);
                    }
                }
            }
            else if (c.actionType == Command.Type.Pull){
                // Cell is free where the agent is going
                if (this.cellIsFree(newAgentRow, newAgentCol)){
                    int boxRow = curAgentRow + Command.dirToRowChange(c.dir2);
                    int boxCol = curAgentCol + Command.dirToColChange(c.dir2);
                    // and there is a box in dir2 of the agent
                    if (this.boxAt(boxRow, boxCol)){
                        Cell curBoxCell = this.getLevel().get(boxRow).get(boxCol);
                        Cell.CoordinatesPair curBoxCellCoords = new Cell.CoordinatesPair(curBoxCell);
                        Node n = new Node(this);
                        n.setAction(c);
                        // Update level
                        /*ArrayList<ArrayList<Cell>> updatedLevel =
                                (ArrayList<ArrayList<Cell>>) this.getLevel().clone();*/
                        ArrayList<ArrayList<Cell>> updatedLevel = copyLevel(this.getLevel());
                        // Box moves to its new location (which is current agent location in this case)
                        Box curBox = (Box) this.getLevel().get(boxRow).get(boxCol).getEntity();
                        updatedLevel.get(boxRow).get(boxCol).setEntity(null);
                        updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(curBox);
                        // Agent moves to his new location
                        // (no need to remove him from the previous location as it is done when the box is moved)
                        updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
                        n.setLevel(updatedLevel);
                        // THE LINES BELOW NEED TO BE FIXED AS IT COPIES REFERENCE, NOT NEW VALUES
                        n.agentCellCoords = copyList(getAgentCellCoords()); // give current agent cell coords to new node
                        n.boxCellCoords = copyList(getBoxCellCoords()); // ..same with box cell coords
                        // Add the node to the list of neighbours
                        neighbourNodes.add(n);

                        // Update agent and box cell containers
                        char newAgentCellGoalLetter = getGoalLetter(newAgentRow, newAgentCol);
                        char newBoxCellGoalLetter = getGoalLetter(curAgentRow, curAgentCol);
                        Cell.CoordinatesPair updatedAgentCellCoords =
                                new Cell.CoordinatesPair(newAgentRow, newAgentCol);
                        n.getAgentCellCoords().remove(curAgentCellCoords);
                        if (!n.getAgentCellCoords().contains(updatedAgentCellCoords))
                        n.getAgentCellCoords().add(updatedAgentCellCoords);
                        //LevelReader.getAgentCellCorrds().remove(curAgentCellCoords);
                        //LevelReader.getAgentCellCorrds().add(updatedAgentCellCoords);
                        Cell.CoordinatesPair updatedBoxCellCoords =
                                new Cell.CoordinatesPair(curAgentRow, curAgentCol);
                        n.getBoxCellCoords().remove(curBoxCellCoords);
                        if (!n.getBoxCellCoords().contains(updatedBoxCellCoords))
                        n.getBoxCellCoords().add(updatedBoxCellCoords);
                        //LevelReader.getBoxCellCoords().remove(curBoxCellCoords);
                        //LevelReader.getBoxCellCoords().add(updatedBoxCellCoords);
                        // Update cur agent coords
                        //curAgentCellCoords = new Cell.CoordinatesPair(newAgentRow, newAgentCol);
                    }
                }
            }

        }
        return neighbourNodes;
    }

    boolean cellIsFree(int row, int col){
        if (this.getLevel().get(row).get(col).getType().equals(Type.Wall)
                || this.getLevel().get(row).get(col).getEntity() != null){
            return false;
        }
        return true;
    }

    boolean boxAt(int row, int col){
        if (this.getLevel().get(row).get(col).getEntity() instanceof Box){
            return true;
        }
        return false;
    }

    char getGoalLetter(int row, int col){
        return this.getLevel().get(row).get(col).getGoalLetter();
    }

    // Check if the goal with the given character is satisfied at this node
    boolean isSatisfied(char goalLetter){
        // change the following to be static field of Node class instead, makes more sense
        ArrayList<Cell.CoordinatesPair> goalCoords = LevelReader.getGoalCellCorrds();
        ArrayList<Cell> goalCells = new ArrayList<>();
        for (Cell.CoordinatesPair coordinatesPair : goalCoords){
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

    /**
     * Use this to copy a level - else cells of a new level refer to the cells of the original
     * ... lovely OOP and Java in particular
     * @param oldLevel
     * @return New level just like the old one
     */
    private ArrayList<ArrayList<Cell>> copyLevel(ArrayList<ArrayList<Cell>> oldLevel){
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
     * @param oldList
     * @return Cloned list
     */
    public ArrayList<Cell.CoordinatesPair> copyList(ArrayList<Cell.CoordinatesPair> oldList){
        ArrayList<Cell.CoordinatesPair> clonedList = new ArrayList<>(oldList.size());
        for (Cell.CoordinatesPair coordinatesPair : oldList){
            clonedList.add(new Cell.CoordinatesPair(coordinatesPair));
        }
        return clonedList;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (o instanceof Node){
            Node p = (Node) o;
            return getLevel().equals(p.getLevel());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, level, g, action, boxCellCoords, agentCellCoords);
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
                //System.out.print(cell + " ");
                if(cell.isGoal()){
                    stringBuilder.append(cell.getGoalLetter()).append(" ");
                }
                else if (cell.getEntity() != null){
                    stringBuilder.append(cell.getEntity()).append(" ");
                }
                else{
                    stringBuilder.append(cell.getType()).append(" ");
                }
                //stringBuilder.append(cell.getType()).append(" ");
            }
            //System.out.println();
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
