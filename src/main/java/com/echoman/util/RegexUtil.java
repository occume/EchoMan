package com.echoman.util;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	public static MatchResult getMatchResult(String input, String regex){
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		MatchResult result = null;
		
		if(m.find()){
			result = m.toMatchResult();
		}
		
		return result;
	}
	
	public static String getGroup(String input, String regex){
		MatchResult result = getMatchResult(input, regex);
		String ret = "";
		if(result != null){
			ret = result.group();
		}
		return ret;
	}
	/**
	 * if match, get group 1
	 * @param input
	 * @param regex
	 * @return
	 */
	public static String getGroup1(String input, String regex){
		
		MatchResult result = getMatchResult(input, regex);
		String ret = "";
		if(result != null){
			ret = result.group(1);
		}
		return ret;
	}
	/**
	 * if match, get group 1 and group 2
	 * @param input
	 * @param regex
	 * @return
	 */
	public static String[] getGroup12(String input, String regex) {
		
		MatchResult result = getMatchResult(input, regex);
		String[] ret = new String[2];
		if(result != null){
			ret[0] = result.group(1);
			ret[1] = result.group(2);
		}
		return ret;
	}
}
