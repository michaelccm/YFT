package com.yfjcebp.projectmanager.budget;

import javax.swing.table.TableModel;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.common.AIFTableLine;
import com.teamcenter.rac.common.TCTable;

public class MyTCTable extends TCTable{
	public MyAIFTableModel myModle;
	@Override
	public AIFTableLine addRow(Object paramObject) {
		// TODO Auto-generated method stub
		return myModle.addRow(paramObject);
	}
	
	@Override
	public void addRows(Object paramObject) {
		// TODO Auto-generated method stub
		//super.addRows(paramObject);
		final Object val$linesToAdd=paramObject;
		AbstractAIFOperation local3 = new AbstractAIFOperation()
	    {
	      public void executeOperation()
	        throws Exception
	      {
	    	  MyTCTable.this.myModle.addRows(val$linesToAdd);
	      }
	    };
	    addRowsInternal(local3);
	}
	
	public MyTCTable() {
		super();
		myModle=(MyAIFTableModel) this.dataModel;
		setModel(myModle);
	}
	
	@Override
	public TableModel getModel() {
		// TODO Auto-generated method stub
		
		return myModle;
	}
	
	@Override
	public void setModel(TableModel arg0) {
		// TODO Auto-generated method stub
		super.setModel(arg0);
	}
}
