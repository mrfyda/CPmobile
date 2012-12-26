package cpmobile.core;

import java.io.Serializable;

public class Station implements Serializable {

	private static final long serialVersionUID = -7152271674194799261L;
	
	private String _name;
	
	public Station(String name) {          
		setName(name);
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}
}
