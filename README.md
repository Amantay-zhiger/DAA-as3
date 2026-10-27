# Assignment 3. Maze Escape

Student ID: **253091**
Name: **Zhiger Amantay**
Group: **SE-2510**
Maze size (rows x columns): **12 x 12**

## Program output


Broken: 143
Kept: 121
Last broken wall: 236
MST cost: 40988
Heaviest broken wall: 742
Path length: 30
DFS visited: 106
Dead ends: 42
Zones: 9
Largest zone: 26
Is DAG: 0
Flow stages: 6
Fastest route cost: 16


## Questions

**Mission 0**

`find(x)` returns the representative (root) of the group containing cell `x`. `union(a, b)` joins two different groups and returns `false` if they are already in the same group. Path compression shortens paths during `find`, and union by size attaches the smaller tree under the larger tree.

**Mission 1. Why is Broken always R*C - 1, and Kept always (R-1)*(C-1)?**

The maze starts with `R*C` separate cells. Every broken wall joins two previously separate groups, reducing the number of groups by one, until all cells are connected; therefore exactly `R*C - 1` walls are broken. The total number of inner walls is `R*(C-1) + C*(R-1)`, so the number kept is `R*(C-1) + C*(R-1) - (R*C-1) = (R-1)*(C-1)`.

**Mission 2**

Fill in the measured cost after running the program. Kruskal and Prim both find a minimum spanning tree of the same connected, weighted graph. A minimum spanning tree has minimum possible total edge cost, so both algorithms must return the same total even though they may select edges in a different order.

**Mission 3**

Mission 1 creates a spanning tree: all cells are connected and no cycle is added, because a wall is broken only when its endpoints are in different DSU sets. In a tree there is exactly one path between any two vertices. The DFS is iterative and uses arrays as an explicit stack rather than recursive method calls, so a long path through a 1000 x 1000 maze does not overflow the Java call stack.

**Mission 4**

The first pass records DFS finishing order. Processing vertices in reverse finishing order on the reversed graph groups together exactly those vertices that can reach one another, producing strongly connected components in topological order. A directed cycle with two or more cells puts those cells in the same strongly connected component, so there are fewer zones than cells; if every zone contains one cell, no directed cycle exists (self-slides are forbidden by the task).

**Mission 5**

The graph of strongly connected components is a DAG, and Kosaraju numbers its zones in topological order. Therefore every edge goes from a smaller zone number to a larger one, so when a zone is processed all possible incoming updates have already been considered; one forward pass computes the longest chain and cheapest route. Building the zone graph and processing it takes expected `O(n + m)` time with the hash map used to keep the cheapest edge for each pair of zones, and `O(n + m)` space.

## AI use

I used **ChatGPT** to help understand the assignment, implement and review the algorithms, and explain the code. I compiled the project and checked it with the supplied examples. I will review the code and make sure I can explain it during the defense.

## Timing (Mission 7)

Milliseconds, averages from 5 runs of `java -cp out Bench` on the current test environment. Your own computer may produce different times.

| maze | Kruskal average (ms) | Prim average (ms) |
|---|---:|---:|
| 100 x 100 | 12 | 11 |
| 300 x 300 | 116 | 67 |
| 1000 x 1000 | 1294 | 1961 |

The running time grows as the maze becomes larger because the number of cells and walls increases. Kruskal sorts all walls, which costs `O(m log m)`; Prim uses a priority queue and is also approximately `O(m log m)` here, but actual timings depend on the data structure overhead, Java warm-up, and the computer. These numbers are one measurement run and are not universal.

## Complexity

`n` is the number of cells and `m` is the number of walls or slides. For the maze grid, the number of walls is `O(n)`.

| algorithm | time | space | assumptions |
|---|---|---|---|
| DSU `find` and `union` | `O(α(n))` amortized | `O(n)` for the DSU structure | Path compression and union by size |
| Kruskal | `O(m log m)` | `O(n + m)` | Sorting walls and using DSU |
| Prim | `O(m log m)` | `O(n + m)` | Adjacency lists and priority queue |
| DFS | `O(n + m)` | `O(n)` | Iterative DFS with arrays as a stack |
| Kosaraju | `O(n + m)` | `O(n + m)` | Two iterative DFS passes |
| Route in the zone graph | expected `O(n + m)` | `O(n + m)` | Hash map for cheapest edge per zone pair; zones are topologically ordered |
