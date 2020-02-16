//package com.yfjcebp.extsupport.form;
//
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.util.List;
//import java.util.Vector;
//import javax.swing.ComboBoxEditor;
//import javax.swing.ComboBoxModel;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.JComboBox;
//import javax.swing.JTextField;
//
//class AutoCompleter implements KeyListener, ItemListener {
//	private JComboBox owner = null;
//	private JTextField editor = null;
//
//	private ComboBoxModel model = null;
//
//	public AutoCompleter(JComboBox comboBox) {
//		this.owner = comboBox;
//		this.editor = ((JTextField) comboBox.getEditor().getEditorComponent());
//		this.editor.addKeyListener(this);
//		this.model = comboBox.getModel();
//		this.owner.addItemListener(this);
//	}
//
//	public void keyTyped(KeyEvent e) {
//	}
//
//	public void keyPressed(KeyEvent e) {
//	}
//
//	public void keyReleased(KeyEvent e) {
//		char ch = e.getKeyChar();
//		if ((ch == 65535) || (Character.isISOControl(ch)) || (ch == '')) {
//			return;
//		}
//
//		int caretPosition = this.editor.getCaretPosition();
//		String str = this.editor.getText();
//		if (str.length() == 0) {
//			return;
//		}
//		autoComplete(str, caretPosition);
//	}
//
//	protected void autoComplete(String strf, int caretPosition) {
//		Object[] opts = getMatchingOptions(strf.substring(0, caretPosition));
//		if (this.owner != null) {
//			this.model = new DefaultComboBoxModel(opts);
//			this.owner.setModel(this.model);
//		}
//		if (opts.length > 0) {
//			String str = opts[0].toString();
//			if (caretPosition > this.editor.getText().length())
//				return;
//			this.editor.setCaretPosition(caretPosition);
//			this.editor.setText(this.editor.getText().trim()
//					.substring(0, caretPosition));
//			if (this.owner != null)
//				try {
//					this.owner.showPopup();
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//		}
//	}
//
//	protected Object[] getMatchingOptions(String str) {
//		List v = new Vector();
//		List v1 = new Vector();
//
//		for (int k = 0; k < this.model.getSize(); k++) {
//			Object itemObj = this.model.getElementAt(k);
//			if (itemObj != null) {
//				String item = itemObj.toString().toLowerCase();
//				if (item.startsWith(str.toLowerCase())) {
//					v.add(this.model.getElementAt(k));
//				} else
//					v1.add(this.model.getElementAt(k));
//			} else {
//				v1.add(this.model.getElementAt(k));
//			}
//		}
//		for (int i = 0; i < v1.size(); i++) {
//			v.add(v1.get(i));
//		}
//		if (v.isEmpty()) {
//			v.add(str);
//		}
//		return v.toArray();
//	}
//
//	public void itemStateChanged(ItemEvent event) {
//		if (event.getStateChange() == 1) {
//			int caretPosition = this.editor.getCaretPosition();
//			if (caretPosition != -1)
//				try {
//					this.editor.moveCaretPosition(caretPosition);
//				} catch (IllegalArgumentException ex) {
//					ex.printStackTrace();
//				}
//		}
//	}
//}