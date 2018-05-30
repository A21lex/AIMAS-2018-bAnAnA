package aimas.aiutils;

import aimas.Command;
import aimas.MapParser;
import aimas.Node;
import aimas.actions.Action;
import aimas.actions.expandable.AchieveGoalAction;
import aimas.actions.expandable.SolveLevelAction;
import aimas.board.entities.Agent;

import java.util.*;

public class MASolver {

    public static List<String> getSolutionForLevel(Node node){
        World world =  new World(node);
        MapParser parser = new MapParser(node.getLevel());
        parser.parseMap(Node.getSpaceCellCoords(), node.getLevel());
        BdiHtnFsmSolver.cellWeights = parser.getCellWeights();
        SolveLevelAction solveLevel = new SolveLevelAction(node, BdiHtnFsmSolver.cellWeights);

        List<List<Command>> GlobalSolution = new ArrayList<>();
        List<Agent> orderedAgents = world.getState().getAgents();
        orderedAgents.sort(new Comparator<Agent>() {
            @Override
            public int compare(Agent o1, Agent o2) {
                return o1.getNumber() - o2.getNumber();
            }
        });
        for(Agent agent : orderedAgents){
            GlobalSolution.add(getSolutionForAgent(agent, solveLevel, world));
        }

        List<String> ResultSolution = new ArrayList<>();
        StringBuilder compositeCommand = new StringBuilder();
        String regex = "\\[|\\]"; // to get rid of square brackets for separate moves

        int longestSolutionLength = 0;
        for (List<Command> commands : GlobalSolution){
            if (commands.size() > longestSolutionLength){
                longestSolutionLength = commands.size();
            }
        }

        World globalWorld = new World(node); // world to keep track of everybody's actions

        int counterOfConflicts = 0;
        for (int i = 0; i < longestSolutionLength; i++){
            compositeCommand.append("[");
            int agentNum = 0;
            for (List<Command> commands : GlobalSolution){
                Command command; // iterate over commands of every agent.
                try {
                    command = commands.get(i);
                }
                catch (IndexOutOfBoundsException ex){
//                    Command prevCommand;
//                    try {
//                        prevCommand = commands.get(i - 1);
//                    }
//                    catch (IndexOutOfBoundsException e){
//                        prevCommand = new Command(Command.CommandType.NoOp);
//                    }
//                    command = Command.getOppositeMoveCommand(prevCommand);
//                    ArrayList<Command> potentialCommands = new ArrayList<>();
//                    for (Command c : Command.EVERY){
//                        if (c.actionCommandType == Command.CommandType.Move){
//                            potentialCommands.add(c);
//                        }
//                    }
//                    int r = new Random().nextInt(potentialCommands.size());
//                    command = potentialCommands.get(r);
//                    while (!(globalWorld.isAValidMove(agentNum, command))){
//                        potentialCommands.remove(command); // remove invalid command from list
//                        r = new Random().nextInt(potentialCommands.size());
//                        command = potentialCommands.get(r);
//                    }
                    command = new Command(Command.CommandType.NoOp);
                }

                String editedCommand;
                if (globalWorld.isAValidMove(agentNum, command)){
                    globalWorld.makeAMove(agentNum, command);
                }
                else {
                    counterOfConflicts++;
                    command = new Command(Command.CommandType.NoOp); // do noop this time
                    commands.add(i, command); // shift by 1 correspondingly
                    // Replan !
                    SolveLevelAction resolveLevel = new SolveLevelAction(globalWorld.getState(),
                            BdiHtnFsmSolver.cellWeights);
                    List<Command> newCommands = getSolutionForAgent(orderedAgents.get(agentNum), resolveLevel, globalWorld);
                    if (counterOfConflicts>10){
                        break;
                    }
                    commands.subList(i+1, commands.size()).clear(); // clear old commands after current one
                    commands.addAll(i+1, newCommands); // and add new ones

                    longestSolutionLength += newCommands.size();
                    System.err.println("Num of conflicts occured: " + counterOfConflicts);
                }
                editedCommand = command.toString().replaceAll(regex, "");
                compositeCommand.append(editedCommand);
                compositeCommand.append(",");
                agentNum++;
            }
            if (compositeCommand.length() > 0){
                compositeCommand.setLength(compositeCommand.length()-1); // remove last comma
            }
            compositeCommand.append("]");
            ResultSolution.add(compositeCommand.toString());
            compositeCommand.setLength(0);
            if (globalWorld.getState().isSolved()){
                break; // exit early if solved everything (not to waste noop actions)
            }
        }
//        for (String string : ResultSolution){
//            System.err.println(string);
//        }
        return ResultSolution;
    }

    public static List<Command> getSolutionForAgent(Agent agent, SolveLevelAction solveLevelAction, World world){
        World copyOfWorld = new World(world.getState());
        List<Command> solution = new ArrayList<>();
        List<Action> achievegoals = solveLevelAction.decompose(copyOfWorld.getState());
        for (Action action : achievegoals){
            AchieveGoalAction achieveGoalAction = (AchieveGoalAction) action;
            achieveGoalAction.setParent(null); // we do not want our FSM to go to its parent but solve the action itself
            if (achieveGoalAction.getAgent().getNumber() == agent.getNumber()){
                List<Command> tempList = BdiHtnFsmSolver.HTNBDIFSM(agent, action, copyOfWorld);
                solution.addAll(tempList);
            }
        }
        return solution;
    }
}
