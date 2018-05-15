package aimas.board.entities;

import aimas.Command;
import aimas.board.CoordinatesPair;
import aimas.Node;

import java.util.ArrayList;
import java.util.Objects;

public class Agent extends Entity{
    //public static final Agent NO_AGENT = new Agent(Color.BLUE, -1);

    private int number;
    // commands this agent has to do; to be filled during runtime of the program
    private ArrayList<Command> actions = new ArrayList<>();
    public int getNumber() {
        return number;
    }
    public ArrayList<Command> getActions() {
        return actions;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public Agent(Color color, int number) {
        //this.currentCell = currentCell;
        this.color = color;
        this.number = number;
    }

    public Agent(Agent original){
        this.color = original.color;
        this.number = original.number;
    }

    // Go through all coordinates of the node and check which corresponds to THIS agent
    public CoordinatesPair getCoordinates(Node node) {
        ArrayList<CoordinatesPair> agentCoordinates = node.getAgentCellCoords();
        for (CoordinatesPair agentCoordinate : agentCoordinates){
            if (node.getCellAtCoords(agentCoordinate).getEntity().equals(this)){
                return agentCoordinate;
            }
        }
        return null; // exception will let us know that something is very wrong here
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (obj instanceof Agent){
            Agent a = (Agent) obj;
            return color.equals(a.color) && (number == a.number);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, number);
    }

    @Override
    public String toString() {
        return "" + getNumber()/* + " on: " + currentCell.getI() + " : " + currentCell.getJ()*/;
    }
}
