package aimas;

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.Type;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;

import java.util.*;

public class PathFinder {
    // based on simple representation like below;
    // 0 empty cell, 1 occupied cell
//            {0, 1, 1, 0},
//            {0, 0, 0, 1},
//            {1, 1, 0, 1, 0},
//            {1, 1, 0, 0, 0, 1},
//            {1, 1, 0, 1, 0}
//    };

    private static List<CoordinatesPair> foundPath = new ArrayList<>();
    public static List<CoordinatesPair> getFoundPath(){
        return foundPath;
    }


    public static boolean pathExists(ArrayList<ArrayList<Cell>> level, Cell startingCell, Cell finishingCell,
                                     boolean wObstacles, boolean aObstacles, boolean bObstacles){
        foundPath.clear();

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
        Map<CoordinatesPair, CoordinatesPair> cameFrom = new HashMap<>(); // record path through coordinates
        HashSet<CoordinatesPair> visited = new HashSet<>();
        ArrayDeque<CoordinatesPair> frontier = new ArrayDeque<>();
        CoordinatesPair startingCoordinatesPair = new CoordinatesPair(startingCell);
        CoordinatesPair finishingCoordinatesPair = new CoordinatesPair(finishingCell);
        //frontier.addLast(startingCell); // startingCell is a root here
        frontier.addLast(startingCoordinatesPair);
        cameFrom.put(startingCoordinatesPair, null); // root came from nowhere (as it is the starting coordinate)
        //System.out.println("Starting cell: " + startingCell.toString());
        //System.out.println("Finishing cell: " + finishingCell.toString());
        //System.out.println("Starting cell: " + startingCoordinatesPair.toString());
        //System.out.println("Finishing cell: " + finishingCoordinatesPair.toString());
        while (!frontier.isEmpty()){
            //Cell subtreeRoot = frontier.pollFirst(); // get first element of the queue
            CoordinatesPair subtreeRoot = frontier.pollFirst();
            if (subtreeRoot.equals(finishingCoordinatesPair)){
                //if (subtreeRoot.equals(finishingCell)){ // reached the goal!
                foundPath = constructPath(subtreeRoot, cameFrom); // get how we reached the goal
                return true;
            }
            for (CoordinatesPair child: subtreeRoot.getChildren(simplifiedLevel)){
                if (visited.contains(child)){
                    continue;
                }
                if (!frontier.contains(child)){
                    cameFrom.put(child, subtreeRoot); // record reaching the child
                    frontier.addLast(child);
                }
            }
            visited.add(subtreeRoot);
        }
        return false;
    }
    // Overloaded pathExists taking coordinates instead of cells (often makes more sense this way)
    // boolean input values are to count anything we want as an obstacle: walls/agents/boxes
    public static boolean pathExists(ArrayList<ArrayList<Cell>> level, CoordinatesPair startingCoordinatesPair,
                                     CoordinatesPair finishingCoordinatesPair,
                                     boolean wObstacles, boolean aObstacles, boolean bObstacles){
        foundPath.clear();
        // convert level to array of 1s and 0s depending on the defined obstacles
        Integer[][] simplifiedLevel = getSimplifiedLevelArray(level,
                startingCoordinatesPair, finishingCoordinatesPair, wObstacles, aObstacles, bObstacles);
        // commence BFS

        Map<CoordinatesPair, CoordinatesPair> cameFrom = new HashMap<>(); // record path through coordinates
        HashSet<CoordinatesPair> visited = new HashSet<>();
        ArrayDeque<CoordinatesPair> frontier = new ArrayDeque<>();
        //frontier.addLast(startingCell); // startingCell is a root here
        frontier.addLast(startingCoordinatesPair);
        cameFrom.put(startingCoordinatesPair, null); // root came from nowhere (as it is the starting coordinate)
        while (!frontier.isEmpty()){
            CoordinatesPair subtreeRoot = frontier.pollFirst();
            if (subtreeRoot.equals(finishingCoordinatesPair)){
                foundPath = constructPath(subtreeRoot, cameFrom); // get how we reached the goal
                return true;
            }
            for (CoordinatesPair child: subtreeRoot.getChildren(simplifiedLevel)){
                if (visited.contains(child)){
                    continue;
                }
                if (!frontier.contains(child)){
                    cameFrom.put(child, subtreeRoot); // record reaching the child
                    frontier.addLast(child);
                }
            }
            visited.add(subtreeRoot);
        }
        return false;
    }

    private static List<CoordinatesPair> constructPath(CoordinatesPair reachedState,
                                                       Map<CoordinatesPair, CoordinatesPair> cameFrom){
        List<CoordinatesPair> path = new ArrayList<>();
        CoordinatesPair finalCoordinate = reachedState; // keep to add to the list in the end
        while (cameFrom.get(reachedState) != null){ // until we are the start coordinate
            CoordinatesPair prevCoordinate = cameFrom.get(reachedState);
            path.add(prevCoordinate);
            reachedState = prevCoordinate;
        }
        Collections.reverse(path);
        path.add(finalCoordinate); // add final coordinate

        return path;
    }

    /**
     * Returns boxes on path between 2 cells (note that if final
     * cell has a box, it still counts as "0" (as we are trying to "reach" this goal)
     */
    public static ArrayList<Box> getBoxesOnPath(Node node,
                                                CoordinatesPair startingCoordinatesPair,
                                                CoordinatesPair finishingCoordinatesPair,
                                                boolean wObstacles, boolean aObstacles, boolean bObstacles,
                                                ArrayList<Box> exceptionBoxes){

        ArrayList<Box> boxesOnPath = new ArrayList<>();
        ArrayList<ArrayList<Cell>> level = node.getLevel();

        pathExists(level, startingCoordinatesPair, finishingCoordinatesPair,
                wObstacles, aObstacles, bObstacles);
        List<CoordinatesPair> foundPathLoc = getFoundPath();

        for (CoordinatesPair coordPair : foundPathLoc){
            if (node.getCellAtCoords(coordPair).getEntity() instanceof Box &&
                    !exceptionBoxes.contains(node.getCellAtCoords(coordPair).getEntity())){
                boxesOnPath.add((Box)node.getCellAtCoords(coordPair).getEntity());
            }
        }
        return boxesOnPath;
    }

    private static Integer[][] getSimplifiedLevelArray(ArrayList<ArrayList<Cell>> level,
                                                       CoordinatesPair startingCoordinatesPair,
                                                       CoordinatesPair finishingCoordinatesPair,
                                                       boolean wObstacles, boolean aObstacles, boolean bObstacles){
        ArrayList<ArrayList<Integer>> simpleLevel = new ArrayList<>();
        for (ArrayList<Cell> row: level){
            ArrayList<Integer> simpleRow = new ArrayList<>();
            for (Cell cell: row){
                int num; // 0 for free, 1 for occupied. Start and Finish cell is also 0. (to reach boxes)
                // finally, if it is our own agent, also make it 0
                if ((cell.getType()== Type.WALL && wObstacles) ||
                        (cell.getType()==Type.SPACE && cell.getEntity() instanceof Box && bObstacles
                                && !cell.getCoordinates().equals(finishingCoordinatesPair)
                                && !cell.getCoordinates().equals(startingCoordinatesPair)) ||
                        (cell.getType()== Type.SPACE && cell.getEntity() instanceof Agent && aObstacles
                                && !(((Agent)cell.getEntity()).getNumber() == 0))){ // SA friendly only for now
                    num = 1;
                }
                else {
                    num = 0;
                }
                simpleRow.add(num);
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