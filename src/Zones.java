import java.util.*;

/** Missions 4 and 5: Kosaraju SCCs and shortest route in the zone DAG. */
public class Zones {
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);
        boolean[] seen = new boolean[n];
        int[] finish = new int[n];
        int finishCount = 0;

        // First pass: iterative DFS on the original graph, recording finish order.
        int[] stackCell = new int[n];
        int[] stackNext = new int[n];
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stackCell[0] = start;
            stackNext[0] = 0;
            seen[start] = true;
            while (top >= 0) {
                int u = stackCell[top];
                if (stackNext[top] < out[u].length) {
                    int edge = out[u][stackNext[top]++];
                    int v = s.to()[edge];
                    if (!seen[v]) {
                        seen[v] = true;
                        top++;
                        stackCell[top] = v;
                        stackNext[top] = 0;
                    }
                } else {
                    finish[finishCount++] = u;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zone = 0;
        // Second pass: process reverse finishing order on the reversed graph.
        int[] todo = new int[n];
        for (int i = finishCount - 1; i >= 0; i--) {
            int start = finish[i];
            if (zoneOf[start] != -1) continue;
            int top = 0;
            todo[0] = start;
            zoneOf[start] = zone;
            while (top >= 0) {
                int u = todo[top--];
                for (int edge : in[u]) {
                    int v = s.from()[edge];
                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zone;
                        todo[++top] = v;
                    }
                }
            }
            zone++;
        }
        return zoneOf;
    }

    /**
     * Returns [longest chain length in zones, cheapest start-to-exit route cost].
     * Zone numbers are topological, so one forward pass is enough.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        final long INF = Long.MAX_VALUE / 4;
        long[] distance = new long[zoneCount];
        int[] stages = new int[zoneCount];
        Arrays.fill(distance, INF);
        Arrays.fill(stages, 1);
        int start = zoneOf[0], finish = zoneOf[s.n() - 1];
        distance[start] = 0;

        // For every zone pair, keep only the cheapest slide between them.
        HashMap<Long, Integer> cheapest = new HashMap<>();
        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            if (a == b) continue;
            long key = (((long)a) << 32) | (b & 0xffffffffL);
            Integer old = cheapest.get(key);
            if (old == null || s.time()[i] < old) cheapest.put(key, s.time()[i]);
        }

        @SuppressWarnings("unchecked")
        ArrayList<long[]>[] edges = new ArrayList[zoneCount];
        for (int z = 0; z < zoneCount; z++) edges[z] = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cheapest.entrySet()) {
            int a = (int)(entry.getKey() >>> 32);
            int b = (int)(long)entry.getKey();
            edges[a].add(new long[]{b, entry.getValue()});
        }

        for (int a = 0; a < zoneCount; a++) {
            for (long[] edge : edges[a]) {
                int b = (int)edge[0];
                stages[b] = Math.max(stages[b], stages[a] + 1);
                if (distance[a] != INF) {
                    distance[b] = Math.min(distance[b], distance[a] + edge[1]);
                }
            }
        }
        int longest = 0;
        for (int value : stages) longest = Math.max(longest, value);
        if (distance[finish] == INF) throw new IllegalArgumentException("Exit is unreachable from start");
        return new long[]{longest, distance[finish]};
    }
}
