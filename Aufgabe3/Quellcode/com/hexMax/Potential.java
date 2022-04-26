package com.hexMax;

public class Potential {
	private int subtractions;
	private int additions;
	
	private int value;
	private int cost;
	
	public Potential(int value) {
		this.value = value;
	}
	
	public Potential(int subtractions, int additions, int value, int cost) {
		this.subtractions = subtractions;
		this.additions = additions;
		this.value = value;
		this.cost = cost;
	}
	
	public Potential(Potential oldPotential, Potential digitPotential) {
		subtractions = oldPotential.getSubtractions() + digitPotential.getSubtractions();
		additions = oldPotential.getAdditions() + digitPotential.getAdditions();
		
		value = additions - subtractions;
		
		if(subtractions < additions) {
			cost = subtractions;
		}
		else {
			cost = additions;
		}
	}
	
	@Override
	public boolean equals(Object arg0) {
		Potential potential = (Potential) arg0;
		
		if(potential.getValue() == value) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int getSubtractions() {
		return subtractions;
	}

	public int getAdditions() {
		return additions;
	}

	public int getValue() {
		return value;
	}

	public int getCost() {
		return cost;
	}
}
