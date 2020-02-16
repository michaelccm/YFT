/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		  

 #																			   
 #=============================================================================
 # File name: JCIMoveToCommand.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-11	MengYaWei  	Customization of pse application copy	 								   
 #=============================================================================
 */

package com.yfjcebp.pse.paste;

import java.awt.Frame;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.MissingResourceException;

import org.eclipse.swt.widgets.Display;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.InterfaceAIFOperationExecutionListener;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.paste.PasteDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.commands.MoveToCommand;
import com.teamcenter.rac.pse.operations.MoveToOperation;
import com.teamcenter.rac.util.ConfirmDialog;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.pse.paste.PKRCreater;

public class JCIMoveToCommand extends MoveToCommand {
	private Registry reg = Registry.getRegistry("com.yfjcebp.pse.paste.paste");
	private final ArrayList<TCComponentUser> userListTag = new ArrayList();

	public JCIMoveToCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JCIMoveToCommand(InterfaceAIFComponent[] ainterfaceaifcomponent,
			Boolean boolean1, Boolean boolean2) {
		super(ainterfaceaifcomponent, boolean1, boolean2);
		// TODO Auto-generated constructor stub
	}

	public JCIMoveToCommand(InterfaceAIFComponent[] ainterfaceaifcomponent,
			Frame frame, Boolean boolean1) {
		AIFComponentContext aaifcomponentcontext[];
		System.out.println("--------- pse -----------");
		parent = frame;
		aaifcomponentcontext = buildComponentContextFromClipboard(
				ainterfaceaifcomponent, null);// 从粘贴板获得粘贴盠�对象要粘贴的东西的引�
		if (aaifcomponentcontext == null || ainterfaceaifcomponent == null)
			return;

		// ainterfaceaifcomponent[0]//为�定粘贴的目标�象引用集�
		// aaifcomponentcontext这个序列�粘贴的�象引用集�

		if (ainterfaceaifcomponent.length == 1) {

			try {
				String type = ainterfaceaifcomponent[0].getType();
				System.out.println("type======" + type);
				String property = ainterfaceaifcomponent[0]
						.getProperty("al_source_type");
				System.out.println("al_source_type======" + property);

				if (ainterfaceaifcomponent[0] instanceof TCComponentBOMLine) {

					TCComponentItemRevision itemRevision = ((TCComponentBOMLine) ainterfaceaifcomponent[0])
							.getItemRevision();

					if (itemRevision.getType().equals("JCI6_PFARevision")) {

						// 5.17---copy用户user
						int contextCnt = aaifcomponentcontext.length;
						for (int i = 0; i < contextCnt; i++) {
							TCComponent comp = (TCComponent) aaifcomponentcontext[i]
									.getComponent();
							if ((comp instanceof TCComponentUser))
								this.userListTag.add((TCComponentUser) comp);
							else if ((comp instanceof TCComponentGroupMember)) {
								this.userListTag
										.add(((TCComponentGroupMember) comp)
												.getUser());
							}
						}

						if ((this.userListTag != null)
								&& (this.userListTag.size() > 0)) {
							((TCComponentBOMLine) ainterfaceaifcomponent[0])
									.getItemRevision()
									.add("JCI6_SDT",
											this.userListTag
													.toArray(new TCComponentUser[this.userListTag
															.size()]));
							return;

						} else {

							TCComponentBOMLine bomLine = (TCComponentBOMLine) ainterfaceaifcomponent[0];

							// 5.29--俔�做一�话�
							// DeviceData data = new DeviceData();
							Display display = Display.getDefault();
							ChooseDialog dialog = new ChooseDialog(
									bomLine.getSession(), display);

							System.out
									.println("=======6.3xiugai=======dialog=========================="
											+ dialog.getNumberStr());

							if (dialog.getflag() < 0) {
								return;
							}

							String pkrLocation = dialog.getNumberStr();
							// ================================================
							PKRCreater pkrcreater = new PKRCreater(
									ainterfaceaifcomponent[0],
									aaifcomponentcontext, pkrLocation);

							if (pkrcreater.PEFFunAreaRevType.equals(pkrcreater
									.getPEFFunAreaRevision().getType())) {
								String errMsg = null;
								String rtnStr = pkrcreater
										.isALLPKRTRevisionType();
								if ("ALL".equals(rtnStr)) {
									errMsg = pkrcreater.VerifyFuncArea();
									if (errMsg != null) {
										MessageBox.post(errMsg, "WARNING",
												MessageBox.WARNING);
										return;
									} else {
										pkrcreater
												.baseOnPKRTRevisionsCreatePKRItems();

										// 刷新
										// TCComponentBOMLine bomLine =
										// (TCComponentBOMLine)
										// ainterfaceaifcomponent[0];
										bomLine.refresh();
									}
									return;
								} else if ("SOME".equals(rtnStr)) {
									// 提示：PKR模板与其他类型的零组件应该分�粘贴到功能�域版朸��
									MessageBox.post(reg.getString("SomePKRT"),
											"WARNING", MessageBox.WARNING);
									return;
								}
							}

							// =================================================
						}
					}
				}

				// 5.8俔�--CfgAttachmentLine(TCComponentCfgAttachmentLine)
				else if (property.equals("JCI6_PKRRevision")) {

					PKRCreater pkrcreater2 = new PKRCreater(
							ainterfaceaifcomponent[0], aaifcomponentcontext);

					System.out.println("------JCI6_PKRRevision昐�等于�"
							+ pkrcreater2.getPKRRevision().getType());

					if ("JCI6_PKRRevision".equals(pkrcreater2.getPKRRevision()
							.getType())) {
						String errMsg = null;
						String rtnStr = pkrcreater2.isALLDRTRevisionType();
						if ("ALL".equals(rtnStr)) {
							// 判断他们的FuncArea值是否相�
							errMsg = pkrcreater2.VerifyFuncAreaforDRT();
							if (errMsg != null) {
								MessageBox.post(errMsg, "WARNING",
										MessageBox.WARNING);
								return;
							} else {
								// 执�诏�---5.8
								pkrcreater2.baseOnDRTRevisionsCreateDR();
							}
							return;

						} else if ("SOME".equals(rtnStr)) {
							// 提示：DRT模板与其他类型的零组件应该分�粘贴到功能�域版朸��
							MessageBox.post(reg.getString("SomeDRT"),
									"WARNING", MessageBox.WARNING);
							return;
						}
					} else {

						return;
					}

				} else {
					return;
				}
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		InterfaceAIFComponent interfaceaifcomponent;
		try {
			interfaceaifcomponent = aaifcomponentcontext[0].getComponent();// 获得的是版本

			if (ainterfaceaifcomponent.length == 1
					&& (interfaceaifcomponent instanceof TCComponentBOMLine)
					&& (ainterfaceaifcomponent[0] instanceof TCComponentBOMLine)
					&& !((TCComponentBOMLine) interfaceaifcomponent).window()
							.getRedliningMode()
					&& ((TCComponentBOMLine) interfaceaifcomponent)
							.getPendingCut()
					&& !((TCComponentBOMLine) ainterfaceaifcomponent[0])
							.getPendingCut()) {
				if (validForMoveTo(aaifcomponentcontext)) {
					TCComponentBOMLine atccomponentbomline[] = new TCComponentBOMLine[aaifcomponentcontext.length];
					for (int i = 0; i < aaifcomponentcontext.length; i++)
						atccomponentbomline[i] = (TCComponentBOMLine) aaifcomponentcontext[i]
								.getComponent();

					TCComponentBOMWindow tccomponentbomwindow = atccomponentbomline[0]
							.window();
					MoveToOperation movetooperation = new MoveToOperation(
							(TCComponentBOMLine) ainterfaceaifcomponent[0],
							atccomponentbomline);
					movetooperation
							.addOperationListener(new InterfaceAIFOperationExecutionListener() {

								@Override
								public void startOperation(String s1) {
								}

								@Override
								public void endOperation() {
								}

								@Override
								public void exceptionThrown(Exception exception1) {
									MessageBox.post(exception1, false);
								}

							});
					atccomponentbomline[0].getSession().queueOperation(
							movetooperation);
					tccomponentbomwindow.fireComponentChangeEvent();
				}
				return;
			}
		} catch (Exception exception) {
			MessageBox.post(parent, exception);
			return;
		}
		try {
			if ((interfaceaifcomponent instanceof TCComponentBOMLine)
					&& ((TCComponentBOMLine) interfaceaifcomponent).window()
							.getRedliningMode()
					&& ((TCComponentBOMLine) interfaceaifcomponent)
							.getPendingCut()
					&& !doConfirmPost(Registry.getRegistry(this),
							"moveNodesAction.redline_warning.TITLE",
							"moveNodesAction.redline_warning.MESSAGE"))
				return;
		} catch (MissingResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (ainterfaceaifcomponent.length > 1
					&& (interfaceaifcomponent instanceof TCComponentBOMLine)
					&& ((TCComponentBOMLine) interfaceaifcomponent)
							.getPendingCut()
					&& !doConfirmPost(Registry.getRegistry(this),
							"moveNodesAction.redline_warning.TITLE",
							"moveNodesAction.multi_selection_warning.MESSAGE"))
				return;
		} catch (MissingResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (aaifcomponentcontext != null && parent != null) {
			String s = (String) aaifcomponentcontext[0].getContext();
			if (hasVisibleAttributesOnRelation(s)) {
				//setRunnablePropsOnRelation(null, aaifcomponentcontext);
				setRunnablePropsOnRelation(aaifcomponentcontext);
			} else {
				dlg = new PasteDialog(aaifcomponentcontext, parent,
						boolean1.booleanValue());
				dlg.addPropertyChangeListener(this);
				setRunnable(dlg);
			}
		} else if (aaifcomponentcontext != null)
			setRunnable(new com.teamcenter.rac.commands.paste.PasteCommand.PasteRunnable(
					aaifcomponentcontext));
		if (aaifcomponentcontext != null) {
			AbstractAIFUIApplication currentApplication = AIFUtility.getCurrentApplication();
		}
		PropertyChangeSupport	propertySupport = new PropertyChangeSupport(this);
		return;
		// TODO Auto-generated constructor stub
	}

	public JCIMoveToCommand(InterfaceAIFComponent[] ainterfaceaifcomponent,
			String s, Boolean boolean1, Boolean boolean2) {
		super(ainterfaceaifcomponent, s, boolean1, boolean2);
		// TODO Auto-generated constructor stub
	}

	public JCIMoveToCommand(InterfaceAIFComponent[] ainterfaceaifcomponent,
			String s, Boolean boolean1, Frame frame) {
		super(ainterfaceaifcomponent, s, boolean1, frame);
		// TODO Auto-generated constructor stub
	}

	public JCIMoveToCommand(TCComponent[] atccomponent,
			InterfaceAIFComponent[] ainterfaceaifcomponent, Boolean boolean1) {
		super(atccomponent, ainterfaceaifcomponent, boolean1);
		// TODO Auto-generated constructor stub
	}

	public JCIMoveToCommand(TCComponent[] atccomponent,
			InterfaceAIFComponent[] ainterfaceaifcomponent, Frame frame) {
		super(atccomponent, ainterfaceaifcomponent, frame);
		// TODO Auto-generated constructor stub
	}

	public JCIMoveToCommand(TCComponent[] atccomponent,
			InterfaceAIFComponent[] ainterfaceaifcomponent, String s,
			Frame frame) {
		super(atccomponent, ainterfaceaifcomponent, s, frame);
		// TODO Auto-generated constructor stub
	}

	private boolean doConfirmPost(Registry registry, String s, String s1) {
		boolean flag = true;
		if (Display.getCurrent() != null) {
			ConfirmDialog confirmdialog = new ConfirmDialog(null,
					registry.getString(s), registry.getString(s1));
			confirmdialog.open();
			if (!confirmdialog.isOkayClicked())
				flag = false;
		} else if (ConfirmationDialog.post(parent, registry.getString(s),
				registry.getString(s1)) != 2)
			flag = false;
		return flag;
	}
}
