package com.hexMax;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Number {
	private Digit[] digits;
	
	private ArrayList<ArrayList<Potential>> potentials;
	
	public Number(String numberSting) {
		char[] numberChars = numberSting.toCharArray();
		
		digits = new Digit[numberChars.length];
		
		for(int n = 0; n < numberChars.length; n++) {
			digits[n] = Digit.getDigit(numberChars[n]);
		}
		
		potentials = new ArrayList<ArrayList<Potential>>();
		
		calculateNextPotentials();
	}
	
	public String getInterimResults(Number before) {
		
		Queue<Integer> additions = new LinkedList<Integer>();
		Queue<Integer> subtractions = new LinkedList<Integer>();
		
		for(int digitIndex = 0; digitIndex < digits.length; digitIndex++) {
			Digit unchangedDigit = before.getDigits()[digitIndex];
			Digit changedDigit = digits[digitIndex];
			
			char[] unchangedDigitCode = unchangedDigit.getCode().toCharArray();
			char[] changedDigitCode = changedDigit.getCode().toCharArray();
			
			for(int n = 0; n < 7; n++) {
				if(unchangedDigitCode[n] == '1' && changedDigitCode[n] == '0') {
					subtractions.offer((digitIndex * 7) + n);
				}
				else if(unchangedDigitCode[n] == '0' && changedDigitCode[n] == '1') {
					additions.offer((digitIndex * 7) + n);
				}
			}
		}
		
		int numberCodeLenght = 7 * digits.length;
		char[] numberCode = new char[numberCodeLenght];
		
		for(int digitIndex = 0; digitIndex < digits.length; digitIndex++) {
			char[] digitCode = before.getDigits()[digitIndex].getCode().toCharArray();
			for(int n = 0; n < 7; n++) {
				numberCode[(digitIndex * 7) + n] = digitCode[n];
			}
		}
		
		String interimResults = new String();
		
		interimResults += convertCodeToString(numberCode);
		
		while(!additions.isEmpty()) {
			Integer addition = additions.poll();
			Integer subtraction = subtractions.poll();
			
			numberCode[addition] = '1';
			numberCode[subtraction] = '0';
			interimResults += convertCodeToString(numberCode);
		}
		
		System.out.println(interimResults);
		return interimResults;
	}
	
	public String convertCodeToString(char[] numberCode) {
		String topLine = new String();
		String middleLine = new String();
		String bottomLine = new String();
		
		for(int n = 0; n < digits.length; n++) {
			int digitIndex = n * 7;
			
			topLine += " ";
			if(numberCode[digitIndex + 5] == '1') {
				topLine += "_";
			}
			else {
				topLine += " ";
			}
			topLine += " ";
			
			if(numberCode[digitIndex + 4] == '1') {
				middleLine += "|";
			}
			else {
				middleLine += " ";
			}
			
			if(numberCode[digitIndex + 6] == '1') {
				middleLine += "_"; 
			}
			else {
				middleLine += " ";
			}
			
			if(numberCode[digitIndex] == '1') {
				middleLine += "|";
			}
			else {
				middleLine += " ";
			}
			
			if(numberCode[digitIndex + 3] == '1') {
				bottomLine += "|";
			}
			else {
				bottomLine += " ";
			}
			
			if(numberCode[digitIndex + 2] == '1') {
				bottomLine += "_";
			}
			else {
				bottomLine += " ";
			}
			
			if(numberCode[digitIndex + 1] == '1') {
				bottomLine += "|";
			}
			else {
				bottomLine += " ";
			}
			
			topLine +=    " ";
			middleLine += " ";
			bottomLine += " ";
		}
		
		String number = new String();
		
		number += topLine;
		number += "\n";
		number += middleLine;
		number += "\n";
		number += bottomLine;
		number += "\n\n";
		
		return number;
	}
	
	public void improve(int changes) {
		int additions = 0;
		int subtractions = 0;
	
		for(int digitIndex = 0; digitIndex < digits.length; digitIndex++) {
			Digit[] betterDigits = digits[digitIndex].getBetterDigits();
			
			for(Digit betterDigit : betterDigits) {
				int[] changeCosts = Digit.getChangeCosts(digits[digitIndex], betterDigit);
				
				int additionsAfterChange = additions + changeCosts[0];
				int subtractionsAfterChange = subtractions + changeCosts[1];
					
				int matchDifferenceAfterChange = additionsAfterChange - subtractionsAfterChange; // This value describes the difference of matches between the original and the changed number.
				int changesAfterChange = 0;
				
				if(subtractionsAfterChange > additionsAfterChange) {
					changesAfterChange = changes - subtractionsAfterChange;
				}
				else {
					changesAfterChange = changes - additionsAfterChange;
				}
				
				if(changesAfterChange < 0) {
					continue;
				}
				else {
					if(canMatchDifferenceBeDesolved(matchDifferenceAfterChange, changesAfterChange, digitIndex)) {
						digits[digitIndex] = betterDigit;
						additions += changeCosts[0];
						subtractions += changeCosts[1];
						break;
					}
					else {
						continue;
					}
				}
			}
		}
		
		
		/*
		 * Desolving Matchdifference
		 */
		int matchDifference = additions - subtractions;
		int changesLeft = 0;
		
		if(subtractions > additions) {
			changesLeft = changes - subtractions;
		}
		else {
			changesLeft = changes - additions;
		}
		
		int startDigitIndex = 0;
		
		for(int  digitIndex = digits.length - 1; digitIndex > -1; digitIndex--) {
			if(canMatchDifferenceBeDesolved(matchDifference, changesLeft, digitIndex)) {
				startDigitIndex = digitIndex;
				break;
			}
		}
		
		for(int digitIndex = startDigitIndex + 1; digitIndex < digits.length; digitIndex++) {
			for(Digit changedDigit : Digit.values()) {
				int[] changeCosts = Digit.getChangeCosts(digits[digitIndex], changedDigit);
				
				int additionsAfterChange = additions + changeCosts[0];
				int subtractionsAfterChange = subtractions + changeCosts[1];
					
				int matchDifferenceAfterChange = additionsAfterChange - subtractionsAfterChange; // This value describes the difference of matches between the original and the changed number.
				int changesAfterChange = 0;
				
				if(subtractionsAfterChange > additionsAfterChange) {
					changesAfterChange = changes - subtractionsAfterChange;
				}
				else {
					changesAfterChange = changes - additionsAfterChange;
				}
				
				if(changesAfterChange < 0) {
					continue;
				}
				else {
					if(changesAfterChange < 0) {
						continue;
					}
					else {
						if(canMatchDifferenceBeDesolved(matchDifferenceAfterChange, changesAfterChange, digitIndex)) {
							digits[digitIndex] = changedDigit;
							additions += changeCosts[0];
							subtractions += changeCosts[1];
							break;
						}
						else {
							continue;
						}
					}
				}
			}
		}
	}
	
	public void calculateNextPotentials() {
		if(potentials.size() == 0) {
			ArrayList<Potential> potentialsAtIndex = new ArrayList<Potential>();
			
			potentialsAtIndex.add(new Potential(0, 0, 0, 0));
			for(Potential potential : Digit.getPotentials(digits[digits.length - 1])) {
				potentialsAtIndex.add(potential);
			}
			
			potentials.add(potentialsAtIndex);
		}
		else {
			ArrayList<Potential> potentialsAtLastIndex = potentials.get(potentials.size() - 1);
			Potential[] digitPotentials = Digit.getPotentials(digits[digits.length - 1 - potentials.size()]);
					
			ArrayList<Potential> potentialsAtIndex = new ArrayList<Potential>();
			potentialsAtIndex.addAll(potentialsAtLastIndex);
					
			for(Potential lastIndexPotential : potentialsAtLastIndex) {
				for(Potential digitPotential : digitPotentials) {
					Potential newPotential = new Potential(lastIndexPotential, digitPotential);
					
					if(!potentialsAtIndex.contains(newPotential)) {
						potentialsAtIndex.add(newPotential);
					}
					else {
						int exsitingPotentialIndex = potentialsAtIndex.indexOf(newPotential);
						Potential exsistingPotential = potentialsAtIndex.get(exsitingPotentialIndex);
						
						if(newPotential.getCost() < exsistingPotential.getCost()) {
							potentialsAtIndex.set(exsitingPotentialIndex, newPotential);
						}
					}
				}
			}
			
			potentials.add(potentialsAtIndex);
		}
	}
	
	public boolean canMatchDifferenceBeDesolved(int matchDiffrence, int changesLeft, int digitIndex) {
		int invertedDigitsIndex = digits.length - 1 - digitIndex;
		int potentialIndex = potentials.size() - 1;
		
		Potential wantedPotential = new Potential(matchDiffrence * -1);
		if(invertedDigitsIndex == 0) {
			if(matchDiffrence == 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else if(potentialIndex >= invertedDigitsIndex - 1) {
			if(potentials.get(invertedDigitsIndex - 1).contains(wantedPotential)) {
				int foundPotentialIndex = potentials.get(invertedDigitsIndex - 1).indexOf(wantedPotential);
				Potential foundPotential = potentials.get(invertedDigitsIndex - 1).get(foundPotentialIndex);
				
				if(foundPotential.getCost() <= changesLeft) {
					return true;
				}
				else {
					return false;
				}
			}
			else{
				return false;
			}
		}
		else {
			if(potentials.get(potentialIndex).contains(wantedPotential)) {
				int foundPotentialIndex = potentials.get(potentialIndex).indexOf(wantedPotential);
				Potential foundPotential = potentials.get(potentialIndex).get(foundPotentialIndex);
				
				if(foundPotential.getCost() <= changesLeft) {
					return true;
				}
				else {
					calculateNextPotentials();
					return canMatchDifferenceBeDesolved(matchDiffrence, changesLeft, digitIndex);
				}
			}
			else{
				calculateNextPotentials();
				return canMatchDifferenceBeDesolved(matchDiffrence, changesLeft, digitIndex);
			}
		}
	}
	
	@Override
	public String toString() {
		String string = new String();
		
		for(Digit digit : digits) {
			string += digit.getCharacter();
		}
		
		return string;
	}
	
	public Digit[] getDigits() {
		return digits;
	}
}
