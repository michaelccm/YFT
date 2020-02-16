package com.yfjcebp.extsupport.formtest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.text.AttributeSet;  
import javax.swing.text.BadLocationException;  
import javax.swing.text.PlainDocument;  

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;

public class JCIUtil{
	
	
	/**
	 * 用于查询
	 * 
	 * @param session
	 * @param query_name
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static TCComponent[] query(TCSession session, String query_name,
			String[] arg1, String[] arg2) {
		TCComponentContextList imancomponentcontextlist = null;
		TCComponent[] component = null;
		try {
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session
					.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
					.find(query_name);
			TCTextService imantextservice = session.getTextService();
			String[] queryAttribute = new String[arg1.length];
			for (int i = 0; i < arg1.length; ++i)
				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);

			String[] queryValues = new String[arg2.length];
			for (int i = 0; i < arg2.length; ++i)
				queryValues[i] = arg2[i];
			imancomponentcontextlist = imancomponentquery
					.getExecuteResultsList(queryAttribute, queryValues);
			component = imancomponentcontextlist.toTCComponentArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return component;
	}
	
	/**
	 * 通过所给的JCI6ExtRateRevision，找到其最新已发布版本
	 * 
	 * @param searchItemRevisions
	 * @return
	 * @throws TCException
	 */
	public static  ArrayList<TCComponentItemRevision> getLastPassRevision(
			ArrayList<TCComponentItemRevision> itemRevisionList)
			throws TCException {
		ArrayList<TCComponentItemRevision> list = new ArrayList<TCComponentItemRevision>();
		list.addAll(itemRevisionList);
		HashMap<String, Date> listExistMap = new HashMap<String, Date>();
		for (TCComponentItemRevision tcComponentItemRevision : itemRevisionList) {
			//查看对象是否已经离职，如果已经离职，就移除
			TCComponentItem tcComponentItem = tcComponentItemRevision.getItem();
			TCComponent[] refs = tcComponentItem.getReferenceListProperty("IMAN_reference");
			if (refs == null || refs.length == 0) {
				boolean ifail = list.remove(tcComponentItemRevision);
				if (ifail) {
					System.out.println("移除队列中非最新已发布版本："
							+ tcComponentItemRevision
									.getStringProperty("object_string")
							+ "_"
							+ tcComponentItemRevision
									.getStringProperty("item_id"));
				} else {
					System.out.println("移除队列中非最新已发布版本："
							+ tcComponentItemRevision
									.getStringProperty("object_string")
							+ "_"
							+ tcComponentItemRevision
									.getStringProperty("item_id")
							+ "  失败！！！");
				}
			}
			//查看时间序列
			String itemUID = tcComponentItemRevision.getItem().getUid();
			if (listExistMap.containsKey(itemUID)) {
				Date tmpRevDate = tcComponentItemRevision
						.getDateProperty("creation_date");
				Date mapDate = listExistMap.get(itemUID);
				if (tmpRevDate.after(mapDate)) {
					boolean ifail = list.remove(tcComponentItemRevision);
					if (ifail) {
						System.out.println("移除队列中非最新已发布版本："
								+ tcComponentItemRevision
										.getStringProperty("object_string")
								+ "_"
								+ tcComponentItemRevision
										.getStringProperty("item_id"));
					} else {
						System.out.println("移除队列中非最新已发布版本："
								+ tcComponentItemRevision
										.getStringProperty("object_string")
								+ "_"
								+ tcComponentItemRevision
										.getStringProperty("item_id")
								+ "  失败！！！");
					}
				}
			} else {
				Date tmpRevDate = tcComponentItemRevision
						.getDateProperty("creation_date");
				listExistMap.put(itemUID, tmpRevDate);
			}
		}
		return list;
	}
	
	/**
	 * 获取相关的汇率
	 * @param currentComponeyName
	 * @return
	 */
	public static HashMap<String, String[]> getModifier(String ratePreferenceName,TCSession tcsession){
		HashMap<String, String[]> componeyNameAndModifierMap = new HashMap<String, String[]>();
		TCPreferenceService prefSvc = tcsession.getPreferenceService();
		String[] strs = prefSvc.getStringArray(TCPreferenceService.TC_preference_all, "YFJC_User_Rate_Mapping");
		for (String string : strs) {
			if (string.startsWith("ExtSupporter{.}")) {
				String componyName = "";
				String[] propValue = string.split("=");
				if (propValue.length != 2) {
					continue;
				}
				//获取公司 propValue[0]=ExtSupporter{.}tata1
				System.out.println(string);
				String[] props = propValue[0].split("\\{.\\}");
				if (props.length == 2) {
					componyName = props[1].trim();
				}
				//获取费率 propValue[1]=Normal Hours,x1{;}Normal Hours,x1{;}Normal Hours,x1{;}Normal Hours,x1
				if (componyName != null && !componyName.isEmpty()) {
					String[] rs = propValue[1].split("\\{;\\}");
					if (rs.length == 4) {
						String[] s = new String[4];
						for (String string2 : rs) {
							String[] string2s = string2.split(",");
							if (string2.contains("Normal")) {
								s[0] = string2s[1];
							}else if (string2.contains("Overtime")) {
								s[1] = string2s[1];
							}else if (string2.contains("Holiday")) {
								s[2] = string2s[1];
							}else if (string2.contains("Weekend")) {
								s[3] = string2s[1];
							}
						}
						System.out.println("添加-------------"+componyName);
						componeyNameAndModifierMap.put(componyName, s);
					}
				}
			}
		}
		System.out.println("componeyNameAndModifierMap的数量："+componeyNameAndModifierMap.size());
		return componeyNameAndModifierMap;
	}
	
	/**
	 * 将date日期转换为英文的星期
	 * @param date
	 * @return
	 */
	public static String dateToWeek(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.ENGLISH);
		return sdf.format(date);
	}
	
	/**
	 * 获取当月的天，以星期的格式
	 * 
	 * @return
	 */
	public static List<Date> dayWithWeekend(int jciYear,int jciMonth) {
		List<Date> list = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM");
		Date date = null;
		try {
			date = sdf1.parse(jciYear + "_" + jciMonth);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		int month = cal.get(Calendar.MONTH);
		while (cal.get(Calendar.MONTH) == month) {
			list.add(cal.getTime());
			cal.add(Calendar.DATE, 1);
		}

		return list;
	}
	
	/**
	 * 获取首选项中的节假日
	 * @param tcsession
	 * @param holiday_preference
	 * @return
	 */
	public static ArrayList<Date> getPreferenceHoliday(TCSession tcsession , String holiday_preference) {
		ArrayList<Date> holidayList = new ArrayList<Date>();
		String[] holiday = tcsession.getPreferenceService().getStringArray(4,
				holiday_preference);
		SimpleDateFormat tmpsm = new SimpleDateFormat("yyyy-MM-dd");
		for (String string : holiday) {
			try {
				holidayList.add(tmpsm.parse(string));
			} catch (ParseException e) {
			}
		}
		return holidayList;

	}
	
	/**
	 * 将类型Date转换为String
	 * 格式：yyyy/MM/dd
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date){
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(date);
	}
	
	
	/**
	 * 判断对象是否
	 * @param com
	 * @return
	 */
	public static Boolean isPass(TCComponent com){
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
