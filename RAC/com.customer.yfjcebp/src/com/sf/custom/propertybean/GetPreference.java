package com.sf.custom.propertybean;

import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

//获取首选项工具类
public class GetPreference {
	
	public static String GetVaiue(TCSession session,String preferenceName) {
		TCPreferenceService preferenceService = session.getPreferenceService();
		String value = preferenceService.getStringValue(preferenceName);
		return value;
	}
	
	public static String[] GetVaiues(TCSession session,String preferenceName) {
		TCPreferenceService preferenceService = session.getPreferenceService();
		String[] values = preferenceService.getStringValues(preferenceName);
		return values;
	}
}
