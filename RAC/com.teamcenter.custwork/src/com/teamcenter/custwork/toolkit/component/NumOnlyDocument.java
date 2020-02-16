package com.teamcenter.custwork.toolkit.component;

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumOnlyDocument extends PlainDocument {

	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		//System.out.println("<><><><><><><><><><><><><><><><><><><><>");
		if(Pattern.matches("^[0-9]*$", str)){
			super.insertString(offs, str, a);
		}else{
			return;
		}
		
	}
	
	

}
