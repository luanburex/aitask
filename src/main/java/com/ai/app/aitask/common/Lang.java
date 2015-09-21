package com.ai.app.aitask.common;

public class Lang {


	public static boolean isNumeric(String str){
		if (str == null || str.length()<=0)
			return false;
		return str.matches("^\\d*$");
	}

}
