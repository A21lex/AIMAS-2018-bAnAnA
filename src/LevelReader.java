import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aleksandrs on 4/4/18.
 */
public class LevelReader {

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
        Cell finishingCell = new Cell(5,17); // << Set finishing cell

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

        System.out.println("boxes: " + boxCells);
        System.out.println("goals " + goalCells);

        PathFinder pathFinder = new PathFinder();
        boolean pathExists = pathFinder.pathExists(level, startingCell, finishingCell,
                wObstacles, aObstacles, bObstacles);
        System.out.println("path exists = " + pathExists);

    }

    // read file
    public ArrayList<ArrayList<Cell>> getLevel(String pathToLevel) throws IOException{
        ArrayList<ArrayList<Cell>> level = new ArrayList<>(); // store level here
        BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToLevel));
        try {
            String line = bufferedReader.readLine();
            int curRow = 0; // keep track of rows
            while (line != null){
                ArrayList<Cell> row = new ArrayList<>();
                for (int curCol = 0; curCol < line.length(); curCol++){
                    char c = line.charAt(curCol);
                    if(c == ' '){
                        row.add(new Cell(curRow, curCol, Cell.Type.Empty, false, c));
                    }
                    else if (c == '+'){
                        row.add(new Cell(curRow, curCol, Cell.Type.Wall, true, c));
                    }
                    else if ('0' <= c && c <= '9'){
                        Cell agentCell = new Cell(curRow, curCol, Cell.Type.Agent, true, c);
                        row.add(agentCell);
                        agentCells.add(agentCell);
                    }
                    else if ('A' <= c && c <= 'Z'){
                        Cell boxCell = new Cell(curRow, curCol, Cell.Type.Box, true, c);
                        row.add(boxCell);
                        boxCells.add(boxCell);
                    }
                    else if ('a' <= c && c <= 'z'){
                        Cell goalCell = new Cell(curRow, curCol, Cell.Type.Goal, false, c);
                        row.add(goalCell);
                        goalCells.add(goalCell);
                    }
                }
                level.add(row); // add a row to the level
                curRow++; // ..and advance to the next row
                line = bufferedReader.readLine();
            }
            return level;
        }
        finally {
            bufferedReader.close();
        }
    }

    public ArrayList<Cell> getGoalCells(){
        return goalCells;
    }

    public ArrayList<Cell> getBoxCells(){
        return boxCells;
    }

    public ArrayList<Cell> getAgentCells(){
        return agentCells;
    }
}
