/**
 * Created by Arturs Gumenuks on 08.04.2018.
 */

import java.io.IOException;
import java.util.ArrayList;

public class GoalPrioritizer {

    private static ArrayList<Cell.CoordinatesPair> goalCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> boxCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> agentCellCoords = new ArrayList<>();

    public static void main(String[] args){
        LevelReader levelReader = new LevelReader();
        String pathToLevel = "SAD1.lvl"; // << Set path to level file here
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = true; // agents are obstacles
        boolean bObstacles = true; // boxes are obstacles
        Cell startingCell = new Cell(1,1); // << Set starting cell
        Cell finishingCell = new Cell(5,17); // << Set finishing cell

        ArrayList<ArrayList<Cell>> level = null;
        try {
            level = levelReader.getLevel(pathToLevel);
        } catch (IOException e) {
            System.out.println("######################");
            System.out.println("Probably incorrect path");
        }

//        for (ArrayList<Cell> row: level){
//            for (Cell cell: row){
//                System.out.print(cell + " ");
//            }
//            System.out.println();
//        }

        boxCellCoords = levelReader.getBoxCellCoords();
        goalCellCoords = levelReader.getGoalCellCorrds();
        agentCellCoords = levelReader.getAgentCellCorrds();

        System.out.println("boxes: " + boxCellCoords);
        System.out.println("goals " + goalCellCoords);
        System.out.println("agents " + agentCellCoords);

        PathFinder pathFinder = new PathFinder();
        boolean pathExists = pathFinder.pathExists(level, startingCell, finishingCell,
                wObstacles, aObstacles, bObstacles);
        System.out.println("path exists = " + pathExists);

        GoalPrioritizer goalPrioritizer = new GoalPrioritizer();
        ArrayList<ArrayList<Cell.CoordinatesPair>> goalPartOrd = goalPrioritizer.prioritizeGoals(levelReader, pathFinder, level);

        System.out.println("Ordering relations found: " + goalPartOrd.size());
        for (ArrayList<Cell.CoordinatesPair> item: goalPartOrd){
            for (Cell.CoordinatesPair relCell: item){
                System.out.print(relCell.getX() + ":" + relCell.getY() + ":"
                        + level.get(relCell.getX()).get(relCell.getY()).getGoalLetter()+ " ");
            }
            System.out.println();
        }

    }

    // method for prioritizing goals based on how their satisfaction affects connectivity between other goals and
    // agent; please note that for now the agent should initially be able to achieve any goal
    // otherwise we can work with set (of Cell type objects) differences

    private ArrayList<ArrayList<Cell.CoordinatesPair>> prioritizeGoals(LevelReader levelReader,
                                                            PathFinder pathFinder, ArrayList<ArrayList<Cell>> level ){
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = false; // agents are obstacles
        boolean bObstacles = true;

        ArrayList<ArrayList<Cell.CoordinatesPair>> goalPartOrd = new ArrayList<ArrayList<Cell.CoordinatesPair>>();

        Cell.CoordinatesPair agentCellCoords = LevelReader.getAgentCellCorrds().get(0); // taking any agent for now, since colors are omitted
        Cell agentCell = level.get(agentCellCoords.getX()).get(agentCellCoords.getY());
        // goalPriorities.add(new ArrayList<Cell>());
        for (int i = 0; i< goalCellCoords.size(); i++){
            //System.out.println("EXECUTED");
           // goalPriorities.get(goalPriorities.size()-1).add(goalCellCoords.get(i));
            // get location of the goal cell currently being processed
            int curI = goalCellCoords.get(i).getX();
            int curJ = goalCellCoords.get(i).getY();
            Type oldType = level.get(curI).get(curJ).getType();
            Entity oldEntity = level.get(curI).get(curJ).getEntity();
            Entity someBox = null; // take first box with this letter (in reality the one we test)
            //iter through boxes and find one with letter of the current goal (just for now)
            for (Cell.CoordinatesPair boxCellCoord: boxCellCoords){
                Box curBox = (Box) level.get(boxCellCoord.getX()).get(boxCellCoord.getY()).getEntity();
                char goalLetter =
                        level.get(goalCellCoords.get(i).getX()).get(goalCellCoords.get(i).getY()).getGoalLetter();
                if (Character.toLowerCase(curBox.getLetter()) == goalLetter){
                    someBox = curBox;
                    boxCellCoords.remove(boxCellCoord); // remove cur box cell from the list as it is free now
                    level.get(boxCellCoord.getX()).get(boxCellCoord.getY()).setEntity(null); // and remove entity from it correspondingly
                    break;
                }
            }

            level.get(curI).get(curJ).setEntity(someBox); // and put the box on the cur. goal
            int numOfBlockedGoals = 0;
            for (int j = 0; j< goalCellCoords.size(); j++){

                Cell goalCell = level.get(goalCellCoords.get(j).getX()).get(goalCellCoords.get(j).getY());
                if(i!=j && !pathFinder.pathExists(level, agentCell, goalCell,
                        wObstacles,aObstacles,bObstacles)) {

                    numOfBlockedGoals++; // will be used later

                    goalPartOrd.add(new ArrayList<Cell.CoordinatesPair>());
                    goalPartOrd.get(goalPartOrd.size()-1).add(goalCellCoords.get(j));
                    goalPartOrd.get(goalPartOrd.size()-1).add(goalCellCoords.get(i));
                }
            }

            level.get(curI).get(curJ).setType(oldType);
            level.get(curI).get(curJ).setEntity(oldEntity);
        }
        return goalPartOrd;
    }
}

class CellPrior{
    private Cell cell;
    private int priority;
    CellPrior(Cell cell, int priority){
        this.cell = cell;
        this.priority = priority;
    }

    public Cell getCell(){
        return cell;
    }

    public int getPriority(){
        return priority;
    }

    public void setCell(Cell cell){
        this.cell = cell;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }
}

