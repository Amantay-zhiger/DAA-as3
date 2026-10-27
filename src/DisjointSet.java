/**
 * Mission 0. Disjoint set union (DSU).
 * Union by size and path compression.
 */
public class DisjointSet {
    private final int[] parent;
    private final int[] size;
    private int sets;

    public DisjointSet(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        parent = new int[n];
        size = new int[n];
        sets = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    /** The root of the set that contains x, with path compression. */
    public int find(int x) {
        if (x < 0 || x >= parent.length) throw new IndexOutOfBoundsException("cell: " + x);
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    /** Joins two sets; false means they were already one set. */
    public boolean union(int a, int b) {
        int rootA = find(a), rootB = find(b);
        if (rootA == rootB) return false;
        if (size[rootA] < size[rootB]) {
            int temp = rootA; rootA = rootB; rootB = temp;
        }
        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        sets--;
        return true;
    }

    public boolean connected(int a, int b) { return find(a) == find(b); }
    public int count() { return sets; }
}
