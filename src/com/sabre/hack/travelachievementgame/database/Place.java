package com.sabre.hack.travelachievementgame.database;

public class Place {
	private long id;
	private String name;
	private int count;
	private String fb_id;

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

	public String getFb_id() {
		return fb_id;
	}

	public void setFb_id(String fb_id) {
		this.fb_id = fb_id;
	}
	
	@Override
	public String toString(){
		StringBuilder result = new StringBuilder("name: ");
		return result.append(name).append(" count: ").append(count).toString();
	}
}
