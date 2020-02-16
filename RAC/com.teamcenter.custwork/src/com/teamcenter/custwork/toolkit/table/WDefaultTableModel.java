package com.teamcenter.custwork.toolkit.table;
import javax.swing.table.DefaultTableModel;
public class WDefaultTableModel extends DefaultTableModel {

	public WDefaultTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if(row+1 == this.getRowCount()){
			return false;
		}
		if(column == 0 || column == 3 || column == 4 ||column == 5 || column == 6){
			return false;
		}else{
			return true;
		}
		
		
	}
	

	
}
