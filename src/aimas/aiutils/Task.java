package aimas.aiutils;

import aimas.board.Cell;
import aimas.board.entities.Box;

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
}
