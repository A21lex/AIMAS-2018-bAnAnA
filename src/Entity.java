/**
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
// Available colors taken from the programming project description
// For SA track colors of boxes and agents will be set to BLUE as that's default
enum Color{
    BLUE, RED, GREEN, CYAN, MAGENTA, ORANGE, PINK, YELLOW
}

class Agent extends Entity{
    //public static final Agent NO_AGENT = new Agent(Color.BLUE, -1);

    private int number;
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public Agent(Cell currentCell, Color color, int number) {
        //this.currentCell = currentCell;
        this.color = color;
        this.number = number;
    }

    public Agent(Agent original){
        this.color = original.color;
        this.number = original.number;
    }

    @Override
    public String toString() {
        return "" + getNumber()/* + " on: " + currentCell.getI() + " : " + currentCell.getJ()*/;
    }
}

class Box extends Entity{
    //public static final Box NO_BOX = new Box(Color.BLUE, '0');

    private char letter;
    public char getLetter() {
        return letter;
    }
    public void setLetter(char letter) {
        this.letter = letter;
    }

    public Box(Cell currentCell, Color color, char letter) {
        //this.currentCell = currentCell;
        this.color = color;
        this.letter = letter;
    }

    public Box(Box original){
        this.color = original.color;
        this.letter = original.letter;
    }

    @Override
    public String toString() {
        return "" + getLetter()/* + " on: " + currentCell.getI() + " : " + currentCell.getJ()*/;
    }
}














