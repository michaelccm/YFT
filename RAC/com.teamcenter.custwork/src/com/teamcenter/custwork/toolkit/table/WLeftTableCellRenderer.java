package com.teamcenter.custwork.toolkit.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.teamcenter.rac.common.table.AbstractTCTableCellRenderer;
import com.teamcenter.rac.kernel.TCComponent;


import com.teamcenter.custwork.workmanager.ManHourObject;

public class WLeftTableCellRenderer implements TableCellRenderer{

	public static final DefaultTableCellRenderer defaultCellRenderer =new DefaultTableCellRenderer();

	@Override
	public Component getTableCellRendererComponent(
			JTable jtable, Object obj,boolean isSelected, boolean hasFocus, int i, int j) 
	{
	
		String cloumnName = jtable.getColumnName(j);
	    if(cloumnName.equals("No")){
	    	obj = Integer.toString(i+1);
	    	
	    }
		
		Component component = defaultCellRenderer.getTableCellRendererComponent(jtable, obj, isSelected, hasFocus, i, j);
                if (j == 0)
                {
                   component.setFont (new Font("Arial", Font.BOLD, 14));
                }
                else
                {
                   component.setFont (new Font("Arial", Font.PLAIN, 14));
                }

		if(jtable.getRowCount() == i+1){
			component.setForeground(Color.BLACK);
			component.setBackground(ManHourObject.totalCellBackColor);
		}else{ 
			component.setForeground(Color.black);
			if(j == 3){
				component.setForeground(Color.BLACK);
				component.setBackground(ManHourObject.nColor);			
			}else if(j == 4){
				component.setForeground(Color.BLACK);
				component.setBackground(ManHourObject.oColor);
			}else if(j == 5){
				component.setForeground(Color.BLACK);
				component.setBackground(ManHourObject.wColor);
			}else if(j == 6){
				component.setForeground(Color.BLACK);
				component.setBackground(ManHourObject.hColor);
			}else{
				component.setForeground(Color.BLACK);
				component.setBackground(ManHourObject.totalCellBackColor);			
			}			
			if(isSelected){
				if (j == 0 || j == 1 || j ==2)
				{
					component.setBackground(ManHourObject.selectedCellBackColor);
				}
			}
		}
		
		defaultCellRenderer.setHorizontalAlignment(JLabel.CENTER); 
		return component;
	}






}
