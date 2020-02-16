package com.yfjcebp.extsupport;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;
import java.util.Vector;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCSession;

public class SectionManagerDialog extends AbstractAIFDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TCComponentTaskTemplate taskTemplate;
	private TCSession session;
	private Vector<String> extSupportVec;
	private Map<String, TCComponentUser> ext_user_map;
	private String yearMonth;
	private Map<String,String> ext_division;
	private String ext_daibiao;
		
	public SectionManagerDialog(TCSession session,TCComponentTaskTemplate taskTemplate,Vector<String> extSupportVec,Map<String, TCComponentUser> ext_user_map,Map<String,String> ext_division,String yearMonth,String ext_daibiao){
		super(true);
		this.taskTemplate = taskTemplate;
		this.session =session;
		this.extSupportVec = extSupportVec;
		this.ext_user_map = ext_user_map;
		this.yearMonth = yearMonth;
		this.ext_division = ext_division;
		this.ext_daibiao =ext_daibiao;
	} 
	
	public void initDialog() throws Exception{
		setLayout(new BorderLayout());
		setTitle("Select ExtSupport");
		SectionManagerPanel pane = new SectionManagerPanel(this,extSupportVec,ext_user_map,ext_division,yearMonth,ext_daibiao,session);
		pane.setNew(true);
		pane.setTaskTemplate(taskTemplate);
		pane.initDialog();
		add(pane,BorderLayout.CENTER);
		setPreferredSize(new Dimension(540,600));
		centerToScreen();
		setVisible(true);
	} 
	
	

}
