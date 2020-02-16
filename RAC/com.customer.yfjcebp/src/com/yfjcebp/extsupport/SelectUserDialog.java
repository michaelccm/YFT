package com.yfjcebp.extsupport;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.list.iList;

public class SelectUserDialog extends AbstractAIFDialog implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private iList list;
	private JButton okButton;
	private JButton cancelButton;
	private TCComponentQueryType queryType;
	private TCSession session;
	private TCTextService textService;
	private Vector<String> extUserVec;
	private String ext_year;
	private String ext_month;
	private Vector<TCComponent> initUsagePercentVec;
	private StringBuffer sb;

	public SelectUserDialog(TCSession session, JTable table,
			Vector<String> extUserVec, String ext_year, String ext_month,
			Vector<TCComponent> initUsagePercentVec, StringBuffer sb) {
		super(true);
		this.session = session;
		this.table = table;
		this.extUserVec = extUserVec;
		this.ext_year = ext_year;
		this.ext_month = ext_month;
		this.initUsagePercentVec = initUsagePercentVec;
		this.sb = sb;
	}

	public void initDialog() {
		setLayout(new BorderLayout());
		setTitle("Select ExtSupport");
		Vector<String> listVec = getListVec();
		String text = "Please Select ExtSupport,Support Multi:";
		MultilineLabel label = new MultilineLabel(text);
		list = new iList(listVec);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrPane = new JScrollPane(list);
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		okButton.setEnabled(false);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent listselectionevent) {
				// TODO Auto-generated method stub
				if (list.isSelectionEmpty()) {
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}
			}
		});
		add(label, BorderLayout.NORTH);
		add(scrPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(300, 500));
		centerToScreen();
		setVisible(true);

	}

	/**
	 *  排除当前table中已经存在的用户
	 * @return
	 */
	private Vector<String> getListVec() {
		String[] firstColVals = getFirstColumnVals();
		Vector<String> vec = new Vector<String>();
		for (int i = 0, size = extUserVec.size(); i < size; i++) {
			boolean hasExist = false;
			String s = extUserVec.get(i);
			if (firstColVals != null) {
				for (int j = 0; j < firstColVals.length; j++) {
					if (s.equals(firstColVals[j])) {
						hasExist = true;
						break;
					}
				}
			}
			if (!hasExist) {
				vec.add(s);
			}
		}
		return vec;
	}

	/**
	 * 得到当前jtable中第一列的用户
	 * 
	 * @return
	 */
	private String[] getFirstColumnVals() {
		int rowcnt = table.getRowCount();
		String[] firstColVals = null;
		if (rowcnt != 0) {
			firstColVals = new String[rowcnt];
			for (int i = 0; i < rowcnt; i++) {
				firstColVals[i] = (String) table.getValueAt(i, 1);
			}
		}
		return firstColVals;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		Object s = event.getSource();
		if (s.equals(okButton)) {
			Object[] vallist = list.getSelectedValues();
			dispose();
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			// 先获得表格第二列到最后一列的列名
			int columnCnt = table.getColumnCount();
			String[] queryKeys = new String[] { "jci6_UserName", "jci6_Year",
					"jci6_Month" };
			String[] queryValues = new String[3];
			Vector<String> yearMonth = new Vector<String>();
			for (int i = 1; i < columnCnt; i++) {
				String colName = table.getColumnName(i);
				yearMonth.add(colName);
			}
			double usagePercent = 0;
			try {
				sb.setLength(0);
				for (int i = 0, size = vallist.length; i < size; i++) {
					String username = (String) vallist[i];
					queryValues[0] = username;
					queryValues[1] = ext_year;
					queryValues[2] = ext_month;
					usagePercent = getUsagePercent(queryKeys, queryValues);
					if (usagePercent <= 0) {
						if (sb.toString().equals("")) {
							sb.append("The ExtSupport has been occupied,can not select:\n");
							sb.append(username);
						} else {
							sb.append("\n").append(username);
						}
					} else {
						Vector<String> rowVals = new Vector<String>();
						rowVals.add((table.getRowCount()+1)+"");
						rowVals.add(username);
						rowVals.add(JCI6YFJCUtil.doubleTrans(usagePercent)
								+ "%");
						model.addRow(rowVals);
					}
				}
				if(!sb.toString().equals("")){
					MessageBox.post(sb.toString(), "INFORMATION", MessageBox.INFORMATION);
				}
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (s.equals(cancelButton)) {
			dispose();
		}
	}

	// 查找选中的用户在本月度是否被别的sectionmanager圈中,调用查询查找ExtSupport
	// jci6_UserName外包用户名，jci6_Year年度,jci6_Month月度
	private double getUsagePercent(String[] queryKeys, String[] queryValues)
			throws TCException {
		readyQuery();
		TCComponent[] comps = JCI6YFJCUtil.query(textService, queryType,
				"YFJC_SearchExtSupport", queryKeys, queryValues);
		double percent = 100;
		if (comps != null) {
			for (int i = 0; i < comps.length; i++) {
				if (initUsagePercentVec == null
						|| !initUsagePercentVec.contains(comps[i]))
					percent = percent
							- comps[i].getDoubleProperty("jci6_Percent");
			}
		}
		return percent;
	}

	private void readyQuery() throws TCException {
		if (queryType == null)
			queryType = (TCComponentQueryType) session
					.getTypeComponent("ImanQuery");
		if (textService == null)
			textService = session.getTextService();
	}

}
