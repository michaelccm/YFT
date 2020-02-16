/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.commands.open;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.ICommandListener;
import com.teamcenter.rac.aif.InterfaceAIFOperationExecutionListener;
import com.teamcenter.rac.aif.kernel.AIFComponentChangeEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentPropertyChangeEvent;
import com.teamcenter.rac.aif.kernel.IPreferenceService;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commandsuppression.ICommandSuppressionService;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.common.IEditableSwingComp;
import com.teamcenter.rac.common.TCTypeRenderer;
import com.teamcenter.rac.common.implicitco.ImplicitCheckOutOperation;
import com.teamcenter.rac.common.implicitco.ImplicitCheckOutOperation.SubOperations;
import com.teamcenter.rac.common.viewedit.IViewEditOperationListener;
import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.common.viewedit.ViewEditHelper.CKO;
import com.teamcenter.rac.form.AbstractTCForm;
import com.teamcenter.rac.form.AutomaticTCForm;
import com.teamcenter.rac.form.FormLoader;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.stylesheet.AutomaticRendering;
import com.teamcenter.rac.stylesheet.LoadRendererProgressPanel;
import com.teamcenter.rac.stylesheet.PropertyBeanContainer;
import com.teamcenter.rac.stylesheet.RenderingLoader;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.Header;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.viewer.ISubViewer;

import com.teamcenter.rac.workflow.commands.newprocess.NewProcessCommand;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

public class OpenFormPanel extends JPanel implements
		InterfaceAIFComponentEventListener, InterfaceSignalOnClose {
	public static final String REFRESH_DISPLAY_REG_KEY = "refreshDisplayMessage";
	private static final String FORM_DOUBLE_CLICK_PREF = "Form_double_click";
	protected final TCComponentForm form;
	private IEditableSwingComp renderingPanel;
	protected final OpenFormDialog formDialog;
	protected ISubViewer formViewer;
	protected final Registry appReg;
	protected TCSession session;
	private final Map<ViewEditHelper.CKO, List<JButton>> buttonMap;
	protected JButton okButton;
	private JButton applyButton;
	private JButton implicitCoApplyButton;
	private JButton implicitCoOkButton;
	private JButton clearButton;
	private JButton cancelButton;
	private JButton editButton;
	private JButton saveCheckInButton;
	private JButton saveCheckOutButton;
	private JButton cancelCheckOutButton;
	private JButton closeButton;
	private boolean overrideEdit;
	private boolean showButtonPanel;
	private JPanel topPanel;
	private JPanel buttonPanel;
	private Separator buttonPanelSeparator;
	private boolean modal;
	private JLabel modalIcon;
	protected JPanel bottomPanel;
	protected boolean okToModify;
	protected boolean useStyleSheet;
	private LoadRendererProgressPanel progressPanel;
	private ViewEditHelper helper;
	private ICommandListener m_cancelCOCommandListener;
	private ICommandListener m_saveCICommandListener;

	public OpenFormPanel(OpenFormDialog paramOpenFormDialog,
			TCComponentForm paramTCComponentForm) throws Exception {
		super(new VerticalLayout(2, 5, 5, 5, 5));

		System.out
				.println("ww OpenFormPanel(OpenFormDialog paramOpenFormDialog, TCComponentForm paramTCComponentForm)");

		this.formViewer = null;
		this.appReg = Registry.getRegistry(this);
		this.buttonMap = new HashMap();
		this.overrideEdit = false;
		this.showButtonPanel = false;
		this.modal = true;
		this.okToModify = false;
		this.useStyleSheet = true;
		this.form = paramTCComponentForm;
		this.formDialog = paramOpenFormDialog;
		this.formViewer = null;

		// add by wuwei
		Registry localRegistry = Registry.getRegistry("portal");
		this.useStyleSheet = localRegistry.getBoolean("useStyleSheet", false);

		refreshForms();
		this.session = paramTCComponentForm.getSession();
		this.helper = new ViewEditHelper(this.session);
		if (this.useStyleSheet)
			initializeOpenStyleSheetPanel();
		else
			initializeOpenFormPanel();
		this.session.addAIFComponentEventListener(this);
	}

	public OpenFormPanel(ISubViewer paramISubViewer,
			TCComponentForm paramTCComponentForm) throws Exception {
		super(new VerticalLayout(2, 5, 5, 5, 5));
		this.formViewer = null;
		this.appReg = Registry.getRegistry(this);
		this.buttonMap = new HashMap();
		this.overrideEdit = false;
		this.showButtonPanel = false;
		this.modal = true;
		this.okToModify = false;
		this.useStyleSheet = true;
		this.form = paramTCComponentForm;
		this.formDialog = null;
		this.formViewer = paramISubViewer;
		initializeOpenEditPanels();
	}

	public OpenFormPanel(TCComponentForm paramTCComponentForm) throws Exception {
		this((OpenFormDialog) null, paramTCComponentForm);
	}

	public OpenFormPanel(TCComponentForm paramTCComponentForm,
			boolean paramBoolean) throws Exception {
		this((OpenFormDialog) null, paramTCComponentForm);
		this.overrideEdit = paramBoolean;
	}

	public OpenFormPanel(OpenFormDialog paramOpenFormDialog,
			TCComponentForm paramTCComponentForm, boolean paramBoolean)
			throws Exception {
		super(new VerticalLayout(2, 5, 5, 5, 5));
		this.formViewer = null;
		this.appReg = Registry.getRegistry(this);
		this.buttonMap = new HashMap();
		this.overrideEdit = false;
		this.showButtonPanel = false;
		this.modal = true;
		this.okToModify = false;
		this.useStyleSheet = true;
		this.form = paramTCComponentForm;
		this.formDialog = paramOpenFormDialog;
		this.formViewer = null;
		initializeOpenEditPanels();
	}

	private void initializeOpenEditPanels() throws Exception {
		System.out.println("initializeOpenEditPanels");
		Registry localRegistry = Registry.getRegistry("portal");
		this.useStyleSheet = localRegistry.getBoolean("useStyleSheet", true);

		this.session = this.form.getSession();
		this.helper = new ViewEditHelper(this.session);
		refreshForms();
		if (this.useStyleSheet) {

			System.out.println("this.helper.isCheckoutable(this.form)-->"
					+ this.helper.isCheckoutable(this.form));
			String formType = form.getType();
			System.out.println("lala formType-->" + formType);
			if (formType.equals("JCI6_ExtSupPlan")
					|| formType.equals("JCI6_ExtWDHrForm")) {
				initializeOpenStyleSheetPanel();
			} else if (formType.equals("JCI6_Ext2Supp")) {
				if (this.helper.isCheckoutable(this.form))
					initializeJCIOpenStyleSheetPanel();
				else
					initializeJCIOpenStyleSheetPanel_no_checkouable();
			} else {
				if (this.helper.isCheckoutable(this.form))
					initializeOpenEditStyleSheetPanel();
				else
					initializeOpenStyleSheetPanel();
			}

			// if (this.helper.isCheckoutable(this.form))
			// initializeOpenEditStyleSheetPanel();
			// else
			// initializeOpenStyleSheetPanel();
		} else
			initializeOpenFormPanel();
		this.session.addAIFComponentEventListener(this);
	}

	private void initializeJCIOpenStyleSheetPanel() throws Exception {
		System.out
				.println("**************initializeJCIOpenStyleSheetPanel begin*********");
		setBackground(Color.white);
		this.modalIcon = new JLabel(this.appReg.getImageIcon("modeless.ICON"));
		this.modalIcon
				.setToolTipText(this.appReg.getString("modeless.TOOLTIP"));
		setModal(true);
		this.showButtonPanel = true;
		Header localHeader = new Header(
				TCTypeRenderer.getIcon(this.form, false),
				this.form.getDisplayType());
		this.topPanel = new JPanel(new HorizontalLayout(2, 0, 0, 0, 0));
		this.topPanel.setOpaque(false);
		this.topPanel.add("unbound.bind", localHeader);
		this.topPanel.add("right", this.modalIcon);
		add("top", this.topPanel);
		this.progressPanel = new LoadRendererProgressPanel(500L);
		add("unbound.bind.left.top", this.progressPanel);
		this.buttonPanel = new JPanel(new ButtonLayout());
		this.buttonPanel.setOpaque(false);
		this.buttonPanel.setVisible(false);
		this.buttonPanelSeparator = new Separator();
		this.buttonPanelSeparator.setVisible(false);
		TCSession localTCSession = this.form.getSession();
		localTCSession.queueOperation(new RenderPanelOperation(this.appReg
				.getString("loadForm.MESSAGE")));
		if (this.formDialog != null) {
			this.okButton = new JButton(this.appReg.getString("ok"));
			this.okButton.setText("Save draft");
			this.okButton.setMnemonic(this.appReg.getString("ok.MNEMONIC")
					.charAt(0));
			this.okButton.setVisible(true);
			this.okButton.addActionListener(new ActionListener() {
				public void actionPerformed(
						ActionEvent paramAnonymousActionEvent) {
					System.out.println("-----okbutton2---");
					if (OpenFormPanel.this.isPanelSavable()) {
						TCSession localTCSession = OpenFormPanel.this.form
								.getSession();
						localTCSession
								.queueOperation(new OpenFormPanel.SaveFormOperation(
										localTCSession, false));
					}
				}
			});
			this.buttonPanel.add(this.okButton);
			addButtons(ViewEditHelper.CKO.CHECKED_IN, this.okButton);
			addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE,
					this.okButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
					this.okButton);
		}
		String stage = this.form.getProperty("process_stage");
		if ((stage == null) || (stage.length() <= 1)) {
			addApplyButton1();
			this.applyButton.setText("Submit");
		}
		if (this.formDialog == null)
			addClearButton();
		add("bottom.nobind.right.top", this.buttonPanel);
		add("bottom.bind", this.buttonPanelSeparator);
		JButton localJButton = new JButton(
				this.appReg.getImageIcon("viewer.PRINTICON"));
		localJButton.setOpaque(false);
		localJButton.setMargin(new Insets(0, 0, 0, 0));
		localJButton.setToolTipText(this.appReg.getString("viewer.PRINTTIP"));
		localJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
				InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = { OpenFormPanel.this.form };
				Registry localRegistry = Registry
						.getRegistry("com.teamcenter.rac.common.actions.actions");
				AbstractAIFCommand localAbstractAIFCommand = null;
				if ((OpenFormPanel.this.formDialog == null)
						|| (OpenFormPanel.this.formDialog.parent == null))
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] {
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				else
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] { Utilities.getCurrentFrame(),
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				if (localAbstractAIFCommand != null)
					localAbstractAIFCommand.executeModeless();
			}
		});
		this.bottomPanel = new JPanel(new HorizontalLayout(2, 0, 0, 0, 0));
		this.bottomPanel.setOpaque(false);
		this.bottomPanel.add("right", localJButton);
		add("bottom.bind.right.top", this.bottomPanel);
		setFocus();
	}

	private void initializeJCIOpenStyleSheetPanel_no_checkouable()
			throws Exception {
		System.out
				.println("**************initializeJCIOpenStyleSheetPanel_no_checkouable begin*********");
		setBackground(Color.white);
		this.modalIcon = new JLabel(this.appReg.getImageIcon("modeless.ICON"));
		this.modalIcon
				.setToolTipText(this.appReg.getString("modeless.TOOLTIP"));
		setModal(true);
		this.showButtonPanel = true;
		Header localHeader = new Header(
				TCTypeRenderer.getIcon(this.form, false),
				this.form.getDisplayType());
		this.topPanel = new JPanel(new HorizontalLayout(2, 0, 0, 0, 0));
		this.topPanel.setOpaque(false);
		this.topPanel.add("unbound.bind", localHeader);
		this.topPanel.add("right", this.modalIcon);
		add("top", this.topPanel);
		this.progressPanel = new LoadRendererProgressPanel(500L);
		add("unbound.bind.left.top", this.progressPanel);
		this.buttonPanel = new JPanel(new ButtonLayout());
		this.buttonPanel.setOpaque(false);
		this.buttonPanel.setVisible(false);
		this.buttonPanelSeparator = new Separator();
		this.buttonPanelSeparator.setVisible(false);
		TCSession localTCSession = this.form.getSession();
		localTCSession.queueOperation(new RenderPanelOperation(this.appReg
				.getString("loadForm.MESSAGE")));
		if (this.formDialog != null) {
			this.okButton = new JButton(this.appReg.getString("ok"));
			this.okButton.setText("Save draft");
			this.okButton.setMnemonic(this.appReg.getString("ok.MNEMONIC")
					.charAt(0));
			this.okButton.setVisible(false);
			this.okButton.addActionListener(new ActionListener() {
				public void actionPerformed(
						ActionEvent paramAnonymousActionEvent) {
					System.out.println("-----okbutton2---");
					if (OpenFormPanel.this.isPanelSavable()) {
						TCSession localTCSession = OpenFormPanel.this.form
								.getSession();
						localTCSession
								.queueOperation(new OpenFormPanel.SaveFormOperation(
										localTCSession, false));
					}
				}
			});
			this.buttonPanel.add(this.okButton);
			addButtons(ViewEditHelper.CKO.CHECKED_IN, this.okButton);
			addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE,
					this.okButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
					this.okButton);
		}

		String stage = this.form.getProperty("process_stage");
		if ((stage == null) || (stage.length() <= 1)) {
			addApplyButton2();
			this.applyButton.setText("Submit");
		}

		if (this.formDialog == null)
			addClearButton();
		add("bottom.nobind.right.top", this.buttonPanel);
		add("bottom.bind", this.buttonPanelSeparator);
		JButton localJButton = new JButton(
				this.appReg.getImageIcon("viewer.PRINTICON"));
		localJButton.setOpaque(false);
		localJButton.setMargin(new Insets(0, 0, 0, 0));
		localJButton.setToolTipText(this.appReg.getString("viewer.PRINTTIP"));
		localJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
				InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = { OpenFormPanel.this.form };
				Registry localRegistry = Registry
						.getRegistry("com.teamcenter.rac.common.actions.actions");
				AbstractAIFCommand localAbstractAIFCommand = null;
				if ((OpenFormPanel.this.formDialog == null)
						|| (OpenFormPanel.this.formDialog.parent == null))
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] {
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				else
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] { Utilities.getCurrentFrame(),
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				if (localAbstractAIFCommand != null)
					localAbstractAIFCommand.executeModeless();
			}
		});
		this.bottomPanel = new JPanel(new HorizontalLayout(2, 0, 0, 0, 0));
		this.bottomPanel.setOpaque(false);
		this.bottomPanel.add("right", localJButton);
		add("bottom.bind.right.top", this.bottomPanel);
		setFocus();
	}

	private void refreshForms() throws TCException {
		boolean bool1 = this.appReg.getBoolean("refreshFormBeforeOpen", false);
		String[] arrayOfString = this.appReg.getStringArray("refreshFormTypes",
				null, null);
		if (!(bool1))
			return;
		if ((arrayOfString == null) || (arrayOfString.length == 0)) {
			this.form.refresh();
		} else {
			boolean bool2 = Utilities.contains(this.form.getType(),
					arrayOfString);
			if (!(bool2))
				return;
			this.form.refresh();
		}
	}

	public AbstractTCForm getOldFormPanel() {
		AbstractTCForm localAbstractTCForm = null;
		if (this.renderingPanel instanceof AbstractTCForm)
			localAbstractTCForm = (AbstractTCForm) this.renderingPanel;
		return localAbstractTCForm;
	}

	public AbstractTCForm getFormPanel() {
		AbstractTCForm localAbstractTCForm = null;
		if (this.renderingPanel instanceof AbstractTCForm)
			localAbstractTCForm = (AbstractTCForm) this.renderingPanel;
		return localAbstractTCForm;
	}

	public AbstractRendering getStyleSheetPanel() {
		AbstractRendering localAbstractRendering = null;
		if (this.renderingPanel instanceof AbstractRendering)
			localAbstractRendering = (AbstractRendering) this.renderingPanel;
		return localAbstractRendering;
	}

	public void setModal(boolean paramBoolean) {
		this.modal = paramBoolean;
		this.modalIcon.setVisible(!(paramBoolean));
	}

	public boolean isModal() {
		return this.modal;
	}

	private void addClearButton() {
		this.clearButton = new JButton(this.appReg.getString("clear"));
		this.clearButton.setMnemonic(this.appReg.getString("clear.MNEMONIC")
				.charAt(0));
		this.clearButton.setVisible(false);
		this.clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				try {
					if (OpenFormPanel.this.form == null)
						return;
					OpenFormPanel.this.form.refresh();
				} catch (Exception localException) {
					OpenFormPanel.this.handleException(localException);
				}
			}
		});
		this.buttonPanel.add(this.clearButton);
		addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE, this.clearButton);
		addButtons(
				ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
				this.clearButton);
		addButtons(
				ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE,
				this.clearButton);
		addButtons(ViewEditHelper.CKO.NOT_CHECKOUTABLE, this.clearButton);
	}

	private void initializeOpenFormPanel() throws Exception {
		System.out
				.println("**************initializeOpenFormPanel begin*********");
		try {
			this.renderingPanel = FormLoader.load(this.form);
		} catch (InvocationTargetException localInvocationTargetException) {
			if ((this.formDialog == null) || (this.formDialog.parent == null)) {
				MessageBox.post(localInvocationTargetException
						.getTargetException().toString(), this.appReg
						.getString("openError.TITLE"), 1);
			} else {
				this.formDialog.setShowDialogFlag(false);
				MessageBox.post(this.formDialog.parent,
						localInvocationTargetException.getTargetException()
								.toString(), this.appReg
								.getString("openError.TITLE"), 1);
			}
			throw localInvocationTargetException;
		} catch (Exception localException) {
			handleException(localException);
			throw localException;
		}
		this.renderingPanel.addToPanel(this);
		initOkToModify();
		this.buttonPanel = new JPanel(new ButtonLayout());
		if (this.formDialog != null) {
			this.showButtonPanel = true;
			this.okButton = new JButton(this.appReg.getString("ok"));
			this.okButton.setMnemonic(this.appReg.getString("ok.MNEMONIC")
					.charAt(0));
			this.okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent paramActionEvent) {
					System.out.println("-----okbutton1---");
					if (!(OpenFormPanel.this.renderingPanel.isSavable(true)))
						return;
					TCSession localTCSession = OpenFormPanel.this.form
							.getSession();
					localTCSession
							.queueOperation(new OpenFormPanel.SaveFormOperation(
									true));

				}
			});
			this.buttonPanel.add(this.okButton);
			addButtons(ViewEditHelper.CKO.CHECKED_IN, this.okButton);
			addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE,
					this.okButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
					this.okButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE,
					this.okButton);
		}
		addApplyButton();
		if (this.formDialog == null)
			addClearButton();
		processButtons();
		processPanels();
		JLabel localJLabel = new JLabel(this.appReg.getImageIcon("open.ICON"));
		this.modalIcon = new JLabel(this.appReg.getImageIcon("modeless.ICON"));
		this.modalIcon
				.setToolTipText(this.appReg.getString("modeless.TOOLTIP"));
		setModal(true);
		JPanel localJPanel = new JPanel(new HorizontalLayout(2, 0, 0, 0, 0));
		localJPanel.add("left", localJLabel);
		localJPanel.add("right", this.modalIcon);
		add("top", localJPanel);
		add("top.bind", new Separator());
		add("bottom.nobind.right.top", this.buttonPanel);
		this.buttonPanelSeparator = new Separator();
		add("bottom.bind", this.buttonPanelSeparator);
		if (!(this.showButtonPanel)) {
			this.buttonPanel.setVisible(false);
			this.buttonPanelSeparator.setVisible(false);
		}
		JButton localJButton = new JButton(
				this.appReg.getImageIcon("viewer.PRINTICON"));
		localJButton.setMargin(new Insets(0, 0, 0, 0));
		localJButton.setToolTipText(this.appReg.getString("viewer.PRINTTIP"));
		localJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = { OpenFormPanel.this.form };
				Registry localRegistry = Registry
						.getRegistry("com.teamcenter.rac.common.actions.actions");
				AbstractAIFCommand localAbstractAIFCommand = null;
				if ((OpenFormPanel.this.formDialog == null)
						|| (OpenFormPanel.this.formDialog.parent == null))
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] {
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				else
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] { Utilities.getCurrentFrame(),
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				if (localAbstractAIFCommand == null)
					return;
				localAbstractAIFCommand.executeModeless();
			}
		});
		this.bottomPanel = new JPanel(new HorizontalLayout(10, 0, 0, 0, 0));
		this.bottomPanel.add("right.nobind.left.bottom", localJButton);
		add("bottom.bind.right.top", this.bottomPanel);
		setFocus();
	}

	protected ActionListener getCancelButtonActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				boolean bool = OpenFormPanel.this.checkForSave(false);
				if (!(bool))
					return;
				OpenFormPanel.this.formDialog.disposeDialog();
			}
		};
	}

	// modify by wuwei
	protected void addApplyButton2() {
		this.applyButton = new JButton(this.appReg.getString("apply"));
		this.applyButton.setMnemonic(this.appReg.getString("apply.MNEMONIC")
				.charAt(0));
		if (this.formDialog == null) {
			this.applyButton.setText(this.appReg.getString("save"));
		}
		this.applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
				System.out.println("addApplyButton2");
				if (OpenFormPanel.this.isPanelSavable()) {
					TCSession localTCSession = OpenFormPanel.this.form
							.getSession();

					localTCSession
							.queueOperation(new OpenFormPanel.SaveAndSubmitFormOperation(
									localTCSession, false));
				}
			}
		});
		this.buttonPanel.add(this.applyButton);
	}

	protected void addApplyButton1() {
		this.applyButton = new JButton(this.appReg.getString("apply"));
		this.applyButton.setMnemonic(this.appReg.getString("apply.MNEMONIC")
				.charAt(0));
		if (this.formDialog == null) {
			this.applyButton.setText(this.appReg.getString("save"));
		}
		this.applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
				System.out.println("applyButton1");
				if (OpenFormPanel.this.isPanelSavable()) {
					TCSession localTCSession = OpenFormPanel.this.form
							.getSession();
					try {
						TCComponent[] extComs1 = OpenFormPanel.this.form
								.getReferenceListProperty("jci6_Ext2Detail");
						if (extComs1 != null)
							System.out.println("TEST11:is the form empty?"
									+ extComs1.length);
					} catch (TCException e2) {
						e2.printStackTrace();
					}

					localTCSession
							.queueOperation(new OpenFormPanel.SaveAndSubmitFormOperation(
									localTCSession, false));
				}
			}
		});
		this.buttonPanel.add(this.applyButton);
	}

	// modify by wuwei

	private void initializeOpenStyleSheetPanel() throws Exception {
		setBackground(UIManager.getColor("TextField.background"));
		this.modalIcon = new JLabel(this.appReg.getImageIcon("modeless.ICON"));
		this.modalIcon
				.setToolTipText(this.appReg.getString("modeless.TOOLTIP"));
		setModal(true);
		this.showButtonPanel = true;
		Header localHeader = new Header(
				TCTypeRenderer.getIcon(this.form, false),
				this.form.getDisplayType());
		this.topPanel = new JPanel(new HorizontalLayout(2, 0, 0, 0, 0));
		this.topPanel.setOpaque(false);
		this.topPanel.add("unbound.bind", localHeader);
		this.topPanel.add("right", this.modalIcon);
		add("top", this.topPanel);
		this.progressPanel = new LoadRendererProgressPanel(500L);
		add("unbound.bind.left.top", this.progressPanel);
		this.buttonPanel = new JPanel(new ButtonLayout());
		this.buttonPanel.setOpaque(false);
		this.buttonPanel.setVisible(false);
		this.buttonPanelSeparator = new Separator();
		this.buttonPanelSeparator.setVisible(false);
		TCSession localTCSession = this.form.getSession();
		localTCSession.queueOperation(new RenderPanelOperation(""));
		if (this.formDialog != null) {
			this.okButton = new JButton(this.appReg.getString("ok"));
			this.okButton.setMnemonic(this.appReg.getString("ok.MNEMONIC")
					.charAt(0));
			this.okButton.setVisible(false);
			this.okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent paramActionEvent) {

					System.out.println("-----okbutton2---");
					if (!(OpenFormPanel.this.isPanelSavable()))
						return;
					TCSession localTCSession = OpenFormPanel.this.form
							.getSession();
					localTCSession
							.queueOperation(new OpenFormPanel.SaveFormOperation(
									true));
				}
			});
			this.buttonPanel.add(this.okButton);
			addButtons(ViewEditHelper.CKO.CHECKED_IN, this.okButton);
			addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE,
					this.okButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
					this.okButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE,
					this.okButton);
		}

		addApplyButton();

		if (this.formDialog == null)
			addClearButton();
		add("bottom.nobind.right.top", this.buttonPanel);
		add("bottom.bind", this.buttonPanelSeparator);
		JButton localJButton = new JButton(
				this.appReg.getImageIcon("viewer.PRINTICON"));
		localJButton.setOpaque(false);
		localJButton.setMargin(new Insets(0, 0, 0, 0));
		localJButton.setToolTipText(this.appReg.getString("viewer.PRINTTIP"));
		localJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = { OpenFormPanel.this.form };
				Registry localRegistry = Registry
						.getRegistry("com.teamcenter.rac.common.actions.actions");
				AbstractAIFCommand localAbstractAIFCommand = null;
				if ((OpenFormPanel.this.formDialog == null)
						|| (OpenFormPanel.this.formDialog.parent == null))
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] {
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				else
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] { Utilities.getCurrentFrame(),
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				if (localAbstractAIFCommand == null)
					return;
				localAbstractAIFCommand.executeModeless();
			}
		});
		this.bottomPanel = new JPanel(new HorizontalLayout(10, 0, 0, 0, 0));
		this.bottomPanel.setOpaque(false);
		this.bottomPanel.add("right.nobind.left.bottom", localJButton);
		add("bottom.bind.right.top", this.bottomPanel);
		setFocus();
	}

	protected void addApplyButton() {
		this.applyButton = new JButton(this.appReg.getString("apply"));
		this.applyButton.setMnemonic(this.appReg.getString("apply.MNEMONIC")
				.charAt(0));
		if (this.formDialog == null)
			this.applyButton.setText(this.appReg.getString("save"));
		this.applyButton.setVisible(false);
		this.applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (!(OpenFormPanel.this.isPanelSavable()))
					return;
				TCSession localTCSession = OpenFormPanel.this.form.getSession();
				localTCSession
						.queueOperation(new OpenFormPanel.SaveFormOperation(
								false));
			}
		});
		this.buttonPanel.add(this.applyButton);
		addButtons(ViewEditHelper.CKO.CHECKED_IN, this.applyButton);
		addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE, this.applyButton);
		addButtons(
				ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
				this.applyButton);
		addButtons(
				ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE,
				this.applyButton);
	}

	private boolean addButtons(ViewEditHelper.CKO paramCKO, JButton paramJButton) {
		boolean bool = false;
		Object localObject = (List) this.buttonMap.get(paramCKO);
		if (localObject == null) {
			localObject = new ArrayList();
			this.buttonMap.put(paramCKO, (List<JButton>) localObject);
		}
		bool = ((List) localObject).add(paramJButton);
		return bool;
	}

	public void addCloseButton() {
		this.closeButton = new JButton(this.appReg.getString("close"));
		this.closeButton.setMnemonic(this.appReg.getString("close.MNEMONIC")
				.charAt(0));
		this.closeButton.setVisible(true);
		this.closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				try {
					OpenFormPanel.this.checkForSave(true);
				} catch (Exception localException) {
					OpenFormPanel.this.handleException(localException);
				}
			}
		});
		this.buttonPanel.add(this.closeButton);
	}

	private void initializeOpenEditStyleSheetPanel() throws Exception {
		ICommandSuppressionService localICommandSuppressionService = (ICommandSuppressionService) OSGIUtil
				.getService(Activator.getDefault(),
						ICommandSuppressionService.class);
		setBackground(UIManager.getColor("TextField.background"));
		this.modalIcon = new JLabel(this.appReg.getImageIcon("modeless.ICON"));
		this.modalIcon
				.setToolTipText(this.appReg.getString("modeless.TOOLTIP"));
		setModal(true);
		Header localHeader = new Header(
				TCTypeRenderer.getIcon(this.form, false),
				this.form.getDisplayType());
		this.topPanel = new JPanel(new HorizontalLayout(2, 0, 0, 0, 0));
		this.topPanel.setOpaque(false);
		this.topPanel.add("unbound.bind", localHeader);
		this.topPanel.add("right", this.modalIcon);
		add("top", this.topPanel);
		this.progressPanel = new LoadRendererProgressPanel(500L);
		add("unbound.bind.left.top", this.progressPanel);
		this.buttonPanel = new JPanel(new ButtonLayout());
		this.buttonPanel.setOpaque(false);
		this.buttonPanel.setVisible(false);
		this.buttonPanelSeparator = new Separator();
		this.buttonPanelSeparator.setVisible(false);
		this.editButton = new JButton(this.appReg.getString("edit"));
		this.editButton.setMnemonic(this.appReg.getString("edit.MNEMONIC")
				.charAt(0));
		this.editButton.setToolTipText(this.appReg.getString("edit.TIP"));
		this.editButton.setVisible(false);
		this.editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (OpenFormPanel.this.formDialog != null)
					OpenFormPanel.this.formDialog
							.processEditButtonAction(OpenFormPanel.this.helper);
				else
					OpenFormPanel.this
							.processEditButtonAction(OpenFormPanel.this.helper);
			}
		});
		if (!(localICommandSuppressionService
				.isCommandSuppressed("com.teamcenter.rac.checkOut"))) {
			this.buttonPanel.add(this.editButton);
			addButtons(ViewEditHelper.CKO.CHECKED_IN, this.editButton);
			addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE,
					this.editButton);
			addButtons(ViewEditHelper.CKO.NOT_CHECKOUTABLE, this.editButton);
		}
		if (this.formDialog != null) {
			this.implicitCoOkButton = new JButton(
					this.appReg.getString("implicitCoOk"));
			this.implicitCoOkButton.setMnemonic(this.appReg.getString(
					"implicitCoOk.MNEMONIC").charAt(0));
			this.implicitCoOkButton.setToolTipText(this.appReg
					.getString("implicitCoOk.TIP"));
			this.implicitCoOkButton.setVisible(false);
			this.implicitCoOkButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent paramActionEvent) {
					if (OpenFormPanel.this.formDialog != null)
						OpenFormPanel.this.formDialog
								.processImplicitCoOkButtonAction();
					else
						OpenFormPanel.this.processImplicitCoOkButtonAction();
				}
			});
			this.buttonPanel.add(this.implicitCoOkButton);
			addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE,
					this.implicitCoOkButton);
		}
		this.implicitCoApplyButton = new JButton(
				this.appReg.getString("implicitCoApply"));
		this.implicitCoApplyButton.setMnemonic(this.appReg.getString(
				"implicitCoApply.MNEMONIC").charAt(0));
		this.implicitCoApplyButton.setToolTipText(this.appReg
				.getString("implicitCoApply.TIP"));
		this.implicitCoApplyButton.setVisible(false);
		this.implicitCoApplyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (OpenFormPanel.this.formDialog != null)
					OpenFormPanel.this.formDialog
							.processImplicitCoApplyButtonAction();
				else
					OpenFormPanel.this.processImplicitCoApplyButtonAction();
			}
		});
		this.buttonPanel.add(this.implicitCoApplyButton);
		addButtons(ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE,
				this.implicitCoApplyButton);
		this.saveCheckInButton = new JButton(
				this.appReg.getString("saveCheckIn"));
		this.saveCheckInButton.setMnemonic(this.appReg.getString(
				"saveCheckIn.MNEMONIC").charAt(0));
		this.saveCheckInButton.setToolTipText(this.appReg
				.getString("saveCheckIn.TIP"));
		this.saveCheckInButton.setVisible(false);
		this.saveCheckInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (!(OpenFormPanel.this.isPanelSavable()))
					return;
				boolean bool = true;
				if (OpenFormPanel.this.formViewer != null)
					bool = false;
				OpenFormPanel.SaveFormOperation localSaveFormOperation = new OpenFormPanel.SaveFormOperation(
						bool);
				localSaveFormOperation.executeOperation();
				if (OpenFormPanel.this.renderingPanel.getErrorFlag())
					return;
				try {
					if (OpenFormPanel.this.helper.isSWTPerspective()) {
						TCSession localTCSession = OpenFormPanel.this.form
								.getSession();
						OpenFormPanel.this.helper.unReserveSWTOperation(
								OpenFormPanel.this.form, localTCSession, null);
					} else {
						OpenFormPanel.this.helper.checkInOperation(
								OpenFormPanel.this.form,
								OpenFormPanel.this.m_saveCICommandListener);
					}
					if (OpenFormPanel.this.formDialog == null)
						return;
					OpenFormPanel.this.formDialog.disposeDialog();
				} catch (TCException localTCException) {
					OpenFormPanel.this.handleException(localTCException);
				}
			}
		});
		if (!(localICommandSuppressionService
				.isCommandSuppressed("com.teamcenter.rac.checkIn"))) {
			this.buttonPanel.add(this.saveCheckInButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
					this.saveCheckInButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE,
					this.saveCheckInButton);
		}
		this.saveCheckOutButton = new JButton(
				this.appReg.getString("saveCheckOut"));
		this.saveCheckOutButton.setMnemonic(this.appReg.getString(
				"saveCheckOut.MNEMONIC").charAt(0));
		this.saveCheckOutButton.setToolTipText(this.appReg
				.getString("saveCheckOut.TIP"));
		this.saveCheckOutButton.setVisible(false);
		this.saveCheckOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (!(OpenFormPanel.this.isPanelSavable()))
					return;
				TCSession localTCSession = OpenFormPanel.this.form.getSession();
				localTCSession
						.queueOperation(new OpenFormPanel.SaveFormOperation(
								false));
			}
		});
		if (!(localICommandSuppressionService
				.isCommandSuppressed("com.teamcenter.rac.saveCheckOut"))) {
			this.buttonPanel.add(this.saveCheckOutButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
					this.saveCheckOutButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE,
					this.saveCheckOutButton);
		}
		this.cancelCheckOutButton = new JButton(
				this.appReg.getString("cancelCheckOut"));
		this.cancelCheckOutButton.setMnemonic(this.appReg.getString(
				"cancelCheckOut.MNEMONIC").charAt(0));
		this.cancelCheckOutButton.setToolTipText(this.appReg
				.getString("cancelCheckOut.TIP"));
		this.cancelCheckOutButton.setVisible(false);
		this.cancelCheckOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				try {
					if (OpenFormPanel.this.helper.isSWTPerspective())
						OpenFormPanel.this.helper.cancelCheckoutSWTOperation(
								OpenFormPanel.this.form,
								OpenFormPanel.this.form.getSession(), null);
					else
						OpenFormPanel.this.helper.cancelCheckOutOperation(
								OpenFormPanel.this.form,
								OpenFormPanel.this.m_cancelCOCommandListener);
					if (OpenFormPanel.this.formDialog == null)
						return;
					OpenFormPanel.this.formDialog.disposeDialog();
				} catch (Exception localException) {
					OpenFormPanel.this.handleException(localException);
				}
			}
		});
		if (!(localICommandSuppressionService
				.isCommandSuppressed("com.teamcenter.rac.cancelCheckOut"))) {
			this.buttonPanel.add(this.cancelCheckOutButton);
			addButtons(
					ViewEditHelper.CKO.CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE,
					this.cancelCheckOutButton);
		}
		if (this.formViewer != null)
			addClearButton();
		add("bottom.nobind.right.top", this.buttonPanel);
		add("bottom.bind", this.buttonPanelSeparator);
		JButton localJButton = new JButton(
				this.appReg.getImageIcon("viewer.PRINTICON"));
		localJButton.setOpaque(false);
		localJButton.setMargin(new Insets(0, 0, 0, 0));
		localJButton.setToolTipText(this.appReg.getString("viewer.PRINTTIP"));
		localJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = { OpenFormPanel.this.form };
				Registry localRegistry = Registry
						.getRegistry("com.teamcenter.rac.common.actions.actions");
				AbstractAIFCommand localAbstractAIFCommand = null;
				if ((OpenFormPanel.this.formDialog == null)
						|| (OpenFormPanel.this.formDialog.parent == null))
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] {
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				else
					localAbstractAIFCommand = (AbstractAIFCommand) localRegistry
							.newInstanceFor("printCommand",
									new Object[] { Utilities.getCurrentFrame(),
											arrayOfInterfaceAIFComponent,
											Boolean.TRUE });
				if (localAbstractAIFCommand == null)
					return;
				localAbstractAIFCommand.executeModeless();
			}
		});
		TCSession localTCSession = this.form.getSession();
		localTCSession.queueOperation(new RenderPanelOperation(this.appReg
				.getString("loadForm.MESSAGE")));
		this.bottomPanel = new JPanel(new HorizontalLayout(10, 0, 0, 0, 0));
		this.bottomPanel.setOpaque(false);
		this.bottomPanel.add("right.nobind.left.bottom", localJButton);
		add("bottom.bind.right.top", this.bottomPanel);
		setFocus();
	}

	protected void setButtonVisibility(ViewEditHelper.CKO paramCKO,
			boolean paramBoolean) {
		List localList = (List) this.buttonMap.get(paramCKO);
		if (localList != null) {
			Iterator localIterator = localList.iterator();
			while (localIterator.hasNext()) {
				JButton localJButton = (JButton) localIterator.next();
				localJButton.setVisible(paramBoolean);
				localJButton.setEnabled(paramBoolean);
			}
		}
		if ((!(paramBoolean))
				|| (!(ViewEditHelper.CKO.NOT_CHECKOUTABLE.equals(paramCKO)))
				|| (this.helper.isCheckoutable(this.form))
				|| (this.editButton == null))
			return;
		this.editButton.setVisible(false);
		this.editButton.setEnabled(false);
	}

	protected void processButtons() {
		System.out.println("processButtons");
		ViewEditHelper.CKO localCKO1 = getObjectState();
		if (localCKO1 != ViewEditHelper.CKO.NOT_CHECKOUTABLE)
			if (!(isPanelLoaded()))
				localCKO1 = ViewEditHelper.CKO.NOT_CHECKOUTABLE;
			else
				this.showButtonPanel = true;
		if ((isFormReadOnly())
				&& (ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE
						.equals(localCKO1)))
			localCKO1 = ViewEditHelper.CKO.NOT_CHECKOUTABLE;
		Set localSet = this.buttonMap.keySet();
		Iterator localIterator = localSet.iterator();
		while (localIterator.hasNext()) {
			ViewEditHelper.CKO localCKO2 = (ViewEditHelper.CKO) localIterator
					.next();
			if (localCKO2 == localCKO1)
				continue;
			setButtonVisibility(localCKO2, false);
		}
		setButtonVisibility(localCKO1, true);

		// add by wuwei
		String ftype = this.form.getType();
		System.out.println("form类型---->" + ftype);
		if (ftype.equals("JCI6_Ext2Supp")) {
			if (this.okButton != null) {
				this.okButton.setEnabled(true);
				this.okButton.setVisible(true);
			}
		}
		if ((ftype.equals("JCI6_ExtSupPlan"))
				|| (ftype.equals("JCI6_ExtWDHrForm"))) {
			System.out.println("okButton-->" + this.okButton
					+ "--->applyButton-->" + this.applyButton);
			if (this.okButton != null)
				this.okButton.setVisible(false);
			if (this.applyButton != null)
				this.applyButton.setVisible(false);
		}
	}

	protected void processPanels() {
		ViewEditHelper.CKO localCKO = getObjectState();
		if (localCKO != ViewEditHelper.CKO.NOT_CHECKOUTABLE) {
			if (!(isPanelLoaded()))
				localCKO = ViewEditHelper.CKO.NOT_CHECKOUTABLE;
			if ((isFormReadOnly())
					&& (ViewEditHelper.CKO.IMPLICITLY_CHECKOUTABLE
							.equals(localCKO)))
				localCKO = ViewEditHelper.CKO.NOT_CHECKOUTABLE;
		}

		switch (localCKO.ordinal()) {
		case 2:
		case 3:
		case 4:
			if (this.overrideEdit)
				return;
			setRenderingPanelEditable(true);
			break;
		case 5:
			if (this.useStyleSheet) {
				setRenderingPanelEditable(false);
				return;
			}
			setRenderingPanelEditable(true);
			break;
		case 1:
			this.renderingPanel.setEditability(false);
		}

		// switch (localCKO.ordinal())
		// {
		// case 1:
		// case 2:
		// if (!this.overrideEdit)
		// setRenderingPanelEditable(true);
		// break;
		// case 3:
		// if (this.useStyleSheet)
		// setRenderingPanelEditable(false);
		// else
		// setRenderingPanelEditable(true);
		// break;
		// case 4:
		// this.renderingPanel.setEditability(false);
		// }

	}

	protected void setRenderingPanelEditable(boolean paramBoolean) {
		this.renderingPanel.setEditability(paramBoolean);
	}

	protected boolean initOkToModify() {
		boolean bool = this.renderingPanel.getErrorFlag();
		this.okToModify = (!(bool));
		return this.okToModify;
	}

	protected boolean isPanelLoaded() {
		boolean bool = this.renderingPanel.getErrorFlag();
		okToModify = !bool;
		return okToModify;
	}

	protected void handleException(Exception paramException) {
		TCException localTCException = new TCException(paramException);
		if ((this.formDialog == null) || (this.formDialog.parent == null)) {
			MessageBox.post(this.appReg.getString("unableToOpen.MESSAGE"),
					localTCException, this.appReg.getString("openError.TITLE"),
					4);
		} else {
			this.formDialog.setShowDialogFlag(false);
			MessageBox.post(this.formDialog.parent,
					this.appReg.getString("unableToOpen.MESSAGE"),
					localTCException, this.appReg.getString("openError.TITLE"),
					4);
		}
	}

	public void setFocus() {
		if (!(this.showButtonPanel))
			return;
		ViewEditHelper.CKO localCKO = getObjectState();
		List localList = (List) this.buttonMap.get(localCKO);
		if ((localList == null) || (localList.size() < 1))
			return;
		((JButton) localList.get(0)).requestFocusInWindow();
	}

	protected void postSaveFormUpdates() {
	}

	public void processSetState() {
		processSetState(true);
	}

	public void processSetState(final boolean paramBoolean) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boolean val$refreshPanel = paramBoolean;
				OpenFormPanel.this.setState(val$refreshPanel);
			}
		});
	}

	protected void setState() {
		setState(true);
	}

	protected void setState(boolean paramBoolean) {
		this.renderingPanel.setErrorFlag(false);
		if (paramBoolean) {
			try {
				if (!(this.form.isValidUid()))
					return;
				this.renderingPanel = this.renderingPanel.refreshPanel(this,
						this.form);
			} catch (Exception localException) {
				handleException(localException);
			}
			initOkToModify();
		}
		processButtons();
		this.showButtonPanel = this.okToModify;
		processPanels();
		if (this.showButtonPanel) {
			this.buttonPanel.setVisible(true);
			this.buttonPanelSeparator.setVisible(true);
		} else {
			this.buttonPanel.setVisible(false);
			this.buttonPanelSeparator.setVisible(false);
		}
		setFocus();
		validate();
		repaint();
	}

	public void processComponentEvents(
			final AIFComponentEvent[] paramArrayOfAIFComponentEvent) {
		String str = this.appReg.getString("refreshDisplayMessage");
		this.session.setReadyStatus();
		this.session.queueOperation(new AbstractAIFOperation() {
			public void executeOperation() throws Exception {
				OpenFormPanel.this
						.processComponentEventsRequest(paramArrayOfAIFComponentEvent);
			}
		});
	}

	protected boolean isPanelSavable() {
		boolean bool = this.renderingPanel.isSavable(true);
		return bool;
	}

	protected void processComponentEventsRequest(
			AIFComponentEvent[] paramArrayOfAIFComponentEvent) {
		try {
			if (this.form == null)
				return;
			for (AIFComponentEvent localAIFComponentEvent : paramArrayOfAIFComponentEvent) {
				InterfaceAIFComponent localInterfaceAIFComponent = localAIFComponentEvent
						.getComponent();
				if ((!(localInterfaceAIFComponent instanceof TCComponentForm))
						|| (!(localAIFComponentEvent instanceof AIFComponentChangeEvent)))
					continue;
				if (!(localAIFComponentEvent instanceof AIFComponentPropertyChangeEvent)) {
					if (!(localInterfaceAIFComponent.equals(this.form)))
						continue;
					processSetState();
					return;
				}
				if (!(localInterfaceAIFComponent.equals(this.form)))
					continue;
				processSetState(false);
				return;
			}
		} catch (Exception localException) {
			MessageBox.post(localException);
		}
	}

	public void closeSignaled() {
		detachListeners();
	}

	protected void detachListeners() {
		this.session.removeAIFComponentEventListener(this);
		removeRendering();
	}

	public void addOkButtonListener(ActionListener paramActionListener) {
		if (this.okButton == null)
			return;
		this.okButton.addActionListener(paramActionListener);
	}

	public void addApplyButtonListener(ActionListener paramActionListener) {
		if (this.applyButton == null)
			return;
		this.applyButton.addActionListener(paramActionListener);
	}

	public void addClearButtonListener(ActionListener paramActionListener) {
		if (this.clearButton == null)
			return;
		this.clearButton.addActionListener(paramActionListener);
	}

	public void addCancelButtonListener(ActionListener paramActionListener) {
		if (this.cancelButton == null)
			return;
		this.cancelButton.addActionListener(paramActionListener);
	}

	public void addSaveCheckInButtonListener(ActionListener paramActionListener) {
		if (this.saveCheckInButton == null)
			return;
		this.saveCheckInButton.addActionListener(paramActionListener);
	}

	public void addSaveCheckInCommandListener(
			ICommandListener paramICommandListener) {
		this.m_saveCICommandListener = paramICommandListener;
	}

	public void addSaveCheckOutButtonListener(ActionListener paramActionListener) {
		if (this.saveCheckOutButton == null)
			return;
		this.saveCheckOutButton.addActionListener(paramActionListener);
	}

	public void addCancelCheckOutButtonListener(
			ActionListener paramActionListener) {
		if (this.cancelCheckOutButton == null)
			return;
		this.cancelCheckOutButton.addActionListener(paramActionListener);
	}

	public void addCancelCheckOutCommandListener(
			ICommandListener paramICommandListener) {
		this.m_cancelCOCommandListener = paramICommandListener;
	}

	public void addCloseButtonListener(ActionListener paramActionListener) {
		if (this.closeButton == null)
			return;
		this.closeButton.addActionListener(paramActionListener);
	}

	public void removeRendering() {
		if ((this.renderingPanel == null)
				|| (!(this.renderingPanel instanceof AbstractRendering)))
			return;
		PropertyBeanContainer localPropertyBeanContainer = null;
		if (this.renderingPanel instanceof AutomaticRendering)
			localPropertyBeanContainer = ((AutomaticRendering) this.renderingPanel)
					.getBeanContainer();
		else if (this.renderingPanel instanceof PropertyBeanContainer)
			localPropertyBeanContainer = (PropertyBeanContainer) this.renderingPanel;
		if (localPropertyBeanContainer == null)
			return;
		localPropertyBeanContainer.removeEventListener();
	}

	public void processEditButtonAction(ViewEditHelper paramViewEditHelper) {
		if (paramViewEditHelper == null)
			return;
		try {
			if (paramViewEditHelper.isSWTPerspective()) {
				paramViewEditHelper.reserveSWTOperation(this.form,
						this.form.getSession(),
						new IViewEditOperationListener() {
							public void operationComplete(int paramInt) {
								if (paramInt != 0)
									return;
								OpenFormPanel.this.processSetState();
							}

							public void operationError(
									TCException paramTCException) {
								Logger.getLogger(OpenFormPanel.class).error(
										paramTCException.getLocalizedMessage(),
										paramTCException);
							}
						});
				return;
			}
			if (!(paramViewEditHelper.reserveOperation(this.form)))
				return;
			processSetState();
		} catch (TCException localTCException) {
			MessageBox.post(localTCException);
		}
	}

	protected boolean checkForSave(boolean paramBoolean) {
		boolean bool1 = true;
		int i = 1;
		if ((this.implicitCoOkButton != null)
				&& (this.implicitCoOkButton.isVisible())
				&& (this.renderingPanel instanceof AbstractRendering)) {
			Registry localRegistry = Registry.getRegistry(this);
			if (((AbstractRendering) this.renderingPanel).isRenderingModified()) {
				int j = ConfirmationDialog.post(Utilities.getCurrentFrame(),
						localRegistry.getString("genSaveChanges.TITLE"),
						localRegistry.getString("genSaveChanges.TEXT"));
				if (j == 2) {
					boolean bool2 = this.renderingPanel.isSavable(true);
					if (bool2) {
						i = 0;
						performImplicitCO(paramBoolean);
					} else {
						bool1 = false;
					}
				}
			}
		} else {
			bool1 = this.renderingPanel.checkForSave(this.form);
		}
		if ((bool1) && (paramBoolean) && (i != 0))
			this.formDialog.disposeDialog();
		return bool1;
	}

	public void processImplicitCoOkButtonAction() {
		performImplicitCO(true);
	}

	public void processImplicitCoApplyButtonAction() {
		performImplicitCO(false);
	}

	private void performImplicitCO(boolean paramBoolean) {
		if (!(isPanelSavable()))
			return;
		String str = Registry.getRegistry(this).getString("doingImplicitCO");
		ImplicitCheckOutOperation localImplicitCheckOutOperation = null;
		localImplicitCheckOutOperation = new ImplicitCheckOutOperation(
				this.renderingPanel, this.form, str);
		if (this.renderingPanel instanceof AbstractRendering) {
			AbstractRendering localAbstractRendering = (AbstractRendering) this.renderingPanel;
			final InterfaceAIFOperationExecutionListener localInterfaceAIFOperationExecutionListener = localAbstractRendering
					.getEventProcessingShield();
			localImplicitCheckOutOperation.addOperationListener(
					ImplicitCheckOutOperation.SubOperations.CHECK_OUT,
					new InterfaceAIFOperationExecutionListener() {

						public void startOperation(String paramString) {
							if (localInterfaceAIFOperationExecutionListener == null)
								return;
							localInterfaceAIFOperationExecutionListener
									.startOperation(paramString);
						}

						public void endOperation() {
						}

						public void exceptionThrown(Exception paramException) {
							if (localInterfaceAIFOperationExecutionListener == null)
								return;
							localInterfaceAIFOperationExecutionListener
									.exceptionThrown(paramException);
						}
					});
			localImplicitCheckOutOperation.addOperationListener(
					ImplicitCheckOutOperation.SubOperations.SAVE,
					new InterfaceAIFOperationExecutionListener() {
						public void startOperation(String paramString) {
						}

						public void endOperation() {
							// if (!(this.val$closeDialogOnFinish))
							// return;
							OpenFormPanel.this.closeDialogOnFinish();
						}

						public void exceptionThrown(Exception paramException) {
							if (localInterfaceAIFOperationExecutionListener == null)
								return;
							localInterfaceAIFOperationExecutionListener
									.exceptionThrown(paramException);
						}
					});
		}
		this.session.queueOperation(localImplicitCheckOutOperation);
	}

	private void closeDialogOnFinish() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (OpenFormPanel.this.formDialog == null)
					return;
				OpenFormPanel.this.formDialog.disposeDialog();
			}
		});
	}

	protected ViewEditHelper.CKO getObjectState() {
		return this.helper.getObjectState(this.form);
	}

	private boolean isFormReadOnly() {
		TCPreferenceService localTCPreferenceService = this.session
				.getPreferenceService();
		return (!(FormEditability.EDIT.getValue()
				.equalsIgnoreCase(localTCPreferenceService
						.getStringValue("Form_double_click"))));
	}

	private static enum FormEditability {
		EDIT;

		// private final String m_value;

		public String getValue() {
			return "EDIT";
		}
	}

	private class RenderPanelOperation extends AbstractAIFOperation {
		public RenderPanelOperation(String arg2) {
		}

		public void executeOperation() {
			try {
				AbstractRendering localAbstractRendering = null;
				AbstractTCForm localAbstractTCForm = null;
				localAbstractRendering = RenderingLoader.load(
						OpenFormPanel.this.form, Boolean.TRUE,
						OpenFormPanel.this.formDialog);
				if (localAbstractRendering instanceof AutomaticRendering) {
					localAbstractTCForm = FormLoader.load(
							OpenFormPanel.this.form, true);
					if (localAbstractTCForm instanceof AutomaticTCForm)
						localAbstractTCForm = null;
				}
				OpenFormPanel.this.renderingPanel = ((localAbstractTCForm != null) ? localAbstractTCForm
						: localAbstractRendering);
				OpenFormPanel.this.remove(OpenFormPanel.this.progressPanel);
				OpenFormPanel.this.progressPanel.stopUpdateThread();
				OpenFormPanel.this.renderingPanel
						.addToPanel(OpenFormPanel.this);
				OpenFormPanel.this.initOkToModify();
				OpenFormPanel.this.processButtons();
				OpenFormPanel.this.processPanels();
				OpenFormPanel.this.buttonPanel
						.setVisible(OpenFormPanel.this.showButtonPanel);
				OpenFormPanel.this.buttonPanelSeparator
						.setVisible(OpenFormPanel.this.showButtonPanel);
				if (OpenFormPanel.this.formDialog != null)
					OpenFormPanel.this.renderingPanel.displayDialog(
							OpenFormPanel.this.formDialog, true);
				OpenFormPanel.this.revalidate();
				OpenFormPanel.this.repaint();
			} catch (InvocationTargetException localInvocationTargetException) {
				if ((OpenFormPanel.this.formDialog == null)
						|| (OpenFormPanel.this.formDialog.parent == null)) {
					MessageBox.post(localInvocationTargetException
							.getTargetException().toString(),
							localInvocationTargetException,
							OpenFormPanel.this.appReg
									.getString("openError.TITLE"), 1);
				} else {
					OpenFormPanel.this.formDialog.setShowDialogFlag(false);
					MessageBox.post(OpenFormPanel.this.formDialog.parent,
							localInvocationTargetException.getTargetException()
									.toString(),
							localInvocationTargetException,
							OpenFormPanel.this.appReg
									.getString("openError.TITLE"), 1);
				}
			} catch (Exception localException) {
				OpenFormPanel.this.handleException(localException);
			} finally {
				OpenFormPanel.this.progressPanel.stopUpdateThread();
			}
		}
	}

	private class SaveFormOperation extends AbstractAIFOperation {
		private final boolean dismissOnComplete;

		public SaveFormOperation(TCSession session, boolean paramBoolean) {
			this.dismissOnComplete = paramBoolean;
		}

		public SaveFormOperation(boolean paramBoolean) {
			this.dismissOnComplete = paramBoolean;
		}

		public void executeOperation() {
			TCSession localTCSession = (TCSession) getSession();
			Registry localRegistry = Registry.getRegistry(this);
			if (OpenFormPanel.this.okButton != null)
				OpenFormPanel.this.okButton.setEnabled(false);
			if (OpenFormPanel.this.applyButton != null)
				OpenFormPanel.this.applyButton.setEnabled(false);
			if (OpenFormPanel.this.cancelButton != null)
				OpenFormPanel.this.cancelButton.setEnabled(false);
			localTCSession.setStatus(localRegistry
					.getString("saveForm.MESSAGE"));
			try {
				OpenFormPanel.this.renderingPanel.save();
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			
			OpenFormPanel.this.postSaveFormUpdates();
			
			//add by wuwei
			if (OpenFormPanel.this.applyButton != null)
				OpenFormPanel.this.applyButton.setEnabled(true);
			localTCSession.setReadyStatus();
			if (((!(OpenFormPanel.this.renderingPanel.getErrorFlag())) && (this.dismissOnComplete))
					|| (OpenFormPanel.this.formViewer != null)) {
				if (OpenFormPanel.this.formDialog == null)
					return;
				OpenFormPanel.this.formDialog.disposeDialog();
			} else {
				OpenFormPanel.this.processSetState();
			}
		}
	}

	private class SaveAndSubmitFormOperation extends AbstractAIFOperation {
		private final boolean dismissOnComplete;

		public SaveAndSubmitFormOperation(TCSession session, boolean bool) {
			this.dismissOnComplete = bool;
		}

		public void executeOperation() {
			try {
				AIFComponentContext[] refs = OpenFormPanel.this.form
						.whereReferenced();
				int isinProcess = 0;
				if ((refs != null) && (refs.length > 0)) {
					for (AIFComponentContext aifComponentContext : refs) {
						InterfaceAIFComponent com = aifComponentContext
								.getComponent();
						if (com instanceof TCComponentTask) {
							isinProcess++;
							// return;
						}
					}
				}

			} catch (TCException e1) {
				e1.printStackTrace();
			}

			System.out.println("SaveAndSubmitFormOperation executeOperation");
			TCSession localTCSession = (TCSession) getSession();
			Registry localRegistry = Registry.getRegistry(this);
			String formType = OpenFormPanel.this.form.getType();

			if (OpenFormPanel.this.okButton != null)
				OpenFormPanel.this.okButton.setEnabled(false);

			if ((OpenFormPanel.this.applyButton != null)
					&& (!formType.equals("JCI6_Ext2Supp")))
				OpenFormPanel.this.applyButton.setEnabled(false);

			if (OpenFormPanel.this.cancelButton != null)
				OpenFormPanel.this.cancelButton.setEnabled(false);
			localTCSession.setStatus(localRegistry
					.getString("saveForm.MESSAGE"));
			int i = 29;

			try {
				OpenFormPanel.this.renderingPanel.save();
			} catch (Exception localException1) {
				localException1.printStackTrace();
			}
			OpenFormPanel.this.postSaveFormUpdates();

			if (OpenFormPanel.this.okButton != null)
				OpenFormPanel.this.okButton.setEnabled(true);

			if ((OpenFormPanel.this.applyButton != null))
				OpenFormPanel.this.applyButton.setEnabled(true);
			
			

			localTCSession.setReadyStatus();
			if ((this.dismissOnComplete)
					|| (OpenFormPanel.this.formViewer != null)) {
				if (OpenFormPanel.this.formDialog != null)
					OpenFormPanel.this.formDialog.disposeDialog();

				// new Thread() {
				// public void run() {
				// try {
				// Thread.sleep(2000L);
				// TCComponent[] extComs = OpenFormPanel.this.form
				// .getReferenceListProperty("jci6_Ext2Detail");
				// System.out.println("TEST1:is the form empty?");
				// if ((extComs == null) || (extComs.length == 0)) {
				// MessageBox.post("未发现保存的数据，空表单，不能发起流程...",
				// "INFORMATION", 2);
				// } else {
				// System.out.println("TEST1:is the form empty?"
				// + extComs.length);
				// if (OpenFormPanel.this.formDialog != null)
				// OpenFormPanel.this.formDialog
				// .disposeDialog();
				//
				// System.out.println("-------发起流程！！！---");
				// NewProcessCommand command = new
				// NewProcessCommand(AIFUtility.getActiveDesktop(),
				// AIFUtility.getCurrentApplication(), new
				// TCComponent[] { OpenFormPanel.this.form });
				// command.executeModal();
				// }
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// } catch (TCException e) {
				// e.printStackTrace();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }.start();
			} else {

				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000L);
							TCComponent[] extComs = OpenFormPanel.this.form
									.getReferenceListProperty("jci6_Ext2Detail");
							System.out.println("TEST1:is the form empty?");
							if ((extComs == null) || (extComs.length == 0)) {
								MessageBox.post("未发现保存的数据，空表单，不能发起流程...",
										"INFORMATION", 2);
								
							} else {
								System.out.println("TEST1:is the form empty?"
										+ extComs.length);
								if (OpenFormPanel.this.formDialog != null)
									OpenFormPanel.this.formDialog.disposeDialog();
								System.out.println("-------发起流程！！！---");
								NewProcessCommand command = new NewProcessCommand(AIFUtility.getActiveDesktop(),AIFUtility.getCurrentApplication(),new TCComponent[] { OpenFormPanel.this.form });
								command.executeModal();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (TCException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				OpenFormPanel.this.processSetState();

			}

			

		}
	}
}