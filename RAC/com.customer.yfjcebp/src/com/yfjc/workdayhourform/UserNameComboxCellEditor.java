package com.yfjc.workdayhourform;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class UserNameComboxCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox<String> combo;

	public UserNameComboxCellEditor(JComboBox<String> combo) {
		this.combo = combo;
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				fireEditingStopped();
			}
		});

	}

	@Override
	public Object getCellEditorValue() {
		String s = null;
		if (combo.getSelectedItem() != null) {
			s = combo.getSelectedItem().toString();
		} else {
			s = "";
		}

		return s;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (combo.getItemCount() == 0) {
			return null;
		}
		String s = value == null ? "" : value.toString();

		combo.setSelectedItem(s);

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

}
