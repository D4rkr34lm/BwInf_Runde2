package com.muellabfuhr.graph.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.muellabfuhr.graph.Edge;

public class Blossom {
	private Blossom base;
	private ArrayList<Blossom> innerPath = new ArrayList<Blossom>(); 
	
	private HashSet<Blossom> subblossoms = new HashSet<Blossom>();
	private HashSet<Integer> vertices = new HashSet<Integer>();
	private HashSet<Edge> outerEdges = new HashSet<Edge>();

	private boolean onSurface = true;
	private Blossom parent = null;
	
	private char label = '/';
	private Blossom labelOrigin = null;
	
	private double dualVar = 0;
	
	public Blossom(int vertex, HashSet<Edge> edges) {
		vertices.add(vertex);
		outerEdges.addAll(edges);
	}
	
	public Blossom(Blossom base, ArrayList<Blossom> innerPath) {
		this.base = base;
		this.innerPath = innerPath;
		
		subblossoms.add(base);
		subblossoms.addAll(innerPath);
		
		HashSet<Edge> oldOuterEdges = new HashSet<Edge>();
		
		for(Blossom subblossom : subblossoms) {
			vertices.addAll(subblossom.getVertices());
			oldOuterEdges.addAll(subblossom.getOuterEdges());
			
			subblossom.setOnSurface(false);
			subblossom.setParent(this);
		}
		
		for(Edge oldOuterEdge : oldOuterEdges) {
			if(vertices.contains(oldOuterEdge.getA()) && vertices.contains(oldOuterEdge.getB())) {
				continue;
			}
			else {
				outerEdges.add(oldOuterEdge);
			}
		}
	}
	
	public void augment(Edge matchingEdge, HashSet<Edge> matchingEdges) {
		Blossom newBase = getConnectedSubblossom(matchingEdge);
		if(!subblossoms.isEmpty() && newBase != base) {
			
			/*
			 * Determine which way to go around the blossom
			 */
			int newBaseIndex = innerPath.indexOf(newBase);
			
			int startingPoint;
			int direction;
			
			if(newBaseIndex % 2 == 0) {
				/*
				 * Right
				 */
				direction = -1;
				startingPoint = innerPath.size() - 1;
			}
			else {
				/*
				 * Left
				 */
				direction = 1;
				startingPoint = 0;
			}
			
			/* 
			 * Augment the blossom going from the old base, to the new base using the determined direction
			 */
			Blossom curr = base;
			Blossom next = innerPath.get(startingPoint);
			
			Edge connectingEdge = curr.getEdgeTo(next, matchingEdges);
			
			if(matchingEdges.contains(connectingEdge)) {
				matchingEdges.remove(connectingEdge);
			}
			else {
				matchingEdges.add(connectingEdge);
				curr.augment(connectingEdge, matchingEdges);
				next.augment(connectingEdge, matchingEdges);	
			}
			
			for(int i = startingPoint; i != newBaseIndex; i += direction) {
				curr = innerPath.get(i);
				next = innerPath.get(i + direction);
				
				connectingEdge = curr.getEdgeTo(next, matchingEdges);
				
				if(matchingEdges.contains(connectingEdge)) {
					matchingEdges.remove(connectingEdge);
				}
				else {
					matchingEdges.add(connectingEdge);
					curr.augment(connectingEdge, matchingEdges);
					next.augment(connectingEdge, matchingEdges);	
				}
			}
			
			/*
			 * Adjust inner path
			 */
			
			do {
				innerPath.add(base);
				base = innerPath.get(0);
				innerPath.remove(0);
				
			}while (base != newBase);
			
			base.augment(matchingEdge, matchingEdges);
		}
		else if(!subblossoms.isEmpty()){
			base.augment(matchingEdge, matchingEdges);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		Blossom blossom = (Blossom) obj;
		
		if(blossom.getVertices().size() == vertices.size() && vertices.containsAll(blossom.getVertices())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Blossom getConnectedSubblossom(Edge edge) {
		if(outerEdges.contains(edge)) {
			if(subblossoms.isEmpty()) {
				return this;
			}
			else {
				for(Blossom subblossom : subblossoms) {
					if(subblossom.getOuterEdges().contains(edge)) {
						return subblossom;
					}
				}
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	public Edge getEdgeTo(Blossom b, HashSet<Edge> matchingEdges) {
		Edge ret = null;
		
		for(Edge e : b.getOuterEdges()) {
			if(outerEdges.contains(e)) {
				ret = e;
				
				if(matchingEdges.contains(e)) {
					return e;
				}
			}
		}
		
		return ret;
	}
	
	public char getLabel() {
		return label;
	}
	
	public Blossom getLabelOrigin() {
		return labelOrigin;
	}
	
	public void setLabel(char label) {
		this.label = label;
	}
	
	public void setLabel(char label, Blossom labelOrigin) {
		this.label = label;
		this.labelOrigin = labelOrigin;
	}
	
	public void resetLabel() {
		label = '/';
		labelOrigin = null;
		
		for(Blossom subblossom : subblossoms) {
			subblossom.resetLabel();
		}
	}
	
	public HashSet<Edge> getOuterEdges() {
		return outerEdges;
	}
	
	public ArrayList<Blossom> getInnerPath(){
		return innerPath;
	}
	
	public int getOppositVertex(Edge edge) {
		for(Integer v : vertices) {
			int opposingVertex = edge.getOppositVertex(v);
			if(opposingVertex != -1) {
				return opposingVertex;
			}
		}
		return -1;
	}
	
	public HashSet<Blossom> getSubblossoms() {
		return subblossoms;
	}
	
	public HashSet<Integer> getVertices(){
		return vertices;
	}
	
	public boolean onSurface() {
		return onSurface;
	}
	
	public void setOnSurface(boolean onSurface) {
		this.onSurface = onSurface;
	}
	
	public Blossom getBase() {
		return base;
	}
	
	public double getDualVar() {
		return dualVar;
	}
	
	public void setDualVar(double dualVar) {
		this.dualVar = dualVar;
	}
	
	public Blossom getParent() {
		if(parent.onSurface) {
			return parent;
		}
		else {
			return parent.getParent();
		}
	}
	
	public void setParent(Blossom parent) {
		this.parent = parent;
	}
}
