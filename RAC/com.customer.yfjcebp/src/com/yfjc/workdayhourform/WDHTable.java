package com.yfjc.workdayhourform;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class WDHTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<String> extNames;
	public WDHTable(DefaultTableModel dtm,Vector<String> extNames) {
		// TODO Auto-generated constructor stub
		super(dtm);
		this.extNames = extNames;
	}



	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 0) {
			JComboBox<String> combox = new JComboBox<String>(extNames);
			return new UserNameComboxCellEditor(combox);
		}else if(column == 3 || column == 4){
			return new WDHTextFieldEditor();
		}
		return super.getCellEditor(row, column);
	}


}
