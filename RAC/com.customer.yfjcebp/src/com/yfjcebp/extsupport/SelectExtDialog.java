package com.yfjcebp.extsupport;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.combobox.iComboBox;

public class SelectExtDialog extends AbstractAIFDialog implements
		ActionListener {

	/**
	 * 
	 */
	private iComboBox extSupportCombox;
	private iComboBox yearMonthCombox;
	private TCSession session;
	private static final long serialVersionUID = 1L;
	private Map<String, Vector<String>> ext_daibiao_map;
	private Map<String, TCComponentUser> ext_user_map;
	private Vector<String> ext_db;
	private JButton nextButton;
	private Registry reg = Registry.getRegistry(this);
	private String current_date;
	private Vector<String> yearMonthVec;
	private iTextField extSupport_field;
	private iTextField year_field;
	private TCComponentTaskTemplate taskTemplate;
	private Map<String, String> ext_division;

	public SelectExtDialog(TCSession session,
			TCComponentTaskTemplate taskTemplate,
			Map<String, Vector<String>> ext_daibiao_map,
			Map<String, TCComponentUser> ext_user_map, Vector<String> ext_db, Map<String, String> ext_division) {
		super(true);
		this.session = session;
		this.taskTemplate = taskTemplate;
		this.ext_daibiao_map = ext_daibiao_map;
		this.ext_user_map = ext_user_map;
		this.ext_db = ext_db;
		this.ext_division = ext_division;
	}

	public void init() throws TCException, IOException {
		setLayout(new BorderLayout());
		String title = reg.getString("selectExtSupport.Title");
		setTitle(title);
		getCurrentYearMonth();
		JPanel panel = new JPanel(new PropertyLayout());
		JLabel label = new JLabel(title + ":");
		extSupportCombox = new iComboBox(true);
		extSupportCombox.setItems(ext_db);
		extSupportCombox.setMaximumRowCount(5);
		panel.add("1.1.left", label);
		panel.add("1.2.left", extSupportCombox);
		extSupportCombox.addActionListener(this);

		JLabel year_Label = new JLabel(reg.getString("selectYear.Label"));
		yearMonthCombox = new iComboBox(true);
		String[] lovValues = JCI6YFJCUtil.getLovValues(session,
				"JCI6_NormalHours");
		// 得到当前日期以后的
		yearMonthVec = getLastDateString(lovValues);
		//yearMonthCombox.setMaximumRowCount(5);
		yearMonthCombox.setItems(yearMonthVec);// JCI6_NormalHours
		//yearMonthCombox.setSelectedIndex(0);

		extSupport_field = extSupportCombox.getTextField();
		extSupport_field.getFilterDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent documentevent) {
						// TODO Auto-generated method stub
						String s = extSupport_field.getText().trim();
						if (yearMonthVec.contains(yearMonthCombox
								.getSelectedItem()) && ext_db.contains(s)) {
							nextButton.setEnabled(true);
						} else {
							nextButton.setEnabled(false);
						}
					}

					@Override
					public void insertUpdate(DocumentEvent documentevent) {
						// TODO Auto-generated method stub
						String s = extSupport_field.getText().trim();
						if (yearMonthVec.contains(yearMonthCombox
								.getSelectedItem()) && ext_db.contains(s)) {
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

		year_field = yearMonthCombox.getTextField();
		year_field.getFilterDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent documentevent) {
						// TODO Auto-generated method stub
						String s = year_field.getText().trim();
						if (yearMonthVec.contains(s)
								&& ext_db.contains(extSupportCombox
										.getSelectedItem())) {
							nextButton.setEnabled(true);
						} else {
							nextButton.setEnabled(false);
						}
					}

					@Override
					public void insertUpdate(DocumentEvent documentevent) {
						// TODO Auto-generated method stub
						String s = year_field.getText().trim();
						if (yearMonthVec.contains(s)
								&& ext_db.contains(extSupportCombox
										.getSelectedItem())) {
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
		panel.add("2.1.left", year_Label);
		panel.add("2.2.left", yearMonthCombox);

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

	// 得到当前年月
	private void getCurrentYearMonth() {
		Date date = new Date();
		SimpleDateFormat sb = new SimpleDateFormat("yyyy.MM");
		current_date = sb.format(date);
	}

	private Vector<String> getLastDateString(String[] lovValues) {
		Vector<String> comBoxValues = new Vector<String>();
		int index = -1;
		for (int i = 0; i < lovValues.length; i++) {
			if (lovValues[i].equals(current_date)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			for (int i = index + 1; i < lovValues.length; i++) {
				comBoxValues.add(lovValues[i]);
			}
		}
		return comBoxValues;
	}

	@Override
	public void actionPerformed(ActionEvent actionevent) {
		// TODO Auto-generated method stub
		Object obj = actionevent.getSource();
		if (obj != nextButton) {
			if (extSupportCombox.getSelectedIndex() != -1
					&& yearMonthCombox.getSelectedIndex() != -1) {
				nextButton.setEnabled(true);
			} else {
				nextButton.setEnabled(false);
			}
		} else {
			String extSupport = (String) extSupportCombox.getSelectedItem();
			String yearMonth = (String) yearMonthCombox.getSelectedItem();
			//System.out.println(yearMonthCombox.getSelectedItem());
			SectionManagerDialog dialog = new SectionManagerDialog(session,
					taskTemplate, ext_daibiao_map.get(extSupport),
					ext_user_map, ext_division,yearMonth,extSupport);
			try {
				dispose();
				dialog.initDialog();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
