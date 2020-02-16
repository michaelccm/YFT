package com.yfjcebp.extsupport.formtest;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;

public class CellBean2 {

	//显示的值
	private String showedValue;
	//值是否显示
	private boolean isValueShowedable = true;
	//列所在的table:fix/move
	public static String tableType_fix = "fix";
	public static String tableType_move = "move";
	private String tableType;
	/**
	 * 列：
	 * fix:Name、Global ID
	 * move:"Company", "Program", "Onboard Date","Cost", "N", "O", "W", "H",Day
	 */
	public static int columnType_fix0_Name = 0;
	public static int columnType_fix1_GlobalID = 1;
	public static int columnType_move0_Company = 10;
	public static int columnType_move1_Program = 11;
	public static int columnType_move2_OnboardDate = 12;
	public static int columnType_move3_Cost = 13;
	public static int columnType_move4_N = 14;
	public static int columnType_move5_O = 15;
	public static int columnType_move6_W = 16;
	public static int columnType_move7_H = 17;
	public static int columnType_move8_Day = 18;
	private int columnType;
	//单个的类型：Normal、Overtime、Holiday、Weekend
	public static String cellType_N = "Normal";
	public static String cellType_O = "Overtime";
	public static String cellType_W = "Weekend";
	public static String cellType_H = "Holiday";
	public static String cellType_Null = "NULL";
	private String cellType;
	//日期，哪天
	private int inDay;
	
	//单元格存在对应的TC对象
	private TCComponent com;
	
	//com的类型
	private String comCellType;
	
	//是否是空的
	private boolean hasValue = false;
	
	//是否已经发布
	private boolean isPass = false;
	
	//jci6_Rate
	private double rate;
	//jci6_Modifier
	private double modifier; 
	
	/**
	 * 默认：用于新建行的时候使用
	 */
	CellBean2(String tableType,int columnType,String cellType){
		setShowedValue("");
		setValueShowedable(true);
		setTableType(tableType);
		setColumnType(columnType);
		setCellType(cellType);
		com = null;
	}
	
	/**
	 * 用于已有Com的情况下使用的
	 * @param com
	 */
	CellBean2(TCComponent com,String tableType,int columnType,String cellType) {
		hasValue = true;
		isPass = JCIUtil.isPass(com);
		String value = "";
		try {
			switch (columnType) {
			case 0:
				//jci6_ExtName
				value = com.getStringProperty("jci6_ExtName");
				
				break;
			case 1:
				//jci6_GID
				value = com.getStringProperty("jci6_GID");
				break;
			case 10:
				//jci6_Company
				value = com.getStringProperty("jci6_Company");
				break;
			case 11:
				//jci6_Program
				TCComponent tmpProgram = com.getReferenceProperty("jci6_Program");
				if (tmpProgram == null) {
					value = "";
				}else{
					String tmpProgramID = tmpProgram.getStringProperty("item_id");
					String tmpProgramName = tmpProgram.getStringProperty("object_name");
					value = tmpProgramID+"_"+tmpProgramName;
				}
				break;
			case 12:
				//jci6_Onboard
				SimpleDateFormat smf = new SimpleDateFormat("yyyy/MM/dd");
				Date tmpOnboard = com.getDateProperty("jci6_Onboard");
				if (tmpOnboard == null) {
					value = "";
				}else{
					value = smf.format(tmpOnboard);
				}
				break;
//			case 13:
//				
//				break;
			case 18:
				//jci6_Hours
				double tmpHour = com.getDoubleProperty("jci6_Hours");
				value = Double.toString(tmpHour);
				comCellType = com.getStringProperty("jci6_BillType");
				if (comCellType.contains(cellType_N)) {
					comCellType = cellType_N;
				}else if (comCellType.contains(cellType_O)) {
					comCellType = cellType_O;
				}else if (comCellType.contains(cellType_W)) {
					comCellType = cellType_W;
				}else if (comCellType.contains(cellType_H)) {
					comCellType = cellType_H;
				}
				break;
			}
			setShowedValue(value);
			setValueShowedable(true);
			setTableType(tableType);
			setColumnType(columnType);
			comCellType = comCellType;
			setCellType(cellType);
			com = null;
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 其中的com是否已经发布
	 * @return
	 */
	public boolean isComPass(){
		return isPass;
	}
	
	public String getComCellType(){
		return comCellType;
	}
	
	public boolean isTypeSame(){
		if (cellType != null && comCellType != null) {
			if (!cellType.equals(comCellType)) {
				return false;
			}
		}
		return true;
	}
	public int getInDay() {
		return inDay;
	}

	public void setInDay(int inDay) {
		this.inDay = inDay;
	}


	public boolean doHasValue(){
		return hasValue;
	}
	
	public boolean doHasCom(){
		if (com == null) {
			return false;
		}else{
			return true;
		}
	}

	public String getShowedValue() {
		return showedValue;
	}

	public void setShowedValue(String showedValue) {
		if (showedValue.length() > 0) {
			hasValue = true;
		}
		this.showedValue = showedValue;
	}

	public boolean isValueShowedable() {
		return isValueShowedable;
	}

	public void setValueShowedable(boolean isValueShowedable) {
		this.isValueShowedable = isValueShowedable;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public int getColumnType() {
		return columnType;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public String getCellType() {
		return cellType;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public TCComponent getCom() {
		return com;
	}

//	public void setCom(TCComponent com) {
//		this.com = com;
//	}
	
	
	
}
