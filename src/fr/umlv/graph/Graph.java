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

	
	public Graph(int vertexCount) {
		TARGET = vertexCount - 1;
		adjacenyList = new ArrayList<>(vertexCount);
		
		for (int v = 0; v < vertexCount; v++)
			adjacenyList.add(new ArrayList<>());
	}
	
	public int vertices() {
		return adjacenyList.size();
	}
	
	public void addEdge(Edge edge) {
		adjacenyList.get(edge.from).add(edge);
		adjacenyList.get(edge.to).add(edge);
	}
	
	public Iterable<Edge> adjacent(int vertex) {
		return adjacenyList.get(vertex);
	}
	
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
	
	public static Graph toGraph (int [][] itr){
		int i, j;
		int line, col;
		
		line = itr.length;
		col = itr[0].length;
		Graph g = new Graph (line * col + 2); // on veut un sommet par pixel donc on calcule le nombre de pixel avec itr et on ajoute les deux sommets s et t
		for (i = 0; i < col; i++){
			g.addEdge(new Edge(SOURCE, i+1, INFINI , 0));
			g.addEdge(new Edge ((line - 1) * col + i + 1, g.TARGET, itr[(line - 1)][i], 0));
		}
		
		for (i = 1; i <= line; i++){
			for (j = 1; j <= col; j++){
				if (line != i)
					g.addEdge(new Edge(j, j + col, itr[i-1][j-1], 0));
				
				if (col != j && line != i)
					g.addEdge(new Edge( j, j + col + 1, INFINI, 0));
				
				if (1 == i)
					continue;
				
				g.addEdge(new Edge( j, j - col, INFINI, 0));
				
				if (col != j)
					g.addEdge(new Edge( j, j - col - 1, INFINI, 0));
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
	
	private boolean maxedEdge (Edge edge) {
		return edge.capacity == edge.used;
	}

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
