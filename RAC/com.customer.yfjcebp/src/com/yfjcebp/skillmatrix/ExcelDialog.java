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
 #	2013-3-11	wuh  		Ini		��ʼ��								   
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
 * ʹ��swt������Ľ��� To use swt components to do the interface.
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
	private String warningMessage;// ������Ϣ����ɫ���壩
	private String errorMessage;// ������Ϣ����ɫ���壩
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

	// ��ʼ��
	private void init() {
		deviceData = new DeviceData();
		display = new Display(deviceData);
		shell = new Shell(display, SWT.DIALOG_TRIM);
		shell.setText(reg.getString("megtext"));//"������Ϣ����"
		styleText = new StyledText(shell, SWT.READ_ONLY | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		styleText.setBounds(10, 5, 340, 220);
		s1 = reg.getString("nowimport") + "" + parsePath
				+ reg.getString("file.ColumnName") + "\n" + "\n";//Ŀǰ������ǣ�  �ļ�
		styleText.append(s1);
		setMessage();
		Button okBtton = new Button(shell, SWT.PUSH);
		okBtton.setText(reg.getString("okbut.LABEL"));//ȷ����O��
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
				// ����O��ʱ
				if (e.keyCode == 111) {
					// �ð���ԭ�еĹ���ʧЧ
					e.doit = false;

				}
			}
		});
		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(reg.getString("cancelbut.LABEL"));//ȡ��
		cancelButton.setBounds(180, 230, 75, 30);// ���ð�ť��ʾλ�ü���ȡ��߶�
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}

		});
		cancelButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// ����C��ʱ
				if (e.keyCode == 99) {
					// �ð���ԭ�еĹ���ʧЧ
					e.doit = false;
					// ִ���Զ�����¼����ر�shell
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
			// ���û��رմ���ʱ���ͷ�displayռ�õ��ڴ���Դ
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
	 * ȡ������ɫ���� Take the text color object
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
	 * ��ʾ������Ϣ
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
