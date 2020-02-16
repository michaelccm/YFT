/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.teamcenter.rac.commands.delete;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soaictstubs.ICCTException;
import com.teamcenter.rac.kernel.SoaUtil;
import com.teamcenter.services.rac.core._2007_06.DataManagement;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import javax.swing.JPanel;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.common.AbstractTCApplication;
import java.util.Iterator;
import com.teamcenter.rac.kernel.TCComponentType;
import java.util.HashMap;
import com.teamcenter.rac.aif.AIFDesktop;
import java.awt.Frame;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentTaskInBox;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentFolder;
import java.util.ArrayList;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentPseudoFolder;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCComponentCfgAttachmentLine;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import java.util.List;
import java.util.Map;
import com.teamcenter.rac.kernel.TCComponent;
import org.apache.log4j.Logger;
import com.teamcenter.rac.aif.AbstractAIFOperation;

public class DeleteOperation extends AbstractAIFOperation {
	private static final Logger logger;
	protected TCComponent parentCmp;
	private TCComponent childCmp;
	private String contextString;
	private Map<String, List<TCComponent>> table;
	private Map<TCComponent, List<AIFComponentContext>> whereReferencedTable;
	protected boolean deleteAllSequencesFlag;
	public static final String CONTENTS_RELATION = "contents";
	public static final String OWINING_USER = "owning_user";
	public static final String SURROGATE_LIST = "surrogate_list";
	public static final int DELETE_ERROR_CODE = 7010;

	public DeleteOperation(final TCComponent parentCmp) {
		super(Registry.getRegistry((Class) DeleteOperation.class).getString(
				"deleteObjects"));
		this.deleteAllSequencesFlag = false;
		this.parentCmp = parentCmp;
	}

	public DeleteOperation(final TCComponent parentCmp,
			final TCComponent childCmp, final String contextString) {
		super((Registry.getRegistry((Class) DeleteOperation.class).getString(
				"deleteObjects")
				+ childCmp == null) ? "" : (" " + childCmp.toString()));
		this.deleteAllSequencesFlag = false;
		this.parentCmp = parentCmp;
		this.childCmp = childCmp;
		this.contextString = contextString;
	}

	public DeleteOperation(final AIFComponentContext aifComponentContext) {
		this((TCComponent) aifComponentContext.getParentComponent(),
				(TCComponent) aifComponentContext.getComponent(),
				(String) aifComponentContext.getContext());
	}

	public DeleteOperation(final TCComponent parentCmp,
			final Map<String, List<TCComponent>> table) {
		super(Registry.getRegistry((Class) DeleteOperation.class).getString(
				"deleteObjects"));
		this.deleteAllSequencesFlag = false;
		this.parentCmp = parentCmp;
		this.table = table;
	}

	public DeleteOperation(
			final AIFComponentContext aifComponentContext,
			final Map<TCComponent, List<AIFComponentContext>> whereReferencedTable) {
		this(aifComponentContext);
		this.whereReferencedTable = whereReferencedTable;
	}

	public DeleteOperation(
			final TCComponent tcComponent,
			final Map<String, List<TCComponent>> map,
			final Map<TCComponent, List<AIFComponentContext>> whereReferencedTable) {
		this(tcComponent, map);
		this.whereReferencedTable = whereReferencedTable;
	}

	public void executeOperation() throws TCException {
		if (this.table != null) {
			this.executeByTable();
			return;
		}
		ArrayList<TCComponent> ext2Details = new ArrayList<TCComponent>();
		if (childCmp != null && childCmp instanceof TCComponentForm) {
			String childCmpType = childCmp.getType();
			if ("JCI6_Ext2Supp".equals(childCmpType)) {
				TCComponent[] coms = childCmp.getReferenceListProperty("jci6_Ext2Detail");
				if (coms != null && coms.length > 0) {
					for (TCComponent tcComponent : coms) {
						ext2Details.add(tcComponent);
					}
				}
			}
			
		}
		final Registry registry = Registry.getRegistry((Object) this);
		if (this.parentCmp instanceof TCComponentCfgAttachmentLine) {
			((TCComponentCfgAttachmentLine) this.parentCmp).deleteChild(
					this.childCmp, this.contextString);
		} else if (this.contextString != null) {
			if (this.contextString.startsWith("ERROR: ")) {
				throw new TCException(this.contextString);
			}
			if (this.contextString.equals("window")) {
				if (this.parentCmp instanceof TCComponentBOMWindow) {
					this.cutItems(this.parentCmp, this.childCmp,
							this.contextString);
				}
			} else {
				if (this.contextString.equals("view")
						&& this.parentCmp instanceof TCComponentPseudoFolder) {
					throw new TCException(new int[]{1}, new int[]{0},
							new String[]{registry
									.getString("cannotEditStructure.MESSAGE")});
				}
				if ((this.contextString.equals("root_target_attachments") || this.contextString
						.equals("root_reference_attachments"))
						&& this.parentCmp instanceof TCComponentPseudoFolder
						&& this.parentCmp.getChildrenCount() > 0) {
					throw new TCException(
							new int[]{1},
							new int[]{0},
							new String[]{registry.getString("referencedby")
									+ " "
									+ ((TCComponentTask) ((TCComponentPseudoFolder) this.parentCmp)
											.getOwningComponent()).getRoot()
											.getName()});
				}
				try {
					AIFComponentContext[] whereReferenced = new AIFComponentContext[0];
					if (this.whereReferencedTable == null
							|| !this.whereReferencedTable
									.containsKey(this.childCmp)) {
						whereReferenced = this.childCmp.whereReferenced();
					} else {
						final List<AIFComponentContext> list = this.whereReferencedTable
								.get(this.childCmp);
						if (!list.isEmpty()) {
							whereReferenced = list
									.toArray(new AIFComponentContext[list
											.size()]);
						}
					}
					final ArrayList<TCComponent> list2 = new ArrayList<TCComponent>();
					if (whereReferenced != null) {
						if (whereReferenced.length >= 1) {
							for (final AIFComponentContext aifComponentContext : whereReferenced) {
								if (aifComponentContext.getComponent() instanceof TCComponentFolder) {
									if (!((TCComponentFolder) aifComponentContext
											.getComponent())
											.equals((Object) this.parentCmp)) {
										try {
											((TCComponentFolder) aifComponentContext
													.getComponent())
													.cutOperation(
															"contents",
															new TCComponent[]{this.childCmp},
															false);
											list2.add((TCComponentFolder) aifComponentContext
													.getComponent());
										} catch (TCException ex) {
											this.pasteBack(list2, this.childCmp);
											throw ex;
										}
									}
								}
							}
						} else if (whereReferenced.length == 0
								&& this.childCmp
										.isTypeOf("POM_application_object")) {
							this.contextString = "query_result";
							this.parentCmp = null;
						}
					}
					if (this.childCmp instanceof TCComponentItemRevision
							&& !this.deleteAllSequencesFlag) {
						try {
							this.deleteThisSequence(this.childCmp);
							return;
						} catch (TCException ex2) {
							this.pasteBack(list2, this.childCmp);
							throw ex2;
						}
					}
					TCComponent owningResource = null;
					if (this.childCmp instanceof TCComponentTaskInBox) {
						owningResource = ((TCComponentTaskInBox) this.childCmp)
								.getOwningResource();
					}
					if (owningResource instanceof TCComponentResourcePool) {
						throw new TCException(
								new int[]{1},
								new int[]{0},
								new String[]{registry
										.getString("ResourcePoolSubscriptionOperation.MESSAGE")});
					}
					if (this.parentCmp instanceof TCComponentFolder) {
						if (this.childCmp instanceof TCComponentTaskInBox) {
							this.removeSurrogatesForTaskInboxes(new TCComponent[]{this.childCmp});
							return;
						}
					} else if (this.parentCmp == null
							&& this.childCmp instanceof TCComponentFolder
							&& !this.removeSurrogatesForInboxFolder(this.childCmp)) {
						return;
					}
					try {
						if (this.childCmp.getType().equals("Fnd0Markup")) {
							this.childCmp.delete();
							this.parentCmp.refresh();
							this.parentCmp.fireComponentChangeEvent();
						} else {
							this.childCmp.removeAndDestroy(this.contextString,
									this.parentCmp);
						}
					} catch (TCException ex3) {
						if (ex3.getErrorCode() != 7010) {
							this.pasteBack(list2, this.childCmp);
							throw ex3;
						}
					}
				} catch (TCException ex4) {
					if (ex4.getErrorCode() != 7010) {
						throw ex4;
					}
				}
			}
		} else if (this.childCmp != null) {
			if (this.childCmp instanceof TCComponentItemRevision
					&& !this.deleteAllSequencesFlag) {
				this.deleteThisSequence(this.childCmp);
			} else {
				this.childCmp.delete();
			}
		}
		
		if (ext2Details.size() > 0) {
			for (TCComponent tcComponent : ext2Details) {
				tcComponent.delete();
			}
		}
	}

	private int ConfirmRemovalOfSurrogates(
			final TCComponentUser tcComponentUser,
			final TCComponentUser tcComponentUser2,
			final TCComponent tcComponent, final boolean b) throws TCException {
		final Registry registry = Registry.getRegistry((Object) this);
		final AIFDesktop activeDesktop = AIFUtility.getActiveDesktop();
		String s;
		if (b) {
			s = tcComponentUser.getUserId() + " "
					+ registry.getString("removeSurrogate") + " "
					+ registry.getString("confirmationmessage");
		} else {
			s = registry.getString("inboxDeletionConfirmation")
					+ registry.getString("confirmationmessage");
		}
		return ConfirmationDialog.post((Frame) activeDesktop,
				registry.getString("deleteSurrogate"), s, true);
	}

	private boolean isSurrogateUser(final TCComponentUser tcComponentUser,
			final TCComponentUser tcComponentUser2) throws TCException {
		final TCComponent[] referenceValueArray = tcComponentUser2
				.getTCProperty("surrogate_list").getReferenceValueArray();
		for (int i = 0; i < referenceValueArray.length; ++i) {
			final TCComponentUser tcComponentUser3 = (TCComponentUser) referenceValueArray[i];
			if (tcComponentUser3 != null
					&& tcComponentUser3.equals((Object) tcComponentUser)) {
				return true;
			}
		}
		return false;
	}

	private void removeSurrogatesForTaskInboxes(final TCComponent[] array)
			throws TCException {
		boolean b = false;
		final TCComponentUser user = array[0].getSession().getUser();
		if (array.length > 1) {
			b = true;
		}
		int n = 1;
		for (int i = 0; i < array.length; ++i) {
			final TCComponentUser tcComponentUser = (TCComponentUser) array[i]
					.getTCProperty("owning_user").getReferenceValue();
			if (user != tcComponentUser
					&& this.isSurrogateUser(user, tcComponentUser)) {
				int n2 = 2;
				if (b) {
					if (n != 0) {
						n2 = this.ConfirmRemovalOfSurrogates(user,
								tcComponentUser, array[i], false);
					}
					n = 0;
				} else {
					n2 = this.ConfirmRemovalOfSurrogates(user, tcComponentUser,
							array[i], true);
				}
				if (n2 != 2) {
					return;
				}
				tcComponentUser.removeSurrogate(user);
				array[i].delete();
			} else {
				array[i].removeAndDestroy("contents", this.parentCmp);
			}
		}
	}

	private boolean removeSurrogatesForInboxFolder(final TCComponent tcComponent)
			throws TCException {
		boolean b = true;
		final AIFComponentContext[] children = tcComponent
				.getChildren("contents");
		final TCComponentUser user = tcComponent.getSession().getUser();
		if (children != null && children.length >= 1) {
			int n = 1;
			for (int i = 0; i < children.length; ++i) {
				final TCComponentUser tcComponentUser = (TCComponentUser) ((TCComponent) children[i]
						.getComponent()).getTCProperty("owning_user")
						.getReferenceValue();
				if (user != tcComponentUser
						&& this.isSurrogateUser(user, tcComponentUser)) {
					int confirmRemovalOfSurrogates = 2;
					if (n != 0) {
						confirmRemovalOfSurrogates = this
								.ConfirmRemovalOfSurrogates(user,
										tcComponentUser, tcComponent, false);
					}
					n = 0;
					if (confirmRemovalOfSurrogates != 2) {
						b = false;
						break;
					}
					tcComponentUser.removeSurrogate(user);
					b = true;
				}
			}
		}
		return b;
	}

	private void executeByTable() throws TCException {
		final HashMap hashMap = new HashMap();
		for (final Map.Entry<String, List<TCComponent>> entry : this.table
				.entrySet()) {
			final String s = entry.getKey();
			if (s.startsWith("ERROR: ")) {
				continue;
			}
			final List<TCComponent> list = entry.getValue();
			if (list == null) {
				continue;
			}
			if (list.isEmpty()) {
				continue;
			}
			final HashMap<TCComponent, ArrayList<TCComponentFolder>> hashMap2 = new HashMap<TCComponent, ArrayList<TCComponentFolder>>();
			for (final TCComponent tcComponent : list) {
				final ArrayList<TCComponentFolder> list2 = new ArrayList<TCComponentFolder>();
				try {
					AIFComponentContext[] whereReferenced = new AIFComponentContext[0];
					if (this.whereReferencedTable == null
							|| !this.whereReferencedTable
									.containsKey(tcComponent)) {
						whereReferenced = tcComponent.whereReferenced();
					} else {
						final List<AIFComponentContext> list3 = this.whereReferencedTable
								.get(tcComponent);
						if (!list3.isEmpty()) {
							whereReferenced = list3
									.toArray(new AIFComponentContext[list3
											.size()]);
						}
					}
					if (whereReferenced != null && whereReferenced.length > 0) {
						for (final AIFComponentContext aifComponentContext : whereReferenced) {
							if (aifComponentContext.getComponent() instanceof TCComponentFolder) {
								if (!((TCComponentFolder) aifComponentContext
										.getComponent())
										.equals((Object) this.parentCmp)) {
									try {
										((TCComponentFolder) aifComponentContext
												.getComponent()).cutOperation(
												"contents",
												new TCComponent[]{tcComponent},
												false);
										list2.add((TCComponentFolder) aifComponentContext
												.getComponent());
									} catch (TCException ex) {
										List<TCComponent> listtmp = new ArrayList<TCComponent>();
										listtmp.addAll(list2);
										this.pasteBack(listtmp, tcComponent);
//										this.pasteBack(
//												( list2,
//												tcComponent);
										throw ex;
									}
								}
							}
						}
					}
				} catch (TCException ex2) {
				}
				if (!list2.isEmpty()) {
					hashMap2.put(tcComponent, list2);
				}
			}
			final HashMap<Object, ArrayList<TCComponent>> hashMap3 = new HashMap<Object, ArrayList<TCComponent>>();
			final ArrayList<TCComponentType> list4 = new ArrayList<TCComponentType>();
			for (int j = 0; j < list.size(); ++j) {
				final TCComponent tcComponent2 = list.get(j);
				final TCComponentType typeComponent = tcComponent2
						.getTypeComponent();
				final ArrayList<TCComponent> list5 = new ArrayList<TCComponent>();
				if (!hashMap3.containsKey(typeComponent)) {
					list5.add(tcComponent2);
					hashMap3.put(typeComponent, list5);
					list4.add(typeComponent);
					for (int k = j + 1; k < list.size(); ++k) {
						final TCComponent tcComponent3 = list.get(k);
						final TCComponentType typeComponent2 = tcComponent3
								.getTypeComponent();
						if (hashMap3.containsKey(typeComponent2)) {
							list5.add(tcComponent3);
							hashMap3.put(typeComponent2, list5);
						}
					}
				}
			}
			for (int l = 0; l < list4.size(); ++l) {
				boolean b = true;
				final ArrayList<TCComponent> list6 = hashMap3.get(list4.get(l));
				final TCComponent[] array2 = new TCComponent[list6.size()];
				for (int n = 0; n < list6.size(); ++n) {
					array2[n] = (TCComponent) list6.get(n);
				}
				if (array2[0] instanceof TCComponentTaskInBox) {
					this.removeSurrogatesForTaskInboxes(array2);
				} else {
					if (array2[0] instanceof TCComponentFolder) {
						for (int n2 = 0; n2 < array2.length; ++n2) {
							if (array2[0].getType().equalsIgnoreCase(
									"User_Inbox")
									&& !this.removeSurrogatesForInboxFolder(array2[n2])) {
								b = false;
								break;
							}
						}
					}
					if (b) {
						final Map removeAndDestroy = array2[0]
								.getTypeComponent().removeAndDestroy(
										this.parentCmp, s, array2);
						if (removeAndDestroy != null) {
							for (final Object next : removeAndDestroy.keySet()) {
								if (((TCException) removeAndDestroy.get(next))
										.getErrorCode() == 7010) {
									removeAndDestroy.remove(next);
								}
							}
							if (!removeAndDestroy.isEmpty()) {
								hashMap.putAll(removeAndDestroy);
								if (!hashMap2.isEmpty()) {
									for (final Map.Entry<TCComponent, ArrayList<TCComponentFolder>> entry2 : hashMap2
											.entrySet()) {
										final TCComponent tcComponent4 = entry2
												.getKey();
										if (hashMap.containsKey(tcComponent4.getUid())) {
											List<TCComponent> listtmp = new ArrayList<TCComponent>();
											listtmp.addAll(entry2.getValue());
											this.pasteBack(listtmp,tcComponent4);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (!hashMap.isEmpty()) {
			this.storeOperationResult((Object) hashMap);
		}
	}

	private void cutItems(final TCComponent tcComponent,
			final TCComponent tcComponent2, final String s) throws TCException {
		final AbstractTCApplication abstractTCApplication = (AbstractTCApplication) AIFUtility
				.getCurrentApplication();
		final JPanel applicationPanel = abstractTCApplication
				.getApplicationPanel();
		boolean b = true;
		try {
			if (((TCComponentBOMWindow) tcComponent).isValidUid()) {
				if (applicationPanel != null) {
					final Object invokeMethodIfExist = Utilities
							.invokeMethodIfExist((Object) applicationPanel,
									"getCurrentBOMPanel", new Object[0]);
					abstractTCApplication.clearSelection();
					if (((TCComponentBOMWindow) tcComponent).getTopBOMLine()
							.getItem().equals((Object) tcComponent2)
							&& invokeMethodIfExist != null) {
						Utilities.invokeMethodIfExist(invokeMethodIfExist,
								"setDeleteTopItemOnClose", new Object[0]);
						Utilities.invokeMethodIfExist(
								(Object) applicationPanel, "closeBOMPanel",
								new Object[]{invokeMethodIfExist});
						b = false;
					} else {
						this.cutBOMLine(((TCComponentBOMWindow) tcComponent)
								.getTopBOMLine(),
								(TCComponentItem) tcComponent2);
						tcComponent.save();
					}
				} else {
					this.cutBOMLine(((TCComponentBOMWindow) tcComponent)
							.getTopBOMLine(), (TCComponentItem) tcComponent2);
					tcComponent.save();
				}
			}
			final AIFComponentContext[] whereReferenced = tcComponent2
					.whereReferenced();
			final ArrayList<TCComponent> list = new ArrayList<TCComponent>();
			for (final AIFComponentContext aifComponentContext : whereReferenced) {
				if (aifComponentContext.getComponent() instanceof TCComponentFolder) {
					try {
						((TCComponentFolder) aifComponentContext.getComponent())
								.cutOperation("contents",
										new TCComponent[]{tcComponent2});
						list.add((TCComponentFolder) aifComponentContext
								.getComponent());
					} catch (TCException ex) {
						this.pasteBack(list, tcComponent2);
						throw ex;
					}
				}
			}
			try {
				if (b) {
					tcComponent2.delete();
				}
			} catch (TCException ex2) {
				this.pasteBack(list, tcComponent2);
				throw ex2;
			}
		} catch (Exception ex3) {
			DeleteOperation.logger.error((Object) ex3.getClass().getName(),
					(Throwable) ex3);
			try {
				if (tcComponent != null && tcComponent.isValidUid()) {
					tcComponent.save();
					tcComponent.refresh();
				}
			} catch (Exception ex4) {
				DeleteOperation.logger.error((Object) ex4.getClass().getName(),
						(Throwable) ex4);
			}
			if (ex3 instanceof TCException) {
				throw (TCException) ex3;
			}
			throw new TCException(ex3);
		}
		if (tcComponent != null && tcComponent.isValidUid()) {
			tcComponent.save();
		}
	}

	private void cutBOMLine(final TCComponentBOMLine tcComponentBOMLine,
			final TCComponentItem tcComponentItem) throws TCException {
		if (tcComponentBOMLine == null) {
			return;
		}
		for (final AIFComponentContext aifComponentContext : tcComponentBOMLine
				.getChildren()) {
			if (aifComponentContext.getComponent() instanceof TCComponentBOMLine) {
				final TCComponentBOMLine tcComponentBOMLine2 = (TCComponentBOMLine) aifComponentContext
						.getComponent();
				if (tcComponentBOMLine2.isValidUid()) {
					if (tcComponentBOMLine2.getItem().equals(
							(Object) tcComponentItem)) {
						tcComponentBOMLine2.cut();
					} else {
						this.cutBOMLine(tcComponentBOMLine2, tcComponentItem);
					}
				}
			}
		}
	}

	public void setDeleteAllSequencesFlag(final boolean deleteAllSequencesFlag) {
		this.deleteAllSequencesFlag = deleteAllSequencesFlag;
	}

	public void deleteThisSequence(final TCComponent inputObject)
			throws TCException {
		final DataManagementService service = DataManagementService
				.getService(inputObject.getSession());
		final DataManagement.PurgeSequencesInfo[] array = {new DataManagement.PurgeSequencesInfo()};
		array[0].inputObject = inputObject;
		array[0].validateLatestFlag = true;
		SoaUtil.checkPartialErrors((ServiceData) service.purgeSequences(array));
	}

	private void pasteBack(final List<TCComponent> list,
			final TCComponent tcComponent) {
		if (list != null && !list.isEmpty()) {
			for (final TCComponent tcComponent2 : list) {
				final AIFComponentContext aifComponentContext = new AIFComponentContext(
						(InterfaceAIFComponent) tcComponent2,
						(InterfaceAIFComponent) tcComponent,
						(Object) "contents");
				try {
					tcComponent2.pasteOperation(aifComponentContext);
				} catch (TCException ex) {
				}
			}
		}
	}

	static {
		logger = Logger.getLogger((Class) DeleteOperation.class);
	}
}