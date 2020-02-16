package com.teamcenter.custwork.toolkit.component;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import com.teamcenter.rac.common.table.AbstractTCTableCellRenderer;
import com.teamcenter.rac.kernel.TCComponent;

public class STMSTCTableCheckCellRenderer extends AbstractTCTableCellRenderer {


	@Override
	public Component getTableCellRendererComponent(JTable jtable, Object obj,
			boolean flag, boolean flag1, int i, int j) {
		JCheckBox check = new JCheckBox();
		check.setBackground(Color.WHITE);
		if(obj != null && obj instanceof Boolean){
			check.setSelected((Boolean) obj);
		}
		if(flag){
			check.setForeground(jtable.getSelectionForeground());
			check.setBackground(jtable.getSelectionBackground());
		}else{
			check.setForeground(jtable.getForeground());
			check.setBackground(i % 2 != 1 ? jtable.getBackground() : alternateBackground);
		}	
		check.setHorizontalAlignment(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		return check;

	}


	@Override
	protected Icon getDisplayIcon(TCComponent tccomponent, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void initiateIcons() {
		// TODO Auto-generated method stub

	}



}
