/**
 * Created by Arturs Gumenuks on 08.04.2018.
 */

import java.io.IOException;
import java.util.*;

public class GoalPrioritizer {

    private static ArrayList<Cell.CoordinatesPair> goalCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> boxCellCoords = new ArrayList<>();
    private static ArrayList<Cell.CoordinatesPair> agentCellCoords = new ArrayList<>();

    private static ArrayList<Cell> goalCells = new ArrayList<>();
    private static ArrayList<Cell> boxCells = new ArrayList<>();
    private static ArrayList<Cell> agentCells = new ArrayList<>();

    private static ArrayList<ArrayList<Cell>> globalCombs = new ArrayList<>();
    private static ArrayList<String> instantiations = new ArrayList<>();
    private static ArrayList<ArrayList<Cell>> level = new ArrayList<>();

    public static void main(String[] args){
        String pathToLevel = "C:\\wTESTING\\AIlevels\\SACrunch.lvl"; // << Set path to level file here
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = true; // agents are obstacles
        boolean bObstacles = true; // boxes are obstacles
        Cell startingCell = new Cell(1,1); // << Set starting cell
        Cell finishingCell = new Cell(1,3); // << Set finishing cell

        //ArrayList<ArrayList<Cell>> level = null;
        try {
            level = LevelReader.getLevel(pathToLevel);
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

        boxCellCoords = LevelReader.getBoxCellCoords();
        goalCellCoords = LevelReader.getGoalCellCoords();
        agentCellCoords = LevelReader.getAgentCellCoords();

        for (Cell.CoordinatesPair coordinatesPair : boxCellCoords){
            boxCells.add(getCellAt(level,coordinatesPair));
        }
        for (Cell.CoordinatesPair coordinatesPair : goalCellCoords){
            goalCells.add(getCellAt(level,coordinatesPair));
        }
        for (Cell.CoordinatesPair coordinatesPair : agentCellCoords){
            agentCells.add(getCellAt(level,coordinatesPair));
        }
        System.out.println("boxes: " + boxCells);
        System.out.println("goals " + goalCells);
        System.out.println("agents " + agentCells);

        PathFinder pathFinder = new PathFinder();
        boolean pathExists = pathFinder.pathExists(level, startingCell, finishingCell,
                wObstacles, aObstacles, bObstacles);
        System.out.println("path exists = " + pathExists);

//        GoalPrioritizer goalPrioritizer = new GoalPrioritizer();
//        ArrayList<ArrayList<Cell>> goalPartOrd = goalPrioritizer.prioritizeGoals(levelReader, pathFinder, level);
//
//        System.out.println("Ordering relations found: " + goalPartOrd.size());
//        for (ArrayList<Cell> item: goalPartOrd){
//            for (Cell relCell: item){
//                System.out.print(relCell.getI() + ":" + relCell.getJ() + ":" + relCell.getGoalLetter()+ " ");
//            }
//            System.out.println();
//        }
        GoalPrioritizer goalPrioritizer = new GoalPrioritizer();
        HashMap<Cell, ArrayList<ArrayList<ArrayList<Cell>>>> reachabilityMap =
                goalPrioritizer.findLinearizations(pathFinder, level);

        ArrayList<ArrayList<OrdConstraint>> constraints = new ArrayList<>();

        for (HashMap.Entry<Cell,ArrayList<ArrayList<ArrayList<Cell>>>> entry : reachabilityMap.entrySet()) {
            //System.out.println("Goal cell: " + goalPrioritizer.makeCellName(entry.getKey()));
            ArrayList<ArrayList<ArrayList<Cell>>> val = entry.getValue();
            // System.out.println(val.get(1).size());
            for (int i=0; i<val.size(); i++){
                for(int j =0; j<val.get(i).size(); j++) {
                    constraints.add(new ArrayList<OrdConstraint>());
                    for (int k=0;k<val.get(i).get(j).size(); k++){
                        constraints.get(constraints.size()-1).add(new OrdConstraint(entry.getKey(),val.get(i).get(j).get(k)));
                    }
                    //System.out.println();
                }
            }
        }
        System.out.println();
        System.out.println("koo");
        for (int i=0; i<constraints.size(); i++){
            for (int j=0; j<constraints.get(i).size(); j++){
                String strConstraint = goalPrioritizer.makeCellName(constraints.get(i).get(j).getBeforeCell()) +
                        " < " + goalPrioritizer.makeCellName(constraints.get(i).get(j).getAfterCell());
                System.out.print(strConstraint+" ");
            }
            System.out.println();
        }

       // HashMap<OrdConstraint, Boolean> constrInstantiations = new HashMap<>();
        ArrayList<OrdConstraint> uniqueConstraints = new ArrayList<>();

        for (int i = 0; i<constraints.size(); i++){
            for (int j = 0; j<constraints.get(i).size(); j++){
                System.out.println(constraints.get(i).get(j).getBeforeCell() + " " + constraints.get(i).get(j).getAfterCell());
                if(!goalPrioritizer.constraintPresented(constraints.get(i).get(j), uniqueConstraints, true, true)){
                    //constrInstantiations.put(constraints.get(i).get(j), false);
                    System.out.println("Supposed to be printed devil may cry");
                    uniqueConstraints.add(constraints.get(i).get(j));
                }
            }
        }
        System.out.println("Printing unique constraints");
       // for (HashMap.Entry<OrdConstraint, Boolean> entry : constrInstantiations.entrySet()) {
        for (OrdConstraint entry: uniqueConstraints){
            String lhs = goalPrioritizer.makeCellName(entry.getBeforeCell());
            String rhs = goalPrioritizer.makeCellName(entry.getAfterCell());
            System.out.println(lhs+" < "+rhs);
        }

        // generating instantiations of true and false
        final int n = uniqueConstraints.size();
        for (int i = 0; i < Math.pow(2, n); i++) {
            String bin = Integer.toBinaryString(i);
            while (bin.length() < n)
                bin = "0" + bin;
            //System.out.println(bin);
            instantiations.add(bin);
        }

        ArrayList<String> satisfInstantiations = new ArrayList<>(); // satisfying instantiation
        // evaluate
        // if evaluates to true add to satisfInst...
        // check transitivity thing

        String someInst = instantiations.get(0);
       // System.out.println(someInst);
        System.out.println();
        for (int i = 0; i<instantiations.size(); i++){
            System.out.print(instantiations.get(i)+" ");
            boolean evalRes = goalPrioritizer.evaluate(constraints, uniqueConstraints, instantiations.get(i));
            System.out.print(evalRes);
            System.out.println();
            if (evalRes) satisfInstantiations.add(instantiations.get(i));
        }

//        ArrayList<String> satisfInstantiationsTransitRespected = new ArrayList<>();
//        for (int i = 0; i<satisfInstantiations.size(); i++){
//            if(goalPrioritizer.transitivityRespected(uniqueConstraints,satisfInstantiations.get(i)))
//                satisfInstantiationsTransitRespected.add(satisfInstantiations.get(i));
//        }
//        System.out.println("Satisfying instantiations with transitivity respected");
//        for (int i = 0; i<satisfInstantiationsTransitRespected.size(); i++){
//            System.out.println(satisfInstantiationsTransitRespected.get(i));
//        }

        ArrayList<ArrayList<OrdConstraint>> properConstrListList = new ArrayList<>();
        for (int i = 0; i<satisfInstantiations.size(); i++){
            ArrayList<OrdConstraint> properConstrList =
                    goalPrioritizer.transitivityRespected(uniqueConstraints,satisfInstantiations.get(i));
            if(!properConstrList.isEmpty()) properConstrListList.add(properConstrList);
        }
        System.out.println();
        System.out.println("Possible goal satisfaction orders: ");
        // FINDING LINEARIZATIONS
        ArrayList<ArrayList<Cell>> solvingSequences = goalPrioritizer.findSolvingSequences(goalCells, properConstrListList);
        for (ArrayList<Cell> solvingSequence : solvingSequences){
            for (Cell cell : solvingSequence){
                System.out.print(goalPrioritizer.makeCellName(cell) + " ");
            }
            System.out.println();
        }

    }

    public ArrayList<ArrayList<Cell>> findSolvingSequences(ArrayList<Cell> allGoals,
                                                           ArrayList<ArrayList<OrdConstraint>> properConstrListList){
        ArrayList<ArrayList<Cell>> solvingSequences = new ArrayList<>();
        for (int i = 0; i<9999; i++){
            Collections.shuffle(allGoals);
            ArrayList<Cell> sequence = new ArrayList<Cell>(allGoals);

            for (int j = 0; j<properConstrListList.size(); j++){
                if (constraintsRespected(sequence, properConstrListList.get(j))
                        && !solvingSequences.contains(sequence)) solvingSequences.add(sequence);
            }
        }
        return solvingSequences;
    }

    public boolean constraintsRespected(ArrayList<Cell> sequence, ArrayList<OrdConstraint> properConstraList){
        for (int i = 0; i<sequence.size(); i++){
            for (int j = i; j<sequence.size(); j++) {
                OrdConstraint relation = new OrdConstraint(sequence.get(i), sequence.get(j));
                if (constraintPresented(relation, properConstraList, false, true)) return false;
            }
        }
        return true;
    }

    public ArrayList<OrdConstraint> transitivityRespected(ArrayList<OrdConstraint> uniqueConstraints,String satisfInstantiation){
        ArrayList<OrdConstraint> uniqueConstraintsPos = new ArrayList<>(uniqueConstraints);
        for (int i = 0; i<uniqueConstraints.size(); i++){
            if (satisfInstantiation.charAt(i) == '0') {
               // Cell tempCell = uniqueConstraints.get(i).getBeforeCell();
                OrdConstraint transitConstraint = new OrdConstraint(uniqueConstraints.get(i).getAfterCell(),
                        uniqueConstraints.get(i).getBeforeCell());
                //uniqueConstraintsPos.get(i).setBeforeCell(uniqueConstraints.get(i).getAfterCell());
                //uniqueConstraintsPos.get(i).setAfterCell(uniqueConstraints.get(i).getBeforeCell());
                uniqueConstraintsPos.set(i, transitConstraint);
            }
        }
        //System.out.println("kookoo");

//        for (int i = 0; i<uniqueConstraintsPos.size(); i++){
//            String strConstraint = makeCellName(uniqueConstraintsPos.get(i).getBeforeCell()) +
//                    " < " + makeCellName(uniqueConstraintsPos.get(i).getAfterCell());
//            System.out.println(strConstraint);
//        }

        for (int i = 0; i<uniqueConstraintsPos.size(); i++){
            Cell transBeforeCell = uniqueConstraintsPos.get(i).getBeforeCell();
            Cell jointCell = uniqueConstraintsPos.get(i).getAfterCell();

            for (int j = 0; j<uniqueConstraintsPos.size(); j++){
                if (uniqueConstraintsPos.get(j).getBeforeCell().equals(jointCell)){
                    Cell transAfterCell = uniqueConstraintsPos.get(j).getAfterCell();
                    OrdConstraint transConstraint = new OrdConstraint(transBeforeCell, transAfterCell);
                    if (constraintPresented(transConstraint, uniqueConstraintsPos, false, true)) return new ArrayList<>();
                }
            }
        }
        return uniqueConstraintsPos;
    }

    public boolean evaluate( ArrayList<ArrayList<OrdConstraint>> constraints, ArrayList<OrdConstraint> uniqueConstraints,
                             String curInstantiation){
        boolean temp = false;
        for (int i = 0; i<constraints.size(); i++){
            for (int j = 0; j<constraints.get(i).size(); j++){
                if (getConstrTrurthValue(constraints.get(i).get(j), uniqueConstraints, curInstantiation)){
                    temp = true;
                    break;
                }
            }
            if (!temp) return false;
            else {
                temp = false;
            }
        }
        return true;
    }

    public boolean getConstrTrurthValue(OrdConstraint constraint, ArrayList<OrdConstraint> uniqueConstraints,
                                        String curInstantiation){
        for (int i = 0; i<uniqueConstraints.size(); i++){
            if (constraint.equals(uniqueConstraints.get(i))) {
                if (curInstantiation.charAt(i)=='1') return true;
                else if (curInstantiation.charAt(i)=='0') return false;
            }
            else if (constraint.inverseOf(uniqueConstraints.get(i))){
                if (curInstantiation.charAt(i)=='1') return false;
                else if (curInstantiation.charAt(i)=='0') return true;
            }
        }
        System.out.println("Not supposed to be here");
        return false; // unreachable
    }

    public boolean constraintPresented(OrdConstraint constraint, ArrayList<OrdConstraint> uniqueConstraints,
                                       boolean considerDirect, boolean considerInverse){
//        for (HashMap.Entry<OrdConstraint, Boolean> entry : constrInstantiations.entrySet()) {
//            if (entry.getKey().equals(constraint) || entry.getKey().inverseOf(constraint)) return true;
//        }
        Cell.CoordinatesPair beforeCellCoorPair =
                new Cell.CoordinatesPair(constraint.getBeforeCell().getI(), constraint.getBeforeCell().getJ());
        //Cell beforeCell = getCellAt(level,beforeCellCoorPair);
        Cell beforeCell = new Cell(constraint.getBeforeCell().getI(), constraint.getBeforeCell().getJ());

        Cell.CoordinatesPair afterCellCoorPair =
                new Cell.CoordinatesPair(constraint.getAfterCell().getI(), constraint.getAfterCell().getJ());
       // Cell afterCell = getCellAt(level,afterCellCoorPair);
        Cell afterCell = new Cell(constraint.getAfterCell().getI(), constraint.getAfterCell().getJ());
        OrdConstraint krutchFFS = new OrdConstraint(beforeCell, afterCell);

        for (OrdConstraint entry: uniqueConstraints) {
            //System.out.println(krutchFFS);
           // System.out.println(entry);
            if ((entry.equals(constraint) && considerDirect) || (entry.inverseOf(constraint) && considerInverse)) return true;
        }
        return false;
    }

    private HashMap<Cell, ArrayList<ArrayList<ArrayList<Cell>>>> findLinearizations(PathFinder pathFinder,
                                                                                    ArrayList<ArrayList<Cell>> level ) {

        HashMap<Cell, ArrayList<ArrayList<ArrayList<Cell>>>> reachabilityMap =
                new HashMap<Cell,ArrayList<ArrayList<ArrayList<Cell>>>>();
        for (int i = 0; i<goalCells.size(); i++) {
            Cell goalCell = goalCells.get(i);
            ArrayList<Cell> otherGoals = new ArrayList<Cell>(goalCells);
            otherGoals.remove(i);
            //System.out.println("FAILING");
            ArrayList<ArrayList<ArrayList<Cell>>> combOfSizeCSize = new ArrayList<>();
            // combOfSizeCSize.add(new ArrayList<ArrayList<Cell>>());
            reachabilityMap.put(goalCell, combOfSizeCSize);

            for (int j = 1; j < goalCells.size(); j++){
                boolean pathWasBlocked = false;
                int cSize = j;
                combinations3(otherGoals, cSize, 0, sizedList(cSize));
                ArrayList<ArrayList<ArrayList<Cell>>> curCSizeList = reachabilityMap.get(goalCell);
               // curCSizeList.add(new ArrayList<ArrayList<Cell>>(globalCombs));

                ArrayList<ArrayList<Cell>> blockingCombs = new ArrayList<>();
                for(int k = 0; k<globalCombs.size(); k++){
                    if (isCombinationBlocking(goalCell,globalCombs.get(k), pathFinder, level)){
                        blockingCombs.add(globalCombs.get(k));
                        pathWasBlocked = true;
                    }
                }

                curCSizeList.add(new ArrayList<ArrayList<Cell>>(blockingCombs));

                reachabilityMap.put(goalCell, curCSizeList);
                globalCombs.clear();
                if(pathWasBlocked) break;
            }

        }

        for (HashMap.Entry<Cell,ArrayList<ArrayList<ArrayList<Cell>>>> entry : reachabilityMap.entrySet()) {
            System.out.println("Goal cell: " + makeCellName(entry.getKey()));

            ArrayList<ArrayList<ArrayList<Cell>>> val = entry.getValue();
           // System.out.println(val.get(1).size());
            for (int i=0; i<val.size(); i++){
                System.out.println("Combinations of size " + (i+1));
                for(int j =0; j<val.get(i).size(); j++) {
                    for (int k=0;k<val.get(i).get(j).size(); k++){
                        System.out.print(makeCellName(val.get(i).get(j).get(k))+" ");
                    }
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println();
        }

        return reachabilityMap;
    }

    private String makeCellName(Cell cell){
        return cell.getGoalLetter()+cell.toString();
    }

    private static ArrayList<Cell> sizedList(int n){
        ArrayList<Cell> resList = new ArrayList<Cell>();
        for (int i = 0; i<n; i++) resList.add(null);
        return resList;
    }

    private static void combinations3(ArrayList<Cell> arr, int len, int startPosition, ArrayList<Cell> result){
        if (len == 0){
            Cell[] resultarr = result.toArray(new Cell[result.size()]);
           // System.out.println(Arrays.toString(resultarr));
            globalCombs.add(new ArrayList<Cell>(result));
            return;
        }
        for (int i = startPosition; i <= arr.size()-len; i++){
            result.set(result.size() - len, arr.get(i));
            combinations3(arr, len-1, i+1, result);
        }
       // System.out.println();
    }

    /*private static void combinations3Bool(ArrayList<Boolean> arr, int len, int startPosition, ArrayList<Boolean> result){
        if (len == 0){
            Boolean[] resultarr = result.toArray(new Boolean[result.size()]);
            // System.out.println(Arrays.toString(resultarr));
            instatntiations.add(new ArrayList<Boolean>(result));
            return;
        }
        for (int i = startPosition; i <= arr.size()-len; i++){
            result.set(result.size() - len, arr.get(i));
            combinations3Bool(arr, len-1, i+1, result);
        }
        // System.out.println();
    }*/


    // method for prioritizing goals based on how their satisfaction affects connectivity between other goals and
    // agent; please note that for now the agent should initially be able to achieve any goal
    // otherwise we can work with set (of Cell type objects) differences

    private boolean isCombinationBlocking(Cell goalCell, ArrayList<Cell> combination, PathFinder pathFinder,
                                          ArrayList<ArrayList<Cell>> level){
        ArrayList<Type> oldTypes = new ArrayList<>();
        ArrayList<Entity> oldEntities = new ArrayList<>();
        for (int i=0; i<combination.size(); i++){
            int curI = combination.get(i).getI();
            int curJ = combination.get(i).getJ();
            oldTypes.add(level.get(curI).get(curJ).getType());
            oldEntities.add(level.get(curI).get(curJ).getEntity());

            Entity someBox = null; // take first box with this letter (in reality the one we test)
            //iter through boxes and find one with letter of the current goal (just for now)
            for (Cell boxCell: boxCells){
                Box curBox = (Box) boxCell.getEntity();
                if (Character.toLowerCase(curBox.getLetter()) == combination.get(i).getGoalLetter()){
                    someBox = curBox;
                    break;
                }
            }
            level.get(curI).get(curJ).setEntity(someBox);
        }

        Cell.CoordinatesPair agentCellCoordinate = LevelReader.getAgentCellCoords().get(0);
        Cell agentCell = getCellAt(level, agentCellCoordinate);
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = false; // agents are obstacles
        boolean bObstacles = true;

        boolean pathExists = pathFinder.pathExists(level, agentCell, goalCell,
                wObstacles,aObstacles,bObstacles);

        for (int i=0; i<combination.size(); i++) {
            int curI = combination.get(i).getI();
            int curJ = combination.get(i).getJ();
            level.get(curI).get(curJ).setType(oldTypes.get(i));
            level.get(curI).get(curJ).setEntity(oldEntities.get(i));
        }

        return !pathExists;
    }

    private static Cell getCellAt(ArrayList<ArrayList<Cell>> level, Cell.CoordinatesPair coordinatesPair){
        return level.get(coordinatesPair.getX()).get(coordinatesPair.getY());
    }

    private ArrayList<ArrayList<Cell>> prioritizeGoals(PathFinder pathFinder, ArrayList<ArrayList<Cell>> level ){
        boolean wObstacles = true; // walls are obstacles
        boolean aObstacles = false; // agents are obstacles
        boolean bObstacles = true;

        ArrayList<ArrayList<Cell>> goalPartOrd = new ArrayList<ArrayList<Cell>>();

        Cell.CoordinatesPair agentCellCoord = LevelReader.getAgentCellCoords().get(0);
        Cell agentCell = getCellAt(level, agentCellCoord); // taking any agent for now, since colors are omitted
       // goalPriorities.add(new ArrayList<Cell>());
        for (int i=0; i<goalCells.size(); i++){
            //System.out.println("EXECUTED");
           // goalPriorities.get(goalPriorities.size()-1).add(goalCells.get(i));
            // get location of the goal cell currently being processed
            int curI = goalCells.get(i).getI();
            int curJ = goalCells.get(i).getJ();
            Type oldType = level.get(curI).get(curJ).getType();
            Entity oldEntity = level.get(curI).get(curJ).getEntity();
            Entity someBox = null; // take first box with this letter (in reality the one we test)
            //iter through boxes and find one with letter of the current goal (just for now)
            for (Cell boxCell: boxCells){
                Box curBox = (Box) boxCell.getEntity();
                if (Character.toLowerCase(curBox.getLetter()) == goalCells.get(i).getGoalLetter()){
                    someBox = curBox;
                    boxCells.remove(boxCell); // remove cur box cell from the list as it is free now
                    boxCell.setEntity(null); // and remove entity from it correspondingly
                    break;
                }
            }

            level.get(curI).get(curJ).setEntity(someBox); // and put the box on the cur. goal
            int numOfBlockedGoals = 0;
            for (int j=0; j<goalCells.size(); j++){

                if(i!=j && !pathFinder.pathExists(level, agentCell, goalCells.get(j),
                        wObstacles,aObstacles,bObstacles)) {

                    numOfBlockedGoals++; // will be used later

                    goalPartOrd.add(new ArrayList<Cell>());
                    goalPartOrd.get(goalPartOrd.size()-1).add(goalCells.get(j));
                    goalPartOrd.get(goalPartOrd.size()-1).add(goalCells.get(i));
                }
            }

            level.get(curI).get(curJ).setType(oldType);
            level.get(curI).get(curJ).setEntity(oldEntity);
        }
        return goalPartOrd;
    }
}

class CellPrior{ // not used atm
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

class OrdConstraint{
    private Cell beforeCell;
    private Cell afterCell;
    private boolean instVal;

    OrdConstraint(Cell beforeCell, Cell afterCell){
        this.beforeCell = beforeCell;
        this.afterCell = afterCell;
        instVal = false;
    }

    public Cell getBeforeCell(){
        return beforeCell;
    }

    public Cell getAfterCell(){
        return afterCell;
    }

    public boolean getInstVal(){
        return instVal;
    }

    public void setBeforeCell(Cell beforeCell){
        this.beforeCell = beforeCell;
    }

    public void setAfterCell (Cell afterCell){
        this.afterCell = afterCell;
    }

    public void setInstVal(boolean instVal){
        this.instVal = instVal;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (o instanceof OrdConstraint){
            OrdConstraint oc = (OrdConstraint) o;
            return  (beforeCell.equals(oc.getBeforeCell()) && afterCell.equals(oc.getAfterCell()));
        }
        return false;
    }

    public boolean inverseOf(OrdConstraint oc){
        if (oc.getBeforeCell().equals(afterCell) && oc.getAfterCell().equals(beforeCell)) return true;
        return false;
    }

}

