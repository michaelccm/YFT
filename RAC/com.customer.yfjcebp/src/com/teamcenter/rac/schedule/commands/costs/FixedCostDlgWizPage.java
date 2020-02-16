///*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
//
//package com.teamcenter.rac.schedule.commands.costs;
//
//import com.teamcenter.rac.schedule.project.common.costing.FixedCostDetail;
//import com.teamcenter.rac.schedule.project.common.costing.FixedCostDetailImpl;
//import com.teamcenter.rac.schedule.project.common.gui.StringHolder;
//import com.teamcenter.rac.schedule.project.common.l10n.ClassType;
//import com.teamcenter.rac.schedule.project.common.l10n.DisplayNameCache;
//import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
//import com.teamcenter.rac.aifrcp.AIFUtility;
//import com.teamcenter.rac.kernel.TCComponentListOfValues;
//import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
//import com.teamcenter.rac.schedule.common.util.CostValueFormatter;
//import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
//import com.teamcenter.rac.schedule.common.util.SwtWidgetsFactory;
//import com.teamcenter.rac.util.*;
//import com.teamcenter.rac.util.MessageBox;
//
//import java.math.BigDecimal;
//import java.text.MessageFormat;
//import java.text.ParseException;
//import java.util.Arrays;
//import java.util.List;
//import org.eclipse.jface.wizard.WizardPage;
//import org.eclipse.swt.custom.StyledText;
//import org.eclipse.swt.events.*;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.*;
//
//// Referenced classes of package com.teamcenter.rac.schedule.commands.costs:
////            SchedTaskCostsDialogControl
//
///*
// * DECOMPILATION REPORT
// * 
// * Decompiled from:
// * C:\workspace\com.teamcenter.rac.schedule\com.teamcenter.rac.schedule_9000
// * .1.0.jar Total time: 109 ms Jad reported messages/errors: Couldn't resolve
// * all exception handlers in method validateFields Exit status: 0 Caught
// * exceptions:
// */
//public class FixedCostDlgWizPage extends WizardPage {
//	//String[] valuesFullNames;
//	private Registry r;
//	private int numCols;
//	private SchedTaskCostsDialogControl controller;
//	private StyledText nameText;
//	private StyledText estText;
//	private StyledText actText;
//	private Button chkButton;
//	// private Combo accrualTypeCombo;
//	private Combo currencyCombo;
//	// private Combo billCodeCombo;
//	// private Combo billSubCodeCombo;
//	private Combo billTypeCombo;
//	boolean addNew;
//	private Object updateFixCostObj;
//	private String name;
//	private int accrualType;
//	private String estCost;
//	private String actCost;
//	private BigDecimal estBD;
//	private BigDecimal actBD;
//	private BigDecimal accruedCost;
//	private String currency;
//	private int currencyIdx;
//	private boolean useActual;
//	private int billCode;
//	private int billSubCode;
//	private int billType;
//	private DisplayNameCache nameCache;
//
//	public FixedCostDlgWizPage(
//			SchedTaskCostsDialogControl schedtaskcostsdialogcontrol,
//			Object obj, boolean flag, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3,
//			int paramInt4, String paramString3) {
//		super(paramString1);
//		setDescription(paramString2);
//		numCols = 2;
//		addNew = true;
//		updateFixCostObj = null;
//		name = "";
//		accrualType = 0;
//		estCost = "";
//		actCost = "";
//		estBD = new BigDecimal(0);
//		actBD = new BigDecimal(0);
//		accruedCost = new BigDecimal(0);
//		currency = "";
//		currencyIdx = 0;
//		useActual = false;
//		billCode = 0;
//		billSubCode = 0;
//		billType = 0;
//		
//		numCols = paramInt1;
//		controller = schedtaskcostsdialogcontrol;
//		addNew = flag;
//		updateFixCostObj = obj;
//		billCode = j;
//		billSubCode = k;
//		billType = l;
//		currency = s2;
//		nameCache = RACInterface
//				.getDisplayNameCache(schedtaskcostsdialogcontrol.getSession());
//		r = Registry.getRegistry(this);
//		if (!flag) {
//			FixedCostDetail fixedcostdetail = (FixedCostDetail) updateFixCostObj;
//			name = fixedcostdetail.getName();
//			accrualType = fixedcostdetail.getAccrualType();
//			currency = fixedcostdetail.getCurrency();
//			currencyIdx = Arrays.asList(controller.currencyList).indexOf(
//					currency);
//			actBD = fixedcostdetail.getActualCost();
//			estBD = fixedcostdetail.getEstimatedCost();
//			estCost = CurrencyFormatter.format(
//					fixedcostdetail.getEstimatedCost(), currency);
//			actCost = CurrencyFormatter.format(fixedcostdetail.getActualCost(),
//					currency);
//			// useActual = fixedcostdetail.getUseActualCost();
//			useActual = false;
//			String s3 = fixedcostdetail.getBillingCode();
//			billCode = Math.max(0,
//					StringHolder.findInternal(controller.lovBillCodeList, s3));
//			s3 = fixedcostdetail.getBillingSubCode();
//			StringHolder astringholder[] = billCode <= -1
//					|| controller.lovBillSubCodeList.length <= billCode
//					|| controller.lovBillSubCodeList[billCode] == null ? (new StringHolder[] { StringHolder.EMPTY })
//					: controller.lovBillSubCodeList[billCode];
//			billSubCode = Math.max(0,
//					StringHolder.findInternal(astringholder, s3));
//			s3 = fixedcostdetail.getBillingType();
//			billType = Math.max(0,
//					StringHolder.findInternal(controller.billTypeList, s3));
//		}
//	}
//
//	public void createControl(Composite composite) {
//		GridLayout gridlayout = new GridLayout();
//		gridlayout.numColumns = numCols;
//		composite.setLayout(gridlayout);
//		boolean flag = false;
//		nameText = SwtWidgetsFactory.addText(
//				composite,
//				(new StringBuilder())
//						.append(nameCache.getAttrDisplayName(
//								ClassType.FIXED_COST, "Cost Name")).append(":")
//						.toString(), name, 300, 16384, flag);
//		nameText.setTextLimit(30);
//		// accrualTypeCombo = SwtWidgetsFactory.addCombo(
//		// composite,
//		// (new StringBuilder())
//		// .append(nameCache.getAttrDisplayName(
//		// ClassType.FIXED_COST, "accrual_type"))
//		// .append(":").toString(),
//		// new String[] { r.getString("fixedCost.START_ACCRUAL"),
//		// r.getString("fixedCost.PRORATED_ACCRUAL"),
//		// r.getString("fixedCost.END_ACCRUAL") }, 0, flag);
//		// accrualTypeCombo.select(accrualType);
//		estText = SwtWidgetsFactory.addText(
//				composite,
//				(new StringBuilder())
//						.append(nameCache.getAttrDisplayName(
//								ClassType.FIXED_COST, "Estimated Cost"))
//						.append(":").toString(), estCost, 100, 131072, flag);
//		estText.addFocusListener(new FocusAdapter() {
//
//			public void focusLost(FocusEvent focusevent) {
//				super.focusLost(focusevent);
//				BigDecimal bigdecimal = validateCost(estText, estBD, currency);
//				if (bigdecimal != null)
//					estBD = bigdecimal;
//			}
//
//		});
//		// actText = SwtWidgetsFactory
//		// .addText(
//		// composite,
//		// (new StringBuilder())
//		// .append(nameCache.getAttrDisplayName(
//		// ClassType.FIXED_COST,
//		// "act_costvalue_form_tag")).append(":")
//		// .toString(), actCost, 100, 131072, flag);
//		// actText.addFocusListener(new FocusAdapter() {
//		//
//		// public void focusLost(FocusEvent focusevent) {
//		// super.focusLost(focusevent);
//		// BigDecimal bigdecimal = validateCost(actText, actBD, currency);
//		// if (bigdecimal != null)
//		// actBD = bigdecimal;
//		// }
//		//
//		// final FixedCostDlgWizPage this$0;
//		//
//		// {
//		// this$0 = FixedCostDlgWizPage.this;
//		//
//		// }
//		// });
//		currencyCombo = SwtWidgetsFactory.addCombo(
//				composite,
//				(new StringBuilder())
//						.append(nameCache.getAttrDisplayName(
//								ClassType.COST_VALUE, "Currency")).append(":")
//						.toString(), controller.currencyList, 0, flag);
//		currencyCombo.select(currencyIdx);
//		currencyCombo.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent selectionevent) {
//				super.widgetSelected(selectionevent);
//				currency = currencyCombo.getText();
//				estText.setText(CurrencyFormatter.format(estBD, currency));
//				// actText.setText(CurrencyFormatter.format(actBD, currency));
//			}
//
//			final FixedCostDlgWizPage this$0;
//
//			{
//				this$0 = FixedCostDlgWizPage.this;
//			}
//		});
//		// chkButton = SwtWidgetsFactory.addCheckBox(composite, nameCache
//		// .getAttrDisplayName(ClassType.FIXED_COST, "use_actual_cost"),
//		// flag);
//		// chkButton.setSelection(useActual);
//
//		// billCodeCombo = SwtWidgetsFactory.addCombo(
//		// composite,
//		// (new StringBuilder())
//		// .append(nameCache.getAttrDisplayName(
//		// ClassType.FIXED_COST, "bill_code")).append(":")
//		// .toString(), controller.lovBillCodeList, 0, flag);
//		// billCodeCombo.addSelectionListener(new SelectionAdapter() {
//		//
//		// public void widgetSelected(SelectionEvent selectionevent) {
//		// int i = billCodeCombo.getSelectionIndex();
//		// setBillCodeComboSelection(i, 0);
//		// }
//		//
//		// final FixedCostDlgWizPage this$0;
//		//
//		// {
//		// this$0 = FixedCostDlgWizPage.this;
//		// }
//		// });
//		StringHolder astringholder[] = new StringHolder[0];
//		if (controller.lovBillSubCodeList.length > billCode
//				&& billCode > -1
//				&& controller.lovBillSubCodeList[controller.billCodeDBIdx] != null)
//			astringholder = controller.lovBillSubCodeList[controller.billCodeDBIdx];
//		// billSubCodeCombo = SwtWidgetsFactory.addCombo(
//		// composite,
//		// (new StringBuilder())
//		// .append(nameCache.getAttrDisplayName(
//		// ClassType.FIXED_COST, "bill_sub_code"))
//		// .append(":").toString(), astringholder, 0, flag);
//		// setBillCodeComboSelection(billCode, billSubCode);
//
//		// 修改billTypeCombo的LOV代码指向自定义的LOV
//		TCSession session = (TCSession) AIFUtility.getCurrentApplication()
//				.getSession();
//
//		TCComponentListOfValues findLOVByName = TCComponentListOfValuesType
//				.findLOVByName(session, "JCI6_NonLabour");
//		controller.billTypeList = ScheduleUtil.getStringHolders(findLOVByName);
////		try {
////			valuesFullNames = findLOVByName.getListOfValues()
////					.getValuesFullNames();
//			billTypeCombo = SwtWidgetsFactory.addCombo(composite,
//					"Bill Type :", controller.billTypeList, 0, flag);
//			billTypeCombo.select(billType);
//
////		} catch (TCException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//		SwtWidgetsFactory.addBlank(composite, 2);
//		// if (ScheduleDeferredContext.inDeferredSession())
//		// SwtWidgetsFactory.addLabel(composite, r.getString("notes.TITLE"),
//		// r.getString("deferred.NOTES"), 200, 16384, flag);
//		composite.pack();
//		setControl(composite);
//	}
//
//	FixedCostDetail getData() {
//		Object obj = null;
//		name = nameText.getText().trim();
//		// accrualType = accrualTypeCombo.getSelectionIndex();
//		accrualType = 0;
//		estCost = estText.getText().trim();
//		// actCost = actText.getText().trim();
//		actCost = "";
//		currency = currencyCombo.getText().trim();
//		// useActual = chkButton.getSelection();
//		// billCode = billCodeCombo.getSelectionIndex();
//		// billSubCode = billSubCodeCombo.getSelectionIndex();
//		billType = billTypeCombo.getSelectionIndex();
//		billCode = -1;
//		billSubCode = -1;
//		if (validateFields())
//			if (addNew) {
//				String s;
//				if (billCode > -1
//						&& controller.lovBillSubCodeList.length > billCode
//						&& billSubCode > -1
//						&& controller.lovBillSubCodeList[billCode] != null
//						&& controller.lovBillSubCodeList[billCode].length > billSubCode)
//					s = controller.lovBillSubCodeList[billCode][billSubCode]
//							.getInternal();
//				else
//					s = "";
//				// String s1 = controller.lovBillCodeList.length <= billCode ?
//				// ""
//				// : controller.lovBillCodeList[billCode].getInternal();
//				String s3 = (controller.billTypeList == null || controller.billTypeList.length <= billType) ? ""
//						: controller.billTypeList[billType].getInternal();
//				String s1 = "";
//				s = "";
//				FixedCostDetailImpl fixedcostdetailimpl = new FixedCostDetailImpl(
//						name, accrualType, estBD, actBD, accruedCost, currency,
//						useActual, s1, s, s3);
//				obj = fixedcostdetailimpl;
//			} else {
//				FixedCostDetail fixedcostdetail = (FixedCostDetail) updateFixCostObj;
//				fixedcostdetail.setName(name);
//				fixedcostdetail.setAccrualType(accrualType);
//				if (fixedcostdetail.getTcComponentState() != 2)
//					fixedcostdetail.setTcComponentState(3);
//				fixedcostdetail.setEstimatedCost(estBD);
//				fixedcostdetail.setActualCost(actBD);
//				fixedcostdetail.setCurrency(currency);
//				fixedcostdetail.setUseActualCost(useActual);
//				fixedcostdetail.setBillingCode(billCode <= -1
//						|| controller.lovBillCodeList.length <= billCode ? ""
//						: controller.lovBillCodeList[billCode].getInternal());
//				String s2 = billCode <= -1
//						|| controller.lovBillSubCodeList.length <= billCode
//						|| billSubCode <= -1
//						|| controller.lovBillSubCodeList[billCode] == null
//						|| controller.lovBillSubCodeList[billCode].length <= billSubCode ? ""
//						: controller.lovBillSubCodeList[billCode][billSubCode]
//								.getInternal();
//				s2 = "";
//				fixedcostdetail.setBillingSubCode(s2);
//				fixedcostdetail.setBillingType(billType <= -1
//						|| controller.billTypeList.length <= billType ? ""
//						: controller.billTypeList[billType].getInternal());
//				obj = fixedcostdetail;
//			}
//		return ((FixedCostDetail) (obj));
//	}
//
//	private BigDecimal validateCost(StyledText styledtext,
//			BigDecimal bigdecimal, String s) {
//		String s1 = styledtext.getText();
//		BigDecimal bigdecimal1 = null;
//		if (s1 != null && s1.trim().length() > 0)
//			try {
//				bigdecimal1 = CurrencyFormatter.parse(s1, s);
//				styledtext.setText(CurrencyFormatter.format(bigdecimal1, s));
//			} catch (ParseException parseexception) {
//				String s2 = r.getString("schedTaskCosts.BADCOST");
//				s2 = MessageFormat.format(s2, new Object[] { s1 });
//				MessageBox.post(getShell(), s2,
//						r.getString("schedTaskCosts.TITLE"), 1);
//				styledtext.setText(CurrencyFormatter.format(bigdecimal, s));
//			}
//		return bigdecimal1;
//	}
//
//	boolean validateFields() {
//		boolean flag;
//		flag = true;
//
//		if (name == null || name.length() == 0) {
//			String s = r.getString("schedTaskCosts.NONAME");
//			s = MessageFormat.format(s, new Object[] { nameCache
//					.getAttrDisplayName(ClassType.FIXED_COST, "cost_name") });
//			MessageBox.post(getShell(), s, r.getString("schedTaskCosts.TITLE"),
//					2);
//			flag = false;
//			return flag;
//		}
//
//		try {
//			if (estCost == null || estCost.length() <= 0) {
//				estBD = CurrencyFormatter.parse(estCost, currency);
//				if (estBD == null) {
//					String s1 = r.getString("schedTaskCosts.NOEST");
//					s1 = MessageFormat.format(s1, new Object[] { nameCache
//							.getAttrDisplayName(ClassType.FIXED_COST,
//									"est_costvalue_form_tag") });
//					MessageBox.post(getShell(), s1,
//							r.getString("schedTaskCosts.TITLE"), 2);
//					flag = false;
//					return flag;
//				}
//				if (estBD.precision() - estBD.scale() > 16) {
//					String s2 = r.getString("costToLarge.MSG");
//					s2 = MessageFormat
//							.format(s2,
//									new Object[] {
//											CurrencyFormatter.format(estBD,
//													currency),
//											CurrencyFormatter.format(
//													CostValueFormatter
//															.getMaxBD(estBD
//																	.scale()),
//													currency) });
//					MessageBox.post(getShell(), s2,
//							r.getString("costToLarge.TITLE"), 1);
//					flag = false;
//					return flag;
//				}
//			}
//			// if (actCost == null || actCost.length() <= 0) {
//			// actBD = CurrencyFormatter.parse(actCost, currency);
//			// if (actBD == null) {
//			// String s3 = r.getString("schedTaskCosts.NOACT");
//			// s3 = MessageFormat.format(s3, new Object[] { nameCache
//			// .getAttrDisplayName(ClassType.FIXED_COST,
//			// "act_costvalue_form_tag") });
//			// MessageBox.post(getShell(), s3,
//			// r.getString("schedTaskCosts.TITLE"), 2);
//			// flag = false;
//			// return flag;
//			// }
//			//
//			// if (actBD.precision() - actBD.scale() > 16) {
//			// String s4 = r.getString("costToLarge.MSG");
//			// s4 = MessageFormat
//			// .format(s4,
//			// new Object[] {
//			// CurrencyFormatter.format(actBD,
//			// currency),
//			// CurrencyFormatter.format(
//			// CostValueFormatter
//			// .getMaxBD(actBD
//			// .scale()),
//			// currency) });
//			// MessageBox.post(getShell(), s4,
//			// r.getString("costToLarge.TITLE"), 1);
//			// flag = false;
//			// return flag;
//			// }
//			// }
//		} catch (ParseException parseexception) {
//			String s5 = r.getString("schedTaskCosts.NOCOST");
//			s5 = MessageFormat.format(s5, new Object[] { nameCache
//					.getAttrDisplayName(ClassType.FIXED_COST,
//							"est_costvalue_form_tag")
//			// ,nameCache.getAttrDisplayName(ClassType.FIXED_COST,
//			// "act_costvalue_form_tag")
//					});
//			MessageBox.post(getShell(), s5,
//					r.getString("schedTaskCosts.TITLE"), 2);
//			flag = false;
//		}
//		return flag;
//	}
//
//	private void setBillCodeComboSelection(int i, int j) {
//		StringHolder astringholder[];
//		if (controller.lovBillSubCodeList.length > i
//				&& controller.lovBillSubCodeList[i] != null)
//			astringholder = controller.lovBillSubCodeList[i];
//		else
//			astringholder = (new StringHolder[] { StringHolder.EMPTY });
//		// billCodeCombo.select(i);
//		// billSubCodeCombo.setItems(StringHolder.getDisplay(astringholder));
//		// billSubCodeCombo.select(j);
//		billCode = i;
//		billSubCode = j;
//	}
//
//}