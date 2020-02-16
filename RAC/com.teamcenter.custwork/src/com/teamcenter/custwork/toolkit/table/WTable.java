package com.teamcenter.custwork.toolkit.table;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.teamcenter.custwork.workmanager.ManHourObject;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourInfo;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourMonthTmp;

import java.awt.Color;

public class WTable extends JTable{

	private String[] titletiptext = null;

	public WTable(TableModel dm,String[] titletiptext) {
		super(dm);
		this.titletiptext = titletiptext;
	}
	
	public WTable(TableModel dm) {
		super(dm);
	}
	
	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new GroupableTableHeader(columnModel) {   
            public String getToolTipText(MouseEvent e) {   
                String tip = null;   
                java.awt.Point p = e.getPoint();   
                int index = columnModel.getColumnIndexAtX(p.x);   
                int realIndex =  columnModel.getColumn(index).getModelIndex();   
                if(titletiptext != null && titletiptext.length>realIndex){
                	return titletiptext[realIndex];   
                }else{
                	return "";
                }
               
            }   
        };   
   
	}
	
	public void addGroupColumnHeaderList(List<GroupHeader> goupColumnHeaderList, ManHourInfo mhinfo){
		GroupableTableHeader header = (GroupableTableHeader)getTableHeader();
		TableColumnModel tableColumnModel = getColumnModel();
		for(GroupHeader groupHeader:goupColumnHeaderList){
			header.addColumnGroup(groupHeader.createColumnGroup(tableColumnModel));
		}		
		for (int i = 0; i < tableColumnModel.getColumnCount(); i++) 
		{
                   Color cc = null;
		   if (mhinfo != null && mhinfo.theMonthTmp.length != 0)
                   {
                      if (mhinfo.theMonthTmp[i].isHoliday)
                      {
                         cc =  ManHourObject.holidayBackColor;
                      }
		      else if (mhinfo.theMonthTmp[i].isWeekend)
                      {
                         cc = ManHourObject.weekendBackColor;
                      }
                      else if (!mhinfo.theMonthTmp[i].isWorkRequired)
                      {
                         cc= ManHourObject.noWorkingDayBackColor;
                      }
                   }

		   tableColumnModel.getColumn(i).setHeaderRenderer(new GroupRenderer(cc));
		}
		getTableHeader().setUI(new GroupableTableHeaderUI(getTableHeader()));
	}
	
	public void setWidths(String[] widths){
		int clo = getColumnCount();
		for(int i=0;i<clo;i++){
			//getColumnModel().getColumn(i).setMinWidth(Integer.parseInt(widths[i]));
			getColumnModel().getColumn(i).setMaxWidth(Integer.parseInt(widths[i]));
		}
	}
	
	
	public void setWidthsByHashMap(HashMap widMap){
		int clo = getColumnCount();
		for(int i=0;i<clo;i++){
			String cloumnName = this.getColumnName(i);
			Object objWid = widMap.get(cloumnName);
			if(objWid != null){
				int wid = (Integer) objWid;
				getColumnModel().getColumn(i).setMaxWidth(wid);
			}
			
			//getColumnModel().getColumn(i).setMinWidth(Integer.parseInt(widths[i]));
			
		}
	}

	
	// buildNewMHEObject
        private Object buildNewMHEObject (ManHourObject old_value, ManHourObject value)
        {
           String btStr = value.getBillType ();
	   String wrkHrs = value.getWorkingHours();
	   String status = value.getTseStatus();
	   String tseref = value.getTseRef ();
	   String mheref = value.getMheRef ();

           if (old_value == null)
           {
              if (wrkHrs == null || wrkHrs.length() == 0)
              {
                 return null;
              }
              else
              {
                 if (btStr.equals (ManHourObject.nBillType))
                 {
                    return (Object) value;
                 }
                 else
                 {
	    	    String wrkHrs_new = ":" + wrkHrs;
	    	    String btStr_new = ":" + btStr;
	    	    String status_new = ":" + status;
		        String tseref_new = ":" + tseref;
		        String mheref_new = ":" + mheref;
                    ManHourObject value_new = new ManHourObject(wrkHrs_new);
                    value_new.setBillType(btStr_new);
                    value_new.setTseStatus(status_new);
		    value_new.setTseRef (tseref_new);
		    value_new.setMheRef (mheref_new);
		    return (Object) value_new;
                 }
              }
           }
           else
           {
              String [] btStrSet_old = old_value.getBillType().split("\\:");
	          String [] wrkHrsSet_old = old_value.getWorkingHours().split("\\:");
	          String [] tseStatusSet_old = old_value.getTseStatus().split("\\:");
	          String [] tseRefSet_old = old_value.getTseRef().split("\\:");
	          String [] mheRefSet_old = old_value.getMheRef().split("\\:");

	      if (btStrSet_old.length == 2)     // yyy:YYY  or :YYY
              {
                 String wrkHrs0_old = wrkHrsSet_old[0];
		 String wrkHrs1_old = wrkHrsSet_old[1];
	         String btStr0_old = btStrSet_old[0];
		 String btStr1_old = btStrSet_old[1];
		 String tseStatus0_old = tseStatusSet_old[0];
		 String tseStatus1_old = tseStatusSet_old[1];
		 String tseRef0_old = tseRefSet_old[0];
		 String tseRef1_old = tseRefSet_old[1];
		 String mheRef0_old = mheRefSet_old[0];
		 String mheRef1_old = mheRefSet_old[1];

		 if (btStr.equals (ManHourObject.nBillType))     //XXX
                 {
                    if (btStr0_old == null || btStr0_old.length() == 0)   //:YYY
                    {
		       if (wrkHrs == null || wrkHrs.length() == 0)  //000
                       {
		          String wrkHrs_new = ":" + wrkHrs1_old;
		          String btStr_new = ":" + btStr1_old;
		          String tseStatus_new = ":" + tseStatus1_old;
		          String tseRef_new = ":" + tseRef1_old;
		          String mheRef_new = ":" + mheRef1_old;

		          ManHourObject value_new = new ManHourObject (wrkHrs_new);
		          value_new.setBillType (btStr_new);
		          value_new.setTseStatus(tseStatus_new);
		          value_new.setTseRef(tseRef_new);
		          value_new.setMheRef(mheRef_new);
		          return (Object) value_new; 
		       }
		       else  //XXX
                       {
                          String wrkHrs_new = wrkHrs + ":" + wrkHrs1_old;
		          String btStr_new = btStr +  ":" + btStr1_old;
		          String tseStatus_new = status + ":" + tseStatus1_old;
		          String tseRef_new = tseref + ":" + tseRef1_old;
		          String mheRef_new = mheref + ":" + mheRef1_old;

		          ManHourObject value_new = new ManHourObject (wrkHrs_new);
   		          value_new.setBillType (btStr_new);
		          value_new.setTseStatus(tseStatus_new);
		          value_new.setTseRef(tseRef_new);
		          value_new.setMheRef(mheRef_new);
		          return (Object) value_new; 
                       }
                    }
		    else    // yyy:YYY
                    {
		       if (wrkHrs == null || wrkHrs.length() == 0)   //000
                       {
		          String wrkHrs_new = ":" + wrkHrs1_old;
		          String btStr_new = ":" + btStr1_old;
		          String tseStatus_new = ":" + tseStatus1_old;
		          String tseRef_new = ":" + tseRef1_old;
		          String mheRef_new = ":" + mheRef1_old;

		          ManHourObject value_new = new ManHourObject (wrkHrs_new);
		          value_new.setBillType (btStr_new);
		          value_new.setTseStatus(tseStatus_new);
		          value_new.setTseRef(tseRef_new);
		          value_new.setMheRef(mheRef_new);
		          return (Object) value_new; 
		       }
		       else //XXX
                       {
                          String wrkHrs_new = wrkHrs + ":" + wrkHrs1_old;
		          String btStr_new = btStr +  ":" + btStr1_old;
		          String tseStatus_new = status + ":" + tseStatus1_old;
		          String tseRef_new = tseref + ":" + tseRef1_old;
		          String mheRef_new = mheref + ":" + mheRef1_old;

		          ManHourObject value_new = new ManHourObject (wrkHrs_new);
   		          value_new.setBillType (btStr_new);
		          value_new.setTseStatus(tseStatus_new);
		          value_new.setTseRef(tseRef_new);
		          value_new.setMheRef(mheRef_new);
		          return (Object) value_new; 
                       }
                    }
                 }
                 else    // :XXX
                 {
                    if (btStr0_old == null || btStr0_old.length() == 0)   //:YYY
                    {
		       if (wrkHrs == null || wrkHrs.length() == 0)    //:000
                       {
   		          return null;
		       }
		       else
                       {
                          String wrkHrs_new = wrkHrs0_old + ":" + wrkHrs;
		          String btStr_new = btStr0_old + ":" + btStr;
		          String tseStatus_new = tseStatus0_old + ":" + status;
		          String tseRef_new = tseRef0_old + ":" + tseref;
		          String mheRef_new = mheRef0_old + ":" + mheref;

		          ManHourObject value_new = new ManHourObject (wrkHrs_new);
		          value_new.setBillType (btStr_new);
		          value_new.setTseStatus(tseStatus_new);
		          value_new.setTseRef(tseRef_new);
		          value_new.setMheRef(mheRef_new);
		          return (Object) value_new; 
                       }
                    }
                    else  //yyy:YYY
                    {
		       if (wrkHrs == null || wrkHrs.length() == 0)  //:000
                       {
		          String wrkHrs_new = wrkHrs0_old;
                          String btStr_new = btStr0_old;
		          String tseStatus_new = tseStatus0_old;
		          String tseRef_new = tseRef0_old;
		          String mheRef_new = mheRef0_old;

		          ManHourObject value_new = new ManHourObject (wrkHrs_new);
		          value_new.setBillType (btStr_new);
		          value_new.setTseStatus(tseStatus_new);
		          value_new.setTseRef(tseRef_new);
		          value_new.setMheRef(mheRef_new);
   		          return (Object) value_new; 
		       }
		       else //:XXX
                       {
                          String wrkHrs_new = wrkHrs0_old + ":" + wrkHrs;
		          String btStr_new = btStr0_old + ":" + btStr;
		          String tseStatus_new = tseStatus0_old + ":" + status;
		          String tseRef_new = tseRef0_old + ":" + tseref;
		          String mheRef_new = mheRef0_old + ":" + mheref;

		          ManHourObject value_new = new ManHourObject (wrkHrs_new);
		          value_new.setBillType (btStr_new);
		          value_new.setTseStatus(tseStatus_new);
		          value_new.setTseRef(tseRef_new);
		          value_new.setMheRef(mheRef_new);
		          return (Object) value_new; 
                       }
                    } 
		 }
              }                 
	      else  // set length =1  //yyy
	      {
                 String wrkHrs0_old = wrkHrsSet_old[0];
	         String btStr0_old = btStrSet_old[0];
		 String tseStatus0_old = tseStatusSet_old[0];
		 String tseRef0_old = tseRefSet_old[0];
		 String mheRef0_old = mheRefSet_old[0];

		 if (btStr.equals (ManHourObject.nBillType))  //XXX
                 {
		    if (wrkHrs == null || wrkHrs.length() == 0) //000
                    {
		       return null;
		    }
		    else //XXX
                    {
                       String wrkHrs_new = wrkHrs;
		       String btStr_new = btStr;
		       String tseStatus_new = status;
		       String tseRef_new = tseref;
		       String mheRef_new = mheref;

		       ManHourObject value_new = new ManHourObject (wrkHrs_new);
		       value_new.setBillType (btStr_new);
		       value_new.setTseStatus(tseStatus_new);
		       value_new.setTseRef(tseRef_new);
		       value_new.setMheRef(mheRef_new);
		       return (Object) value_new; 
                    }
		 }
                 else //:XXX
                 {
		    if (wrkHrs == null || wrkHrs.length() == 0)  //:000
                    {
		       return old_value;
		    }
		    else //:XXX
                    {
                       String wrkHrs_new = wrkHrs0_old + ":" + wrkHrs;
		       String btStr_new = btStr0_old + ":" + btStr;
		       String tseStatus_new = tseStatus0_old + ":" + status;
		       String tseRef_new = tseRef0_old + ":" + tseref;
		       String mheRef_new = mheRef0_old + ":" + mheref;

		       ManHourObject value_new = new ManHourObject (wrkHrs_new);
		       value_new.setBillType (btStr_new);
		       value_new.setTseStatus(tseStatus_new);
		       value_new.setTseRef(tseRef_new);
		       value_new.setMheRef(mheRef_new);
		       return (Object) value_new; 
                    }
		 }
              }
           }
        }


	// setValueAt
	public void setValueAt (Object value, int row, int col)
        {

         try
           {
              if (value == null)
              {
                 super.setValueAt (value, row, col);
		 return;
              }

              String classType = value.getClass().toString ();
	      if (!classType.equals ("class com.teamcenter.custwork.workmanager.ManHourObject"))
              {
                 super.setValueAt (value, row, col);
		 return;
              }

              // current value
	      ManHourObject mhValue = (ManHourObject) value;
	      String btStr = mhValue.getBillType ();
	      String wrkHrs = mhValue.getWorkingHours();
              if (btStr.equals (ManHourObject.wBillType) || btStr.equals (ManHourObject.hBillType))
              {
                 if (wrkHrs != null && wrkHrs.length() != 0)
                 {
                    super.setValueAt (value, row, col);
                 }
		 else
                 {
                   super.setValueAt (null, row, col);
                 }
		 return;
              }

	      // old value
	      Object value_old = getValueAt (row, col);
	      if (value_old == null)
              {
                 super.setValueAt (buildNewMHEObject (null, mhValue), row, col);
		 return;
              }
         
	      String classType_old = value_old.getClass().toString ();
	      if (!classType_old.equals ("class com.teamcenter.custwork.workmanager.ManHourObject"))
              {
                 super.setValueAt (buildNewMHEObject (null, mhValue), row, col);
		 return;
              }

              ManHourObject mhValue_old = (ManHourObject) value_old;
              String wrkHours_old = mhValue_old.getWorkingHours();
	      String btStr_old = mhValue_old.getBillType ();
	      if (btStr_old.equals (ManHourObject.wBillType) || btStr_old.equals (ManHourObject.hBillType))
              {
                 super.setValueAt (buildNewMHEObject (null, mhValue), row, col);
		 return;
              }

	      super.setValueAt (buildNewMHEObject (mhValue_old, mhValue), row, col);
	      return;
           }
	   catch (Exception e)
           {
           }
        }
}
