import java.util.*;

/** Mission 3: iterative DFS to avoid overflowing the Java call stack. */
public class Escape {
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols(), exit = n - 1;
        int[][] graph = Graphs.open(w, broken);
        int deadEnds = 0;
        for (int[] neighbours : graph) if (neighbours.length == 1) deadEnds++;

        boolean[] seen = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        int[] stackCell = new int[n];
        int[] stackNext = new int[n];
        int top = 0, visited = 1;
        stackCell[0] = 0;
        seen[0] = true;

        while (top >= 0 && !seen[exit]) {
            int u = stackCell[top];
            int[] ordered = new int[4];
            int count = 0;
            int[] candidates = {u - cols, u + 1, u + cols, u - 1};
            for (int j = 0; j < 4; j++) {
                int v = candidates[j];
                if (v < 0 || v >= n) continue;
                if ((j == 1 || j == 3) && v / cols != u / cols) continue;
                for (int neighbour : graph[u]) {
                    if (neighbour == v) { ordered[count++] = v; break; }
                }
            }
            if (stackNext[top] >= count) {
                top--;
                continue;
            }
            int v = ordered[stackNext[top]++];
            if (seen[v]) continue;
            seen[v] = true;
            visited++;
            parent[v] = u;
            if (v == exit) break;
            top++;
            stackCell[top] = v;
            stackNext[top] = 0;
        }

        if (!seen[exit]) return new Result(new int[0], visited, deadEnds);
        int length = 1;
        for (int v = exit; v != 0; v = parent[v]) length++;
        int[] path = new int[length];
        int v = exit;
        for (int i = length - 1; i >= 0; i--) {
            path[i] = v;
            if (v == 0) break;
            v = parent[v];
        }
        return new Result(path, visited, deadEnds);
    }
}
