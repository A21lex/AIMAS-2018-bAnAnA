package aimas.actions;

import aimas.Node;
import aimas.board.entities.Agent;

/**
 * Action - atomic, has to implement a heuristic function (to guide A*)
 */
public abstract class AtomicAction extends Action{

    public abstract int heuristic(Node node);
    protected Agent agent;

    public Agent getAgent() {
        return agent;
    }
}
