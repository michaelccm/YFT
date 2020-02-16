package com.teamcenter.custwork.toolkit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NumTopDateTool {
	public static String getDateString(Date date){
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  if(date != null){
			  return dateFormat.format(date);
		  }else{
			  return null;
		  }
		 
	}
	
	
	public static String getDateString1(Date date){
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		  if(date != null){
			  return dateFormat.format(date);
		  }else{
			  return null;
		  }
		 
	}
	
	public static Date getDate(String str) throws ParseException{
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  if(str != null && str.trim().length() >0){
			  return dateFormat.parse(str);
		  }else{
			  return null;
		  }
		  
	}
}
