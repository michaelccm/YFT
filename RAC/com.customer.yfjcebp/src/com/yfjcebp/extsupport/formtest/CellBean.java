package com.yfjcebp.extsupport.formtest;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;

public class CellBean {

	private String showedValue;
	private Double approvedValue;
	private Double noApprovedValue;
	private boolean isVisible = true;
	
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
	public final static String cellType_N = "Normal Hours";
	public final static String cellType_O = "Overtime Hours";
	public final static String cellType_W = "Weekend Hours";
	public final static String cellType_H = "Holiday Hours";
	public final static String cellType_Null = "NULL";
	private String cellType;
	
	//是否已经发布
	private boolean isPass = false;
	//是否存在
	private boolean hasExtTSE = false;
	// JCI6_ExtTSE
	private TCComponent extTSE_component;
	/**属性
	 *  jci6_Division	TypeReference
		jci6_Section	TypeReference
		jci6_Date	Date
		jci6_BillType	String（32）
		jci6_Rate	Double
		jci6_Modifier	String (8)
		jci6_GID	String（32）
		jci6_ExtName	String（32）
		jci6_Company	String（32）
		jci6_Program	TypeReference
		jci6_Onboard	Date
		jci6_Hours	Double
	 */
	private TCComponent division;
	private TCComponent	section;
	//yyyy_MM_dd
	private Date date;
	private String billType;
	private Double rate;
	private String modifier;
	private String gid;
	private String extName;
	private String company;
	private TCComponent program;
	private Date onboard;
	private Double hours;
	
	
	CellBean(int columnType,String cellType) {
		this.columnType = columnType;
		this.cellType = cellType;
		if (columnType == columnType_move8_Day) {
			showedValue = "";
		}else{
			showedValue = "";
		}
	}
	
	CellBean(TCComponent extTSE_component,int columnType,String cellType){
		this.extTSE_component = extTSE_component;
		hasExtTSE = true;
		this.columnType = columnType;
		this.cellType = cellType;
		try {
			division = extTSE_component.getReferenceProperty("jci6_Division");
			section = extTSE_component.getReferenceProperty("jci6_Section");
			date = extTSE_component.getDateProperty("jci6_Date");
			billType = extTSE_component.getStringProperty("jci6_BillType");
			rate = extTSE_component.getDoubleProperty("jci6_Rate");
			modifier = extTSE_component.getStringProperty("jci6_Modifier");
			gid = extTSE_component.getStringProperty("jci6_GID");
			extName = extTSE_component.getStringProperty("jci6_ExtName");
			company = extTSE_component.getStringProperty("jci6_Company");
			program = extTSE_component.getReferenceProperty("jci6_Program");
			onboard = extTSE_component.getDateProperty("jci6_Onboard");
			hours = extTSE_component.getDoubleProperty("jci6_Hours");
			isPass = JCIUtil.isPass(extTSE_component);
			
			switch (columnType) {
			case 0:
				showedValue = extName;
				break;
			case 1:
				showedValue = gid;
				break;
			case 10:
				
				showedValue = company;
				break;
			case 11:
				if (program != null) {
					String tmpProgramID = program.getStringProperty("item_id");
					String tmpProgramName = program.getStringProperty("object_name");
					showedValue = tmpProgramID+"_"+tmpProgramName;
				}else{
					showedValue = "";
				}
				break;
			case 12:
				SimpleDateFormat smf = new SimpleDateFormat("yyyy/MM/dd");
				showedValue = smf.format(onboard);
				break;
//			case 13:
//				showedValue = gid;
//				break;
//			case 14:
//				showedValue = gid;
//				break;
//			case 15:
//				showedValue = gid;
//				break;
//			case 16:
//				showedValue = gid;
//				break;
//			case 17:
//				showedValue = gid;
//				break;
			case 18:
				if (billType.equals(cellType_O)) {
					this.cellType = billType;
				}
				showedValue = hours.toString().replace(".0", "");
				break;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isPass(){
		return isPass;
	}
	
	public int getColumnType(){
		return columnType;
	}
	
	public void setValue(Double approvedValue,Double noApprovedValue){
		this.approvedValue = approvedValue;
		this.noApprovedValue = noApprovedValue;
		showedValue = Double.toString(approvedValue+noApprovedValue);
	}
	public Double getApprovedValue(){
		return approvedValue;
	}
	public Double getNoApprovedValue(){
		return noApprovedValue;
	}
	
	public String getCellType(){
		return cellType;
	}
	
	public void setCellType(String cellType){
		this.cellType = cellType;
	}
	
	public void setShowedValue(String showedValue){
		this.showedValue = showedValue;
	}
	
	public String getShowedValue(){
		return showedValue;
	}
	
	public void setVisible(boolean ifail){
		this.isVisible = ifail;
	}
	public boolean isVisible(){
		return isVisible;
	}
	
	public boolean hasExtTSE(){
		return this.hasExtTSE;
	}
	
	//创建Head
	public TCComponent createHead(TCComponent extRate,String programInfoID){
		
		return null;
	}
	
	//创建follow
	public TCComponent createFollow(TCComponent extRate,String programInfoID){
		
		return null;
	}
	
	
}
