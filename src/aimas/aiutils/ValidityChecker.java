package aimas.aiutils;

import aimas.Node;
import aimas.board.CoordinatesPair;

public class ValidityChecker {

    public static boolean isCellFree(Node currentState, CoordinatesPair coordinate) {
        return currentState.getCellAtCoords(coordinate).getEntity() == null;
    }
}
