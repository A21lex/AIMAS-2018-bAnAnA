/**
 * Created by aleksandrs on 4/3/18.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

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

    boolean pathExists(ArrayList<ArrayList<Cell>> level, Cell startingCell, Cell finishingCell,
                       boolean wObstacles, boolean aObstacles, boolean bObstacles){
        // convert level to array of 1s and 0s depending on the defined obstacles
        Integer[][] simplifiedLevel = getSimplifiedLevelArray(level, wObstacles, aObstacles, bObstacles);
        for(int i = 0; i < simplifiedLevel.length; i++){
            for (int j = 0; j < simplifiedLevel[i].length; j++){
             //   System.out.print(simplifiedLevel[i][j]);
            }
        //    System.out.println();
        }
        // commence BFS
        //HashSet<Cell> visited = new HashSet<>();
        //ArrayDeque<Cell> frontier = new ArrayDeque<>();
        HashSet<Cell.CoordinatesPair> visited = new HashSet<>();
        ArrayDeque<Cell.CoordinatesPair> frontier = new ArrayDeque<>();
        Cell.CoordinatesPair startingCoordinatesPair = new Cell.CoordinatesPair(startingCell);
        Cell.CoordinatesPair finishingCoordinatesPair = new Cell.CoordinatesPair(finishingCell);
        //frontier.addLast(startingCell); // startingCell is a root here
        frontier.addLast(startingCoordinatesPair);
        //System.out.println("Starting cell: " + startingCell.toString());
        //System.out.println("Finishing cell: " + finishingCell.toString());
        System.out.println("Starting cell: " + startingCoordinatesPair.toString());
        System.out.println("Finishing cell: " + finishingCoordinatesPair.toString());
        while (!frontier.isEmpty()){
            //Cell subtreeRoot = frontier.pollFirst(); // get first element of the queue
            Cell.CoordinatesPair subtreeRoot = frontier.pollFirst();
            if (subtreeRoot.equals(finishingCoordinatesPair)){
            //if (subtreeRoot.equals(finishingCell)){ // reached the goal!
                return true;
            }
            for (Cell.CoordinatesPair child: subtreeRoot.getChildren(simplifiedLevel)){
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


    private Integer[][] getSimplifiedLevelArray(ArrayList<ArrayList<Cell>> level,
                                                boolean wObstacles, boolean aObstacles, boolean bObstacles){
        ArrayList<ArrayList<Integer>> simpleLevel = new ArrayList<>();

        for (ArrayList<Cell> row: level){
            ArrayList<Integer> simpleRow = new ArrayList<>();
            for (Cell cell: row){
                int num; // 0 for free, 1 for occupied
                if ((cell.getType()== Type.Wall && wObstacles) ||
                        (cell.getType()==Type.Space && cell.getEntity() instanceof Box && bObstacles) ||
                        (cell.getType()== Type.Space && cell.getEntity() instanceof Agent && aObstacles)){
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


