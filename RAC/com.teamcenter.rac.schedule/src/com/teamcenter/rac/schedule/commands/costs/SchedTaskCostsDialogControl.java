/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.schedule.commands.costs;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBillRate;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentSchedulingFixedCost;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.ScheduleViewApplication;
import com.teamcenter.rac.schedule.common.util.CostValueFormatter;
import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
import com.teamcenter.rac.schedule.common.util.SwtWidgetsFactory;
import com.teamcenter.rac.schedule.project.common.costing.CostLineItem;
import com.teamcenter.rac.schedule.project.common.costing.RACSelectionSetContainer;
import com.teamcenter.rac.schedule.project.common.gui.HoursFormatter;
import com.teamcenter.rac.schedule.project.common.gui.StringHolder;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACCostAbstract;
import com.teamcenter.rac.schedule.scheduler.componentutils.PreferenceHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.schedule.soainterface.CostsRunner;
import com.teamcenter.rac.schedule.soainterface.UpdateTaskCostDataRunner;
import com.teamcenter.rac.util.CurrencyFormatter;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SchedTaskCostsDialogControl {
	private final class CreateSchedTaskCostsDialog implements Runnable {
		private final Shell m_shell;
		private final IWizard w;
		SchedTaskCostsDialogControl cntrl;

		private CreateSchedTaskCostsDialog(Shell paramShell,
				IWizard paramIWizard,
				SchedTaskCostsDialogControl paramSchedTaskCostsDialogControl) {
			this.m_shell = paramShell;
			this.w = paramIWizard;
			this.cntrl = paramSchedTaskCostsDialogControl;
		}

		public void run() {
			try {
				SchedTaskCostsDialogControl.this.dialog = new SchedTaskCostsDialogControl.SchedTaskCostsDlg(
						this.m_shell, this.w, this.cntrl);
				SchedTaskCostsDialogControl.this.dialog.create();
				SchedTaskCostsDialogControl.this.dialog.getShell()
						.setBackground(SwtWidgetsFactory.getColor());
			} catch (Exception localException) {
				SchedTaskCostsDialogControl.logger.error(localException
						.getClass().getName(), localException);
			}
		}
	}

	class SchedTaskCostsDlg extends WizardDialog {

		final SchedTaskCostsDialogControl this$0;

		protected void finishPressed() {
			SchedTaskCostsDlgWizPage schedtaskcostsdlgwizpage = (SchedTaskCostsDlgWizPage) getSelectedPage();
			recalculateCosts(schedtaskcostsdlgwizpage);
			SchedTaskCostsDialogControl.existingTempDeletedFixedcost.clear();
			SchedTaskCostsDialogControl.existingTempDirtyFixedcost.clear();
			super.finishPressed();
		}

		protected void cancelPressed() {
			ArrayList arraylist = new ArrayList();
			SchedTaskCostsDlgWizPage schedtaskcostsdlgwizpage = (SchedTaskCostsDlgWizPage) getSelectedPage();
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer afixedcostcontainer[] = schedtaskcostsdlgwizpage.arrayCostDetails[0].fixedCostData;
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer afixedcostcontainer1[] = oldArrayCostDetails[0].fixedCostData;
			ArrayList arraylist1 = new ArrayList(afixedcostcontainer1.length);
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer afixedcostcontainer2[];
			int l = (afixedcostcontainer2 = afixedcostcontainer1).length;
			for (int j = 0; j < l; j++) {
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer = afixedcostcontainer2[j];
				arraylist1.add(fixedcostcontainer.fixedCost);
			}

			for (int i = 0; i < afixedcostcontainer.length; i++)
				if (!arraylist1.contains(afixedcostcontainer[i].fixedCost)) {
					arraylist
							.add(arrayCostDetails[0].fixedCostData[i].fixedCost);
					int k = arraylist.size();
					TCComponentSchedulingFixedCost atccomponentschedulingfixedcost[] = (TCComponentSchedulingFixedCost[]) arraylist
							.toArray(new TCComponentSchedulingFixedCost[k]);
					schedtaskcostsdlgwizpage
							.deleteRowsAndUpdate(atccomponentschedulingfixedcost);
				}

			ArrayList arraylist2 = new ArrayList();
			for (Iterator iterator = SchedTaskCostsDialogControl.existingTempDeletedFixedcost
					.iterator(); iterator.hasNext();) {
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer1 = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer) iterator
						.next();
				boolean flag = false;
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer afixedcostcontainer3[];
				int i2 = (afixedcostcontainer3 = oldArrayCostDetails[0].fixedCostData).length;
				for (int l1 = 0; l1 < i2; l1++) {
					com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer2 = afixedcostcontainer3[l1];
					if (fixedcostcontainer1.fixedCost.getUid() != fixedcostcontainer2.fixedCost
							.getUid())
						continue;
					flag = true;
					break;
				}

				if (!flag)
					arraylist2.add(fixedcostcontainer1);
			}

			SchedTaskCostsDialogControl.existingTempDeletedFixedcost
					.removeAll(arraylist2);
			if (SchedTaskCostsDialogControl.existingTempDeletedFixedcost.size()
					+ SchedTaskCostsDialogControl.existingTempDirtyFixedcost
							.size() > 0) {
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate taskcostupdate = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate();
				taskcostupdate.task = arrayCostDetails[0].task;
				if (SchedTaskCostsDialogControl.existingTempDeletedFixedcost
						.size() > 0)
					taskcostupdate.newCosts = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[]) SchedTaskCostsDialogControl.existingTempDeletedFixedcost
							.toArray(new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[1]);
				if (SchedTaskCostsDialogControl.existingTempDirtyFixedcost
						.size() > 0)
					taskcostupdate.updatedCosts = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[]) SchedTaskCostsDialogControl.existingTempDirtyFixedcost
							.toArray(new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[1]);
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate ataskcostupdate[] = { taskcostupdate };
				UpdateTaskCostDataRunner updatetaskcostdatarunner = new UpdateTaskCostDataRunner(
						ataskcostupdate);
				int j1 = updatetaskcostdatarunner.execute();
				if (j1 == 0) {
					TCComponentScheduleTask atccomponentscheduletask[] = new TCComponentScheduleTask[1];
					TCComponentScheduleTask tccomponentscheduletask = arrayCostDetails[0].task;
					atccomponentscheduletask[0] = tccomponentscheduletask;
					CostsRunner costsrunner = new CostsRunner(
							atccomponentscheduletask);
					int k1 = costsrunner.execute();
				}
			}
			SchedTaskCostsDialogControl.existingTempDeletedFixedcost.clear();
			SchedTaskCostsDialogControl.existingTempDirtyFixedcost.clear();
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate taskcostupdate1 = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate();
			taskcostupdate1.billCode = arrayCostDetails[0].billCode;
			taskcostupdate1.billingType = arrayCostDetails[0].billingType;

			// if (schedtaskcostsdlgwizpage.billRateCombo.getText() != null)
			// taskcostupdate1.billRate = schedtaskcostsdlgwizpage.billRateCombo
			// .getText();
			// modify by wuwei
			taskcostupdate1.billRate = "X1";
			taskcostupdate1.task = schedtaskcostsdlgwizpage.arrayCostDetails[0].task;
			taskcostupdate1.subCode = arrayCostDetails[0].billSubCode;
			try {
				TCComponent atccomponent[] = ScheduleUtil.getBillRates(sess);
				if (atccomponent != null) {
					for (int i1 = 0; i1 < atccomponent.length; i1++)
						// if (atccomponent[i1].getStringProperty("object_name")
						// .equals(schedtaskcostsdlgwizpage.billRateCombo
						// .getText()))
						// modify by wuwei
						if (atccomponent[i1].getStringProperty("object_name")
								.equals("X1")) {
							TCComponentBillRate tccomponentbillrate = (TCComponentBillRate) atccomponent[i1];
							taskcostupdate1.rate = tccomponentbillrate;
						}

				}
			} catch (TCException tcexception) {
				SchedTaskCostsDialogControl.logger.error(tcexception.getClass()
						.getName(), tcexception);
			}
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate ataskcostupdate1[] = { taskcostupdate1 };
			UpdateTaskCostDataRunner updatetaskcostdatarunner1 = new UpdateTaskCostDataRunner(
					ataskcostupdate1);
			updatetaskcostdatarunner1.execute();
			super.cancelPressed();
		}

		public boolean close() {
			for (Iterator iterator = SchedTaskCostsDialogControl.existingTempDeletedFixedcost
					.iterator(); iterator.hasNext();) {
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer) iterator
						.next();
				boolean flag = false;
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer afixedcostcontainer[];
				int l = (afixedcostcontainer = oldArrayCostDetails[0].fixedCostData).length;
				for (int k = 0; k < l; k++) {
					com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer1 = afixedcostcontainer[k];
					if (fixedcostcontainer.name
							.compareTo(fixedcostcontainer1.name) != 0)
						continue;
					flag = true;
					break;
				}

				if (!flag)
					SchedTaskCostsDialogControl.existingTempDeletedFixedcost
							.remove(fixedcostcontainer);
			}

			if (SchedTaskCostsDialogControl.existingTempDeletedFixedcost.size()
					+ SchedTaskCostsDialogControl.existingTempDirtyFixedcost
							.size() > 0) {
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate taskcostupdate = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate();
				taskcostupdate.task = arrayCostDetails[0].task;
				if (SchedTaskCostsDialogControl.existingTempDeletedFixedcost
						.size() > 0)
					taskcostupdate.newCosts = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[]) SchedTaskCostsDialogControl.existingTempDeletedFixedcost
							.toArray(new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[1]);
				if (SchedTaskCostsDialogControl.existingTempDirtyFixedcost
						.size() > 0)
					taskcostupdate.updatedCosts = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[]) SchedTaskCostsDialogControl.existingTempDirtyFixedcost
							.toArray(new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[1]);
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate ataskcostupdate[] = { taskcostupdate };
				UpdateTaskCostDataRunner updatetaskcostdatarunner = new UpdateTaskCostDataRunner(
						ataskcostupdate);
				int i = updatetaskcostdatarunner.execute();
				if (i == 0) {
					TCComponentScheduleTask atccomponentscheduletask[] = new TCComponentScheduleTask[1];
					TCComponentScheduleTask tccomponentscheduletask = arrayCostDetails[0].task;
					atccomponentscheduletask[0] = tccomponentscheduletask;
					CostsRunner costsrunner = new CostsRunner(
							atccomponentscheduletask);
					int j = costsrunner.execute();
				}
			}
			SchedTaskCostsDialogControl.existingTempDeletedFixedcost.clear();
			SchedTaskCostsDialogControl.existingTempDirtyFixedcost.clear();
			return super.close();
		}

		SchedTaskCostsDlg(Shell shell, IWizard iwizard,
				SchedTaskCostsDialogControl schedtaskcostsdialogcontrol1) {
			super(shell, iwizard);
			this$0 = SchedTaskCostsDialogControl.this;
		}
	}

	private final class ShowDialog implements Runnable {

		final SchedTaskCostsDialogControl this$0;

		public void run() {
			try {
				dialog.open();
			} catch (Exception exception) {
				SchedTaskCostsDialogControl.logger.error(exception.getClass()
						.getName(), exception);
			}
		}

		private ShowDialog() {
			super();
			this$0 = SchedTaskCostsDialogControl.this;
		}

		ShowDialog(ShowDialog showdialog) {
			this();
		}
	}

	protected static final double maxResourceLoad = 100D;
	private static final Logger logger;
	Registry reg;
	protected SchedTaskCostsDlg dialog;
	TCSession sess;
	TCComponentScheduleTask selTask;
	int billCodeDBIdx;
	int billSubCodeDBIdx;
	int billTypeDBIdx;
	int billRateDBIdx;
	StringHolder lovBillCodeList[];
	StringHolder lovBillSubCodeList[][];
	StringHolder billTypeList[];
	TCComponent billRateCom[];
	String billRateList[];
	String currencyList[];
	String taskName;
	SchedTaskCostsDlgWizard wizard;
	FixedCostsModel fixedCosts;
	String fixedCostsTableData[][];
	String breakData[][];
	RACCostAbstract costAbs;
	HashMap costFormMap;
	com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailContainer arrayCostDetails[];
	com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailContainer oldArrayCostDetails[];
	com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedCostContainerTemp;
	HashMap fixCostMap;
	HashMap breakDownMap;
	HashMap fixCostDataMap;
	Vector breakModel;
	TreeNode breakTree;
	boolean needTaskDetailsUpdate;
	public com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse updateTaskCostDataResponse;
	protected static List existingTempDeletedFixedcost = new ArrayList();
	protected static List existingTempDirtyFixedcost = new ArrayList();
	private String currencySymbol;
	private Map resourceTimeCostHolder;

	public SchedTaskCostsDialogControl(
			ScheduleViewApplication scheduleviewapplication,
			TCComponentScheduleTask tccomponentscheduletask,
			com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse costdetailresponse) {
		selTask = null;
		billCodeDBIdx = 0;
		billSubCodeDBIdx = 0;
		billTypeDBIdx = 0;
		billRateDBIdx = 0;
		taskName = "Unknown TaskName";
		fixedCosts = null;
		fixedCostsTableData = null;
		breakData = null;
		arrayCostDetails = null;
		oldArrayCostDetails = null;
		fixedCostContainerTemp = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer();
		breakModel = new Vector();
		breakTree = null;
		needTaskDetailsUpdate = false;
		resourceTimeCostHolder = new HashMap();
		sess = scheduleviewapplication.getSession();
		reg = Registry.getRegistry(this);
		selTask = tccomponentscheduletask;
		currencySymbol = PreferenceHelper.getDefCurrency(sess);
		arrayCostDetails = costdetailresponse.costDetails;
		oldArrayCostDetails = costdetailresponse.costDetails;
		try {
			taskName = TaskHelper.getName(tccomponentscheduletask);
		} catch (TCException _ex) {
			logger.error("Task Name is UNKNOWN !!!!");
		}
		try {
			TCComponent tccomponent = TaskHelper
					.getScheduleForTask(tccomponentscheduletask);
			if (!ScheduleUtil
					.canChangeSchedulingData((TCComponentSchedule) tccomponent)) {
				MessageBox.post(reg.getString("schedTaskCosts.NOPERMISSION"),
						reg.getString("schedTaskCosts.TITLE"), 1);
				return;
			}
		} catch (TCException _ex) {
			logger.error("Error getting Schedule");
		}
		TCComponentListOfValues tccomponentlistofvalues = ScheduleUtil
				.getCostBillCodeLOVComp(sess);
		if (tccomponentlistofvalues != null) {
			lovBillCodeList = ScheduleUtil
					.getStringHolders(tccomponentlistofvalues);
			lovBillSubCodeList = ScheduleUtil
					.getCostBillSubCodeStrList(tccomponentlistofvalues);
		}
		
		TCComponentListOfValues findLOVByName = TCComponentListOfValuesType.findLOVByName(this.sess, "JCI6_NonLabour");
		 this.billTypeList = ScheduleUtil.getStringHolders(findLOVByName);
		//billTypeList = ScheduleUtil.getBillTypesStrList(sess);
		if (lovBillCodeList == null || lovBillCodeList.length == 1
				&& lovBillCodeList[0].toString().trim().length() == 0)
			MessageBox.post(reg.getString("lovErr"),
					reg.getString("schedTaskCosts.TITLE"), 4);
		TCComponent tccomponent1;
		TCComponent tccomponent2;
		RACSelectionSetContainer racselectionsetcontainer;
		try {
			billRateList = ScheduleUtil.getBillRatesStrList(sess);
			TCComponent atccomponent[] = ScheduleUtil.getBillRates(sess);
			int i = atccomponent != null ? atccomponent.length : 0;
			billRateCom = new TCComponent[i + 1];
			billRateCom[0] = null;
			for (int j = 0; j < i; j++)
				billRateCom[j + 1] = atccomponent[j];

			billRateList = ScheduleUtil.getBillRatesStrList(billRateCom);
			if (billRateList != null)
				billRateList[0] = reg.getString("billRates.NONE");
			currencyList = ScheduleUtil.getCurrencyStrList(sess, false);
			if (lovBillCodeList == null || lovBillSubCodeList == null
					|| billTypeList == null || currencyList == null) {
				logger.error("Schedule Task Cost Bill Code LOV is not found.");
				return;
			}
		} catch (TCException _ex) {
			logger.error("Schedule for Task is not found.");
			return;
		}
		try {
			tccomponent1 = TaskHelper
					.getScheduleForTask(tccomponentscheduletask);

			tccomponent2 = TaskHelper.getMasterSchedule();
			racselectionsetcontainer = new RACSelectionSetContainer(
					tccomponent2.getUid());
			racselectionsetcontainer.add(tccomponentscheduletask.getUid(),
					tccomponent1.getUid());
			racselectionsetcontainer.addObject(
					tccomponentscheduletask.getUid(), tccomponentscheduletask);
			costAbs = new RACCostAbstract(racselectionsetcontainer, sess);

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setCostDetail();
		setupDialog();
		return;
	}

	public void setCostDetail() {
		if (arrayCostDetails != null) {
			taskName = arrayCostDetails[0].task.toString();
			if (wizard != null) {
				String s = reg.getString("schedTaskWizPg.TITLE");
				s = MessageFormat.format(s, new Object[] { taskName });
				wizard.setWindowTitle(s);
			}
			fixCostMap = null;
			String s1 = "a";
			s1 = arrayCostDetails[0].billCode;
			if (s1 != null && s1.length() > 0)
				billCodeDBIdx = Math.max(0,
						StringHolder.findInternal(lovBillCodeList, s1));
			String s2 = arrayCostDetails[0].billSubCode;
			if (s2 != null && s2.length() > 0)
				billSubCodeDBIdx = Math.max(0, StringHolder.findInternal(
						lovBillSubCodeList[billCodeDBIdx], s2));
			String s3 = arrayCostDetails[0].billingType;
			if (s3 != null && s3.length() > 0)
				billTypeDBIdx = Math.max(0,
						StringHolder.findInternal(billTypeList, s3));
			TCComponentBillRate tccomponentbillrate = arrayCostDetails[0].billRate;
			if (tccomponentbillrate != null) {
				String s4 = null;
				try {
					s4 = tccomponentbillrate.getProperty("object_name");
				} catch (TCException _ex) {
					logger.error("Error getting object_name of bill rate");
				}
				int j = 0;
				if (s4 != null) {
					for (int l = 0; l < billRateList.length; l++) {
						int j1 = billRateList[l].compareToIgnoreCase(s4);
						if (j1 == 0)
							j = l;
					}

				}
				billRateDBIdx = j;
			}
			fixedCosts = new FixedCostsModel(this);
			fixedCosts.clear();
			fixedCostsTableData = new String[arrayCostDetails[0].fixedCostData.length][];
			fixCostDataMap = new HashMap();
			for (int i = 0; i < arrayCostDetails[0].fixedCostData.length; i++) {
				String s6 = arrayCostDetails[0].fixedCostData[i].name;
				Double double1 = Double.valueOf(0.0D);
				if (arrayCostDetails[0].fixedCostData[i].estimate
						.startsWith("$"))
					double1 = Double
							.valueOf(arrayCostDetails[0].fixedCostData[i].estimate
									.substring(1));
				else
					double1 = Double
							.valueOf(arrayCostDetails[0].fixedCostData[i].estimate);
				Double double2 = Double.valueOf(0.0D);
				if (arrayCostDetails[0].fixedCostData[i].useActual) {
					if (arrayCostDetails[0].fixedCostData[i].actual
							.startsWith("$"))
						double2 = Double
								.valueOf(arrayCostDetails[0].fixedCostData[i].actual
										.substring(1));
					else
						double2 = Double
								.valueOf(arrayCostDetails[0].fixedCostData[i].actual);
				} else {
					try {
						if (arrayCostDetails[0].fixedCostData[i].accrualType == 0) {
							String s11 = TaskHelper
									.getFnd0State(arrayCostDetails[0].task);
							if (s11.equalsIgnoreCase("in_progress"))
								double2 = double1;
							else if (s11.equalsIgnoreCase("complete"))
								double2 = double1;
							else
								double2 = new Double(0.0D);
						} else if (arrayCostDetails[0].fixedCostData[i].accrualType == 1) {
							double d = TaskHelper
									.getPercentageComplete(arrayCostDetails[0].task);
							double2 = Double
									.valueOf((double1.doubleValue() * d) / 100D);
						} else {
							String s12 = TaskHelper
									.getFnd0State(arrayCostDetails[0].task);
							if (s12.equalsIgnoreCase("complete"))
								double2 = double1;
							else
								double2 = new Double(0.0D);
						}
					} catch (TCException tcexception1) {
						logger.error(tcexception1.getClass().getName(),
								tcexception1);
					}
				}
				String s13 = CurrencyFormatter.format(
						Double.valueOf(double1.doubleValue()),
						arrayCostDetails[0].fixedCostData[i].currency);
				String s16 = CurrencyFormatter.format(
						Double.valueOf(double2.doubleValue()),
						arrayCostDetails[0].fixedCostData[i].currency);
				TCComponentSchedulingFixedCost tccomponentschedulingfixedcost = arrayCostDetails[0].fixedCostData[i].fixedCost;
				
				//new by wuwei
				String newStr1=arrayCostDetails[0].fixedCostData[i].billingType;
				System.out.println("newStr1-->"+newStr1);
				fixedCostsTableData[i] = (new String[] { s6, s13, newStr1,
						tccomponentschedulingfixedcost.getUid() });
			}

			if (arrayCostDetails[0].numchildren > 0) {
				String s5 = "";
				breakData = new String[arrayCostDetails[0].numchildren][];
				breakTree = new TreeNode(null);
				for (int k = 0; k < breakData.length; k++) {
					if (arrayCostDetails[0].taskCostDetails.length > 0) {
						TCComponentScheduleTask tccomponentscheduletask1 = arrayCostDetails[0].taskCostDetails[k].task;
						s5 = tccomponentscheduletask1.toString();
					}
					String s8 = "";
					if (s5 != null && s5.length() > 0)
						s8 = s5;
					String s9 = HoursFormatter
							.format(arrayCostDetails[0].taskCostDetails[k].totalEstimatedMinutes);
					String s14 = HoursFormatter
							.format(arrayCostDetails[0].taskCostDetails[k].totalAccruedMinutes);
					String s17 = CurrencyFormatter
							.format((new BigDecimal(
									String.valueOf(arrayCostDetails[0].taskCostDetails[k].totalEstimatedCost)))
									.setScale(2, 4), currencySymbol);
					String s19 = CurrencyFormatter
							.format((new BigDecimal(
									String.valueOf(arrayCostDetails[0].taskCostDetails[k].totalAccruedCost)))
									.setScale(2, 4), currencySymbol);
					breakData[k] = (new String[] { s8, s9, s14, s17, s19 });
					TreeNode treenode1 = new TreeNode(breakData[k]);
					treenode1.setParent(breakTree);
					breakModel.add(treenode1);
				}

			} else {
				try {
					breakModel.clear();
					TCComponentScheduleTask tccomponentscheduletask = arrayCostDetails[0].task;
					ResourceCostUtil.getEstimatedHoursAndCostsPerResource(
							tccomponentscheduletask, resourceTimeCostHolder);
					if (TaskHelper.getApprovedWork(tccomponentscheduletask) == 0) {
						ResourceCostUtil
								.allocateAllWorkPerResource(
										tccomponentscheduletask,
										resourceTimeCostHolder);
					} else {
						ResourceCostUtil
								.processTimesheetsPerResource(
										tccomponentscheduletask,
										resourceTimeCostHolder);
						if (TaskHelper.getApprovedWork(tccomponentscheduletask) < TaskHelper
								.getWorkComplete(tccomponentscheduletask))
							ResourceCostUtil
									.allocateNonTimesheetWorkPerResource(
											tccomponentscheduletask,
											resourceTimeCostHolder);
					}
					breakTree = new TreeNode(null);
					breakData = resourceTimeCostHolder.size() <= 0
							|| resourceTimeCostHolder == null ? new String[1][]
							: new String[resourceTimeCostHolder.size()][];
					String s7 = "";
					int i1 = 0;
					if (resourceTimeCostHolder != null
							&& resourceTimeCostHolder.size() > 0) {
						for (Iterator iterator = resourceTimeCostHolder
								.values().iterator(); iterator.hasNext();) {
							ResourceTimeCostHolder resourcetimecostholder = (ResourceTimeCostHolder) iterator
									.next();
							if (resourcetimecostholder.getResUid() != null) {
								s7 = getSession().stringToComponent(
										resourcetimecostholder.getResUid())
										.toString();
								breakData[i1] = (new String[] {
										s7 == null || s7.length() <= 0 ? ""
												: s7,
										HoursFormatter
												.format(resourcetimecostholder
														.getEstimatedMinutes()),
										HoursFormatter
												.format(resourcetimecostholder
														.getTotalAccruedMinutes()),
										CurrencyFormatter.format(Double
												.valueOf(resourcetimecostholder
														.getEstimatedCost()
														.doubleValue()),
												currencySymbol),
										CurrencyFormatter.format(Double
												.valueOf(resourcetimecostholder
														.getTotalAccruedCost()
														.doubleValue()),
												currencySymbol) });
								TreeNode treenode = new TreeNode(breakData[i1]);
								treenode.setParent(breakTree);
								breakModel.add(treenode);
								i1++;
							}
						}

					} else {
						String s10 = "";
						if (s7 != null && s7.length() > 0)
							s10 = s7;
						String s15 = HoursFormatter.format(TaskHelper
								.getWorkEstimate(tccomponentscheduletask));
						String s18 = HoursFormatter.format(TaskHelper
								.getWorkComplete(tccomponentscheduletask));
						double d1 = arrayCostDetails[0].totalEstimatedCost;
						BigDecimal bigdecimal = BigDecimal.valueOf(d1);
						double d2 = arrayCostDetails[0].totalAccruedCost;
						BigDecimal bigdecimal1 = BigDecimal.valueOf(d2);
						if (arrayCostDetails[0].fixedCostData.length > 0) {
							for (int k1 = 0; k1 < arrayCostDetails[0].fixedCostData.length; k1++)
								if (arrayCostDetails[0].fixedCostData[k1].estimate != null) {
									String s21 = arrayCostDetails[0].fixedCostData[k1].estimate;
									BigDecimal bigdecimal2 = new BigDecimal(s21);
									bigdecimal = bigdecimal
											.subtract(bigdecimal2);
									if (d2 != 0.0D)
										if (arrayCostDetails[0].fixedCostData[k1].useActual) {
											String s23 = arrayCostDetails[0].fixedCostData[k1].actual;
											BigDecimal bigdecimal3 = new BigDecimal(
													s23);
											bigdecimal1 = bigdecimal1
													.subtract(bigdecimal3);
										} else if (arrayCostDetails[0].fixedCostData[k1].accrualType == 0) {
											String s24 = TaskHelper
													.getFnd0State(arrayCostDetails[0].task);
											if (s24.equalsIgnoreCase("in_progress"))
												bigdecimal1 = bigdecimal1
														.subtract(bigdecimal2);
											else if (s24
													.equalsIgnoreCase("complete"))
												bigdecimal1 = bigdecimal1
														.subtract(bigdecimal2);
											else
												bigdecimal1 = bigdecimal1
														.subtract(BigDecimal
																.valueOf(0L));
										} else if (arrayCostDetails[0].fixedCostData[k1].accrualType == 1) {
											double d3 = TaskHelper
													.getPercentageComplete(arrayCostDetails[0].task);
											BigDecimal bigdecimal4 = bigdecimal2
													.multiply(
															BigDecimal
																	.valueOf(d3))
													.divide(BigDecimal
															.valueOf(100L));
											bigdecimal1 = bigdecimal1
													.subtract(bigdecimal4);
										} else {
											String s25 = TaskHelper
													.getFnd0State(arrayCostDetails[0].task);
											if (s25.equalsIgnoreCase("complete"))
												bigdecimal1 = bigdecimal1
														.subtract(bigdecimal2);
											else
												bigdecimal1 = bigdecimal1
														.subtract(BigDecimal
																.valueOf(0L));
										}
								}

						}
						String s20 = CurrencyFormatter.format(bigdecimal,
								currencySymbol);
						String s22 = CurrencyFormatter.format(bigdecimal1,
								currencySymbol);
						breakData[0] = (new String[] { s10, s15, s18, s20, s22 });
						TreeNode treenode2 = new TreeNode(breakData[0]);
						treenode2.setParent(breakTree);
						breakModel.add(treenode2);
					}
				} catch (TCException tcexception) {
					logger.error(tcexception.getClass().getName(), tcexception);
				}
			}
		}
	}

	public void setVariableCostLineItems(boolean flag) {
		if (breakDownMap != null) {
			breakData = new String[breakDownMap.size()][];
			Collection collection = breakDownMap.values();
			CostLineItem acostlineitem[] = (CostLineItem[]) collection
					.toArray(new CostLineItem[0]);
			breakTree = new TreeNode(null);
			breakModel.clear();
			for (int i = 0; i < breakData.length; i++) {
				acostlineitem[i].refresh();
				String s = acostlineitem[i].getName();
				String s1 = HoursFormatter.format(acostlineitem[i]
						.getEstimatedMinutes());
				String s2 = HoursFormatter.format(acostlineitem[i]
						.getAccruedMinutes());
				String s3 = CostValueFormatter.format(acostlineitem[i]
						.getEstimatedCost());
				String s4 = CostValueFormatter.format(acostlineitem[i]
						.getAccruedCost());
				breakData[i] = (new String[] { s, s1, s2, s3, s4 });
				TreeNode treenode = new TreeNode(acostlineitem[i]);
				treenode.setParent(breakTree);
				breakModel.add(treenode);
			}

		}
	}

	public static SchedTaskCostsDialogControl post(
			ScheduleViewApplication scheduleviewapplication,
			TCComponentScheduleTask tccomponentscheduletask,
			com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse costdetailresponse) {
		SchedTaskCostsDialogControl schedtaskcostsdialogcontrol = new SchedTaskCostsDialogControl(
				scheduleviewapplication, tccomponentscheduletask,
				costdetailresponse);
		schedtaskcostsdialogcontrol.setVisible(true);
		return schedtaskcostsdialogcontrol;
	}

	private SchedTaskCostsDlgWizard initWizard() {
		SchedTaskCostsDlgWizard schedtaskcostsdlgwizard = new SchedTaskCostsDlgWizard();
		String s = reg.getString("schedTaskWizPg.TITLE");
		s = MessageFormat.format(s, new Object[] { taskName });
		schedtaskcostsdlgwizard.setWindowTitle(s);
		schedtaskcostsdlgwizard.setHelpAvailable(false);
		schedtaskcostsdlgwizard.addPage(new SchedTaskCostsDlgWizPage(this, s,
				reg.getString("schedTaskWizPg.DESC"), 3, arrayCostDetails,
				fixedCostContainerTemp));
		return schedtaskcostsdlgwizard;
	}

	private void setupDialog() {
		wizard = initWizard();
		Shell shell = SchedTaskCostsUtil.getShell(null);
		if (shell != null)
			if (UIUtilities.isDisplayThread(shell))
				(new CreateSchedTaskCostsDialog(shell, wizard, this)).run();
			else
				shell.getDisplay().syncExec(
						new CreateSchedTaskCostsDialog(shell, wizard, this));

	}

	public void setVisible(boolean flag) {
		if (dialog == null) {
			logger.error("No dialog to make visible - oops!");
			return;
		}
		if (flag) {
			Display display = Display.getCurrent();
			if (display != null)
				dialog.open();
			else
				Display.getDefault().syncExec(new ShowDialog(null));
		}
	}

	public void recalculateCosts(
			SchedTaskCostsDlgWizPage schedtaskcostsdlgwizpage) {
		SchedTaskCostsDlgWizPage schedtaskcostsdlgwizpage1 = schedtaskcostsdlgwizpage;
		fixedCostContainerTemp = schedtaskcostsdlgwizpage.fixedCostContainerTemp;
		if (oldArrayCostDetails[0].billRate != null)
			oldArrayCostDetails[0].billRate.getObjectString();
		com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate taskcostupdate = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate();
		taskcostupdate.billCode = StringHolder.findInternalValue(
				lovBillCodeList,
				// modify by wuwei
				"unassigned");
		// schedtaskcostsdlgwizpage.billCodeCombo.getText());
		taskcostupdate.billingType = StringHolder.findInternalValue(
				billTypeList, "Normal Hours");// schedtaskcostsdlgwizpage.billTypeCombo.getText()
		// if (schedtaskcostsdlgwizpage.billRateCombo.getText() != null)
		// taskcostupdate.billRate = schedtaskcostsdlgwizpage.billRateCombo
		// .getText();
		taskcostupdate.billRate = "X1";
		taskcostupdate.task = schedtaskcostsdlgwizpage.arrayCostDetails[0].task;
		taskcostupdate.subCode = StringHolder.findInternalValue(
				lovBillSubCodeList[0], "unassigned");

		// taskcostupdate.subCode = StringHolder.findInternalValue(
		// lovBillSubCodeList[schedtaskcostsdlgwizpage.billCodeCombo
		// .getSelectionIndex()],
		// schedtaskcostsdlgwizpage.billSubCodeCombo.getText());
		try {
			TCComponent atccomponent[] = ScheduleUtil.getBillRates(sess);
			if (atccomponent != null) {
				for (int i = 0; i < atccomponent.length; i++)
					if (atccomponent[i].getStringProperty("object_name")
							.equals("X1")) {// schedtaskcostsdlgwizpage.billRateCombo.getText()
						TCComponentBillRate tccomponentbillrate = (TCComponentBillRate) atccomponent[i];
						taskcostupdate.rate = tccomponentbillrate;
					}

			}
		} catch (TCException tcexception) {
			logger.error(tcexception.getClass().getName(), tcexception);
		}
		com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate ataskcostupdate[] = { taskcostupdate };
		UpdateTaskCostDataRunner updatetaskcostdatarunner = new UpdateTaskCostDataRunner(
				ataskcostupdate);
		int j = updatetaskcostdatarunner.execute();
		if (j == 0) {
			Object obj = updatetaskcostdatarunner.getResults();
			if (obj instanceof com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse) {
				com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse updatetaskcostdataresponse = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse) obj;
				TCComponentScheduleTask atccomponentscheduletask[] = new TCComponentScheduleTask[1];
				atccomponentscheduletask[0] = updatetaskcostdataresponse.responses[0].updatedTask;
				CostsRunner costsrunner = new CostsRunner(
						atccomponentscheduletask);
				int k = costsrunner.execute();
				Object obj2 = null;
				if (k == 0) {
					Object obj1 = costsrunner.getResults();
					if (obj1 instanceof com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse) {
						com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse costdetailresponse = (com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse) obj1;
						if (costdetailresponse != null) {
							schedtaskcostsdlgwizpage1.arrayCostDetails = costdetailresponse.costDetails;
							oldArrayCostDetails = costdetailresponse.costDetails;
						}
					}
				}
			}
		}
	}

	public TCSession getSession() {
		return sess;
	}

	static {
		logger = Logger
				.getLogger(com.teamcenter.rac.schedule.commands.costs.SchedTaskCostsDialogControl.class);
		if (!logger.isDebugEnabled() && Debug.isOn("schedTaskCostsDialog"))
			logger.setLevel(Level.DEBUG);
	}

}
