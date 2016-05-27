package fr.umlv.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
		int state;
		
		line = itr.length;
		col = itr[0].length;
		Graph g = new Graph (line * col + 2); // on veut un sommet par pixel donc on calcule le nombre de pixel avec itr et on ajoute les deux sommets s et t
		for (i = 0; i < line; i++){
			g.addEdge(new Edge(SOURCE, i+1, INFINI , 0));
			g.addEdge(new Edge (line * col - i, g.TARGET, itr[(line - 1) - i][(col - 1)], 0));
		}
		
		for (i = 1; i <= line; i++){
			for (j = 1; j <= col; j++){
				state = i + ((j-1) * line);
				// Si je ne suis pas sur le bord droit de l'image alors je peux alors l'arc de (i,j) -> (i,j+1)
				if (col != j)
					g.addEdge(new Edge(state, state + line, itr[i-1][j-1], 0));
				// Si je ne suis pas sur le bord gauche ni sur le bord bas de l'image alors je peux alors l'arc de (i,j) -> (i+1,j-1)
				if (1 != j && 1 != i)
					g.addEdge(new Edge( state, state - line + 1, INFINI, 0));
				
				// Si je ne suis pas sur le bord gauche de l'image alors je peux alors l'arc de (i,j) -> (i,j-1)
				if (1 != j)
					g.addEdge(new Edge( state,state - line, INFINI, 0));
				
				// Si je ne suis pas sur le bord bas de l'image alors je peux alors l'arc de (i,j) -> (i-1,j-1)
				if (line != i && 1 != j)
					g.addEdge(new Edge( state, state - line - 1, INFINI, 0));
			}
		}
		
		return g;		
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
	private LinkedList<Edge> findPath () {
		LinkedList<Edge> path = new LinkedList<>();
		int vertMax = vertices();
		boolean visited[] = new boolean [vertMax]; // Init: false
		int father[] = new int[vertMax];
		int next;
		
		Queue<Integer> queue = new PriorityQueue<>();
		int i;
		
		// Init
		for (i = 0 ; i < vertMax ; i++) {
			father[i] = -1;
		}
		
		// Source
		queue.add(SOURCE);
		
		while (father[TARGET] == -1 && false == queue.isEmpty()) {
			next = queue.poll();
			visited[next] = true;
			
			for (Edge edge : adjacent(next)) {
				if (edge.capacity != edge.used && !visited[edge.to] && father[edge.to] == -1) { // Not visited and edge not full
					queue.add(edge.to);
					visited[edge.to] = true;
					father[edge.to] = next;
				}
			}
		}
		
		if (father[TARGET] != -1) {
			next = TARGET;
			
			while (next != SOURCE) {
				path.addFirst(findEdge(next, father[next]));
				next = father[next];
			}
			
			return path;
		}
		return null; // No path found.
	}
	
	/**
	 * Returns the next non infinite edge, which capacity is not infinite.
	 * @param adjacent : an ArrayList of adjacent edges.
	 * @return A finite Edge.
	 */
	private Edge nextFiniteEdge (int current) {
		Optional<Edge> edge = adjacenyList.get(current).stream().filter(e -> e.capacity != INFINI && e.from == current).findFirst();
		
		if (edge.isPresent())
			return edge.get(); // Finite Edge
		return adjacenyList.get(current).stream().filter(e -> e.used != 0 && e.to == current).findFirst().get(); // Edge taken backwards
	}
	
	void fillGraph () {
		LinkedList<Edge> path;
		
		while (null != (path = findPath())) { // While there is a positive path
			int min = INFINI;
			int last = path.get(0).from;
			Iterator<Edge> it = path.iterator();
			
//			path.forEach(e -> System.out.println(e));
			
			while (it.hasNext()) {
				Edge edge = it.next();
				int plusValue = Edge.getPlusValueEdge(last, edge);
//				System.out.println(edge + "Last = " + last +"/PV = " + plusValue);
				last = edge.other(last);
				
				if (min > plusValue)
					min = plusValue;
			}
//			System.out.println(path);
			// Here, minimum get!
			last = path.get(0).from;
			
//			System.out.println("MIN = " + min);
			
			it = path.iterator();
			while (it.hasNext()) {
				Edge edge = it.next();
				
				Edge.modEdge(last, edge, min);
				
				last = edge.other(last);
			}
			
//			path.forEach(e -> System.out.println(e));
		}
	}
	
	/**
	 * Returns a minimal cut of this graph.
	 * @return The list of Edges cut
	 */
	public ArrayList<Edge> minimalCut () {
		ArrayList<Edge> cut = new ArrayList <>();
		boolean visited[] = new boolean[vertices()];
		Queue<Integer> queue = new PriorityQueue<>();
		int verticetmp;
		
		fillGraph();
		System.out.println("Flot max: " + adjacenyList.get(TARGET).stream().map(e -> e.used).reduce(0, Integer::sum));
		System.out.println("Graph filled");
		// When there is no path found, the minimal cut can be found.
		
		queue.add(SOURCE);
		visited[SOURCE] = true;
		while (!queue.isEmpty()) {
			verticetmp = queue.poll();
			for (Edge edge : adjacenyList.get(verticetmp)) {
				if (!edge.isMaxed() && edge.from == verticetmp && !visited[edge.to]) {
				/* From a given vertice and an edge, add the next vertice if :
				 * the edge is not full,
				 * the next vertice is not visited
				 * the source of the edge is the current vertice 
				 */
					visited[edge.to] = true;
					queue.add(edge.to);
				}
			}
		}
		
//		if (visited[TARGET] == true) System.out.println("error2");
		
		for (int i = 0 ; i < adjacenyList.get(SOURCE).size() ; i++) {
			Edge e;
			int current = i+1;
			
//			tmp.forEach(x -> System.out.println(x));
			while (!(e = nextFiniteEdge(current)).isMaxed() || visited[e.to]) { // Arc de S (visité) à T (non visité) plein : arc à ajouter
				current = e.other(e.from);
//				if (current == TARGET) System.out.println("error");
//				System.out.println("Test :" + current);
				
			}
			
			cut.add(e);
//			System.out.println("i = " + i);
		}

		
		System.out.println("Cut finished");
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
