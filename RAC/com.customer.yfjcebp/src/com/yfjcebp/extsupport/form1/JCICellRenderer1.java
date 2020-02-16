package com.yfjcebp.extsupport.form1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class JCICellRenderer1 extends DefaultTableCellRenderer{
	/**
	 * 用于保存从�8列开始的�有单元格的类型：
	 * Normal、Weekend、Overtime、Holiday
	 */
	private HashMap<String, String> cellTypeMap = new HashMap<String, String>();
	private HashMap<Integer, String> cellTypeMap_Column = new HashMap<Integer, String>();
	
	public final static String normalType = "Normal Hours";
	public final static String overtimeType = "Overtime Hours";
	public final static String weekendType = "Weekend Hours";
	public final static String holidayType = "Holiday Hours";
	
	public final static Color normalColorSelected = new Color(67,143,255);
	public final static Color normalColorNoSelected = Color.WHITE;
	public final static Color overtimeColorSelected = new Color(104,255,1);
	public final static Color overtimeColorNoSelected = Color.ORANGE;
	public final static Color weekendColorSelected = new Color(120,120,120);
	public final static Color weekendColorNoSelected = new Color(180,180,180);
	public final static Color holidayColorSelected = new Color(217, 148, 55);
	public final static Color holidayColorNoSelected = new Color(245, 227, 203);
	
	boolean isFix = false;
	
	private HashMap<String, Color> rColor = new HashMap<String, Color>();
	
	/**
	 * 用于保存已经发布的�
	 */
	private HashMap<Integer, Boolean> passMap = new HashMap<Integer, Boolean>();
	
	
//	public JCICellRenderer(JTable table) {
//		if (table != null) {
//			isFix = true;
//		}
//		
//	}
	/**
	 * 设置显示单元格类型，以�来控制显示的颜色
	 * normalType,overtimeType,weekendType,holidayType
	 * @param location	类似：（row,column�
	 * @param type
	 */
	public void setCellType(String location,String type){
		cellTypeMap.put(location, type);
	}
	public String getCellType(String location){
		return cellTypeMap.get(location);
	}
	
	/**
	 * 设置显示单元格类型，以�来控制显示的颜色
	 * normalType,overtimeType,weekendType,holidayType
	 * @param location	类似：column
	 * @param type
	 */
	public void setCellType(Integer location,String type){
		cellTypeMap_Column.put(location, type);
	}
	
	public void setPass(int row , Boolean ispass){
		passMap.put(row, ispass);
	}
	
	public Color getColor(String location){
		return rColor.get(location);
	}
	

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isFix) {
			//是fix的：�丸�拉�，一丸�舚�
		}else{
			//是mov�
			if (column == 4) {
				//Normal
				if (isSelected) {
					super.setForeground(table.getSelectionForeground());
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setForeground(Color.BLACK);
					super.setBackground(Color.GREEN);
				}
			}else if (column == 5) {
				//Overtime
				if (isSelected) {
					super.setForeground(table.getSelectionForeground());
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setForeground(Color.BLACK);
					super.setBackground(Color.ORANGE);
				}
			}else if (column == 6) {
				//Weekend
				if (isSelected) {
					super.setForeground(table.getSelectionForeground());
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setForeground(Color.BLACK);
					super.setBackground(Color.LIGHT_GRAY);
				}
			}else if (column == 7) {
				//Holiday
				if (isSelected) {
					super.setForeground(table.getSelectionForeground());
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setForeground(Color.BLACK);
					super.setBackground(Color.PINK);
				}
			}else if (column > 7) {
				String tmpType = cellTypeMap.get(row+","+column);
//				System.out.println(row+","+column+"=="+tmpType);
				tmpType = ((tmpType!=null && tmpType.length() > 3)?tmpType:cellTypeMap_Column.get(column));
//				System.out.println(row+","+column+"=="+tmpType);
				tmpType = ((tmpType!=null && tmpType.length() > 3)?tmpType:normalType);
//				System.out.println(row+","+column+"=="+tmpType);
				if (tmpType.equals(normalType)) {
					if (isSelected) {
						super.setForeground(Color.BLACK);
						super.setBackground(normalColorSelected);
					}else{
//						String cell0 = table.getValueAt(row, 0).toString();
//						String cell1 = table.getValueAt(row, 0).toString();
						String cell2 = table.getValueAt(row, 0).toString();
						String normalValue = table.getValueAt(row, column).toString();
						if ((normalValue.trim().isEmpty()||Double.parseDouble(normalValue)== 0.0d) && !cell2.isEmpty()) {
							super.setForeground(Color.BLACK);
							super.setBackground(Color.RED);
						}else{
							super.setForeground(Color.BLACK);
							super.setBackground(normalColorNoSelected);
						}
					}
				}else if (tmpType.equals(overtimeType)) {
					if (isSelected) {
						super.setForeground(Color.BLACK);
						super.setBackground(overtimeColorSelected);
					}else{
						super.setForeground(Color.BLACK);
						super.setBackground(overtimeColorNoSelected);
					}
				}else if (tmpType.equals(weekendType)) {
					if (isSelected) {
						super.setForeground(Color.BLACK);
						super.setBackground(weekendColorSelected);
					}else{
						super.setForeground(Color.BLACK);
						super.setBackground(weekendColorNoSelected);
					}
				}else if (tmpType.equals(holidayType)) {
					if (isSelected) {
						super.setForeground(Color.BLACK);
						super.setBackground(holidayColorSelected);
					}else{
						super.setForeground(Color.BLACK);
						super.setBackground(holidayColorNoSelected);
					}
				}
			}else{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		}
		rColor.put(row+","+column, super.getBackground());
		if (passMap.get(row) != null && passMap.get(row)== true ) {
			super.setForeground(Color.LIGHT_GRAY);
			super.setFont(table.getFont().deriveFont(Font.BOLD));
		}
		setValue(value);
		return this;
	}

}
