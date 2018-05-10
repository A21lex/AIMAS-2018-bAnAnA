package aimas.board.entities; /**
 * Created by aleksandrs on 4/10/18.
 */

/**
 * This class is a superclass for Agent and Box which are both game entities able to  move between cells
 */
public class Entity {
    //protected Cell currentCell;
    protected Color color;


//    Cell getCurrentCell(){
//        return this.currentCell;
//    }
//    void setCurrentCell(Cell currentCell){
//        this.currentCell = currentCell;
//    }
    Color getColor(){
        return this.color;
    }
    void setColor(Color color){
        this.color = color;
    }

    // Default constuctor
    public Entity(){

    }

    // Copy constructor
    public Entity(Entity original){
        this.color = original.color;
    }

}














