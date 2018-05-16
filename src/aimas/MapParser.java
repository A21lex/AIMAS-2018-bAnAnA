package aimas;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.Type;
import aimas.board.entities.*;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import static oracle.jrockit.jfr.events.Bits.doubleValue;


public class MapParser{
    public static Graph<CoordinatesPair, CoordinatesPairPair> mapGraph;
    public static  Map<CoordinatesPair, Double> cellWeights;
    public ArrayList<ArrayList<Cell>> level;

    public MapParser(ArrayList<ArrayList<Cell>> level){
        this.level=level;
    }
    public void parseMap(ArrayList<CoordinatesPair> spaceCellsCoords, ArrayList<ArrayList<Cell>> level){
        //compute graph
        this.mapGraph = this.makeGraph(spaceCellsCoords, level);
        //computeBetweenessCentrality(mapGraph);
        cellWeights= normalizeBC(computeBetweenessCentrality(mapGraph));
        // compute shortest distances between all nodes (space cells)
        // computeDijkstraShortestPath();
    }

    //method that builds a graph of cells (coordinates pair)
    public Graph<CoordinatesPair, CoordinatesPairPair> makeGraph(ArrayList<CoordinatesPair> spaceCellCoords, ArrayList<ArrayList<Cell>> level) {
        Graph<CoordinatesPair, CoordinatesPairPair> mapGraph;
        mapGraph = new UndirectedSparseGraph<>();
        //add space cells as vertices
        for (CoordinatesPair pair : spaceCellCoords) {
            mapGraph.addVertex(pair);
        }
        //if cells are neighbours add edge
        for (CoordinatesPair pair : spaceCellCoords) {
            ArrayList<CoordinatesPair> neighbours;
            neighbours=pair.getNeighbouringCoords(pair);
            for (CoordinatesPair neighbour : neighbours) {
                if (spaceCellCoords.contains(neighbour)) {
                    mapGraph.addEdge(new CoordinatesPairPair(pair, neighbour), pair, neighbour);
                }
            }
        }
        return mapGraph;
    }

    //method that calculates the betweeness centrality
    public Map<CoordinatesPair, Double> computeBetweenessCentrality(Graph<CoordinatesPair, CoordinatesPairPair> graph) {
        Map<CoordinatesPair, Double> weights = new HashMap<>();
        BetweennessCentrality<CoordinatesPair, CoordinatesPairPair> bc = new BetweennessCentrality<>(graph);
        bc.setRemoveRankScoresOnFinalize(false);
        bc.evaluate();
        for (CoordinatesPair pair : graph.getVertices()) {
            weights.put(pair, bc.getVertexRankScore(pair));
        }
        return weights;
    }

    public Map<CoordinatesPair, Double> normalizeBC( Map<CoordinatesPair, Double> weights) {
        Map<CoordinatesPair, Double> normalBc = new HashMap<>();
        double highest = 0.0;

        for (Map.Entry<CoordinatesPair, Double> entry : weights.entrySet()) {
            if (entry.getValue() > highest)
                highest = entry.getValue();
        }
        for (Map.Entry<CoordinatesPair, Double> entry : weights.entrySet()) {
            normalBc.put(entry.getKey(), (entry.getValue() / highest)*100);
        }
        return normalBc;
    }

    public Map<CoordinatesPair,Double> getCellWeights(){ return cellWeights; }
    public void setCellWeights(Map<CoordinatesPair, Double> cellWeights) { this.cellWeights = cellWeights; }

    //method that calculates shortest paths between all nodes of the graph, using the Dijkstra algoritm
    /*private void computeDijkstraShortestPath() {
        MapParser.dijkstraShortestPath = new DijkstraShortestPath<Cell, CellPair>(mapGraph);
        for (Cell cellA : mapGraph.getVertices()) {
            distanceMap.put(cellA, dijkstraShortestPath.getDistanceMap(cellA));
        }
        System.out.println("Dijkstra completed... ");
    }*/


    //utils
    // check whether coords pair cell is wall
    public boolean isWall(CoordinatesPair pair){
        return level.get(pair.getX()).get(pair.getY()).getType().equals(Type.WALL);
    }

    //get the space neighbours of a pair of coords.
    private ArrayList<CoordinatesPair> getSpaceCellNeighborCoords(CoordinatesPair pair) {
        ArrayList <CoordinatesPair> spaceCellNeighbourCoords = new ArrayList<>();
        ArrayList <CoordinatesPair> neighbours = pair.getNeighbouringCoords(pair);
        for (CoordinatesPair neighbourg: neighbours){
            try {
                if (level.get(neighbourg.getX()).get(neighbourg.getY()).getType().equals(Type.SPACE))
                    spaceCellNeighbourCoords.add(neighbourg);
            }
            catch (IndexOutOfBoundsException e){
                //do nothing..
            }
        }
        return spaceCellNeighbourCoords;
    }

    //count number of surrounding walls for a cell c with coordinates i,j
    public int countWallNeighbours(CoordinatesPair pair) {
        int walls=0;
        for (CoordinatesPair neighbourg: pair.getNeighbouringCoords(pair)){
            try {
                if (level.get(neighbourg.getX()).get(neighbourg.getY()).getType().equals(Type.WALL))
                    walls++;
            }
            catch (IndexOutOfBoundsException e){
                //do nothing..
            }
        }
        return walls;
    }

    /*might be reduntant
    public ArrayList<ArrayList<Cell>> findDeadEndTunnels (){
        ArrayList <ArrayList<Cell>> dead_end_tunnels = new ArrayList();
        ArrayList <ArrayList<Cell>> isolated_tunnels =new ArrayList<>();
        for (CoordinatesPair pair : LevelReader.getSpaceCellCoords()){
            //if the cell is surrounded by 4 walls then it is isolated
            if (countWallNeighbours(pair)==4){
                cell.setWeight(-1);
                ArrayList<Cell> isolatedTunnel = new ArrayList<>();
                isolated_tunnels.add(isolatedTunnel);
            }
            //if the cell is surrounded by 3 walls, is the end of a dead-end tunnel
            if (countWallNeighbours(cell)==3) {
                cell.setWeight(0);
                ArrayList<Cell> deadEndTunnel = new ArrayList<>();
                deadEndTunnel.add(cell);

                //search recursively for the rest of the tunnel, from the neighbour
                Cell neighbour = getSpaceCellNeighbors(cell).get(0);
                //deadEndTunnel.add(neighbour);
                discoverDeadEndTunnel(cell,neighbour,deadEndTunnel);
                dead_end_tunnels.add(deadEndTunnel);
            }

        }
        return dead_end_tunnels;
    }

    public ArrayList<Cell> discoverDeadEndTunnel (Cell initial, Cell neighbour, ArrayList<Cell> tunnel) {
        if (countWallNeighbours(neighbour) == 2) {
            neighbour.setWeight(initial.getWeight() + 1);
            tunnel.add(neighbour);

            ArrayList<Cell> neighborNeighbours = getSpaceCellNeighbors(neighbour);
            for (Cell n : neighborNeighbours) {
                if (n.getI()==initial.getI()&&n.getJ() == initial.getJ())
                    continue;
                else {
                    return discoverDeadEndTunnel(neighbour, n, tunnel);
                }

            }
        }
        // articulation square, tunnel done
        //TODO HashSet/Map
        else if (countWallNeighbours(neighbour) == 1) {
            this.artSquares.add(neighbour);
            neighbour.setWeight(50);
            return tunnel;
            //TODO art square
        }

        //dead-end, tunnel done
        else if (countWallNeighbours(neighbour) == 3) {
            return tunnel;
            //TODO is isolated?
        }

        return null;

    }

    public ArrayList<Cell> getArtSquares(){
        return this.artSquares;
    }*/

}
