package com.avin.bakitrackernew;

public class Participant {
	private long id;
	private String name;
	private double amount;
	
	public double amountCache;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		String suffix;
		if (amount > 0) {
			suffix = "Give amount: Rs ";
		} else {
			suffix = "Take amount: Rs ";
		}
		return name + "  ------>  " + suffix + Math.abs(amount);
	}
}
