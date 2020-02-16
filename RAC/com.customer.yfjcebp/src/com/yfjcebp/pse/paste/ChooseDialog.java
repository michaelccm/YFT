package com.yfjcebp.pse.paste;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class ChooseDialog extends AbstractAIFDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Display display;
	private Shell shell;
	private TCSession session;

	private Button okBtton;
	private Button cancelButton;
	private Combo combo;

	private String number = "";
	private int flag = -1;

	public ChooseDialog(TCSession session, Display display) {
		// DeviceData data = new DeviceData();
		// this.errorMessage = errorMessage;
		this.display = display;
		this.session = session;
		init(display);
	}

	public String getNumberStr() {
		return number;
	}
	
	public int getflag() {
		return flag;
	}

	private void init(final Display display) {
		System.out.println("=========6.3修改======初始化=======================");
		shell = new Shell(display);
		shell.setSize(280, 300);
		shell.setText("Choose PKRSetting : ");

		Label label_version = new Label(shell, SWT.CENTER);
		label_version.setText("Please choose or set PKR value :");
		label_version.setBounds(80, 50, 180, 30);
		combo = new Combo(shell, SWT.DROP_DOWN);
		combo.setBounds(80, 100, 200, 50);
		combo.setItems(getTCPreferenceArray(session, "YFJC_PKRChoose_setting"));

		okBtton = new Button(shell, SWT.PUSH);

		okBtton.setText("OK(O)");
		okBtton.setBounds(80, 180, 75, 30);

		okBtton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String text = combo.getText();
				if (!text.equals("")) {
					
					if (isStringValueExists(
							text,
							getTCPreferenceArray(session,
									"YFJC_PKRChoose_setting"))) {
						number = text;
					} else {

						String[] items = combo.getItems();
						String[] arrays = new String[items.length + 1];
						for (int i = 0; i < items.length; i++) {
							arrays[i] = items[i];
						}
						arrays[items.length] = text;
						number = text;
						try {
							setTCPreferenceArray(session,
									"YFJC_PKRChoose_setting", arrays);
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

				flag=1;
				
				// 释放资源
				shell.dispose();
			}
		});

		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel(C)");
		cancelButton.setBounds(180, 180, 75, 30);// 设置按钮显示位置及宽度、高度

		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// 释放资源
				shell.dispose();
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

	private String[] getTCPreferenceArray(TCSession tcsession,
			String preferenceName) {
		String[] preString = null;
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();
		preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_user, preferenceName);
		return preString;
	}

	private void setTCPreferenceArray(TCSession tcsession,
			String preferenceName, String[] value) throws TCException {
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();
		tcpreservice.setStringArray(TCPreferenceService.TC_preference_user,
				preferenceName, value);
	}

	private boolean isStringValueExists(String value, String[] arrays) {
		if (arrays.length == 0) {
			return false;
		}

		for (int i = 0; i < arrays.length; i++) {
			if (arrays[i].equals(value)) {
				return true;
			}
		}
		return false;
	}
}
