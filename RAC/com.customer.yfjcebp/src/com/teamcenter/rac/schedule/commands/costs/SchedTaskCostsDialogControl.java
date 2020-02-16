///*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
//
//package com.teamcenter.rac.schedule.commands.costs;
//
//import com.teamcenter.rac.schedule.project.common.costing.*;
//import com.teamcenter.rac.schedule.project.common.dataModel.CostValue;
//import com.teamcenter.rac.schedule.project.common.gui.HoursFormatter;
//import com.teamcenter.rac.schedule.project.common.gui.StringHolder;
//import com.teamcenter.rac.schedule.project.server.RACInterface.RACCostAbstract;
//import com.teamcenter.rac.aifrcp.AIFUtility;
//import com.teamcenter.rac.kernel.*;
//import com.teamcenter.rac.schedule.ScheduleViewApplication;
//import com.teamcenter.rac.schedule.common.util.*;
//import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
//import com.teamcenter.rac.util.*;
//import com.teamcenter.rac.util.MessageBox;
//import com.teamcenter.rac.util.log.Debug;
//import java.math.BigDecimal;
//import java.text.MessageFormat;
//import java.text.ParseException;
//import java.util.*;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.eclipse.jface.viewers.TreeNode;
//import org.eclipse.jface.wizard.IWizard;
//import org.eclipse.jface.wizard.WizardDialog;
//import org.eclipse.swt.widgets.*;
//import org.eclipse.swt.widgets.Shell;
//
//// Referenced classes of package com.teamcenter.rac.schedule.commands.costs:
////            FixedCostsModel, SchedTaskCostsDlgWizard, SchedTaskCostsDlgWizPage, SchedTaskCostsUtil, 
////            SchedTaskCostsOperation
//
//public class SchedTaskCostsDialogControl {
//
//	class SchedTaskCostsDlg extends WizardDialog {
//
//		protected void finishPressed() {
//			SchedTaskCostsDlgWizPage schedtaskcostsdlgwizpage = (SchedTaskCostsDlgWizPage) getSelectedPage();
//			// int i =
//			// schedtaskcostsdlgwizpage.billCodeCombo.getSelectionIndex();
//			int i = 0;
//			String s = i >= 0 ? cntrl.lovBillCodeList[i].getInternal() : "";
//			cntrl.costDetail.setBillingCode(s);
//			if (cntrl.lovBillSubCodeList.length > i
//					&& cntrl.lovBillSubCodeList[i] != null
//			// && cntrl.lovBillSubCodeList[i].length >
//			// schedtaskcostsdlgwizpage.billSubCodeCombo
//			// .getSelectionIndex())
//			)
//				// cntrl.costDetail
//				// .setBillingSubCode(cntrl.lovBillSubCodeList[i][schedtaskcostsdlgwizpage.billSubCodeCombo
//				// .getSelectionIndex()].getInternal());
//				cntrl.costDetail.setBillingSubCode("");
//			else
//				cntrl.costDetail.setBillingSubCode("");
//			// int j =
//			// schedtaskcostsdlgwizpage.billTypeCombo.getSelectionIndex();
//			int j = 0;
//
//			String s1 = j >= 0 ? cntrl.billTypeList[j].getInternal() : "";
//			cntrl.costDetail.setBillingType(s1);
//			// j = schedtaskcostsdlgwizpage.billRateCombo.getSelectionIndex();
//			j = 0;
//			j = j >= 0 ? j : 0;
//			String s2 = cntrl.billRateList[j];
//			TCComponentBillRate tccomponentbillrate = (TCComponentBillRate) cntrl.billRateCom[j];
//			BillRateDetail billratedetail = cntrl.costDetail.getBillRate();
//			if (tccomponentbillrate != null && billratedetail != null)
//				try {
//					int k = tccomponentbillrate.getIntProperty("tc_type");
//					TCComponent tccomponent = tccomponentbillrate
//							.getReferenceProperty("costvalue_form_tag");
//					String s4 = tccomponent.getProperty("cost");
//					String s3 = tccomponent.getProperty("currency");
//					double d;
//					BigDecimal bigdecimal;
//					try {
//						bigdecimal = CurrencyFormatter.parse(s4, s3);
//						if (bigdecimal != null) {
//							d = bigdecimal.doubleValue();
//						} else {
//							d = 0.0D;
//							bigdecimal = new BigDecimal(0);
//						}
//					} catch (ParseException parseexception) {
//						d = 0.0D;
//						bigdecimal = new BigDecimal(0);
//					}
//					billratedetail.setRateDetail(s2, k, d, bigdecimal, s3,
//							tccomponentbillrate);
//				} catch (TCException tcexception) {
//					MessageBox.post(tcexception);
//				}
//			try {
//				SchedTaskCostsOperation schedtaskcostsoperation = (SchedTaskCostsOperation) r
//						.newInstanceFor("schedTaskCostsOperation",
//								new Object[] { cntrl });
//				sess.queueOperation(schedtaskcostsoperation);
//			} catch (Exception exception) {
//				MessageBox.post(exception);
//			}
//			super.finishPressed();
//		}
//
//		SchedTaskCostsDialogControl cntrl;
//		final SchedTaskCostsDialogControl this$0;
//
//		SchedTaskCostsDlg(org.eclipse.swt.widgets.Shell shell, IWizard iwizard,
//				SchedTaskCostsDialogControl schedtaskcostsdialogcontrol1) {
//			super(shell, iwizard);
//			this$0 = SchedTaskCostsDialogControl.this;
//			cntrl = schedtaskcostsdialogcontrol1;
//		}
//	}
//
//	private final class CreateSchedTaskCostsDialog implements Runnable {
//		private final Shell m_shell;
//		private final IWizard w;
//		SchedTaskCostsDialogControl cntrl;
//		final SchedTaskCostsDialogControl this$0;
//
//		public void run() {
//			try {
//				dialog = new SchedTaskCostsDlg(m_shell, w, cntrl);
//				dialog.create();
//				dialog.getShell().setBackground(SwtWidgetsFactory.getColor());
//			} catch (Exception exception) {
//				SchedTaskCostsDialogControl.logger.error(exception.getClass()
//						.getName(), exception);
//			}
//		}
//
//		private CreateSchedTaskCostsDialog(Shell shell, IWizard iwizard,
//				SchedTaskCostsDialogControl schedtaskcostsdialogcontrol1) {
//			super();
//			this$0 = SchedTaskCostsDialogControl.this;
//
//			m_shell = shell;
//			w = iwizard;
//			cntrl = schedtaskcostsdialogcontrol1;
//		}
//
//	}
//
//	private final class ShowDialog implements Runnable {
//
//		public void run() {
//			try {
//				dialog.open();
//			} catch (Exception exception) {
//				exception.printStackTrace();
//				SchedTaskCostsDialogControl.logger.error(exception.getClass()
//						.getName(), exception);
//			}
//		}
//
//		final SchedTaskCostsDialogControl this$0;
//
//		private ShowDialog() {
//			super();
//			this$0 = SchedTaskCostsDialogControl.this;
//		}
//
//	}
//
//	public SchedTaskCostsDialogControl(
//			ScheduleViewApplication scheduleviewapplication,
//			TCComponentScheduleTask tccomponentscheduletask) {
//		selTask = null;
//		billCodeDBIdx = 0;
//		billSubCodeDBIdx = 0;
//		billTypeDBIdx = 0;
//		billRateDBIdx = 0;
//		taskName = "Unknown TaskName";
//		fixedCosts = null;
//		fixedCostsTableData = (String[][]) null;
//		breakData = (String[][]) null;
//		breakModel = new Vector();
//		breakTree = null;
//		sess = scheduleviewapplication.getSession();
//		r = Registry.getRegistry(this);
//		selTask = tccomponentscheduletask;
//		try {
//			taskName = TaskHelper.getName(tccomponentscheduletask);
//		} catch (TCException tcexception) {
//			logger.error("Task Name is UNKNOWN !!!!");
//		}
//		try {
//			TCComponent tccomponent = TaskHelper
//					.getScheduleForTask(tccomponentscheduletask);
//			if (!tccomponent.getLogicalProperty("is_modifiable")) {
//				MessageBox.post(r.getString("schedTaskCosts.NOPERMISSION"),
//						r.getString("schedTaskCosts.TITLE"), 1);
//				return;
//			}
//		} catch (TCException tcexception1) {
//			logger.error("Error getting Schedule");
//		}
//		TCComponentListOfValues tccomponentlistofvalues = ScheduleUtil
//				.getCostBillCodeLOVComp(sess);
//		lovBillCodeList = ScheduleUtil
//				.getStringHolders(tccomponentlistofvalues);
//		lovBillSubCodeList = ScheduleUtil
//				.getCostBillSubCodeStrList(tccomponentlistofvalues);
//		// TCSession session = (TCSession) AIFUtility.getCurrentApplication()
//		// .getSession();
//
//		TCComponentListOfValues findLOVByName = TCComponentListOfValuesType
//				.findLOVByName(sess, "JCI6_NonLabour");
//		billTypeList = ScheduleUtil.getStringHolders(findLOVByName);
//		if (lovBillCodeList == null || lovBillCodeList.length == 1
//				&& lovBillCodeList[0].toString().trim().length() == 0)
//			MessageBox.post(r.getString("lovErr"),
//					r.getString("schedTaskCosts.TITLE"), 4);
//		TCComponent tccomponent1;
//		TCComponent tccomponent2;
//		RACSelectionSetContainer racselectionsetcontainer;
//		try {
//			TCComponent atccomponent[] = ScheduleUtil.getBillRates(sess);
//			int i = atccomponent != null ? atccomponent.length : 0;
//			billRateCom = new TCComponent[i + 1];
//			billRateCom[0] = null;
//			for (int j = 0; j < i; j++)
//				billRateCom[j + 1] = atccomponent[j];
//
//			billRateList = ScheduleUtil.getBillRatesStrList(billRateCom);
//			if (billRateList != null)
//				billRateList[0] = r.getString("billRates.NONE");
//			currencyList = ScheduleUtil.getCurrencyStrList(sess, false);
//			if (// lovBillCodeList == null || lovBillSubCodeList == null||
//			billTypeList == null || currencyList == null) {
//				logger.error("Schedule Task Cost Bill Code LOV is not found.");
//				return;
//			}
//
//			tccomponent1 = TaskHelper
//					.getScheduleForTask(tccomponentscheduletask);
//			tccomponent2 = TaskHelper.getMasterSchedule();
//			racselectionsetcontainer = new RACSelectionSetContainer(
//					tccomponent2.getUid());
//			racselectionsetcontainer.add(tccomponentscheduletask.getUid(),
//					tccomponent1.getUid());
//			racselectionsetcontainer.addObject(
//					tccomponentscheduletask.getUid(), tccomponentscheduletask);
//			costAbs = new RACCostAbstract(racselectionsetcontainer, sess);
//			costFormMap = costAbs.getCostInformation();
//			CostDetail costdetail = (CostDetail) costFormMap
//					.get(tccomponentscheduletask.getUid());
//			if (containsUnsetRates(costdetail)) {
//				String s = r.getString("noRates.MSG");
//				s = MessageFormat.format(
//						s,
//						new Object[] { CurrencyFormatter.format(
//								Integer.valueOf(0), "") });
//				MessageBox.post(s, r.getString("noRates.TITLE"), 4);
//			}
//			setCostDetail(costdetail);
//			setupDialog();
//			return;
//
//		} catch (TCException tcexception2) {
//			logger.error("Schedule for Task is not found.");
//			return;
//		}
//	}
//
//	private boolean containsUnsetRates(CostDetail costdetail) {
//		label0: {
//			if (costdetail == null)
//				break label0;
//			HashMap hashmap = costdetail.getBreakDown();
//			if (hashmap == null)
//				break label0;
//			Collection collection = hashmap.values();
//			Iterator iterator = collection.iterator();
//			CostLineItem costlineitem;
//			do {
//				do {
//					if (!iterator.hasNext())
//						break label0;
//					costlineitem = (CostLineItem) iterator.next();
//				} while (costlineitem == null);
//				if (costlineitem.getResourceRate() != null
//						&& costlineitem.getResourceRate().wasDefaulted())
//					return true;
//			} while (!containsUnsetRates(costlineitem.getCostDetail()));
//			return true;
//		}
//		return false;
//	}
//
//	public void setCostDetail(CostDetail costdetail) {
//		costDetail = costdetail;
//		if (costDetail != null) {
//			taskName = costDetail.getName();
//			if (wizard != null) {
//				String s = r.getString("schedTaskWizPg.TITLE");
//				s = MessageFormat.format(s, new Object[] { taskName });
//				wizard.setWindowTitle(s);
//			}
//			fixCostMap = costDetail.getFixedCostMap();
//			String s1 = costDetail.getBillingCode();
//			if (s1 != null && s1.length() > 0)
//				billCodeDBIdx = Math.max(0,
//						StringHolder.findInternal(lovBillCodeList, s1));
//			String s2 = costDetail.getBillingSubCode();
//			if (s2 != null && s2.length() > 0)
//				billSubCodeDBIdx = Math.max(0, StringHolder.findInternal(
//						lovBillSubCodeList[billCodeDBIdx], s2));
//			String s3 = costDetail.getBillingType();
//			if (s3 != null && s3.length() > 0)
//				billTypeDBIdx = Math.max(0,
//						StringHolder.findInternal(billTypeList, s3));
//			BillRateDetail billratedetail = costDetail.getBillRate();
//			if (billratedetail != null) {
//				String s5 = billratedetail.getName();
//				if (s5 != null && s5.length() > 0) {
//					billRateDBIdx = Arrays.asList(billRateList).indexOf(s5);
//					if (billRateDBIdx < 0)
//						billRateDBIdx = 0;
//				}
//			}
//		}
//		fixedCosts = new FixedCostsModel(this);
//		if (fixCostMap != null) {
//			fixedCosts.clear();
//			Collection collection = fixCostMap.values();
//			HashSet hashset = new HashSet();
//			int i = 0;
//			Iterator iterator = collection.iterator();
//			do {
//				if (!iterator.hasNext())
//					break;
//				FixedCostDetail fixedcostdetail = (FixedCostDetail) iterator
//						.next();
//				if (fixedcostdetail.getTcComponentState() != 0) {
//					i++;
//					hashset.add(fixedcostdetail);
//				}
//			} while (true);
//			fixedCostsTableData = new String[i][];
//			int k = 0;
//			for (Iterator iterator1 = hashset.iterator(); iterator1.hasNext();) {
//				FixedCostDetail fixedcostdetail1 = (FixedCostDetail) iterator1
//						.next();
//				String s8 = fixedcostdetail1.getName();
//				String s10 = CurrencyFormatter.format(
//						fixedcostdetail1.getEstimatedCost(),
//						fixedcostdetail1.getCurrency());
//				// String s12 = CurrencyFormatter.format(
//				// fixedcostdetail1.getAccruedCost(),
//				// fixedcostdetail1.getCurrency());
//				String s12 = fixedcostdetail1.getBillingType();
//				fixedCosts.addRow(fixedcostdetail1);
//				fixedCostsTableData[k] = (new String[] { s8, s10, s12 });
//				k++;
//			}
//
//		}
//		if (costDetail != null)
//			breakDownMap = costDetail.getBreakDown();
//		if (breakDownMap != null) {
//			breakData = new String[breakDownMap.size()][];
//			Collection collection1 = breakDownMap.values();
//			CostLineItem acostlineitem[] = (CostLineItem[]) collection1
//					.toArray(new CostLineItem[0]);
//			breakTree = new TreeNode(null);
//			breakModel.clear();
//			for (int j = 0; j < breakData.length; j++) {
//				acostlineitem[j].refresh();
//				String s4 = acostlineitem[j].getName();
//				String s6 = HoursFormatter.format(acostlineitem[j]
//						.getEstimatedMinutes());
//				String s7 = HoursFormatter.format(acostlineitem[j]
//						.getAccruedMinutes());
//				String s9 = CostValueFormatter.format(acostlineitem[j]
//						.getEstimatedCost());
//				String s11 = CostValueFormatter.format(acostlineitem[j]
//						.getAccruedCost());
//				breakData[j] = (new String[] { s4, s6, s7, s9, s11 });
//				TreeNode treenode = new TreeNode(acostlineitem[j]);
//				treenode.setParent(breakTree);
//				breakModel.add(treenode);
//			}
//
//		}
//		if (costDetail != null)
//			costDetail.refresh();
//	}
//
//	public void setVariableCostLineItems(boolean flag) {
//		breakDownMap = costDetail.getBreakDown();
//		if (breakDownMap != null) {
//			breakData = new String[breakDownMap.size()][];
//			Collection collection = breakDownMap.values();
//			CostLineItem acostlineitem[] = (CostLineItem[]) collection
//					.toArray(new CostLineItem[0]);
//			breakTree = new TreeNode(null);
//			breakModel.clear();
//			for (int i = 0; i < breakData.length; i++) {
//				acostlineitem[i].refresh();
//				String s = acostlineitem[i].getName();
//				String s1 = HoursFormatter.format(acostlineitem[i]
//						.getEstimatedMinutes());
//				String s2 = HoursFormatter.format(acostlineitem[i]
//						.getAccruedMinutes());
//				String s3 = CostValueFormatter.format(acostlineitem[i]
//						.getEstimatedCost());
//				String s4 = CostValueFormatter.format(acostlineitem[i]
//						.getAccruedCost());
//				breakData[i] = (new String[] { s, s1, s2, s3, s4 });
//				TreeNode treenode = new TreeNode(acostlineitem[i]);
//				treenode.setParent(breakTree);
//				breakModel.add(treenode);
//			}
//
//		}
//		if (flag)
//			costDetail.refresh();
//	}
//
//	public static SchedTaskCostsDialogControl post(
//			ScheduleViewApplication scheduleviewapplication,
//			TCComponentScheduleTask tccomponentscheduletask) {
//		SchedTaskCostsDialogControl schedtaskcostsdialogcontrol = new SchedTaskCostsDialogControl(
//				scheduleviewapplication, tccomponentscheduletask);
//		schedtaskcostsdialogcontrol.setVisible(true);
//		return schedtaskcostsdialogcontrol;
//	}
//
//	private SchedTaskCostsDlgWizard initWizard() {
//		SchedTaskCostsDlgWizard schedtaskcostsdlgwizard = new SchedTaskCostsDlgWizard();
//		String s = r.getString("schedTaskWizPg.TITLE");
//		s = MessageFormat.format(s, new Object[] { taskName });
//		schedtaskcostsdlgwizard.setWindowTitle(s);
//		schedtaskcostsdlgwizard.setHelpAvailable(false);
//		schedtaskcostsdlgwizard.addPage(new SchedTaskCostsDlgWizPage(this, s, r
//				.getString("schedTaskWizPg.DESC"), 3));
//		return schedtaskcostsdlgwizard;
//	}
//
//	private void setupDialog() {
//		wizard = initWizard();
//
//		Shell shell = SchedTaskCostsUtil.getShell(null);
//		if (shell != null)
//			if (UIUtilities.isDisplayThread(shell))
//				(new CreateSchedTaskCostsDialog(shell, wizard, this)).run();
//			else
//				shell.getDisplay().syncExec(
//						new CreateSchedTaskCostsDialog(shell, wizard, this));
//	}
//
//	public void setVisible(boolean flag) {
//		if (dialog == null) {
//			logger.error("No dialog to make visible - oops!");
//			return;
//		}
//		if (flag) {
//			Display display = Display.getCurrent();
//			if (display != null)
//				dialog.open();
//			else
//				Display.getDefault().syncExec(new ShowDialog());
//		}
//	}
//
//	public TCSession getSession() {
//		return sess;
//	}
//
//	private static final Logger logger;
//	Registry r;
//	SchedTaskCostsDlg dialog;
//	TCSession sess;
//	TCComponentScheduleTask selTask;
//	int billCodeDBIdx;
//	int billSubCodeDBIdx;
//	int billTypeDBIdx;
//	int billRateDBIdx;
//	StringHolder lovBillCodeList[];
//	StringHolder lovBillSubCodeList[][];
//	StringHolder billTypeList[];
//	TCComponent billRateCom[];
//	String billRateList[];
//	String currencyList[];
//	String taskName;
//	private SchedTaskCostsDlgWizard wizard;
//	FixedCostsModel fixedCosts;
//	String fixedCostsTableData[][];
//	String breakData[][];
//	RACCostAbstract costAbs;
//	HashMap costFormMap;
//	CostDetail costDetail;
//	HashMap fixCostMap;
//	HashMap breakDownMap;
//	Vector breakModel;
//	TreeNode breakTree;
//
//	static {
//		logger = Logger
//				.getLogger("com/teamcenter/rac/schedule/commands/costs/SchedTaskCostsDialogControl");
//		if (!logger.isDebugEnabled() && Debug.isOn("schedTaskCostsDialog"))
//			logger.setLevel(Level.DEBUG);
//	}
//
//}
//
///*
// * DECOMPILATION REPORT
// * 
// * Decompiled from: C:\Documents and
// * Settings\Administrator\桌面\com.teamcenter.rac
// * .schedule\com.teamcenter.rac.schedule_9000.1.0.jar Total time: 125 ms Jad
// * reported messages/errors: Couldn't resolve all exception handlers in method
// * <init> Exit status: 0 Caught exceptions:
// */