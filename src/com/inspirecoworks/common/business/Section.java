package com.inspirecoworks.common.business;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Section extends LinkedHashMap<String, Article> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5191207183511150372L;
	public String title;
	public String link;


	
}
