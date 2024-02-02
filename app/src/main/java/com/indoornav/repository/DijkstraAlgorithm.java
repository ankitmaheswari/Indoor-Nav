package com.indoornav.repository;

import java.util.ArrayList;
import java.util.Scanner;

public class DijkstraAlgorithm {

    /**
     * @param args
     */

    public static boolean[] pathDetermined;
    public static int noOfVertices,noOfEdges;
    public static Graph<Integer> graph=null;
    public static int[] d;
    public final static int INFINITY=Integer.MAX_VALUE;

    public static void main(String[] args) {
        // TODO Auto-generated method stub


        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();
        dijkstraAlgorithm.findShortestPath();

    }

    public void constructGraph()
    {

        int u, v, w;
        Scanner scanner=new Scanner(System.in);
        while(scanner.hasNext())
        {
            noOfVertices=scanner.nextInt();
            noOfEdges=scanner.nextInt();

            //For the next function 'doDepthFirstSearch'
            pathDetermined=new boolean[noOfVertices+1];
            d=new int[noOfVertices+1];


            graph=new Graph<Integer>(noOfVertices);
            for(int i=0;i<noOfEdges;i++)
            {
                u=scanner.nextInt();
                v=scanner.nextInt();
                w=scanner.nextInt();
                graph.addEdge(u, v, w);
                //graph.addEdge(v, u, w);
            }
            break;
        }
        graph.printGraph();

    }

    /**
     * Initialize a single source graph
     * INITIALIZE-SINGLE-SOURCE(G, s)
     for each vertex v in V [G]
     do d[v] <- Infinity
     ¹[v] <- NIL
     d[s] <- 0
     *
     */
    public void initializeSingleSource()
    {
        //Initialize source distance as zero
        d[1]=0;

        //Initialize all vertices distance as infinity
        for(int i=2;i<=noOfVertices;i++)
        {
            d[i]=INFINITY;
        }
    }

    /**
     * Method to relax an edge
     *
     * RELAX(u, v, w)
     if d[v] > d[u] +w(u, v)
     then d[v] <- d[u] +w(u, v)
     ¹[v] <- u
     *
     *
     * @param u
     * @param v
     * @param w
     */
    public void doRelax(int u, int v, int w)
    {
        if(d[v]>d[u]+w)
            d[v]=d[u]+w;
    }

    /**
     * Function to find the shortest path using Dijkstra's Algorithm
     *
     *
     * DIJKSTRA(G, w, s)
     INITIALIZE-SINGLE-SOURCE(G, s)
     S <- Empty
     Q <- V[G]
     while Q != Empty
     do u <- EXTRACT-MIN(Q)
     S <- S U u
     for each vertex v in Adj[u]
     do RELAX(u, v, w)
     *
     */
    public void findDijkstraShortestPath()
    {
        initializeSingleSource();
        for(int i=1;i<=noOfVertices;i++)
        {
            pathDetermined[i]=true;
            ArrayList<Edge> adjacentList=graph.getOutEdges(i);
            if(adjacentList!=null)
                for(Edge e : adjacentList)
                {
                    doRelax((Integer)e.u, (Integer)e.v, (Integer)e.w);
                }

        }
    }

    /**
     * To construct the graph and find the shortest path
     *
     */
    public void findShortestPath()
    {
        constructGraph();
        findDijkstraShortestPath();

        for(int i=1;i<=noOfVertices;i++)
        {
            System.out.println("Distance for "+i+" is "+d[i] );
        }
    }


}
