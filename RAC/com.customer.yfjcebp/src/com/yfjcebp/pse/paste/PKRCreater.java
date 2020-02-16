/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		  
 
 #																			   
 #=============================================================================
 # File name: PKRCreater.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-4-17	MengYaWei  	Create PKR check item		 								   
 #=============================================================================
 */
package com.yfjcebp.pse.paste;

import java.util.Vector;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentCfgAttachmentLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;

public class PKRCreater {
	private Registry reg = Registry.getRegistry("com.yfjcebp.pse.paste.paste");
	// class
	// this
	// private String object_name = "";

	public final String relationSMTE = "JCI6_SMTE"; // SMTE关系
	public final String relationSMTET = "JCI6_SMTET"; // SMTET关系
	public final String propFunArea = "jci6_FuncArea"; // 功能领域属性名称
	public final String PEFFunAreaRevType = "JCI6_PFARevision"; // 功能领域版本类型
	public final String PKRTItemRevType = "JCI6_PKRTRevision"; // PKR检查项模板版本类型
	public final String PKRItemType = "JCI6_PKR"; // PKR检查项类型
	public final String DRType = "JCI6_DesignReq"; // 设计要求类型
	public final String DRTRevType = "JCI6_DRTRevision"; // 设计要求模板版本类型
	private TCComponentItemRevision PEFFunAreaRevision = null;
	private TCComponentItemRevision[] PKRTRevisions = null;
	private TCSession tcSession = null;

	// 5.8修改
	private TCComponentItemRevision PKRTRevision_target = null;
	private TCComponentItemRevision[] DRTRevisions = null;

	// 6.3修改
	private String pkrLocation = "";

	public PKRCreater(InterfaceAIFComponent interfaceaifcomponent,
			TCComponent[] atccomponent) throws TCException {
		PEFFunAreaRevision = ((TCComponentBOMLine) interfaceaifcomponent)
				.getItemRevision();
		tcSession = PEFFunAreaRevision.getSession();
		PKRTRevisions = new TCComponentItemRevision[atccomponent.length];
		for (int i = 0; i < atccomponent.length; i++) {
			TCComponent tempCpt = atccomponent[i];
			if (tempCpt instanceof TCComponentItem) {
				PKRTRevisions[i] = ((TCComponentItem) tempCpt)
						.getLatestItemRevision();
			} else if (tempCpt instanceof TCComponentBOMLine) {
				PKRTRevisions[i] = ((TCComponentBOMLine) tempCpt)
						.getItemRevision();
			} else {
				PKRTRevisions[i] = (TCComponentItemRevision) tempCpt;
			}
		}
	}

	public PKRCreater(InterfaceAIFComponent interfaceaifcomponent,
			AIFComponentContext[] aaifcomponentcontext, String pkrLocation)
			throws TCException {
		System.out.println("-------coming the first Construction FUNC---！！！");

		this.pkrLocation = pkrLocation;

		PEFFunAreaRevision = ((TCComponentBOMLine) interfaceaifcomponent)
				.getItemRevision();

		tcSession = PEFFunAreaRevision.getSession();
		PKRTRevisions = new TCComponentItemRevision[aaifcomponentcontext.length];
		for (int i = 0; i < aaifcomponentcontext.length; i++) {
			TCComponent tempCpt = (TCComponent) aaifcomponentcontext[i]
					.getComponent();
			if (tempCpt instanceof TCComponentItem) {
				PKRTRevisions[i] = ((TCComponentItem) tempCpt)
						.getLatestItemRevision();
			} else if (tempCpt instanceof TCComponentBOMLine) {
				PKRTRevisions[i] = ((TCComponentBOMLine) tempCpt)
						.getItemRevision();
			} else {
				PKRTRevisions[i] = (TCComponentItemRevision) tempCpt;
			}
		}
	}

	public TCSession getTCSession() {
		return tcSession;
	}

	public TCComponentItemRevision[] getDRTRevisions() {
		return DRTRevisions;
	}

	// 5.8修改
	public PKRCreater(InterfaceAIFComponent interfaceaifcomponent,
			AIFComponentContext[] aaifcomponentcontext) throws TCException {

		System.out.println("-------coming to second construction FUNC---！！！");
		// 目的地对象
		PKRTRevision_target = (TCComponentItemRevision) ((TCComponentCfgAttachmentLine) interfaceaifcomponent)
				.getRelatedComponent("al_object");
		// TCComponent component = ((TCComponentBOMLine)
		// interfaceaifcomponent).getRelatedComponent("al_object");

		tcSession = PKRTRevision_target.getSession();
		DRTRevisions = new TCComponentItemRevision[aaifcomponentcontext.length];

		for (int i = 0; i < aaifcomponentcontext.length; i++) {
			TCComponent tempCpt = (TCComponent) aaifcomponentcontext[i]
					.getComponent();
			if (tempCpt instanceof TCComponentItem) {
				DRTRevisions[i] = ((TCComponentItem) tempCpt)
						.getLatestItemRevision();
			} else if (tempCpt instanceof TCComponentBOMLine) {
				DRTRevisions[i] = ((TCComponentBOMLine) tempCpt)
						.getItemRevision();
			} else {
				DRTRevisions[i] = (TCComponentItemRevision) tempCpt;
			}
		}
	}

	public TCComponentItemRevision getPEFFunAreaRevision() {
		return PEFFunAreaRevision;
	}

	// 5.8修改
	public TCComponentItemRevision getPKRRevision() {
		return PKRTRevision_target;
	}

	/*
	 * 检验源版本对象序列中是否全部是JCI6_PKRTRevision类型， 3中情况： 全部是：ALL 全部不是：NONE 部分是:SOME
	 */
	public String isALLPKRTRevisionType() {
		boolean hasPKRTRevision = false;
		boolean hastOtherRevision = false;
		String rtnStr = null;
		for (int i = 0; i < PKRTRevisions.length; i++) {
			if (PKRTItemRevType.equals(PKRTRevisions[i].getType())) {
				hasPKRTRevision = true;
			} else {
				hastOtherRevision = true;
			}
		}
		if (hasPKRTRevision && hastOtherRevision) {
			rtnStr = "SOME";
		} else if (hasPKRTRevision) {
			rtnStr = "ALL";
		} else if (hastOtherRevision) {
			rtnStr = "NONE";
		}
		return rtnStr;
	}

	/*
	 * 5.8修改 检验源版本对象序列中是否全部是JCI6_DRTRevision类型， 3中情况： 全部是：ALL 全部不是：NONE 部分是:SOME
	 */
	public String isALLDRTRevisionType() {
		boolean hasDRTRevision = false;
		boolean hastOtherRevision = false;
		String rtnStr = null;
		for (int i = 0; i < DRTRevisions.length; i++) {
			if (DRTRevType.equals(DRTRevisions[i].getType())) {
				hasDRTRevision = true;
			} else {
				hastOtherRevision = true;
			}
		}

		if (hasDRTRevision && hastOtherRevision) {
			rtnStr = "SOME";
		} else if (hasDRTRevision) {
			rtnStr = "ALL";
		} else if (hastOtherRevision) {
			rtnStr = "NONE";
		}
		return rtnStr;
	}

	/*
	 * 检验"PEFFunAreaRevision"PEF功能领域版本对象的"功能领域"
	 * 和"PKRTRevision"PKR检查项模板版本对象的"所属功能领域"是否相同
	 */
	public String VerifyFuncArea() throws TCException {
		String errMsg = "";
		String PEFFunArea = PEFFunAreaRevision.getProperty(propFunArea);// PEFFunAreaRevision的属性jci6_FuncArea
		if ((PEFFunArea != null) && (!"".equals(PEFFunArea))) {
			for (int i = 0; i < PKRTRevisions.length; i++) {
				TCComponentItemRevision tempRev = PKRTRevisions[i];
				String PKRTFunArea = tempRev.getProperty(propFunArea);// PKRTRevision的属性jci6_FuncArea
				if (!PEFFunArea.equals(PKRTFunArea)) {
					errMsg = errMsg + tempRev.getProperty("item_id") + "_"
							+ tempRev.getProperty("item_revision_id") + "\n";
				}
			}
			if (!"".equals(errMsg)) {
				errMsg += reg.getString("hasDiffFunArea");
				return errMsg;
			} else {
				return null;
			}
		} else {
			errMsg = PEFFunAreaRevision.getProperty("item_id") + "_"
					+ PEFFunAreaRevision.getProperty("item_revision_id")
					+ reg.getString("FunAreaUnset");
			return errMsg;
		}
	}

	/*
	 * 5.8修改 检验"PEFFunAreaRevision"PEF功能领域版本对象的"功能领域"
	 * 和"PKRTRevision"PKR检查项模板版本对象的"所属功能领域"是否相同
	 */
	public String VerifyFuncAreaforDRT() throws TCException {
		String errMsg = "";
		String PKRFunArea = PKRTRevision_target.getProperty(propFunArea);// PKRTRevision_target的属性jci6_FuncArea
		if ((PKRFunArea != null) && (!"".equals(PKRFunArea))) {
			for (int i = 0; i < DRTRevisions.length; i++) {
				TCComponentItemRevision tempRev = DRTRevisions[i];
				String DRTFunArea = tempRev.getProperty(propFunArea);// PKRTRevision的属性jci6_FuncArea
				if (!PKRFunArea.equals(DRTFunArea)) {
					errMsg = errMsg + tempRev.getProperty("item_id") + "_"
							+ tempRev.getProperty("item_revision_id") + "\n";
				}
			}
			if (!"".equals(errMsg)) {
				errMsg += reg.getString("hasDiffFunArea");
				return errMsg;
			} else {
				return null;
			}
		} else {
			errMsg = PKRTRevision_target.getProperty("item_id") + "_"
					+ PKRTRevision_target.getProperty("item_revision_id")
					+ reg.getString("FunAreaUnset");
			return errMsg;
		}
	}

	/*
	 * 5.8修改---- 基于DRTRevision“PKR检查项模板”版本对象创建DR对象
	 */
	public void baseOnDRTRevisionsCreateDR() throws TCException {

		System.out.println("----- 基于DRTRevision“PKR检查项模板”版本对象创建DR对象====");

		System.out.println("----- =================================-------------");
		TCComponent[] tempReferences = PKRTRevision_target
				.getReferenceListProperty("JCI6_SDT");// 获得“SDT责任工程师”序列
		TCComponentUser SMTELeader = (TCComponentUser) PKRTRevision_target
				.getReferenceProperty("jci6_SMTELeader");// SMTELeader

		if (SMTELeader != null) {
			openByPass();
			grantPrivilege(tcSession, PKRTRevision_target, SMTELeader,
					new String[] { "READ", "WRITE", "DELETE" });
			closeByPass();
		}

		TCComponentUser SDT_user = (TCComponentUser) PKRTRevision_target
				.getRelatedComponent("owning_user");

		System.out.println("SMTELeader==============="+SMTELeader);
		System.out.println("SDT_user==============="+SDT_user);
//		if (tempReferences != null && tempReferences.length > 0) {
//			for (int i = 0; i < tempReferences.length; i++) {
//				if (tempReferences[i] instanceof TCComponentUser) {// 如果类型为user
//					SDT_user = (TCComponentUser) tempReferences[i];
//					openByPass();
//					grantPrivilege(tcSession, PKRTRevision_target, SDT_user,
//							new String[] { "READ", "WRITE", "DELETE" });
//					closeByPass();
//					break;
//				}
//			}
//		}

		if (DRTRevisions != null && DRTRevisions.length > 0) {
			baseOnDRERevisionCreateDesignReq(tcSession, getPKRRevision(),
					DRTRevisions, SDT_user, SMTELeader);
		}

	}

	/*
	 * 基于PKRTRevision“PKR检查项模板”版本对象创建PKRItem“PKR检查项”Item对象
	 */
	public void baseOnPKRTRevisionsCreatePKRItems() throws TCException {
		Vector<TCComponent> PKRItems = new Vector<TCComponent>();

		System.out
				.println("----基于PKRTRevision“PKR检查项模板”版本对象创建PKRItem“PKR检查项”Item对象");

		TCComponent[] tempReferences = PEFFunAreaRevision
				.getReferenceListProperty("JCI6_SDT");// 获得“SDT责任工程师”序列
		TCComponentUser SMTELeader = (TCComponentUser) PEFFunAreaRevision
				.getReferenceProperty("jci6_SMTELeader");// SMTELeader
		if (SMTELeader != null) {
			openByPass();
			grantPrivilege(tcSession, PEFFunAreaRevision, SMTELeader,
					new String[] { "READ", "WRITE", "DELETE" });
			closeByPass();
		}
		TCComponentUser SDT_user = null;

		if (tempReferences != null && tempReferences.length > 0) {
			for (int i = 0; i < tempReferences.length; i++) {
				if (tempReferences[i] instanceof TCComponentUser) {// 如果类型为user
					SDT_user = (TCComponentUser) tempReferences[i];
					openByPass();
					grantPrivilege(tcSession, PEFFunAreaRevision, SDT_user,
							new String[] { "READ", "WRITE", "DELETE" });
					closeByPass();
					break;
				}
			}
		}

		TCComponentItemType PKRItemType = (TCComponentItemType) tcSession
				.getTypeComponent(this.PKRItemType);
		for (int i = 0; i < PKRTRevisions.length; i++) {
			TCComponentItemRevision PKRTRevision = PKRTRevisions[i];

			// 5.3修改
			String object_name = PKRTRevision.getProperty("object_name");

			openByPass();
			TCComponentItem PKRItem = PKRItemType.create(
					PKRItemType.getNewID(), "A", this.PKRItemType, object_name,
					"PKR", null);
			closeByPass();

			if (PKRItem != null) {

				// 5.27修改---把PEF功能领域的项目，指派给pkr的item以及itemrevision
				TCComponent[] relatedComponents = PEFFunAreaRevision
						.getRelatedComponents("project_list");//project_list
				System.out
						.println("----------------5.27修改---------------------------"
								+ relatedComponents[0].getType());
				openByPass();
				relatedComponents[0].add("project_data", PKRItem);
				closeByPass();
				System.out
						.println("=========================5.27修改---把PEF功能领域的项目，指派给pkr的item以及itemrevision=================");

				if (SMTELeader != null) {
					grantPrivilege(tcSession, PKRItem, SMTELeader,
							new String[] { "READ", "WRITE", "DELETE" });// 增加SMTELeader的读权限
				}

				if (SDT_user != null) {

					if ((TCComponentUser) (PKRItem
							.getReferenceProperty("owning_user")) != SDT_user) {
						openByPass();
						PKRItem.changeOwner(SDT_user,
								(TCComponentGroup) SDT_user
										.getReferenceProperty("default_group"));// 修改“PKR检查项”对象的Owner
						closeByPass();
					}
					grantPrivilege(tcSession, PKRItem, SDT_user, new String[] {
							"READ", "WRITE", "DELETE" });//

				}

				TCComponentItemRevision PKRRevision = PKRItem
						.getLatestItemRevision();// 获得当前新建PKRItem的最新版本对象
				if (SDT_user != null) {
					if ((TCComponentUser) (PKRRevision
							.getReferenceProperty("owning_user")) != SDT_user) {

						openByPass();
						PKRRevision.changeOwner(SDT_user,
								(TCComponentGroup) SDT_user
										.getReferenceProperty("default_group"));// 修改“PKR检查项”版本对象的Owner
						closeByPass();
					}
				}

				// 6.3修改
				openByPass();
				PKRRevision.setProperty("jci6_Location", pkrLocation);
				closeByPass();

				openByPass();
				PKRRevision.add(relationSMTET, PKRTRevision);// PKRRevision与PKRTRevision的关系SMTE_Template
				closeByPass();

				TCComponent[] DRTRevisions = PKRTRevision
						.getReferenceListProperty(relationSMTET);// PKRTRevision对象与DRTRevision版本对象之间的关系SMTE_Template
				if (DRTRevisions != null && DRTRevisions.length > 0) {
					baseOnDRERevisionCreateDesignReq(tcSession, PKRRevision,
							DRTRevisions, SDT_user, SMTELeader);
				}
				// openByPass();
				// PEFFunAreaRevision.add(relationSMTE, PKRItem);//
				// PEFFunAreaRevision与PKRItem之间的关系SMTE
				// closeByPass();

				openByPass();
				PKRItems.add(PKRItem);
				closeByPass();

				// 5.8修改---要测试一下
				TCComponent[] tcComponents = PKRTRevision
						.getRelatedComponents("IMAN_manifestation");
				for (TCComponent component : tcComponents) {
					// saveas到DR下面
					System.out.println("IMAN_manifestation的集合名字类型========="
							+ component.getType() + " {.} "
							+ component.getClass().toString());
					if (component instanceof TCComponentDataset) {
						TCComponentDataset dataset = (TCComponentDataset) component;

						openByPass();
						TCComponentDataset saveAs = dataset.saveAs(dataset
								.getProperty("object_name"));
						closeByPass();

						openByPass();
						PKRRevision.add("IMAN_manifestation", saveAs);
						closeByPass();
					}
				}

				TCComponent[] tcComponents2 = PKRTRevision
						.getRelatedComponents("IMAN_specification");
				for (TCComponent component : tcComponents2) {
					System.out.println("IMAN_specification的集合名字类型========="
							+ component.getType() + " {.} "
							+ component.getClass().toString());
					if (component instanceof TCComponentDataset) {
						openByPass();
						PKRRevision.add("IMAN_specification", component);
						closeByPass();
					}
				}

			} else {
				MessageBox.post(reg.getString("CreatePKRFailed"), "ERROR",
						MessageBox.ERROR);
				return;
			}
		}

		openByPass();
		TCComponent[] PKRTSets = PEFFunAreaRevision
				.getRelatedComponents("FND_TraceLink");// PEFFunAreaRevision版本与PKRSet检查项集之间的关系FND_TraceLink
		closeByPass();

		if (PKRTSets != null && PKRTSets.length > 0) {

			for (int j = 0; j < PKRTSets.length; j++) {
				if (PKRTSets[j] instanceof TCComponentFolder) {// 检查项集--JCI6_PKRTSet
																// 继承了Folder
					if (PKRItems.size() > 0) {
						TCComponent[] tcp = new TCComponent[PKRItems.size()];
						openByPass();
						PKRTSets[j].add("contents", PKRItems.toArray(tcp));
						closeByPass();
						if (SMTELeader != null) {
							// grantPrivilege(tcSession, PKRTSets[j],
							// SMTELeader);

							// 5.28修改---把SMTELeader转成owner
							if ((TCComponentUser) (PKRTSets[j]
									.getReferenceProperty("owning_user")) != SMTELeader) {
								openByPass();
								PKRTSets[j]
										.changeOwner(
												SMTELeader,
												(TCComponentGroup) SMTELeader
														.getReferenceProperty("default_group"));
								closeByPass();
							}

						}
						if (SDT_user != null) {
							grantPrivilege(tcSession, PKRTSets[j], SDT_user,
									new String[] { "READ" });
						}
					}
				}
			}
		}
	}

	/*
	 * 基于DRTRevision"设计要求模板"版本对象创建DesignReq“设计要求”对象
	 */
	private void baseOnDRERevisionCreateDesignReq(TCSession tcSession,
			TCComponentItemRevision PKRRevision, TCComponent[] DRTRevisions,
			TCComponentUser SDT_user, TCComponentUser SMTE_Leader)
			throws TCException {

		System.out.println("-----基于DRTRevision设计要求模板版本对象创建DesignReq“设计要求”对象");

		for (int i = 0; i < DRTRevisions.length; i++) {
			String DRTRevisionType = DRTRevisions[i].getType();
			if (DRTRevType.equals(DRTRevisionType)) {
				TCComponentItemRevision DRTRevision = (TCComponentItemRevision) DRTRevisions[i];

				// 5.3修改--得到DRTRevision的name
				String object_name = DRTRevision.getProperty("object_name");
				TCComponent DesignReq = null;

				try {
					DataManagementService dmService = DataManagementService
							.getService(tcSession);
					com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
					createIn[0] = new DataManagement.CreateIn();
					createIn[0].data.boName = DRType;

					// 5.4修改名字用revision的名字
					createIn[0].data.stringProps
							.put("object_name", object_name);
					createIn[0].data.stringProps.put("object_desc",
							"Design Requirement");
					CreateResponse responese = dmService
							.createObjects(createIn);
					DesignReq = responese.output[0].objects[0];

					// 5.27---PEFFunAreaRevision修改为PKRTRevision_target
					TCComponent relatedComponent = PKRRevision
							.getRelatedComponent("project_list");//project_list
					
					System.out
							.println("==========================5.27修改DesignRequire=================================================="
									+ relatedComponent);
					openByPass();
					relatedComponent.add("project_data", DesignReq);
					closeByPass();
					System.out
							.println("=========================5.27修改---把PEF功能领域的项目，指派给DR!!!!=================");

					if (SMTE_Leader != null) {
						
						System.out.println("=========================SMTE_Leader != null   !!!!=================");
						
						openByPass();
						grantPrivilege(tcSession, DesignReq, SMTE_Leader,
								new String[] { "READ", "WRITE", "DELETE" });
						closeByPass();
					}
					if (SDT_user != null) {

						if ((TCComponentUser) (DesignReq
								.getReferenceProperty("owning_user")) != SDT_user) {

							System.out.println("=========================DesignReq changeOwner   !!!!=================");
							
							openByPass();
							DesignReq
									.changeOwner(
											SDT_user,
											(TCComponentGroup) SDT_user
													.getReferenceProperty("default_group"));// 修改“设计要求”对象的Owner
							closeByPass();
						}
						openByPass();
						grantPrivilege(tcSession, DesignReq, SDT_user,
								new String[] { "READ", "WRITE", "DELETE" });
						closeByPass();
					}
					
					
					openByPass();
					DesignReq.add(relationSMTET, DRTRevision);// “设计要求”与“设计要求模板”版本之间的关系SMTE_Template
					closeByPass();

					openByPass();
					PKRRevision.add(relationSMTE, DesignReq); // PKRRevision与“设计要求”之间的关系SMTE、
					closeByPass();

					// 5.8修改---要测试一下
					TCComponent[] tcComponents = DRTRevision
							.getRelatedComponents("IMAN_manifestation");
					for (TCComponent component : tcComponents) {
						// saveas到DR下面
						System.out.println("IMAN_manifestation的集合名字类型========="
								+ component.getType() + " {.} "
								+ component.getClass().toString());
						if (component instanceof TCComponentDataset) {
							TCComponentDataset dataset = (TCComponentDataset) component;

							openByPass();
							TCComponentDataset saveAs = dataset.saveAs(dataset
									.getProperty("object_name"));
							closeByPass();

							openByPass();
							DesignReq.add("IMAN_manifestation", saveAs);
							closeByPass();
						}
					}

					TCComponent[] tcComponents2 = DRTRevision
							.getRelatedComponents("IMAN_specification");
					for (TCComponent component : tcComponents2) {
						System.out.println("IMAN_specification的集合名字类型========="
								+ component.getType() + " {.} "
								+ component.getClass().toString());
						if (component instanceof TCComponentDataset) {
							openByPass();
							DesignReq.add("IMAN_specification", component);
							closeByPass();
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// 授予权限
	public void grantPrivilege(TCSession tcSession, TCComponent tccomponent,
			TCComponentUser user, String[] as2) throws TCException {
		TCAccessControlService accessControlService = tcSession
				.getTCAccessControlService();
		// 5.28修改权限增加写和删除的权限
		// String[] as2 = new String[] { "READ", "WRITE", "DELETE" };

		if (tccomponent instanceof TCComponentItem) {
			TCComponentItem tempItem = (TCComponentItem) tccomponent;
			openByPass();
			accessControlService.grantPrivilege(tempItem, user, as2);
			closeByPass();

			TCComponentItemRevision[] tempRevs = tempItem
					.getReleasedItemRevisions();

			if (tempRevs != null && tempRevs.length > 0) {
				for (int i = 0; i < tempRevs.length; i++) {
					openByPass();
					accessControlService.grantPrivilege(tempRevs[i], user, as2);
					closeByPass();
				}
			}

			TCComponentItemRevision lastedRev = tempItem
					.getLatestItemRevision();
			openByPass();
			accessControlService.grantPrivilege(lastedRev, user, as2);
			closeByPass();

		} else if (tccomponent instanceof TCComponentItemRevision) {
			TCComponentItem tempItem = ((TCComponentItemRevision) tccomponent)
					.getItem();
			openByPass();
			accessControlService.grantPrivilege(tempItem, user, as2);
			closeByPass();

			TCComponentItemRevision[] tempRevs = tempItem
					.getReleasedItemRevisions();
			if (tempRevs != null && tempRevs.length > 0) {
				for (int i = 0; i < tempRevs.length; i++) {
					openByPass();
					accessControlService.grantPrivilege(tempRevs[i], user, as2);
					closeByPass();
				}
			}
			TCComponentItemRevision lastedRev = tempItem
					.getLatestItemRevision();
			openByPass();
			accessControlService.grantPrivilege(lastedRev, user, as2);
			closeByPass();
		} else {
			openByPass();
			accessControlService.grantPrivilege(tccomponent, user, as2);
			closeByPass();
		}
	}

	// 开旁路
	private void openByPass() {
		try {
			tcSession.getUserService().call("open_or_close_pass",
					new Object[] { 1 });

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 关闭旁路
	private void closeByPass() {
		try {
			tcSession.getUserService().call("open_or_close_pass",
					new Object[] { 0 });
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
