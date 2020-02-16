package com.yfjc.workdayhourform;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.combobox.iComboBox;

public class SelectDivisionDialog extends AbstractAIFDialog implements
		ActionListener {

	/**
	 * 
	 */
	private iComboBox divisionCombox;
	private TCSession session;
	private static final long serialVersionUID = 1L;
	private JButton nextButton;
	private iTextField division_field;
	private Vector<String> departmentV;// 部门名
	private Map<String, TCComponentGroup> divisionMap;
	private TCComponentTaskTemplate template;



	public SelectDivisionDialog(TCSession session,
			TCComponentTaskTemplate template) {
		super(true);
		this.session = session;
		this.departmentV = new Vector<String>();
		this.divisionMap = new HashMap<String, TCComponentGroup>();
		this.template = template;
	}

	public void init() throws TCException {
		setLayout(new BorderLayout());
		setTitle("Select Division");
		JPanel panel = new JPanel(new PropertyLayout());
		JLabel label = new JLabel("Please Select Division");
		getDivisionValues();
		divisionCombox = new iComboBox(true);
		divisionCombox.setItems(departmentV);
		panel.add("1.1.left", label);
		panel.add("1.2.left", divisionCombox);
		divisionCombox.addActionListener(this);
		division_field = divisionCombox.getTextField();
		division_field.getFilterDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent documentevent) {
						// TODO Auto-generated method stub
						String s = division_field.getText().trim();
						if (departmentV.contains(s)) {
							nextButton.setEnabled(true);
						} else {
							nextButton.setEnabled(false);
						}
						pack();
					}

					@Override
					public void insertUpdate(DocumentEvent documentevent) {
						// TODO Auto-generated method stub
						String s = division_field.getText().trim();
						if (departmentV.contains(s)) {
							nextButton.setEnabled(true);
						} else {
							nextButton.setEnabled(false);
						}
					}

					@Override
					public void changedUpdate(DocumentEvent documentevent) {
						// TODO Auto-generated method stub

					}
				});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		nextButton = new JButton("Next");
		buttonPanel.add(nextButton);
		nextButton.setEnabled(false);
		nextButton.addActionListener(this);
		add(panel);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
		centerToScreen();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent actionevent) {
		// TODO Auto-generated method stub
		Object obj = actionevent.getSource();
		if (obj == nextButton) {
			String value = (String) divisionCombox.getSelectedItem();
			dispose();
			WorkdayHourDialog dialog = new WorkdayHourDialog(session, template,value,divisionMap.get(value));
			try {
				dialog.initDialog();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// 获得JCI6_Division lov
	private void getDivisionValues() throws TCException {
		ListOfValuesInfo info = TCComponentListOfValuesType.findLOVByName(
				session, "JCI6_Division").getListOfValues();
		Object[] object = info.getListOfValues();
		for (int i = 0; i < object.length; i++) {
			if (object[i] instanceof TCComponentGroup) {
				TCComponentGroup group = (TCComponentGroup) object[i];
				String gname = group.getFullName().split("\\.")[0];
				departmentV.add(gname);
				divisionMap.put(gname, group);
			}
		}
	}

}
