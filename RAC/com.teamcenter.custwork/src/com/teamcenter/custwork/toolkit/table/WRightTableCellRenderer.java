package com.teamcenter.custwork.toolkit.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.teamcenter.custwork.workmanager.ManHourObject;
import com.teamcenter.custwork.workmanager.WorkManagerDialog;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourProgram;

import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourMonthTmp;


public class WRightTableCellRenderer implements TableCellRenderer{
	
	private Color mhBgColor_ = ManHourObject.defaultBackColor;
	private Color mhFgColor_ = ManHourObject.defaultForeColor;

	private WorkManagerDialog wrkMgr_ = null;

	public static final DefaultTableCellRenderer defaultCellRenderer =new DefaultTableCellRenderer();

	
	public void setWorkManager (WorkManagerDialog wrk_mgr)
        {
           wrkMgr_ = wrk_mgr;
        }


	public void setManHourBgColor (Color bg_color)
	{
	   mhBgColor_ = bg_color;
	}
	
	
	public void setManHourFgColor (Color fg_color)
	{
	   mhFgColor_ = fg_color;
	}
	
	
	@Override
	public Component getTableCellRendererComponent(
            JTable jtable, Object obj,boolean isSelected, boolean hasFocus, int i, int j) 
	{
	
            String cloumnName = jtable.getColumnName(j);
	    if(cloumnName.equals("No"))
	    {
	       obj = Integer.toString(i+1);
	    }
		
            Component component = defaultCellRenderer.getTableCellRendererComponent(jtable, obj, isSelected, hasFocus, i, j);
            if (obj != null && obj.toString().length() > 6)
            {
               component.setFont (new Font("Arial", Font.PLAIN, 12));
            }
            else 
            {
               component.setFont (new Font("Arial", Font.PLAIN, 14));
            }
       
            if(jtable.getRowCount() == i+1)
	    {
               component.setForeground(Color.BLACK);
               component.setBackground(ManHourObject.totalCellBackColor);
            }
	    else
	    { 
               component.setForeground(mhFgColor_);
               component.setBackground(mhBgColor_);


           if (wrkMgr_ != null)
           {
              if (wrkMgr_.mhInfo_ != null)
              {
                 ManHourMonthTmp[] monthTmp = wrkMgr_.mhInfo_.theMonthTmp;

		         if (monthTmp[j].isHoliday)
                 {
                    component.setBackground(ManHourObject.holidayBackColor);
                 }

                 if (monthTmp[j].isWorkRequired)
                 {
                    component.setBackground(ManHourObject.workRequiredBackColor);
                 }
              }
              
              ManHourProgram mhPrg = wrkMgr_.getManHourProgramByRow(i);
              if (mhPrg != null)
              {
                 if (j < mhPrg.startDay - 1 || j> mhPrg.endDay -1 )
                 {
		            component.setBackground(ManHourObject.noProgramDayBackColor);    // the color for project startDay and endDay.
                 }
              }
	   }
		              
	   if (obj!=null)
	   {
		   
              if (ManHourObject.isMe(obj))
              {
                 ManHourObject mhObj = (ManHourObject)obj;
		      
                 if (mhObj.getTseStatus().equals ("Released") || mhObj.getTseStatus().equals ("Released:Released") || mhObj.getTseStatus().equals (":Released" ))
                 {
                    component.setBackground(ManHourObject.releasedStatusBackColor);
                 }
                 else if (mhObj.getTseStatus().contains ("Failed") || mhObj.getTseStatus().contains ("Failed:Failed") || mhObj.getTseStatus().contains (":Failed") )
                 {
                    component.setBackground(ManHourObject.failedStatusBackColor);
                 }
                 else if (mhObj.getTseStatus().equals ("Working") || mhObj.getTseStatus().equals ("Working:Working") || mhObj.getTseStatus().equals (":Working"))
                 {
                    component.setBackground(ManHourObject.workRequiredBackColor);
                 }

              }
           }
			
           if(jtable.isCellSelected(i, j))
           {
              component.setBackground(ManHourObject.selectedCellBackColor);
           }
           if(hasFocus)
           {
              component.setBackground(ManHourObject.selectedCellBackColor);
           }
           if(isSelected)
           {
              component.setForeground(Color.RED);
	       }
	   }
			
	   defaultCellRenderer.setHorizontalAlignment(JLabel.CENTER); 
		
  	   return component;
      }

}
