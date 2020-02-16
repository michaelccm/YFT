package com.yfjcebp.extsupport;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTable;

import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;

public class ExtSupportArrayRendering extends JPanel implements
		InterfacePropertyComponent, InterfaceBufferedPropertyComponent,InterfaceAIFComponentEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TCComponentForm form;
	private JTable table;
	private Vector<ExtSupportUtil> initExtSupportUtilVec;
	private StringBuffer sb = new StringBuffer();
	private String property;
	private TCProperty tcproperty;
	private Vector<ExtSupportUtil> vec = new Vector<ExtSupportUtil>();
	private SectionManagerPanel sectionManager;

	public ExtSupportArrayRendering() {
		//System.out.println("ExtSupportArrayRendering");
		property = "";
	}

	@Override
	public TCProperty saveProperty(TCComponent tccomponent) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("saveProperty(TCComponent)");
		
		return null;
	}

	@Override
	public TCProperty saveProperty(TCProperty tcproperty) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("saveProperty(TCProperty)");
		sectionManager.save();
		return null;
	}

	@Override
	public Object getEditableValue() {//saveProperty(TCProperty tcproperty)有返回值时才会调用此方法
		// TODO Auto-generated method stub
		//System.out.println("getEditableValue");
		return null;
	}
	
	

	@Override
	public String getProperty() {
		// TODO Auto-generated method stub
		//System.out.println("getProperty");
		return property;
	}

	@Override
	public boolean isMandatory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPropertyModified(TCComponent tccomponent) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("===============isPropertyModified(TCComponent)==============");
				
		return isModified();
	}

	@Override
	public boolean isPropertyModified(TCProperty tcproperty) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("===============isPropertyModified(TCProperty)==============");
				
		return isModified();
	}

	@Override
	public void load(TCComponent tccomponent) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("load   TCComponent");
		tcproperty = tccomponent.getTCProperty(property);
		if (tccomponent instanceof TCComponentForm) {
			form = (TCComponentForm) tccomponent;
			// 得到当前对象所有者
			TCComponentUser own_user = (TCComponentUser) tccomponent
					.getTCProperty("owning_user").getReferenceValue();
			// 得到当前用户
			TCSession session = tccomponent.getSession();
			sectionManager = new SectionManagerPanel(form, session,own_user);
			this.setLayout(new BorderLayout());
			this.add(sectionManager, BorderLayout.CENTER);
			sectionManager.setTcproperty(tcproperty);
			sectionManager.initDialog();
		}
	}

	@Override
	public void load(TCProperty tcproperty) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("load   TCProperty");
		TCComponent tccomponent = tcproperty.getTCComponent();
		if (tccomponent instanceof TCComponentForm) {
			form = (TCComponentForm) tccomponent;
			// 得到当前对象所有者
			TCComponentUser own_user = (TCComponentUser) tccomponent
					.getTCProperty("owning_user").getReferenceValue();
			// 得到当前用户
			TCSession session = tccomponent.getSession();
			sectionManager = new SectionManagerPanel(form, session,own_user);
			this.setLayout(new BorderLayout());
			this.add(sectionManager, BorderLayout.CENTER);
			sectionManager.setTcproperty(tcproperty);
			sectionManager.initDialog();
			
		}
	}

	@Override
	public void load(TCComponentType tccomponenttype) throws Exception {
		// TODO Auto-generated method stub
		if (tccomponenttype != null) {
			TCPropertyDescriptor tcpropertydescriptor = tccomponenttype
					.getPropertyDescriptor(property);
			load(tcpropertydescriptor);
		}
	}

	@Override
	public void load(TCPropertyDescriptor tcpropertydescriptor)
			throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("load   TCPropertyDescriptor");
	}

	@Override
	public void save(TCComponent tccomponent) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("save TCComponent");
	}

	@Override
	public void save(TCProperty tcproperty) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("save TCProperty");
	}

	@Override
	public void setMandatory(boolean flag) {
		// TODO Auto-generated method stub
		//System.out.println("setMandatory");
	}

	@Override
	public void setModifiable(boolean flag) {
		// TODO Auto-generated method stub
		//System.out.println("setModifiable");
	}

	@Override
	public void setProperty(String s) {
		// TODO Auto-generated method stub
		//System.out.println("setProperty");
		property = s;
	}

	@Override
	public void setUIFValue(Object obj) {
		// TODO Auto-generated method stub
		//System.out.println("setUIFValue");
	}

	private boolean isModified() {
		//System.out.println("isModified");
		table = sectionManager.getTable();
		initExtSupportUtilVec = sectionManager.getInitExtSupportUtilVec();
		int initExtSize = initExtSupportUtilVec.size();
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
		Vector<TCComponent> needRemoveExt = sectionManager.getNeedRemoveExtVec();
		if(needRemoveExt.size() > 0)
			return true;
		int colcnt = table.getColumnCount();
		int rowcnt = table.getRowCount();
		//System.out.println("rowcnt--->" + rowcnt + "======>colcnt===>" + colcnt);
				
		if (initExtSize != 0 && (rowcnt == 0 || colcnt == 1)) {
			//System.out.println("initExtSize != 0 && (rowcnt == 0 || colcnt == 1)");
					
			return true;
		}
		String[] yearColNames = new String[colcnt - 1];
		for (int j = 2; j < colcnt; j++) {
			//System.out.println("j====>" + j);
			yearColNames[j - 1] = table.getColumnName(j);
			//System.out.println("-----------------------");
			//System.out.println("yearColNames[j]--------------------"+ yearColNames[j-1]);
					
		}

		for (int i = 0; i < rowcnt; i++) {
			//System.out.println("for (int i = 0; i < rowcnt; i++)");
			String userName = (String) table.getValueAt(i, 1);
			//System.out.println("userName--->"+userName);
			getExtIndex(userName, initExtSize);
			int use_extcnt = vec.size();
			if (use_extcnt == 0) {
				//System.out.println("ExtSupportUtil not exist");
				return true;
			} else {
				for (int j = 2; j < colcnt; j++) {
					//System.out.println("yearColNames[j-1]====>"+yearColNames[j-1]);
					ExtSupportUtil ext = getColExt(use_extcnt,yearColNames[j-1]);
					if (ext != null) {
						if (!(ext.getUsagePercent() + "%").equals(table
								.getValueAt(i, j))) {
							//System.out.println("!ext.getUsagePercent().equals(table.getValueAt(i, j))");
									
							return true;
						} else {
							//System.out.println("dddd");
						}
					} else {
						//System.out.println("!yearColNames[j-1].equals(sb.toString())");
								
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private ExtSupportUtil getColExt(int use_extcnt,String yearColName){
		for(int n=0;n<use_extcnt;n++){
			ExtSupportUtil ext = vec.get(n);
			sb.setLength(0);
			sb.append(sectionManager.getMonth_pro()[Integer.valueOf(ext.getMonth())-1]).append("-").append(ext.getYear());
			if(sb.toString().equals(yearColName)){
				return ext;
			}
		}
		return null;
	} 

	/**
	 * 判断username在initExtSupportUtilVec中是否存在
	 * @param userName
	 * @return
	 */
	private void getExtIndex(String userName, int size) {
		vec.clear();
		for (int i = 0; i < size; i++) {
			ExtSupportUtil ext = initExtSupportUtilVec.get(i);
			if (ext.getUserName().equals(userName)) {
				vec.add(ext);
			}
		}
	}

	@Override
	public void processComponentEvents(AIFComponentEvent[] aaifcomponentevent) {
		// TODO Auto-generated method stub
		//System.out.println("processComponentEvents");
	}

	

	

}
