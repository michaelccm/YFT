package com.yfjc.workdayhourform;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTable;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.extsupport.JCI6YFJCUtil;

public class ExtHourArrayBean_lastcommit extends JPanel implements
		InterfacePropertyComponent, InterfaceBufferedPropertyComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TCSession session;
	private TCComponentForm form;
	private String property;
	private TCProperty tcProperty;
	private WorkdayHourPanel myPanel;
	private Registry reg = Registry.getRegistry(this);
	private String[] wdhProNames = new String[] { "jci6_UserName",
			"jci6_Project", "jci6_Task", "jci6_Division", "jci6_Hour",
			"jci6_ExtraHour", "jci6_Year", "jci6_Month" };

	public ExtHourArrayBean_lastcommit() {
		session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		property = "";
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public String getProperty() {
		return property;
	}

	@Override
	public boolean isMandatory() {
		return false;
	}

	@Override
	public boolean isPropertyModified(TCComponent arg0) throws Exception {
		return false;
	}

	@Override
	public boolean isPropertyModified(TCProperty arg0) throws Exception {

		// 判断是table是否修改了
		JTable table = myPanel.getTable();
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
		Vector<TCComponent> oldCompVec = myPanel.getOldCompVec();
		int row_cnt = table.getRowCount();
		int old_size = oldCompVec.size();
		if (row_cnt != old_size) {
			return true;
		} else {
			for (int i = 0; i < row_cnt; i++) {
				TCComponent comp = oldCompVec.get(i);
				TCProperty[] propertys = comp.getTCProperties(wdhProNames);
				if (!propertys[0].getStringValue().equals(
						table.getValueAt(i, 0))) {
					return true;
				}
				TCComponentProject prj = (TCComponentProject) propertys[1]
						.getReferenceValue();
				String project_string = prj.getProperty("object_string");
				if (!project_string.equals(table.getValueAt(i, 1))) {
					return true;
				}
				String name = "";
				TCComponentScheduleTask task = (TCComponentScheduleTask) propertys[2]
						.getReferenceValue();
				name = task.getProperty("object_name");
				if (!name.equals(table.getValueAt(i, 2))) {
					return true;
				}

				double normalHour = propertys[4].getDoubleValue();
				if (normalHour != Double.valueOf((String) table
						.getValueAt(i, 3))) {
					return true;
				}

				double extractHour = propertys[5].getDoubleValue();
				if (extractHour != Double.valueOf((String) table.getValueAt(i,
						4))) {
					return true;
				}
				TCComponentGroup group = (TCComponentGroup) propertys[3]
						.getReferenceValue();
				if (!group.getFullName().split("\\.")[0].equals(table.getValueAt(i, 5))) {
					return true;
				}

			}
		}

		return false;
	}

	@Override
	public void load(TCComponent arg0) throws Exception {
	}

	@Override
	public void load(TCProperty arg0) throws Exception {
		tcProperty = arg0;
		//System.out.println(" tc property name " + tcProperty.getName());
		form = (TCComponentForm) tcProperty.getTCComponent();
		//System.out.println(" tc form " + form.getType());
		TCComponentUser user = session.getUser();
		TCComponentUser owner = null;
		TCComponentTaskTemplate template = null;
		try {
			owner = (TCComponentUser) form.getTCProperty("owning_user")
					.getReferenceValue();
			boolean isEnable = true;
			if (user != owner) {
				isEnable = false;
			}
			boolean flag = true; // 确认并发送流程按钮状态
			String templateName = reg.getString("ExtHourReviewProcess");
			//System.out.println("  templateName " + templateName);
			if (templateName == null || templateName.trim().length() == 0) {
				MessageBox.post("未找到流程模板", "WARNING", MessageBox.WARNING);
			}
			//System.out.println("findTaskTemplate start");
			template = JCI6YFJCUtil.findTaskTemplate(session, templateName);
			//System.out.println("findTaskTemplate end");
			if (template == null) {
				MessageBox.post("未找到流程模板" + templateName, "WARNING",
						MessageBox.WARNING);
			} else {
				TCComponent[] comps = form.getTCProperty("process_stage_list")
						.getReferenceValueArray();
				if (comps != null && comps.length > 0) {
					for (int i = 0; i < comps.length; i++) {
						//System.out.println(" the reference object type======"+ comps[i].getType());
								
						if (comps[i].getType().equals("EPMTask")) {
							TCComponentTaskTemplate template1 = (TCComponentTaskTemplate) comps[i]
									.getTCProperty("task_template")
									.getReferenceValue();
							if (template1 == template) {
								flag = false;
								break;
							}
						}
					}
				}
				//System.out.println("WorkdayHourPanel start");
				myPanel = new WorkdayHourPanel(flag, isEnable,
						form, tcProperty, template);
				this.setLayout(new BorderLayout());
				this.add(myPanel, BorderLayout.CENTER);
			}

		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load(TCComponentType tccomponenttype) throws Exception {
		if (tccomponenttype != null) {
			TCPropertyDescriptor tcpropertydescriptor = tccomponenttype
					.getPropertyDescriptor(property);
			load(tcpropertydescriptor);
		}
	}

	@Override
	public void load(TCPropertyDescriptor tcpropertydescriptor)
			throws Exception {
	}

	@Override
	public void save(TCComponent arg0) throws Exception {
		//System.out.println("i  am  here D");
	}

	@Override
	public void save(TCProperty arg0) throws Exception {
		//System.out.println("i  am  here C");

	}

	@Override
	public void setMandatory(boolean arg0) {
	}

	@Override
	public void setModifiable(boolean arg0) {
	}

	@Override
	public void setProperty(String s) {
		property = s;
	}

	@Override
	public void setUIFValue(Object arg0) {
	}

	@Override
	public TCProperty saveProperty(TCComponent arg0) throws Exception {
		//System.out.println("i  am  here B");
		return null;
	}

	@Override
	public TCProperty saveProperty(TCProperty arg0) throws Exception {
		//System.out.println("i  am  here A");
		// 在这里写保存操作
		if (myPanel.checkTable() == false) {
			MessageBox.post("请检查是否有单元格为空！", "WARNING", MessageBox.WARNING);
		} else {
			String s = myPanel.checkOtherForm();
			if(s.equals("")){
				myPanel.saveData();
				form.setTCProperty(arg0);
				form.save();
			}else{
				MessageBox.post(s, "INFORMATION", MessageBox.INFORMATION);
			}
			
		}
		//System.out.println(" i am  end save ");
		return null;
	}
	
	

}
