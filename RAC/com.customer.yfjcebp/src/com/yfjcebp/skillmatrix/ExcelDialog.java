/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		  
 #																			   
 #=============================================================================
 # File name: ExcelDialog.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-11	wuh  		Ini		初始化								   
 #=============================================================================
 */
package com.yfjcebp.skillmatrix;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.Registry;


/**
 * 使用swt组件做的界面 To use swt components to do the interface.
 * 
 * @author wuhui
 * 
 */
public class ExcelDialog extends AbstractAIFDialog {

	private static final long serialVersionUID = 1L;
	private String parsePath;
	private String s1;
	private DeviceData deviceData;
	private Display display;
	private StyledText styleText;
	private Shell shell;
	private String warnmessage = "";
	private String errmessage = "";
	private String warningMessage;// 警告信息（黄色字体）
	private String errorMessage;// 错误信息（红色字体）
	private Registry reg = Registry.getRegistry(ExcelDialog.class);

	public StyledText getStyleText() {
		return styleText;
	}

	public void setStyleText(StyledText styleText) {
		this.styleText = styleText;
	}

	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public ExcelDialog(String parsePath, String errorMessage,
			String errmessage, String warningMessage, String warnmessage) {
		this.parsePath = parsePath;
		this.errorMessage = errorMessage;
		this.errmessage = errmessage;
		this.warningMessage = warningMessage;
		this.warnmessage = warnmessage;
		init();
	}

	// 初始化
	private void init() {
		deviceData = new DeviceData();
		display = new Display(deviceData);
		shell = new Shell(display, SWT.DIALOG_TRIM);
		shell.setText(reg.getString("megtext"));//"导入信息错误"
		styleText = new StyledText(shell, SWT.READ_ONLY | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		styleText.setBounds(10, 5, 340, 220);
		s1 = reg.getString("nowimport") + "" + parsePath
				+ reg.getString("file.ColumnName") + "\n" + "\n";//目前导入的是：  文件
		styleText.append(s1);
		setMessage();
		Button okBtton = new Button(shell, SWT.PUSH);
		okBtton.setText(reg.getString("okbut.LABEL"));//确定（O）
		okBtton.setBounds(80, 230, 75, 30);
		okBtton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}

		});
		okBtton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// 按下O键时
				if (e.keyCode == 111) {
					// 让按键原有的功能失效
					e.doit = false;

				}
			}
		});
		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(reg.getString("cancelbut.LABEL"));//取消
		cancelButton.setBounds(180, 230, 75, 30);// 设置按钮显示位置及宽度、高度
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}

		});
		cancelButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// 按下C键时
				if (e.keyCode == 99) {
					// 让按键原有的功能失效
					e.doit = false;
					// 执行自定义的事件，关闭shell
					shell.dispose();
				}
			}
		});
		shell.setSize(400, 300);
		if (!errmessage.equals("") || !warnmessage.equals("")) {
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			// 当用户关闭窗口时，释放display占用的内存资源
			display.dispose();
		}
	}

	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	/**
	 * 取文字颜色对象 Take the text color object
	 * 
	 * @param startOffset
	 * @param length
	 * @param color
	 * @return
	 */
	public StyleRange getColorStyle(int startOffset, int length, Color color) {
		StyleRange styleRange = new StyleRange(startOffset, length, color, null);
		return styleRange;
	}

	/**
	 * 显示错误信息
	 */
	public void setMessage() {
		if (!errmessage.equals("")) {
			getStyleText().append(errorMessage);
			getStyleText().setStyleRange(
					getColorStyle(
							getS1().length(),
							errorMessage.length(),
							getShell().getDisplay().getSystemColor(
									SWT.COLOR_RED)));

			if (!getStyleText().isDisposed()) {
				getStyleText().append(errmessage);
			}
		}
		if (!warnmessage.equals("")) {
			if (!getStyleText().isDisposed()) {
				int a = getStyleText().getText().length();
				Color color = getShell().getDisplay().getSystemColor(
						SWT.COLOR_YELLOW);
				getStyleText().append(warningMessage);
				StyleRange styleRange = getColorStyle(a,
						warningMessage.length(), color);
				getStyleText().setStyleRange(styleRange);
				getStyleText().append(warnmessage);
			}
		}
	}
}
