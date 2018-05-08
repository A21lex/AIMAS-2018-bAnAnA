package aimas;

import aimas.entities.Box;

import java.util.ArrayList;

public abstract class SearchMethod {
    public abstract int heuristic(Node start);



    // From Warm-Up assignment
    protected static int manhDist(int i1, int j1, int i2, int j2){ // between cells of a map, not between state nodes
        int diffI = Math.abs(i1 - i2);
        int diffJ = Math.abs(j1 - j2);
        return diffI + diffJ;
    }
}

class SimpleGoalSatisfier extends SearchMethod {
    char goalToSatisfy;
    public SimpleGoalSatisfier(char goalToSatisfy){
        this.goalToSatisfy = goalToSatisfy;
    }
    @Override
    public int heuristic(Node start) {
        CoordinatesPair agentCellCoords = start.getAgentCellCoords().get(0); // just take the only agent for now
        int agentRow = agentCellCoords.getX();
        int agentCol = agentCellCoords.getY();

        int boxRow = 0;
        int boxCol = 0;
        ArrayList<CoordinatesPair> boxCellCoords = start.getBoxCellCoords();
        for (CoordinatesPair coordinatesPair : boxCellCoords){
            if (start.getCellAtCoords(coordinatesPair).getEntity() instanceof Box){
                Box box = (Box) start.getCellAtCoords(coordinatesPair).getEntity();
                if (box.getLetter() == Character.toUpperCase(goalToSatisfy)){
                    boxRow = coordinatesPair.getX();
                    boxCol = coordinatesPair.getY();
                    break;
                }
            }
        }
        int goalRow = 0;
        int goalCol = 0;
        ArrayList<CoordinatesPair> goalCellCoords = Node.getGoalCellCoords();
        for (CoordinatesPair coordinatesPair : goalCellCoords){
            if (start.getCellAtCoords(coordinatesPair).getGoalLetter() == goalToSatisfy){
                goalRow = coordinatesPair.getX();
                goalCol = coordinatesPair.getY();
                break;
            }
        }
        // Now we have coords of agent, box and goal we try to satisfy. Let's roll.
        int fromBoxToGoal = manhDist(boxRow, boxCol, goalRow, goalCol);
        int fromAgentToBox = manhDist(agentRow, agentCol, boxRow, boxCol);

        // int fromAgentToGoal = manhDist(agentRow, agentCol, goalRow, goalCol);
//        System.out.println("Agent to box: " + fromAgentToBox);
//        System.out.println("Box to goal: " + fromBoxToGoal);
//        System.out.println();

        return fromBoxToGoal + fromAgentToBox;
    }
}

class NodeReacher extends SearchMethod {

    @Override
    public int heuristic(Node start) {
        return 0; // rewrite this accordingly
    }
}