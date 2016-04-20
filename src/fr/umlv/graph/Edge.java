package fr.umlv.graph;

public class Edge {
	final int from;
	final int to;
	final int capacity;
	int used;
	
	public Edge(int from, int to, int capacity, int used) {
		this.from = from;
		this.to = to;
		this.capacity = capacity;
		this.used = used;
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getUsed() {
		return used;
	}
	
	public void setUsed(int used) {
		this.used = used;
	}
	
	public int other(int v) {
		return (from == v)? to: from;
	}
	
	public static Edge max (Edge x, Edge y) {
		return (((x.capacity - x.used) - (y.capacity - y.used)) > 0) ? x : y;
	}
}
