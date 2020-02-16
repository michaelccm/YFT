package com.teamcenter.custwork.toolkit.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.ComboBoxEditor;

import java.io.Serializable;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.table.TableCellEditor;
 

// ylong 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.teamcenter.custwork.workmanager.WorkManagerDialog;



public class WComboBoxEditor extends AbstractCellEditor implements ActionListener, TableCellEditor, Serializable
{
   private JComboBox comboBox;

   static private int row_ = -1;
   static private int col_ = -1;

   WorkManagerDialog wrkMgr_ = null;
   boolean stopEdit_ = true;


   public void setWorkManager (WorkManagerDialog wrk_mgr)
   {
      wrkMgr_ = wrk_mgr;
   } 

	
   static public int getCurRow ()
   {
      return row_;
   }
	
	
   static public int getCurCol()
   {
      return col_;
   }
   
   
   public WComboBoxEditor (JComboBox comboBox, boolean stop_edit) 
   {
//    super ();
      this.comboBox = comboBox;
      stopEdit_ = stop_edit;

      this.comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
      // hitting enter in the combo box should stop cellediting (see below)
      this.comboBox.addActionListener(this);
   }
   

   private void setValue(Object value) 
   {
      comboBox.setSelectedItem(value);
   }
  

   @Override
   public void actionPerformed(java.awt.event.ActionEvent e) 
   {
      // Selecting an item results in an actioncommand "comboBoxChanged".
      // We should ignore these ones.

	  //stopCellEditing();
      // Hitting enter results in an actioncommand "comboBoxEdited"
      //if(e.getActionCommand().equals("comboBoxEdited")) 
      if (stopEdit_)
      {
         stopCellEditing();
      }
      else
      {
	     if (e.getModifiers() == 16)
         {
            stopCellEditing();
         }
      }
   }
   

   // Implementing CellEditor
   @Override
   public Object getCellEditorValue() 
   {
      return comboBox.getSelectedItem();
   }
    
   @Override
   public boolean stopCellEditing() 
   {
      if (comboBox.isEditable()) 
      {
         // Notify the combo box that editing has stopped (e.g. User pressed F2)
         comboBox.actionPerformed(new ActionEvent(this, 0, ""));
      }
      fireEditingStopped();
      return true;
   }
   

   @Override
   public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) 
   {
          
      row_ = row;
      col_ = column;

      if (column == 2)
      {
         comboBox.removeAllItems();
     	 Object obj = table.getValueAt(row, column-1);
     	    
         if(obj != null && obj.toString().trim().length()>0)
         {
            // ylong 
            if (wrkMgr_ != null && 
                wrkMgr_.pMap_ != null &&
                !wrkMgr_.pMap_.isEmpty())
            {
               String keyStr = obj.toString();

               ArrayList <String> ttList = wrkMgr_.pMap_.get(keyStr);
               if (ttList != null && ttList.size () > 0)
               {
                  Collections.sort(ttList);  
                  for (int j = 0; j < ttList.size(); j++) 
                  {  
                     String ttStr = ttList.get(j); 
                     comboBox.addItem(ttStr);
                  }
               }      
            }
         }
      }

      setValue(value);
      return comboBox;
   }
}
   
   



