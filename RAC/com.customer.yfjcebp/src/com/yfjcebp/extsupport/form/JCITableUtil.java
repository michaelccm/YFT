package com.yfjcebp.extsupport.form;

import javax.swing.text.AttributeSet;  
import javax.swing.text.BadLocationException;  
import javax.swing.text.PlainDocument;  

import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;

public class JCITableUtil{
	
	/**
	 * ÅÐ¶Ï¶ÔÏóÊÇ·ñ
	 * @param com
	 * @return
	 */
	public static Boolean isPass(TCComponent com,TCComponent form){
		boolean isPass = false;
		TCComponent[] releasedCom;
		try {
			if (com == null) {
				return false;
			}
			
			releasedCom = com.getReferenceListProperty("release_status_list");
			if (releasedCom != null && releasedCom.length >0) {
				isPass = true;
			}else{
				isPass = false;
			}
//			if (!isPass) {
//				ViewEditHelper viewHelp = new ViewEditHelper(form.getSession());
//				boolean ischeckoutAble = viewHelp.isCheckoutable(form);
//				if(!ischeckoutAble){
//					isPass = true;
//				}
//			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isPass;
	}
	
	public LimitedDocument getLimitedDocument(int num , String allowChar){
		LimitedDocument l = new LimitedDocument(num);
		l.setAllowChar(allowChar);
		return l;
	}

	public class LimitedDocument extends PlainDocument {  
		  
	    private int _maxLength  = -1;  
	    private String _allowCharAsString = null;  
	  
	    public LimitedDocument(){  
	        super();  
	    }  
	  
	    public LimitedDocument( int maxLength ){  
	        super();  
	        this._maxLength = maxLength;  
	    }  
	  
	    public void insertString( int offset, String str, AttributeSet attrSet) throws BadLocationException{  
	        if(str == null) {  
	            return;  
	        }  
	        if(_allowCharAsString != null && str.length() == 1) {  
	            if(_allowCharAsString.indexOf(str) == -1){  
	                return;  
	            }  
	        }  
	        char[] charVal = str.toCharArray();  
	        String strOldValue = getText(0, getLength());  
	        byte[] tmp = strOldValue.getBytes();  
	        if(_maxLength != -1 && (tmp.length + charVal.length > _maxLength)){  
	            return;  
	        }  
	        super.insertString(offset, str, attrSet);  
	    }  
	  
	    public void setAllowChar(String str) {  
	        _allowCharAsString = str;  
	    }  
	} 
}
