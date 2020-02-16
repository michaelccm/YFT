/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.schedule.commands.costs;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentSchedulingFixedCost;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
import com.teamcenter.rac.schedule.common.util.SwtWidgetsFactory;
import com.teamcenter.rac.schedule.project.common.gui.StringHolder;
import com.teamcenter.rac.schedule.project.common.l10n.ClassType;
import com.teamcenter.rac.schedule.project.common.l10n.DisplayNameCache;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
import com.teamcenter.rac.util.CurrencyFormatter;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class FixedCostDlgWizPage extends WizardPage {
	private Registry registry;
	private int numCols = 2;
	private SchedTaskCostsDialogControl controller;
	private StyledText nameText;
	private StyledText estimatedFixedCostText;

	// modify by wuwei
	// private StyledText accruedFixedCostText;
	// private Button chkButton;

	// modify by wuwei
	// private Combo accrualTypeCombo;

	private Combo currencyCombo;

	// modify by wuwei
	// private Combo billCodeCombo;
	// private Combo billSubCodeCombo;

	private Combo billTypeCombo;
	boolean addNew = true;
	private String name = "";
	private int accrualType = 0;
	private String disEstimatedFixedCost = "";
	private String disAccruedFixedCost = "";
	private String currency = "";
	private int currencyIdx = 0;
	private boolean useActual = false;
	private String billCode = "";
	private String billSubCode = "";
	private String billType = "";
	private DisplayNameCache nameCache;
	private Double estimatedFixedCost = Double.valueOf(0.0D);
	private Double accruedFixedCost = Double.valueOf(0.0D);
	int billCodeIdx = 0;
	int billSubCodeIndex = 0;
	int billingTypeIndex = 0;
	private int selbillCodeCombo = 0;
	private int selbillSubCodeCombo = 0;
	private int selbillTypeCombo = 0;
	private TCComponentSchedulingFixedCost schedulingFixedCost;
	private static final Logger logger = Logger
			.getLogger(FixedCostDlgWizPage.class);
	private static final String defFixedCostEstimate = "0.0";
	private static final String defFixedCostAccrued = "0.0";

	public FixedCostDlgWizPage(
			SchedTaskCostsDialogControl paramSchedTaskCostsDialogControl,
			Object paramObject, boolean paramBoolean, String paramString1,
			String paramString2, int paramInt1, int paramInt2, int paramInt3,
			int paramInt4, String paramString3) {
		super(paramString1);
		
		System.out.println("===FixedCostDlgWizPage start===");
		setDescription(paramString2);
		this.numCols = paramInt1;
		this.controller = paramSchedTaskCostsDialogControl;
		this.addNew = paramBoolean;
		this.currency = paramString3;
		this.nameCache = RACInterface
				.getDisplayNameCache(paramSchedTaskCostsDialogControl
						.getSession());
		this.registry = Registry.getRegistry(this);
		if ((paramBoolean)
				|| (paramSchedTaskCostsDialogControl.arrayCostDetails == null)
				|| (paramSchedTaskCostsDialogControl.arrayCostDetails.length <= 0))
			return;
		this.name = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].name;
		this.accrualType = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].accrualType;
		this.currency = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].currency;
		this.currencyIdx = Arrays.asList(this.controller.currencyList).indexOf(
				this.currency);
		try {
			if (paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].estimate != null)
				this.estimatedFixedCost = Double
						.valueOf(paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].estimate);
			if (paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].actual != null)
				this.accruedFixedCost = Double
						.valueOf(paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].actual);
		} catch (NumberFormatException localNumberFormatException) {
			logger.error(localNumberFormatException.getClass().getName(),
					localNumberFormatException);
		}
		this.disEstimatedFixedCost = CurrencyFormatter.format(
				Double.valueOf(this.estimatedFixedCost.doubleValue()),
				this.currency);
		this.disAccruedFixedCost = CurrencyFormatter.format(
				Double.valueOf(this.accruedFixedCost.doubleValue()),
				this.currency);
		this.useActual = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].useActual;
		this.billCode = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].billCode;
		this.billSubCode = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].subCode;
		this.billType = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].billingType;
		this.schedulingFixedCost = paramSchedTaskCostsDialogControl.arrayCostDetails[0].fixedCostData[paramInt2].fixedCost;
		if ((this.billCode != null) && (this.billCode.length() > 0))
			this.billCodeIdx = Math.max(0, StringHolder.findInternal(
					this.controller.lovBillCodeList, this.billCode));
		if ((this.billSubCode != null) && (this.billSubCode.length() > 0))
			this.billSubCodeIndex = Math.max(0, StringHolder.findInternal(
					this.controller.lovBillSubCodeList[this.billCodeIdx],
					this.billSubCode));
		if ((this.billType != null) && (this.billType.length() > 0))
			this.billingTypeIndex = Math.max(0, StringHolder.findInternal(
					this.controller.billTypeList, this.billType));
		this.selbillCodeCombo = this.billCodeIdx;
		this.selbillSubCodeCombo = this.billSubCodeIndex;
		this.selbillTypeCombo = this.billingTypeIndex;
	}

	public void createControl(Composite paramComposite) {
		GridLayout localGridLayout = new GridLayout();
		localGridLayout.numColumns = this.numCols;
		paramComposite.setLayout(localGridLayout);
		for (int i = 0; i < this.controller.lovBillCodeList.length; ++i) {
			if (!(this.billCode.equals(this.controller.lovBillCodeList[i]
					.toString())))
				continue;
			this.billCodeIdx = i;
		}
		if ((this.controller.lovBillSubCodeList.length > this.billCodeIdx)
				&& (this.controller.lovBillSubCodeList[this.billCodeIdx] != null)) {
			StringHolder[] arrayOfStringHolder1 = this.controller.lovBillSubCodeList[this.billCodeIdx];
		}
		StringHolder[] arrayOfStringHolder1 = { StringHolder.EMPTY };
		for (int j = 0; j < arrayOfStringHolder1.length; ++j) {
			if (!(this.billSubCode.equals(arrayOfStringHolder1[j].toString())))
				continue;
			this.billSubCodeIndex = j;
		}
		boolean j = false;
		for (int aa = 0; aa < this.controller.billTypeList.length; ++aa) {
			if (!(this.billType.equals(this.controller.billTypeList[aa]
					.toString())))
				continue;
			this.billingTypeIndex = aa;
		}

		this.nameText = SwtWidgetsFactory.addText(
				paramComposite,
				this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
						"cost_name") + ":", this.name, 300, 16384, j);
		this.nameText.setTextLimit(30);
		this.nameText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent paramTraverseEvent) {
				if ((paramTraverseEvent.detail != 16)
						&& (paramTraverseEvent.detail != 8))
					return;
				paramTraverseEvent.doit = true;
			}
		});

		// modify by wuwei
		// this.accrualTypeCombo = SwtWidgetsFactory.addCombo(
		// paramComposite,
		// this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
		// "accrual_type") + ":",
		// new String[] {
		// this.registry.getString("fixedCost.START_ACCRUAL"),
		// this.registry.getString("fixedCost.PRORATED_ACCRUAL"),
		// this.registry.getString("fixedCost.END_ACCRUAL") }, 0,
		// j);
		// this.accrualTypeCombo.select(this.accrualType);

		this.estimatedFixedCostText = SwtWidgetsFactory.addText(
				paramComposite,
				this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
						"est_costvalue_form_tag") + ":",
				this.disEstimatedFixedCost, 100, 131072, j);
		this.estimatedFixedCostText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent paramTraverseEvent) {
				// modify by wuwei
				// if ((paramTraverseEvent.detail != 16)
				// && (paramTraverseEvent.detail != 8))
				// return;
				paramTraverseEvent.doit = true;
			}
		});
		this.estimatedFixedCostText
				.addVerifyKeyListener(new VerifyKeyListener() {
					public void verifyKey(VerifyEvent paramVerifyEvent) {
						// modify by wuwei
						// paramVerifyEvent.doit = false;
						paramVerifyEvent.doit = true;

						if ((paramVerifyEvent.keyCode != 16777219)
								&& (paramVerifyEvent.keyCode != 16777220)
								&& (paramVerifyEvent.keyCode != 8)
								&& (paramVerifyEvent.keyCode != 127)
								&& (paramVerifyEvent.keyCode != 16777225)
								&& (paramVerifyEvent.keyCode != 32)
								&& (paramVerifyEvent.keyCode != 10)
								&& (paramVerifyEvent.keyCode != 48)
								&& (paramVerifyEvent.keyCode != 49)
								&& (paramVerifyEvent.keyCode != 50)
								&& (paramVerifyEvent.keyCode != 51)
								&& (paramVerifyEvent.keyCode != 52)
								&& (paramVerifyEvent.keyCode != 53)
								&& (paramVerifyEvent.keyCode != 54)
								&& (paramVerifyEvent.keyCode != 55)
								&& (paramVerifyEvent.keyCode != 56)
								&& (paramVerifyEvent.keyCode != 57)
								&& (paramVerifyEvent.keyCode != 16777264)
								&& (paramVerifyEvent.keyCode != 16777265)
								&& (paramVerifyEvent.keyCode != 16777266)
								&& (paramVerifyEvent.keyCode != 16777267)
								&& (paramVerifyEvent.keyCode != 16777268)
								&& (paramVerifyEvent.keyCode != 16777269)
								&& (paramVerifyEvent.keyCode != 16777270)
								&& (paramVerifyEvent.keyCode != 16777271)
								&& (paramVerifyEvent.keyCode != 16777272)
								&& (paramVerifyEvent.keyCode != 16777273)
								&& (paramVerifyEvent.keyCode != 16777262)
								&& (paramVerifyEvent.keyCode != 78)
								&& (paramVerifyEvent.keyCode != 46)
								&& (paramVerifyEvent.keyCode != 72)
								&& (paramVerifyEvent.keyCode != 68)
								&& (paramVerifyEvent.keyCode != 87))
							return;
						paramVerifyEvent.doit = true;
					}
				});
		this.estimatedFixedCostText.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent paramFocusEvent) {
				super.focusLost(paramFocusEvent);
				Double localDouble = FixedCostDlgWizPage.this.checkCost(
						FixedCostDlgWizPage.this.estimatedFixedCostText,
						FixedCostDlgWizPage.this.estimatedFixedCost,
						FixedCostDlgWizPage.this.currency);
				if (localDouble == null)
					return;
				FixedCostDlgWizPage.this.estimatedFixedCost = localDouble;
			}
		});

		// modify by wuwei
		// this.accruedFixedCostText = SwtWidgetsFactory.addText(
		// paramComposite,
		// this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
		// "act_costvalue_form_tag") + ":",
		// this.disAccruedFixedCost, 100, 131072, j);
		// this.accruedFixedCostText.addTraverseListener(new TraverseListener()
		// {
		// public void keyTraversed(TraverseEvent paramTraverseEvent) {
		// if ((paramTraverseEvent.detail != 16)
		// && (paramTraverseEvent.detail != 8))
		// return;
		// paramTraverseEvent.doit = true;
		// }
		// });

		// this.accruedFixedCostText.addVerifyKeyListener(new
		// VerifyKeyListener() {
		// public void verifyKey(VerifyEvent paramVerifyEvent) {
		// System.out.println(paramVerifyEvent.character);
		// paramVerifyEvent.doit = false;
		// if ((paramVerifyEvent.keyCode != 16777219)
		// && (paramVerifyEvent.keyCode != 16777220)
		// && (paramVerifyEvent.keyCode != 8)
		// && (paramVerifyEvent.keyCode != 127)
		// && (paramVerifyEvent.keyCode != 16777225)
		// && (paramVerifyEvent.keyCode != 32)
		// && (paramVerifyEvent.keyCode != 10)
		// && (paramVerifyEvent.keyCode != 48)
		// && (paramVerifyEvent.keyCode != 49)
		// && (paramVerifyEvent.keyCode != 50)
		// && (paramVerifyEvent.keyCode != 51)
		// && (paramVerifyEvent.keyCode != 52)
		// && (paramVerifyEvent.keyCode != 53)
		// && (paramVerifyEvent.keyCode != 54)
		// && (paramVerifyEvent.keyCode != 55)
		// && (paramVerifyEvent.keyCode != 56)
		// && (paramVerifyEvent.keyCode != 57)
		// && (paramVerifyEvent.keyCode != 16777264)
		// && (paramVerifyEvent.keyCode != 16777265)
		// && (paramVerifyEvent.keyCode != 16777266)
		// && (paramVerifyEvent.keyCode != 16777267)
		// && (paramVerifyEvent.keyCode != 16777268)
		// && (paramVerifyEvent.keyCode != 16777269)
		// && (paramVerifyEvent.keyCode != 16777270)
		// && (paramVerifyEvent.keyCode != 16777271)
		// && (paramVerifyEvent.keyCode != 16777272)
		// && (paramVerifyEvent.keyCode != 16777273)
		// && (paramVerifyEvent.keyCode != 16777262)
		// && (paramVerifyEvent.keyCode != 78)
		// && (paramVerifyEvent.keyCode != 46)
		// && (paramVerifyEvent.keyCode != 72)
		// && (paramVerifyEvent.keyCode != 68)
		// && (paramVerifyEvent.keyCode != 87))
		// return;
		// paramVerifyEvent.doit = true;
		// }
		// });
		// this.accruedFixedCostText.addFocusListener(new FocusAdapter() {
		// public void focusLost(FocusEvent paramFocusEvent) {
		// super.focusLost(paramFocusEvent);
		// Double localDouble = FixedCostDlgWizPage.this.checkCost(
		// FixedCostDlgWizPage.this.accruedFixedCostText,
		// FixedCostDlgWizPage.this.accruedFixedCost,
		// FixedCostDlgWizPage.this.currency);
		// if (localDouble == null)
		// return;
		// FixedCostDlgWizPage.this.accruedFixedCost = localDouble;
		// }
		// });

		this.currencyCombo = SwtWidgetsFactory.addCombo(
				paramComposite,
				this.nameCache.getAttrDisplayName(ClassType.COST_VALUE,
						"currency") + ":", this.controller.currencyList, 0, j);
		this.currencyCombo.select(this.currencyIdx);
		this.currencyCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				super.widgetSelected(paramSelectionEvent);
				FixedCostDlgWizPage.this.currency = FixedCostDlgWizPage.this.currencyCombo
						.getText();
				FixedCostDlgWizPage.this.estimatedFixedCostText.setText(CurrencyFormatter.format(
						Double.valueOf(FixedCostDlgWizPage.this.estimatedFixedCost
								.doubleValue()),
						FixedCostDlgWizPage.this.currency));
				// modify by wuwei
				// FixedCostDlgWizPage.this.accruedFixedCostText.setText(CurrencyFormatter.format(
				// Double.valueOf(FixedCostDlgWizPage.this.accruedFixedCost
				// .doubleValue()),
				// FixedCostDlgWizPage.this.currency));
			}
		});
		// this.chkButton = SwtWidgetsFactory.addCheckBox(paramComposite,
		// this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
		// "use_actual_cost"), j);
		// this.chkButton.setSelection(this.useActual);
		this.selbillCodeCombo = this.billCodeIdx;
		this.selbillSubCodeCombo = this.billSubCodeIndex;
		this.selbillTypeCombo = this.billingTypeIndex;

		// modify by wuwei
		// this.billCodeCombo = SwtWidgetsFactory.addCombo(
		// paramComposite,
		// this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
		// "bill_code") + ":", this.controller.lovBillCodeList,
		// this.selbillCodeCombo, j);
		// this.billCodeCombo.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent paramSelectionEvent) {
		// FixedCostDlgWizPage.this.selbillCodeCombo =
		// FixedCostDlgWizPage.this.billCodeCombo
		// .getSelectionIndex();
		// FixedCostDlgWizPage.this.setBillCodeComboSelection(
		// FixedCostDlgWizPage.this.selbillCodeCombo,
		// FixedCostDlgWizPage.this.selbillSubCodeCombo,
		// FixedCostDlgWizPage.this.selbillTypeCombo);
		// }
		// });
		StringHolder[] arrayOfStringHolder2 = new StringHolder[0];
		if ((this.controller.lovBillSubCodeList.length > this.billCodeIdx)
				&& (this.billCodeIdx > -1)
				&& (this.controller.lovBillSubCodeList[this.billCodeIdx] != null))
			arrayOfStringHolder2 = this.controller.lovBillSubCodeList[this.billCodeIdx];
		// this.billSubCodeCombo = SwtWidgetsFactory.addCombo(
		// paramComposite,
		// this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
		// "bill_sub_code") + ":", arrayOfStringHolder2,
		// this.selbillSubCodeCombo, j);
		// this.billSubCodeCombo.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent paramSelectionEvent) {
		// FixedCostDlgWizPage.this.selbillSubCodeCombo =
		// FixedCostDlgWizPage.this.billSubCodeCombo
		// .getSelectionIndex();
		// FixedCostDlgWizPage.this.setBillCodeComboSelection(
		// FixedCostDlgWizPage.this.selbillCodeCombo,
		// FixedCostDlgWizPage.this.selbillSubCodeCombo,
		// FixedCostDlgWizPage.this.selbillTypeCombo);
		// }
		// });
//		this.billTypeCombo = SwtWidgetsFactory.addCombo(
//				paramComposite,
//				this.nameCache.getAttrDisplayName(ClassType.FIXED_COST,
//						"bill_type") + ":", this.controller.billTypeList,
//				this.selbillTypeCombo, j);
//		this.billTypeCombo.select(this.billingTypeIndex);
//		this.billTypeCombo.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent paramSelectionEvent) {
//				FixedCostDlgWizPage.this.selbillTypeCombo = FixedCostDlgWizPage.this.billTypeCombo
//						.getSelectionIndex();
//				FixedCostDlgWizPage.this.setBillCodeComboSelection(
//						FixedCostDlgWizPage.this.selbillCodeCombo,
//						FixedCostDlgWizPage.this.selbillSubCodeCombo,
//						FixedCostDlgWizPage.this.selbillTypeCombo);
//			}
//		});
	
		//add by wuwei
    TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();

    TCComponentListOfValues findLOVByName = TCComponentListOfValuesType.findLOVByName(session, "JCI6_NonLabour");

    this.controller.billTypeList = ScheduleUtil.getStringHolders(findLOVByName);

    this.billTypeCombo = SwtWidgetsFactory.addCombo(paramComposite, "bill_type:", this.controller.billTypeList, 0, j);

  
    this.billTypeCombo.select(billingTypeIndex);// billType
    
		SwtWidgetsFactory.addBlank(paramComposite, 2);
		if (ScheduleDeferredContext.inDeferredSession())
			SwtWidgetsFactory.addLabel(paramComposite,
					this.registry.getString("notes.TITLE"),
					this.registry.getString("deferred.NOTES"), 200, 16384, j);
		paramComposite.pack();
		setControl(paramComposite);
	}

	com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer getData() {
		com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer localFixedCostContainer = new com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.FixedCostContainer();
		if (this.nameText.getText() != null) {
			localFixedCostContainer.name = this.nameText.getText().trim();
		}

		if (validateFields()) {
			// modify by wuwei
			// localFixedCostContainer.accrualType = this.accrualTypeCombo
			// .getSelectionIndex();

			localFixedCostContainer.accrualType = 0;

			// modify by wuwei
			System.out
					.println("FixedCostDlgWizPage::getData() localFixedCostContainer.accrualType-->"
							+ localFixedCostContainer.accrualType);
			System.out
					.println("FixedCostDlgWizPage::getData() estimatedFixedCostText-->"
							+ estimatedFixedCostText.getText());
			try {
				if ((this.estimatedFixedCostText.getText() != null)
						&& (!(this.estimatedFixedCostText.getText().trim()
								.equals("")))) {
					localFixedCostContainer.estimate = String.valueOf(Double
							.valueOf(CurrencyFormatter
									.parse(this.estimatedFixedCostText
											.getText(),
											this.currencyCombo.getText())
									.toString().trim()));
					System.out
							.println("FixedCostDlgWizPage::getData() estimatedFixedCostText-->"
									+ this.estimatedFixedCostText.getText());
					System.out
							.println("FixedCostDlgWizPage::getData() currencyCombo-->"
									+ this.currencyCombo.getText());
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.estimate-->"
									+ localFixedCostContainer.estimate);

					// System.out.println("FixedCostDlgWizPage::getData() accruedFixedCostText.estimate-->"+accruedFixedCostText.getText());

					// localFixedCostContainer.actual = String.valueOf(Double
					// .valueOf(CurrencyFormatter
					// .parse(this.accruedFixedCostText.getText(),
					// this.currencyCombo.getText())
					// .toString().trim()));
					localFixedCostContainer.actual = "0.0";

					//new by wuwei
					localFixedCostContainer.currency = this.currencyCombo
							.getText().trim();

					localFixedCostContainer.useActual = false;
					// localFixedCostContainer.useActual = this.chkButton
					// .getSelection();

					localFixedCostContainer.billCode = "unassigned";
					localFixedCostContainer.subCode = "unassigned";
					// modify by wuwei
					// localFixedCostContainer.billCode = StringHolder
					// .findInternalValue(this.controller.lovBillCodeList,
					// this.billCodeCombo.getText());
					// localFixedCostContainer.subCode = StringHolder
					// .findInternalValue(
					// this.controller.lovBillSubCodeList[this.selbillCodeCombo],
					// this.billSubCodeCombo.getText());
					localFixedCostContainer.billingType = StringHolder
							.findInternalValue(this.controller.billTypeList,
									this.billTypeCombo.getText());
					localFixedCostContainer.fixedCost = this.schedulingFixedCost;

					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.actual-->"
									+ localFixedCostContainer.actual);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.currency-->"
									+ localFixedCostContainer.currency);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.useActual-->"
									+ localFixedCostContainer.useActual);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.billCode-->"
									+ localFixedCostContainer.billCode);

					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.subCode-->"
									+ localFixedCostContainer.subCode);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.billingType-->"
									+ localFixedCostContainer.billingType);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.fixedCost-->"
									+ localFixedCostContainer.fixedCost);

				} else {
					localFixedCostContainer.estimate = "0.0";
				}

			} catch (ParseException localParseException1) {
				logger.error(localParseException1.getClass().getName(),
						localParseException1);
			}

			try {
				// modify by wuwei
				localFixedCostContainer.actual = "0.0";
				if (!(localFixedCostContainer.actual.trim().equals(""))) {
					// if
					// (!(this.accruedFixedCostText.getText().trim().equals("")))
					// {
					// localFixedCostContainer.actual = String.valueOf(Double
					// .valueOf(CurrencyFormatter
					// .parse(this.accruedFixedCostText.getText(),
					// this.currencyCombo.getText())
					// .toString().trim()));
					localFixedCostContainer.currency = this.currencyCombo
							.getText().trim();
					// localFixedCostContainer.useActual = this.chkButton
					// .getSelection();
					// localFixedCostContainer.billCode = StringHolder
					// .findInternalValue(this.controller.lovBillCodeList,
					// this.billCodeCombo.getText());
					// localFixedCostContainer.subCode = StringHolder
					// .findInternalValue(
					// this.controller.lovBillSubCodeList[this.selbillCodeCombo],
					// this.billSubCodeCombo.getText());
					// modify by wuwei
					localFixedCostContainer.useActual = false;
					localFixedCostContainer.billCode = "unassigned";
					localFixedCostContainer.subCode = "unassigned";

					localFixedCostContainer.billingType = StringHolder
							.findInternalValue(this.controller.billTypeList,
									this.billTypeCombo.getText());
					localFixedCostContainer.fixedCost = this.schedulingFixedCost;

					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.currency-->"
									+ localFixedCostContainer.currency);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.actual-->"
									+ localFixedCostContainer.actual);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.useActual-->"
									+ localFixedCostContainer.useActual);

					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.billCode-->"
									+ localFixedCostContainer.billCode);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.subCode-->"
									+ localFixedCostContainer.subCode);
					System.out
							.println("FixedCostDlgWizPage::getData() localFixedCostContainer.billingType-->"
									+ localFixedCostContainer.billingType);

				} else {
					localFixedCostContainer.actual = "0.0";
				}

			} catch (Exception localParseException2) {
				logger.error(localParseException2.getClass().getName(),
						localParseException2);
			}

		}
		return localFixedCostContainer;
	}

	private BigDecimal validateCost(StyledText paramStyledText,
			BigDecimal paramBigDecimal, String paramString) {
		String str1 = paramStyledText.getText();
		BigDecimal localBigDecimal = null;
		if ((str1 != null) && (str1.trim().length() > 0))
			try {
				localBigDecimal = CurrencyFormatter.parse(str1, paramString);
				paramStyledText.setText(CurrencyFormatter.format(
						localBigDecimal, paramString));
			} catch (ParseException localParseException) {
				String str2 = this.registry.getString("schedTaskCosts.BADCOST");
				str2 = MessageFormat.format(str2, new Object[] { str1 });
				MessageBox.post(getShell(), str2,
						this.registry.getString("schedTaskCosts.TITLE"), 1);
				paramStyledText.setText(CurrencyFormatter.format(
						paramBigDecimal, paramString));
			}
		return localBigDecimal;
	}

	boolean validateFields() {
		boolean i = true;
		if (((this.nameText.getText() != null) && (this.nameText.getText()
				.trim() == ""))
				|| (this.nameText.getText().trim() == null)
				|| (this.nameText.getText().length() == 0)) {
			String str = this.registry.getString("schedTaskCosts.NONAME");
			str = MessageFormat.format(str, new Object[] { this.nameCache
					.getAttrDisplayName(ClassType.FIXED_COST, "cost_name") });
			MessageBox.post(getShell(), str,
					this.registry.getString("schedTaskCosts.TITLE"), 2);
			i = false;
			return i;
		}
		return i;
	}

	private Double checkCost(StyledText paramStyledText, Double paramDouble,
			String paramString) {
		String str1 = paramStyledText.getText();
		Double localDouble = null;
		if ((str1 != null) && (str1.trim().length() > 0))
			try {
				localDouble = Double.valueOf(CurrencyFormatter.parse(str1,
						paramString).toString());
				paramStyledText.setText(CurrencyFormatter.format(localDouble,
						paramString));
			} catch (ParseException localParseException) {
				String str2 = this.registry.getString("schedTaskCosts.BADCOST");
				str2 = MessageFormat.format(str2, new Object[] { str1 });
				MessageBox.post(getShell(), str2,
						this.registry.getString("schedTaskCosts.TITLE"), 1);
				paramStyledText.setText(CurrencyFormatter.format(paramDouble,
						paramString));
			}
		return localDouble;
	}

	// modify by wuwei
	private void setBillCodeComboSelection(int paramInt1, int paramInt2,
			int paramInt3) {
		StringHolder[] arrayOfStringHolder;
		if ((this.controller.lovBillSubCodeList.length > paramInt1)
				&& (this.controller.lovBillSubCodeList[paramInt1] != null))
			arrayOfStringHolder = this.controller.lovBillSubCodeList[paramInt1];
		else
			arrayOfStringHolder = new StringHolder[] { StringHolder.EMPTY };
		// this.billCodeCombo.select(paramInt1);
		// this.billSubCodeCombo.setItems(StringHolder
		// .getDisplay(arrayOfStringHolder));
		// this.billSubCodeCombo.select(paramInt2);
		
		
		//this.billCode = this.billCodeCombo.getItem(paramInt1);
		//this.billSubCode = this.billSubCodeCombo.getItem(paramInt2);
		this.billType = this.billTypeCombo.getItem(paramInt3);
	}
}