package com.yfjcebp.extsupport;

import com.teamcenter.rac.kernel.TCComponent;

public class ExtSupportUtil {

	private String userName="";//jci6_UserName
	private String year="";//jci6_Year
	private String month="";//jci6_Month;
	private TCComponent extsupport;//ExtSupport
	private String usagePercent="";//jci6_Percent
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public TCComponent getExtsupport() {
		return extsupport;
	}
	public void setExtsupport(TCComponent extsupport) {
		this.extsupport = extsupport;
	}
	public String getUsagePercent() {
		return usagePercent;
	}
	public void setUsagePercent(String usagePercent) {
		this.usagePercent = usagePercent;
	}
	
	 
}
