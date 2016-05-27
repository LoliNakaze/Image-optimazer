package fr.umlv.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public class Graph {
	private final ArrayList<ArrayList<Edge>> adjacenyList;
	final static int INFINI = 256;
	private final static int SOURCE = 0;
	private final int TARGET;

	/**
	 * The Constructor of the Class Graph.java.
	 * return A Graph with the number of vertices given in parameters. <br>
	 * The source is the vertex 0 and the target is the last vertex.
	 * @param vertexCount the number of vertices in the graph.
	 */
	public Graph(int vertexCount) {
		TARGET = vertexCount - 1;
		adjacenyList = new ArrayList<>(vertexCount);
		
		for (int v = 0; v < vertexCount; v++)
			adjacenyList.add(new ArrayList<>());
	}
	
	/**
	 * The methode to have the number of vertex in the graph.
	 * @return the number of vertices in the graph.
	 */
	public int vertices() {
		return adjacenyList.size();
	}
	
	/**
	 * This methode add an Edge in the list in the graph.
	 * @param edge the edge you want to add in the graph.
	 */
	public void addEdge(Edge edge) {
		adjacenyList.get(edge.from).add(edge);
		adjacenyList.get(edge.to).add(edge);
	}
	
	/**
	 * 
	 * @param vertex the vertex you want to have all the vertices.
	 * @return the list of the Edge for the vertex given in argument.
	 */
	public Iterable<Edge> adjacent(int vertex) {
		return adjacenyList.get(vertex);
	}
	/**
	 * 
	 * @return List of all the edges in the graph.
	 */
	public Iterable<Edge> edges() {
		ArrayList<Edge> list = new ArrayList<>();
		for (int vertex = 0; vertex < vertices(); vertex++) {
			for (Edge edge : adjacent(vertex)) {
				if (edge.to != vertex)
					list.add(edge);
			}
		}
		return list;
	}
	
	/**
	 * Convert a 2D tab of interest and creat a graph with the tab <br>
	 * and fill the edges with the good interest
	 * @param itr take a 2D tab of interest
	 * @return the graph created with the 2D tab of interest
	 */
	public static Graph toGraph (int [][] itr){
		int i, j;
		int line, col;
		int state;
		
		line = itr.length;
		col = itr[0].length;
		Graph g = new Graph (line * col + 2); // on veut un sommet par pixel donc on calcule le nombre de pixel avec itr et on ajoute les deux sommets s et t
		for (i = 0; i < line; i++){
			g.addEdge(new Edge(SOURCE, i+1, INFINI , 0));
			g.addEdge(new Edge ((line * col) - i, g.TARGET, itr[(line - 1) - i][(col - 1)], 0));
		}
		
		for (i = 1; i <= line; i++){
			for (j = 1; j <= col; j++){
				state = i + ((j-1) * line);
				// Si je ne suis pas sur le bord droit de l'image alors je peux alors l'arc de (i,j) -> (i,j+1)
				if (col != j)
					g.addEdge(new Edge(state, state + line, itr[i-1][j-1], 0));
				// Si je ne suis pas sur le bord droit ni sur le bord bas de l'image alors je peux alors l'arc de (i,j) -> (i+1,j-1)
				if (1 != j && 1 != i)
					g.addEdge(new Edge( state, state - line - 1, INFINI, 0));
				
				// Si je ne suis pas sur le bord gauche de l'image alors je peux alors l'arc de (i,j) -> (i,j-1)
				if (1 != j)
					g.addEdge(new Edge( state,state - line, INFINI, 0));
				
				// Si je ne suis pas sur le bord bas de l'image alors je peux alors l'arc de (i,j) -> (i-1,j-1)
				if (line != i && 1 != j)
					g.addEdge(new Edge( state, state - line + 1, INFINI, 0));
			}
		}
		
		return g;		
	}

	/**
	 * Find the next edge to take, by choosing the one with :<br>
	 * <li>the greatest capacity, if it's an edge from <b>current</b><br>
	 * <li>the most used capacity, if it's an edge to <b>current</b><br>
	 * <li>any other possible edge otherwise<br>
	 * <br>
	 * Can return <i>null</i> if there is no edge available.<br>
	 * <br>
	 * @param current : The current vertice
	 * @param visited : The table of the visited vertices, or currently in the constructed path.
	 * @param list : The list of edges adjacent to the current vertice.
	 * @return The next edge of the path
	 */
	private Edge findNextNonFullEdge (int current, boolean visited[], ArrayList<Edge> list) {
		Edge max = null;		// Index of the element that has the max augment value
		int val = 0; 			// Max augment value
		
		visited[current] = true;
		
		for (int i = 1 ; i < list.size() ; i++) {
			Edge e = list.get(i);
			
			if (e.from == current) { // Edge that has current as its origin
				if (!visited[e.to]) { // Destination already visited ?
					int augmentValue;
					if (INFINI != e.capacity &&	((augmentValue = (e.capacity - e.used)) > 0)) { // Edge not full
						if (val < augmentValue) {
							max = e;
							val = augmentValue;
						}
					}
				}
			}
			else { // Other types of edge
				if (!visited[e.from])
					if (val < e.used) { // Is there something to take off ?
						max = e;
						val = e.used;
					}
			}
		}
		
		if (null == max) {
			Optional<Edge> tmp = list	.stream()
										.filter(e -> (e.from == current && !visited[e.to] && INFINI == e.capacity))
										.findFirst();
			
			return tmp.orElse(null);
		}
			
		return max;
	}
	
	/**
	 * @param source One vertice.
	 * @param other The other vertice.
	 * @return Returns the edge which links source and other.<br>
	 * Returns <i>null</i> if the edge doesn't exist.
	 */
	private Edge findEdge (int source, int other) {
		Iterable<Edge> tmp = adjacent(source);
		
		for (Edge edge : tmp) {
			if (edge.from == other || edge.to == other) {
				return edge;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds a path from the source to the target, by taking the largest edge available.
	 * @return The list of edges of the path.
	 */
	private ArrayList<Edge> findPath () {
		ArrayList<Edge> path = new ArrayList<>();
		int vertMax = vertices();
//		boolean visited[] = new boolean [vertMax]; // Init: false
		int father[] = new int[vertMax];
		int next;
		
		Queue<Integer> queue = new PriorityQueue<>();
//		Edge nextEdge = null;
//		int nextVertice;
		int i;
		
		// Init
		for (i = 0 ; i < vertMax ; i++) {
			father[i] = -1;
		}
		
		// Source
		queue.add(SOURCE);
		
		while (father[TARGET] == -1 && false == queue.isEmpty()) {
			next = queue.poll();
//			visited[next] = true;
			Iterable<Edge> tmp = adjacent(next);
			
			for (Edge edge : tmp) {
				if (father[edge.to] == -1) { // Not visited
					queue.add(edge.to);
					father[edge.to] = next;
				}
			}
		}
		
		if (father[TARGET] != -1) {
			next = TARGET;
			
			while (next != SOURCE) {
				path.add(findEdge(next, father[next]));
			}
			
			return path;
		}
		return null; // No path found.
		
		// Choose first element
		/*
		ArrayList<Edge> list = new ArrayList<>();
		for (Edge edge : adjacent(SOURCE)) { // For each left vertice
			Edge to = adjacenyList.get(edge.to).stream().filter(x -> edge.to == x.from).findFirst().get();
			
			list.add(to);
		}
		
		Edge start = list.stream().reduce(list.get(0), Edge::max);
		if (start.capacity == start.used)
			return null; // No path available
		nextVertice = start.to;
		path.add(start);
		path.add(adjacenyList.get(SOURCE).stream().filter(e -> e.to == start.from).findFirst().get());
		
		for (int i = 0 ; i < vertMax && (null != (nextEdge = findNextNonFullEdge(nextVertice, visited, adjacenyList.get(nextVertice)))) && nextVertice != TARGET ; i++ , path.add(nextEdge));
		//	Find next edge :
		//		if [not null] and [next vertice is not the target],
		// 		then [add next edge in the path] and proceed.
		
		if (null == nextEdge) // No path available
			return null;
		return path;
		*/
	}
	
	/**
	 * Returns the next non infinite edge, which capacity is not infinite.
	 * @param adjacent : an ArrayList of adjacent edges.
	 * @return A finite Edge.
	 */
	private Edge nextFiniteEdge (ArrayList<Edge> adjacent) {
		return adjacent.stream().filter(e -> e.capacity != INFINI).findFirst().get();
	}
	
	/**
	 *  
	 * @param edge
	 * @return if the edge is full
	 */
	private boolean maxedEdge (Edge edge) {
		return edge.capacity == edge.used;
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentStreamValue () {
		return adjacenyList.get(TARGET).stream().map(e -> e.used).reduce(0, Integer::sum);
	}
	

	private void fillGraph () {
		ArrayList<Edge> path;
		
		while (null != (path = findPath())) { // While there is a positive path
			int min = INFINI;
			int last = path.get(0).from;
			Iterator<Edge> it = path.iterator();
			
			while (it.hasNext()) {
				Edge edge = it.next();
				int plusValue = Edge.getPlusValueEdge(last, edge);
				
				last = edge.other(last);
				
				if (min > plusValue)
					min = plusValue;
			}
			
			// Here, minimum get!
			last = path.get(0).from;
			
			it = path.iterator();
			while (it.hasNext()) {
				Edge edge = it.next();
				
				Edge.modEdge(last, edge, min);
				
				last = edge.other(last);
			}
		}
	}
	
	/**
	 * Returns a minimal cut of this graph.
	 * @return The list of Edges cut
	 */
	public ArrayList<Edge> minimalCut () {
		ArrayList<Edge> cut = new ArrayList <>();
		
		fillGraph();
		// When there is no path found, the minimal cut can be found.
		for (int i = 0 ; i < adjacenyList.get(SOURCE).size() ; i++) {
			ArrayList<Edge> tmp = adjacenyList.get(adjacenyList.get(SOURCE).get(i).to);
			Edge e;
			
			while (!maxedEdge (e = nextFiniteEdge(tmp))) {
				tmp = adjacenyList.get(e.other(e.from));
			}
			
			cut.add(e);
		}
		return cut;
	}

	/**
	 * Create a file of the graph.
	 * @param path the path of the file.
	 * @throws IOException throw an IOException
	 */
	public void writeFile(Path path) throws IOException {
		try(BufferedWriter writer = Files.newBufferedWriter(path);
			PrintWriter printer = new PrintWriter(writer)) {
			
			printer.println("digraph G{");
			
			for (Edge e : edges())
				printer.println(e.from + "->" + e.to + "[label=\"" + e.used + "/" + e.capacity + "\"];");
			
			printer.println("}");
		}
	}
}
