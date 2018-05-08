package aimas.actions;

import aimas.Node;

import java.util.List;

public abstract class ExpandableAction extends Action {
    public abstract List<Action> decompose(Node node);
}
