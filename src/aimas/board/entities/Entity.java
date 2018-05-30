package aimas.board.entities;

/**
 * This class is a superclass for Agent and Box which are both game entities able to  move between cells
 */
public class Entity {
    //protected Cell currentCell;
    protected Color color;


    public Color getColor(){
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














