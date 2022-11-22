package graphdemo;




/**
 * A test bed for a weighted digraph abstract data type implementation
 * and implementations of elementary classical graph algorithms that use the
 * ADT
 * @see GraphAPI.java, Graph.java, City.java
 * @author Duncan, Tyler Scott
 * <pre>
 * usage: GraphDemo <graphFileName>
 * <graphFileName> - a text file containing the description of the graph
 * in DIMACS file format
 * Date: 11/22/2022
 * course: csc 3102
 * programming project 3
 * Instructor: Dr. Duncan
 * </pre>
 */
//7, 1

import java.awt.Label;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

    public class GraphDemo {
        public static final Double INFINITY = Double.POSITIVE_INFINITY;
        public static final Integer NIL = -1;

        public static void main(String[] args) throws GraphException {
            if (args.length != 1) {
                System.out.println("Usage: GraphDemo <filename>");
                System.exit(1);
            }
            City c1, c2;
            Scanner console;
            int menuReturnValue, i, j;
            Function<City, PrintStream> f = aCity -> System.out.printf("%-2d  %-30s%n", aCity.getKey(), aCity.getLabel().trim());
            Graph<City> g = readGraph(args[0]);
            long s = g.size();
            menuReturnValue = -1;
            while (menuReturnValue != 0) {
                menuReturnValue = menu();
                switch (menuReturnValue) {
                    case 1: //post-order depth-first-search traversal of g'

                        System.out.println();
                        System.out.println("Breadth-First-Search Traversal of the Graph In " + args[0]);
                        System.out.println("==========================================================================");
                        g.bfsTraverse(f);

                        System.out.println("==========================================================================");
                        System.out.println();
                        System.out.println("Depth-First-Search Traversal of the Graph In " + args[0]);
                        System.out.println("==========================================================================");
                        g.dfsTraverse(f);
                        System.out.println("==========================================================================");
                        System.out.println();
                        System.out.println();
                        break;

                    case 2: //Connected Components of G
                        System.out.println();
                        System.out.println("Connected Components in " + args[0]);
                        System.out.println();


                        if (g.size() > 0) {
                            int[] components = new int[(int) g.size()];
                            int numComp = getComponents(g, components);

                            System.out.println("case: " + Arrays.toString(components));

                            for (int k = 1; k <= numComp; k++) {
                                System.out.println("*** Component # " + k + " ***");
                                for (int m = 1; m <= g.size(); m++) {
                                    if (components[m - 1] == k) {
                                        System.out.println("\n" + g.retrieveVertex(new City(m)).getLabel().trim());
                                    }
                                }
                            }
                            System.out.println("--------------------------");
                            //Number of Components: 3
                            System.out.println("\nNumber of Components: " + numComp);
                            //End code
                        } else
                            System.out.println("The graph has no connected component.");
                        System.out.println();
                        System.out.println();
                        System.out.println();
                        break;

                    case 3://Shortest-path algorithm
                        console = new Scanner(System.in);
                        System.out.printf("Enter the source vertex: ");
                        i = console.nextInt();
                        System.out.printf("Enter the destination vertex: ");
                        j = console.nextInt();
                        if (g.isPath(new City(i), new City(j)) && g.isPath(new City(j), new City(i))) {
                            int dest;
                            double[][] cost = new double[(int) g.size()][(int) g.size()];
                            int[][] path = new int[(int) g.size()][(int) g.size()];
                            int initial = i;
                            floyd(g, cost, path);
                            System.out.printf("Shortest round trip from %s to %s:%n", g.retrieveVertex(new City(i)).getLabel().trim(), g.retrieveVertex(new City(j)).getLabel().trim());
                            System.out.println("=========================================================================================");

                            int temp = i;
                            System.out.printf("%s -> %s:%n", g.retrieveVertex(new City(i)).getLabel().trim(), g.retrieveVertex(new City(j)).getLabel().trim());

                            while (temp != j) {
                                System.out.printf("%-30s -> %-30s %.2f mi%n",
                                        g.retrieveVertex(new City(temp)).getLabel().trim(), g.retrieveVertex(new City(path[temp - 1][j - 1])).getLabel().trim(),
                                        g.retrieveEdge(new City(temp), new City(path[temp - 1][j - 1])));
                                temp = path[temp - 1][j - 1];
                            }
                            System.out.printf("Distance: %.2f mi%n%n", cost[i - 1][j - 1]);
                            System.out.printf("%s -> %s:%n", g.retrieveVertex(new City(j)).getLabel().trim(), g.retrieveVertex(new City(i)).getLabel().trim());

                            temp = j;
                            while (temp != (i)) {
                                System.out.printf("%-30s -> %-30s %.2f mi%n",
                                        g.retrieveVertex(new City(temp)).getLabel().trim(), g.retrieveVertex(new City(path[temp - 1][i - 1])).getLabel().trim(),
                                        g.retrieveEdge(new City(temp), new City(path[temp - 1][i - 1])));
                                temp = path[temp - 1][i - 1];
                            }
                            System.out.printf("Distance: %.2f mi%n%n", cost[j - 1][i - 1]);
                            //End code
                            System.out.println("=========================================================================================");
                            System.out.printf("Round Trip Distance: %.2f miles.%n%n", cost[i - 1][j - 1] + cost[j - 1][i - 1]);
                        } else
                            System.out.printf("There is no path.%n%n");
                        break;

                    case 4: //Check whether g is bipartite
                        System.out.println();
                        AtomicBoolean bip = new AtomicBoolean(true);
                        int[] part = isBipartite(g, bip);
                        if (bip.get()) {
                            if (g.size() == 0)
                                System.out.println("An empty graph is vacuously bipartite.");
                            else {
                                System.out.printf("The Graph is Bipartite.%n%n");
                                System.out.println("First Partition: ");
                                for (i = 1; i <= g.size(); i++) {
                                    if (part[i] == 1)
                                        System.out.printf("%d. %s%n ", i, g.retrieveVertex(new City(i)).getLabel().trim());
                                }
                                System.out.println();
                                System.out.println("Second Partition: ");
                                int k = 0;
                                for (i = 1; i <= g.size(); i++) {
                                    if (part[i] == 0) {
                                        System.out.printf("%d. %s%n ", i, g.retrieveVertex(new City(i)).getLabel().trim());
                                        k++;
                                    }
                                }
                                if (k == 0)
                                    System.out.println("EMPTY");
                            }
                        } else
                            System.out.println("The graph is not bipartite.");
                        System.out.println();
                        break;
                    case 5: //primMST;
                        int edgesInMST = 0;
                        System.out.printf("Enter the root of the MST: ");
                        console = new Scanner(System.in);
                        j = console.nextInt();
                        int[] mst = new int[(int) g.size()];
                        double totalWt = primMST(g, j, mst);
                        String cityNameA, cityNameB;
                        System.out.println();
                        for (i = 1; i <= g.size(); i++) {
                            if (mst[i - 1] < 1)
                                cityNameA = "NONE";
                            else {
                                edgesInMST++;
                                cityNameA = g.retrieveVertex(new City(mst[i - 1])).getLabel().trim();
                            }
                            cityNameB = g.retrieveVertex(new City(i)).getLabel().trim();
                            System.out.printf("%d-%s parent[%d] <- %d (%s)%n", i, cityNameB, i, mst[i - 1], cityNameA);
                        }
                        System.out.printf("The weight of the minimum spanning tree/forest is %.2f miles.%n", totalWt);
                        System.out.printf("Spanning Tree Edge Density is %.2f%%.%n%n", 100.0 * edgesInMST / g.countEdges());
                        break;
                    default:
                        ;
                } //end switch
            }//end while
        }//end main

        /**
         * This method reads a text file formatted as described in the project description.
         *
         * @param filename the name of the DIMACS formatted graph file.
         * @return an instance of a graph.
         */
        private static Graph<City> readGraph(String filename) {
            try {
                Graph<City> newGraph = new Graph();
                try (FileReader reader = new FileReader(filename)) {
                    char temp;
                    City c1, c2, aCity;
                    String tmp;
                    int k, m, v1, v2, j, size = 0, nEdges = 0;
                    Integer key, v1Key, v2Key;
                    Double weight;
                    Scanner in = new Scanner(reader);
                    while (in.hasNext()) {
                        tmp = in.next();
                        temp = tmp.charAt(0);
                        if (temp == 'p') {
                            size = in.nextInt();
                            nEdges = in.nextInt();
                        } else if (temp == 'c') {
                            in.nextLine();
                        } else if (temp == 'n') {
                            key = in.nextInt();
                            tmp = in.nextLine();
                            aCity = new City(key, tmp);
                            newGraph.insertVertex(aCity);
                        } else if (temp == 'e') {
                            v1Key = in.nextInt();
                            v2Key = in.nextInt();
                            weight = in.nextDouble();
                            c1 = new City(v1Key);
                            c2 = new City(v2Key);
                            newGraph.insertEdge(c1, c2, weight);
                        }
                    }
                }
                return newGraph;
            } catch (IOException exception) {
                System.out.println("Error processing file: " + exception);
            }
            return null;
        }

        /**
         * Display the menu interface for the application.
         *
         * @return the menu option selected.
         */
        private static int menu() {
            Scanner console = new Scanner(System.in);
            //int option;
            String option;
            do {
                System.out.println("  BASIC WEIGHTED GRAPH APPLICATION   ");
                System.out.println("=======================================================");
                System.out.println("[1] BFS/DFS Traversal of G");
                System.out.println("[2] Connected Components of G");
                System.out.println("[3] Floyd's Shortest Round Trip in G");
                System.out.println("[4] Check whether G is Bipartite");
                System.out.println("[5] Prim's Minimum Spanning Tree/Forest in G");
                System.out.println("[0] Quit");
                System.out.println("=====================================");
                System.out.printf("Select an option: ");
                option = console.nextLine().trim();
                try {
                    int choice = Integer.parseInt(option);
                    if (choice < 0 || choice > 7) {
                        System.out.println("Invalid option...Try again");
                        System.out.println();
                    } else
                        return choice;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid option...Try again");
                }
            } while (true);
        }

        /**
         * Determines whether an undirected graph is bipartite
         *
         * @param g         an undirected graph
         * @param bipartite is true if g is bipartite, otherwise false
         * @return an array of size |G|+1. The first entry is |G| and the remaining
         * entries are in {0,1} when g is bipartite where [i] = 0 if vertex i
         * is in the first partition and 1 if it is in the second partition;
         * if g is not bipartite NULL returned.
         */
        private static int[] isBipartite(Graph<City> g, AtomicBoolean bipartite) throws GraphException {
            int G = (int) g.size();  //-1 not sure but we start to compare from 0.
            int[] mainP = new int[(int) g.size() + 1];
            Arrays.fill(mainP, -1);
            bipartite.set(true);

            for (int i = 1; i <= G - 1; i++) {

                if (mainP[i] == -1) {
                    mainP[i] = 0;
                }
                for (int j = i + 1; j < G; j++) {
                    if (g.isEdge(new City(i), new City(j))) { //check here
                        if (mainP[i] == mainP[j]) {
                            bipartite.set(false);
                            return null;
                        }
                        if (mainP[j] == -1) {
                            mainP[j] = (mainP[i] + 1) % 2;
                        }
                    }
                }
            }
            return mainP;
        }


        /**
         * This method computes the cost and path matrices using the
         * Floyd all-pairs shortest path algorithm.
         *
         * @param g    an instance of a weighted directed graph.
         * @param dist a matrix containing distances between pairs of vertices.
         * @param path a matrix of intermediate vertices along the path between a pair
         *             of vertices. 0 indicates that the two vertices are adjacent.
         * @return none.
         */
        private static void floyd(Graph<City> g, double dist[][], int path[][]) throws GraphException {
            if (g.isEmpty()) {
                throw new GraphException("Empty graph");
            }
            int i, j, k;
            for (i = 1; i <= g.size(); i++) {
                for (j = 1; j <= g.size(); j++) {
                    if (i == j) {
                        path[i - 1][j - 1] = j;
                        dist[i - 1][j - 1] = 0;
                    } else if (g.isEdge(new City(i), new City(j))) {
                        path[i - 1][j - 1] = j;
                        dist[i - 1][j - 1] = (int) g.retrieveEdge(new City(i), new City(j));
                    } else {
                        dist[i - 1][j - 1] = INFINITY;
                        path[i - 1][j - 1] = NIL;
                    }
                }
            }
            for (i = 1; i <= g.size(); i++) {
                dist[i - 1][i - 1] = 0;
                path[i - 1][i - 1] = i;
            }
            for (k = 1; k <= g.size(); k++) {
                for (i = 1; i <= g.size(); i++) {
                    for (j = 1; j <= g.size(); j++) {
                        if (dist[i - 1][k - 1] != INFINITY && dist[k - 1][j - 1] != INFINITY
                                && dist[i - 1][j - 1] > dist[i - 1][k - 1] + dist[k - 1][j - 1]) {
                            path[i - 1][j - 1] = path[i - 1][k - 1];
                            dist[i - 1][j - 1] = dist[i - 1][k - 1] + dist[k - 1][j - 1];
                        }
                    }
                }
            }
        }


        /**
         * This method generates a minimum spanning tree rooted at a given
         * vertex, root. If no such MST exists, then it generates a minimum
         * spanning forest.
         *
         * @param g      a weighted undirected graph
         * @param root   root of the minimum spanning tree, when one exists.
         * @param parent the parent implementation of the minimum spanning tree/forest
         * @return the weight of such a tree or forest.
         * @throws GraphException when this graph is empty
         *                        <pre>
         *                        {@code
         *                        If a minimum spanning tree rooted at r is in the graph,
         *                        the parent implementation of a minimum spanning tree or forest is
         *                        determined. If no such tree exist, the parent implementation
         *                        of an MSF is generated. If the tree is empty, an exception
         *                        is generated.
         *                        }
         *                        </pre>
         */
        private static double primMST(Graph<City> g, int root, int[] parent) throws GraphException {
            //implement this method
            int i;
            if (g.isEmpty())
                throw new GraphException("Empty graph in call to primMST()");
            if (!g.isVertex(new City(root)))
                throw new GraphException("Non-existent root in call to primMST()");
            int numVertices = (int) g.size();
            double[] dist = new double[numVertices + 1];
            boolean[] processed = new boolean[numVertices];
            for (i = 0; i < numVertices; i++) {
                dist[i] = INFINITY;
                parent[i] = -1;
            }
            dist[root - 1] = 0;
            double totalWeight = 0;
            class Node {
                public int parent, id;
                public double key;

                public Node() {

                }

                public Node(int p, int v, double k) {
                    parent = p;
                    id = v;
                    key = k;
                }
            }

            Comparator<Node> cmp = (v1, v2) ->
            {
                double d = v1.key - v2.key;
                if (d < 0)
                    return -1;
                if (d > 0)
                    return 1;
                d = v1.id - v2.id;
                if (d < 0)
                    return -1;
                if (d > 0)
                    return 1;
                return 0;
            };

            //Define an instance of the PriorityQueue class that uses the comparator;

            PriorityQueue<Node> pq = new PriorityQueue<>(cmp);
            boolean[] vertexInTree = new boolean[(int) g.size() + 1];
            Arrays.fill(vertexInTree, false);
            int vCt = 0;

            int treeWeight = 0;

            for (int j = 0; j < numVertices; j++) {
                pq.add(new Node(-1, i, INFINITY));
            }
            pq.add(new Node(-1, root, 0));

            int[] msTree = new int[(int) g.size()];
            Arrays.fill(msTree, -1);

            while (vCt < numVertices) {
                Node u = pq.remove();
                while (vertexInTree[u.id] == true) {
                    u = pq.remove();
                }
                vCt++;
                msTree[u.id - 1] = u.parent;
                if (msTree[u.id - 1] == -1) {
                    u.key = 0;
                }
                dist[u.id] = u.key;
                treeWeight += u.key;
                for (int v = 1; v <= numVertices; v++) {
                    if (g.isEdge(new City(u.id), new City(v)) && vertexInTree[v] == false) {
                        if (g.retrieveEdge(new City(u.id), new City(v)) < dist[v]) {
                            dist[v] = g.retrieveEdge(new City(u.id), new City(v));
                            msTree[v - 1] = u.id;
                            pq.add(new Node(u.id, v, dist[v]));
                        }
                    } else if (g.isEdge(new City(v), new City(u.id)) && vertexInTree[v] == false) {
                        if (g.retrieveEdge(new City(v), new City(u.id)) < dist[v]) {
                            dist[v] = g.retrieveEdge(new City(v), new City(u.id));
                            msTree[v - 1] = u.id;
                            pq.add(new Node(u.id, v, dist[v]));
                        }
                    }
                }
            }

            for (int j = 0; j < numVertices; j++) {
                parent[j] = msTree[j];
            }

            return treeWeight;
        }


        /**
         * Generates the connected components of this graph
         *
         * @param g          a graph of city objects
         * @param components an associative array of city
         *                   key to component number: if components[i] = j, then
         *                   city i+1 is in component j.
         * @return the number of components in this graph
         * @throws GraphException
         */
        private static int getComponents(Graph<City> g, int components[]) throws GraphException {

            //start at the beginning
            //begin traversal
            //each vertex that can be reached is a component
            //keep doing this until all are put into

            if (g.isEmpty()) {
                throw new GraphException("Empty graph");
            }
            int count = 0, numVertices = 0;
            Queue<Integer> q = new LinkedList<>();
            while (numVertices < g.size()) {
                count++;
                int v = 1;
                while (components[v - 1] != 0) {
                    v++;
                }
                q.add(v);
                components[v - 1] = count;
                numVertices++;
                while (!q.isEmpty()) {
                    int r = q.poll();
                    for (int i = 1 + r; i <= g.size(); i++) {
                        if (g.isEdge(new City(r), new City(i)) && components[i - 1] == 0) {
                            q.add(i);
                            components[i - 1] = count;
                            numVertices++;
                        }
                    }
                }
            }
            return count;
        }
    }


