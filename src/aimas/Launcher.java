package aimas;

import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.ExpandableAction;
import aimas.actions.atomic.MoveSurelyAction;
import aimas.actions.expandable.AchieveGoalAction;
import aimas.actions.expandable.RemoveBoxAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.aiutils.BestFirstSearch;
import aimas.aiutils.BoxAssigner;
import aimas.aiutils.World;
import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Color;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;


/**
 * Created by aleksandrs on 4/19/18.
 */

/**
 * Run everything.. basically can be used for testing for now.
 *
 */
public class Launcher {

    public static void main(String[] args) {
        Node start = new Node(null);
        try{
             //start.setLevel(LevelReader.getLevel("res/levels/test_levels/SAtowersOfSaigon04.lvl"));
            start.setLevel(LevelReader.getLevel("res/levels/competition_levelsSP17/SAHiveMind.lvl"));
            //start.setLevel(LevelReader.getLevel("res/levels/test_levels/SAtestfull.lvl"));
        }
        catch (IOException e){
            System.out.println("########");
            System.out.println("Probably incorrect path");
        }
        // The lines below are mandatory as nodes are compared using box/agent coords!
        start.setBoxCellCoords(Node.copyList(LevelReader.getBoxCellCoords()));
        start.setAgentCellCoords(Node.copyList(LevelReader.getAgentCellCoords()));
        start.setGoalCellCoords(Node.copyList(LevelReader.getGoalCellCoords()));
        start.setTunnelCellCoords(Node.copyList(LevelReader.getTunnelCellCoords()));
        start.setSpaceCells(Node.copyList(LevelReader.getSpaceCellCoords()));

        World world =  new World(start);


        //test tunnels
        System.out.println("tunnels:");
        System.out.println(start.getTunnelCellCoords());
        //testing..
        MapParser parser = new MapParser(start.getLevel());
        parser.parseMap(start.getSpaceCelllCoords(), start.getLevel());

        Set<Map.Entry<CoordinatesPair,Double>> hashSet=parser.getCellWeights().entrySet();
        for(Map.Entry entry:hashSet ) {

            System.out.println("Key="+entry.getKey()+", Value="+entry.getValue());
        }

        System.out.println("HashMap size="+ hashSet.size());

        // Test pathfinder with path detection
        boolean testieeeee = PathFinder.pathExists(start.getLevel(), new CoordinatesPair(4,8),
                new CoordinatesPair(4,10), true, true, true);


        /*double startTime = System.nanoTime();
        ArrayList<Node> shortestPath = BestFirstSearch.AStar(start, 'd');
        double timeElapsed = (System.nanoTime() - startTime) / 1000000000.0;
        System.out.println("Printing shortest path");
        for (int i = shortestPath.size()-1; i >= 0; i--) {
            if (shortestPath.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(shortestPath.get(i).getAction());
            }
        }
        System.out.println("the time elapsed: " + timeElapsed);*/
        // TODO: Boxes can be set as immutable if put in dead end cells
        // TODO2: Make A* have a pending list of goals which had been disachieved
        // TODO3: Make boxes immutable/unmovable once they satisfy a goal
        // Let's say we have the following list of goals to achieve
        ArrayList<Character> goalsToAchieve = new ArrayList<>();
          //goalsToAchieve.add('w');

        goalsToAchieve.add('w');
        goalsToAchieve.add('u');
//        goalsToAchieve.add('d');
//        goalsToAchieve.add('c');
//        goalsToAchieve.add('d');
//        goalsToAchieve.add('a');


        /*ArrayList<Node> totalShortestPath = new ArrayList<>(); // combine paths from A* and GP to solve level
        Node curNode = start;
        for (Character goal : goalsToAchieve){
            ArrayList<Node> tempPath = BestFirstSearch.AStar(curNode, goal);
            for (int i = tempPath.size() - 1; i >= 0; i--){
                totalShortestPath.add(0,tempPath.get(i)); // add to start of list
            }
            tempPath.clear();
            curNode = totalShortestPath.get(0); // we are at last element of the totpath
            System.out.println(curNode);
        }

        System.out.println("Printing total shortest path");
        for (int i = totalShortestPath.size() - 1; i >= 0; i--) {
            if (totalShortestPath.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(totalShortestPath.get(i).getAction().toString());
                //System.out.println(totalShortestPath.get(i));
            }
        }*/

//        // Testing new action stuff
//        Action movesomewhere = new MoveSurelyAction(new CoordinatesPair(2,1), null);
//        AtomicAction atomic = (AtomicAction) movesomewhere;
//        ArrayList<Node> pathh = BestFirstSearch.AStar(start,atomic);
//        for (int i = pathh.size() - 1; i > 0; i--){
//            System.out.println(pathh.get(i).getAction().toString());
//        }
//
//        int a = 777;
        List<CoordinatesPair> boxCoords = start.getBoxCellCoords();
        List<Box> boxes = new ArrayList<>();
        //AtomicAction gettobox = new MoveSurelyAction(new CoordinatesPair(3, 10));
        //ArrayList<Node> fdf = BestFirstSearch.AStar(start, gettobox);

        // testing BoxAssigner
        HashMap<Cell, Box> goalsBoxes =  BoxAssigner.assignBoxesToGoals(start);
        System.out.println("Boxes and goals are assigned as follows: ");
        for (Cell cell : goalsBoxes.keySet()){
            System.out.println("Goal cell: "+cell);
            System.out.println("Box: " + goalsBoxes.get(cell));
            System.out.println("Box coords: " + goalsBoxes.get(cell).getCoordinates(start));
        }

        //ExpandableAction someaction = (ExpandableAction) actions.get(0);
        //List<Action> actions2 = someaction.decompose(start);
        //Action one = actions2.get(2);
        //Action two = actions2.get(3);
       /* actionsToPerform.add(one);
        actionsToPerform.add(two);

        ExpandableAction someotheraction = (ExpandableAction) actions.get(1);
        actionsToPerform.add(someotheraction.decompose(start).get(2));
        actionsToPerform.add(someotheraction.decompose(start).get(3));*/

        /* THIS IS JUST A TEST
         FOR NOW ONLY WORKS FOR LEVELS WHERE IT IS POSSIBLE
        TO ACHIEVE GOALS WITHOUT HAVING TO CLEAR PATHS
        AND THUS WITHOUT HAVING TO CONSIDER ORDER OF ACHIEVING GOALS
        SAanagram
        SAchoice
        SAD1
        SAsoko3_48
        SAbAnAnA  (our SA level for now)

        Possibly add BDI in this loop... reconsideration when goal achieved for example
        Need to avoid agent being stuck in the corner/dead-end, else this also does not work of course

        */

      /* sentinel  ExpandableAction solveLevel = new SolveLevelAction(start);
        List<Action> actions = solveLevel.decompose(start);
        List<Action> actionsToPerform = new ArrayList<>();
        for (Action action : actions){
            ExpandableAction expandableAction = (ExpandableAction) action;
            for (Action subAction: expandableAction.decompose(start)){
                ExpandableAction expandableSubaction;
                if (subAction instanceof ExpandableAction) {
                    expandableSubaction = (ExpandableAction) subAction;
                    try {
                        actionsToPerform.add(expandableSubaction.decompose(start).get(0));
                        actionsToPerform.add(expandableSubaction.decompose(start).get(1));

                        actionsToPerform.add(expandableSubaction.decompose(start).get(2));
                        actionsToPerform.add(expandableSubaction.decompose(start).get(3));
                    }
                    catch (IndexOutOfBoundsException ex){
                        // likely we are at a node which is already achieved
                        // not as many actions, it's fine, continue
                        continue;
                    }
                }
                else { // is not expandable
                    actionsToPerform.add(subAction);
                }
            }
        }

        List<Node> path = getTotalPath(actionsToPerform, start);
        System.out.println("Printing total shortest path");
        for (int i = path.size() - 1; i >= 0; i--) {
            if (path.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(path.get(i).getAction().toString());
                //System.out.println(path.get(i));
            }
        }
        // Check last node of solution on whether it is achieved
        System.out.println(solveLevel.isAchieved(path.get(0)) ? "Success" : "Failure");
        boolean test = true; sentinel */

        // some testing from Arturs's side;
       // ExpandableAction solveLevelTest = new SolveLevelAction(start);
        //buildTree(solveLevelTest,start);
       // printTree(solveLevelTest);
        System.out.println(PathFinder.pathExists(start.getLevel(), new CoordinatesPair(1, 3), new CoordinatesPair(3, 5), true,
                false, true));
        for (CoordinatesPair coordPar : PathFinder.getFoundPath()){
            System.out.println(coordPar);
        }


        SolveLevelAction solveLevel1 = new SolveLevelAction(start);
       // MoveSurelyAction msAction = new MoveSurelyAction(new CoordinatesPair(3,4), new SolveLevelAction(start));
        System.out.println(solveLevel1.getChildrenActions().size());
       // buildTree(solveLevel1, start);
        //printTree(solveLevel1);

       // System.out.println(findLeftMostDesc(solveLevel1.getChildrenActions().get(1)));
        //System.out.println("***");
       // System.out.println(findNextHTNnode(solveLevel1.getChildrenActions().get(0).getChildrenActions().get(3)));
        System.out.println(PathFinder.getBoxesOnPath(start, new CoordinatesPair(1, 3),
                new CoordinatesPair(5, 1), true, false, false, new ArrayList<Box>()).size());

        HTNBDIFSM(world.getState().getAgents().get(0), solveLevel1, world);
        //System.out.println("kk "+LevelReader.findParkingCell(start, new CoordinatesPair(3,5)));

        // System.out.println("last " + PathFinder.getBoxesOnPath(start, new CoordinatesPair(5,4), new CoordinatesPair(3,2),
        //true, false, false).size());

        //******************************************************************************

        /*// Get a box to operate on
        List<CoordinatesPair> boxCoords = start.getBoxCellCoords();
        List<Box> boxes = new ArrayList<>();
        for (CoordinatesPair coordinate : boxCoords){
            boxes.add((Box) start.getCellAtCoords(coordinate).getEntity());
        }
        List<Action> actions = new ArrayList<>();
        for (Box box : boxes){
            // do some stuff for ALL boxes in the level
            actions.add(new ClearPathToBox(box));
            actions.add(new GetToBoxAction(box));
            actions.add(new MoveBoxAction(box, new CoordinatesPair(3, 10)));

        }
        List<Node> path = getTotalPath(actions, start);

        *//*Box box = boxes.get(8); //take U box in list of coords

        List<Action> actionsToPerform = new ArrayList<>();
        // Define some actions
        Action clearPathToBox = new ClearPathToBox(box);
        Action getToBoxAction = new GetToBoxAction(box);
        Action moveBoxAction = new MoveBoxAction(box, new CoordinatesPair(3, 10));
        actionsToPerform.add(clearPathToBox);
        actionsToPerform.add(getToBoxAction);
        actionsToPerform.add(moveBoxAction);

        List<Node> path = new ArrayList<>();

        path = getTotalPath(actionsToPerform, start);*//*
        System.out.println("Printing total shortest path");
        for (int i = path.size() - 1; i >= 0; i--) {
            if (path.get(i).getAction() != null) { // if the action is null, this is a start node
                System.out.println(path.get(i).getAction().toString());
                //System.out.println(totalShortestPath.get(i));
            }
        }*/



//        ArrayList<Node> stufftest = BestFirstSearch.AStar(start, clearPathToBox);
//        for (int i = stufftest.size() - 1; i >= 0; i--) {
//            if (stufftest.get(i).getAction() != null) { // if the action is null, this is a start node
//                System.out.println(stufftest.get(i).getAction().toString());
//                System.out.println(stufftest.get(i));
//            }
//        }
//
//
//        System.out.println(getToBoxAction.getType());
//        ArrayList<Node> testshortpathtobox = BestFirstSearch.AStar(start, getToBoxAction);
//        for (int i = testshortpathtobox.size() - 1; i >= 0; i--) {
//            if (testshortpathtobox.get(i).getAction() != null) { // if the action is null, this is a start node
//                System.out.println(testshortpathtobox.get(i).getAction().toString());
//                System.out.println(testshortpathtobox.get(i));
//            }
//        }
//
//        ArrayList<Node> last = BestFirstSearch.AStar(start, moveBoxAction);
//        for (int i = last.size() -1; i >= 0; i--){
//            if (last.get(i).getAction() != null){
//                System.out.println(last.get(i).getAction().toString());
//                System.out.println(last.get(i));
//            }
//        }

    }

    static List<Node> getTotalPath(List<Action> actionsToPerform, Node node){
        List<Node> path = new ArrayList<>();
        Node curNode = node;
        for (Action action : actionsToPerform){
            AtomicAction atomicAction = (AtomicAction) action; // is there a better way than casting all the time?
            ArrayList<Node> tempPath = BestFirstSearch.AStar(curNode, atomicAction);
            for (int i = tempPath.size() - 1; i >= 0; i--){
                path.add(0,tempPath.get(i)); // add to start of list
            }
            tempPath.clear();
            if (path.size() > 0) { // if path.size()==0, it means action had already been completed, so do nothing
                curNode = path.get(0); // we are at last element of the totpath
            }
            System.out.println(curNode);
        }
        return path;
    }


    // HTN tree related functions
    static void buildTree(Action action, Node node){
        if (action instanceof  ExpandableAction) {
            ((ExpandableAction) action).decompose(node);
        }
        for (Action childAct: action.getChildrenActions()){
            buildTree(childAct, node);
        }
    }

    // pre-order traversal of HTN tree
    static void printTree(Action action){
        System.out.println(action.toString());
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

    // state machine (move to separate class)
    static void HTNBDIFSM(Agent agent, Action htnroot, World world) {
        Action curAction = htnroot;
        Node curNode = world.getState();
        int curState = 1;
        int prevState = -2;
        boolean finished = false;
        Action curAchieveGoalAction = htnroot; // will 100% be overwritten
        ArrayList<Command> allCommands = new ArrayList<>();
        System.out.println();
        while(!finished) {
            System.out.println(curState + " " + curAction + "***" + curAction.getParent());
            switch (curState) {
                case 1:
                    if (curAction instanceof AchieveGoalAction) curAchieveGoalAction = curAction;
                    if (curAction.isAchieved(curNode)) {
                        curState = 2;
                    }
                    else if (curAction instanceof ExpandableAction) {
                        if (curAction instanceof RemoveBoxAction){
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
                        }
                        curState = 3;
                    }
                    else if (curAction instanceof AtomicAction) {
                        curState = 4;
                    }
                    break;

                case 2:
                    Action successor = findNextHTNnode(curAction);
                    if (successor.equals(curAction)) {
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

                        finished = true;
                    }
                    else {
                        curAction = successor;
                        curState = 1;
                    }
                    break;

                case 3:
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
                       System.out.println(curNode);
                       System.out.println("Faling action: " + curAction);
                   }

                case 4:
                    ArrayList<Node> pathOfNodes = BestFirstSearch.AStar(curNode,(AtomicAction) curAction);
                    Collections.reverse(pathOfNodes);
                    //ArrayList<Command> commands = new ArrayList<>();
                    boolean atomicActionDone = false;
                    System.out.println(curAction);
                    System.out.println("size: " + pathOfNodes.size());
                    for (Node pathNode : pathOfNodes){
                       // System.out.println(pathNode);
                        if(world.isAValidMove(agent.getNumber(),pathNode.getAction())){
                            allCommands.add(pathNode.getAction());
                            world.makeAMove(agent.getNumber(), pathNode.getAction());
                           // System.out.println(world.getState());
                        }
                        else {
                            System.out.println("problem: " +pathNode.getAction().toString());
                            curState = 4;
                            break;
                        }
                        curNode = world.getState();
                        System.out.println(curNode);
                        atomicActionDone = true;
                    }

                    if (atomicActionDone) {

                        if (curAchieveGoalAction.isAchieved(world.getState())){
                            curAction = findNextHTNnode(curAchieveGoalAction);
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


    }



}