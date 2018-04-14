import java.util.ArrayList;
import java.util.Objects;

// use to denote type of cell - empty vs wall
enum Type{
    Wall, Space
}

public class Cell {
    private int i, j; // coordinates of the cell
    private Type type; // wall or space
    private Entity entity; // agent or box currently located on the cell; null if nothing (or if it is a wall)
    private char goalLetter; // a lowercase goalLetter if it is a goal; else 0 (default value for char)

    //logic will work something like in the following line:
    //if cell.isGoal() && cell.getGoalLetter()==cell.getEntity().getGoalLetter() then goalComplete=true

    public boolean isGoal(){
        char c = this.getGoalLetter();
        return 'a' <= c && c <= 'z'; // will return true if goalLetter is a lowercase letter
    }

    // Simple constructor essentially only for coordinates
    Cell(int i, int j){
        this.i = i;
        this.j = j;
    }
    // Full constructor for all the info contained in the cell
    Cell(int i, int j, Type type, Entity entity, char goalLetter){
        this.i = i;
        this.j = j;
        this.type = type;
        this.entity = entity;
        this.goalLetter = goalLetter;
    }

    int getI() {
        return i;
    }
    int getJ() {
        return j;
    }
    Type getType(){
        return this.type;
    }
    Entity getEntity() {
        return this.entity;
    }
    char getGoalLetter(){
        return this.goalLetter;
    }
    void setType(Type type){
        this.type = type;
    }
    void setEntity(Entity entity){
        this.entity = entity;
    }
    void setGoalLetter(char goalLetter){
        this.goalLetter = goalLetter;
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
