package com.teamcenter.custwork.toolkit.table;


import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.Color;

class GroupRenderer extends DefaultTableCellRenderer
{
    Color hColor = null;

    GroupRenderer (Color cc)
    {
         super ();
         hColor = cc;            
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
    {
	JTableHeader header = table.getTableHeader();
	if (header != null) 
	{
           setForeground(header.getForeground());
           if (hColor == null)
	   {
              setBackground(header.getBackground());
           }
	   else
           {
              setBackground(hColor);
           }
	   //setFont(header.getFont());
           setFont (new Font("Arial", Font.PLAIN, 15));
	}
	setHorizontalAlignment(JLabel.CENTER);
	setText((value == null) ? "" : value.toString());
	setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	return this;
   }

}
