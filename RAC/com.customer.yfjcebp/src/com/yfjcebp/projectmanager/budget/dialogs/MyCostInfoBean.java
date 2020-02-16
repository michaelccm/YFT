package com.yfjcebp.projectmanager.budget.dialogs;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class MyCostInfoBean {
	public String object_name="";
	public int type=0;
	public String GroupName;
	public String SelectionName;
	public int year;
	public String costType;
	public TCComponentItemRevision revision;
	public MyCostInfoBean(TCComponentItemRevision revision,String object_name, int type, String groupName,
			String selectionName, int year, String costType) {
		this.object_name = object_name;
		this.type = type;
		GroupName = groupName;
		SelectionName = selectionName;
		this.year = year;
		this.costType = costType;
		this.revision = revision;
	}
	
	
	public String jci6_Jan="";
	public String jci6_Feb="";
	public String jci6_Mar="";
	public String jci6_Apr="";
	public String jci6_May="";
	public String jci6_Jun="";
	public String jci6_Jul="";
	public String jci6_Aug="";
	public String jci6_Sep="";
	public String jci6_Oct="";
	public String jci6_Nov="";
	public String jci6_Dec="";
	
	
	
	// 创建 ---人工费用信息和非人工费用信息
//		private TCComponent createCostInfo(TCSession session,
//				TCComponentItemRevision revision, String object_name, int type,
//				String GroupName, String SelectionName, int year, String costType)
//				throws TCException, ServiceException {
//			
//			// 创建费用信息
//			DataManagementService dmService = DataManagementService
//					.getService(session);
//
//			// modify by wuwei
//			com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
//
//			createIn[0] = new DataManagement.CreateIn();
//
//			createIn[0].data.boName = "JCI6_CostInfo";
//			String ProjectID = revision.getProperty("item_id");
//			// 得到时间戳
//			String timestamp = getSystemTime();
//			String name = "";
//
//			String name_Unit = "";
//			if (object_name.equals("人月")) {
//				name_Unit = "ManMonth";
//			} else if (object_name.equals("小时")) {
//				name_Unit = "Hour";
//			} else if (object_name.equals("元")) {
//				name_Unit = "Yuan";
//			}
//
//			// 先得到group的对象,根据对象得到Group的短写名字
//			TCComponentGroup componentGroup = null;
//			if (type != 1) {
//				TCComponentGroupType groupType = (TCComponentGroupType) session
//						.getTypeComponent("Group");
//				componentGroup = groupType.find(GroupName);
//			}
//
//			if (type == 1) {
//				name = ProjectID + "_Budget_" + costType + "_" + year + "_"
//						+ timestamp;
//				createIn[0].data.stringProps.put("object_name", name);
//			} else {
//				name = ProjectID + "_Budget_" + year + "_"
//						+ componentGroup.getProperty("name") + "_" + SelectionName
//						+ "_" + name_Unit + "_" + timestamp;
//				createIn[0].data.stringProps.put("object_name", name);
//			}
//
//			// modify by wuwei
//			if (type == 1) {
//				createIn[0].data.stringProps.put("jci6_CPT", "Budget");
//				createIn[0].data.stringProps.put("jci6_CostType", costType);
//				createIn[0].data.stringProps.put("jci6_Unit", name_Unit);
//				createIn[0].data.intProps.put("jci6_Year", new BigInteger(year+""));
//			} else {
//				createIn[0].data.stringProps.put("jci6_CPT", "Budget");
//				createIn[0].data.stringProps.put("jci6_CostType", "Normal Hours");
//				createIn[0].data.stringProps.put("jci6_Unit", name_Unit);
//				createIn[0].data.intProps.put("jci6_Year", new BigInteger(year+""));
//				//createIn[0].data.stringProps.put("jci6_Division", componentGroup);
//			}
//
//			TCComponent component = null;
//			CreateResponse responese = dmService.createObjects(createIn);
//			int create_count = responese.serviceData.sizeOfCreatedObjects();
//			int create_error_count = responese.serviceData.sizeOfPartialErrors();
//			if(create_error_count==0){
//				 component = responese.output[0].objects[0];
//			}
//			else{
//				String message = responese.serviceData.getPartialError(0).getMessages()[0];
//				//System.out.println("error message is "+message);
//			}
//				
//
//			TCProperty[] property = null;
//			if (type == 1) {
//				// 非人工费用信息
//				// 用TCProperty[]来存储一系列要修改的TC中的属性名称
//				/*
//				 * //System.out.println("---------创建非人工信息---------");
//				*/
//
//			} else {
//				// 人工费用信息
//				//System.out.println("---------创建人工信息---------");
//
//				
//				property = component.getTCProperties(new String[] { "jci6_Division",
//						"jci6_RateLevel" });
//
//
//				// 查询指定的组
//				property[0].setReferenceValueData(componentGroup);
//
//				// //System.out.println("---学科名称-----" + SelectioName);
//
//				// 三期ebp修改---by wuwei
//				boolean flag_new = false;
//				if (SelectionName.equals("Resident Engineer")) {
//					//System.out.println("---2014/6/11  Set RateLevel NULL---"+ SelectionName);
//							
//					flag_new = true;
//					property[1].setReferenceValueData((TCComponent) null);
//				} else {
//					// 查询学科
//					TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
//							new String[] { "discipline_name" },
//							new String[] { SelectionName });
//					if (tcp != null)
//						property[1].setReferenceValueData(tcp[0]);
//					else{
//						//System.out.println("-----查询学科名称没找到！！！-----");
//					}
//				}
//
//				// 5.4修改----人工费用
//				component.setTCProperties(property);
//				if (flag_new) {
//
//					//System.out.println("---2014/6/11  SelectioName---"+ SelectionName);
//							
//					component.setProperty("jci6_TaskType", "tasktype26");
//				}
//
//			}
//
//			// 下挂到指定的版本下
//			// openByPass();
//			revision.add("IMAN_external_object_link", component);
//			// closeByPass();
//
//			// 7.2修改---把数据集的项目，指派给新创建的费用信息
//			// openByPass();
//			// projectComponent.add("project_data", component);
//			// closeByPass();
//
//			// component.save();
//			component.refresh();
//
//			return component;
//		}
}
