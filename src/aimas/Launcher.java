package aimas;

import aimas.actions.Action;
import aimas.actions.AtomicAction;
import aimas.actions.ExpandableAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.aiutils.*;
import aimas.board.entities.Agent;

import java.io.IOException;
import java.util.*;


/**
 * Created by aleksandrs on 4/19/18.
 */

/**
 * Run everything.. basically can be used for testing for now.
 *
 */
public class Launcher {

    private static boolean isMA = false;
    public static void main(String[] args) {
        Node node = new Node(null);
        try{
            node.setLevel(LevelReader.getLevel("res/levels/competition_levelsSP18/SABeTrayEd.lvl"));
            //node.setLevel(LevelReader.getLevel("res/levels/test_levels/SAHiveMind.lvl"));
        }
        catch (IOException e){
            System.out.println("########");
            System.out.println("Probably incorrect path");
        }
        // The lines below are mandatory as nodes are compared using box/agent coords!
        node.setBoxCellCoords(Node.copyList(LevelReader.getBoxCellCoords()));
        node.setAgentCellCoords(Node.copyList(LevelReader.getAgentCellCoords()));
        node.setGoalCellCoords(Node.copyList(LevelReader.getGoalCellCoords()));
        node.setTunnelCellCoords(Node.copyList(LevelReader.getTunnelCellCoords()));
        node.setSpaceCells(Node.copyList(LevelReader.getSpaceCellCoords()));


//        /**
//         * Simple solver by Alex; works for simple SA (no clearing) and MA (no clearings, only simple conflicts)
//          */
//        List<String> Solution = SimpleMASASolver.getSolutionForLevel(node);
//        int countAllNoopTimes = 0;
//        for (String string : Solution){
//            System.out.println(string);
//            if (string.startsWith("[NoOp,NoOp,NoOp")){
//                countAllNoopTimes++;
//            }
//            if (countAllNoopTimes>10){
//                System.err.println("Got stuck, 10 times 3 agents did NoOp");
//                break;
//            }
//        }

        if (node.getAgents().size()>1){
            isMA = true;
        }
        if (isMA){
            List<String> Solution = SimpleMASASolver.getSolutionForLevel(node);
            for (String string : Solution){
                System.out.println(string);
            }
        }
        else{
            World world =  new World(node);
            MapParser parser = new MapParser(node.getLevel());
            parser.parseMap(Node.getSpaceCellCoords(), node.getLevel());
            BdiHtnFsmSolver.cellWeights = parser.getCellWeights();
            SolveLevelAction solveLevel = new SolveLevelAction(node);
            Agent agent = world.getState().getAgents().get(0); // take the only agent
            ArrayList<Command> Solution = BdiHtnFsmSolver.HTNBDIFSM(agent, solveLevel, world);
            for (Command command : Solution){
                System.out.println(command.toString());
            }
            try {
                Thread.sleep(300); // not to interrupt printing
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
            //System.err.println("Solution length: " + Solution.size());
        }
        // Main out.
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
            if (path.size() > 0) { // if path.size() == 0, it means action had already been completed, so do nothing
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

}