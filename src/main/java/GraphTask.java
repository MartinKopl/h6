import java.util.*;

/** Container class to different classes, that makes the whole
 * set of classes one class formally.
 *
 * Problem: placing watchtowers in a network of villages.
 * The road network is a simple undirected graph: vertices are villages,
 * edges are roads. Each road AB is represented by two arcs AB and BA.
 * A tower placed in a village guards this village and all its neighbours.
 * The method {@link Graph#placeTowers()} finds a placement of towers
 * that leaves no village unguarded (a dominating set of the graph).
 * The placement is found by a fixed greedy rule, which keeps the number
 * of towers small. The exact minimum number of towers is NOT required
 * and is NOT guaranteed: the result is the greedy placement, which may
 * contain more towers than the minimum (see example 6).
 */
public class GraphTask {

    /** Main method. */
    public static void main (String[] args) {
        GraphTask a = new GraphTask();
        a.run();
    }

    /** Actual main method to run examples and everything. */
    public void run() {
        Graph g = new Graph ("G");
        g.createRandomSimpleGraph (6, 9);
        showExample (g, true);

        exampleEmptyGraph();
        exampleIsolatedVillages();
        exampleStar();
        examplePath();
        exampleTieBreak();
        exampleGreedyNotOptimal();
        exampleBigGraph (2500, 7500);
    }

    /** Example 1: empty graph, the result must be an empty list. */
    private void exampleEmptyGraph() {
        Graph g = new Graph ("Example 1: empty graph");
        showExample (g, true);
    }

    /** Example 2: three villages, one road. The isolated village C
     * must get its own tower. */
    private void exampleIsolatedVillages() {
        Graph g = new Graph ("Example 2: isolated village");
        Vertex c = g.createVertex ("C");
        Vertex b = g.createVertex ("B");
        Vertex a = g.createVertex ("A");
        g.createRoad (a, b);
        showExample (g, true);
    }

    /** Example 3: star. One tower in the centre K guards everything. */
    private void exampleStar() {
        Graph g = new Graph ("Example 3: star");
        Vertex k = g.createVertex ("K");
        String[] names = {"A", "B", "C", "D", "E"};
        for (int i = 0; i < names.length; i++) {
            Vertex v = g.createVertex (names [i]);
            g.createRoad (k, v);
        }
        showExample (g, true);
    }

    /** Example 4: path A-B-C-D-E-F-G. Greedy result is B, E, F or
     * similar with three towers (the exact minimum is also three). */
    private void examplePath() {
        Graph g = new Graph ("Example 4: path of seven villages");
        String[] names = {"A", "B", "C", "D", "E", "F", "G"};
        Vertex previous = null;
        for (int i = 0; i < names.length; i++) {
            Vertex v = g.createVertex (names [i]);
            if (previous != null) {
                g.createRoad (previous, v);
            }
            previous = v;
        }
        showExample (g, true);
    }

    /** Example 5: cycle A-B-C-D-A. All villages are equally good,
     * so the alphabetically first village A must be chosen first. */
    private void exampleTieBreak() {
        Graph g = new Graph ("Example 5: cycle, alphabetical tie-break");
        Vertex d = g.createVertex ("D");
        Vertex c = g.createVertex ("C");
        Vertex b = g.createVertex ("B");
        Vertex a = g.createVertex ("A");
        g.createRoad (a, b);
        g.createRoad (b, c);
        g.createRoad (c, d);
        g.createRoad (d, a);
        showExample (g, true);
    }

    /** Example 6: network where the greedy choice is not optimal.
     * Greedy takes C first (guards 6 villages) and needs 3 towers,
     * but towers in A and B alone guard all nine villages. */
    private void exampleGreedyNotOptimal() {
        Graph g = new Graph ("Example 6: greedy is not optimal");
        Vertex a = g.createVertex ("A");
        Vertex b = g.createVertex ("B");
        Vertex c = g.createVertex ("C");
        Vertex a1 = g.createVertex ("A1");
        Vertex a2 = g.createVertex ("A2");
        Vertex a3 = g.createVertex ("A3");
        Vertex b1 = g.createVertex ("B1");
        Vertex b2 = g.createVertex ("B2");
        Vertex b3 = g.createVertex ("B3");
        g.createRoad (a, a1);
        g.createRoad (a, a2);
        g.createRoad (a, a3);
        g.createRoad (a, c);
        g.createRoad (b, b1);
        g.createRoad (b, b2);
        g.createRoad (b, b3);
        g.createRoad (c, a1);
        g.createRoad (c, a2);
        g.createRoad (c, b1);
        g.createRoad (c, b2);
        showExample (g, true);
    }

    /** Example 7: big random graph, only the solution time is measured.
     * @param n number of villages
     * @param m number of roads
     */
    private void exampleBigGraph (int n, int m) {
        Graph g = new Graph ("Example 7: big random graph");
        g.createRandomSimpleGraph (n, m);
        long start = System.nanoTime();
        List<Vertex> towers = g.placeTowers();
        long stop = System.nanoTime();
        double milliseconds = (stop - start) / 1000000.0;
        System.out.println();
        System.out.println ("Example 7: big random graph");
        System.out.println ("Villages: " + n + ", roads: " + m);
        System.out.println ("Number of towers: " + towers.size());
        System.out.println ("All villages guarded: "
                + g.isEverythingGuarded (towers));
        System.out.println ("Solution time: " + milliseconds + " ms");
    }

    /** Print a small graph, solve it and print the result with checks.
     * The last printed line compares the toString() result of the graph
     * taken before and after solving. This only shows that the textual
     * representation (names and order of vertices and arcs) stayed the
     * same; it is not a proof that the graph was not changed.
     * @param g graph to solve
     * @param findExactMinimum if true, the exact minimum is also found
     *    by brute force (only for small graphs)
     */
    private void showExample (Graph g, boolean findExactMinimum) {
        System.out.println (g);
        String before = g.toString();
        List<Vertex> towers = g.placeTowers();
        String after = g.toString();
        System.out.println ("Greedy towers in order of choosing: " + towers);
        System.out.println ("Number of towers: " + towers.size());
        System.out.println ("All villages guarded: "
                + g.isEverythingGuarded (towers));
        if (findExactMinimum) {
            System.out.println ("Exact minimum number of towers: "
                    + g.exactMinimumTowerCount());
        }
        System.out.println ("toString() same before and after: "
                + before.equals (after));
    }

    /** Vertex represents one village in the road network.
     * Vertices of a graph form a linked list (field next), each vertex
     * has a linked list of outgoing arcs (field first).
     */
    class Vertex {

        private String id;
        private Vertex next;
        private Arc first;
        private int info = 0;
        // You can add more fields, if needed

        /** Create a vertex.
         * @param s name of the village
         * @param v next vertex in the list of vertices
         * @param e first outgoing arc
         */
        Vertex (String s, Vertex v, Arc e) {
            id = s;
            next = v;
            first = e;
        }

        /** Create a vertex without arcs.
         * @param s name of the village
         */
        Vertex (String s) {
            this (s, null, null);
        }

        /** Name of the village.
         * @return name of the village
         */
        @Override
        public String toString() {
            return id;
        }
    }


    /** Arc represents one arrow in the graph. Two-directional edges are
     * represented by two Arc objects (for both directions).
     */
    class Arc {

        private String id;
        private Vertex target;
        private Arc next;
        private int info = 0;
        // You can add more fields, if needed

        /** Create an arc.
         * @param s name of the arc
         * @param v target vertex
         * @param a next arc in the list of outgoing arcs
         */
        Arc (String s, Vertex v, Arc a) {
            id = s;
            target = v;
            next = a;
        }

        /** Create an arc without a target.
         * @param s name of the arc
         */
        Arc (String s) {
            this (s, null, null);
        }

        /** Name of the arc.
         * @return name of the arc
         */
        @Override
        public String toString() {
            return id;
        }
    }


    /** Graph represents the road network of villages as a linked list
     * of vertices, each vertex has its own list of outgoing arcs.
     */
    class Graph {

        private String id;
        private Vertex first;
        private int info = 0;
        // You can add more fields, if needed

        /** Create a graph.
         * @param s name of the graph
         * @param v first vertex
         */
        Graph (String s, Vertex v) {
            id = s;
            first = v;
        }

        /** Create an empty graph.
         * @param s name of the graph
         */
        Graph (String s) {
            this (s, null);
        }

        /** Textual representation of the graph (adjacency lists).
         * @return graph as a multi-line string
         */
        @Override
        public String toString() {
            String nl = System.getProperty ("line.separator");
            StringBuffer sb = new StringBuffer (nl);
            sb.append (id);
            sb.append (nl);
            Vertex v = first;
            while (v != null) {
                sb.append (v.toString());
                sb.append (" -->");
                Arc a = v.first;
                while (a != null) {
                    sb.append (" ");
                    sb.append (a.toString());
                    sb.append (" (");
                    sb.append (v.toString());
                    sb.append ("->");
                    sb.append (a.target.toString());
                    sb.append (")");
                    a = a.next;
                }
                sb.append (nl);
                v = v.next;
            }
            return sb.toString();
        }

        /** Create a new vertex and add it to the beginning of the
         * list of vertices.
         * @param vid name of the vertex
         * @return created vertex
         */
        public Vertex createVertex (String vid) {
            Vertex res = new Vertex (vid);
            res.next = first;
            first = res;
            return res;
        }

        /** Create a new arc and add it to the beginning of the
         * list of outgoing arcs of the source vertex.
         * @param aid name of the arc
         * @param from source vertex
         * @param to target vertex
         * @return created arc
         */
        public Arc createArc (String aid, Vertex from, Vertex to) {
            Arc res = new Arc (aid);
            res.next = from.first;
            from.first = res;
            res.target = to;
            return res;
        }

        /**
         * Create a connected undirected random tree with n vertices.
         * Each new vertex is connected to some random existing vertex.
         * @param n number of vertices added to this graph
         */
        public void createRandomTree (int n) {
            if (n <= 0)
                return;
            Vertex[] varray = new Vertex [n];
            for (int i = 0; i < n; i++) {
                varray [i] = createVertex ("v" + String.valueOf(n-i));
                if (i > 0) {
                    int vnr = (int)(Math.random()*i);
                    createArc ("a" + varray [vnr].toString() + "_"
                            + varray [i].toString(), varray [vnr], varray [i]);
                    createArc ("a" + varray [i].toString() + "_"
                            + varray [vnr].toString(), varray [i], varray [vnr]);
                } else {}
            }
        }

        /**
         * Create an adjacency matrix of this graph.
         * Side effect: corrupts info fields in the graph
         * @return adjacency matrix
         */
        public int[][] createAdjMatrix() {
            info = 0;
            Vertex v = first;
            while (v != null) {
                v.info = info++;
                v = v.next;
            }
            int[][] res = new int [info][info];
            v = first;
            while (v != null) {
                int i = v.info;
                Arc a = v.first;
                while (a != null) {
                    int j = a.target.info;
                    res [i][j]++;
                    a = a.next;
                }
                v = v.next;
            }
            return res;
        }

        /**
         * Create a connected simple (undirected, no loops, no multiple
         * arcs) random graph with n vertices and m edges.
         * @param n number of vertices
         * @param m number of edges
         */
        public void createRandomSimpleGraph (int n, int m) {
            if (n <= 0)
                return;
            if (n > 2500)
                throw new IllegalArgumentException ("Too many vertices: " + n);
            if (m < n-1 || m > n*(n-1)/2)
                throw new IllegalArgumentException
                        ("Impossible number of edges: " + m);
            first = null;
            createRandomTree (n);       // n-1 edges created here
            Vertex[] vert = new Vertex [n];
            Vertex v = first;
            int c = 0;
            while (v != null) {
                vert[c++] = v;
                v = v.next;
            }
            int[][] connected = createAdjMatrix();
            int edgeCount = m - n + 1;  // remaining edges
            while (edgeCount > 0) {
                int i = (int)(Math.random()*n);  // random source
                int j = (int)(Math.random()*n);  // random target
                if (i==j)
                    continue;  // no loops
                if (connected [i][j] != 0 || connected [j][i] != 0)
                    continue;  // no multiple edges
                Vertex vi = vert [i];
                Vertex vj = vert [j];
                createArc ("a" + vi.toString() + "_" + vj.toString(), vi, vj);
                connected [i][j] = 1;
                createArc ("a" + vj.toString() + "_" + vi.toString(), vj, vi);
                connected [j][i] = 1;
                edgeCount--;  // a new edge happily created
            }
        }

        /** Create an undirected road between two villages as two
         * opposite arcs AB and BA.
         * @param a first village
         * @param b second village
         */
        public void createRoad (Vertex a, Vertex b) {
            createArc ("a" + a.toString() + "_" + b.toString(), a, b);
            createArc ("a" + b.toString() + "_" + a.toString(), b, a);
        }

        /** Collect all vertices of this graph into a list.
         * @return list of vertices in the order of the linked list
         */
        private List<Vertex> vertexList() {
            List<Vertex> result = new ArrayList<Vertex>();
            Vertex v = first;
            while (v != null) {
                result.add (v);
                v = v.next;
            }
            return result;
        }

        /**
         * Find a placement of watchtowers that guards every village, using
         * the greedy rule described below. The result is a correct covering
         * placement, but not necessarily a minimum one: the method does not
         * search for the smallest possible number of towers. A tower guards
         * its own village and all neighbouring villages. On each step
         * the tower is placed in the village that would guard the biggest
         * number of villages that are not guarded yet. If several villages
         * are equally good, the village with the alphabetically first name
         * is chosen (names are compared as strings using
         * {@link String#compareTo(String)}). The process ends when every
         * village is guarded.
         *
         * An isolated village always gets its own tower. The result for
         * an empty graph is an empty list. The graph is not changed
         * (also the info fields are not used).
         *
         * Time complexity is O(k(n+m)), where n is the number of villages,
         * m is the number of arcs and k is the number of chosen towers.
         *
         * @return villages with towers in the order of choosing
         */
        public List<Vertex> placeTowers() {
            List<Vertex> towers = new ArrayList<Vertex>();
            List<Vertex> villages = vertexList();
            Set<Vertex> guarded = new HashSet<Vertex>();

            while (guarded.size() < villages.size()) {
                Vertex best = null;
                int bestGain = 0;
                for (Vertex candidate : villages) {
                    int gain = countNewlyGuarded (candidate, guarded);
                    boolean better = false;
                    if (gain > bestGain) {
                        better = true;
                    } else if (gain == bestGain && best != null
                            && candidate.id.compareTo (best.id) < 0) {
                        better = true;
                    }
                    if (better) {
                        best = candidate;
                        bestGain = gain;
                    }
                }
                // best is never null here: an unguarded village itself
                // always has gain of at least one
                towers.add (best);
                guarded.add (best);
                Arc a = best.first;
                while (a != null) {
                    guarded.add (a.target);
                    a = a.next;
                }
            }
            return towers;
        }

        /** Count the villages that a tower in the given village would
         * guard in addition to the villages that are guarded already.
         * @param village village where the tower would be placed
         * @param guarded set of villages that are guarded already
         * @return number of newly guarded villages
         */
        private int countNewlyGuarded (Vertex village, Set<Vertex> guarded) {
            Set<Vertex> newlyGuarded = new HashSet<Vertex>();
            if (!guarded.contains (village)) {
                newlyGuarded.add (village);
            }
            Arc a = village.first;
            while (a != null) {
                if (!guarded.contains (a.target)) {
                    newlyGuarded.add (a.target);
                }
                a = a.next;
            }
            return newlyGuarded.size();
        }

        /** Check that the given towers guard every village of this graph.
         * @param towers villages with towers
         * @return true, if no village is left unguarded
         */
        public boolean isEverythingGuarded (List<Vertex> towers) {
            Set<Vertex> guarded = new HashSet<Vertex>();
            for (Vertex tower : towers) {
                guarded.add (tower);
                Arc a = tower.first;
                while (a != null) {
                    guarded.add (a.target);
                    a = a.next;
                }
            }
            for (Vertex village : vertexList()) {
                if (!guarded.contains (village)) {
                    return false;
                }
            }
            return true;
        }

        /** Find the exact minimum number of towers by trying all subsets
         * of villages (brute force). Meant only for checking the greedy
         * result on small examples.
         * @return minimum number of towers needed to guard all villages
         * @throws IllegalArgumentException if the graph has more than
         *    20 villages
         */
        public int exactMinimumTowerCount() {
            List<Vertex> villages = vertexList();
            int n = villages.size();
            if (n > 20) {
                throw new IllegalArgumentException
                        ("Too many villages for brute force: " + n);
            }
            int best = n;
            for (int mask = 0; mask < (1 << n); mask++) {
                if (Integer.bitCount (mask) >= best) {
                    continue;
                }
                List<Vertex> chosen = new ArrayList<Vertex>();
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) {
                        chosen.add (villages.get (i));
                    }
                }
                if (isEverythingGuarded (chosen)) {
                    best = chosen.size();
                }
            }
            return best;
        }
    }

}