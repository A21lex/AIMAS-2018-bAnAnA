package aimas;

import java.util.ArrayList;
import java.util.Objects;

// Use this for Coordinates pair only (e..g in PathFinder)
public class CoordinatesPair {
    private int x;
    private int y;

    public CoordinatesPair(int x, int y){
        this.x = x;
        this.y = y;
    }

    public CoordinatesPair(Cell cell){
        this.x = cell.getI();
        this.y = cell.getJ();
    }

    // Copy constructor for node class
    public CoordinatesPair(CoordinatesPair original){
        this.x = original.x;
        this.y = original.y;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this){
            return true;
        }
        if (o instanceof CoordinatesPair){
            CoordinatesPair p = (CoordinatesPair) o;
            return  (getX() == p.getX()) && (getY() == p.getY());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }

    @Override
    public String toString() {
        return x + ":" + y;
    }

    // Returns true if we do not try to move into a wall or outside the level
    private boolean isLegalMove(int i, int j, Integer[][] level){
        if ((i < 0 || i >= level.length) || (j < 0 || j >= level[i].length) || level[i][j] == 1){
            return false;
        }
        return true;
    }

    // Returns neighboring cells (their coordinates) which are results of legal moves
    ArrayList<CoordinatesPair> getChildren(Integer[][] level){
        int[][] values={{0,1},{0,-1},{1,0},{-1,0}};
        ArrayList<CoordinatesPair> children = new ArrayList<>();
        for (int i = 0; i < values.length; i++){
            if (isLegalMove(this.getX()+values[i][0], this.getY()+values[i][1],level)){
                children.add(new CoordinatesPair(this.getX()+values[i][0], this.getY()+values[i][1]));
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
}
