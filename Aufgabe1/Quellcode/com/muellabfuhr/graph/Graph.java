package com.muellabfuhr.graph;

import java.awt.PrintGraphics;
import java.awt.geom.FlatteningPathIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.muellabfuhr.graph.matching.Blossom;

public class Graph {
	private int vertexCount;
	private int edgeCount;
	
	private HashSet<Integer> vertexSet = new HashSet<>();
	private HashSet<Edge> edgeSet = new HashSet<Edge>();
	private HashMap<Integer, HashSet<Edge>> adjacencyList = new HashMap<Integer, HashSet<Edge>>(); 
	private HashMap<Integer, HashMap<Integer, Edge>> adjacencyMap = new HashMap<Integer, HashMap<Integer, Edge>>();
	
	public Graph(File data) throws IOException {
		FileReader fileReader = new FileReader(data);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line = bufferedReader.readLine();
		String[] lineParts = line.split(" ");
		
		vertexCount = Integer.parseInt(lineParts[0]);
		edgeCount = Integer.parseInt(lineParts[1]);

		for(int i = 0; i < vertexCount; i++) {
			adjacencyList.put(i, new HashSet<Edge>());
			adjacencyMap.put(i, new HashMap<Integer, Edge>());
			vertexSet.add(i);
		}
		
		line = bufferedReader.readLine();
		
		while(line != null) {
			lineParts = line.split(" ");
			
			int a = Integer.parseInt(lineParts[0]);
			int b = Integer.parseInt(lineParts[1]);
			int cost = Integer.parseInt(lineParts[2]);
			
			Edge edge = new Edge(a, b, cost);
			
			edgeSet.add(edge);
			
			adjacencyList.get(a).add(edge);
			adjacencyList.get(b).add(edge);
			
			adjacencyMap.get(a).put(b, edge);
			adjacencyMap.get(b).put(a, edge);
			
			line = bufferedReader.readLine();
		}
		
		bufferedReader.close();
	}
	
	public Graph(ArrayList<Edge> edgeSet) {	
		edgeCount = 0;
		vertexCount = 0;
		
		for(Edge edge : edgeSet) {
			edgeCount++;
			this.edgeSet.add(edge);
			
			if(adjacencyList.containsKey(edge.getA())) {
				adjacencyList.get(edge.getA()).add(edge);
				adjacencyMap.get(edge.getA()).put(edge.getB(), edge);
			}
			else {
				vertexSet.add(edge.getA());
				vertexCount++;
				adjacencyList.put(edge.getA(), new HashSet<Edge>());
				adjacencyMap.put(edge.getA(), new HashMap<Integer, Edge>());
				adjacencyList.get(edge.getA()).add(edge);
				adjacencyMap.get(edge.getA()).put(edge.getB(), edge);
			}
			
			if(adjacencyList.containsKey(edge.getB())) {
				adjacencyList.get(edge.getB()).add(edge);
				adjacencyMap.get(edge.getB()).put(edge.getA(), edge);
			}
			else {
				vertexSet.add(edge.getB());
				vertexCount++;
				adjacencyList.put(edge.getB(), new HashSet<Edge>());
				adjacencyMap.put(edge.getB(), new HashMap<Integer, Edge>());
				adjacencyList.get(edge.getB()).add(edge);
				adjacencyMap.get(edge.getB()).put(edge.getA(), edge);
			}
		}
	} 
	
	public Graph(HashSet<Edge> edgeSet) {	
		edgeCount = 0;
		vertexCount = 0;
		
		for(Edge edge : edgeSet) {
			edgeCount++;
			this.edgeSet.add(edge);
			
			if(adjacencyList.containsKey(edge.getA())) {
				adjacencyList.get(edge.getA()).add(edge);
				adjacencyMap.get(edge.getA()).put(edge.getB(), edge);
			}
			else {
				vertexSet.add(edge.getA());
				vertexCount++;
				adjacencyList.put(edge.getA(), new HashSet<Edge>());
				adjacencyMap.put(edge.getA(), new HashMap<Integer, Edge>());
				adjacencyList.get(edge.getA()).add(edge);
				adjacencyMap.get(edge.getA()).put(edge.getB(), edge);
			}
			
			if(adjacencyList.containsKey(edge.getB())) {
				adjacencyList.get(edge.getB()).add(edge);
				adjacencyMap.get(edge.getB()).put(edge.getA(), edge);
			}
			else {
				vertexSet.add(edge.getB());
				vertexCount++;
				adjacencyList.put(edge.getB(), new HashSet<Edge>());
				adjacencyMap.put(edge.getB(), new HashMap<Integer, Edge>());
				adjacencyList.get(edge.getB()).add(edge);
				adjacencyMap.get(edge.getB()).put(edge.getA(), edge);
			}
		}
	} 
	
	public HashMap<Integer, HashMap<Integer, Path>> getShortestPaths() {
		HashMap<Integer, HashMap<Integer, Path>> shortestPaths = new HashMap<Integer, HashMap<Integer, Path>>();
		
		for(Integer v : vertexSet) {
			shortestPaths.put(v, getShortestPaths(v));
		}
		
		return shortestPaths;
	}
	
	public HashMap<Integer, Path> getShortestPaths(int startingVertex) {
		HashSet<Integer> visitedVertecies = new HashSet<Integer>();
		PriorityQueue<Path> paths = new PriorityQueue<Path>();
		HashMap<Integer, Path> shortestPaths = new HashMap<Integer, Path>();
		
		for(Edge edge : adjacencyList.get(startingVertex)) {
			Path newPath = new Path(startingVertex);
			newPath.append(edge);
			
			paths.add(newPath);
		}
		
		visitedVertecies.add(startingVertex);
		
		while(paths.peek() != null) {
			Path bestPath = paths.poll();
		
			if(visitedVertecies.contains(bestPath.getEndVertex())){
				continue;
			}
			else {
				shortestPaths.put(bestPath.getEndVertex(), bestPath);
				
				visitedVertecies.add(bestPath.getEndVertex());
				
				for(Edge edge : adjacencyList.get(bestPath.getEndVertex())) {
					Path newPath = bestPath.clone();
					newPath.append(edge);
					
					paths.offer(newPath);
				}
			}
		}
		
		shortestPaths.put(startingVertex, new Path(startingVertex));
		
		return shortestPaths;
	}
	
	public Path getPostmanTour() {
		/*
		 * Find critical vertices 
		 * (vertices with an uneven degree)
		 */
		ArrayList<Integer> criticalVertices = new ArrayList<Integer>();
		
		for(Integer v : vertexSet) {
			if(adjacencyList.get(v).size() % 2 != 0) {
				criticalVertices.add(v);
			}
		}
		
		/*
		 * Extend graph to make an eulerian Graph
		 */
		if(!criticalVertices.isEmpty()) {
			/*
			 * Calculate edges for matching graph 
			 */
			HashMap<Integer, HashMap<Integer, Path>> shortestPaths = getShortestPaths();
			
			ArrayList<Edge> matchingEdges = new ArrayList<Edge>();
			HashMap<Edge, Path> edgePathMap = new HashMap<Edge, Path>();
			
			for(int i1 = 0; i1 < criticalVertices.size(); i1++) {
				for(Integer i2 = i1 + 1; i2 < criticalVertices.size(); i2++) {
					int v1 = criticalVertices.get(i1);
					int v2 = criticalVertices.get(i2);
					
					Edge matchingEdge = new Edge(v1, v2, shortestPaths.get(v1).get(v2).getCost());
					
					matchingEdges.add(matchingEdge);
					edgePathMap.put(matchingEdge, shortestPaths.get(v1).get(v2));
				}
			}
			
			/*
			 * Calculate minimal cost perfect matching
			 */
			Graph matchingGraph = new Graph(matchingEdges);
			HashSet<Edge> perfectMatching = matchingGraph.getMinimalCostPerfectMatching();
			
			/*
			 * Supplement graph with edges on paths corresponding to matching edges in the matching.
			 */
			for(Edge matchingEdge : perfectMatching) {
				for(Edge pathEdge : edgePathMap.get(matchingEdge).getEdgeSet()) {
					Edge supplementEdge = new Edge(pathEdge.getA(), pathEdge.getB(), pathEdge.getCost());
					edgeSet.add(supplementEdge);
					adjacencyList.get(supplementEdge.getA()).add(supplementEdge);
					adjacencyList.get(supplementEdge.getB()).add(supplementEdge);
					edgeCount++;
				}
			}
		}
		
		/*
		 * Calculate euler Tour
		 */
		HashSet<Edge> coverdEdges = new HashSet<Edge>();
		
		Path eulerTour = null;

		while(!coverdEdges.containsAll(edgeSet)) {
			if(eulerTour == null) {
				int base = 0;
				int currentVertex = base;
			
				Path subCycle = new Path(currentVertex);
				
				do {
					for(Edge edge : adjacencyList.get(currentVertex)){
						if(!coverdEdges.contains(edge)) {
							subCycle.append(edge);
							coverdEdges.add(edge);
							currentVertex = edge.getOppositVertex(currentVertex);
							break;
						}
					}
				}
				while(currentVertex != base);
				
				eulerTour = subCycle;
			}
			else {
				int base = -1;
				
				for(Integer vertex : eulerTour.getVertexSet()) {
					for(Edge edge : adjacencyList.get(vertex)) {
						if(!coverdEdges.contains(edge)) {
							base = vertex;
						}
					}
				}
				
				int currentVertex = base;
				
				Path subCycle = new Path(base);
				
				do {
					for(Edge edge : adjacencyList.get(currentVertex)){
						if(!coverdEdges.contains(edge)) {
							subCycle.append(edge);
							coverdEdges.add(edge);
							currentVertex = edge.getOppositVertex(currentVertex);
							break;
						}
					}
				}
				while(currentVertex != base);
				
				eulerTour.insertCycle(subCycle);
			}
		}
		
		return eulerTour;
	}
	
	public HashSet<Edge> getMinimalCostPerfectMatching(){
		/*
		 * This method is based on Galil's paper, released March 1986,
		 * "Efficient Algorithms for Finding Maximum Matching in Graphs"
		 * its recommended to read said paper before reading this method.
		 */
		
		/*
		 * Invert edge cost 
		 */
		int highestCost = -1;
		
		for(Edge edge : edgeSet) {
			if(highestCost == -1) {
				highestCost = edge.getCost();
			}
			else if(highestCost < edge.getCost()) {
				highestCost = edge.getCost();
			}
		}
		highestCost++;
		
		for(Edge edge : edgeSet) {
			edge.setCost(highestCost - edge.getCost());
		}
		
		
		HashSet<Edge> matchingEdges = new HashSet<Edge>();
		
		HashMap<Integer, Double> dualVar = new HashMap<Integer, Double>();
		
		/*
		 * Assign dual variable to v (u(v))
		 */		
		for(Integer v : vertexSet) {
			double maxEdgeCost = 0;
			boolean maxEdgeCostFound = false;
			for(Edge edge : adjacencyList.get(v)) {
				if(!maxEdgeCostFound) {
					maxEdgeCost = edge.getCost();
					maxEdgeCostFound = true;
				}
			    else if(edge.getCost() > maxEdgeCost) {
					maxEdgeCost = edge.getCost();
				}
			}
			
			dualVar.put(v, maxEdgeCost / 2.0);
		}
		
		/*
		 * Create initial surface graph and link it to the base graph 
		 * (make a blossom for each vertex containing only this vertex)
		 * (blossoms like this (containing only one vertex) are referred to as trivial)
		 */
		HashSet<Blossom> surfaceGraph = new HashSet<Blossom>();
		HashMap<Integer, Blossom> associationMap = new HashMap<Integer, Blossom>(); 
		
		for(Integer v : vertexSet) {
			Blossom b = new Blossom(v, adjacencyList.get(v));
			surfaceGraph.add(b);
			associationMap.put(v, b);
		}
		
		while(true) {
			/*
			 * Reset labels
			 */
			for(Blossom b : surfaceGraph) {
				b.resetLabel();
			}
			
			/*
			 * Find all single blossoms
			 * Label them by 'S' and insert them into Q
			 */
			Queue<Blossom> q = new LinkedList<Blossom>();
			
			for(Blossom b : surfaceGraph) {
				boolean isMatched = false;
				
				for(Edge outerEdge : b.getOuterEdges()) {
					if(matchingEdges.contains(outerEdge)) {
						isMatched = true;
						break;
					}
				}
				
				if(!isMatched) {
					b.setLabel('S');
					q.offer(b);
				}
			}
			
			boolean hasBeenAugmented = false;
			
			while(!hasBeenAugmented) {
				while(!q.isEmpty() && !hasBeenAugmented) {
					Blossom b = q.poll();
					if(b.onSurface()) {
						for(Edge edge : b.getOuterEdges()) {
							if(getSlack(edge, dualVar) == 0) {
								Blossom d = associationMap.get(b.getOppositVertex(edge));
								
								if(d.getLabel() == '/') {
									/*
									 * Case C1
									 * Apply R12
									 */
									d.setLabel('T', b);
									
									Edge matchingEdge = null;
									for(Edge outerEdge : d.getOuterEdges()) {
										if(matchingEdges.contains(outerEdge)) {
											matchingEdge = outerEdge;
											break;
										}
									}
									
									Blossom dSpouse = associationMap.get(d.getOppositVertex(matchingEdge));
									
									int debug = d.getOppositVertex(matchingEdge);
									
									dSpouse.setLabel('S', d);
									q.offer(dSpouse);
								}
								else if(d.getLabel() == 'S') {
									/*
									 * Case C2
									 * Use the labels to construct a path from the origin of b's label to the origin of d's label
									 */
									ArrayList<Blossom> pathBlossoms = new ArrayList<Blossom>();
									
									pathBlossoms.add(b);
									Blossom bOrigin = b;
									
									while(bOrigin.getLabelOrigin() != null) {
										bOrigin = bOrigin.getLabelOrigin();
										if(bOrigin.onSurface()) {
											pathBlossoms.add(0, bOrigin);
										}
										else if(pathBlossoms.get(0) != bOrigin.getParent()){
											pathBlossoms.add(0, bOrigin.getParent());
										}

									}
									
									pathBlossoms.add(d);
									Blossom dOrigin = d;
									
									while (dOrigin.getLabelOrigin() != null) {
										dOrigin = dOrigin.getLabelOrigin();
										if(dOrigin.onSurface()) {
											pathBlossoms.add(dOrigin);
										}
										else if(pathBlossoms.get(pathBlossoms.size() - 1) != dOrigin.getParent()){
											pathBlossoms.add(dOrigin.getParent());
										}
									}
									
									
									if(pathBlossoms.get(pathBlossoms.size() - 1) != pathBlossoms.get(0)) {
										/*
										 * Augmenting path has been discovered!
										 * Augment the matching 
										 */
										for(int i = 0; i < pathBlossoms.size() - 1; i++) {
											Blossom curr = pathBlossoms.get(i);
											Blossom next = pathBlossoms.get(i + 1);
											
											Edge connectingEdge = curr.getEdgeTo(next, matchingEdges);
											
											if(matchingEdges.contains(connectingEdge)) {
												matchingEdges.remove(connectingEdge);
											}
											else {
												matchingEdges.add(connectingEdge);
												curr.augment(connectingEdge, matchingEdges);
												next.augment(connectingEdge, matchingEdges);
											}
										}
										
										hasBeenAugmented = true;
										break;
									}
									else {
										/*
										 * Uneven alternating cycle has been discovered!
										 * Make a new blossom
										 */
										
										/*
										 * Cut of blossom stem
										 */
										Blossom labelOrigin = null;
										Blossom base = null;
										
										while(pathBlossoms.get(0) == pathBlossoms.get(pathBlossoms.size() - 1)) {
											labelOrigin = base;
											base = pathBlossoms.get(0);
											
											pathBlossoms.remove(pathBlossoms.size() - 1);
											pathBlossoms.remove(0);
										}
										
										/*
										 * Make the new blossom and label it by 'S'
										 */
										Blossom newBlossom = new Blossom(base, pathBlossoms);
										
										newBlossom.setLabel('S', labelOrigin);
										q.offer(newBlossom);
										
										surfaceGraph.removeAll(newBlossom.getSubblossoms());
										surfaceGraph.add(newBlossom);
										
										for(Integer vertex : newBlossom.getVertices()) {
											associationMap.put(vertex, newBlossom);
										}
										break;
									}
								}
							}
						}
					}
				}
				
				if(!hasBeenAugmented) {
					/*
					 * Calculate Delta Values
					 */
					
					/*
					 * Delta 1 (minimum dual variable of a S-Vertex)
					 */
					boolean delta1Found = false;
					double delta1 = -1;
					
					for(Integer vertex : vertexSet) {
						if(associationMap.get(vertex).getLabel() == 'S') {
							double vertexDualVar = dualVar.get(vertex);
							
							if(!delta1Found) {
								delta1 = vertexDualVar;
								delta1Found = true;
							}
							else if(vertexDualVar < delta1){
								delta1 = vertexDualVar;
							}
						}
					}
					
					/*
					 * Delta 2 (minimum slack on an edge between an S-Vertex and an unlabeled vertex)
					 */
					boolean delta2Found = false;
					double delta2 = -1;
					HashSet<Blossom> blossomsToRevisitD2 = new HashSet<Blossom>(); 
					
					for(Integer vertex : vertexSet) {
						if(associationMap.get(vertex).getLabel() == 'S') {
							for(Edge edge : adjacencyList.get(vertex)) {
								if(associationMap.get(edge.getOppositVertex(vertex)).getLabel() == '/') {
									if(!delta2Found) {
										delta2 = getSlack(edge, dualVar);
										blossomsToRevisitD2.add(associationMap.get(vertex));
										delta2Found = true;
									}
									else if(getSlack(edge, dualVar) == delta2){
										blossomsToRevisitD2.add(associationMap.get(vertex));
									}
									else if (getSlack(edge, dualVar) < delta2) {
										blossomsToRevisitD2 = new HashSet<Blossom>();
										blossomsToRevisitD2.add(associationMap.get(vertex));
										delta2 = getSlack(edge, dualVar);
									}
								}
							}
						}
					}
					
					/*
					 * Delta 3 (minimum slack on an edge between two surface S-Blossoms)
					 */
					boolean delta3Found = false;
					double delta3 = -1;
					HashSet<Blossom> blossomsToRevisitD3 = new HashSet<Blossom>(); 
					
					for(Blossom blossom : surfaceGraph) {
						if(blossom.getLabel() == 'S') {
							for(Edge edge : blossom.getOuterEdges()) {
								if(associationMap.get(blossom.getOppositVertex(edge)).getLabel() == 'S') {
									if(!delta3Found) {
										delta3 = getSlack(edge, dualVar) / 2.0;
										blossomsToRevisitD3.add(blossom);
										delta3Found = true;
									}
									else if(getSlack(edge, dualVar) / 2.0 == delta3){
										blossomsToRevisitD3.add(blossom);
									}
									else if (getSlack(edge, dualVar) / 2.0 < delta3) {
										delta3 = getSlack(edge, dualVar) / 2.0;
										blossomsToRevisitD3 = new HashSet<Blossom>();
										blossomsToRevisitD3.add(blossom);
									}
								}
							}
						}
					}
					
					/*
					 * Delta 4 (minimum dual variable of a (nontrivial) T-Blossom)
					 */
					boolean delta4Found = false;
					double delta4 = -1;
					HashSet<Blossom> blossomsToRevisitD4 = new HashSet<Blossom>(); 
					
					for(Blossom blossom : surfaceGraph) {
						if(!blossom.getSubblossoms().isEmpty() && blossom.getLabel() == 'T') {
							if(!delta4Found) {
								delta4 = blossom.getDualVar();
								blossomsToRevisitD4.add(blossom);
								delta4Found = true;
							}
							else if (blossom.getDualVar() == delta4) {
								blossomsToRevisitD4.add(blossom);
							}
							else if (blossom.getDualVar() < delta4) {
								delta4 = blossom.getDualVar();
								blossomsToRevisitD4 = new HashSet<Blossom>();
								blossomsToRevisitD4.add(blossom);
							}
						}
					}
					
					
					
					/*
					 * Choose lowest delta 
					 */		
					double delta = -1;
					
					if((!delta2Found || (delta2Found && delta1 < delta2)) && (!delta3Found || (delta3Found && delta1 < delta3)) && (!delta4Found || (delta4Found && delta1 < delta4))) {
						/*
						 * Delta 1
						 * Optimum has been reached
						 */
						System.out.println("Delta 1 choosen");
						HashSet<Integer> matchedVertices = new HashSet<Integer>();
						for(Edge matchingEdge : matchingEdges) {
							matchedVertices.add(matchingEdge.getA());
							matchedVertices.add(matchingEdge.getB());
						}
						
						for(Integer v : vertexSet) {
							if(!matchedVertices.contains(v)) {
								for(Integer w : vertexSet) {
									if(!matchedVertices.contains(w) && v != w) {
										matchingEdges.add(adjacencyMap.get(v).get(w));
										matchedVertices.add(w);
										matchedVertices.add(v);
									}
								}
							}
						}
						
						break;
					}
					 if(delta2Found && delta2 <= delta1 && (!delta3Found || (delta3Found && delta2 <= delta3)) && (!delta4Found || (delta4Found && delta2 <= delta4))) {
						/*
						 * Delta 2
						 */
						System.out.println("Delta 2 choosen");
						delta = delta2;
						for(Blossom blossom : blossomsToRevisitD2) {
							q.offer(blossom);
						}
					}
					else if(delta3Found && delta3 <= delta1 && (!delta2Found || (delta2Found && delta3 <= delta2)) && !delta4Found || (delta4Found && delta3 <= delta4)) {
						/*
						 * Delta 3
						 */
						System.out.println("Delta 3 choosen");
						delta = delta3;
						for(Blossom blossom : blossomsToRevisitD3) {
							q.offer(blossom);
						}
					}
					else if (delta4Found && delta4 <= delta1 && (!delta2Found || (delta2Found && delta4 <= delta2)) && (!delta3Found || (delta3Found && delta4 <= delta3))) {
						/*
						 * Delta 4
						 */
						System.out.println("Delta 4 choosen");
						delta = delta4;
						
						for(Blossom blossom : blossomsToRevisitD4) {
							expandTBlossom(blossom, q, matchingEdges, surfaceGraph, associationMap);
						}
					}
					else {
						break;
					}
					
					for(Integer vertex : vertexSet) {
						double dual = dualVar.get(vertex);
						
						if(associationMap.get(vertex).getLabel() == 'S') {
							dual -= delta; 
						}
						else if (associationMap.get(vertex).getLabel() == 'T') {
							dual += delta;
						}
						
						dualVar.put(vertex, dual);
					}
					
					for(Blossom blossom : surfaceGraph) {
						if(!blossom.getSubblossoms().isEmpty()) {
							double dual = blossom.getDualVar();
							
							if(blossom.getLabel() == 'S') {
								dual += 2.0 * delta;
							}
							else if(blossom.getLabel() == 'T') {
								dual -= 2.0 * delta; 
							}
							
							blossom.setDualVar(dual);
						}
					}
				}
			}
			
			if(!hasBeenAugmented) {
				break;
			}
			
			HashSet<Blossom> newSurfaceGraph = new HashSet<Blossom>();
			newSurfaceGraph.addAll(surfaceGraph);
			for(Blossom blossom : surfaceGraph) {
				if(!blossom.getSubblossoms().isEmpty() && blossom.getLabel() == 'S' && blossom.getDualVar() == 0) {
					expandSBlossom(blossom, newSurfaceGraph, associationMap);
				}
			}
			surfaceGraph = newSurfaceGraph;
		}
		return matchingEdges;
	}
	
	public void expandTBlossom(Blossom blossom, Queue<Blossom> q, HashSet<Edge> matchingEdges, HashSet<Blossom> surfaceGraph, HashMap<Integer, Blossom> associationMap) {
		/*
		 * Label the inner path
		 */
		Edge labelOriginEdge = blossom.getEdgeTo(blossom.getLabelOrigin(), matchingEdges);
		
		Blossom newPathStart = blossom.getConnectedSubblossom(labelOriginEdge);
		ArrayList<Blossom> innerPath = new ArrayList<Blossom>();
		innerPath.addAll(blossom.getInnerPath());
		
		/*
		 * Determine which way to go around the blossom  
		*/
		int newPathStartIndex = innerPath.indexOf(newPathStart);
		
		int direction;
		int endpoint;
		
		if(newPathStartIndex % 2 == 0) {
			/*
			 * Move right
			 */
			direction = 1;
			innerPath.add(blossom.getBase());
			endpoint = innerPath.size() - 1;
		}
		else {
			/*
			 * Move left
			 */
			direction = -1;
			innerPath.add(0, blossom.getBase());
			endpoint = 0;
		}
		
		/*
		 * Label new path
		 */
		newPathStart.setLabel('T', blossom.getLabelOrigin());
		
		for(int i = innerPath.indexOf(newPathStart); i != endpoint; i += direction) {
			Blossom curr = innerPath.get(i);
			Blossom next = innerPath.get(i += direction);
			
			if(curr.getLabel() == 'T') {
				next.setLabel('S', curr);
				q.offer(next);
			}
			else if(curr.getLabel() == 'S') {
				next.setLabel('T', curr);
			}
		}
		
		Edge matchingEdge = null;
		for(Edge outerEdge : blossom.getOuterEdges()) {
			if(matchingEdges.contains(outerEdge)) {
				matchingEdge = outerEdge;
				break;
			}
		}
		
		Blossom spouse = associationMap.get(blossom.getOppositVertex(matchingEdge));
		
		spouse.setLabel('S', blossom.getBase());
		
		surfaceGraph.remove(blossom);
		for(Blossom subblossom : blossom.getSubblossoms()) {
			for(Integer vertex : subblossom.getVertices()) {
				associationMap.put(vertex, subblossom);
			}
			surfaceGraph.add(subblossom);
			subblossom.setOnSurface(true);
			subblossom.setParent(null);
		}
	}
	
	public void expandSBlossom(Blossom blossom, HashSet<Blossom> surfaceGraph, HashMap<Integer, Blossom> associationMap) {
		surfaceGraph.remove(blossom);
		for(Blossom subblossom : blossom.getSubblossoms()) {
			if(!subblossom.getSubblossoms().isEmpty() && subblossom.getDualVar() == 0) {
				expandSBlossom(subblossom, surfaceGraph, associationMap);
			}
			else {
				for(Integer vertex : subblossom.getVertices()) {
					associationMap.put(vertex, subblossom);
				}
				surfaceGraph.add(subblossom);
				subblossom.setOnSurface(true);
				subblossom.setParent(null);
			}
		}
	}
	
	private double getSlack(Edge edge, HashMap<Integer, Double> dualVar) {
		double slack = dualVar.get(edge.getA()) + dualVar.get(edge.getB()) - edge.getCost();
		return slack;
	}
	
	public int getEdgeCount() {
		return edgeCount;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public HashSet<Edge> getEdgeSet(){
		return edgeSet;
	}
	
	public HashSet<Integer> getVertexSet() {
		return vertexSet;
	}
	
	public HashMap<Integer, HashSet<Edge>> getAdjacencyList() {
		return adjacencyList;
	}
	
	public HashMap<Integer, HashMap<Integer, Edge>> getAdjacencyMap(){
		return adjacencyMap;
	}
}
