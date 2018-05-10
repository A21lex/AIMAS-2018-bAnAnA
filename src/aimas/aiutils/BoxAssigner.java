package aimas.aiutils;

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.Node;
import aimas.board.entities.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BoxAssigner {

    /**
     * Decide which goal to satisfy with which box
     * @param node State of the level for which calculation takes place
     * @return
     */
    public static HashMap<Cell, Box> assignBoxesToGoals(Node node) {
        HashMap<Cell, Box> goalsBoxes = new HashMap<>();

        List<Cell> goalCells = new ArrayList<>();
        HashSet<Character> lettersEncountered = new HashSet<>();
        for (CoordinatesPair coordinate : Node.getGoalCellCoords()){
            Cell goalCell = node.getCellAtCoords(coordinate);
            goalCells.add(goalCell);
            lettersEncountered.add(goalCell.getGoalLetter());
        }

        List<Box> allBoxes = new ArrayList<>(); // here we don't care about boxes not satisfying any goal
        for (CoordinatesPair coordinate : node.getBoxCellCoords()){
            Box box = (Box) node.getCellAtCoords(coordinate).getEntity();
            if (lettersEncountered.contains(Character.toLowerCase(box.getLetter()))){
                allBoxes.add(box);
            }
        }

        for (Cell goalCell : goalCells){
            // find relevant boxes closest to the goalcell
            List<Box> relevantBoxes = new ArrayList<>();
            for (Box box : allBoxes){
                if (Character.toLowerCase(box.getLetter()) == goalCell.getGoalLetter()){
                    relevantBoxes.add(box);
                }
            }
            if (relevantBoxes.size() == 1){
                // there is only one box corresponding to this goal, easy-peasy
                goalsBoxes.put(goalCell, relevantBoxes.get(0));
                allBoxes.remove(relevantBoxes.get(0)); // this box is now occupied, remove from box list
                continue;
            }
            Box winnerBox = relevantBoxes.get(0); // take some relevant box
            CoordinatesPair winnerBoxCoord = winnerBox.getCoordinates(node);
            int winnerApproxDistance = BestFirstSearch.manhDist(winnerBoxCoord.getX(), winnerBoxCoord.getY(),
                    goalCell.getI(), goalCell.getJ());
            for (Box box : relevantBoxes){
                // find the box closest to the goalcell
                CoordinatesPair potentialWinnerBoxCoord = box.getCoordinates(node);
                int potentialWinnerApproxDistance = BestFirstSearch.manhDist(potentialWinnerBoxCoord.getX(),
                        potentialWinnerBoxCoord.getY(), goalCell.getI(), goalCell.getJ());

                if (potentialWinnerApproxDistance < winnerApproxDistance){
                    winnerApproxDistance = potentialWinnerApproxDistance;
                    winnerBox = box;
                    winnerBoxCoord = potentialWinnerBoxCoord;
                }
            }
            goalsBoxes.put(goalCell, winnerBox);
            allBoxes.remove(winnerBox); // this box is now occupied, remove from box list

        }

        return goalsBoxes;
    }

}
