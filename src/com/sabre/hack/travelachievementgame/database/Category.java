package com.sabre.hack.travelachievementgame.database;

public class Category {
	private long id;
	private String name;
	private int count;

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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public String toString(){
		StringBuilder result = new StringBuilder("name: ");
		return result.append(name).append(" count: ").append(count).toString();
	}
}
