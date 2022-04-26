package com.muellabfuhr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.muellabfuhr.graph.Edge;
import com.muellabfuhr.graph.Graph;
import com.muellabfuhr.graph.Path;

public class MuellabfuhrApplication extends JFrame{
	public static void main(String[] args) throws IOException {
		MuellabfuhrApplication app = new MuellabfuhrApplication();
	}
	
	public MuellabfuhrApplication() {
		setTitle("40. Bundeswettbewerb Informatik - Runde 2 - Aufgabe 1 Müllabfuhr");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1150, 600);
		setLayout(null);
		
		init();
		
		setVisible(true);
	}
	
	public void init() {
		JTextArea resultDisplay = new JTextArea();
		resultDisplay.setLocation(10, 80);
		resultDisplay.setSize(1100, 450);
		
		JScrollPane resultDisplayContainer = new JScrollPane(resultDisplay);
		resultDisplayContainer.setLocation(10, 80);
		resultDisplayContainer.setSize(1100, 450);
		add(resultDisplayContainer);
		
		for(int n = 0; n < 9; n++) {
			int index = n;
			
			JButton loadExampleButton = new JButton("Lade Beispiel " + Integer.toString(index));
			loadExampleButton.addActionListener(new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Graph graph = new Graph(new File("Data\\muellabfuhr" + index + ".txt"));
						
						String routes = new String();
						Path[] dayRoutes = getDayRoutes(graph);
						int longestPathLenght = -1;
						
						for(int i = 0; i < 5; i++) {
							if(dayRoutes[i] == null) {
								continue;
							}
							
							if(longestPathLenght == -1) {
								longestPathLenght = dayRoutes[i].getCost();
							}
							else if(longestPathLenght < dayRoutes[i].getCost()) {
								longestPathLenght = dayRoutes[i].getCost();
							}
							
							routes += "Tag " + Integer.toString(i + 1) + ": " + dayRoutes[i].toString() + "\n";
						}
						
						routes += "Maximale Lange einer Tagestour: " + Integer.toString(longestPathLenght);
						
						resultDisplay.setText(routes);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			loadExampleButton.setLocation(5 + 125 * n, 5);
			loadExampleButton.setSize(120, 25);
			add(loadExampleButton);
		}
		
		
	}

	public Path[] getDayRoutes(Graph graph) {
		/*
		 * Calculate shortest paths
		 */
		HashMap<Integer, HashMap<Integer, Path>> shortestPaths = graph.getShortestPaths();
		
		/* 
		 * Determine edge frequency 
		 */
		HashMap<Edge, Integer> edgeFrequency = new HashMap<Edge, Integer>();
		
		for(Edge candidate : graph.getEdgeSet()) {
			if(!edgeFrequency.containsKey(candidate)) {
				edgeFrequency.put(candidate, 0);
			}
			
			Path[] connectingPathes = new Path[2];
			connectingPathes[0] = shortestPaths.get(0).get(candidate.getA());
			connectingPathes[1] = shortestPaths.get(0).get(candidate.getB());
						
			for(int n = 0; n < 2; n++) {
				for(Edge edge : connectingPathes[n].getEdgeSet()) {
					if(edgeFrequency.containsKey(edge)) {
						edgeFrequency.put(edge, edgeFrequency.get(edge) + 1);
					}
					else {
						edgeFrequency.put(edge, 1);
					}
				}
			}
		}
		
		/*
		 * Determine required edges
		 */
		HashSet<Edge> requiredEdges = new HashSet<Edge>();
		
		for(Edge edge : graph.getEdgeSet()) {
			if(edgeFrequency.get(edge) <= 1) {
				requiredEdges.add(edge);
			}
		}		
		
		/*
		 * Determine representative edges
		 */
		HashSet<Edge> representativeEdges = new HashSet<Edge>();
		
		for(int n = 0; n < 5 && n < requiredEdges.size(); n++) {
			if(representativeEdges.isEmpty()) {
				Edge representativeEdge = null;
				for(Edge edge : requiredEdges) {
					if(representativeEdge == null) {
						representativeEdge = edge;
					}
					else if(getDistanceToDepot(representativeEdge, shortestPaths) < getDistanceToDepot(edge, shortestPaths)) {
						representativeEdge = edge;
					}
				}
				representativeEdges.add(representativeEdge);
			}
			else {
				Edge representativeEdge = null;
				for(Edge edge : requiredEdges) {
					if(!representativeEdges.contains(edge) && representativeEdge == null) {
						representativeEdge = edge;
					}
					else if(!representativeEdges.contains(edge) && getDistance(representativeEdge, representativeEdges, shortestPaths) < getDistance(edge, representativeEdges, shortestPaths)) {
						representativeEdge = edge;
					}
				}
				representativeEdges.add(representativeEdge);
			}
		}
		requiredEdges.removeAll(representativeEdges);
		
		/*
		 * Make clusters with required Edges
		 */
		HashMap<Edge, HashSet<Edge>> clusters = new HashMap<Edge, HashSet<Edge>>();
		
		for(Edge representativeEdge : representativeEdges) {
			HashSet<Edge> cluster = new HashSet<Edge>();
			cluster.add(representativeEdge);
			clusters.put(representativeEdge, cluster);
			
		}
		
		for(Edge requiredEdge : requiredEdges) {
			Edge choosenRepresentativ = null;
			
			for(Edge edge : representativeEdges) {
				if(choosenRepresentativ == null) {
					choosenRepresentativ = edge;
				}
				else if(getDistance(choosenRepresentativ, requiredEdge, shortestPaths) > getDistance(edge, requiredEdge, shortestPaths)) {
					choosenRepresentativ = edge;
				}
				else if (getDistance(choosenRepresentativ, requiredEdge, shortestPaths) == getDistance(edge, requiredEdge, shortestPaths)) {
					if(clusters.get(edge).size() < clusters.get(choosenRepresentativ).size()) {
						choosenRepresentativ = edge;
					}
				}
			}
			
			clusters.get(choosenRepresentativ).add(requiredEdge);
		}
		
		/*
		 * Supplement clusters with edges on shortest paths to required edges;
		 */
		ArrayList<HashSet<Edge>> completeClusters = new ArrayList<HashSet<Edge>>();
		
		for(Edge representativeEdge : representativeEdges) {
			HashSet<Edge> cluster = clusters.get(representativeEdge);
			HashSet<Edge> clusterRequiredEdges = new HashSet<Edge>();
			clusterRequiredEdges.addAll(cluster);
			
			for(Edge requiredEdge : clusterRequiredEdges) {
				cluster.addAll(shortestPaths.get(0).get(requiredEdge.getA()).getEdgeSet());
				cluster.addAll(shortestPaths.get(0).get(requiredEdge.getB()).getEdgeSet());
			}
			
			completeClusters.add(cluster);
		}
	
		/*
		 * Calculate the postman tour for each cluster
		 */
		Path[] postmanTours = new Path[5];
		
		for(int k = 0; k < 5 && k < completeClusters.size(); k++) {
			Graph clusterGraph = new Graph(completeClusters.get(k));
			postmanTours[k] = clusterGraph.getPostmanTour();
		}
		
		return postmanTours;
	}
	
	public int getDistanceToDepot(Edge edge, HashMap<Integer, HashMap<Integer, Path>> shortestPaths) {
		return Math.max(shortestPaths.get(0).get(edge.getA()).getCost(), shortestPaths.get(0).get(edge.getB()).getCost());
	}
	
	public int getDistance(Edge a, Edge b, HashMap<Integer, HashMap<Integer, Path>> shortestPaths) {
		return Math.max(
				Math.max(shortestPaths.get(a.getA()).get(b.getA()).getCost(), shortestPaths.get(a.getA()).get(b.getB()).getCost()), 
				Math.max(shortestPaths.get(a.getB()).get(b.getA()).getCost(), shortestPaths.get(a.getB()).get(b.getB()).getCost()));
	}
	
	public int getDistance(Edge edge, HashSet<Edge> edgeSet, HashMap<Integer, HashMap<Integer, Path>> shortestPaths) {
		int minimalDistance = -1;
		
		for(Edge edgeSetEdge : edgeSet) {
			int distance = getDistance(edge, edgeSetEdge, shortestPaths);
			if(minimalDistance == -1) {
				minimalDistance = distance;
			}
			else if(distance < minimalDistance){
				minimalDistance = distance;
			}
		}
		
		return minimalDistance;
	}
}
