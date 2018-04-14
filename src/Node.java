/**
 * Created by aleksandrs on 4/14/18.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class for storing current node in the state space
 * Stores level state and keeps track of number of nodes generated
 */
public class Node {
    public static void main(String[] args) {
        ArrayList<ArrayList<Cell>> level = new ArrayList<>();
        LevelReader lr = new LevelReader();
        try {
            level = lr.getLevel("SAD1.lvl");
        } catch (IOException e) {
            System.out.println("########");
            System.out.println("Probably incorrect path.");
        }

        Node node = new Node(null);
        node.setLevel(level);
        Node node2 = new Node(null);
        node2.setLevel(level);

        boolean equals = node.equals(node2);

        System.out.println(node);
    }
    private Node parent;
    private ArrayList<ArrayList<Cell>> level;
    private int g; // length of the path from the start node to the current node
    private static int nodeCount; // total amount of nodes generated


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

    public boolean isInitialState() {
        return this.parent == null;
    }

    public ArrayList<ArrayList<Cell>> getLevel() {
        return level;
    }

    public void setLevel(ArrayList<ArrayList<Cell>> level) {
        this.level = level;
    }

    public int getNodeCount() {
        return nodeCount;
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
        return Objects.hash(parent, level, g);
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
