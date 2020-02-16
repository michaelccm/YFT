package com.teamcenter.custwork.toolkit.component;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JCheckBox;

public class LDComboBoxEditor implements ComboBoxEditor {
	JCheckBox jc = null;
	
	public LDComboBoxEditor(JCheckBox jc){
		this.jc = jc;
	}

	@Override
	public Component getEditorComponent() {
		// TODO Auto-generated method stub
		return jc;
	}

	@Override
	public void setItem(Object anObject) {
		System.out.println(anObject);

	}

	@Override
	public Object getItem() {
		// TODO Auto-generated method stub
		return this.getEditorComponent();
	}

	@Override
	public void selectAll() {
		jc.setSelected(true);

	}

	@Override
	public void addActionListener(ActionListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeActionListener(ActionListener l) {
		// TODO Auto-generated method stub

	}

}
