package com.muellabfuhr.graph;

public class Edge {
	private int a;
	private int b;
	
	private int cost;
	
	public Edge(int a, int b, int cost) {
		this.a = a;
		this.b = b;
		this.cost = cost;
	}

	public int getOppositVertex(int vertex) {
		if(a == vertex) {
			return b;
		}
		else if(vertex == b){
			return a;
		}
		else {
			return -1;
		}
	}
	
	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public boolean equals(Object object) {
		Edge edge = (Edge) object;
		
		if(edge.getA() == a && edge.getB() == b && edge.getCost() == cost) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public Edge clone() {
		return new Edge(a, b, cost);
	}
	
	@Override
	public String toString() {
		String val = "Edge: " + a + " -> " + b + " Cost: " + cost;
		return val;
	}
}
