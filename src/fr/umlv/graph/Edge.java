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
	
	public static Edge max (Edge x, Edge y) {
		return (((x.capacity - x.used) - (y.capacity - y.used)) > 0) ? x : y;
	}
	
	/**
	 * Modify the used capacity of the <b>edge</b> by adding or subtracting <b>value</b> from it,
	 * whether the <b>current</b> vertice is the source of this edge.<br>
	 * <br>
	 * It is <u>added</u> when the current vertice is the source, <u>subtracted</u> otherwise.
	 * 
	 * @param current : The current vertice
	 * @param mod : The modified Edge
	 * @param value : The value applied
	 */
	public static void modEdge (int current, Edge mod, int value) {
		mod.setUsed(mod.getUsed() + ((mod.from == current) ? value : -value));
//		mod.used += value;
	}
	
	/**
	 * Returns the plus value of the <b>edge</b>.<br>
	 * <br>
	 * The plus value is the amount earnable using this edge.<br>
	 * It is calculated whether the <b>current</b> vertice is the source of this edge:<br>
	 * <li> the difference between the capacity and the used capacity, if it is,<br>
	 * <li> the used capacity otherwise.<br>
	 * <br>
	 * @param current : The current vertice
	 * @param edge : The edge who needs evaluation.
	 * @return The evaluated plus value of the edge.
	 */
	public static int getPlusValueEdge (int current, Edge edge) {
		return (edge.from == current) ? (edge.capacity - edge.used) : edge.used;
//		return edge.capacity - edge.used;
	}
}
