package com.yfjcebp.extsupport;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextFieldEditor extends DefaultCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String oldValue;

	public TextFieldEditor() {
		super(new JTextField());
		final JTextField textArea = new JTextField();
		editorComponent = textArea;
		textArea.setDocument(new DoubleDocument());
		delegate = new DefaultCellEditor.EditorDelegate() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void setValue(Object value) {
				oldValue = value.toString();
				textArea.setText((value != null) ? value.toString().substring(
						0, value.toString().length() - 1) : "");
			}

			@Override
			public Object getCellEditorValue() {
				return textArea.getText().equals("") ?  "0%" : textArea.getText()+"%";
			}
		};
	}

}

class DoubleDocument extends PlainDocument {
	private static final long serialVersionUID = 1L;

	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		// 若字符串为空，直接返回。
		if (str == null) {
			return;
		}
		int len = getLength();
		String s = getText(0, len);// 文本框已有的字符
		try {
			s = s.substring(0, offs) + str + s.substring(offs, len);// 在已有的字符后添加字符
			double i = Double.parseDouble(s); // 只能为正整数
			if (i > 100 || i < 0) { // 限制范围
				throw new Exception();
			}
		} catch (Exception e) {
			return;
		}
		super.insertString(offs, str, a);// 把字符添加到文本框
	}
}
