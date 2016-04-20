package fr.umlv.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Graph {
	private final ArrayList<ArrayList<Edge>> adjacenyList;
	
	public Graph(int vertexCount) {
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
