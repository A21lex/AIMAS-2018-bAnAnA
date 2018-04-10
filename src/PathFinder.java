/**
 * Created by aleksandrs on 4/3/18.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class PathFinder {
    // based on simple representation like below;
    // 0 empty cell, 1 occupied cell
//            {0, 1, 1, 0},
//            {0, 0, 0, 1},
//            {1, 1, 0, 1, 0},
//            {1, 1, 0, 0, 0, 1},
//            {1, 1, 0, 1, 0}
//    };

    public static void main(String[] args) {

//        System.out.println();
//        PathFinder PathFinder = new PathFinder();
//        boolean pathExists = PathFinder.pathExists(level, new Cell(0,0), new Cell(2,2),
//                true, true, true);
//        System.out.println("there exists an unobstructed path = " + pathExists);

    }

    boolean pathExists(ArrayList<ArrayList<Cell>> level, Cell startingCell, Cell finishingCell,
                       boolean wObstacles, boolean aObstacles, boolean bObstacles){
        // convert level to array of 1s and 0s depending on the defined obstacles
        Integer[][] simplifiedLevel = getSimplifiedLevelArray(level, wObstacles, aObstacles, bObstacles);
        for(int i = 0; i < simplifiedLevel.length; i++){
            for (int j = 0; j < simplifiedLevel[i].length; j++){
             //   System.out.print(simplifiedLevel[i][j]);
            }
        //    System.out.println();
        }
        // commence BFS
        HashSet<Cell> visited = new HashSet<>();
        ArrayDeque<Cell> frontier = new ArrayDeque<>();
        frontier.addLast(startingCell); // startingCell is a root here
        System.out.println("Starting cell: " + startingCell.toString());
        System.out.println("Finishing cell: " + finishingCell.toString());
        while (!frontier.isEmpty()){
            Cell subtreeRoot = frontier.pollFirst(); // get first element of the queue

            if (subtreeRoot.equals(finishingCell)){ // reached the goal!
                return true;
            }
            for (Cell child: subtreeRoot.getChildren(simplifiedLevel)){
                if (visited.contains(child)){
                    continue;
                }
                if (!frontier.contains(child)){
                    frontier.addLast(child);
                }
            }
            visited.add(subtreeRoot);
        }
        return false;
    }

    private Integer[][] getSimplifiedLevelArray(ArrayList<ArrayList<Cell>> level,
                                                boolean wObstacles, boolean aObstacles, boolean bObstacles){
        ArrayList<ArrayList<Integer>> simpleLevel = new ArrayList<>();

        for (ArrayList<Cell> row: level){
            ArrayList<Integer> simpleRow = new ArrayList<>();
            for (Cell cell: row){
                int num; // 0 for free, 1 for occupied
                if ((cell.getType()== Cell.Type.Wall && wObstacles) ||
                        (cell.getType()==Cell.Type.Box && bObstacles) ||
                        (cell.getType()== Cell.Type.Agent && aObstacles)){
                    num = 1;
                }
                else {
                    num = 0;
                }
                simpleRow.add(num);
                boolean isOccupied = (num==1) ? true : false;
                cell.setOccupied(isOccupied);
            }
            simpleLevel.add(simpleRow);
        }
        Integer[][] simpleLevelArray = new Integer[simpleLevel.size()][];
        for (int i = 0; i < simpleLevel.size(); i++) {
            ArrayList<Integer> row = simpleLevel.get(i);
            simpleLevelArray[i] = row.toArray(new Integer[row.size()]);
        }
        return simpleLevelArray;
    }

}

class Cell {
    private int i, j;
    private boolean isOccupied; // use to denote cell as occupied or free (e.g. goal can be occupied or free)
    enum Type{          // use to denote type of cell
        Wall, Box, Agent, Goal, Empty
    }

    private Type type;

    private char lettMark;

    Cell(int i, int j){
        this.i = i;
        this.j = j;
    }

    Cell(int i, int j, Type type, boolean isOccupied, char lettMark){
        this.i = i;
        this.j = j;
        this.type = type;
        this.isOccupied = isOccupied;
        this.lettMark = lettMark;
    }


    int getI() {
        return i;
    }

    int getJ() {
        return j;
    }

    char getLettMark(){
        return lettMark;
    }

    void setOccupied(boolean isOccupied){
        this.isOccupied = isOccupied;
    }
    void setType(Type type){
        this.type = type;
    }

    boolean getOccupied(){
        return this.isOccupied;
    }

    Type getType(){
        return this.type;
    }

    // Returns true if we do not try to move into a wall or outside the level
    private boolean isLegalMove(int i, int j, Integer[][] level){
        if ((i < 0 || i >= level.length) || (j < 0 || j >= level[i].length) || level[i][j] == 1){
            return false;
        }
        return true;
    }


    // Returns neighboring cells which are results of legal moves
    ArrayList<Cell> getChildren(Integer[][] level){
        int[][] values={{0,1},{0,-1},{1,0},{-1,0}};
        ArrayList<Cell> children = new ArrayList<>();
        for (int i = 0; i < values.length; i++){
            if (isLegalMove(this.getI()+values[i][0], this.getJ()+values[i][1],level)){
                children.add(new Cell(this.getI()+values[i][0], this.getJ()+values[i][1]));
            }
        }
        //the code below achieves the same goal; still here for clarity

//        if (isLegalMove(this.getI(), this.getJ()+1, level)) {
//            children.add(new Cell(this.getI(), this.getJ()+1));
//        }
//        if (isLegalMove(this.getI(), this.getJ()-1, level)) {
//            children.add(new Cell(this.getI(), this.getJ()-1));
//        }
//        if (isLegalMove(this.getI()+1, this.getJ(), level)) {
//            children.add(new Cell(this.getI()+1, this.getJ()));
//        }
//        if (isLegalMove(this.getI()-1, this.getJ(), level)) {
//            children.add(new Cell(this.getI()-1, this.getJ()));
//        }
        return children;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (o instanceof Cell){
            Cell p = (Cell) o;
            return  (getI() == p.getI()) && (getJ() == p.getJ());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i,j);
    }

    @Override
    public String toString() {
        return i + ":" + j;
    }
}