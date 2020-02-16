package com.yfjcebp.projectmanager.budget;

import java.math.BigDecimal;

import com.teamcenter.rac.aif.common.AIFTableLine;
import com.teamcenter.rac.aif.common.AIFTableModel;

public class MyAIFTableModel extends AIFTableModel {
	@Override
	public Object getValueAt(int paramInt, String paramString) {
		// TODO Auto-generated method stub
		Object ss = super.getValueAt(paramInt, paramString);

		String value = ss.toString();
		
		if(isDouble(value)){
			BigDecimal bd = new BigDecimal(value);
			value = bd.stripTrailingZeros().toPlainString();
		}
		
		ss=value;

		return ss;
	}

	boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException ex) {
		}
		return false;
	}
}
