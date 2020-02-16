package com.teamcenter.custwork.toolkit.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import com.d9.services.rac.work._2018_06.ManHourManageService;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourInfo;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourMonthTmp;

import com.teamcenter.custwork.workmanager.ManHourObject;

import java.util.List;

/**
 * 锁定列 使用两个表来实现锁定列的功能
 * 
 * @author wuqi
 * 
 */
public class WorkSheetTable {
	private Object[][] data; // 数据
	private Object[] column; // 列标题
	private WTable leftTable, rightTable;
	private int columnFixedIndex; // 冻结列的索引
	private boolean isNeedFixed = true; // 是否需要冻结
	private JScrollPane scroll;
	private JViewport viewport;
	private DefaultTableModel leftModel;
	private DefaultTableModel rightModel;
	public WorkSheetTable(Object[][] _data, Object[] _column, int _columnFixedIndex) {
		super();
		this.data = _data;
		this.column = _column;
		if (_columnFixedIndex <= 0) {
			columnFixedIndex = 0;
			isNeedFixed = false;
		} else if (_columnFixedIndex >= _column.length) {
			columnFixedIndex = 0;
			isNeedFixed = false;
		} else {
			columnFixedIndex = _columnFixedIndex;
		}
		init();
	}

	
	private void init() {
		leftModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) 
			{
				if (column == 0 || column == 3 || column == 4 ||column == 5
						|| column == 6|| row == leftModel.getRowCount() - 1) 
				{
					return false;
				}else
				{
                                   // ylong
	                           boolean isEditable = true;
	                
	                           int rowCnt =rightTable.getRowCount();
	                           int colCnt =rightTable.getColumnCount ();

	                           for (int j = 0; j < colCnt; j++)
				   {
				       Object dObj = data[row][j+7];

	                               if (dObj != null && dObj.toString() != null && dObj.toString().length() > 0)
	                               {
	                                  isEditable = false;
	                                  break;
				       }
	                           }

				   return isEditable;
				}
			}

			
			@Override
			public String getColumnName(int _column) {
				// TODO Auto-generated method stub
				return (String) column[_column];
			}

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return columnFixedIndex;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return data.length;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return data[rowIndex][columnIndex];
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				data[rowIndex][columnIndex] = aValue;
			}			
		};
		
		rightModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				data[rowIndex][columnIndex + columnFixedIndex] = aValue;
			}

			@Override
			public String getColumnName(int _column) {
				return (String) column[_column + columnFixedIndex];
			}

			@Override
			public int getColumnCount() {
				return column.length - columnFixedIndex;
			}

			@Override
			public int getRowCount() {
				return data.length;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return data[rowIndex][columnIndex + columnFixedIndex];
			}
		};
		leftTable = new WTable(leftModel) {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				super.valueChanged(e);
				checkSelection(true);
			}
		};
		
		leftTable.setPreferredSize(new Dimension (710, 760));
		rightTable = new WTable(rightModel) {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				super.valueChanged(e);
				checkSelection(false);
			}
		};
	
		rightTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scroll = new JScrollPane();
		if (isNeedFixed) {
			leftTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			rightTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			viewport = new JViewport() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					setBackground(Color.WHITE);
				}
			};
			viewport.setView(leftTable);
			viewport.setPreferredSize(new Dimension(leftTable.getPreferredSize().width,//-150,
					leftTable.getPreferredSize().height));
			scroll.setRowHeaderView(viewport);
			scroll.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, leftTable.getTableHeader());
			scroll.setViewportView(rightTable);
			
		} else {
			// 有滚动条		
			rightTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			scroll.setViewportView(rightTable);
		}
	}
	
	

	/**
	 * 检查表格的选择事件 在冻结表格中选中冻结表格中某行时，非冻结部分表格也选中相同行
	 * 在非冻结表格中选中某行时，如果与冻结表格选中索引不一致时，去掉冻结表格中的选中
	 * 
	 * @param isFixedTable
	 *            ：方法调用是否来自固定表格
	 */
	private void checkSelectionOld(boolean isFixedTable) {
		int fixedSelectedIndex = leftTable.getSelectedRow();
		int selectedIndex = rightTable.getSelectedRow();
		if (isFixedTable) {
			if (fixedSelectedIndex >= 0) {
				rightTable.setRowSelectionInterval(fixedSelectedIndex, fixedSelectedIndex);
			}
		} else {
			if (selectedIndex != fixedSelectedIndex) {
				leftTable.clearSelection();
			}
		}

	}

	private void checkSelection(boolean isFixedTable) {
		int fixedSelectedIndex = leftTable.getSelectedRow();
		int selectedIndex = rightTable.getSelectedRow();
		if (isFixedTable) {
			//if (fixedSelectedIndex >= 0) {
			//rightTable.setRowSelectionInterval(fixedSelectedIndex, fixedSelectedIndex);
			//}
			rightTable.clearSelection();
		} else {
			if (selectedIndex != fixedSelectedIndex) {
				leftTable.clearSelection();
			}
		}

	}
	
	/**
	 * 增加行
	 * @param objs
	 */
	public void addRowObjectOld(Object[] objs) {
		Object[][] obja = new Object[leftTable.getRowCount() + 1][objs.length];
		for (int i = 0; i < objs.length; i++) {
			obja[0][i] = objs[i];
		}
		for (int i = 0; i < data.length; i++) {
			obja[i + 1] = data[i];
		}
		this.data = obja;
		leftTable.validate();
		leftTable.updateUI();
		rightTable.validate();
		rightTable.updateUI();
	}
	
	public void addRowObject(Object[] objs) {
		int selectRow = leftTable.getSelectedRow();
		Object[][] obja = new Object[leftTable.getRowCount() + 1][objs.length];
		if(selectRow >-1 && selectRow != leftTable.getRowCount()-1){
			int n = 0;
			for(int i=0;i<obja.length;i++){
				if(i == selectRow+1){
					for (int j = 0; j < objs.length;j++) {
						obja[i][j] = objs[j];
					}
				}else{					
					obja[i] = data[n];
					n++;	
				}				
			}
		}else{
			int n = 0;
			for(int i=0;i<obja.length;i++){							
				if(i == leftTable.getRowCount()-1){
					for (int j = 0; j < objs.length;j++) {
						obja[i][j] = objs[j];
					}
				}else{	
					obja[i] = data[n];
					n++;				
				}				
			}
		}	
		this.data = obja;
		leftTable.validate();
		leftTable.updateUI();
		rightTable.validate();
		rightTable.updateUI();
	}
	
	/**
	 * 移除行
	 */
	public void subRow() {
	    int[] rows = leftTable.getSelectedRows();
	    ArrayList alist = new ArrayList();	  
		for (int j = 0; j < data.length; j++) {
			boolean isSeleced = false;
			for (int i = 0; i < rows.length; i++) {
				if (j == rows[i] && j != leftTable.getRowCount()-1) {
					isSeleced = true;
				}
			}
			if(!isSeleced){
				alist.add(data[j]);
			}
		}
		System.out.println(alist.size());
		Object[][] obja = new Object[alist.size()][leftTable.getColumnCount()+rightTable.getColumnCount()];
        if(!alist.isEmpty()){
            for(int n=0;n<alist.size();n++){
            	obja[n] = (Object[]) alist.get(n);
            }
        }
		this.data = obja;		
		leftTable.validate();
		leftTable.updateUI();
		rightTable.validate();
		rightTable.updateUI();
	}
	
	
	// ylong
	public void assignCellValue(List<Object> v_list, ManHourInfo mh_info)
	{
	   try
	   {
              int vCnt = (v_list != null) ? v_list.size():0;
              if (vCnt == 0)
              {
                 return;
              }
              
              int rowCnt = rightTable.getRowCount();
          
	      int[] selectColumns = rightTable.getSelectedColumns();
	      int[] selectRows = rightTable.getSelectedRows();

	      for(int i=0;i<selectRows.length;i++)
	      {
	         for(int j=0;j<selectColumns.length;j++)
	         {	
                    for (int idx = 0; idx < vCnt; idx++)
                    {
                       ManHourObject mheObj = (ManHourObject) v_list.get (idx);
                    
		       if (mheObj.getBillType().equals(ManHourObject.nBillType) ||
                           mheObj.getBillType().equals(ManHourObject.oBillType) )
		       {
                         if (selectRows[i] < rowCnt -1 && mh_info.theMonthTmp[selectColumns[j]].isWorkRequired)
                         {
                            rightTable.setValueAt(mheObj, selectRows[i], selectColumns[j]);
                         }
  	              }
                      else
                      {

                         rightTable.setValueAt(mheObj, selectRows[i], selectColumns[j]);
                      } 		
                   }
                }
             }		  
	     rightModel.fireTableDataChanged();	
	     rightTable.validate();
	     rightTable.updateUI();
	   }
	   catch (Exception e)
	   {
              e.printStackTrace();
	   }
	}


	public WTable getLeftTable() {
		return leftTable;
	}

	public WTable getRightTable() {
		return rightTable;
	}


	public JScrollPane getScroll() {
		return scroll;
	}

	public Object[][] getData() {
		return data;
	}
	
}
