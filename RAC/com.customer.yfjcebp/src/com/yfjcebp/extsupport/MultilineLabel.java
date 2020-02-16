package com.yfjcebp.extsupport;

import javax.swing.JTextArea;
import javax.swing.LookAndFeel;

/**
 * 一个用于显示文本，并且可以自动换行的自定义组件<br>
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

		// 设置为自动换行
		setLineWrap(true);
		setWrapStyleWord(true);
		setHighlighter(null);
		setEditable(false);
		// 设置为label的边框，颜色和字体
		LookAndFeel.installBorder(this, "Label.border");

		LookAndFeel.installColorsAndFont(this, "Label.background",
				"Label.foreground", "Label.font");
	}
}