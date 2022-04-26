package com.hexMax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public enum Digit {
	_F("0001111", 'F', 15),
	_E("0011111", 'E', 14),
	_D("1111001", 'D', 13),
	_C("0011110", 'C', 12),
	_B("0111101", 'B', 11),
	_A("1101111", 'A', 10),
	_9("1110111", '9', 9),
	_8("1111111", '8', 8),
	_7("1100010", '7', 7),
	_6("0111111", '6', 6),
	_5("0110111", '5', 5),
	_4("1100101", '4', 4),
	_3("1110011", '3', 3),
	_2("1011011", '2', 2),
	_1("1100000", '1', 1),
	_0("1111110", '0', 0);
	
	private final String code;
	private final Character character;
	private final int value;
	
	private static Potential[] [] potentials = new Potential[16] [5];
	private static int[] [] [] changeCosts = new int[16] [16] [5];
	
	private Digit(String code, Character character, int value) {
		this.code = code;
		this.character = character;
		this.value = value;
	}

	public static Digit getDigit(Character c) {
		for(Digit digit : values()) {
			if(digit.getCharacter().equals(c)) {
				return digit;
			}
		}
		return null;
	}
	
	public static Potential[] getPotentials(Digit digit) {
		if(potentials[digit.getValue()] [0] != null) {
			return potentials[digit.getValue()];
		}
		else {
			ArrayList<Potential> digitPotentials = new ArrayList<Potential>();
			
			Digit[] values = values();
			
			for(int n = 0; n < values.length; n++) {
				if(!values[n].equals(digit)) {
					int[] changeCost = getChangeCosts(digit, values[n]);
					
					int additions = changeCost[0];
					int subtractions = changeCost[1];
					int value = changeCost[2];
					int cost = changeCost[3];
					
					Potential potential = new Potential(subtractions, additions, value, cost);
					
					if(potential.getValue() != 0) {
						if(!digitPotentials.contains(potential)) {
							digitPotentials.add(potential);
						}
						else {
							int exsistingPotentialIndex = digitPotentials.indexOf(potential);
							Potential exsitingPotential = digitPotentials.get(exsistingPotentialIndex);
							
							if(potential.getCost() < exsitingPotential.getCost()) {
								digitPotentials.set(exsistingPotentialIndex, potential);
							}
						}
					}
				}	
			}
			
			for(int n = 0; n < 5; n++) {
				potentials[digit.getValue()] [n] = digitPotentials.get(n);
			}
			
			return potentials[digit.getValue()];
		}
	}
	
	public static int[] getChangeCosts(Digit from, Digit to) {
		if(changeCosts[from.getValue()] [to.getValue()] [4] != 0) {
			return changeCosts[from.getValue()] [to.getValue()];
		}
		else {
			int additions = 0;
			int subtractions = 0;
			
			char[] fromCodeArray = from.getCode().toCharArray();
			char[] toCodeArray = to.getCode().toCharArray();
			
			for(int i = 0; i < fromCodeArray.length; i++) {
				if(fromCodeArray[i] == '0' && toCodeArray[i] == '1') {
					additions++;
				}
				else if(fromCodeArray[i] == '1' && toCodeArray[i] == '0') {
					subtractions++;
				}
			}
			
			int matchDifference  = additions - subtractions;
			int cost;
			
			if(subtractions < additions) {
				cost = subtractions;
			}
			else {
				cost = additions;
			}
			
			int[] changeCost = new int[5];
			
			changeCost[0] = additions;
			changeCost[1] = subtractions;
			changeCost[2] = matchDifference;
			changeCost[3] = cost;
			changeCost[4] = 1;
			
			changeCosts[from.getValue()] [to.getValue()] = changeCost;
			
			return changeCost;
		}
	}
	
	public Digit[] getBetterDigits() {
		return Arrays.copyOfRange(values(), 0, 15 - value);
	}
	
	public String getCode() {
		return code;
	}

	public Character getCharacter() {
		return character;
	}
	
	public int getValue() {
		return value;
	}
}
