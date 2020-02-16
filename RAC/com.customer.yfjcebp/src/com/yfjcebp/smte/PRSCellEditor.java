package com.yfjcebp.smte;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;

import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

public class PRSCellEditor extends AbstractCellEditor implements
	TableCellEditor, TreeCellEditor {
	protected JTextArea editorComponent;
	protected EditorDelegate delegate;
	protected int clickCountToStart = 1;
	public PRSCellEditor(final JTextArea textArea) {
		textArea.setLineWrap(true);
		editorComponent = textArea;
		this.clickCountToStart = 2;
		delegate = new EditorDelegate() {
			@Override
			public void setValue(Object value) {
				textArea.setText((value != null) ? value.toString() : "");
			}
			@Override
			public Object getCellEditorValue() {
				return textArea.getText();
			}
		};
	}
	public Component getComponent() {
		return editorComponent;
	}
	public void setClickCountToStart(int count) {
		clickCountToStart = count;
	}
	public int getClickCountToStart() {
		return clickCountToStart;
	}
	@Override
	public Object getCellEditorValue() {
		return delegate.getCellEditorValue();
	}
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return delegate.isCellEditable(anEvent);
	}
	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return delegate.shouldSelectCell(anEvent);
	}
	@Override
	public boolean stopCellEditing() {
		return delegate.stopCellEditing();
	}
	@Override
	public void cancelCellEditing() {
		delegate.cancelCellEditing();
	}
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		String stringValue = tree.convertValueToText(value, isSelected,
				expanded, leaf, row, false);
		delegate.setValue(stringValue);
		return editorComponent;
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		delegate.setValue(value);
		  
		// 计算当下行的最佳高度
//		int maxPreferredHeight = 0;
//		for (int i = 0; i < table.getColumnCount(); i++) {
//			editorComponent.setText("" + table.getValueAt(row, i));
//			editorComponent.setSize(table.getColumnModel().getColumn(column)
//					.getWidth(), 0);
//				maxPreferredHeight = Math.max(maxPreferredHeight,
//						editorComponent.getPreferredSize().height);
//		}
//		if (table.getRowHeight(row) != maxPreferredHeight)
//		// 少了这行则处理器瞎忙
//			table.setRowHeight(row, maxPreferredHeight);
		editorComponent.setText(value == null ? "" : value.toString());
		return editorComponent;
		}
	protected class EditorDelegate implements ActionListener, ItemListener,
	   Serializable{
		   protected Object value;
		   public Object getCellEditorValue() {
			   return value;
			   }
		   public void setValue(Object value) {
			   this.value = value;
			   }
		   public boolean isCellEditable(EventObject anEvent) {
			   if (anEvent instanceof MouseEvent) {
				   return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
				   }
			   return true;
			   }
		   public boolean shouldSelectCell(EventObject anEvent) {
			   return true;
			   } 
		   public boolean startCellEditing(EventObject anEvent) {
			   return true;
			   }
		   public boolean stopCellEditing() {
			   fireEditingStopped();
			   return true;
			   }
		   public void cancelCellEditing() {
			   fireEditingCanceled();
			   }
		   @Override
		public void actionPerformed(ActionEvent e) {
			   PRSCellEditor.this.stopCellEditing();
			   }
		   @Override
		public void itemStateChanged(ItemEvent e) {
			   PRSCellEditor.this.stopCellEditing();
		   }
	   }
	   
   }

