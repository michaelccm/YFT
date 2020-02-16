///*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
//
//package com.teamcenter.rac.schedule.commands.costs;
//
//import com.teamcenter.rac.schedule.project.common.costing.*;
//import com.teamcenter.rac.schedule.project.common.gui.HoursFormatter;
//import com.teamcenter.rac.schedule.project.common.gui.StringHolder;
//import com.teamcenter.rac.schedule.project.common.l10n.ClassType;
//import com.teamcenter.rac.schedule.project.common.l10n.DisplayNameCache;
//import com.teamcenter.rac.schedule.project.server.RACInterface.RACCostAbstract;
//import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
//import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
//import com.teamcenter.rac.aifrcp.AIFUtility;
//import com.teamcenter.rac.common.lov.LOVComboBox;
//import com.teamcenter.rac.kernel.TCComponent;
//import com.teamcenter.rac.kernel.TCComponentBillRate;
//import com.teamcenter.rac.kernel.TCComponentListOfValues;
//import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
//import com.teamcenter.rac.schedule.common.util.CostValueFormatter;
//import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
//import com.teamcenter.rac.schedule.common.util.SwtWidgetsFactory;
//import com.teamcenter.rac.util.CurrencyFormatter;
//import com.teamcenter.rac.util.Registry;
//import java.util.*;
//import org.eclipse.jface.viewers.TreeNode;
//import org.eclipse.jface.wizard.*;
//import org.eclipse.swt.events.*;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.*;
//
//// Referenced classes of package com.teamcenter.rac.schedule.commands.costs:
////            SchedTaskCostsDlgWizard, FixedCostDlgWizPage, SchedTaskCostsDialogControl, FixedCostsModel
//
//public class SchedTaskCostsDlgWizPage extends WizardPage {
//	//String[] valuesFullNames;
//	private Registry r;
//	private DisplayNameCache nameCache;
//	private SchedTaskCostsDialogControl control;
//	private Stack costStack;
//	private FixedCostsModel fixCostModel;
//	private Vector breakModel;
//	private Button rollUpBut;
//	private Button drillDnBut;
//	Button addFixedCostBut;
//	Button delFixedCostBut;
//	Button detailFixedCostBut;
//	private int numCols;
//	private Table fixCostTable;
//	private Table breakTable;
//	// Combo billCodeCombo;
//	// Combo billSubCodeCombo;
//	// Combo billTypeCombo;
//	// Combo billRateCombo;
//	Label estCostLabel;
//	Label accCostLabel;
//	Label estWorkLabel;
//	Label accWorkLabel;
//	Label notesLabel;
//	private static int billRatePreviousIdx;
//	private static final int NOTHING_CHANGED = -1;
//	private static final int BILLRATE_CHANGED = 0;
//	private static final int FIXEDCOST_CHANGED = 1;
//
//	class FixedCostDlg extends WizardDialog {
//		protected void finishPressed() {
//			FixedCostDlgWizPage fixedcostdlgwizpage = (FixedCostDlgWizPage) getSelectedPage();
//			FixedCostDetail fixedcostdetail = fixedcostdlgwizpage.getData();
//			if (fixedcostdetail != null) {
//				TableItem tableitem = null;
//				boolean flag = false;
//				if (fixedcostdlgwizpage.addNew) {
//					int i = fixCostTable.getItemCount();
//					tableitem = new TableItem(fixCostTable, 0, i);
//					tableitem.setData("original_index", Integer.valueOf(i));
//					fixCostModel.addRow(fixedcostdetail);
//					String s = (new StringBuilder())
//							.append(fixedcostdetail.hashCode()).append("")
//							.toString();
//					control.fixCostMap.put(s, fixedcostdetail);
//				} else {
//					tableitem = fixCostTable.getSelection()[0];
//					if (tableitem == null) {
//						int k = fixCostModel.displayEntry
//								.indexOf(fixedcostdetail);
//						int j = SwtWidgetsFactory.getTableItemIndexOf(
//								fixCostTable, k);
//						tableitem = fixCostTable.getItem(j);
//					}
//				}
//				control.costAbs.recalculate(FixedCostDetail.class,
//						control.costDetail.getID(), true);
//				String as[] = {
//						fixedcostdetail.getName(),
//						CurrencyFormatter.format(
//								fixedcostdetail.getEstimatedCost(),
//								fixedcostdetail.getCurrency()),
//						/*
//						 * CurrencyFormatter.format(
//						 * fixedcostdetail.getAccruedCost(),
//						 * fixedcostdetail.getCurrency())
//						 */fixedcostdetail.getBillingType() };
//				tableitem.setText(as);
//				SwtWidgetsFactory.ColumnSortNotifyListeners(fixCostTable);
//				updateDisplayedValues(false);
//				super.finishPressed();
//			}
//		}
//
//		final SchedTaskCostsDlgWizPage this$0;
//
//		FixedCostDlg(Shell shell, IWizard iwizard) {
//			super(shell, iwizard);
//			this$0 = SchedTaskCostsDlgWizPage.this;
//		}
//	}
//
//	public SchedTaskCostsDlgWizPage(
//			SchedTaskCostsDialogControl schedtaskcostsdialogcontrol, String s,
//			String s1, int i) {
//		super(s);
//		numCols = 3;
//		nameCache = RACInterface
//				.getDisplayNameCache(schedtaskcostsdialogcontrol.getSession());
//		costStack = new Stack();
//		control = schedtaskcostsdialogcontrol;
//		fixCostModel = schedtaskcostsdialogcontrol.fixedCosts;
//		breakModel = schedtaskcostsdialogcontrol.breakModel;
//		setDescription(s1);
//		numCols = i;
//		r = Registry.getRegistry(this);
//	}
//
//	public void createControl(Composite composite) {
//		GridLayout gridlayout = new GridLayout();
//		gridlayout.numColumns = numCols;
//		composite.setLayout(gridlayout);
//		boolean flag = true;
//		int i = 0;
//		int j = 0;
//		String s = "";
//		String s1 = "";
//		String s2 = "";
//		if (control.costDetail != null) {
//			com.teamcenter.project.common.dataModel.CostValue costvalue = control.costDetail
//					.getTotalEstimatedCost();
//			com.teamcenter.project.common.dataModel.CostValue costvalue1 = control.costDetail
//					.getTotalAccruedCost();
//			i = control.costDetail.getTotalEstimatedMinutes();
//			j = control.costDetail.getTotalAccruedMinutes();
//			s = CostValueFormatter.format(costvalue);
//			s1 = CostValueFormatter.format(costvalue1);
//		}
//		estCostLabel = SwtWidgetsFactory.addLabel(composite,
//				r.getString("costEstimate"), s, 100, 131072, flag);
//		// accCostLabel = SwtWidgetsFactory.addLabel(composite,
//		// r.getString("costAccrue"), s1, 100, 131072, flag);
//		s2 = HoursFormatter.format(i);
//		estWorkLabel = SwtWidgetsFactory.addLabel(composite,
//				r.getString("workEstimate"), s2, 100, 131072, flag);
//		s2 = HoursFormatter.format(j);
//		accWorkLabel = SwtWidgetsFactory.addLabel(composite,
//				r.getString("workAccrue"), s2, 100, 131072, flag);
//		SwtWidgetsFactory.addBlank(composite, 3);
//		SwtWidgetsFactory.addLabel(composite, r.getString("fixCost"), "", 100,
//				131072, flag);
//		fixCostTable = SwtWidgetsFactory.addTable(
//				composite,
//				0,
//				125,
//				new String[] {
//						nameCache.getAttrDisplayName(ClassType.FIXED_COST,
//								"Cost Name"),
//						nameCache.getAttrDisplayName(ClassType.FIXED_COST,
//								"Estimated Cost"), "Bill Type" }, new int[] {
//						16384, 131072, 131072 }, control.fixedCostsTableData,
//				2, 5);
//		SwtWidgetsFactory.addBlank(composite, 1);
//		fixCostTable.addMouseListener(new MouseAdapter() {
//
//			public void mouseDoubleClick(MouseEvent mouseevent) {
//				super.mouseDoubleClick(mouseevent);
//				TableItem tableitem = fixCostTable.getItem(new Point(
//						mouseevent.x, mouseevent.y));
//				if (tableitem == null)
//					fireFixedCostNew(mouseevent.widget.getDisplay()
//							.getActiveShell());
//				else
//					fireFixedCostDetails(mouseevent.widget.getDisplay()
//							.getActiveShell());
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//
//			{
//				this$0 = SchedTaskCostsDlgWizPage.this;
//
//			}
//		});
//		addFixedCostBut = SwtWidgetsFactory.addButton(composite,
//				r.getString("buttonNew"));
//		addFixedCostBut.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent selectionevent) {
//				fireFixedCostNew(selectionevent.display.getActiveShell());
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//			{
//				this$0 = SchedTaskCostsDlgWizPage.this;
//			}
//		});
//		delFixedCostBut = SwtWidgetsFactory.addButton(composite,
//				r.getString("buttonDel"));
//		delFixedCostBut.setEnabled(false);
//		delFixedCostBut.addSelectionListener(new SelectionAdapter() 
//		{
//			public void widgetSelected(SelectionEvent selectionevent) {
//				int ai[] = fixCostTable.getSelectionIndices();
//				int ai1[] = ai;
//				int k = ai1.length;
//				for (int l = 0; l < k; l++) {
//					int i1 = ai1[l];
//					if (fixCostTable.getItem(i1).getData("original_index") != null) {
//						int ai2[] = { ((Integer) fixCostTable.getItem(i1)
//								.getData("original_index")).intValue() };
//						fixCostModel.removeRows(ai2);
//						fixCostTable.getItem(i1).dispose();
//						SwtWidgetsFactory.resetIndexForModel(fixCostTable,
//								ai2[0]);
//					}
//				}
//
//				control.costAbs.recalculate(FixedCostDetail.class,
//						control.costDetail.getID(), true);
//				if (control.costDetail != null)
//					setFinalTotalEstimates();
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//
//			{
//
//				this$0 = SchedTaskCostsDlgWizPage.this;
//
//			}
//		});
//		detailFixedCostBut = SwtWidgetsFactory.addButton(composite,
//				r.getString("buttonDetail"));
//		detailFixedCostBut.setEnabled(false);
//		detailFixedCostBut.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent selectionevent) {
//				fireFixedCostDetails(selectionevent.display.getActiveShell());
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//
//			{
//
//				this$0 = SchedTaskCostsDlgWizPage.this;
//
//			}
//		});
//		fixCostTable.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent selectionevent) {
//				super.widgetSelected(selectionevent);
//				int k = fixCostTable.getSelectionCount();
//				detailFixedCostBut.setEnabled(k == 1);
//				delFixedCostBut.setEnabled(k != 0);
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//
//			{
//
//				this$0 = SchedTaskCostsDlgWizPage.this;
//
//			}
//		});
//		SwtWidgetsFactory.addBlank(composite, 1);
//		// billCodeCombo = SwtWidgetsFactory.addCombo(composite, nameCache
//		// .getAttrDisplayName(ClassType.SCHEDULE_TASK, "bill_code"),
//		// control.lovBillCodeList, 0, flag);
//		StringHolder astringholder[] = new StringHolder[0];
//		if (control.lovBillSubCodeList.length > control.billCodeDBIdx
//				&& control.billCodeDBIdx > -1
//				&& control.lovBillSubCodeList[control.billCodeDBIdx] != null)
//			astringholder = control.lovBillSubCodeList[control.billCodeDBIdx];
//		// billSubCodeCombo = SwtWidgetsFactory.addCombo(composite, nameCache
//		// .getAttrDisplayName(ClassType.SCHEDULE_TASK, "bill_sub_code"),
//		// astringholder, 0, flag);
//		// billCodeCombo.addSelectionListener(new SelectionAdapter() {
//		//
//		// public void widgetSelected(SelectionEvent selectionevent) {
//		// int k = billCodeCombo.getSelectionIndex();
//		// setBillCodeComboSelection(k, 0);
//		// }
//		//
//		// final SchedTaskCostsDlgWizPage this$0;
//		//
//		// {
//		//
//		// this$0 = SchedTaskCostsDlgWizPage.this;
//		//
//		// }
//		// });
//		setBillCodeComboSelection(control.billCodeDBIdx,
//				control.billSubCodeDBIdx);
//
//		// 这里改成NonLabourType的LOV
//		// billTypeCombo =
//		// SwtWidgetsFactory.addCombo(composite,//,"JCI6_NonLabour",
//		// nameCache.getAttrDisplayName(ClassType.SCHEDULE_TASK,
//		// "bill_type"), control.billTypeList, 0, flag);
//
//		// 修改billTypeCombo的LOV代码指向自定义的LOV
//		// TCSession session = (TCSession) AIFUtility.getCurrentApplication()
//		// .getSession();
//		//
//		// TCComponentListOfValues findLOVByName = TCComponentListOfValuesType
//		// .findLOVByName(session, "JCI6_NonLabour");
//		// control.billTypeList = ScheduleUtil.getStringHolders(findLOVByName);
//		// try {
//		// valuesFullNames = findLOVByName.getListOfValues()
//		// .getValuesFullNames();
//		// billTypeCombo = SwtWidgetsFactory.addCombo(composite,
//		// "Bill Type :", control.billTypeList, 0, flag);
//		// billTypeCombo.setVisible(false);
//		// } catch (TCException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//		//
//		// billTypeCombo.select(control.billTypeDBIdx);
//		// billRateCombo = SwtWidgetsFactory.addCombo(composite, nameCache
//		// .getAttrDisplayName(ClassType.SCHEDULE_TASK, "billrate_tag"),
//		// control.billRateList, 0, flag);
//		// billRateCombo.select(control.billRateDBIdx);
//		billRatePreviousIdx = control.billRateDBIdx;
//		if (control.costDetail.isSummaryTask())
//			// billRateCombo.setEnabled(false);
//			// billRateCombo.addSelectionListener(new SelectionAdapter() {
//			//
//			// public void widgetSelected(SelectionEvent selectionevent) {
//			// int k = billRateCombo.getSelectionIndex();
//			// if (billRateCombo.getItemCount() > 1
//			// && SchedTaskCostsDlgWizPage.billRatePreviousIdx != k)
//			// resetFinalTotalEstimates(0, k);
//			// }
//			//
//			// final SchedTaskCostsDlgWizPage this$0;
//			//
//			// {
//			//
//			// this$0 = SchedTaskCostsDlgWizPage.this;
//			//
//			// }
//			// });
//			SwtWidgetsFactory.addBlank(composite, 3);
//		SwtWidgetsFactory.addLabel(composite, r.getString("labelBreakdown"),
//				"", 100, 131072, flag);
//		breakTable = SwtWidgetsFactory
//				.addTable(
//						composite,
//						0,
//						125,
//						new String[] { r.getString("costTableCol1"),
//								r.getString("breakTableCol2"),
//								r.getString("breakTableCol3"),
//						// r.getString("costTableCol2"),
//						// r.getString("costTableCol3") , 131072, 131072
//						}, new int[] { 16384, 131072, 131072 },
//						control.breakData, 2, 4);
//		breakTable.addSelectionListener(new SelectionListener() {
//
//			public void widgetDefaultSelected(SelectionEvent selectionevent) {
//				doHandleSelection();
//			}
//
//			public void widgetSelected(SelectionEvent selectionevent) {
//				doHandleSelection();
//			}
//
//			private void doHandleSelection() {
//				boolean flag1 = false;
//				int k = breakTable.getSelectionIndex();
//				if (k >= 0) {
//					TreeNode treenode = (TreeNode) breakModel.get(k);
//					CostLineItem costlineitem = (CostLineItem) treenode
//							.getValue();
//					CostDetail costdetail = costlineitem.getCostDetail();
//					if (costdetail != null)
//						flag1 = true;
//				}
//				drillDnBut.setEnabled(flag1);
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//			{
//				this$0 = SchedTaskCostsDlgWizPage.this;
//			}
//		});
//		rollUpBut = SwtWidgetsFactory.addButton(composite,
//				r.getString("buttonRollUp"));
//		rollUpBut.setEnabled(false);
//		rollUpBut.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent selectionevent) {
//				if (!costStack.empty()) {
//					retainCurrentSelections();
//					CostDetail costdetail = (CostDetail) costStack.pop();
//					control.setCostDetail(costdetail);
//					breakModel = control.breakModel;
//					fixCostModel = control.fixedCosts;
//					updateDisplayedValues(true);
//					reselectBillRate();
//				}
//				if (costStack.empty())
//					rollUpBut.setEnabled(false);
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//			{
//				this$0 = SchedTaskCostsDlgWizPage.this;
//			}
//		});
//		drillDnBut = SwtWidgetsFactory.addButton(composite,
//				r.getString("buttonDrillDown"));
//		drillDnBut.setEnabled(false);
//		drillDnBut.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent selectionevent) {
//				int k = breakTable.getSelectionIndex();
//				if (k < 0)
//					return;
//				k = SwtWidgetsFactory.getDataModelIndexOf(breakTable, k);
//				if (k >= 0) {
//					retainCurrentSelections();
//					TreeNode treenode = (TreeNode) breakModel.get(k);
//					CostLineItem costlineitem = (CostLineItem) treenode
//							.getValue();
//					CostDetail costdetail = costlineitem.getCostDetail();
//					if (costdetail != null) {
//						costStack.push(control.costDetail);
//						rollUpBut.setEnabled(true);
//						control.costFormMap.put(costdetail.getID(), costdetail);
//						control.setCostDetail(costdetail);
//						breakModel = control.breakModel;
//						fixCostModel = control.fixedCosts;
//						updateDisplayedValues(true);
//						reselectBillRate();
//					}
//				}
//			}
//
//			final SchedTaskCostsDlgWizPage this$0;
//			{
//				this$0 = SchedTaskCostsDlgWizPage.this;
//			}
//		});
//
//		// 去掉警告
//		// if (ScheduleDeferredContext.inDeferredSession()) {
//		// SwtWidgetsFactory.addBlank(composite, 3);
//		// notesLabel = SwtWidgetsFactory.addLabel(composite,
//		// r.getString("notes.TITLE"), r.getString("deferred.NOTES"),
//		// 200, 16384, flag);
//		// }
//		composite.pack();
//		setControl(composite);
//	}
//
//	private void retainCurrentSelections() {
//		// int i = billCodeCombo.getSelectionIndex();
//		int i = 0;
//		i = i >= 0 ? i : 0;
//		control.costDetail.setBillingCode(control.lovBillCodeList[i]
//				.getInternal());
//		// int j = billSubCodeCombo.getSelectionIndex() >= 0 ? billSubCodeCombo
//		// .getSelectionIndex() : 0;
//		int j = 0;
//		String s = control.lovBillSubCodeList[i] != null ? control.lovBillSubCodeList[i][j]
//				.getInternal() : "";
//		control.costDetail.setBillingSubCode(s);
//		// j = billTypeCombo.getSelectionIndex() >= 0 ? billTypeCombo
//		// .getSelectionIndex() : 0;
//		j = 0;
//		control.costDetail
//				.setBillingType(control.billTypeList[j].getInternal());
//	}
//
//	private void setFinalTotalEstimates() {
//		com.teamcenter.project.common.dataModel.CostValue costvalue = control.costDetail
//				.getTotalEstimatedCost();
//		com.teamcenter.project.common.dataModel.CostValue costvalue1 = control.costDetail
//				.getTotalAccruedCost();
//		int i = control.costDetail.getTotalEstimatedMinutes();
//		int j = control.costDetail.getTotalAccruedMinutes();
//		String s = CostValueFormatter.format(costvalue);
//		String s1 = CostValueFormatter.format(costvalue1);
//		estCostLabel.setText(s);
//		// accCostLabel.setText(s1);
//		String s2 = HoursFormatter.format(i);
//		estWorkLabel.setText(s2);
//		s2 = HoursFormatter.format(j);
//		accWorkLabel.setText(s2);
//	}
//
//	private void resetFinalTotalEstimates(int i, int j) {
//		if (j < 0)
//			i = -1;
//		switch (i) {
//		case 0: // '\0'
//			TCComponentBillRate tccomponentbillrate = (TCComponentBillRate) control.billRateCom[j];
//			BillRateDetail billratedetail = RACCostAbstract
//					.tcCompToBillRateDetail(tccomponentbillrate);
//			control.costDetail.setBillRate(billratedetail);
//			control.costAbs.recalculate(BillRateDetail.class,
//					control.costDetail.getID(), true);
//			billRatePreviousIdx = j;
//			control.setVariableCostLineItems(true);
//			breakTable.clearAll();
//			breakTable.removeAll();
//			SwtWidgetsFactory.addTableContents(breakTable, control.breakData);
//			break;
//		}
//		setFinalTotalEstimates();
//	}
//
//	private void reselectBillRate() {
//		BillRateDetail billratedetail = control.costDetail.getBillRate();
//		if (billratedetail != null) {
//			String s = billratedetail.getName();
//			if (s != null && s.length() > 0) {
//				billRatePreviousIdx = Arrays.asList(control.billRateList)
//						.indexOf(s);
//				if (billRatePreviousIdx < 0)
//					billRatePreviousIdx = 0;
//			} else {
//				billRatePreviousIdx = 0;
//			}
//		} else {
//			billRatePreviousIdx = 0;
//		}
//		// billRateCombo.select(billRatePreviousIdx);
//		// if (control.costDetail.isSummaryTask())
//		// billRateCombo.setEnabled(false);
//		// else
//		// billRateCombo.setEnabled(true);
//	}
//
//	private void fireFixedCostDetails(Shell shell) {
//		int i = fixCostTable.getSelectionIndex();
//		if (i < 0)
//			return;
//		i = SwtWidgetsFactory.getDataModelIndexOf(fixCostTable, i);
//		if (i >= 0) {
//			FixedCostDetail fixedcostdetail = (FixedCostDetail) fixCostModel.displayEntry
//					.get(i);
//			SchedTaskCostsDlgWizard schedtaskcostsdlgwizard = new SchedTaskCostsDlgWizard();
//			schedtaskcostsdlgwizard.setWindowTitle(r
//					.getString("fixedCost.TITLE"));
//			schedtaskcostsdlgwizard.setHelpAvailable(false);
//			int j = Math.max(0, StringHolder.findInternal(
//					control.lovBillCodeList, fixedcostdetail.getBillingCode()));
//			StringHolder astringholder[] = j <= -1
//					|| control.lovBillSubCodeList.length <= j
//					|| control.lovBillSubCodeList[j] == null ? (new StringHolder[] { StringHolder.EMPTY })
//					: control.lovBillSubCodeList[j];
//			int k = Math.max(
//					0,
//					StringHolder.findInternal(astringholder,
//							fixedcostdetail.getBillingSubCode()));
//			int l = Math.max(0, StringHolder.findInternal(control.billTypeList,
//					fixedcostdetail.getBillingType()));
//			schedtaskcostsdlgwizard.addPage(new FixedCostDlgWizPage(control,
//					fixedcostdetail, false, r.getString("fixedCost.TITLE"), r
//							.getString("fixedCost.DESC"), 2, j, k, l,
//					control.currencyList[0]));
//			FixedCostDlg fixedcostdlg = new FixedCostDlg(shell,
//					schedtaskcostsdlgwizard);
//			fixedcostdlg.create();
//			fixedcostdlg.open();
//		}
//	}
//
//	private void fireFixedCostNew(Shell shell) {
//		SchedTaskCostsDlgWizard schedtaskcostsdlgwizard = new SchedTaskCostsDlgWizard();
//		schedtaskcostsdlgwizard.setWindowTitle(r.getString("fixedCost.TITLE"));
//		schedtaskcostsdlgwizard.setHelpAvailable(false);
//		schedtaskcostsdlgwizard.addPage(new FixedCostDlgWizPage(control, null,
//				true, r.getString("fixedCost.TITLE"), r
//						.getString("fixedCost.DESC"), 2, 0, 0, // billTypeCombo.getSelectionIndex()
//				0, control.currencyList[0]));
//		FixedCostDlg fixedcostdlg = new FixedCostDlg(shell,
//				schedtaskcostsdlgwizard);
//		fixedcostdlg.create();
//		fixedcostdlg.open();
//	}
//
//	private void updateDisplayedValues(boolean flag) {
//		if (control.costDetail != null) {
//			com.teamcenter.project.common.dataModel.CostValue costvalue = control.costDetail
//					.getTotalEstimatedCost();
//			com.teamcenter.project.common.dataModel.CostValue costvalue1 = control.costDetail
//					.getTotalAccruedCost();
//			int i = control.costDetail.getTotalEstimatedMinutes();
//			int j = control.costDetail.getTotalAccruedMinutes();
//			String s = CostValueFormatter.format(costvalue);
//			String s1 = CostValueFormatter.format(costvalue1);
//			estCostLabel.setText(s);
//			// accCostLabel.setText(s1);
//			String s2 = HoursFormatter.format(i);
//			estWorkLabel.setText(s2);
//			s2 = HoursFormatter.format(j);
//			accWorkLabel.setText(s2);
//			if (flag) {
//				drillDnBut.setEnabled(false);
//				setBillCodeComboSelection(control.billCodeDBIdx,
//						control.billSubCodeDBIdx);
//				// billTypeCombo.select(control.billTypeDBIdx);
//				// billRateCombo.select(control.billRateDBIdx);
//				fixCostTable.clearAll();
//				fixCostTable.removeAll();
//				SwtWidgetsFactory.addTableContents(fixCostTable,
//						control.fixedCostsTableData);
//				breakTable.clearAll();
//				breakTable.removeAll();
//				SwtWidgetsFactory.addTableContents(breakTable,
//						control.breakData);
//			}
//		}
//	}
//
//	private void setBillCodeComboSelection(int i, int j) {
//		StringHolder astringholder[];
//		if (control.lovBillSubCodeList.length > i
//				&& control.lovBillSubCodeList[i] != null)
//			astringholder = control.lovBillSubCodeList[i];
//		else
//			astringholder = (new StringHolder[] { StringHolder.EMPTY });
//		// billCodeCombo.select(i);
//		// billSubCodeCombo.setItems(StringHolder.getDisplay(astringholder));
//		// billSubCodeCombo.select(j);
//	}
//
//}
