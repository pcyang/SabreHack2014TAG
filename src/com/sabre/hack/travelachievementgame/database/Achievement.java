package com.sabre.hack.travelachievementgame.database;

public class Achievement {
	private long id;
	private String name;
	private String filename;
	private String category;
	private int type;
	private int count;
	private int received;

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
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString(){
		StringBuilder result = new StringBuilder("name: ");
		return result.append(name).append(" count: ").append(count).toString();
	}
	public int getReceived() {
		return received;
	}
	public void setReceived(int received) {
		this.received = received;
	}


}
