package aimas.aiutils;

import aimas.Command;
import aimas.Node;
import aimas.PathFinder;
import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.ExpandableAction;
import aimas.actions.expandable.AchieveGoalAction;
import aimas.actions.expandable.RemoveBoxAction;
import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Entity;

import java.util.*;

/**
 * Artur's state machine in a separate class
 */
public class BdiHtnFsmSolver {

    public static Map<CoordinatesPair, Double> cellWeights;

    // HTN tree related functions
    static void buildTree(Action action, Node node){
        if (action instanceof ExpandableAction) {
            ((ExpandableAction) action).decompose(node);
        }
        for (Action childAct: action.getChildrenActions()){
            buildTree(childAct, node);
        }
    }

    // pre-order traversal of HTN tree
    static void printTree(Action action){
        System.err.println(action.toString());
        for (Action childAct: action.getChildrenActions()){
            printTree(childAct);
        }
    }

    static Action findLeftMostDesc(Action action){
        Action tempAction = action;
        while (!tempAction.getChildrenActions().isEmpty()){
            tempAction = tempAction.getChildrenActions().get(0);
        }
        return tempAction;
    }

    static Action findNextHTNnode(Action action){ // find successor
        Action tempChild = action;
        Action tempParent = tempChild.getParent();
        /*if (parent.hasMoreChildren(action.getNumberAsChild()))
            return parent.getChildOfNumber(action.getNumberAsChild()+1);
        else */
        Action tempNode;
        while (!tempParent.hasMoreChildren(tempChild.getNumberAsChild())){
            if (tempParent.getParent() == null) return action;
            tempNode = tempParent;
            tempChild = tempParent;
            tempParent = tempNode.getParent();
        }
        //return findLeftMostDesc(tempParent.getChildOfNumber(tempChild.getNumberAsChild() + 1));
        return tempParent.getChildOfNumber(tempChild.getNumberAsChild() + 1);
    }

    // state machine
    public static ArrayList<Command> HTNBDIFSM(Agent agent, Action htnroot, World world) {
        Action curAction = htnroot;
        Node curNode = world.getState();
        int curState = 1;
        int prevState = -2;
        boolean finished = false;
        Action curAchieveGoalAction = htnroot; // will 100% be overwritten
        ArrayList<Command> allCommands = new ArrayList<>();
        ArrayList<AchieveGoalAction> achievedGoals = new ArrayList<>();
        while(!finished) {
            //if (curAction instanceof RemoveBoxAction) ((RemoveBoxAction)curAction).triggerParkingCellSearch(curNode);
            System.err.println(curState + " " + curAction + "***" + curAction.getParent());
            //for (int i =0; i<htnroot.getChildrenActions().size(); i++){
            //  System.out.println(htnroot.getChildOfNumber(i) + " " +
            //    htnroot.getChildOfNumber(i).getNumberAsChild());
            //}
            System.out.println();
            switch (curState) {
                case 1: // checkin nature of action
                    System.err.println(achievedGoals);
                    if (curAction instanceof AchieveGoalAction) curAchieveGoalAction = curAction;
                    if (curAction.isAchieved(curNode)) {
                        curState = 2;
                    }
                    else if (curAction instanceof ExpandableAction) {
                        /*if (curAction instanceof RemoveBoxAction){
                            Box box = ((RemoveBoxAction)curAction).box;
                            Cell cell = curNode.getCellAtCoords(box.getCoordinates(curNode));
                            if (cell.isGoal()
                                && cell.getGoalLetter() == Character.toLowerCase(box.getLetter())){
                                AchieveGoalAction achieveAgain = new AchieveGoalAction(cell, box, curAchieveGoalAction.getParent());
                                curAchieveGoalAction.getParent().setChildOfNumber(achieveAgain, curAchieveGoalAction.getNumberAsChild() + 1);
                                curAchieveGoalAction.getParent().getChildrenActions().
                                        get(curAchieveGoalAction.getNumberAsChild()+1).
                                        setNumberAsChild(curAchieveGoalAction.getNumberAsChild()+1);
                                for (int i = curAchieveGoalAction.getNumberAsChild()+2;
                                     i<curAchieveGoalAction.getParent().getChildrenActions().size(); i++){
                                    curAchieveGoalAction.getParent().getChildOfNumber(i).
                                           // setNumberAsChild(curAchieveGoalAction.getParent().getChildOfNumber(i).getNumberAsChild()+1);
                                           setNumberAsChild(i);
                                }
                                System.out.println("whole tree");
                               // printTree(curAchieveGoalAction.getParent());
                                for (Action act : curAchieveGoalAction.getParent().getChildrenActions()){
                                    System.out.println(act + " *** " + act.getNumberAsChild());
                                }
                            }
                        } */
                        curState = 3;
                    }
                    else if (curAction instanceof AtomicAction) {
                        curState = 4;
                    }
                    break;

                case 2: // achieved, find successor
                    Action successor = findNextHTNnode(curAction);

                    if (successor instanceof  AchieveGoalAction){ //cur goal achieved add to list
                        achievedGoals.add((AchieveGoalAction)curAchieveGoalAction);
                    }

                    if (successor.equals(curAction)) { // no more successors, finished
                       /* System.out.println("cur action before finish " + curAction);
                        System.out.println(curNode);
                        System.out.println(curAction.isAchieved(curNode));

                        ArrayList<ArrayList<Cell>> level = curNode.getLevel();
                        System.out.println(curNode.getCellAtCoords(new CoordinatesPair(11,8)));
                        for(int i=0; i<level.size(); i++){
                            for (int j=0; j<level.get(i).size(); j++) {
                                //System.out.println(level.get(i).get(j).getCoordinates()+" "+level.get(i).get(j).getEntity());
                            }
                        } */
                        for (Action act : achievedGoals){
                            System.err.println(act +" "+ act.isAchieved(curNode));
                        }
                        System.err.println(curAction);
                        System.err.println(curAction.getParent().hasMoreChildren(curAction.getNumberAsChild()));
                        System.err.println("Solution found");
                        finished = true;

                    }
                    else { // proceeding
                        curAction = successor;
                        curState = 1;
                    }
                    break;

                case 3: // expandable, not achieved, decomposing
                    ((ExpandableAction)curAction).decompose(curNode);
                    try {
                        Action firstChild = curAction.getChildOfNumber(0);

                        if (firstChild instanceof ExpandableAction) {
                            curAction = firstChild;
                            curState = 1;
                        } else if (firstChild instanceof AtomicAction) {
                            curAction = firstChild;
                            curState = 4;
                        }
                        break;
                    }
                    catch(IndexOutOfBoundsException e){
                        System.err.println(curNode);
                        System.err.println("Faling action: " + curAction);
                    }

                case 4: // execute atomic
                    System.err.println("state 4 " + curAction);
                    ArrayList<Node> pathOfNodes = BestFirstSearch.AStar(curNode,(AtomicAction) curAction);
                    Collections.reverse(pathOfNodes);
                    //ArrayList<Command> commands = new ArrayList<>();
                    boolean atomicActionDone = false;
                    System.err.println(curAction);
                    //System.err.println("size: " + pathOfNodes.size());
                    for (Node pathNode : pathOfNodes){
                        // System.out.println(pathNode);
                        if(world.isAValidMove(agent.getNumber(),pathNode.getAction())){
                            allCommands.add(pathNode.getAction());
                            world.makeAMove(agent.getNumber(), pathNode.getAction());
                            // System.out.println(world.getState());
                        }
                        else {
                            //  System.out.println("problem: " +pathNode.getAction().toString());
                            curState = 4;
                            break;
                        }
                        curNode = world.getState();
                        System.err.println(curNode);
                        atomicActionDone = true;
                    }

                    /*for (AchieveGoalAction act : achievedGoals){
                        if (!act.isAchieved(curNode)){
                            AchieveGoalAction achieveAgain = new AchieveGoalAction(act.goalCell, act.box, act.getParent());
                            curAchieveGoalAction.getParent().setChildOfNumber(achieveAgain,curAchieveGoalAction.getNumberAsChild()+1);
                            achievedGoals.remove(act);
                        }
                    } */

                    Iterator<AchieveGoalAction> iter = achievedGoals.iterator();
                    while (iter.hasNext()) {
                        AchieveGoalAction act = iter.next();
                        if (!act.isAchieved(curNode)){
                            AchieveGoalAction achieveAgain = new AchieveGoalAction(act.goalCell, act.box, agent, act.getParent());
                            boolean contained = false;
                            for (int i = act.getNumberAsChild(); i < act.getParent().getChildrenActions().size(); i++){
                                if (act.getParent().getChildOfNumber(i).equals(achieveAgain)) {
                                    contained = true;
                                    break;
                                }
                            }
                            if (!contained) {
                                curAchieveGoalAction.getParent().setChildOfNumber(achieveAgain,curAchieveGoalAction.getNumberAsChild()+1);
//                                curAchieveGoalAction.getParent().setChildOfNumber(achieveAgain,
//                                        curAchieveGoalAction.getParent().getChildrenActions().size()-1);
// does not work...
                                iter.remove();
                            }

                        }
                    }

                    for (Action act1 : curAchieveGoalAction.getParent().getChildrenActions()){
                        //System.out.println(act1 + " xxx " + act1.getNumberAsChild());
                    }


                    if (atomicActionDone) {

                        if (curAchieveGoalAction.isAchieved(world.getState())){
                            // achievedGoals.add((AchieveGoalAction)curAchieveGoalAction);
                            // curAction = findNextHTNnode(curAchieveGoalAction);
                            curState = 1;
                        }
                        else {
                            curState = 5;
                        }
                    }
                    break;

                case 5:
                    if (curAction.isAchieved(curNode)) {
                        curState = 6;
                    }
                    else {
                        curAction = curAction.getParent();
                        curState = 1;
                    }
                    break;

                case 6:
                    if (curAction.getParent().isAchieved(curNode)){
                        curAction = curAction.getParent();
                        curState = 1;
                    }
                    else {
                        curState = 7;
                    }
                    break;

                case 7:
                    if (curAction.getParent().hasMoreChildren(curAction.getNumberAsChild())) {
                        curAction = curAction.getParent().getChildOfNumber(curAction.getNumberAsChild()+1);
                        curState = 1;
                    }
                    else {
                        curAction = curAction.getParent(); // repeating
                        curState = 1;
                    }
                    break;
            }
        }
        return allCommands;
    }
    // this is also in Launcher but I think should be here? Or idk
    public static CoordinatesPair findParkingCell(Node node, CoordinatesPair initialCell, RemoveBoxAction remBoxAct,
                                                  ArrayList<CoordinatesPair> blackList){

        Agent agent = remBoxAct.getAgent(); // agent removing box
        ArrayList<ArrayList<Cell>> level = node.getLevel();
        Map<CoordinatesPair, Double> subsetOfCellWeights = new HashMap<>();
        CoordinatesPair bestParkingCell = new CoordinatesPair(-5,-5);
        for (int i=0; i<level.size(); i++){
            for (int j = 0; j<level.get(i).size();j++){
                Entity prevEntity =  node.getCellAtCoords(level.get(i).get(j).getCoordinates()).getEntity();
                //node.getCellAtCoords(level.get(i).get(j).getCoordinates()).setEntity(remBoxAct.box);
                // node.getCellAtCoords(initialCell).setEntity(prevEntity);
                if (PathFinder.pathExists(level,initialCell,level.get(i).get(j).getCoordinates(), true, false, true) &&
                        !(level.get(i).get(j).getEntity() instanceof Box) /*&&
                        remBoxAct.isAchieved(node)*/
                        && !blackList.contains(level.get(i).get(j).getCoordinates())) {
                    subsetOfCellWeights.put(level.get(i).get(j).getCoordinates(),
                            cellWeights.get(level.get(i).get(j).getCoordinates()));
                }
                // node.getCellAtCoords(level.get(i).get(j).getCoordinates()).setEntity(prevEntity);
                //node.getCellAtCoords(initialCell).setEntity(remBoxAct.box);
            }
        }
        if (!subsetOfCellWeights.isEmpty()) {
            bestParkingCell = findBestAmongReachable(subsetOfCellWeights,node,initialCell, agent);
        }
        else {
            bestParkingCell = findBestAmongReachable(cellWeights,node,initialCell, agent);
        }
        return bestParkingCell;
    }

    public static CoordinatesPair findBestAmongReachable(Map<CoordinatesPair, Double> subsetOfCellWeights, Node node,
                                                         CoordinatesPair initialCell, Agent agent){
        double min = 999.0;
        // CoordinatesPair bestParkingCell = new CoordinatesPair(-5,-5);
        CoordinatesPair bestParkingCell = initialCell;

        System.err.println("how come");

        for (Map.Entry<CoordinatesPair, Double> entry : subsetOfCellWeights.entrySet()){
            Double curWeight = entry.getValue();
            if(entry.getKey().equals(agent.getCoordinates(node))) curWeight +=10;
            if (node.getCellAtCoords(entry.getKey()).isGoal()) {
                if (Character.toUpperCase(node.getCellAtCoords(entry.getKey()).getGoalLetter())
                        == ((Box)node.getCellAtCoords(initialCell).getEntity()).getLetter()){
                    curWeight -= 10;
                }
                else curWeight += 50;
            }
            if(curWeight.doubleValue()
                    +2*Action.manhDist(initialCell.getX(), initialCell.getY(), entry.getKey().getX(), entry.getKey().getY()) < min){
                if(!(node.getCellAtCoords(entry.getKey()).getEntity() instanceof Box)) {
                    min = curWeight.doubleValue()
                            +2*Action.manhDist(initialCell.getX(), initialCell.getY(),entry.getKey().getX(),entry.getKey().getY());
                    bestParkingCell = entry.getKey();
                }
            }
            /*else if(curWeight.doubleValue() == min){
                if (Action.manhDist(initialCell.getX(), initialCell.getY(),entry.getKey().getX(),entry.getKey().getY())<
                        Action.manhDist(initialCell.getX(), initialCell.getY(),bestParkingCell.getX(),bestParkingCell.getY())){
                        bestParkingCell = entry.getKey();
                }
            }*/
        }
        cellWeights.put(bestParkingCell, (Double)(cellWeights.get(bestParkingCell).doubleValue()+1.5));
        return bestParkingCell;
    }
}
