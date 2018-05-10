/**
 * Created by aleksandrs on 4/28/18.
 *//*


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

*/
/**
 * Client implemented to interact with the environment server
 *//*

public final class Client {
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private ArrayList<Agent> agents = new ArrayList<>(); // TODO fix this later to be like in levelreader
    public static ArrayList<ArrayList<Cell>> level = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> goalCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> boxCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> agentCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> spaceCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> tunnelCellCoords = new ArrayList<>();

    private void readLevelToFile() throws IOException{
        Map< Character, Color > colors = new HashMap<>();
        String line, color;

        // Read lines specifying colors
        while ( ( line = in.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            color = line.split( ":" )[0];
            Color enumColor = null;
            for (Color c : Color.values()){
                if (c.name().equalsIgnoreCase(color)){
                    enumColor = c;
                }
            }
            for ( String id : line.split( ":" )[1].split( "," ) )
                colors.put( id.charAt( 0 ), enumColor );
        }
        // Read lines specifying level layout
        int curRow = 0;
        while (!line.equals("")){
            ArrayList<Cell> row = new ArrayList<>();
            for ( int curCol = 0; curCol < line.length(); curCol++ ) {
                char c = line.charAt( curCol );
                if(c == ' '){
                    // Add a new cell of type SPACE with nothing(NULL) on it and which is NOT a goal(thus c=0)
                    Cell cell = new Cell(curRow, curCol, CommandType.SPACE, null, '0');
                    row.add(cell);
                    //row.add(new Cell(curRow, curCol, Cell.CommandType.Empty, false, c));
                    //row.add(new Cell(curRow, curCol, Cell.CommandType.Empty, false, c));
                    //TODO: check for last row
                    if(curCol!=0 && curRow!=0 && curCol!=line.length()-1){
                        spaceCellCoords.add(new Cell.CoordinatesPair(cell));
                    }
                }
                else if (c == '+'){
                    row.add(new Cell(curRow, curCol, CommandType.WALL, null, '0'));
                    //row.add(new Cell(curRow, curCol, Cell.CommandType.WALL, true, c));
                }
                else if ( '0' <= c && c <= '9' ) {
                    // Create a cell with coords only as agent will be created later and he needs a cell
                    Cell cell = new Cell(curRow, curCol);
                    Color agentColor = colors.get(c);
                    Agent agent = new Agent(agentColor, Character.getNumericValue(c));
                    cell.setEntity(agent);
                    cell.setGoalLetter('0'); // this is a non goal cell
                    cell.setType(CommandType.SPACE);
                    //Cell agentCell = new Cell(curRow, curCol, Cell.CommandType.Agent, true, c);
                    row.add(cell);
                    agentCellCoords.add(new Cell.CoordinatesPair(cell));
                    spaceCellCoords.add(new Cell.CoordinatesPair(cell));

                    agents.add(agent); // DO WE NEED THIS?
                }
                else if ('A' <= c && c <= 'Z'){
                    Cell cell = new Cell(curRow, curCol);
                    Color boxColor = colors.get(c);
                    Box box = new Box(boxColor, c);
                    cell.setEntity(box);
                    cell.setGoalLetter('0');
                    cell.setType(CommandType.SPACE);
                    // Cell boxCell = new Cell(curRow, curCol, Cell.CommandType.Box, true, c);
                    row.add(cell);
                    boxCellCoords.add(new Cell.CoordinatesPair(cell));
                    spaceCellCoords.add(new Cell.CoordinatesPair(cell));
                }
                else if ('a' <= c && c <= 'z'){
                    Cell cell = new Cell(curRow, curCol, CommandType.SPACE, null, c);
                    //Cell goalCell = new Cell(curRow, curCol, Cell.CommandType.Goal, false, c);
                    row.add(cell);
                    goalCellCoords.add(new Cell.CoordinatesPair(cell));
                    spaceCellCoords.add(new Cell.CoordinatesPair(cell));
                }
            }
            level.add(row); // add a row to the level
            curRow++; // ..and advance to the next row
            line = in.readLine();
        }
        //determine tunnel cells
        for (Cell.CoordinatesPair spc: spaceCellCoords) {
            // wall on top and bellow or  wall on sides
            if ((level.get(spc.getX() - 1).get(spc.getY()).getType().equals(CommandType.WALL) && level.get(spc.getX() + 1).get(spc.getY()).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX()).get(spc.getY() - 1).getType().equals(CommandType.WALL) && level.get(spc.getX()).get(spc.getY() + 1).getType().equals(CommandType.WALL))) {
                tunnelCellCoords.add(spc);
            }

            //wall on diagonals
            if ((level.get(spc.getX() - 1).get(spc.getY() - 1).getType().equals(CommandType.WALL) && level.get(spc.getX() + 1).get(spc.getY() + 1).getType().equals(CommandType.WALL))
                    || (level.get(spc.getX() + 1).get(spc.getY() - 1).getType().equals(CommandType.WALL) && level.get(spc.getX() - 1).get(spc.getY() + 1).getType().equals(CommandType.WALL))) {
                tunnelCellCoords.add(spc);
            }

            //one wall on side and one wall in diagonal
            //TODO make hasmap to avoid duplicates; disegard corners
            if ((level.get(spc.getX()).get(spc.getY() - 1).getType().equals(CommandType.WALL)
                    && level.get(spc.getX() + 1).get(spc.getY() + 1).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX()).get(spc.getY() - 1).getType().equals(CommandType.WALL)
                            && level.get(spc.getX() - 1).get(spc.getY() + 1).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX()).get(spc.getY() + 1).getType().equals(CommandType.WALL)
                            && level.get(spc.getX() - 1).get(spc.getY() - 1).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX()).get(spc.getY() + 1).getType().equals(CommandType.WALL)
                            && level.get(spc.getX() + 1).get(spc.getY() - 1).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX() - 1).get(spc.getY()).getType().equals(CommandType.WALL)
                            && level.get(spc.getX() + 1).get(spc.getY() - 1).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX() - 1).get(spc.getY()).getType().equals(CommandType.WALL)
                            && level.get(spc.getX() + 1).get(spc.getY() + 1).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX() + 1).get(spc.getY()).getType().equals(CommandType.WALL)
                            && level.get(spc.getX() - 1).get(spc.getY() + 1).getType().equals(CommandType.WALL)) ||
                    (level.get(spc.getX() + 1).get(spc.getY()).getType().equals(CommandType.WALL)
                            && level.get(spc.getX() - 1).get(spc.getY() - 1).getType().equals(CommandType.WALL))) {
                tunnelCellCoords.add(spc);
            }
        }
    }

    public Client() throws IOException{
        readLevelToFile();
    }

    public LinkedList<Command> Solve(){
        // whatever we do to get nodes leading to the solution of the level must come here

    }

    public static void main(String[] args) {
        System.err.println("Hello from Client. Sending this using error output stream.");
        try {
            Client client = new Client();
            while (client.update()){
                ;
            }
        }
        catch (IOException ex){
            // probably nowhere to write
        }

    }

    private boolean update() throws IOException{
//        String jointAction = "[";
//
//        for ( int i = 0; i < agents.size() - 1; i++ )
//            jointAction += agents.get( i ).act() + ",";
//
//        jointAction += agents.get( agents.size() - 1 ).act() + "]";
//
//        // Place message in buffer
//        System.out.println( jointAction );

        String jointAction = "[";

        for (int i = 0; i < agents.size() - 1; i++){
            jointAction += agents.get(i).act() + ",";
        }
        jointAction += agents.get(agents.size() - 1).act() + "]";
        System.out.println(jointAction);
        // Flush buffer
        System.out.flush();

        // Disregard these for now, but read or the server stalls when its output buffer gets filled!
        String percepts = in.readLine();
        if ( percepts == null )
            return false;

        return true;
    }
}
*/
