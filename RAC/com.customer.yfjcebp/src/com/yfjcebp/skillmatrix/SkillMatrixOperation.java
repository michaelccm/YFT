/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: SkillMatrixOperation.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-12	wuh  		Ini		��ʼ�� 								   
 #=============================================================================
 */
package com.yfjcebp.skillmatrix;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.eclipse.swt.widgets.Shell;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class SkillMatrixOperation extends AbstractAIFOperation {

	private String relation = "IMAN_external_object_link";
	private  Registry reg = Registry.getRegistry(this);
	private Double d;
	private TCSession session;
	private TCComponentUser user;
	private POIReadExcel p;
	private ArrayList<String[]> list = new ArrayList<String[]>();
	private String[] s;
	private TCComponentItem item;
	private TCComponentItemRevision itemrevision;
	private String str;
	private HashMap<String, String> map = new HashMap<String, String>();
	private Boolean bool = false;
	private Shell shell;
	private LongRunningOperation lro;
    private String[] propertynames;
    private HashMap<Integer, String> propertymap = new HashMap<Integer, String>();
    private HashMap<Integer, TCComponentUser> user_map = new HashMap<Integer, TCComponentUser>();
    private TCComponentFolder folder = null;
	public SkillMatrixOperation(LongRunningOperation lro, TCSession session,
			HashMap<Integer, TCComponentUser> user_map, Shell shell,HashMap<String, String> map,HashMap<Integer, String> propertymap ) {
		System.out.println("SkillMatrixOperation");
		this.lro = lro;
		this.session = session;
        this.user_map = user_map;
		this.shell = shell;
                this.map = map;
                this.propertymap = propertymap;
 		try {
			executeOperation();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public void executeOperation() throws Exception {
		p = lro.getP();
		list = p.getList();
        //ȡ��һ��
		propertynames = list.get(0);
		TCComponentDataset dataset = getDataset();
		AIFComponentContext[] context = dataset.whereReferenced();
		for (int i = 0; i < context.length; i++) {
			// ���ڣ�����
			if (context[i].getComponent() instanceof TCComponentFolder) {
				folder = (TCComponentFolder) context[i].getComponent();
				break;
			}
		}
		// �ӵڶ��п�ʼ��
		for (int i = 1; i < list.size(); i++) {
			s = list.get(i);
			// ȡÿ�еĵ�һ������
			user = user_map.get(i);
			item = setSkillInfo(user);
			itemrevision = item.getLatestItemRevision();
			for (int j = 0; j < s.length; j++) {
				str = s[j];
				String st = p.excelRange(j);
				setValue(itemrevision, st, str,propertymap.get(j));
			}
		}
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				shell.dispose();
			}
		});
		if(bool == false){
	    	MessageBox.post(reg.getString("completeMessage"), reg.getString("infomessage"), MessageBox.INFORMATION);
	    }
	}

	/**
	 * ͨ����ϵ������user�Ƿ���JCI6_SkillInfo�����£� ���������棬���� ���½�
	 * @param user
	 * @return
	 */

	private TCComponentItem setSkillInfo(TCComponentUser user) {
		TCComponentItem item = null;
		Boolean b = false;
		
		
		try {
			
			AIFComponentContext[] context = user.whereReferencedByTypeRelation(
					new String[] { "JCI6_SkillInfo" },
					new String[] { relation });
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			for (int i = 0; i < context.length; i++) {
				// ���ڣ�����
				if (context[i].getComponent() instanceof TCComponentItem) {
					b = true;
					item = (TCComponentItem) context[i].getComponent();
					// ����
					item.revise(null,"SA_"+formatter.format(new Date()) , null);
					break;
				}
			}
			if (b == false) {
				// �����ڣ��½�
				// �����µļ�����Ϣ����
				TCComponentItemType itemtype = (TCComponentItemType) session
						.getTypeComponent("JCI6_SkillInfo");
				item = itemtype.create(user.getUserId(), "A", "JCI6_SkillInfo",
						"SA_"+formatter.format(new Date()), null, null, null, null);
				item.add(relation, user);
				folder.add("contents", item);
			}
		} catch (TCException e) {
			bool = true;
			e.printStackTrace();
		}
		return item;
	}
	/**
	 * ��ĳ�汾���Ը�ֵ
	 * 
	 * @param itemrevision
	 * @param i
	 * @param value
	 */

	private void setValue(TCComponentItemRevision itemrevision, String st,
			String value,String propertyname) {	
		if(map.get(propertyname)!=null && map.get(propertyname).equals(st) && !propertyname.equals("user_id")&&!propertyname.equals("user_name")){
		if (propertyname != null && !"".equals(propertyname)) {
			if (value.equals("N/A")) {
				// ����������ֵ
			} else {
				try {
					d = Double.valueOf(value);
					itemrevision.lock();
					itemrevision.setDoubleProperty(propertyname, d);
					itemrevision.save();
					itemrevision.unlock();
				} catch (TCException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {

				}
			}
		}
		}
	}
	
	/**
	 * �ж��û�ѡȡ���Ƿ�Ϊ���ݼ�
	 */
	private TCComponentDataset getDataset() {
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		TCComponentDataset dataset = null;
		// �õ��û�ѡȡ�Ķ���
		InterfaceAIFComponent targets[] = app.getTargetComponents();
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] instanceof TCComponentDataset) {
				dataset = (TCComponentDataset) targets[i];
			}
		}

		return dataset;
	}
}
