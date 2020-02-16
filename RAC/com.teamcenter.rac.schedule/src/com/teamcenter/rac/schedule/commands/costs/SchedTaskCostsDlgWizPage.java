/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.schedule.commands.costs;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBillRate;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentSchedulingFixedCost;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
import com.teamcenter.rac.schedule.common.util.SwtWidgetsFactory;
import com.teamcenter.rac.schedule.project.common.costing.BillRateDetail;
import com.teamcenter.rac.schedule.project.common.costing.FixedCostDetail;
import com.teamcenter.rac.schedule.project.common.gui.HoursFormatter;
import com.teamcenter.rac.schedule.project.common.gui.StringHolder;
import com.teamcenter.rac.schedule.project.common.l10n.ClassType;
import com.teamcenter.rac.schedule.project.common.l10n.DisplayNameCache;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACCostAbstract;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
import com.teamcenter.rac.schedule.scheduler.componentutils.PreferenceHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.schedule.soainterface.CostsRunner;
import com.teamcenter.rac.schedule.soainterface.UpdateTaskCostDataRunner;
import com.teamcenter.rac.util.CurrencyFormatter;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdateResponse;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse;
import com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement;
import com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailContainer;
import com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

//TC11 ÐÞ¸Ä
public class SchedTaskCostsDlgWizPage extends WizardPage {
	private Registry registry;
	private DisplayNameCache nameCache;
	private SchedTaskCostsDialogControl control;
	private FixedCostsModel fixCostModel;
	private Vector breakModel;
	private Button rollUpBut;
	private Button drillDnBut;
	Button addFixedCostBut;
	Button delFixedCostBut;
	Button detailFixedCostBut;

	// modify by wuwei
	// Button applyRateModifierBut;

	ScheduleManagement.CostDetailContainer[] arrayCostDetails = null;
	private int numCols = 3;
	private Table fixCostTable;
	private Table breakTable;

	// modify by wuwei
	// Combo billCodeCombo;
	// Combo billSubCodeCombo;
	// Combo billTypeCombo;
	
	// Combo billRateCombo;
	Label estCostLabel;

	// modify by wuwei
	// Label accCostLabel;

	Label estHoursLabel;
	Label accHoursLabel;

	com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedCostContainerTemp = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer();
	protected TCComponentSchedulingFixedCost[] fixedCostDeleteArray;
	private static int billRatePreviousIdx;
	private static final int NOTHING_CHANGED = -1;
	private static final int BILLRATE_CHANGED = 0;
	private static final int FIXEDCOST_CHANGED = 1;
	boolean needUpdate = false;
	boolean fireFixedCostDetailsBool = false;
	boolean fireFixedCostNewBool = false;
	private Stack<TCComponentScheduleTask> parentStack;
	boolean parentSummaryTaskExist = false;
	boolean parentExist = false;
	private static final Logger logger = Logger
			.getLogger(SchedTaskCostsDlgWizPage.class);
	private TCSession sess;
	private String currencySymbol;
	private Map<String, ResourceTimeCostHolder> resourceTimeCostHolder = new HashMap();

	public SchedTaskCostsDlgWizPage(
			SchedTaskCostsDialogControl paramSchedTaskCostsDialogControl,
			String paramString1,
			String paramString2,
			int paramInt,
			ScheduleManagement.CostDetailContainer[] paramArrayOfCostDetailContainer,
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer paramFixedCostContainer) {
		super(paramString1);
		this.nameCache = RACInterface
				.getDisplayNameCache(paramSchedTaskCostsDialogControl
						.getSession());
		this.sess = paramSchedTaskCostsDialogControl.getSession();
		new Stack();
		this.parentStack = new Stack();
		this.control = paramSchedTaskCostsDialogControl;
		this.fixCostModel = paramSchedTaskCostsDialogControl.fixedCosts;
		this.breakModel = paramSchedTaskCostsDialogControl.breakModel;
		this.arrayCostDetails = paramArrayOfCostDetailContainer;
		this.fixedCostContainerTemp = paramFixedCostContainer;
		this.currencySymbol = PreferenceHelper.getDefCurrency(this.sess);
		setDescription(paramString2);
		this.numCols = paramInt;
		this.registry = Registry.getRegistry(this);
	}

	public void createControl(Composite paramComposite) {
		GridLayout localGridLayout = new GridLayout();
		localGridLayout.numColumns = this.numCols;
		paramComposite.setLayout(localGridLayout);
		boolean bool = true;
		String str1 = "";
		String str2 = "";
		String str3 = "";
		String str4 = "";
		if (this.arrayCostDetails.length > 0) {

			// modify by wuwei
			NumberFormat localNumberFormat = NumberFormat
					.getNumberInstance(Locale.CHINA);

			// localNumberFormat.format
			// str1 = CurrencyFormatter.format(new BigDecimal(
			// String.valueOf(this.arrayCostDetails[0].totalEstimatedCost))
			// .setScale(2, 4), this.currencySymbol);
			// str2 = CurrencyFormatter
			// .format(new BigDecimal(String
			// .valueOf(this.arrayCostDetails[0].totalAccruedCost))
			// .setScale(2, 4), this.currencySymbol);

			str1 = localNumberFormat.format(new BigDecimal(String
					.valueOf(this.arrayCostDetails[0].totalEstimatedCost))
					.setScale(2, 4));

			str2 = localNumberFormat.format(new BigDecimal(String
					.valueOf(this.arrayCostDetails[0].totalAccruedCost))
					.setScale(2, 4));
		}
		this.estCostLabel = SwtWidgetsFactory.addLabel(paramComposite,
				this.registry.getString("costEstimate"), str1, 100, 131072,
				bool);

		// modify by wuwei
		// this.accCostLabel = SwtWidgetsFactory.addLabel(paramComposite,
		// this.registry.getString("costAccrue"), str2, 100, 131072, bool);

		str3 = HoursFormatter
				.format(this.arrayCostDetails[0].totalEstimatedMinutes);
		this.estHoursLabel = SwtWidgetsFactory.addLabel(paramComposite,
				this.registry.getString("workEstimate"), str3, 100, 131072,
				bool);
		str4 = HoursFormatter
				.format(this.arrayCostDetails[0].totalAccruedMinutes);
		this.accHoursLabel = SwtWidgetsFactory.addLabel(paramComposite,
				this.registry.getString("workAccrue"), str4, 100, 131072, bool);
		SwtWidgetsFactory.addBlank(paramComposite, 3);
		SwtWidgetsFactory.addLabel(paramComposite,
				this.registry.getString("fixCost"), "", 100, 131072, bool);
//		this.fixCostTable = SwtWidgetsFactory.addTable(
//				paramComposite,
//				0,
//				125,
//				new String[] {
//						this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
//								"cost_name"),
//						this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
//								"est_costvalue_form_tag"),
//						"Bill Type", "" },
//				new int[] { 16384, 131072, 131072, 131072 },
//				this.control.fixedCostsTableData, 2, 5);
		
		this.fixCostTable = SwtWidgetsFactory.addTable(paramComposite,
					0,
					125,
					new String[] { 
					this.nameCache.getAttrDisplayName(ClassType.FIXED_COST, "Cost Name"),
					this.nameCache.getAttrDisplayName(ClassType.FIXED_COST, "Estimated Cost"),
					"Bill Type" ,""}, new int[] { 16384, 131072, 131072 , 131072}, this.control.fixedCostsTableData, 2, 5);

		SwtWidgetsFactory.addBlank(paramComposite, 1);
		this.fixCostTable.getColumn(3).setWidth(0);
		this.fixCostTable.getColumn(3).setResizable(false);
		this.fixCostTable.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent paramMouseEvent) {
				super.mouseDoubleClick(paramMouseEvent);
				TableItem localTableItem = SchedTaskCostsDlgWizPage.this.fixCostTable
						.getItem(new Point(paramMouseEvent.x, paramMouseEvent.y));
//				if (localTableItem == null) {
//					SchedTaskCostsDlgWizPage.this.fireFixedCostNewBool = true;
//					SchedTaskCostsDlgWizPage.this
//							.fireFixedCostNew(paramMouseEvent.widget
//									.getDisplay().getActiveShell());
//				} else {
//					SchedTaskCostsDlgWizPage.this.fireFixedCostDetailsBool = true;
//					SchedTaskCostsDlgWizPage.this
//							.fireFixedCostDetails(paramMouseEvent.widget
//									.getDisplay().getActiveShell());
//				}
				
				if (localTableItem == null) {
						SchedTaskCostsDlgWizPage.this.fireFixedCostNewBool = true;
			          SchedTaskCostsDlgWizPage.this.fireFixedCostNew(paramMouseEvent.widget.getDisplay().getActiveShell());
			        }
			        else{
			        	SchedTaskCostsDlgWizPage.this.fireFixedCostDetailsBool = true;
			          SchedTaskCostsDlgWizPage.this.fireFixedCostDetails(paramMouseEvent.widget.getDisplay().getActiveShell());
			        }
			        
			    }
		});
		
		this.addFixedCostBut = SwtWidgetsFactory.addButton(paramComposite,
				this.registry.getString("buttonNew"));
		this.addFixedCostBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				SchedTaskCostsDlgWizPage.this.fireFixedCostNewBool = true;
				SchedTaskCostsDlgWizPage.this
						.fireFixedCostNew(paramSelectionEvent.display
								.getActiveShell());
			}
		});
		this.delFixedCostBut = SwtWidgetsFactory.addButton(paramComposite,
				this.registry.getString("buttonDel"));
		this.delFixedCostBut.setEnabled(false);
		this.delFixedCostBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				int[] arrayOfInt1 = SchedTaskCostsDlgWizPage.this.fixCostTable
						.getSelectionIndices();
				int i = 0;
				if ((arrayOfInt1 == null) || (arrayOfInt1.length <= 0))
					return;
				SchedTaskCostsDlgWizPage.this.fixedCostDeleteArray = new TCComponentSchedulingFixedCost[arrayOfInt1.length];
				for (int j : arrayOfInt1) {
					// modify bu wuwei
					// new int[arrayOfInt1.length];
					TableItem localTableItem = SchedTaskCostsDlgWizPage.this.fixCostTable
							.getItem(j);
					String str1 = localTableItem.getText(3);

					com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer localFixedCostContainer = null;
					for (int i1 = 0; i1 < SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData.length; ++i1) {
						String str2 = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].fixedCost
								.getUid();
						if ((!(str2.equalsIgnoreCase(str1)))
								|| (SchedTaskCostsDlgWizPage.this.fixCostTable
										.getItem(j) == null))
							continue;
						SchedTaskCostsDlgWizPage.this.fixedCostDeleteArray[i] = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].fixedCost;
						++i;
						localFixedCostContainer = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer();
						localFixedCostContainer.accrualType = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].accrualType;
						localFixedCostContainer.actual = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].actual;
						localFixedCostContainer.billCode = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].billCode;
						localFixedCostContainer.billingType = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].billingType;
						localFixedCostContainer.currency = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].currency;
						localFixedCostContainer.estimate = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].estimate;
						localFixedCostContainer.fixedCost = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].fixedCost;
						localFixedCostContainer.name = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].name;
						localFixedCostContainer.subCode = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].subCode;
						localFixedCostContainer.useActual = SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].fixedCostData[i1].useActual;
						SchedTaskCostsDialogControl.existingTempDeletedFixedcost
								.add(localFixedCostContainer);
					}
				}

				SchedTaskCostsDlgWizPage.this
						.deleteRowsAndUpdate(SchedTaskCostsDlgWizPage.this.fixedCostDeleteArray);
			}
		});
		this.detailFixedCostBut = SwtWidgetsFactory.addButton(paramComposite,
				this.registry.getString("buttonDetail"));
		this.detailFixedCostBut.setEnabled(false);
		this.detailFixedCostBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				SchedTaskCostsDlgWizPage.this.fireFixedCostDetailsBool = true;
				SchedTaskCostsDlgWizPage.this
						.fireFixedCostDetails(paramSelectionEvent.display
								.getActiveShell());
			}
		});
		this.fixCostTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				super.widgetSelected(paramSelectionEvent);
				int i = SchedTaskCostsDlgWizPage.this.fixCostTable
						.getSelectionCount();
				SchedTaskCostsDlgWizPage.this.detailFixedCostBut
						.setEnabled(i == 1);
				SchedTaskCostsDlgWizPage.this.delFixedCostBut
						.setEnabled(i != 0);
			}
		});
		SwtWidgetsFactory.addBlank(paramComposite, 1);

		// modify by wuwei
		// this.billCodeCombo = SwtWidgetsFactory.addCombo(paramComposite,
		// this.nameCache.getAttrDisplayName(ClassType.SCHEDULE_TASK,
		// "bill_code"), this.control.lovBillCodeList, 0, bool);
		StringHolder[] arrayOfStringHolder = new StringHolder[0];
		if ((this.control.lovBillSubCodeList.length > this.control.billCodeDBIdx)
				&& (this.control.billCodeDBIdx > -1)
				&& (this.control.lovBillSubCodeList[this.control.billCodeDBIdx] != null))
			arrayOfStringHolder = this.control.lovBillSubCodeList[this.control.billCodeDBIdx];

		// modify by wuwei
		// this.billSubCodeCombo = SwtWidgetsFactory.addCombo(paramComposite,
		// this.nameCache.getAttrDisplayName(ClassType.SCHEDULE_TASK,
		// "bill_sub_code"), arrayOfStringHolder, 0, bool);
		// this.billCodeCombo.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent paramSelectionEvent) {
		// int i = SchedTaskCostsDlgWizPage.this.billCodeCombo
		// .getSelectionIndex();
		// SchedTaskCostsDlgWizPage.this.setBillCodeComboSelection(i, 0);
		// }
		// });
		// setBillCodeComboSelection(this.control.billCodeDBIdx,
		// this.control.billSubCodeDBIdx);

		//this.billTypeCombo = SwtWidgetsFactory.addCombo(paramComposite,
		//		this.nameCache.getAttrDisplayName(ClassType.SCHEDULE_TASK,
		//				"bill_type"), this.control.billTypeList, 0, bool);
		//this.billTypeCombo.select(this.control.billTypeDBIdx);
		
		//this.billRateCombo = SwtWidgetsFactory.addCombo(paramComposite,
		//		this.nameCache.getAttrDisplayName(ClassType.SCHEDULE_TASK,
		//				"billrate_tag"), this.control.billRateList, 0, bool);
		//this.billRateCombo.select(this.control.billRateDBIdx);

		// if ((TaskHelper.isSummaryTask(this.control.selTask))
		// || (TaskHelper.isScheduleSummaryTask(this.control.selTask))
		// || (TaskHelper.isPhaseTask(this.control.selTask)))
		// this.billRateCombo.setEnabled(false);
		// else
		// this.billRateCombo.setEnabled(true);

		//setBillCodeComboSelection(this.control.billCodeDBIdx, this.control.billSubCodeDBIdx);
		billRatePreviousIdx = this.control.billRateDBIdx;
		// this.billRateCombo.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent paramSelectionEvent) {
		// SchedTaskCostsDlgWizPage.this.applyRateModifierBut
		// .setEnabled(true);
		// }
		// });
		
		//setBillCodeComboSelection(control.billCodeDBIdx, control.billSubCodeDBIdx);
		SwtWidgetsFactory.addBlank(paramComposite, 2);

		// modify by wuwei
		// this.applyRateModifierBut =
		// SwtWidgetsFactory.addButton(paramComposite,
		// this.registry.getString("apply"));
		//
		// this.applyRateModifierBut.setBounds(559, 1286, 66, 25);
		// this.applyRateModifierBut.setEnabled(false);
		// this.applyRateModifierBut.addSelectionListener(new SelectionAdapter()
		// {
		// public void widgetSelected(SelectionEvent paramSelectionEvent) {
		// int i = SchedTaskCostsDlgWizPage.this.billRateCombo
		// .getSelectionIndex();
		// if ((SchedTaskCostsDlgWizPage.this.billRateCombo.getItemCount() <= 1)
		// || (SchedTaskCostsDlgWizPage.billRatePreviousIdx == i))
		// return;
		// SchedTaskCostsDlgWizPage.this.resetFinalTotalEstimates(0, i);
		// SchedTaskCostsDlgWizPage.this.applyRateModifierBut
		// .setEnabled(false);
		// }
		// });

		SwtWidgetsFactory.addBlank(paramComposite, 3);
		SwtWidgetsFactory.addLabel(paramComposite,
				this.registry.getString("labelBreakdown"), "", 100, 131072,
				bool);

		// modify by wuwei
		// this.breakTable = SwtWidgetsFactory.addTable(
		// paramComposite,
		// 0,
		// 125,
		// new String[] { this.registry.getString("costTableCol1"),
		// this.registry.getString("breakTableCol2"),
		// this.registry.getString("breakTableCol3"),
		// this.registry.getString("costTableCol2"),
		// this.registry.getString("costTableCol3") }, new int[] {
		// 16384, 131072, 131072, 131072, 131072 },
		// this.control.breakData, 2, 5, false);

		this.breakTable = SwtWidgetsFactory.addTable(paramComposite, 0, 125,
				new String[] { this.registry.getString("costTableCol1"),
						this.registry.getString("breakTableCol2"),
						this.registry.getString("breakTableCol3") }, new int[] {
						16384, 131072, 131072 }, this.control.breakData, 2, 5,
				false);

		this.breakTable.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
				doHandleSelection();
			}

			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				doHandleSelection();
			}

			private void doHandleSelection() {
				boolean bool = false;
				int i = SchedTaskCostsDlgWizPage.this.breakTable
						.getSelectionIndex();
				if (i >= 0) {
					SchedTaskCostsDlgWizPage.this.breakModel = SchedTaskCostsDlgWizPage.this.control.breakModel;
					try {
						if (SchedTaskCostsDlgWizPage.this.arrayCostDetails[0].task
								.getChildrenCount() > 0)
							bool = true;
					} catch (TCException localTCException) {
						SchedTaskCostsDlgWizPage.logger.error(localTCException
								.getClass().getName(), localTCException);
					}
				}
				SchedTaskCostsDlgWizPage.this.drillDnBut.setEnabled(bool);
			}
		});
		this.rollUpBut = SwtWidgetsFactory.addButton(paramComposite,
				this.registry.getString("buttonRollUp"));
		// this.rollUpBut.setEnabled(checkParent());

		// modify by wuwei
		this.rollUpBut.setEnabled(false);
		this.rollUpBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				if ((!(SchedTaskCostsDlgWizPage.this.parentStack.isEmpty()))
						|| (SchedTaskCostsDlgWizPage.this.parentExist)) {

					// modify by wuwei
					// SchedTaskCostsDlgWizPage.this.switchToTaskRollUp();
					SchedTaskCostsDlgWizPage.this.rollUpBut
							.setEnabled(SchedTaskCostsDlgWizPage.this.parentExist);
				} else {
					SchedTaskCostsDlgWizPage.this.rollUpBut.setEnabled(false);
				}
			}
		});
		this.drillDnBut = SwtWidgetsFactory.addButton(paramComposite,
				this.registry.getString("buttonDrillDown"));
		this.drillDnBut.setEnabled(false);
		this.drillDnBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				int i = breakTable.getSelectionIndex();
				if (i < 0) {
					SchedTaskCostsDlgWizPage.this.drillDnBut.setEnabled(false);
					return;
				}
				i = SwtWidgetsFactory.getDataModelIndexOf(
						SchedTaskCostsDlgWizPage.this.breakTable, i);
				if (i < 0)
					return;
				SchedTaskCostsDlgWizPage.this.switchToTask(i);
			}
		});
		if (ScheduleDeferredContext.inDeferredSession())
			SwtWidgetsFactory.addBlank(paramComposite, 3);
		paramComposite.pack();
		setControl(paramComposite);
	}

	/**
	 * private boolean checkParent() { TCComponentScheduleTask
	 * localTCComponentScheduleTask = this.arrayCostDetails[0].task; Object
	 * localObject = null; try { TCComponent[] arrayOfTCComponent1 =
	 * this.arrayCostDetails[0].task.getRelatedComponents(); for (int i = 0; i <
	 * arrayOfTCComponent1.length; ++i) { TCProperty localTCProperty1 =
	 * arrayOfTCComponent1[i].getTCProperty("object_string"); TCProperty
	 * localTCProperty2 =
	 * localTCComponentScheduleTask.getTCProperty("fnd0ParentTask"); if
	 * ((!(localTCProperty1.toString().isEmpty())) &&
	 * (!(localTCProperty2.toString().isEmpty())) &&
	 * (localTCProperty1.toString()
	 * .equalsIgnoreCase(localTCProperty2.toString()))) {
	 * this.parentSummaryTaskExist =
	 * arrayOfTCComponent1[i].isValidPropertyName("fnd0SummaryTask"); if
	 * (this.parentSummaryTaskExist) { try { TCComponent[] arrayOfTCComponent2 =
	 * arrayOfTCComponent1[i].getRelatedComponents(); for (int j = 0; j <
	 * arrayOfTCComponent2.length; ++j) { TCProperty localTCProperty3 =
	 * arrayOfTCComponent2[j].getTCProperty("object_string"); TCProperty
	 * localTCProperty4 =
	 * localTCComponentScheduleTask.getTCProperty("fnd0ParentTask"); if
	 * ((localTCProperty3.toString().isEmpty()) ||
	 * (localTCProperty4.toString().isEmpty()) ||
	 * (!(localTCProperty3.toString().
	 * equalsIgnoreCase(localTCProperty4.toString())))) continue; localObject =
	 * arrayOfTCComponent2[j]; break label227: } } catch (TCException
	 * localTCException2) { logger.error(localTCException2.getClass().getName(),
	 * localTCException2); } label227:
	 * this.parentStack.push((TCComponentScheduleTask)localObject); } else {
	 * this.parentStack.push((TCComponentScheduleTask)arrayOfTCComponent1[i]); }
	 * this.parentExist = true; break label298: } this.parentExist = false; } }
	 * catch (TCException localTCException1) {
	 * logger.error(localTCException1.getClass().getName(), localTCException1);
	 * } if (!(this.parentStack.isEmpty())) { label298:
	 * this.rollUpBut.setEnabled(true); this.parentExist = true; } return
	 * this.parentExist; }
	 */

	/**
	 * public void switchToTaskRollUp() { retainCurrentSelections();
	 * this.breakModel = this.control.breakModel; try { if
	 * (!(this.parentStack.isEmpty())) { this.breakModel =
	 * this.control.breakModel; TCComponentScheduleTask[]
	 * arrayOfTCComponentScheduleTask = new TCComponentScheduleTask[1];
	 * TCComponentScheduleTask localTCComponentScheduleTask =
	 * (TCComponentScheduleTask)this.parentStack.pop(); Object localObject1 =
	 * null; TCComponent[] arrayOfTCComponent1 =
	 * localTCComponentScheduleTask.getRelatedComponents(); Object localObject4;
	 * for (int i = 0; i < arrayOfTCComponent1.length; ++i) { TCProperty
	 * localTCProperty1 = arrayOfTCComponent1[i].getTCProperty("object_string");
	 * TCProperty localObject2 =
	 * localTCComponentScheduleTask.getTCProperty("fnd0ParentTask"); if
	 * ((!(localTCProperty1.toString().isEmpty())) &&
	 * (!(((TCProperty)localObject2).toString().isEmpty())) &&
	 * (localTCProperty1.
	 * toString().equalsIgnoreCase(((TCProperty)localObject2).toString()))) {
	 * this.parentSummaryTaskExist =
	 * arrayOfTCComponent1[i].isValidPropertyName("fnd0SummaryTask"); if
	 * (this.parentSummaryTaskExist) { try { TCComponent[] arrayOfTCComponent2 =
	 * arrayOfTCComponent1[i].getRelatedComponents(); for (int k = 0; k <
	 * arrayOfTCComponent2.length; ++k) { localObject4 =
	 * arrayOfTCComponent2[k].getTCProperty("object_string"); TCProperty
	 * localTCProperty2 =
	 * localTCComponentScheduleTask.getTCProperty("fnd0ParentTask"); if
	 * ((((TCProperty)localObject4).toString().isEmpty()) ||
	 * (localTCProperty2.toString().isEmpty()) ||
	 * (!(((TCProperty)localObject4).toString
	 * ().equalsIgnoreCase(localTCProperty2.toString())))) continue;
	 * localObject1 = arrayOfTCComponent2[k]; break label265: } } catch
	 * (TCException localTCException2) {
	 * logger.error(localTCException2.getClass().getName(), localTCException2);
	 * } if (!(this.parentStack.contains(localObject1))) label265:
	 * this.parentStack.push((TCComponentScheduleTask)localObject1); } else if
	 * (!(this.parentStack.contains(arrayOfTCComponent1[i]))) {
	 * this.parentStack.push((TCComponentScheduleTask)arrayOfTCComponent1[i]); }
	 * this.parentExist = true; break; } this.parentExist = false; } if
	 * ((!(this.parentStack.isEmpty())) && (this.parentExist)) {
	 * this.rollUpBut.setEnabled(true); this.parentExist = true; }
	 * arrayOfTCComponentScheduleTask[0] = localTCComponentScheduleTask;
	 * CostsRunner localCostsRunner = new
	 * CostsRunner(arrayOfTCComponentScheduleTask); int j =
	 * localCostsRunner.execute(); Object localObject2 = null; if (j == 0) {
	 * Object localObject3 = localCostsRunner.getResults(); if (localObject3
	 * instanceof ScheduleManagement.CostDetailResponse) localObject2 =
	 * (ScheduleManagement.CostDetailResponse)localObject3;
	 * this.control.arrayCostDetails =
	 * ((ScheduleManagement.CostDetailResponse)localObject2).costDetails;
	 * this.control.oldArrayCostDetails =
	 * ((ScheduleManagement.CostDetailResponse)localObject2).costDetails;
	 * this.arrayCostDetails =
	 * ((ScheduleManagement.CostDetailResponse)localObject2).costDetails;
	 * ScheduleManagement.CostDetailContainer[] arrayOfCostDetailContainer =
	 * ((ScheduleManagement.CostDetailResponse)localObject2).costDetails;
	 * this.estCostLabel.setText(CurrencyFormatter.format(new
	 * BigDecimal(String.valueOf
	 * (arrayOfCostDetailContainer[0].totalEstimatedCost)).setScale(2, 4),
	 * this.currencySymbol));
	 * this.accCostLabel.setText(CurrencyFormatter.format(new
	 * BigDecimal(String.valueOf
	 * (arrayOfCostDetailContainer[0].totalAccruedCost)).setScale(2, 4),
	 * this.currencySymbol)); localObject4 =
	 * HoursFormatter.format(arrayOfCostDetailContainer
	 * [0].totalEstimatedMinutes);
	 * this.estHoursLabel.setText((String)localObject4); localObject4 =
	 * HoursFormatter.format(arrayOfCostDetailContainer[0].totalAccruedMinutes);
	 * this.accHoursLabel.setText((String)localObject4); int l =
	 * arrayOfCostDetailContainer[0].fixedCostData.length; String[][]
	 * arrayOfString = new String[l][]; int i1 = 0; String str4; for (int i2 =
	 * 0; i2 < arrayOfCostDetailContainer[0].fixedCostData.length; ++i2) {
	 * String str1 = arrayOfCostDetailContainer[0].fixedCostData[i2].name;
	 * Double localObject6 = Double.valueOf(0.0D); Double localObject7 =
	 * Double.valueOf(0.0D); if
	 * (arrayOfCostDetailContainer[0].fixedCostData[i2].
	 * estimate.startsWith("$")) localObject6 =
	 * Double.valueOf(arrayOfCostDetailContainer
	 * [0].fixedCostData[i2].estimate.substring(1)); else localObject6 =
	 * Double.valueOf(arrayOfCostDetailContainer[0].fixedCostData[i2].estimate);
	 * if (arrayOfCostDetailContainer[0].fixedCostData[i2].useActual) { if
	 * (arrayOfCostDetailContainer[0].fixedCostData[i2].actual.startsWith("$"))
	 * localObject7 =
	 * Double.valueOf(arrayOfCostDetailContainer[0].fixedCostData[
	 * i2].actual.substring(1)); else localObject7 =
	 * Double.valueOf(arrayOfCostDetailContainer[0].fixedCostData[i2].actual); }
	 * else if (arrayOfCostDetailContainer[0].fixedCostData[i2].accrualType ==
	 * 0) { String str2 =
	 * TaskHelper.getFnd0State(this.arrayCostDetails[0].task); if
	 * (str2.equalsIgnoreCase("in_progress")) localObject7 = localObject6; else
	 * if (str2.equalsIgnoreCase("complete")) localObject7 = localObject6; else
	 * localObject7 = new Double(0.0D); } else if
	 * (arrayOfCostDetailContainer[0].fixedCostData[i2].accrualType == 1) {
	 * double d =
	 * TaskHelper.getPercentageComplete(this.arrayCostDetails[0].task);
	 * localObject7 = Double.valueOf(((Double)localObject6).doubleValue() * d /
	 * 100.0D); } else { str3 =
	 * TaskHelper.getFnd0State(this.arrayCostDetails[0].task); if
	 * (str3.equalsIgnoreCase("complete")) localObject7 = localObject6; else
	 * localObject7 = new Double(0.0D); } str3 =
	 * CurrencyFormatter.format(Double.
	 * valueOf(((Double)localObject6).doubleValue()),
	 * arrayOfCostDetailContainer[0].fixedCostData[i2].currency); str4 =
	 * CurrencyFormatter
	 * .format(Double.valueOf(((Double)localObject7).doubleValue()),
	 * arrayOfCostDetailContainer[0].fixedCostData[i2].currency);
	 * arrayOfString;[i1] = { str1, str3, str4,
	 * arrayOfCostDetailContainer[0].fixedCostData[i2].fixedCost.getUid() };
	 * ++i1; } this.fixCostTable.clearAll(); this.fixCostTable.removeAll();
	 * SwtWidgetsFactory.addTableContents(this.fixCostTable, arrayOfString;);
	 * this.fixCostTable.redraw(); String str5; Object localObject8; if
	 * (this.arrayCostDetails[0].numchildren > 0) { String localObject5 = "";
	 * this.control.breakData = new
	 * String[this.arrayCostDetails[0].numchildren][]; this.control.breakTree =
	 * new TreeNode(null); this.breakModel.clear(); for (int i3 = 0; i3 <
	 * this.control.breakData.length; ++i3) { if
	 * (this.arrayCostDetails[0].taskCostDetails.length > 0) {
	 * TCComponentScheduleTask localObject6 =
	 * this.arrayCostDetails[0].taskCostDetails[i3].task; localObject5 =
	 * ((TCComponent)localObject6).toString(); } String localObject6 = ""; if
	 * ((localObject5 != null) && (((String)localObject5).length() > 0))
	 * localObject6 = localObject5; localObject7 =
	 * HoursFormatter.format(this.arrayCostDetails
	 * [0].taskCostDetails[i3].totalEstimatedMinutes); str3 =
	 * HoursFormatter.format
	 * (this.arrayCostDetails[0].taskCostDetails[i3].totalAccruedMinutes); str4
	 * = CurrencyFormatter.format(new
	 * BigDecimal(String.valueOf(this.arrayCostDetails
	 * [0].taskCostDetails[i3].totalEstimatedCost)).setScale(2, 4),
	 * this.currencySymbol); str5 = CurrencyFormatter.format(new
	 * BigDecimal(String
	 * .valueOf(this.arrayCostDetails[0].taskCostDetails[i3].totalAccruedCost
	 * )).setScale(2, 4), this.currencySymbol); this.control.breakData[i3] = {
	 * localObject6, localObject7, str3, str4, str5 }; localObject8 = new
	 * TreeNode(this.control.breakData[i3]);
	 * ((TreeNode)localObject8).setParent(this.control.breakTree);
	 * this.breakModel.add(localObject8); } this.breakTable.clearAll();
	 * this.breakTable.removeAll();
	 * SwtWidgetsFactory.addTableContents(this.breakTable,
	 * this.control.breakData); } else { this.breakModel.clear(); } Object
	 * localObject5 = Integer.valueOf(0); Integer localInteger =
	 * Integer.valueOf(0); Object localObject6 = Integer.valueOf(0); Object
	 * localObject7 = Integer.valueOf(0); String str3 = "None"; if
	 * (this.arrayCostDetails != null) { str4 =
	 * this.arrayCostDetails[0].task.toString(); if (this.control.wizard !=
	 * null) { str5 = this.registry.getString("schedTaskWizPg.TITLE"); str5 =
	 * MessageFormat.format(str5, new Object[] { str4 });
	 * this.control.wizard.setWindowTitle(str5); } str5 = "a"; str5 =
	 * this.arrayCostDetails[0].billCode; if ((str5 != null) && (str5.length() >
	 * 0)) localObject5 = Integer.valueOf(Math.max(0,
	 * StringHolder.findInternal(this.control.lovBillCodeList, str5)));
	 * localObject8 = this.arrayCostDetails[0].billSubCode; if ((localObject8 !=
	 * null) && (((String)localObject8).length() > 0)) localInteger =
	 * Integer.valueOf(Math.max(0,
	 * StringHolder.findInternal(this.control.lovBillSubCodeList
	 * [((Integer)localObject5).intValue()], (String)localObject8))); String
	 * str6 = this.arrayCostDetails[0].billingType; if ((str6 != null) &&
	 * (str6.length() > 0)) localObject6 = Integer.valueOf(Math.max(0,
	 * StringHolder.findInternal(this.control.billTypeList, str6))); TCComponent
	 * localTCComponent =
	 * localTCComponentScheduleTask.getReferenceProperty("billrate_tag"); if
	 * (localTCComponent != null) str3 =
	 * localTCComponent.getStringProperty("object_name"); else str3 = "None"; if
	 * ((str3 != null) && (str3.length() > 0)) for (int i4 = 0; i4 <
	 * this.control.billRateList.length; ++i4) { if
	 * (!(str3.equalsIgnoreCase(this.control.billRateList[i4]))) continue;
	 * localObject7 = Integer.valueOf(i4); break; } }
	 * this.billCodeCombo.select(((Integer)localObject5).intValue());
	 * this.billTypeCombo.select(((Integer)localObject6).intValue());
	 * setBillCodeComboSelection(((Integer)localObject5).intValue(),
	 * ((Integer)localInteger).intValue());
	 * this.billSubCodeCombo.select(((Integer)localInteger).intValue());
	 * this.billRateCombo.select(((Integer)localObject7).intValue());
	 * billRatePreviousIdx = ((Integer)localObject7).intValue(); if
	 * ((TaskHelper.isSummaryTask(this.arrayCostDetails[0].task)) ||
	 * (TaskHelper.isScheduleSummaryTask(this.arrayCostDetails[0].task)) ||
	 * (TaskHelper.isPhaseTask(this.arrayCostDetails[0].task)))
	 * this.billRateCombo.setEnabled(false); else
	 * this.billRateCombo.setEnabled(true); }
	 * this.rollUpBut.setEnabled(!(this.parentSummaryTaskExist)); return; }
	 * this.parentExist = false; } catch (TCException localTCException1) {
	 * logger.error(localTCException1.getClass().getName(), localTCException1);
	 * } }
	 **/

	public void switchToTask(int i) {
		retainCurrentSelections();
		breakModel = control.breakModel;
		try {
			if (arrayCostDetails[0].task.getChildrenCount() > 0) {
				rollUpBut.setEnabled(true);
				breakModel = control.breakModel;
				TCComponentScheduleTask atccomponentscheduletask[] = new TCComponentScheduleTask[1];
				TCComponentScheduleTask tccomponentscheduletask = arrayCostDetails[0].taskCostDetails[i].task;
				parentStack.push(arrayCostDetails[0].task);
				atccomponentscheduletask[0] = tccomponentscheduletask;
				CostsRunner costsrunner = new CostsRunner(
						atccomponentscheduletask);
				int j = costsrunner.execute();
				com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse costdetailresponse = null;
				if (j == 0) {
					Object obj = costsrunner.getResults();
					if (obj instanceof com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse)
						costdetailresponse = (com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse) obj;
					control.arrayCostDetails = costdetailresponse.costDetails;
					control.oldArrayCostDetails = costdetailresponse.costDetails;
					arrayCostDetails = costdetailresponse.costDetails;
					com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailContainer acostdetailcontainer[] = costdetailresponse.costDetails;
					estCostLabel
							.setText(CurrencyFormatter.format(
									(new BigDecimal(
											String.valueOf(acostdetailcontainer[0].totalEstimatedCost)))
											.setScale(2, 4), currencySymbol));
					// accCostLabel
					// .setText(CurrencyFormatter.format(
					// (new BigDecimal(
					// String.valueOf(acostdetailcontainer[0].totalAccruedCost)))
					// .setScale(2, 4), currencySymbol));
					String s = HoursFormatter
							.format(acostdetailcontainer[0].totalEstimatedMinutes);
					estHoursLabel.setText(s);
					s = HoursFormatter
							.format(acostdetailcontainer[0].totalAccruedMinutes);
					accHoursLabel.setText(s);
					int k = acostdetailcontainer[0].fixedCostData.length;
					String as[][] = new String[k][];
					int l = 0;
					for (int i1 = 0; i1 < acostdetailcontainer[0].fixedCostData.length; i1++) {
						String s2 = acostdetailcontainer[0].fixedCostData[i1].name;
						Double double1 = Double.valueOf(0.0D);
						Double double2 = Double.valueOf(0.0D);
						if (acostdetailcontainer[0].fixedCostData[i1].estimate
								.startsWith("$"))
							double1 = Double
									.valueOf(acostdetailcontainer[0].fixedCostData[i1].estimate
											.substring(1));
						else
							double1 = Double
									.valueOf(acostdetailcontainer[0].fixedCostData[i1].estimate);
						if (acostdetailcontainer[0].fixedCostData[i1].useActual) {
							if (acostdetailcontainer[0].fixedCostData[i1].actual
									.startsWith("$"))
								double2 = Double
										.valueOf(acostdetailcontainer[0].fixedCostData[i1].actual
												.substring(1));
							else
								double2 = Double
										.valueOf(acostdetailcontainer[0].fixedCostData[i1].actual);
						} else {
							try {
								if (acostdetailcontainer[0].fixedCostData[i1].accrualType == 0) {
									String s7 = TaskHelper
											.getFnd0State(arrayCostDetails[0].task);
									if (s7.equalsIgnoreCase("in_progress"))
										double2 = double1;
									else if (s7.equalsIgnoreCase("complete"))
										double2 = double1;
									else
										double2 = new Double(0.0D);
								} else if (acostdetailcontainer[0].fixedCostData[i1].accrualType == 1) {
									double d = TaskHelper
											.getPercentageComplete(arrayCostDetails[0].task);
									double2 = Double.valueOf((double1
											.doubleValue() * d) / 100D);
								} else {
									String s8 = TaskHelper
											.getFnd0State(arrayCostDetails[0].task);
									if (s8.equalsIgnoreCase("complete"))
										double2 = double1;
									else
										double2 = new Double(0.0D);
								}
							} catch (TCException tcexception1) {
								logger.error(tcexception1.getClass().getName(),
										tcexception1);
							}
						}
						String s9 = CurrencyFormatter
								.format(Double.valueOf(double1.doubleValue()),
										acostdetailcontainer[0].fixedCostData[i1].currency);
						String s13 = CurrencyFormatter
								.format(Double.valueOf(double2.doubleValue()),
										acostdetailcontainer[0].fixedCostData[i1].currency);
						
						//new by wuwei
						s13=acostdetailcontainer[0].fixedCostData[i1].billingType;
						System.out.println("lala  s2:"+s2+" s9:"+s9+" s13:"+s13);//control.billTypeList[control.billTypeDBIdx].getInternal()
						
						as[l] = (new String[] {
								s2,
								s9,
								s13,
								acostdetailcontainer[0].fixedCostData[i1].fixedCost
										.getUid() });
						l++;
					}

					fixCostTable.clearAll();
					fixCostTable.removeAll();
					SwtWidgetsFactory.addTableContents(fixCostTable, as);
					fixCostTable.redraw();
					if (arrayCostDetails != null
							&& arrayCostDetails[0].numchildren > 0) {
						String s1 = "";
						control.breakData = new String[arrayCostDetails[0].numchildren][];
						control.breakTree = new TreeNode(null);
						breakModel.clear();
						for (int j1 = 0; j1 < control.breakData.length; j1++) {
							if (arrayCostDetails[0].taskCostDetails.length > 0) {
								TCComponentScheduleTask tccomponentscheduletask2 = arrayCostDetails[0].taskCostDetails[j1].task;
								s1 = tccomponentscheduletask2.toString();
							}
							String s4 = "";
							if (s1 != null && s1.length() > 0)
								s4 = s1;
							String s5 = HoursFormatter
									.format(arrayCostDetails[0].taskCostDetails[j1].totalEstimatedMinutes);
							String s10 = HoursFormatter
									.format(arrayCostDetails[0].taskCostDetails[j1].totalAccruedMinutes);
							String s14 = CurrencyFormatter
									.format(Double
											.valueOf(arrayCostDetails[0].taskCostDetails[j1].totalEstimatedCost),
											currencySymbol);
							String s17 = CurrencyFormatter
									.format(Double
											.valueOf(arrayCostDetails[0].taskCostDetails[j1].totalAccruedCost),
											currencySymbol);
							control.breakData[j1] = (new String[] { s4, s5,
									s10, s14, s17 });
							TreeNode treenode1 = new TreeNode(
									control.breakData[j1]);
							treenode1.setParent(control.breakTree);
							breakModel.add(treenode1);
						}

						breakTable.clearAll();
						breakTable.removeAll();
						SwtWidgetsFactory.addTableContents(breakTable,
								control.breakData);
						drillDnBut.setEnabled(false);
					} else {
						breakModel.clear();
						TCComponentScheduleTask tccomponentscheduletask1 = arrayCostDetails[0].task;
						ResourceCostUtil.getEstimatedHoursAndCostsPerResource(
								tccomponentscheduletask1,
								resourceTimeCostHolder);
						if (TaskHelper
								.getApprovedWork(tccomponentscheduletask1) == 0) {
							ResourceCostUtil.allocateAllWorkPerResource(
									tccomponentscheduletask1,
									resourceTimeCostHolder);
						} else {
							ResourceCostUtil.processTimesheetsPerResource(
									tccomponentscheduletask1,
									resourceTimeCostHolder);
							if (TaskHelper
									.getApprovedWork(tccomponentscheduletask1) < TaskHelper
									.getWorkComplete(tccomponentscheduletask1))
								ResourceCostUtil
										.allocateNonTimesheetWorkPerResource(
												tccomponentscheduletask1,
												resourceTimeCostHolder);
						}
						control.breakData = resourceTimeCostHolder == null
								|| resourceTimeCostHolder.size() <= 0 ? new String[1][]
								: new String[resourceTimeCostHolder.size()][];
						control.breakTree = new TreeNode(null);
						String s3 = "";
						int k1 = 0;
						if (resourceTimeCostHolder != null
								&& resourceTimeCostHolder.size() > 0) {
							for (Iterator iterator = resourceTimeCostHolder
									.values().iterator(); iterator.hasNext();) {
								ResourceTimeCostHolder resourcetimecostholder = (ResourceTimeCostHolder) iterator
										.next();
								if (resourcetimecostholder.getResUid() != null) {
									s3 = tccomponentscheduletask1
											.getSession()
											.stringToComponent(
													resourcetimecostholder
															.getResUid())
											.toString();
									control.breakData[k1] = (new String[] {
											s3 == null || s3.length() <= 0 ? ""
													: s3,
											HoursFormatter
													.format(resourcetimecostholder
															.getEstimatedMinutes()),
											HoursFormatter
													.format(resourcetimecostholder
															.getTotalAccruedMinutes()),
											CurrencyFormatter
													.format(Double
															.valueOf(resourcetimecostholder
																	.getEstimatedCost()
																	.doubleValue()),
															currencySymbol),
											CurrencyFormatter
													.format(Double
															.valueOf(resourcetimecostholder
																	.getTotalAccruedCost()
																	.doubleValue()),
															currencySymbol) });
									TreeNode treenode = new TreeNode(
											control.breakData[k1]);
									treenode.setParent(control.breakTree);
									breakModel.add(treenode);
									k1++;
								}
							}

						} else {
							String s6 = "";
							if (s3 != null && s3.length() > 0)
								s6 = s3;
							String s11 = HoursFormatter.format(TaskHelper
									.getWorkEstimate(tccomponentscheduletask1));
							String s15 = HoursFormatter.format(TaskHelper
									.getWorkComplete(tccomponentscheduletask1));
							double d1 = arrayCostDetails[0].totalEstimatedCost;
							BigDecimal bigdecimal = BigDecimal.valueOf(d1);
							double d2 = arrayCostDetails[0].totalAccruedCost;
							BigDecimal bigdecimal1 = BigDecimal.valueOf(d2);
							if (arrayCostDetails[0].fixedCostData.length > 0) {
								for (int i2 = 0; i2 < arrayCostDetails[0].fixedCostData.length; i2++)
									if (arrayCostDetails[0].fixedCostData[i2].estimate != null) {
										String s23 = arrayCostDetails[0].fixedCostData[i2].estimate;
										BigDecimal bigdecimal2 = new BigDecimal(
												s23);
										bigdecimal = bigdecimal
												.subtract(bigdecimal2);
										if (d2 != 0.0D)
											if (arrayCostDetails[0].fixedCostData[i2].useActual) {
												String s25 = arrayCostDetails[0].fixedCostData[i2].actual;
												BigDecimal bigdecimal3 = new BigDecimal(
														s25);
												bigdecimal1 = bigdecimal1
														.subtract(bigdecimal3);
											} else if (arrayCostDetails[0].fixedCostData[i2].accrualType == 0) {
												String s26 = TaskHelper
														.getFnd0State(arrayCostDetails[0].task);
												if (s26.equalsIgnoreCase("in_progress"))
													bigdecimal1 = bigdecimal1
															.subtract(bigdecimal2);
												else if (s26
														.equalsIgnoreCase("complete"))
													bigdecimal1 = bigdecimal1
															.subtract(bigdecimal2);
												else
													bigdecimal1 = bigdecimal1
															.subtract(BigDecimal
																	.valueOf(0L));
											} else if (arrayCostDetails[0].fixedCostData[i2].accrualType == 1) {
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
												String s27 = TaskHelper
														.getFnd0State(arrayCostDetails[0].task);
												if (s27.equalsIgnoreCase("complete"))
													bigdecimal1 = bigdecimal1
															.subtract(bigdecimal2);
												else
													bigdecimal1 = bigdecimal1
															.subtract(BigDecimal
																	.valueOf(0L));
											}
									}

							}
							String s22 = CurrencyFormatter.format(bigdecimal,
									currencySymbol);
							String s24 = CurrencyFormatter.format(bigdecimal1,
									currencySymbol);
							control.breakData[0] = (new String[] { s6, s11,
									s15, s22, s24 });
							TreeNode treenode2 = new TreeNode(
									control.breakData[0]);
							treenode2.setParent(control.breakTree);
							breakModel.add(treenode2);
						}
						resourceTimeCostHolder.clear();
						breakTable.clearAll();
						breakTable.removeAll();
						SwtWidgetsFactory.addTableContents(breakTable,
								control.breakData);
						drillDnBut.setEnabled(false);
					}
					Integer integer = Integer.valueOf(0);
					Integer integer1 = Integer.valueOf(0);
					Integer integer2 = Integer.valueOf(0);
					Integer integer3 = Integer.valueOf(0);
					String s12 = "None";
					String s16 = arrayCostDetails[0].task.toString();
					if (control.wizard != null) {
						String s18 = registry.getString("schedTaskWizPg.TITLE");
						s18 = MessageFormat.format(s18, new Object[] { s16 });
						control.wizard.setWindowTitle(s18);
					}
					String s19 = "a";
					s19 = arrayCostDetails[0].billCode;
					if (s19 != null && s19.length() > 0)
						integer = Integer.valueOf(Math.max(0, StringHolder
								.findInternal(control.lovBillCodeList, s19)));
					String s20 = arrayCostDetails[0].billSubCode;
					if (s20 != null && s20.length() > 0)
						integer1 = Integer
								.valueOf(Math.max(
										0,
										StringHolder
												.findInternal(
														control.lovBillSubCodeList[((Integer) integer)
																.intValue()],
														s20)));
					String s21 = arrayCostDetails[0].billingType;
					if (s21 != null && s21.length() > 0)
						integer2 = Integer.valueOf(Math.max(0, StringHolder
								.findInternal(control.billTypeList, s21)));
					TCComponent tccomponent = tccomponentscheduletask
							.getReferenceProperty("billrate_tag");
					if (tccomponent != null)
						s12 = tccomponent.getStringProperty("object_name");
					else
						s12 = "None";
					if (s12 != null && s12.length() > 0) {
						for (int l1 = 0; l1 < control.billRateList.length; l1++) {
							if (!s12.equalsIgnoreCase(control.billRateList[l1]))
								continue;
							integer3 = Integer.valueOf(l1);
							break;
						}

					}

					// billCodeCombo.select(((Integer) integer).intValue());

					// billTypeCombo.select(((Integer) integer2).intValue());
					
					 // setBillCodeComboSelection(((Integer) integer).intValue(),
					// ((Integer) integer1).intValue());
					// billSubCodeCombo.select(((Integer) integer1).intValue());
					// billRateCombo.select(((Integer) integer3).intValue());
					billRatePreviousIdx = ((Integer) integer3).intValue();
					// if (TaskHelper.isSummaryTask(arrayCostDetails[0].task)
					// || TaskHelper
					// .isScheduleSummaryTask(arrayCostDetails[0].task)
					// || TaskHelper.isPhaseTask(arrayCostDetails[0].task))
					// billRateCombo.setEnabled(false);
					// else
					// billRateCombo.setEnabled(true);
				}
			} else {
				breakTable.clearAll();
				breakTable.removeAll();
			}
		} catch (TCException tcexception) {
			logger.error(tcexception.getClass().getName(), tcexception);
		}
	}

	protected boolean deleteRowsAndUpdate(
			TCComponentSchedulingFixedCost[] paramArrayOfTCComponentSchedulingFixedCost) {
		com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate localTaskCostUpdate = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate();
		try {
			localTaskCostUpdate.billCode = this.arrayCostDetails[0].task
					.getStringProperty("bill_code");
			localTaskCostUpdate.subCode = this.arrayCostDetails[0].task
					.getStringProperty("bill_sub_code");
			localTaskCostUpdate.billingType = this.arrayCostDetails[0].task
					.getStringProperty("bill_type");
			TCComponent localTCComponent = this.arrayCostDetails[0].task
					.getReferenceProperty("billrate_tag");
			if (localTCComponent != null)
				localTaskCostUpdate.billRate = localTCComponent
						.getObjectString();
			else
				localTaskCostUpdate.billRate = "None";
			localTaskCostUpdate.deletedCosts = paramArrayOfTCComponentSchedulingFixedCost;
			localTaskCostUpdate.task = this.arrayCostDetails[0].task;
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse localUpdateTaskCostDataResponse = null;
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate[] arrayOfTaskCostUpdate = { localTaskCostUpdate };
			UpdateTaskCostDataRunner localUpdateTaskCostDataRunner = new UpdateTaskCostDataRunner(
					arrayOfTaskCostUpdate);
			int i = localUpdateTaskCostDataRunner.execute();
			if (i != 0) {
				// break label591;
				return false;
			}

			Object localObject = localUpdateTaskCostDataRunner.getResults();
			if (!(localObject instanceof com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse)) {
				// break label591;
				return false;
			}

			localUpdateTaskCostDataResponse = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse) localObject;
			TCComponentScheduleTask localTCComponentScheduleTask = localUpdateTaskCostDataResponse.responses[0].updatedTask;
			TCComponentScheduleTask[] arrayOfTCComponentScheduleTask = new TCComponentScheduleTask[1];
			arrayOfTCComponentScheduleTask[0] = localTCComponentScheduleTask;
			CostsRunner localCostsRunner = new CostsRunner(
					arrayOfTCComponentScheduleTask);
			int j = localCostsRunner.execute();
			ScheduleManagement.CostDetailResponse localCostDetailResponse = null;
			if (j != 0) {
				// break label591;
				return false;
			}
			localObject = localCostsRunner.getResults();
			if (localObject instanceof ScheduleManagement.CostDetailResponse)
				localCostDetailResponse = (ScheduleManagement.CostDetailResponse) localObject;
			if (localCostDetailResponse == null) {
				// break label591;
				return false;
			}
			ScheduleManagement.CostDetailContainer[] arrayOfCostDetailContainer = localCostDetailResponse.costDetails;
			this.estCostLabel.setText(CurrencyFormatter.format(Double
					.valueOf(arrayOfCostDetailContainer[0].totalEstimatedCost),
					this.currencySymbol));
			// this.accCostLabel.setText(CurrencyFormatter.format(Double
			// .valueOf(arrayOfCostDetailContainer[0].totalAccruedCost),
			// this.currencySymbol));
			String str1 = HoursFormatter
					.format(arrayOfCostDetailContainer[0].totalEstimatedMinutes);
			this.estHoursLabel.setText(str1);
			str1 = HoursFormatter
					.format(arrayOfCostDetailContainer[0].totalAccruedMinutes);
			this.accHoursLabel.setText(str1);
			int k = arrayOfCostDetailContainer[0].fixedCostData.length;
			String[][] arrayOfString = new String[k][];
			int l = 0;
			for (int i1 = 0; i1 < arrayOfCostDetailContainer[0].fixedCostData.length; ++i1) {
				String str2 = arrayOfCostDetailContainer[0].fixedCostData[i1].name;
				Double localDouble1 = Double
						.valueOf(arrayOfCostDetailContainer[0].fixedCostData[i1].estimate);
				Double localDouble2 = Double
						.valueOf(arrayOfCostDetailContainer[0].fixedCostData[i1].actual);
				String str3 = CurrencyFormatter
						.format(Double.valueOf(localDouble1.doubleValue()),
								arrayOfCostDetailContainer[0].fixedCostData[i1].currency);
				String str4 = CurrencyFormatter
						.format(Double.valueOf(localDouble2.doubleValue()),
								arrayOfCostDetailContainer[0].fixedCostData[i1].currency);
				
				//new  by wuwei
				str4=arrayOfCostDetailContainer[0].fixedCostData[i1].billingType;
				
				arrayOfString[l] = new String[] {
						str2,
						str3,
						str4,
						arrayOfCostDetailContainer[0].fixedCostData[i1].fixedCost
								.getUid() };
				++l;
			}
			this.fixCostTable.clearAll();
			this.fixCostTable.removeAll();
			SwtWidgetsFactory
					.addTableContents(this.fixCostTable, arrayOfString);
			this.fixCostTable.redraw();
			return true;
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
		}
		label591: return false;
	}

	// modify by wuwei
	private void retainCurrentSelections() {
		int i = 0;
		int j = 0;
		// int i = this.billCodeCombo.getSelectionIndex();
		i = (i < 0) ? 0 : i;
		// int j = (this.billSubCodeCombo.getSelectionIndex() < 0) ? 0
		// : this.billSubCodeCombo.getSelectionIndex();
		if (this.control.lovBillSubCodeList[i] != null)
			this.control.lovBillSubCodeList[i][j].getInternal();
		// j = (this.billTypeCombo.getSelectionIndex() < 0) ? 0
		// : this.billTypeCombo.getSelectionIndex();
	}

	private void setFinalTotalEstimates() {
		this.control.recalculateCosts(this);
		updateDisplayedValues(true);
	}

	private void resetFinalTotalEstimates(int paramInt1, int paramInt2) {
		if (paramInt2 < 0)
			paramInt1 = -1;
		switch (paramInt1) {
		case 0:
			TCComponentBillRate localTCComponentBillRate = (TCComponentBillRate) this.control.billRateCom[paramInt2];
			RACCostAbstract.tcCompToBillRateDetail(localTCComponentBillRate);
			this.control.billRateDBIdx = paramInt2;
			billRatePreviousIdx = paramInt2;
			this.control.setVariableCostLineItems(true);
			this.breakTable.clearAll();
			this.breakTable.removeAll();
			SwtWidgetsFactory.addTableContents(this.breakTable,
					this.control.breakData);
		case 1:
		}
		setFinalTotalEstimates();
	}

	private void reselectBillRate() {
		BillRateDetail billratedetail = null;
		if (billratedetail != null) {
			String s = billratedetail.getName();
			if (s != null && s.length() > 0) {
				billRatePreviousIdx = Arrays.asList(control.billRateList)
						.indexOf(s);
				if (billRatePreviousIdx < 0)
					billRatePreviousIdx = 0;
			} else {
				billRatePreviousIdx = 0;
			}
		} else {
			billRatePreviousIdx = 0;
		}
		// billRateCombo.select(billRatePreviousIdx);
	}

	private void fireFixedCostDetails(Shell shell) {
		int i = fixCostTable.getSelectionIndex();
		if (i < 0)
			return;
		i = SwtWidgetsFactory.getDataModelIndexOf(this.fixCostTable, i);
	
		/*if (i >= 0) {
		      FixedCostDetail fixedcostdetail = (FixedCostDetail)this.fixCostModel.displayEntry.get(i);

		      SchedTaskCostsDlgWizard schedtaskcostsdlgwizard = new SchedTaskCostsDlgWizard();
		      schedtaskcostsdlgwizard.setWindowTitle(this.registry.getString("fixedCost.TITLE"));

		      schedtaskcostsdlgwizard.setHelpAvailable(false);
		      int j = Math.max(0, StringHolder.findInternal(this.control.lovBillCodeList, fixedcostdetail.getBillingCode()));

		      StringHolder[] astringholder = (j <= -1) || (this.control.lovBillSubCodeList.length <= j) || (this.control.lovBillSubCodeList[j] == null) ? new StringHolder[] { StringHolder.EMPTY } : this.control.lovBillSubCodeList[j];

		      int k = Math.max(0, StringHolder.findInternal(astringholder, fixedcostdetail.getBillingSubCode()));

		      int l = Math.max(0, StringHolder.findInternal(this.control.billTypeList, fixedcostdetail.getBillingType()));

		      schedtaskcostsdlgwizard.addPage(new FixedCostDlgWizPage(this.control, fixedcostdetail, false, this.registry.getString("fixedCost.TITLE"), this.registry.getString("fixedCost.DESC"), 2, j, k, l, this.control.currencyList[0]));

		      FixedCostDlg fixedcostdlg = new FixedCostDlg(shell, schedtaskcostsdlgwizard);

		      fixedcostdlg.create();
		      fixedcostdlg.open();
		    }*/
		
		if (i >= 0) {
			int j = -1;
			TableItem tableitem = fixCostTable.getItem(i);
			String s = tableitem.getText(3);
			Object obj = null;
			for (int k = 0; k < arrayCostDetails[0].fixedCostData.length; k++) {
				String s1 = arrayCostDetails[0].fixedCostData[k].fixedCost
						.getUid();
				if (s1.equalsIgnoreCase(s)) {
					j = k;
					boolean flag = false;
					for (Iterator iterator = SchedTaskCostsDialogControl.existingTempDirtyFixedcost
							.iterator(); iterator.hasNext();) {
						com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer1 = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer) iterator
								.next();
						if (fixedcostcontainer1.name
								.compareTo(arrayCostDetails[0].fixedCostData[k].name) == 0) {
							flag = true;
							break;
						}
					}

					if (!flag) {
						com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer();
						fixedcostcontainer.accrualType = arrayCostDetails[0].fixedCostData[k].accrualType;
						fixedcostcontainer.actual = arrayCostDetails[0].fixedCostData[k].actual;
						fixedcostcontainer.billCode = arrayCostDetails[0].fixedCostData[k].billCode;
						fixedcostcontainer.billingType = arrayCostDetails[0].fixedCostData[k].billingType;
						
						System.out.println("billingType-->"+fixedcostcontainer.billingType);
						
						fixedcostcontainer.currency = arrayCostDetails[0].fixedCostData[k].currency;
						fixedcostcontainer.estimate = arrayCostDetails[0].fixedCostData[k].estimate;
						fixedcostcontainer.fixedCost = arrayCostDetails[0].fixedCostData[k].fixedCost;
						fixedcostcontainer.name = arrayCostDetails[0].fixedCostData[k].name;
						fixedcostcontainer.subCode = arrayCostDetails[0].fixedCostData[k].subCode;
						fixedcostcontainer.useActual = arrayCostDetails[0].fixedCostData[k].useActual;
						fixedcostcontainer.fixedCost = arrayCostDetails[0].fixedCostData[k].fixedCost;
						SchedTaskCostsDialogControl.existingTempDirtyFixedcost
								.add(fixedcostcontainer);
					}
				}
			}

			SchedTaskCostsDlgWizard schedtaskcostsdlgwizard = new SchedTaskCostsDlgWizard();
			schedtaskcostsdlgwizard.setWindowTitle(registry
					.getString("fixedCost.TITLE"));
			schedtaskcostsdlgwizard.setHelpAvailable(false);
			schedtaskcostsdlgwizard.addPage(new FixedCostDlgWizPage(control,
					null, false, registry.getString("fixedCost.TITLE"),
					registry.getString("fixedCost.DESC"), 2, j, 0, 0,
					control.currencyList[0]));
			
			FixedCostDlg fixedcostdlg = new FixedCostDlg(shell,
					schedtaskcostsdlgwizard);
			fixedcostdlg.create();
			fixedcostdlg.open();
		}
	}

	private void fireFixedCostNew(Shell paramShell) {
//		SchedTaskCostsDlgWizard localSchedTaskCostsDlgWizard = new SchedTaskCostsDlgWizard();
//		localSchedTaskCostsDlgWizard.setWindowTitle(this.registry
//				.getString("fixedCost.TITLE"));
//		localSchedTaskCostsDlgWizard.setHelpAvailable(false);
//		localSchedTaskCostsDlgWizard.addPage(new FixedCostDlgWizPage(
//				this.control, null, true, this.registry
//						.getString("fixedCost.TITLE"), this.registry
//						.getString("fixedCost.DESC"), 2, 0, 0,
//				// this.billCodeCombo.getSelectionIndex(),
//				// this.billSubCodeCombo.getSelectionIndex(),
//				// this.billTypeCombo.getSelectionIndex()
//				0, this.control.currencyList[0]));
//		FixedCostDlg localFixedCostDlg = new FixedCostDlg(paramShell,
//				localSchedTaskCostsDlgWizard);
//		localFixedCostDlg.create();
//		localFixedCostDlg.open();
		
		SchedTaskCostsDlgWizard schedtaskcostsdlgwizard = new SchedTaskCostsDlgWizard();
	    schedtaskcostsdlgwizard.setWindowTitle(this.registry.getString("fixedCost.TITLE"));
	    schedtaskcostsdlgwizard.setHelpAvailable(false);
	    
	    //new by wuwei --- this.control.billTypeList
	    schedtaskcostsdlgwizard.addPage(new FixedCostDlgWizPage(this.control, null, true, this.registry.getString("fixedCost.TITLE"), this.registry.getString("fixedCost.DESC"), 2, 0, 0, 0, this.control.currencyList[0]));//this.control.currencyList[0]

	    FixedCostDlg fixedcostdlg = new FixedCostDlg(paramShell, schedtaskcostsdlgwizard);

	    fixedcostdlg.create();
	    fixedcostdlg.open();
	}

	private void updateDisplayedValues(boolean flag) {
		int i = 0;
		int j = 0;
		Double double1 = Double.valueOf(0.0D);
		Double double2 = Double.valueOf(0.0D);
		Double double3 = Double.valueOf(0.0D);
		Double double6 = Double.valueOf(0.0D);
		Double double9 = Double.valueOf(0.0D);
		Double double10 = Double.valueOf(0.0D);
		TableItem atableitem[] = fixCostTable.getItems();
		for (int k = 0; k < atableitem.length && atableitem != null; k++) {
			double9 = Double.valueOf(atableitem[k].getText(1).substring(1)
					.trim());
			double10 = Double.valueOf(atableitem[k].getText(2).substring(1)
					.trim());
		}

		if (arrayCostDetails[0].numchildren > 0) {
			for (int l = 0; l < arrayCostDetails[0].numchildren; l++) {
				i += arrayCostDetails[0].taskCostDetails[l].totalEstimatedMinutes;
				j += arrayCostDetails[0].taskCostDetails[l].totalAccruedMinutes;
				double1 = Double
						.valueOf(double1.doubleValue()
								+ arrayCostDetails[0].taskCostDetails[l].totalEstimatedCost);
				double2 = Double
						.valueOf(double2.doubleValue()
								+ arrayCostDetails[0].taskCostDetails[l].totalAccruedCost);
				Double double4 = Double.valueOf(double1.doubleValue()
						- double9.doubleValue());
				Double double7 = Double.valueOf(double2.doubleValue()
						- double10.doubleValue());
				String s2 = HoursFormatter
						.format(arrayCostDetails[0].taskCostDetails[l].totalEstimatedMinutes);
				String s5 = HoursFormatter
						.format(arrayCostDetails[0].taskCostDetails[l].totalAccruedMinutes);
				String s8 = (new StringBuilder()).append(double4.toString())
						.toString();
				String s11 = (new StringBuilder()).append(double7.toString())
						.toString();
				String s13 = arrayCostDetails[0].taskCostDetails[l].task
						.toString();
				control.breakData[l] = (new String[] { s13, s2, s5, s8, s11 });
			}

		} else {
			i += arrayCostDetails[0].totalEstimatedMinutes;
			j += arrayCostDetails[0].totalAccruedMinutes;
			double1 = Double.valueOf(double1.doubleValue()
					+ arrayCostDetails[0].totalEstimatedCost);
			double2 = Double.valueOf(double2.doubleValue()
					+ arrayCostDetails[0].totalAccruedCost);
			Double double5 = Double.valueOf(double1.doubleValue()
					- double9.doubleValue());
			Double double8 = Double.valueOf(double2.doubleValue()
					- double10.doubleValue());
			String s = HoursFormatter
					.format(arrayCostDetails[0].totalEstimatedMinutes);
			String s3 = HoursFormatter
					.format(arrayCostDetails[0].totalAccruedMinutes);
			String s6 = (new StringBuilder()).append(double5.toString())
					.toString();
			String s9 = (new StringBuilder()).append(double8.toString())
					.toString();
			String s12 = arrayCostDetails[0].task.toString();
			control.breakData[0] = (new String[] { s12, s, s3, s6, s9 });
		}
		estCostLabel.setText(CurrencyFormatter.format(double1, currencySymbol));
		// accCostLabel.setText(CurrencyFormatter.format(double2,
		// currencySymbol));

		String s1 = HoursFormatter.format(i);
		estHoursLabel.setText(s1);
		s1 = HoursFormatter.format(j);
		accHoursLabel.setText(s1);
		if (flag) {
			drillDnBut.setEnabled(false);
			String s4 = arrayCostDetails[0].billCode;
			if (s4 != null && s4.length() > 0)
				control.billCodeDBIdx = Math.max(0,
						StringHolder.findInternal(control.lovBillCodeList, s4));
			String s7 = arrayCostDetails[0].billSubCode;
			if (s7 != null && s7.length() > 0)
				control.billSubCodeDBIdx = Math
						.max(0,
								StringHolder
										.findInternal(
												control.lovBillSubCodeList[control.billCodeDBIdx],
												s7));
			String s10 = arrayCostDetails[0].billingType;
			if (s10 != null && s10.length() > 0)
				control.billTypeDBIdx = Math.max(0,
						StringHolder.findInternal(control.billTypeList, s10));
			// billCodeCombo.select(control.billCodeDBIdx);
			//billTypeCombo.select(control.billTypeDBIdx);
			
			// setBillCodeComboSelection(control.billCodeDBIdx,
			// control.billSubCodeDBIdx);
			// billSubCodeCombo.select(control.billSubCodeDBIdx);
			// billRateCombo.select(control.billRateDBIdx);
			fixCostTable.clearAll();
			fixCostTable.removeAll();
			SwtWidgetsFactory.addTableContents(fixCostTable,
					control.fixedCostsTableData);
			breakTable.clearAll();
			breakTable.removeAll();
			SwtWidgetsFactory.addTableContents(breakTable, control.breakData);
		}
	}

	// private void setBillCodeComboSelection(int paramInt1, int paramInt2) {
	// StringHolder[] arrayOfStringHolder;
	// if ((this.control.lovBillSubCodeList.length > paramInt1)
	// && (this.control.lovBillSubCodeList[paramInt1] != null))
	// arrayOfStringHolder = this.control.lovBillSubCodeList[paramInt1];
	// else
	// arrayOfStringHolder = new StringHolder[] { StringHolder.EMPTY };
	// this.billCodeCombo.select(paramInt1);
	// this.billSubCodeCombo.setItems(StringHolder
	// .getDisplay(arrayOfStringHolder));
	// this.billSubCodeCombo.select(paramInt2);
	// }

	class FixedCostDlg extends WizardDialog {
		FixedCostDlg(Shell paramShell, IWizard paramIWizard) {
			super(paramShell, paramIWizard);
		}

		protected void cancelPressed() {
			SchedTaskCostsDlgWizPage.this.needUpdate = false;
			SchedTaskCostsDlgWizPage.this.fireFixedCostNewBool = false;
			SchedTaskCostsDlgWizPage.this.fireFixedCostDetailsBool = false;
			close();
		}

		protected void finishPressed() {

			FixedCostDlgWizPage fixedcostdlgwizpage = (FixedCostDlgWizPage) getSelectedPage();
			com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer fixedcostcontainer = fixedcostdlgwizpage
					.getData();
			if (fixedcostcontainer != null
					&& fixedcostcontainer.name.trim() != "") {
				int i = fixCostTable.getSelectionIndex();
				if (i >= 0) {
					TableItem tableitem = fixCostTable.getItem(i);
					String s = tableitem.getText(0);
					for (int l = 0; l < arrayCostDetails[0].fixedCostData.length; l++)
						if (s.equals(arrayCostDetails[0].fixedCostData[l].name))
							i = l;

					if (fixedcostcontainer
							.equals(control.arrayCostDetails[0].fixedCostData[i])
							&& fireFixedCostDetailsBool)
						needUpdate = false;
					else
						needUpdate = true;
				} else {
					needUpdate = true;
				}

				System.out.println("needUpdate-->" + needUpdate);
				SwtWidgetsFactory.ColumnSortNotifyListeners(fixCostTable);
				fixedCostContainerTemp = fixedcostcontainer;
				int j = 0;
				int k = 0;
				Double double1 = Double.valueOf(0.0D);
				Double double2 = Double.valueOf(0.0D);
				TableItem atableitem[] = fixCostTable.getItems();
				int i1 = 0;
				for (i1 = 0; i1 < atableitem.length && atableitem != null; i1++) {
					// modify by wuwei
					System.out.println("atableitem[i1].getText(1)-->"
							+ atableitem[i1].getText(1)
							+ "  fixedCostContainerTemp.currency:"
							+ fixedCostContainerTemp.currency);
					
					
					try {
						Double double3 = Double.valueOf(CurrencyFormatter
								.parse(atableitem[i1].getText(1),
										fixedCostContainerTemp.currency)
								.toString().trim());
						Double double4 = Double.valueOf(CurrencyFormatter
								.parse(atableitem[i1].getText(2),
										fixedCostContainerTemp.currency)
								.toString().trim());
						double1 = Double.valueOf(double1.doubleValue()
								+ double3.doubleValue());
						double2 = Double.valueOf(double2.doubleValue()
								+ double4.doubleValue());
					} catch (NumberFormatException numberformatexception) {
						SchedTaskCostsDlgWizPage.logger.error(
								numberformatexception.getClass().getName(),
								numberformatexception);
					} catch (ParseException parseexception) {
						SchedTaskCostsDlgWizPage.logger.error(parseexception
								.getClass().getName(), parseexception);
					}
				}

				for (int j1 = 0; j1 < breakTable.getItemCount(); j1++) {
					j += arrayCostDetails[0].totalEstimatedMinutes;
					k += arrayCostDetails[0].totalAccruedMinutes;
					double1 = Double.valueOf(double1.doubleValue()
							+ arrayCostDetails[0].totalEstimatedCost);
					double2 = Double.valueOf(double2.doubleValue()
							+ arrayCostDetails[0].totalAccruedCost);
				}

				estCostLabel.setText(CurrencyFormatter.format(double1,
						currencySymbol));
				// accCostLabel.setText(CurrencyFormatter.format(double2,
				// currencySymbol));
				String s1 = HoursFormatter.format(j);
				estHoursLabel.setText(s1);
				s1 = HoursFormatter.format(k);
				accHoursLabel.setText(s1);
				boolean flag = false;
				boolean flag1 = false;
				if (needUpdate) {
					com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate taskcostupdate = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate();
					taskcostupdate.task = arrayCostDetails[0].task;
					taskcostupdate.billCode = arrayCostDetails[0].billCode;
					taskcostupdate.billingType = arrayCostDetails[0].billingType;
					taskcostupdate.subCode = arrayCostDetails[0].billSubCode;

					System.out.println("arrayCostDetails[0].task-->"
							+ arrayCostDetails[0].task);
					System.out.println("arrayCostDetails[0].billCode-->"
							+ arrayCostDetails[0].billCode);
					System.out.println("arrayCostDetails[0].billingType-->"
							+ arrayCostDetails[0].billingType);
					System.out.println("arrayCostDetails[0].billSubCode-->"
							+ arrayCostDetails[0].billSubCode);

					if (fireFixedCostNewBool)
						taskcostupdate.newCosts = (new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[] { fixedCostContainerTemp });
					else if (fireFixedCostDetailsBool)
						taskcostupdate.updatedCosts = (new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer[] { fixedCostContainerTemp });
					String as[] = fixedCostContainerTemp.estimate.split("\\.");
					String as1[] = fixedCostContainerTemp.actual.split("\\.");

					System.out.println("fixedCostContainerTemp.estimate-->"
							+ fixedCostContainerTemp.estimate);
					System.out.println("fixedCostContainerTemp.actual-->"
							+ fixedCostContainerTemp.actual);

					TCComponentBillRate tccomponentbillrate = getBillRate();
					if (tccomponentbillrate != null)
						taskcostupdate.rate = tccomponentbillrate;
					if (!fixedCostContainerTemp.estimate.trim().equals("")
							&& !fixedCostContainerTemp.actual.trim().equals("")
							&& as.length <= 2 && as1.length <= 2) {
						com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.TaskCostUpdate ataskcostupdate[] = { taskcostupdate };
						UpdateTaskCostDataRunner updatetaskcostdatarunner = new UpdateTaskCostDataRunner(
								ataskcostupdate);
						int k1 = updatetaskcostdatarunner.execute();
						if (k1 == 0) {
							Object obj = updatetaskcostdatarunner.getResults();
							if (obj instanceof com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse) {
								com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse _tmp = (com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.UpdateTaskCostDataResponse) obj;
							}
							TCComponentScheduleTask atccomponentscheduletask[] = new TCComponentScheduleTask[1];
							TCComponentScheduleTask tccomponentscheduletask = arrayCostDetails[0].task;
							atccomponentscheduletask[0] = tccomponentscheduletask;
							CostsRunner costsrunner = new CostsRunner(
									atccomponentscheduletask);
							int l1 = costsrunner.execute();
							com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse costdetailresponse = null;
							if (l1 == 0) {
								Object obj1 = costsrunner.getResults();
								if (obj1 instanceof com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse)
									costdetailresponse = (com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailResponse) obj1;
								if (costdetailresponse != null) {
									control.arrayCostDetails = costdetailresponse.costDetails;
									arrayCostDetails = costdetailresponse.costDetails;
									com.teamcenter.services.rac.projectmanagement._2014_10.ScheduleManagement.CostDetailContainer acostdetailcontainer[] = costdetailresponse.costDetails;
									estCostLabel
											.setText(CurrencyFormatter.format(
													Double.valueOf(acostdetailcontainer[0].totalEstimatedCost),
													currencySymbol));
									// accCostLabel
									// .setText(CurrencyFormatter.format(
									// Double.valueOf(acostdetailcontainer[0].totalAccruedCost),
									// currencySymbol));

									String s2 = HoursFormatter
											.format(acostdetailcontainer[0].totalEstimatedMinutes);
									estHoursLabel.setText(s2);
									s2 = HoursFormatter
											.format(acostdetailcontainer[0].totalAccruedMinutes);
									accHoursLabel.setText(s2);
									int i2 = acostdetailcontainer[0].fixedCostData.length;
									String as2[][] = new String[i2][];
									int j2 = 0;
									for (int k2 = 0; k2 < acostdetailcontainer[0].fixedCostData.length; k2++) {
										String s4 = acostdetailcontainer[0].fixedCostData[k2].name;
										Double double5;
										if (acostdetailcontainer[0].fixedCostData[k2].estimate
												.startsWith("$"))
											double5 = Double
													.valueOf(acostdetailcontainer[0].fixedCostData[k2].estimate
															.substring(1));
										else
											double5 = Double
													.valueOf(acostdetailcontainer[0].fixedCostData[k2].estimate);
										Double double6;
										if (acostdetailcontainer[0].fixedCostData[k2].actual
												.startsWith("$"))
											double6 = Double
													.valueOf(acostdetailcontainer[0].fixedCostData[k2].actual
															.substring(1));
										else
											double6 = Double
													.valueOf(acostdetailcontainer[0].fixedCostData[k2].actual);
										
										String s5 = CurrencyFormatter
												.format(Double.valueOf(double5
														.doubleValue()),
														acostdetailcontainer[0].fixedCostData[k2].currency);
										String s6 = CurrencyFormatter
												.format(Double.valueOf(0.0D),
														acostdetailcontainer[0].fixedCostData[k2].currency);
										if (acostdetailcontainer[0].fixedCostData[k2].useActual)
											s6 = CurrencyFormatter
													.format(Double.valueOf(double6
															.doubleValue()),
															acostdetailcontainer[0].fixedCostData[k2].currency);
										else
											try {
												String s7 = acostdetailcontainer[0].task
														.getStringProperty("fnd0state");
												if (acostdetailcontainer[0].fixedCostData[k2].accrualType == 0
														&& s7.equals("in_progress")
														|| acostdetailcontainer[0].fixedCostData[k2].accrualType == 2
														&& s7.equals("complete"))
													s6 = CurrencyFormatter
															.format(Double
																	.valueOf(double5
																			.doubleValue()),
																	acostdetailcontainer[0].fixedCostData[k2].currency);
												if (acostdetailcontainer[0].fixedCostData[k2].accrualType == 1) {
													int l2 = (int) acostdetailcontainer[0].task
															.getDoubleProperty("complete_percent");
													double d = (double) l2 / 100D;
													double d1 = double5
															.doubleValue() * d;
													s6 = CurrencyFormatter
															.format(Double
																	.valueOf(d1),
																	acostdetailcontainer[0].fixedCostData[k2].currency);
												}
											} catch (TCException tcexception) {
												SchedTaskCostsDlgWizPage.logger
														.error(tcexception
																.getClass()
																.getName(),
																tcexception);
											}
										
										//new by wuwei
										s6=acostdetailcontainer[0].fixedCostData[k2].billingType;
										System.out.println("lala  s4:"+s4+" s5:"+s5+" s6:"+s6);//control.billTypeList[control.billTypeDBIdx].getInternal()
										as2[j2] = (new String[] {
												s4,
												s5,
												s6,
												acostdetailcontainer[0].fixedCostData[k2].fixedCost
														.getUid() });
										j2++;
									}

									fixCostTable.clearAll();
									fixCostTable.removeAll();
									SwtWidgetsFactory.addTableContents(
											fixCostTable, as2);
									fixCostTable.redraw();
								}
							}
							needUpdate = false;
							fireFixedCostDetailsBool = false;
							fireFixedCostNewBool = false;
						} else {
							detailFixedCostBut.setEnabled(false);
							needUpdate = false;
							fireFixedCostDetailsBool = false;
							fireFixedCostNewBool = false;
						}
						super.finishPressed();
					} else {
						String s3 = registry.getString("schedTaskCosts.NOCOST");
						s3 = MessageFormat.format(
								s3,
								new Object[] {
										nameCache.getAttrDisplayName(
												ClassType.FIXED_COST,
												"est_costvalue_form_tag"),
										nameCache.getAttrDisplayName(
												ClassType.FIXED_COST,
												"act_costvalue_form_tag") });
						MessageBox.post(getShell(), s3,
								registry.getString("schedTaskCosts.TITLE"), 2);
					}
				}
			}
		}

		private TCComponentBillRate getBillRate() {
			TCComponentBillRate localTCComponentBillRate = null;
			try {
				TCComponent[] arrayOfTCComponent = ScheduleUtil
						.getBillRates(SchedTaskCostsDlgWizPage.this.sess);
				if (arrayOfTCComponent != null)
					for (int i = 0; i < arrayOfTCComponent.length; ++i) {

						// modify by wuwei
						// if (!(arrayOfTCComponent[i]
						// .getStringProperty("object_name")
						// .equals(SchedTaskCostsDlgWizPage.this.billRateCombo
						// .getText())))
						// continue;
						localTCComponentBillRate = (TCComponentBillRate) arrayOfTCComponent[i];
					}
			} catch (TCException localTCException) {
				SchedTaskCostsDlgWizPage.logger.error(localTCException
						.getClass().getName(), localTCException);
			}
			return localTCComponentBillRate;
		}
	}
	
	
}