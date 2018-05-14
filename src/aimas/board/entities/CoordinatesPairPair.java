package aimas.board.entities;

import aimas.board.CoordinatesPair;

import java.util.Objects;

public class CoordinatesPairPair {
    private CoordinatesPair pair1;
    private CoordinatesPair pair2;

    public CoordinatesPairPair(CoordinatesPair pair1, CoordinatesPair pair2) {
        this.pair1 = pair1;
        this.pair2 = pair2;
    }

    public CoordinatesPair getPair1() {
        return pair1;
    }
    public void setPair1(CoordinatesPair cell1) {
        this.pair1 = pair1;
    }

    public CoordinatesPair getPair2() {
        return pair2;
    }
    public void setPair2(CoordinatesPair pair2) { this.pair2 = pair2; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (obj instanceof CoordinatesPairPair){
            CoordinatesPairPair p = (CoordinatesPairPair) obj;
            return  ((getPair1().equals(p.getPair1()) && getPair2().equals(p.getPair2())) || getPair1().equals(p.getPair2()) && getPair2().equals(p.getPair1()));
        }



        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair1,pair2);
    }
}

