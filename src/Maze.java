import java.util.*;

/** Missions 1 and 2: build a maze and find a minimum spanning tree. */
public class Maze {
    /** Break walls in file order, keeping only walls that connect different components. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k = 0; k < w.count() && count < chosen.length; k++) {
            if (dsu.union(w.a()[k], w.b()[k])) chosen[count++] = k;
        }
        return Arrays.copyOf(chosen, count);
    }

    /** Kruskal: process walls from lowest cost to highest cost. */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> w.cost()[i]));
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                chosen[count++] = k;
                if (count == chosen.length) break;
            }
        }
        return Arrays.copyOf(chosen, count);
    }

    /** Prim with a priority queue; returns the MST total cost. */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n <= 1) return 0;
        int[][] incident = new int[n][];
        int[] degree = new int[n];
        for (int i = 0; i < w.count(); i++) {
            degree[w.a()[i]]++;
            degree[w.b()[i]]++;
        }
        for (int i = 0; i < n; i++) incident[i] = new int[degree[i]];
        Arrays.fill(degree, 0);
        for (int i = 0; i < w.count(); i++) {
            incident[w.a()[i]][degree[w.a()[i]]++] = i;
            incident[w.b()[i]][degree[w.b()[i]]++] = i;
        }

        boolean[] used = new boolean[n];
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(e -> e[0]));
        used[0] = true;
        for (int edge : incident[0]) {
            int to = w.a()[edge] == 0 ? w.b()[edge] : w.a()[edge];
            pq.add(new long[]{w.cost()[edge], to, edge});
        }
        int taken = 1;
        long total = 0;
        while (!pq.isEmpty() && taken < n) {
            long[] e = pq.poll();
            int to = (int)e[1];
            if (used[to]) continue;
            used[to] = true;
            taken++;
            total += e[0];
            for (int edge : incident[to]) {
                int next = w.a()[edge] == to ? w.b()[edge] : w.a()[edge];
                if (!used[next]) pq.add(new long[]{w.cost()[edge], next, edge});
            }
        }
        if (taken != n) throw new IllegalArgumentException("Wall graph is disconnected");
        return total;
    }
}
