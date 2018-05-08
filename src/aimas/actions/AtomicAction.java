package aimas.actions;

import aimas.Node;

/**
 * Action - atomic, same as non-atomic plus heuristic function (to guide A*)
 */
public abstract class AtomicAction extends Action{

    public abstract int heuristic(Node node);

}
