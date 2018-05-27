package aimas; /**
 * Created by aleksandrs on 4/4/18.
 */

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.Type;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Color;
import aimas.board.entities.Entity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public final class LevelReader {

    private static ArrayList<CoordinatesPair> goalCellCoords = new ArrayList<>();
    private static ArrayList<CoordinatesPair> boxCellCoords = new ArrayList<>();
    private static ArrayList<CoordinatesPair> agentCellCoords = new ArrayList<>();
    private static ArrayList<CoordinatesPair> spaceCellCoords = new ArrayList<>();
    private static ArrayList<CoordinatesPair> tunnelCellCoords = new ArrayList<>();

    // read file - different boxes get different IDs...
    public static ArrayList<ArrayList<Cell>> getLevel(String pathToLevel) throws IOException{
        ArrayList<ArrayList<Cell>> level = new ArrayList<>(); // store level here
        int id = 0; // for unique identification of boxes
        BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToLevel));
        try {
            String line = bufferedReader.readLine();
            // Read lines specifying colors
            HashMap<Character, Color> objectColors = new HashMap<>();
            String colorString;
            while ( line.matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
                line = line.replaceAll( "\\s", "" );
                colorString = line.split( ":" )[0];

                for ( String coloredObject : line.split( ":" )[1].split( "," ) ) {
                    objectColors.put(coloredObject.charAt(0), Color.valueOf(colorString.toUpperCase()));
                }
                line = bufferedReader.readLine();
            }
            final Color DEFAULT_COLOR = Color.BLUE; // dabu dee dabu dai
            int curRow = 0; // keep track of rows
            while (line != null){
                ArrayList<Cell> row = new ArrayList<>();
                for (int curCol = 0; curCol < line.length(); curCol++){
                    char c = line.charAt(curCol);
                    if(c == ' '){
                        // Add a new cell of type SPACE with nothing(NULL) on it and which is NOT a goal(thus c=0)
                        Cell cell = new Cell(curRow, curCol, Type.SPACE, null, '0');
                        row.add(cell);
                        //row.add(new Cell(curRow, curCol, Cell.CommandType.Empty, false, c));
                        //row.add(new Cell(curRow, curCol, Cell.CommandType.Empty, false, c));
                        //TODO: check for last row
                        if(curCol!=0 && curRow!=0 && curCol!=line.length()-1){
                            spaceCellCoords.add(new CoordinatesPair(cell));
                        }
                    }
                    else if (c == '+'){
                        row.add(new Cell(curRow, curCol, Type.WALL, null, '0'));
                        //row.add(new Cell(curRow, curCol, Cell.CommandType.WALL, true, c));
                    }
                    else if ('0' <= c && c <= '9'){
                        // Create a cell with coords only as agent will be created later and he needs a cell
                        Cell cell = new Cell(curRow, curCol);
                        Agent agent = new Agent(objectColors.getOrDefault(c, DEFAULT_COLOR),
                                Character.getNumericValue(c));
                        cell.setEntity(agent);
                        cell.setGoalLetter('0'); // this is a non goal cell
                        cell.setType(Type.SPACE);
                        //Cell agentCell = new Cell(curRow, curCol, Cell.CommandType.Agent, true, c);
                        row.add(cell);
                        agentCellCoords.add(new CoordinatesPair(cell));
                        spaceCellCoords.add(new CoordinatesPair(cell));
                    }
                    else if ('A' <= c && c <= 'Z'){
                        Cell cell = new Cell(curRow, curCol);
                        Box box = new Box(objectColors.getOrDefault(c, DEFAULT_COLOR), c, id);
                        id++; // increase id. every box will have a different one.
                        cell.setEntity(box);
                        cell.setGoalLetter('0');
                        cell.setType(Type.SPACE);
                        // Cell boxCell = new Cell(curRow, curCol, Cell.CommandType.Box, true, c);
                        row.add(cell);
                        boxCellCoords.add(new CoordinatesPair(cell));
                        spaceCellCoords.add(new CoordinatesPair(cell));
                    }
                    else if ('a' <= c && c <= 'z'){
                        Cell cell = new Cell(curRow, curCol, Type.SPACE, null, c);
                        //Cell goalCell = new Cell(curRow, curCol, Cell.CommandType.Goal, false, c);
                        row.add(cell);
                        goalCellCoords.add(new CoordinatesPair(cell));
                        spaceCellCoords.add(new CoordinatesPair(cell));
                    }
                }
                level.add(row); // add a row to the level
                curRow++; // ..and advance to the next row
                line = bufferedReader.readLine();
            }

            //determine tunnel cells
            /*for (CoordinatesPair spc: spaceCellCoords) {
                // wall on top and bellow or  wall on sides
                if ((level.get(spc.getX() - 1).get(spc.getY()).getType().equals(Type.WALL) && level.get(spc.getX() + 1).get(spc.getY()).getType().equals(Type.WALL)) ||
                        (level.get(spc.getX()).get(spc.getY() - 1).getType().equals(Type.WALL) && level.get(spc.getX()).get(spc.getY() + 1).getType().equals(Type.WALL))) {
                    tunnelCellCoords.add(spc);
                }

                //wall on diagonals
                if ((level.get(spc.getX() - 1).get(spc.getY() - 1).getType().equals(Type.WALL) && level.get(spc.getX() + 1).get(spc.getY() + 1).getType().equals(Type.WALL))
                        || (level.get(spc.getX() + 1).get(spc.getY() - 1).getType().equals(Type.WALL) && level.get(spc.getX() - 1).get(spc.getY() + 1).getType().equals(Type.WALL))) {
                    tunnelCellCoords.add(spc);
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
                    tunnelCellCoords.add(spc);
                }
            } */
            return level;
        }
        finally {
            bufferedReader.close();
        }
    }

    public static ArrayList<CoordinatesPair> getGoalCellCoords(){
        return goalCellCoords;
    }

    public static ArrayList<CoordinatesPair> getBoxCellCoords(){
        return boxCellCoords;
    }

    public static ArrayList<CoordinatesPair> getAgentCellCoords(){
        return agentCellCoords;
    }

    public static ArrayList<CoordinatesPair> getTunnelCellCoords(){
        return tunnelCellCoords;
    }
    public static ArrayList<CoordinatesPair> getSpaceCellCoords(){
        return spaceCellCoords;
    }


}
