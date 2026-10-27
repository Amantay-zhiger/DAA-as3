package src;

import java.util.*;

public class FastestRoute {
    public static class Result {
        public int flowStages;
        public long fastestRouteCost;
    }

    public static Result compute(int numCells, List<Graphs.Edge> slides, Zones.Result zonesRes) {
        int Z = zonesRes.numZones;
        int startZone = zonesRes.cellToZone[0];
        int exitZone = zonesRes.cellToZone[numCells - 1];

        Map<Long, Integer> minEdgeCost = new HashMap<>();
        List<Set<Integer>> zoneAdj = new ArrayList<>();
        for (int i = 0; i < Z; i++) zoneAdj.add(new HashSet<>());

        for (Graphs.Edge e : slides) {
            int zu = zonesRes.cellToZone[e.u];
            int zv = zonesRes.cellToZone[e.v];
            if (zu != zv) {
                zoneAdj.get(zu).add(zv);
                long key = (((long) zu) << 32) | (zv & 0xFFFFFFFFL);
                minEdgeCost.put(key, Math.min(minEdgeCost.getOrDefault(key, Integer.MAX_VALUE), e.weight));
            }
        }

        // Flow Stages (Длиннейший путь)
        int[] dpStages = new int[Z];
        for (int i = Z - 1; i >= 0; i--) {
            int maxNext = 0;
            for (int v : zoneAdj.get(i)) {
                maxNext = Math.max(maxNext, dpStages[v]);
            }
            dpStages[i] = 1 + maxNext;
        }

        // Fastest Route Cost (Кратчайший путь)
        long[] dist = new long[Z];
        Arrays.fill(dist, Long.MAX_VALUE / 2);
        dist[startZone] = 0;

        for (int u = 0; u < Z; u++) {
            if (dist[u] >= Long.MAX_VALUE / 4) continue;
            for (int v : zoneAdj.get(u)) {
                long key = (((long) u) << 32) | (v & 0xFFFFFFFFL);
                int w = minEdgeCost.get(key);
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                }
            }
        }

        Result res = new Result();
        res.flowStages = dpStages[startZone];
        res.fastestRouteCost = dist[exitZone];
        return res;
    }
}