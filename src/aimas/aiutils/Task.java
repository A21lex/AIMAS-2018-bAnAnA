package aimas.aiutils;

import aimas.Node;
import aimas.board.Cell;
import aimas.board.entities.Box;

/**
 * Represents a task (combination of box and goal) assigned to an agent to satisfy
 */
public class Task{


    Cell goal;
    Box box;

    public Task(Cell goal, Box box) {
        this.goal = goal;
        this.box = box;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public Cell getGoal() {
        return goal;
    }

    public void setGoal(Cell goal) {
        this.goal = goal;
    }

    public boolean isAchieved(Node node){
        return goal.getCoordinates() == box.getCoordinates(node);
    }
}
