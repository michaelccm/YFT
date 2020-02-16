/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: DateUtil.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-15	liqz  	Ini									   
#=============================================================================
 */
package com.yfjcebp.date.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * @author liqz
 */
public class DateUtils {
	
	/**
	 * getCalendarForDate:: get calendar from date
	 * @param Date
	 * @return Calendar
	 * */
	public static Calendar getCalendarForDate(Date date){
		Calendar calendar = null;
		calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * getIntervalMonthForDate::
	 * @param Calendar starCal 
	 * @param Calendar endCal 
	 * @return int month interval
	 * */
	public static int getIntervalMonthForDate(Calendar starCal,Calendar endCal) {
		int monthday;
		int sYear = starCal.get(Calendar.YEAR);
		int sMonth = starCal.get(Calendar.MONTH);
		int sDay = starCal.get(Calendar.DATE);
		
		int eYear = endCal.get(Calendar.YEAR);
		int eMonth = endCal.get(Calendar.MONTH);
		int eDay = endCal.get(Calendar.DATE);

		monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));
		if (sDay <= eDay) {
			monthday = monthday + 1;
		}
		monthday = Math.abs(monthday); //getMonthsFromStartToEnd(monthday, starCal, endCal);
		return monthday;
	}

	/**
	 * getEveryMonthFromStartToEnd:: get every month and day of the month from start date to end date
	 * @param  intervalMonth 
	 * @param Calendar
	 * @param Calendar
	 * @return Vector<String> dates for every month 
	 * */
	public static Vector<String> getEveryMonthFromStartToEnd(int intervalMonth, Calendar starCal, Calendar endCal) {
		Vector<String> vtDate = new Vector<String>();
		String strFromDate = "";
		String strEndDate = "";
		int days = 0;

		Date now = new Date();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateformatmonth = new SimpleDateFormat("yyyyMM");
		String strNowMonth = dateformatmonth.format(now);
		int nowYearMonth = Integer.parseInt(strNowMonth);
		int startYearMonth = Integer.parseInt(dateformatmonth.format(starCal.getTime()));
		int endYearMonth =  Integer.parseInt(dateformatmonth.format(endCal.getTime()));
    	 
    	 Calendar endCalfrom = (Calendar) endCal.clone();
    	 endCalfrom.set(Calendar.DATE, 1);
    	
    	 if(nowYearMonth <= startYearMonth){
    		 if(startYearMonth == endYearMonth){
    			 strFromDate = dateformat.format(starCal.getTime());
	             strEndDate =  dateformat.format(endCal.getTime());
    		 }else{
	    		 days = getDaysOfMonth(starCal.getTime());
	    		 Calendar endCal1 = (Calendar) starCal.clone();
	    		 endCal1.set(Calendar.DATE, days);
	    		 strFromDate = dateformat.format(starCal.getTime());
	             strEndDate =  dateformat.format(endCal1.getTime());
    		 }
             vtDate.add(strFromDate+"~" + strEndDate);
    	 }
		if (intervalMonth > 1) {
			Calendar starCal2 = (Calendar) starCal.clone();
			Calendar endCal2;
			for (int i = 0; i < intervalMonth - 1; i++) {
				starCal2 = (Calendar) starCal2.clone();
				starCal2.add(Calendar.MONTH, 1);
				starCal2.set(Calendar.DATE, 1);

				if (starCal2.compareTo(endCalfrom) >= 0) {
					continue;
				}
				endCal2 = (Calendar) starCal2.clone();
				days = getDaysOfMonth(starCal2.getTime());
				endCal2.set(Calendar.DATE, days);

				startYearMonth = Integer.parseInt(dateformatmonth.format(starCal2.getTime()));
				if (nowYearMonth <= startYearMonth) {
					strFromDate = dateformat.format(starCal2.getTime());
					strEndDate = dateformat.format(endCal2.getTime());
					vtDate.add(strFromDate + "~" + strEndDate);
				}
			}
		}
		 if(nowYearMonth <= startYearMonth && startYearMonth < endYearMonth){
			strFromDate = dateformat.format(endCalfrom.getTime());
			strEndDate = dateformat.format(endCal.getTime());
			vtDate.add(strFromDate + "~" + strEndDate);
		}
		return vtDate;
	}

	/**
	 * getDaysOfMonth:: get total days of the given date
	 * @param Date
	 * @return int total days
	 * */
	public static int getDaysOfMonth(Date date) {
		Calendar time = Calendar.getInstance();
		SimpleDateFormat dateformatyear = new SimpleDateFormat("yyyy");
		SimpleDateFormat dateformatmonth = new SimpleDateFormat("MM");

		int year = Integer.parseInt(dateformatyear.format(date));
		int month = Integer.parseInt(dateformatmonth.format(date));
		time.clear();
		time.set(Calendar.YEAR, year);
		// year
		time.set(Calendar.MONTH, month - 1);
		// Calendar Jan is 0,month
		int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);//days of this month 
		return day;
	}

}
