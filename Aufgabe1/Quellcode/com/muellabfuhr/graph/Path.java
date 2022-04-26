package com.muellabfuhr.graph;

import java.util.ArrayList;
import java.util.HashSet;

public class Path implements Comparable<Path>{
	private ArrayList<Edge> edgeList = new ArrayList<Edge>();
	private HashSet<Edge> edgeSet = new HashSet<Edge>();
	
	private ArrayList<Integer> vertexList = new ArrayList<Integer>();
	private HashSet<Integer> vertexSet = new HashSet<Integer>();
	
	private int startVertex = -1;
	private int endVertex = -1;
	
	public Path(int startVertex) {
		this.startVertex = startVertex;
		endVertex = startVertex;
		
		vertexList.add(startVertex);
		vertexSet.add(startVertex);
	}
	
	private Path(ArrayList<Edge> edgeList, HashSet<Edge> edgeSet, ArrayList<Integer> vertexList, HashSet<Integer> vertexSet, int startVertex, int endVertex) {
		this.edgeList.addAll(edgeList);
		this.edgeSet.addAll(edgeSet);
		
		this.vertexList.addAll(vertexList);
		this.vertexSet.addAll(vertexSet);
		
		this.startVertex = startVertex;
		this.endVertex = endVertex;
	}
	
	public void append(Edge edge) {
		edgeSet.add(edge);
		edgeList.add(edge);
		
		endVertex = edge.getOppositVertex(endVertex);
		
		vertexList.add(endVertex);
		vertexSet.add(endVertex);
	}
	
	public void insertCycle(Path path) {
		vertexSet.addAll(path.getVertexSet());
		edgeSet.addAll(path.getEdgeSet());
		
		int insertionPoint = -1;
		
		for(int n = 0; n < vertexList.size(); n++) {
			if(vertexList.get(n) == path.getStartVertex()) {
				insertionPoint = n;
			}
		}
		
		vertexList.remove(insertionPoint);
		vertexList.addAll(insertionPoint, path.getVertexList());
		edgeList.addAll(insertionPoint, path.getEdgeList());
		
		
	}
	
	public int getCost() {
			int cost = 0;
			for (Edge edge : edgeList) {
				cost += edge.getCost();
			}
			return cost;
	}
	
	@Override
	protected Path clone() {
		return new Path(edgeList, edgeSet, vertexList, vertexSet, startVertex, endVertex);
	}

	@Override
	public int compareTo(Path path) {
		return getCost() - path.getCost();
	}

	public int getStartVertex() {
		return startVertex;
	}
	
	public int getEndVertex() {
		return endVertex;
	}
	
	public ArrayList<Edge> getEdgeList() {
		return edgeList;
	}
	
	public HashSet<Edge> getEdgeSet() {
		return edgeSet;
	}
	
	public ArrayList<Integer> getVertexList() {
		return vertexList;
	}
	
	public HashSet<Integer> getVertexSet() {
		return vertexSet;
	}
	
	@Override
	public String toString() {
		String val = "";
		for(int v = 0; v < vertexList.size() - 1; v++) {
			val += vertexList.get(v);
			val += " -> "; 
		}
		
		val += vertexList.get(vertexList.size() - 1);
		
		val += "  , Gesamtlänge: " + getCost();
		
		return val;
	}
}
