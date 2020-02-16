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

	public final String relationSMTE = "JCI6_SMTE"; // SMTE��ϵ
	public final String relationSMTET = "JCI6_SMTET"; // SMTET��ϵ
	public final String propFunArea = "jci6_FuncArea"; // ����������������
	public final String PEFFunAreaRevType = "JCI6_PFARevision"; // ��������汾����
	public final String PKRTItemRevType = "JCI6_PKRTRevision"; // PKR�����ģ��汾����
	public final String PKRItemType = "JCI6_PKR"; // PKR���������
	public final String DRType = "JCI6_DesignReq"; // ���Ҫ������
	public final String DRTRevType = "JCI6_DRTRevision"; // ���Ҫ��ģ��汾����
	private TCComponentItemRevision PEFFunAreaRevision = null;
	private TCComponentItemRevision[] PKRTRevisions = null;
	private TCSession tcSession = null;

	// 5.8�޸�
	private TCComponentItemRevision PKRTRevision_target = null;
	private TCComponentItemRevision[] DRTRevisions = null;

	// 6.3�޸�
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
		System.out.println("-------coming the first Construction FUNC---������");

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

	// 5.8�޸�
	public PKRCreater(InterfaceAIFComponent interfaceaifcomponent,
			AIFComponentContext[] aaifcomponentcontext) throws TCException {

		System.out.println("-------coming to second construction FUNC---������");
		// Ŀ�ĵض���
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

	// 5.8�޸�
	public TCComponentItemRevision getPKRRevision() {
		return PKRTRevision_target;
	}

	/*
	 * ����Դ�汾�����������Ƿ�ȫ����JCI6_PKRTRevision���ͣ� 3������� ȫ���ǣ�ALL ȫ�����ǣ�NONE ������:SOME
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
	 * 5.8�޸� ����Դ�汾�����������Ƿ�ȫ����JCI6_DRTRevision���ͣ� 3������� ȫ���ǣ�ALL ȫ�����ǣ�NONE ������:SOME
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
	 * ����"PEFFunAreaRevision"PEF��������汾�����"��������"
	 * ��"PKRTRevision"PKR�����ģ��汾�����"������������"�Ƿ���ͬ
	 */
	public String VerifyFuncArea() throws TCException {
		String errMsg = "";
		String PEFFunArea = PEFFunAreaRevision.getProperty(propFunArea);// PEFFunAreaRevision������jci6_FuncArea
		if ((PEFFunArea != null) && (!"".equals(PEFFunArea))) {
			for (int i = 0; i < PKRTRevisions.length; i++) {
				TCComponentItemRevision tempRev = PKRTRevisions[i];
				String PKRTFunArea = tempRev.getProperty(propFunArea);// PKRTRevision������jci6_FuncArea
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
	 * 5.8�޸� ����"PEFFunAreaRevision"PEF��������汾�����"��������"
	 * ��"PKRTRevision"PKR�����ģ��汾�����"������������"�Ƿ���ͬ
	 */
	public String VerifyFuncAreaforDRT() throws TCException {
		String errMsg = "";
		String PKRFunArea = PKRTRevision_target.getProperty(propFunArea);// PKRTRevision_target������jci6_FuncArea
		if ((PKRFunArea != null) && (!"".equals(PKRFunArea))) {
			for (int i = 0; i < DRTRevisions.length; i++) {
				TCComponentItemRevision tempRev = DRTRevisions[i];
				String DRTFunArea = tempRev.getProperty(propFunArea);// PKRTRevision������jci6_FuncArea
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
	 * 5.8�޸�---- ����DRTRevision��PKR�����ģ�塱�汾���󴴽�DR����
	 */
	public void baseOnDRTRevisionsCreateDR() throws TCException {

		System.out.println("----- ����DRTRevision��PKR�����ģ�塱�汾���󴴽�DR����====");

		System.out.println("----- =================================-------------");
		TCComponent[] tempReferences = PKRTRevision_target
				.getReferenceListProperty("JCI6_SDT");// ��á�SDT���ι���ʦ������
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
//				if (tempReferences[i] instanceof TCComponentUser) {// �������Ϊuser
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
	 * ����PKRTRevision��PKR�����ģ�塱�汾���󴴽�PKRItem��PKR����Item����
	 */
	public void baseOnPKRTRevisionsCreatePKRItems() throws TCException {
		Vector<TCComponent> PKRItems = new Vector<TCComponent>();

		System.out
				.println("----����PKRTRevision��PKR�����ģ�塱�汾���󴴽�PKRItem��PKR����Item����");

		TCComponent[] tempReferences = PEFFunAreaRevision
				.getReferenceListProperty("JCI6_SDT");// ��á�SDT���ι���ʦ������
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
				if (tempReferences[i] instanceof TCComponentUser) {// �������Ϊuser
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

			// 5.3�޸�
			String object_name = PKRTRevision.getProperty("object_name");

			openByPass();
			TCComponentItem PKRItem = PKRItemType.create(
					PKRItemType.getNewID(), "A", this.PKRItemType, object_name,
					"PKR", null);
			closeByPass();

			if (PKRItem != null) {

				// 5.27�޸�---��PEF�����������Ŀ��ָ�ɸ�pkr��item�Լ�itemrevision
				TCComponent[] relatedComponents = PEFFunAreaRevision
						.getRelatedComponents("project_list");//project_list
				System.out
						.println("----------------5.27�޸�---------------------------"
								+ relatedComponents[0].getType());
				openByPass();
				relatedComponents[0].add("project_data", PKRItem);
				closeByPass();
				System.out
						.println("=========================5.27�޸�---��PEF�����������Ŀ��ָ�ɸ�pkr��item�Լ�itemrevision=================");

				if (SMTELeader != null) {
					grantPrivilege(tcSession, PKRItem, SMTELeader,
							new String[] { "READ", "WRITE", "DELETE" });// ����SMTELeader�Ķ�Ȩ��
				}

				if (SDT_user != null) {

					if ((TCComponentUser) (PKRItem
							.getReferenceProperty("owning_user")) != SDT_user) {
						openByPass();
						PKRItem.changeOwner(SDT_user,
								(TCComponentGroup) SDT_user
										.getReferenceProperty("default_group"));// �޸ġ�PKR���������Owner
						closeByPass();
					}
					grantPrivilege(tcSession, PKRItem, SDT_user, new String[] {
							"READ", "WRITE", "DELETE" });//

				}

				TCComponentItemRevision PKRRevision = PKRItem
						.getLatestItemRevision();// ��õ�ǰ�½�PKRItem�����°汾����
				if (SDT_user != null) {
					if ((TCComponentUser) (PKRRevision
							.getReferenceProperty("owning_user")) != SDT_user) {

						openByPass();
						PKRRevision.changeOwner(SDT_user,
								(TCComponentGroup) SDT_user
										.getReferenceProperty("default_group"));// �޸ġ�PKR�����汾�����Owner
						closeByPass();
					}
				}

				// 6.3�޸�
				openByPass();
				PKRRevision.setProperty("jci6_Location", pkrLocation);
				closeByPass();

				openByPass();
				PKRRevision.add(relationSMTET, PKRTRevision);// PKRRevision��PKRTRevision�Ĺ�ϵSMTE_Template
				closeByPass();

				TCComponent[] DRTRevisions = PKRTRevision
						.getReferenceListProperty(relationSMTET);// PKRTRevision������DRTRevision�汾����֮��Ĺ�ϵSMTE_Template
				if (DRTRevisions != null && DRTRevisions.length > 0) {
					baseOnDRERevisionCreateDesignReq(tcSession, PKRRevision,
							DRTRevisions, SDT_user, SMTELeader);
				}
				// openByPass();
				// PEFFunAreaRevision.add(relationSMTE, PKRItem);//
				// PEFFunAreaRevision��PKRItem֮��Ĺ�ϵSMTE
				// closeByPass();

				openByPass();
				PKRItems.add(PKRItem);
				closeByPass();

				// 5.8�޸�---Ҫ����һ��
				TCComponent[] tcComponents = PKRTRevision
						.getRelatedComponents("IMAN_manifestation");
				for (TCComponent component : tcComponents) {
					// saveas��DR����
					System.out.println("IMAN_manifestation�ļ�����������========="
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
					System.out.println("IMAN_specification�ļ�����������========="
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
				.getRelatedComponents("FND_TraceLink");// PEFFunAreaRevision�汾��PKRSet����֮��Ĺ�ϵFND_TraceLink
		closeByPass();

		if (PKRTSets != null && PKRTSets.length > 0) {

			for (int j = 0; j < PKRTSets.length; j++) {
				if (PKRTSets[j] instanceof TCComponentFolder) {// ����--JCI6_PKRTSet
																// �̳���Folder
					if (PKRItems.size() > 0) {
						TCComponent[] tcp = new TCComponent[PKRItems.size()];
						openByPass();
						PKRTSets[j].add("contents", PKRItems.toArray(tcp));
						closeByPass();
						if (SMTELeader != null) {
							// grantPrivilege(tcSession, PKRTSets[j],
							// SMTELeader);

							// 5.28�޸�---��SMTELeaderת��owner
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
	 * ����DRTRevision"���Ҫ��ģ��"�汾���󴴽�DesignReq�����Ҫ�󡱶���
	 */
	private void baseOnDRERevisionCreateDesignReq(TCSession tcSession,
			TCComponentItemRevision PKRRevision, TCComponent[] DRTRevisions,
			TCComponentUser SDT_user, TCComponentUser SMTE_Leader)
			throws TCException {

		System.out.println("-----����DRTRevision���Ҫ��ģ��汾���󴴽�DesignReq�����Ҫ�󡱶���");

		for (int i = 0; i < DRTRevisions.length; i++) {
			String DRTRevisionType = DRTRevisions[i].getType();
			if (DRTRevType.equals(DRTRevisionType)) {
				TCComponentItemRevision DRTRevision = (TCComponentItemRevision) DRTRevisions[i];

				// 5.3�޸�--�õ�DRTRevision��name
				String object_name = DRTRevision.getProperty("object_name");
				TCComponent DesignReq = null;

				try {
					DataManagementService dmService = DataManagementService
							.getService(tcSession);
					com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
					createIn[0] = new DataManagement.CreateIn();
					createIn[0].data.boName = DRType;

					// 5.4�޸�������revision������
					createIn[0].data.stringProps
							.put("object_name", object_name);
					createIn[0].data.stringProps.put("object_desc",
							"Design Requirement");
					CreateResponse responese = dmService
							.createObjects(createIn);
					DesignReq = responese.output[0].objects[0];

					// 5.27---PEFFunAreaRevision�޸�ΪPKRTRevision_target
					TCComponent relatedComponent = PKRRevision
							.getRelatedComponent("project_list");//project_list
					
					System.out
							.println("==========================5.27�޸�DesignRequire=================================================="
									+ relatedComponent);
					openByPass();
					relatedComponent.add("project_data", DesignReq);
					closeByPass();
					System.out
							.println("=========================5.27�޸�---��PEF�����������Ŀ��ָ�ɸ�DR!!!!=================");

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
													.getReferenceProperty("default_group"));// �޸ġ����Ҫ�󡱶����Owner
							closeByPass();
						}
						openByPass();
						grantPrivilege(tcSession, DesignReq, SDT_user,
								new String[] { "READ", "WRITE", "DELETE" });
						closeByPass();
					}
					
					
					openByPass();
					DesignReq.add(relationSMTET, DRTRevision);// �����Ҫ���롰���Ҫ��ģ�塱�汾֮��Ĺ�ϵSMTE_Template
					closeByPass();

					openByPass();
					PKRRevision.add(relationSMTE, DesignReq); // PKRRevision�롰���Ҫ��֮��Ĺ�ϵSMTE��
					closeByPass();

					// 5.8�޸�---Ҫ����һ��
					TCComponent[] tcComponents = DRTRevision
							.getRelatedComponents("IMAN_manifestation");
					for (TCComponent component : tcComponents) {
						// saveas��DR����
						System.out.println("IMAN_manifestation�ļ�����������========="
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
						System.out.println("IMAN_specification�ļ�����������========="
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

	// ����Ȩ��
	public void grantPrivilege(TCSession tcSession, TCComponent tccomponent,
			TCComponentUser user, String[] as2) throws TCException {
		TCAccessControlService accessControlService = tcSession
				.getTCAccessControlService();
		// 5.28�޸�Ȩ������д��ɾ����Ȩ��
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

	// ����·
	private void openByPass() {
		try {
			tcSession.getUserService().call("open_or_close_pass",
					new Object[] { 1 });

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �ر���·
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
