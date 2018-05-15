package aimas.actions;

import aimas.Node;
import aimas.board.entities.Agent;

/**
 * Action - atomic, same as non-atomic plus heuristic function (to guide A*)
 */
public abstract class AtomicAction extends Action{

    public abstract int heuristic(Node node);
    protected Agent agent;

    public Agent getAgent() {
        return agent;
    }
}
