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
package com.yfjcebp.projectmanager.budget.dialogs;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
 * @author wu
 * 
 */
public class Excel_ErrorDialog extends AbstractAIFDialog {

	private static final long serialVersionUID = 1L;
	private String s1;
	private StyledText styleText;
	private Display display;
	private Registry reg = Registry.getRegistry(this);

	private Shell shell;
	private String warnmessage = "";
	private String errmessage = "";
	private String warningMessage;// ������Ϣ����ɫ���壩
	private String errorMessage;// ������Ϣ����ɫ���壩

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
	
	public Excel_ErrorDialog(Display display,ArrayList<HashMap<String, String>> error_list) {
		//DeviceData data = new DeviceData();
		// this.errorMessage = errorMessage;
		//display = new Display(data);
		this.display=display;

		StringBuilder sb_warr = new StringBuilder();
		StringBuilder sb_err = new StringBuilder();
		for (int i = 0; i < error_list.size(); i++) {
			HashMap<String, String> hashMap = error_list.get(i);
			if (hashMap.containsKey("error_num")) {

				sb_err.append(reg.getString("Error_data") + " "
						+ hashMap.get("error_num") + "\n");
			} else if (hashMap.containsKey("error_Date")) {

				sb_err.append(reg.getString("Error_data") + " "
						+ hashMap.get("error_Date") + "\n");
			} else if (hashMap.containsKey("error_msg")) {
				
				sb_err.append(hashMap.get("error_msg") + "\n");
			} else if (hashMap.containsKey("error_Group")) {

				sb_warr.append(reg.getString("Error_Group") + " "
						+ hashMap.get("error_Group") + "\n");
			} else if (hashMap.containsKey("error_Section")) {

				sb_warr.append(reg.getString("Error_Section") + " "
						+ hashMap.get("error_Section") + "\n");
			} else if (hashMap.containsKey("error_Cost")) {

				sb_warr.append(reg.getString("Error_CostType") + " "
						+ hashMap.get("error_CostType") + "\n");
			}
		}
		this.errmessage = sb_warr.toString();
		this.warnmessage = sb_err.toString();
		this.warningMessage = reg.getString("warningMessage");
		this.errorMessage = "\n" + "\n" + reg.getString("errorMessage");

		init(this.display);
		
	}
	

	public Excel_ErrorDialog(ArrayList<HashMap<String, String>> error_list) {
		DeviceData data = new DeviceData();
		// this.errorMessage = errorMessage;
		display = new Display(data);

		StringBuilder sb_warr = new StringBuilder();
		StringBuilder sb_err = new StringBuilder();
		for (int i = 0; i < error_list.size(); i++) {
			HashMap<String, String> hashMap = error_list.get(i);
			if (hashMap.containsKey("error_num")) {

				sb_err.append(reg.getString("Error_data") + " "
						+ hashMap.get("error_num") + "\n");
			} else if (hashMap.containsKey("error_Date")) {

				sb_err.append(reg.getString("Error_data") + " "
						+ hashMap.get("error_Date") + "\n");
			} else if (hashMap.containsKey("error_msg")) {
				
				sb_err.append(hashMap.get("error_msg") + "\n");
			} else if (hashMap.containsKey("error_Group")) {

				sb_warr.append(reg.getString("Error_Group") + " "
						+ hashMap.get("error_Group") + "\n");
			} else if (hashMap.containsKey("error_Section")) {

				sb_warr.append(reg.getString("Error_Section") + " "
						+ hashMap.get("error_Section") + "\n");
			} else if (hashMap.containsKey("error_Cost")) {

				sb_warr.append(reg.getString("Error_CostType") + " "
						+ hashMap.get("error_CostType") + "\n");
			}
		}
		this.errmessage = sb_warr.toString();
		this.warnmessage = sb_err.toString();
		this.warningMessage = reg.getString("warningMessage");
		this.errorMessage = "\n" + "\n" + reg.getString("errorMessage");

		init(display);
		
	}

	// ��ʼ��
	private void init(final Display display) {
		System.out.println("===============================");
		shell = new Shell(display);
		shell.setText(reg.getString("Shell_Error"));
		styleText = new StyledText(shell, SWT.READ_ONLY | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		styleText.setBounds(10, 5, 340, 220);
		s1 = "\n";
		styleText.append(s1);
		setMessage();
		Button okBtton = new Button(shell, SWT.PUSH);

		okBtton.setText("OK(O)");
		okBtton.setBounds(80, 230, 75, 30);
		okBtton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				// �ͷ���Դ
				shell.dispose();
			}
		});

		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel(C)");
		cancelButton.setBounds(180, 230, 75, 30);// ���ð�ť��ʾλ�ü���ȡ��߶�

		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// �ͷ���Դ
				display.dispose();
			}
		});

		shell.setSize(400, 300);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
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
		// styleRange.fontStyle = SWT.BOLD;
		return styleRange;
	}

	/**
	 * ��ʾ������Ϣ
	 */
	public void setMessage() {
		if (!errmessage.equals("")) {
			getStyleText().append(errorMessage + "\n");
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
				getStyleText().append(warningMessage + "\n");
				StyleRange styleRange = getColorStyle(a,
						warningMessage.length(), color);
				getStyleText().setStyleRange(styleRange);
				getStyleText().append(warnmessage);
			}
		}
	}
}
