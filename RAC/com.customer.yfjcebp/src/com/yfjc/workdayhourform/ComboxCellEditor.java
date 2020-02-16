package com.yfjc.workdayhourform;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import com.teamcenter.rac.util.combobox.iComboBox;

public class ComboxCellEditor extends AbstractCellEditor implements TableCellEditor {

	private JComponent combo;

	private Vector v;


	public ComboxCellEditor(Vector v,int column) {
		this.v = v;
		if (column == 1) {
			combo = new iComboBox(v);
			((iComboBox) combo).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					fireEditingStopped();	
				}
			});
		} else {
			combo = new JComboBox(v);
			((JComboBox) combo).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					fireEditingStopped();
				}
			});
		}

	}

	@Override
	public Object getCellEditorValue() {
		String s = null;
		if (combo instanceof JComboBox) {
			JComboBox com = (JComboBox) combo;
			//System.out.println("  combox   count  " + com.getItemCount());
			if (com.getSelectedItem() != null) {
				s = com.getSelectedItem().toString();
			} else {
				s = "";
			}
		} else if (combo instanceof iComboBox) {
			iComboBox com = (iComboBox) combo;
			//System.out.println("  combox   count  " + com.getItemCount());
			if (com.getSelectedItem() != null) {
				s = com.getSelectedItem().toString();
			} else {
				s = "";
			}
		}

		return s;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (combo instanceof JComboBox) {
			JComboBox com = (JComboBox) combo;
			if (com.getItemCount() == 0) {
				return null;
			}
			String s = value == null ? "" : value.toString();
	        if(!s.equals("")){
	        	com.setSelectedItem(s);
	        }
			//com.setSelectedIndex(0);
		} else if (combo instanceof iComboBox) {
			iComboBox com = (iComboBox) combo;
			if (com.getItemCount() == 0) {
				return null;
			}
			String s = value == null ? "" : value.toString();
			if(!s.equals("")){
		       com.setSelectedItem(s);
		    }
			
		}

		return combo;
	}

	@Override
	public void addCellEditorListener(CellEditorListener listener) {
		listenerList.add(CellEditorListener.class, listener);

	}

	@Override
	public void removeCellEditorListener(CellEditorListener listener) {
		listenerList.remove(CellEditorListener.class, listener);
	}

	@Override
	protected void fireEditingStopped() {
		CellEditorListener listener;
		Object[] listeners = listenerList.getListenerList();
		//System.out.println(" listner.length " + listeners.length);
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == CellEditorListener.class) {
				listener = (CellEditorListener) listeners[i + 1];
				if (listener != null) {
					listener.editingStopped(changeEvent);
				}
			}
		}
	}

	@Override
	protected void fireEditingCanceled() {
		CellEditorListener listener;
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == CellEditorListener.class) {
				listener = (CellEditorListener) listeners[i + 1];
				listener.editingCanceled(changeEvent);
			}
		}
	}

	@Override
	public void cancelCellEditing() {
		fireEditingCanceled();
	}

	@Override
	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject event) {
		return true;
	}
	
	public void setItems(Vector items){
		if(combo instanceof JComboBox){
			JComboBox com = (JComboBox) combo;
			com.removeAllItems();
			for(int i=0,size=items.size();i<size;i++){
				com.addItem(items.get(i));
			}
			com.repaint();
			com.validate();
		}
	}

	
	

}
