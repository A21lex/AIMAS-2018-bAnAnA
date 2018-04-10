/**
 * Created by Arturs Gumenuks on 24.02.2018.
 */
import java.util.ArrayList;
import java.util.Collections;

public class PartialOrder {
    public static void main(String[] args){
        ArrayList<Integer> nodes = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> orders = new ArrayList<ArrayList<Integer>>();
        nodes.add(1);
        nodes.add(2);
        nodes.add(3);
        nodes.add(4);
        nodes.add(5);

        for (int i = 0; i<9999; i++){
            Collections.shuffle(nodes);
            ArrayList<Integer> permutation = new ArrayList<Integer>(nodes);
            /*for (int j = 0; j<nodes.size(); j++){
                System.out.print(nodes.get(j)+" ");
            }
            System.out.println();
            System.out.println(AllConstraintsRespected(nodes));
            System.out.println(!orders.contains(permutation));
            System.out.println(); */

            if (AllConstraintsRespected(nodes) && !orders.contains(permutation))  orders.add(permutation);
        }

        for (int i = 0; i<orders.size(); i++){
            ArrayList<Integer> someOrder = orders.get(i);
            for (int j = 0; j<someOrder.size(); j++){
                System.out.print(someOrder.get(j) + " ");
            }
            System.out.println();
        }
        System.out.println(orders.size());
    }

    public static boolean AllConstraintsRespected(ArrayList<Integer> nodeShuffled){
        for (int i = 0; i<nodeShuffled.size(); i++){
            for (int j = i; j<nodeShuffled.size(); j++) {
                if (!ConstraintRespected(nodeShuffled.get(i),nodeShuffled.get(j))) return false;
            }
        }
        return true;
    }

    public static boolean ConstraintRespected(int node1, int node2){
        if (node1 == 2 && node2 == 1) return false;
        if (node1 == 3 && node2 == 1) return false;
        if (node1 == 4 && node2 == 1) return false;
        if (node1 == 3 && node2 == 2) return false;
        if (node1 == 4 && node2 == 2) return false;
        if (node1 == 5 && node2 == 3) return false;
        if (node1 == 5 && node2 == 4) return false;
        //
        if (node1 == 4 && node2 == 3) return false;

        return true;
    }

}
