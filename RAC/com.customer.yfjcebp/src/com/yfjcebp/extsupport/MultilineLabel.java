package com.yfjcebp.extsupport;

import javax.swing.JTextArea;
import javax.swing.LookAndFeel;

/**
 * һ��������ʾ�ı������ҿ����Զ����е��Զ������<br>
 * 
 * @author wuh
 * 
 */
public class MultilineLabel extends JTextArea {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultilineLabel(String s) {
		super(s);
	}

	@Override
	public void updateUI() {
		super.updateUI();

		// ����Ϊ�Զ�����
		setLineWrap(true);
		setWrapStyleWord(true);
		setHighlighter(null);
		setEditable(false);
		// ����Ϊlabel�ı߿���ɫ������
		LookAndFeel.installBorder(this, "Label.border");

		LookAndFeel.installColorsAndFont(this, "Label.background",
				"Label.foreground", "Label.font");
	}
}