package aimas; /**
 * Created by aleksandrs on 4/3/18.
 */

import aimas.entities.Agent;
import aimas.entities.Box;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class PathFinder {
    // based on simple representation like below;
    // 0 empty cell, 1 occupied cell
//            {0, 1, 1, 0},
//            {0, 0, 0, 1},
//            {1, 1, 0, 1, 0},
//            {1, 1, 0, 0, 0, 1},
//            {1, 1, 0, 1, 0}
//    };

    public static void main(String[] args) {
//        System.out.println();
//        PathFinder PathFinder = new PathFinder();
//        boolean pathExists = PathFinder.pathExists(level, new Cell(0,0), new Cell(2,2),
//                true, true, true);
//        System.out.println("there exists an unobstructed path = " + pathExists);

    }

    public static boolean pathExists(ArrayList<ArrayList<Cell>> level, Cell startingCell, Cell finishingCell,
                       boolean wObstacles, boolean aObstacles, boolean bObstacles){
        // convert level to array of 1s and 0s depending on the defined obstacles
        Integer[][] simplifiedLevel = getSimplifiedLevelArray(level,
                startingCell.getCoordinates(), finishingCell.getCoordinates(), wObstacles, aObstacles, bObstacles);
        for(int i = 0; i < simplifiedLevel.length; i++){
            for (int j = 0; j < simplifiedLevel[i].length; j++){
             //   System.out.print(simplifiedLevel[i][j]);
            }
        //    System.out.println();
        }
        // commence BFS
        //HashSet<Cell> visited = new HashSet<>();
        //ArrayDeque<Cell> frontier = new ArrayDeque<>();
        HashSet<CoordinatesPair> visited = new HashSet<>();
        ArrayDeque<CoordinatesPair> frontier = new ArrayDeque<>();
        CoordinatesPair startingCoordinatesPair = new CoordinatesPair(startingCell);
        CoordinatesPair finishingCoordinatesPair = new CoordinatesPair(finishingCell);
        //frontier.addLast(startingCell); // startingCell is a root here
        frontier.addLast(startingCoordinatesPair);
        //System.out.println("Starting cell: " + startingCell.toString());
        //System.out.println("Finishing cell: " + finishingCell.toString());
        //System.out.println("Starting cell: " + startingCoordinatesPair.toString());
        //System.out.println("Finishing cell: " + finishingCoordinatesPair.toString());
        while (!frontier.isEmpty()){
            //Cell subtreeRoot = frontier.pollFirst(); // get first element of the queue
            CoordinatesPair subtreeRoot = frontier.pollFirst();
            if (subtreeRoot.equals(finishingCoordinatesPair)){
            //if (subtreeRoot.equals(finishingCell)){ // reached the goal!
                return true;
            }
            for (CoordinatesPair child: subtreeRoot.getChildren(simplifiedLevel)){
                if (visited.contains(child)){
                    continue;
                }
                if (!frontier.contains(child)){
                    frontier.addLast(child);
                }
            }
            visited.add(subtreeRoot);
        }
        return false;
    }
    // Overloaded pathExists taking coordinates instead of cells (sometimes makes more sense this way)
    public static boolean pathExists(ArrayList<ArrayList<Cell>> level, CoordinatesPair startingCoordinatesPair,
                                     CoordinatesPair finishingCoordinatesPair,
                                     boolean wObstacles, boolean aObstacles, boolean bObstacles){
        // convert level to array of 1s and 0s depending on the defined obstacles
        Integer[][] simplifiedLevel = getSimplifiedLevelArray(level,
                startingCoordinatesPair, finishingCoordinatesPair, wObstacles, aObstacles, bObstacles);
//        for(int i = 0; i < simplifiedLevel.length; i++){
//            for (int j = 0; j < simplifiedLevel[i].length; j++){
//             //      System.out.print(simplifiedLevel[i][j]);
//            }
//            //    System.out.println();
//        }
        // commence BFS
        //HashSet<Cell> visited = new HashSet<>();
        //ArrayDeque<Cell> frontier = new ArrayDeque<>();
        HashSet<CoordinatesPair> visited = new HashSet<>();
        ArrayDeque<CoordinatesPair> frontier = new ArrayDeque<>();
        //frontier.addLast(startingCell); // startingCell is a root here
        frontier.addLast(startingCoordinatesPair);
        //System.out.println("Starting cell: " + startingCell.toString());
        //System.out.println("Finishing cell: " + finishingCell.toString());
        //System.out.println("Starting cell: " + startingCoordinatesPair.toString());
        //System.out.println("Finishing cell: " + finishingCoordinatesPair.toString());
        while (!frontier.isEmpty()){
            //Cell subtreeRoot = frontier.pollFirst(); // get first element of the queue
            CoordinatesPair subtreeRoot = frontier.pollFirst();
            if (subtreeRoot.equals(finishingCoordinatesPair)){
                //if (subtreeRoot.equals(finishingCell)){ // reached the goal!
                return true;
            }
            for (CoordinatesPair child: subtreeRoot.getChildren(simplifiedLevel)){
                if (visited.contains(child)){
                    continue;
                }
                if (!frontier.contains(child)){
                    frontier.addLast(child);
                }
            }
            visited.add(subtreeRoot);
        }
        return false;
    }


    private static Integer[][] getSimplifiedLevelArray(ArrayList<ArrayList<Cell>> level,
                                                       CoordinatesPair startingCoordinatesPair,
                                                CoordinatesPair finishingCoordinatesPair,
                                                boolean wObstacles, boolean aObstacles, boolean bObstacles){
        ArrayList<ArrayList<Integer>> simpleLevel = new ArrayList<>();

        for (ArrayList<Cell> row: level){
            ArrayList<Integer> simpleRow = new ArrayList<>();
            for (Cell cell: row){
                int num; // 0 for free, 1 for occupied. Finish cell is also 0. (to reach boxes)
                if ((cell.getType()== Type.WALL && wObstacles) ||
                        (cell.getType()==Type.SPACE && cell.getEntity() instanceof Box && bObstacles
                        && !cell.getCoordinates().equals(finishingCoordinatesPair)) ||
                        (cell.getType()== Type.SPACE && cell.getEntity() instanceof Agent && aObstacles)){
                    num = 1;
                }
                else {
                    num = 0;
                }
                simpleRow.add(num);
                //boolean isOccupied = (num==1) ? true : false;
                //cell.setOccupied(isOccupied);
            }
            simpleLevel.add(simpleRow);
        }
        Integer[][] simpleLevelArray = new Integer[simpleLevel.size()][];
        for (int i = 0; i < simpleLevel.size(); i++) {
            ArrayList<Integer> row = simpleLevel.get(i);
            simpleLevelArray[i] = row.toArray(new Integer[row.size()]);
        }
        return simpleLevelArray;
    }

}


