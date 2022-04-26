package com.hexMax;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HexMaxApplication extends JFrame{

	public static void main(String[] args) {
		HexMaxApplication app = new HexMaxApplication();
	}
	
	public HexMaxApplication() {
		setTitle("40. Bundeswettbewerb Informatik - Runde 2 - Aufgabe 3 HexMax");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(775, 600);
		setLayout(null);
		
		init();
		
		setVisible(true);
	}
	
	public void init() {
		JCheckBox showInterimResultsBox = new JCheckBox("Zwischenstände anzeigen");
		showInterimResultsBox.setLocation(5, 40);
		showInterimResultsBox.setSize(200, 25);
		add(showInterimResultsBox);
		
		JTextArea resultDisplay = new JTextArea();
		resultDisplay.setLocation(10, 80);
		resultDisplay.setSize(740, 450);
		Font f = new Font("Courier New", Font.BOLD, 20);
		resultDisplay.setFont(f); 
		
		JScrollPane resultDisplayContainer = new JScrollPane(resultDisplay);
		resultDisplayContainer.setLocation(10, 80);
		resultDisplayContainer.setSize(740, 450);
		add(resultDisplayContainer);
		
		for(int n = 0; n < 6; n++) {
			int index = n;
			
			JButton loadExampleButton = new JButton("Lade Beispiel " + Integer.toString(index));
			loadExampleButton.addActionListener(new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					Number startNumber = null;
					Number improvedNumber = null;
					
					try {
						FileReader reader = new FileReader(new File("Data\\hexmax" + Integer.toString(index) + ".txt"));
						BufferedReader bufferedReader = new BufferedReader(reader);
						
						String number = bufferedReader.readLine();
						int changes = Integer.parseInt(bufferedReader.readLine());
						
						startNumber = new Number(number);
						improvedNumber = new Number(number);
						improvedNumber.improve(changes);
						
						bufferedReader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					String result = new String();
					
					result += "Ausgangszahl: " + startNumber.toString() + "\n";
					
					if(showInterimResultsBox.isSelected()) {
						result += improvedNumber.getInterimResults(startNumber);
					}
					
					result += "Verbesserte Zahl: " + improvedNumber.toString();
					
					resultDisplay.setText(result);
				}
			});
			loadExampleButton.setLocation(5 + 125 * n, 5);
			loadExampleButton.setSize(120, 25);
			add(loadExampleButton);
		}
		
		
	}
}
