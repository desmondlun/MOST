package edu.rutgers.MOST.data;

public class Setting {
	public final String key;
	public String value;
	
	public Setting(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String curValue) {
		this.value = curValue;
	}
}

