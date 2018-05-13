package aimas.aiutils;

import aimas.Command;
import aimas.Node;
import aimas.board.Cell;
import aimas.board.CoordinatesPair;
import aimas.board.entities.Agent;
import aimas.board.entities.Box;
import aimas.board.entities.Color;

import java.util.ArrayList;

import static aimas.Node.copyList;

/**
 * Keep track of the current state of the world for all the agents
 */
public class World {

    private Node state;

    public Node getState() {
        return state;
    }

    public World(Node state){
        this.state = state;
    }

    @Override
    public String toString() {
        return state.toString();
    }

    public boolean isAValidMove(int agentNumber, Command c){
        // Find the agent in the current node
        int curAgentRow = 0;
        int curAgentCol = 0;
        CoordinatesPair curAgentCellCoords = null;
        Agent curAgent = null;
        Color curAgentColor = null;
        for (Agent agent : this.state.getAgents()){
            if (agent.getNumber() == agentNumber){
                curAgent = agent;
                curAgentRow = agent.getCoordinates(state).getX();
                curAgentCol = agent.getCoordinates(state).getY();
                curAgentCellCoords = agent.getCoordinates(state);
                curAgentColor = curAgent.getColor();
                break; // found our agent
            }
        }

        // Check command validity
        int newAgentRow = curAgentRow + Command.dirToRowChange(c.dir1);
        int newAgentCol = curAgentCol + Command.dirToColChange(c.dir1);
        if (c.actionCommandType == Command.CommandType.Move) {
            if (state.cellIsFree(newAgentRow, newAgentCol)) {
                return true;
            }
        }
        else if (c.actionCommandType == Command.CommandType.Push){
            if (state.boxAt(newAgentRow, newAgentCol)) {
                int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
                Cell curBoxCell = state.getLevel().get(newAgentRow).get(newAgentCol);
                CoordinatesPair curBoxCellCoords = new CoordinatesPair(curBoxCell);
                // And that new cell of the box is free
                if (state.cellIsFree(newBoxRow, newBoxCol)) {
                    // And check whether colors of box and agent match
                    Box curBox = (Box) state.getLevel().get(newAgentRow).get(newAgentCol).getEntity();
                    if ((curBox.getColor() == curAgentColor)) {
                        return true;
                    }
                }
            }
        }
        else if (c.actionCommandType == Command.CommandType.Pull){
            if (state.cellIsFree(newAgentRow, newAgentCol)) {
                int boxRow = curAgentRow + Command.dirToRowChange(c.dir2);
                int boxCol = curAgentCol + Command.dirToColChange(c.dir2);
                // and there is a box in dir2 of the agent
                if (state.boxAt(boxRow, boxCol)) {
                    // And check whether color of box and agent match
                    Box curBox = (Box) state.getLevel().get(boxRow).get(boxCol).getEntity();
                    if ((curBox.getColor() == curAgentColor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void makeAMove(int agentNumber, Command c){
        // Find the agent in the current node
        int curAgentRow = 0;
        int curAgentCol = 0;
        CoordinatesPair curAgentCellCoords = null;
        Agent curAgent = null;
        Color curAgentColor = null;
        for (Agent agent : this.state.getAgents()){
            if (agent.getNumber() == agentNumber){
                curAgent = agent;
                curAgentRow = agent.getCoordinates(state).getX();
                curAgentCol = agent.getCoordinates(state).getY();
                curAgentCellCoords = agent.getCoordinates(state);
                curAgentColor = curAgent.getColor();
                break; // found our agent
            }
        }

        // Make a move
        int newAgentRow = curAgentRow + Command.dirToRowChange(c.dir1);
        int newAgentCol = curAgentCol + Command.dirToColChange(c.dir1);
        if (c.actionCommandType == Command.CommandType.Move) {
            Node newState = new Node(state); // create a child node with curr node as the parent
            newState.action = c; // action c led us to the new state
            ArrayList<ArrayList<Cell>> updatedLevel = Node.copyLevel(state.getLevel());
            updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(null);
            updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
            // Add level to the new state
            newState.setLevel(updatedLevel);
            newState.agentCellCoords = copyList(state.getAgentCellCoords()); // containers are now also in new node
            newState.boxCellCoords = copyList(state.getBoxCellCoords());
            CoordinatesPair updatedAgentCellCoords = new CoordinatesPair(newAgentRow, newAgentCol);
            newState.getAgentCellCoords().remove(curAgentCellCoords);
            // Only add coords if not already in the list
            if (!newState.getAgentCellCoords().contains(updatedAgentCellCoords)) {
                newState.getAgentCellCoords().add(updatedAgentCellCoords);
            }

            state = newState; // we are in a new state
        }
        else if (c.actionCommandType == Command.CommandType.Push){
            Node newState = new Node(state);
            newState.action = c; // action c led us to the new state
            ArrayList<ArrayList<Cell>> updatedLevel = Node.copyLevel(state.getLevel());
            // Agent moves to his new location
            updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(null);
            updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
            // Box moves to its new location
            int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
            int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
            Box curBox = (Box) state.getLevel().get(newAgentRow).get(newAgentCol).getEntity();
            CoordinatesPair curBoxCellCoords = curBox.getCoordinates(state);
            updatedLevel.get(newBoxRow).get(newBoxCol).setEntity(curBox);
            newState.boxBeingMoved = curBox; // keep track of box being moved
            //curBox.setCoordinates(new CoordinatesPair(newBoxRow, newBoxCol));
            // (no need to remove it from previous location as it is done when agent is moved)
            newState.setLevel(updatedLevel); // set new level to new node
            newState.agentCellCoords = copyList(state.getAgentCellCoords()); // give current agent cell coords to new node
            newState.boxCellCoords = copyList(state.getBoxCellCoords()); // ..same with box cell coords

            // Update agent and box cell containers
            CoordinatesPair updatedAgentCellCoords =
                    new CoordinatesPair(newAgentRow, newAgentCol);
            newState.getAgentCellCoords().remove(curAgentCellCoords); // remove cur cell coord from new node
            if (!newState.getAgentCellCoords().contains(updatedAgentCellCoords)) {
                newState.getAgentCellCoords().add(updatedAgentCellCoords); // add updated cell coord to new node
            }
            CoordinatesPair updatedBoxCellCoords =
                    new CoordinatesPair(newBoxRow, newBoxCol);
            newState.getBoxCellCoords().remove(curBoxCellCoords);
            if (!newState.getBoxCellCoords().contains(updatedBoxCellCoords)) {
                newState.getBoxCellCoords().add(updatedBoxCellCoords);
            }

            state = newState;
        }
        else if (c.actionCommandType == Command.CommandType.Pull){
            Node newState = new Node(state);
            newState.action = c;
            ArrayList<ArrayList<Cell>> updatedLevel = Node.copyLevel(state.getLevel());
            // Box moves to its new location (which is current agent location in this case)
            int boxRow = curAgentRow + Command.dirToRowChange(c.dir2);
            int boxCol = curAgentCol + Command.dirToColChange(c.dir2);
            Box curBox = (Box) state.getLevel().get(boxRow).get(boxCol).getEntity();
            CoordinatesPair curBoxCellCoords = curBox.getCoordinates(state);
            updatedLevel.get(boxRow).get(boxCol).setEntity(null);
            updatedLevel.get(curAgentRow).get(curAgentCol).setEntity(curBox);
            newState.boxBeingMoved = curBox; // keep track of box being moved
            // Agent moves to his new location
            // (no need to remove him from the previous location as it is done when the box is moved)
            updatedLevel.get(newAgentRow).get(newAgentCol).setEntity(curAgent);
            newState.setLevel(updatedLevel);
            newState.agentCellCoords = copyList(state.getAgentCellCoords()); // give current agent cell coords to new node
            newState.boxCellCoords = copyList(state.getBoxCellCoords()); // ..same with box cell coords

            // Update agent and box cell containers
            CoordinatesPair updatedAgentCellCoords =
                    new CoordinatesPair(newAgentRow, newAgentCol);
            newState.getAgentCellCoords().remove(curAgentCellCoords);
            if (!newState.getAgentCellCoords().contains(updatedAgentCellCoords)) {
                newState.getAgentCellCoords().add(updatedAgentCellCoords);
            }

            CoordinatesPair updatedBoxCellCoords =
                    new CoordinatesPair(curAgentRow, curAgentCol);
            newState.getBoxCellCoords().remove(curBoxCellCoords);
            if (!newState.getBoxCellCoords().contains(updatedBoxCellCoords)) {
                newState.getBoxCellCoords().add(updatedBoxCellCoords);
            }

            state = newState;

        }
    }
}
