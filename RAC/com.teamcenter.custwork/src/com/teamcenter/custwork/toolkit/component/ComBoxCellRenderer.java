package com.teamcenter.custwork.toolkit.component;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComBoxCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		System.out.println("List lsit is" + list);
		JCheckBox box = new JCheckBox();
		box.setText(value.toString());
		return box;
	}

}
