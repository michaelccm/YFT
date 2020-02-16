package com.teamcenter.custwork.toolkit.component;
import java.util.HashMap;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCSession;
public class LDTCTable extends TCTable {

	private String[] editCols = null;
	private HashMap tiptxtMap = null;
	public LDTCTable(TCSession session, String[] as,String[] editCols,HashMap tiptxtMap) {
		super(session, as);
		this.editCols = editCols;
		this.tiptxtMap = tiptxtMap;
	}

	@Override
	public boolean isCellEditable(int i, int j) {
		if(editCols != null && editCols.length>0){		
			boolean flag = false;
			for(int n=0;n<editCols.length;n++){			
				if(editCols[n]!=null && getColumnIndex(editCols[n])== j){
					flag = true;
					break;
				}
			}
			return flag;
		}else{
			return super.isCellEditable(i, j);		
		}			
	}

	/*@Override
	protected void initialize() {
		setAutoResizeMode(0);
		ascendingIcon = new UpDownArrow(0);
		descendingIcon = new UpDownArrow(1);
		JTableHeader jtableheader = new JTableHeader(getColumnModel()) {

			public String getToolTipText(MouseEvent mouseevent)
			{
				int i = columnAtPoint(mouseevent.getPoint());
				int j = convertColumnIndexToModel(i);
				if(j >= 0)
				{
				//	int k = getColumnModel().getColumn(j).getWidth();
					String s = getColumnName(i);
					//if(s != null && k < getFontMetrics(getFont()).stringWidth(s) + 4)
					
					if(s != null && tiptxtMap != null){
						Object titps = tiptxtMap.get(s);
						return titps != null?titps.toString():null;
					}						
				}
				return null;
			}
			protected void processMouseEvent(MouseEvent mouseevent)
			{
				if(!SwingUtilities.isLeftMouseButton(mouseevent))
					super.setDraggedColumn(null);
				super.processMouseEvent(mouseevent);
			}


		};
		setTableHeader(jtableheader);
		registerServiceListener();
		int i = getRowHeight() + 1;
		setRowHeight(i);
		addMouseListener(getPopTriggerAdapter());
	}
*/





}
