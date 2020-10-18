import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class GraphTest {

    @Test
    public void testGraphCreate(){
        Graph g = new Graph(3);
        g.add(0, 2);
        g.add(2, 1);
        g.add(1, 0);
        HashSet<Integer>[] edgesList = new HashSet[3];
        for (int i=0;i<3;i++){
            edgesList[i] = new HashSet<>();
        }
        edgesList[0].add(2);
        edgesList[0].add(1);
        edgesList[1].add(2);
        edgesList[1].add(0);
        edgesList[2].add(0);
        edgesList[2].add(1);

        Assertions.assertEquals(g.getGraph().length, edgesList.length);
        HashSet<Integer>[] testComponents = g.getGraph();
        for (int i=0;i<3;i++){
            Assertions.assertEquals(edgesList[i],testComponents[i]);
        }
    }

    @Test
    public void testGraphComponents(){
        Graph g = new Graph(5);
        g.add(0, 2);
        g.add(2, 1);
        g.add(1, 0);
        g.add(3, 4);
        HashSet<HashSet<Integer>> components = new HashSet<>();
        HashSet<Integer> first = new HashSet<>();
        first.add(0);
        first.add(1);
        first.add(2);
        HashSet<Integer> second = new HashSet<>();
        second.add(3);
        second.add(4);
        components.add(first);
        components.add(second);
        Assertions.assertEquals(g.connectedComponents(), components);
    }
}
