package fr.umlv.graph;

public class Edge {
	final int from;
	final int to;
	final int capacity;
	int used;
	
	/**
	 * The Constructor of the class Edge
	 * @param from the vertex where the edge depart
	 * @param to the vertex where the edge arrive
	 * @param capacity the capacity of the edge
	 * @param used the used from the capacity
	 */
	public Edge(int from, int to, int capacity, int used) {
		this.from = from;
		this.to = to;
		this.capacity = capacity;
		this.used = used;
	}
	
	/**
	 * 
	 * @return the vertex where the edge is from 
	 */
	public int getFrom() {
		return from;
	}
	
	/**
	 * 
	 * @return the vertex where the edge is to
	 */
	public int getTo() {
		return to;
	}
	
	/**
	 * 
	 * @return the capacity of the edge
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * 
	 * @return the used of the edge
	 */
	public int getUsed() {
		return used;
	}
	
	/**
	 * Set the used of the edge
	 * @param used the value you want to set to the used of the edge
	 */
	public void setUsed(int used) {
		this.used = used;
	}
	
	/**
	 * 
	 * @param v the indice of a vertex 
	 * @return the opposite vertex of the edge.
	 */
	public int other(int v) {
		return (from == v)? to: from;
	}
	
	@Override
	public String toString () {
		return this.from + "->" + this.to + "(" + this.used + "/" + this.capacity + ")"; 
	}
	
	@Override
	public boolean equals (Object o) {
		if (o instanceof Edge) {
			Edge e = (Edge) o;
			return this.capacity == e.capacity
				&& this.from == e.from
				&& this.to == e.to
				&& this.used == e.used;
		}
		return false;
	}
	
	public boolean isMaxed () {
		return capacity == used;
	}
	

	/**
	 * Compare the two edges in argument and return the edge who sill have the bigger capacity  <br>
	 * @param x the first edge
	 * @param y the second edge
	 * @return the edge who still has the bigger capacity
	 */

	public static Edge max (Edge x, Edge y) {
		return (((x.capacity - x.used) - (y.capacity - y.used)) > 0) ? x : y;
	}
	
	/**
	 * Modify the used capacity of the <b>edge</b> by adding or subtracting <b>value</b> from it,
	 * whether the <b>current</b> vertex is the source of this edge.<br>
	 * <br>
	 * It is <u>added</u> when the current vertex is the source, <u>subtracted</u> otherwise.
	 * 
	 * @param current : The current vertex
	 * @param mod : The modified Edge
	 * @param value : The value applied
	 */
	public static void modEdge (int current, Edge mod, int value) {
		mod.used += (mod.from == current) ? value : -value;
	}
	
	/**
	 * Returns the plus value of the <b>edge</b>.<br>
	 * <br>
	 * The plus value is the amount earnable using this edge.<br>
	 * It is calculated whether the <b>current</b> vertex is the source of this edge:<br>
	 * - the difference between the capacity and the used capacity, if it is,<br>
	 * - the used capacity otherwise.<br>
	 * <br>
	 * @param current : The current vertex
	 * @return The evaluated plus value of the edge.
	 */
	public int getPlusValueEdge (int current) {
		return (from == current) ? (capacity - used) : used;
	}
}
