import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Arturs Gumenuks on 08.04.2018.
 */
public class GoalPrioritizer {

    private static ArrayList<Cell> goalCells = new ArrayList<>();
    private static ArrayList<Cell> boxCells = new ArrayList<>();
    private static ArrayList<Cell> agentCells = new ArrayList<>();

    public static void main(String[] args){
        LevelReader levelReader = new LevelReader();
        String pathToLevel = "C:\\wTESTING\\AIlevels\\SAD1.lvl"; // << Set path to level file here
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = true; // agents are obstacles
        boolean bObstacles = true; // boxes are obstacles
        Cell startingCell = new Cell(1,1); // << Set starting cell
        Cell finishingCell = new Cell(1,3); // << Set finishing cell

        ArrayList<ArrayList<Cell>> level = null;
        try {
            level = levelReader.getLevel(pathToLevel);
        } catch (IOException e) {
            System.out.println("Probably incorrect path");
        }

        for (ArrayList<Cell> row: level){
            for (Cell cell: row){
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        boxCells = levelReader.getBoxCells();
        goalCells = levelReader.getGoalCells();
        agentCells = levelReader.getAgentCells();

        System.out.println("boxes: " + boxCells);
        System.out.println("goals " + goalCells);
        System.out.println("agents " + agentCells);

        PathFinder pathFinder = new PathFinder();
        boolean pathExists = pathFinder.pathExists(level, startingCell, finishingCell,
                wObstacles, aObstacles, bObstacles);
        System.out.println("path exists = " + pathExists);

        GoalPrioritizer goalPrioritizer = new GoalPrioritizer();
        ArrayList<ArrayList<Cell>> goalPartOrd = goalPrioritizer.prioritizeGoals(levelReader, pathFinder, level);

        System.out.println("Ordering relations found: " + goalPartOrd.size());
        for (ArrayList<Cell> item: goalPartOrd){
            for (Cell relCell: item){
                System.out.print(relCell.getI() + ":" + relCell.getJ() + ":" + relCell.getLettMark()+ " ");
            }
            System.out.println();
        }

    }

    // method for prioritizing goals based on how their satisfaction affects connectivity between other goals and
    // agent; please note that for now the agent should initially be able to achieve any goal
    // otherwise we can work with set (of Cell type objects) differences

    private ArrayList<ArrayList<Cell>> prioritizeGoals(LevelReader levelReader,
                                                            PathFinder pathFinder, ArrayList<ArrayList<Cell>> level ){
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = false; // agents are obstacles
        boolean bObstacles = true;

        ArrayList<ArrayList<Cell>> goalPartOrd = new ArrayList<ArrayList<Cell>>();

        Cell agentCell = levelReader.getAgentCells().get(0); // taking any agent for now, since colors are omitted
       // goalPriorities.add(new ArrayList<Cell>());
        for (int i=0; i<goalCells.size(); i++){
            //System.out.println("EXECUTED");
           // goalPriorities.get(goalPriorities.size()-1).add(goalCells.get(i));
            int curI = goalCells.get(i).getI();
            int curJ = goalCells.get(i).getJ();
            Cell.Type oldType = level.get(curI).get(curJ).getType();
            level.get(curI).get(curJ).setType(Cell.Type.Box); // supposing there are more than one box
            int numOfBlockedGoals = 0;
            for (int j=0; j<goalCells.size(); j++){
                // fix i and j to curI and curJ
                if(j!=i && !pathFinder.pathExists(level, agentCell, goalCells.get(j), wObstacles,aObstacles,bObstacles)) {
                    numOfBlockedGoals++; // will be used later

                    goalPartOrd.add(new ArrayList<Cell>());
                    goalPartOrd.get(goalPartOrd.size()-1).add(goalCells.get(j));
                    goalPartOrd.get(goalPartOrd.size()-1).add(goalCells.get(i));
                }
            }

            level.get(curI).get(curJ).setType(oldType);
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

