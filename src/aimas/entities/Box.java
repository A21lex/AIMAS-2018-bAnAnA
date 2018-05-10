package aimas.entities;

import aimas.CoordinatesPair;
import aimas.Node;

import java.util.ArrayList;
import java.util.Objects;

public class Box extends Entity{
    //public static final Box NO_BOX = new Box(Color.BLUE, '0');

    private char letter;
    private int id; // every box has a unique ID - helps distinguish between boxes of same color/letter!

    public int getId() {
        return id;
    }

    // Go through all coordinates of the node and check which corresponds to THIS box
    public CoordinatesPair getCoordinates(Node node) {
        ArrayList<CoordinatesPair> boxCoordinates = node.getBoxCellCoords();
        for (CoordinatesPair boxCoordinate : boxCoordinates){
            if (node.getCellAtCoords(boxCoordinate).getEntity().equals(this)){
                return boxCoordinate;
            }
        }
        return null; // exception will let us know that something is very wrong here
    }

    public char getLetter() {
        return letter;
    }

    public Box(Color color, char letter, int id) {
        this.color = color;
        this.letter = letter;
        this.id = id;
    }

    public Box(Box original){
        this.color = original.color;
        this.letter = original.letter;
        this.id = original.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (obj instanceof Box){
            Box b = (Box) obj;
            return color.equals(b.color) && (letter == b.letter) && (id == b.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, letter, id);
    }

    @Override
    public String toString() {
        return "" + getLetter()/* + " on: " + currentCell.getI() + " : " + currentCell.getJ()*/;
    }
}
