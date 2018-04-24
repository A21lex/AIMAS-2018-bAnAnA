/**
 * Created by aleksandrs on 4/4/18.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class LevelReader {

    private static ArrayList<Cell.CoordinatesPair> goalCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> boxCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> agentCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> spaceCells = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> tunnelCells = new ArrayList<>();

    public static void main(String[] args){
        LevelReader levelReader = new LevelReader();
        String pathToLevel = "testNode.lvl"; // << Set path to level file here
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = true; // agents are obstacles
        boolean bObstacles = true; // boxes are obstacles
        Cell startingCell = new Cell(1,1); // << Set starting cell
        Cell finishingCell = new Cell(1,17); // << Set finishing cell

        ArrayList<ArrayList<Cell>> level = null;
        try {
            level = levelReader.getLevel(pathToLevel);
        } catch (IOException e) {
            System.out.println("Probably incorrect path");
        }
        System.out.println("Printing level");
        for (ArrayList<Cell> row: level){
            for (Cell cell: row){
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        for (ArrayList<Cell> row: level){
            for (Cell cell: row){
                Entity entity = cell.getEntity();
                if (entity instanceof Box){
                    Box box = (Box) entity;
                    System.out.println(box);
                }
                if (entity instanceof Agent){
                    Agent agent = (Agent) entity;
                    System.out.println(agent);
                }
                System.out.print(cell.getEntity() + " ");
            }
            System.out.println();
        }

        System.out.println("boxes: " + boxCellCoords);
        System.out.println("goals " + goalCellCoords);

        PathFinder pathFinder = new PathFinder();
        boolean pathExists = pathFinder.pathExists(level, startingCell, finishingCell,
                wObstacles, aObstacles, bObstacles);
        System.out.println("path exists = " + pathExists);

    }

    // read file
    public static ArrayList<ArrayList<Cell>> getLevel(String pathToLevel) throws IOException{
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
                        // Add a new cell of type SPACE with nothing(NULL) on it and which is NOT a goal(thus c=0)
                        Cell cell = new Cell(curRow, curCol, Type.SPACE, null, '0');
                        row.add(cell);
                        //row.add(new Cell(curRow, curCol, Cell.Type.Empty, false, c));
                        //row.add(new Cell(curRow, curCol, Cell.Type.Empty, false, c));
                        //TODO: check for last row
                        if(curCol!=0 && curRow!=0 && curCol!=line.length()-1){
                            spaceCells.add(new Cell.CoordinatesPair(cell));
                        }
                    }
                    else if (c == '+'){
                        row.add(new Cell(curRow, curCol, Type.WALL, null, '0'));
                        //row.add(new Cell(curRow, curCol, Cell.Type.WALL, true, c));
                    }
                    else if ('0' <= c && c <= '9'){
                        // Create a cell with coords only as agent will be created later and he needs a cell
                        Cell cell = new Cell(curRow, curCol);
                        Agent agent = new Agent(cell, Color.BLUE, Character.getNumericValue(c));
                        cell.setEntity(agent);
                        cell.setGoalLetter('0'); // this is a non goal cell
                        cell.setType(Type.SPACE);
                        //Cell agentCell = new Cell(curRow, curCol, Cell.Type.Agent, true, c);
                        row.add(cell);
                        agentCellCoords.add(new Cell.CoordinatesPair(cell));
                        spaceCells.add(new Cell.CoordinatesPair(cell));
                    }
                    else if ('A' <= c && c <= 'Z'){
                        Cell cell = new Cell(curRow, curCol);
                        Box box = new Box(cell, Color.BLUE, c);
                        cell.setEntity(box);
                        cell.setGoalLetter('0');
                        cell.setType(Type.SPACE);
                       // Cell boxCell = new Cell(curRow, curCol, Cell.Type.Box, true, c);
                        row.add(cell);
                        boxCellCoords.add(new Cell.CoordinatesPair(cell));
                        spaceCells.add(new Cell.CoordinatesPair(cell));
                    }
                    else if ('a' <= c && c <= 'z'){
                        Cell cell = new Cell(curRow, curCol, Type.SPACE, null, c);
                        //Cell goalCell = new Cell(curRow, curCol, Cell.Type.Goal, false, c);
                        row.add(cell);
                        goalCellCoords.add(new Cell.CoordinatesPair(cell));
                        spaceCells.add(new Cell.CoordinatesPair(cell));
                    }
                }
                level.add(row); // add a row to the level
                curRow++; // ..and advance to the next row
                line = bufferedReader.readLine();
            }

            //determine tunnel cells
            for (Cell.CoordinatesPair spc: spaceCells) {
                // wall on top and bellow or  wall on sides
                if ((level.get(spc.getX() - 1).get(spc.getY()).getType().equals(Type.WALL) && level.get(spc.getX() + 1).get(spc.getY()).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX()).get(spc.getY() - 1).getType().equals(Type.WALL) && level.get(spc.getX()).get(spc.getY() + 1).getType().equals(Type.WALL))) {
                    tunnelCells.add(spc);
                }

                //wall on diagonals
                if ((level.get(spc.getX() - 1).get(spc.getY() - 1).getType().equals(Type.WALL) && level.get(spc.getX() + 1).get(spc.getY() + 1).getType().equals(Type.WALL))
                        || (level.get(spc.getX() + 1).get(spc.getY() - 1).getType().equals(Type.WALL) && level.get(spc.getX() - 1).get(spc.getY() + 1).getType().equals(Type.WALL))) {
                    tunnelCells.add(spc);
                }

                //one wall on side and one wall in diagonal
                //TODO make hasmap to avoid duplicates; disegard corners
                if ((level.get(spc.getX()).get(spc.getY() - 1).getType().equals(Type.WALL)
                        && level.get(spc.getX() + 1).get(spc.getY() + 1).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX()).get(spc.getY() - 1).getType().equals(Type.WALL)
                                && level.get(spc.getX() - 1).get(spc.getY() + 1).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX()).get(spc.getY() + 1).getType().equals(Type.WALL)
                                && level.get(spc.getX() - 1).get(spc.getY() - 1).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX()).get(spc.getY() + 1).getType().equals(Type.WALL)
                                && level.get(spc.getX() + 1).get(spc.getY() - 1).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX() - 1).get(spc.getY()).getType().equals(Type.WALL)
                                && level.get(spc.getX() + 1).get(spc.getY() - 1).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX() - 1).get(spc.getY()).getType().equals(Type.WALL)
                                && level.get(spc.getX() + 1).get(spc.getY() + 1).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX() + 1).get(spc.getY()).getType().equals(Type.WALL)
                                && level.get(spc.getX() - 1).get(spc.getY() + 1).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX() + 1).get(spc.getY()).getType().equals(Type.WALL)
                                && level.get(spc.getX() - 1).get(spc.getY() - 1).getType().equals(Type.WALL))) {
                    tunnelCells.add(spc);
                }
            }
            return level;
        }
        finally {
            bufferedReader.close();
        }
    }

    public static ArrayList<Cell.CoordinatesPair> getGoalCellCoords(){
        return goalCellCoords;
    }

    public static ArrayList<Cell.CoordinatesPair> getBoxCellCoords(){
        return boxCellCoords;
    }

    public static ArrayList<Cell.CoordinatesPair> getAgentCellCoords(){
        return agentCellCoords;
    }

    public static ArrayList<Cell.CoordinatesPair> getTunnelCellCoords(){
        return tunnelCells;
    }
}
